package sukriti.ngo.mis.ui.tickets.fragments.detail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import sukriti.ngo.mis.AWSConfig
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.databinding.TicketDetailBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.ui.super_admin.fragments.AssignTicketDialog
import sukriti.ngo.mis.ui.super_admin.fragments.TicketActionDialog
import sukriti.ngo.mis.ui.tickets.TicketDetailViewModel
import sukriti.ngo.mis.ui.tickets.adapters.TicketFilesAdapter
import sukriti.ngo.mis.ui.tickets.data.TicketAction
import sukriti.ngo.mis.ui.tickets.data.TicketFileItem
import sukriti.ngo.mis.ui.tickets.data.TicketTeamData
import sukriti.ngo.mis.ui.tickets.interfaces.AssignTicketDialogHandler
import sukriti.ngo.mis.ui.tickets.interfaces.SubmitTicketHandler
import sukriti.ngo.mis.ui.tickets.interfaces.TicketActionDialogHandler
import sukriti.ngo.mis.ui.tickets.lambda.ListTeam.ListTicketTeamLambdaRequest
import sukriti.ngo.mis.ui.tickets.lambda.TicketActions.TicketActionsLambdaRequest
import sukriti.ngo.mis.utils.*
import sukriti.ngo.mis.utils.Nomenclature.TICKET_ACTION_ASSIGN_TO
import sukriti.ngo.mis.utils.Nomenclature.TICKET_FOLDER_RAISE


class TicketDetail : Fragment() {
    private lateinit var viewModel: TicketDetailViewModel
    private lateinit var binding: TicketDetailBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var navigationClient: NavigationClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var submitTicketHandler: SubmitTicketHandler
    private lateinit var mNavigationHandler: NavigationHandler
    private var userTouchFlagStatusSelection = false

    companion object {
        private var INSTANCE: TicketDetail? = null

        fun getInstance(): TicketDetail {
            if (INSTANCE == null) {
                Log.i("__loadData", "usageReport:GraphicalReport()")
                INSTANCE = TicketDetail()
            }
            return INSTANCE as TicketDetail
        }
    }

    fun setNavigationHandler(handler: NavigationHandler) {
        mNavigationHandler = handler
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationHandler) {
            navigationHandler = context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement NavigationHandler"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("__profile", "profile");
        binding = TicketDetailBinding.inflate(layoutInflater)
        init()
        return binding.root
    }


    private fun init() {
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(TicketDetailViewModel::class.java)
        navigationClient = NavigationClient(childFragmentManager)

        if (viewModel.selectedTicketData.ticket_status != Nomenclature.TICKET_STATUS_CLOSED) {
            binding.statusSelectionContainer.visibility = View.VISIBLE
            val user = sharedPrefsClient.getUserDetails()
            val items = Nomenclature.getTicketActionsNames(viewModel.selectedTicketData, user)
            val adapter = ArrayAdapter(requireContext(), R.layout.item_ticket_actions, items)
            binding.statusSelection.setOnTouchListener(actionSelectionTouchListener)
            binding.statusSelection.adapter = adapter
            binding.statusSelection.onItemSelectedListener = actionSelectionListener
            binding.statusSelection.setSelection(0)
            binding.statusSelectionContainer.setOnClickListener(View.OnClickListener {
                binding.statusSelection.performClick()
            })
        } else {
            binding.statusSelectionContainer.visibility = View.INVISIBLE
        }

        loadData()
    }

    fun loadData() {

        val credentialsProvider = CognitoCachingCredentialsProvider(
            context,
            AWSConfig.identityPoolID,
            AWSConfig.cognitoRegion
        )
        userAlertClient.showWaitDialog("Loading ticket files...")
        viewModel.getTicketFiles(
            credentialsProvider,
            getTicketFilesRequestHandler,
            viewModel.selectedTicketData.ticket_id,
            TICKET_FOLDER_RAISE
        )

        binding.ticketRow.status.text = viewModel.selectedTicketData.ticket_status
        binding.ticketRow.ticketId.text = viewModel.selectedTicketData.ticket_id
        binding.ticketRow.complex.text = viewModel.selectedTicketData.complex_name
        binding.ticketRow.state.text =
            viewModel.selectedTicketData.state_code + ":" + viewModel.selectedTicketData.district_name
        binding.ticketRow.city.text = viewModel.selectedTicketData.city_name
        binding.ticketRow.userRole.text = viewModel.selectedTicketData.creator_role
        binding.ticketRow.userName.text = viewModel.selectedTicketData.creator_id
        binding.ticketRow.date.text =
            Utilities.getTimeDifference(viewModel.selectedTicketData.timestamp)
        binding.ticketRow.critical.text = viewModel.selectedTicketData.criticality
        binding.ticketRow.criticalIv.setBackgroundResource(R.drawable.surface_circle_red)
        binding.ticketRow.title.text = viewModel.selectedTicketData.title

        binding.ticketTitle.editText?.setText(viewModel.selectedTicketData.title)
        binding.description.editText?.setText(viewModel.selectedTicketData.description)

        Log.i("resetBulkQueuedStatus", "isQueuedForUser? "+viewModel.selectedTicketData.isQueuedForUser)
    }


    private var getTicketFilesRequestHandler: RepositoryCallback<List<TicketFileItem>> =
        object : RepositoryCallback<List<TicketFileItem>> {
            override fun onComplete(result: _Result<List<TicketFileItem>>?) {
                userAlertClient.closeWaitDialog()

                if (result is _Result.Success<*>) {
                    val mTicketFileItem = result as _Result.Success<List<TicketFileItem>>


                    val accessTreeAdapter =
                        TicketFilesAdapter(
                            ticketFileSelectionHandler,
                            mTicketFileItem.data
                        )
                    binding.ticketFiles.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.ticketFiles.adapter = accessTreeAdapter
                } else {
                    val err = result as _Result.Error<List<TicketFileItem>>
                    if (err.errorCode == -100) {
                        //No File attached
                        binding.photosContainer.visibility = View.GONE
                    } else {
                        userAlertClient.showDialogMessage("Error Alert", err.message, false)
                    }
                }

            }
        }

    private var ticketTeamRequestHandler: RepositoryCallback<List<TicketTeamData>> =
        object : RepositoryCallback<List<TicketTeamData>> {
            override fun onComplete(result: _Result<List<TicketTeamData>>?) {
                userAlertClient.closeWaitDialog()

                if (result is _Result.Success<*>) {
                    var mTicketTeamData = result.data as List<TicketTeamData>
                    if (mTicketTeamData.size == 0) {
                        userAlertClient.showDialogMessage(
                            "Error Alert",
                            "No user exists with required permissions to handle this ticket.",
                            false
                        )
                    } else {
                        var selectedAction =
                            TicketAction(TICKET_ACTION_ASSIGN_TO, "Assign Ticket", "")
                        var assignTicketDialog = AssignTicketDialog.getInstance()
                        assignTicketDialog.setData(
                            selectedAction,
                            mTicketTeamData,
                            mAssignTicketDialogHandler
                        )
                        assignTicketDialog.show(
                            childFragmentManager.beginTransaction(),
                            "ticketActionDialog"
                        )
                    }


                } else {
                    var err = result as _Result.Error<List<TicketFileItem>>
                    userAlertClient.showDialogMessage("Error Alert", err.message, false)
                }
            }
        }

    private var ticketFileSelectionHandler: TicketFilesAdapter.RemoveHandler =
        object : TicketFilesAdapter.RemoveHandler {
            override fun onRemove(index: Int) {

            }
        }

    private var actionSelectionTouchListener: View.OnTouchListener = object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            userTouchFlagStatusSelection = true
            return false
        }
    }

    private var actionSelectionListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i("_durationSelection", " - ")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                if (userTouchFlagStatusSelection) {
                    var user = sharedPrefsClient.getUserDetails()
                    val items = Nomenclature.getTicketActions(viewModel.selectedTicketData, user)
                    var selectedAction = items[index] as TicketAction
                    Log.i("_selection", "selection: ${selectedAction.actionName}")

                    if (selectedAction.actionId == Nomenclature.TICKET_ACTION_ASSIGN_TO) {
                        //TICKET_ACTION_ASSIGN_TO
                        userAlertClient.showWaitDialog("Fetching potential assignees  for this ticket...")
                        var request = ListTicketTeamLambdaRequest(viewModel.selectedTicketData)
                        viewModel.listTicketTeam(request, ticketTeamRequestHandler)
                    } else {
                        var ticketActionDialog = TicketActionDialog.getInstance()
                        ticketActionDialog.setData(selectedAction, mTicketActionDialogHandler)
                        ticketActionDialog.show(
                            childFragmentManager.beginTransaction(),
                            "ticketActionDialog"
                        )
                    }

                    binding.statusSelection.setSelection(0)
                    userTouchFlagStatusSelection = false
                }
            }
        }


    private var mTicketActionDialogHandler: TicketActionDialogHandler =
        TicketActionDialogHandler { action, comment ->
            var user = sharedPrefsClient.getUserDetails()
            var action = action as TicketAction
            when (action.actionId) {
                Nomenclature.TICKET_ACTION_ACCEPT_ASSIGNMENT -> {
                    userAlertClient.showWaitDialog("Accepting ticket...")
                    var user = sharedPrefsClient.getUserDetails()
                    var request = TicketActionsLambdaRequest(
                        user,
                        viewModel.selectedTicketData,
                        action,
                        comment
                    )
                    viewModel.performTicketAction(request, ticketActionLambdaResponseHandler)
                }

                Nomenclature.TICKET_ACTION_TICKET_PROGRESS -> {
                    userAlertClient.showWaitDialog("Updating ticket progress...")
                    var user = sharedPrefsClient.getUserDetails()
                    var request = TicketActionsLambdaRequest(
                        user,
                        viewModel.selectedTicketData,
                        action,
                        comment
                    )
                    viewModel.performTicketAction(request, ticketActionLambdaResponseHandler)
                }

                Nomenclature.TICKET_ACTION_MARK_RESOLVED -> {
                    userAlertClient.showWaitDialog("Updating ticket status to Resolved...")
                    var user = sharedPrefsClient.getUserDetails()
                    var request = TicketActionsLambdaRequest(
                        user,
                        viewModel.selectedTicketData,
                        action,
                        comment
                    )
                    viewModel.performTicketAction(request, ticketActionLambdaResponseHandler)
                }

                Nomenclature.TICKET_ACTION_MARK_CLOSED -> {
                    userAlertClient.showWaitDialog("Closing ticket...")
                    var user = sharedPrefsClient.getUserDetails()
                    var request = TicketActionsLambdaRequest(
                        user,
                        viewModel.selectedTicketData,
                        action,
                        comment
                    )
                    viewModel.performTicketAction(request, ticketActionLambdaResponseHandler)
                }

                Nomenclature.TICKET_ACTION_RE_OPEN -> {
                    userAlertClient.showWaitDialog("Marking ticket reopened...")
                    var user = sharedPrefsClient.getUserDetails()
                    var request = TicketActionsLambdaRequest(
                        user,
                        viewModel.selectedTicketData,
                        action,
                        comment
                    )
                    viewModel.performTicketAction(request, ticketActionLambdaResponseHandler)
                }
            }
        }

    private var mAssignTicketDialogHandler: AssignTicketDialogHandler = object :
        AssignTicketDialogHandler {
        override fun onSubmit(action: TicketAction?, comment: String?, userId: String?, userRole: String) {
            var user = sharedPrefsClient.getUserDetails()
            var action = action as TicketAction
            userAlertClient.showWaitDialog("Assigning ticket...")
            var request =
                TicketActionsLambdaRequest(user, viewModel.selectedTicketData, action, comment, userId, userRole)
            viewModel.performTicketAction(request, ticketActionLambdaResponseHandler)
        }

    }

    private var ticketActionLambdaResponseHandler: RepositoryCallback<TicketActionsLambdaRequest> =
        object : RepositoryCallback<TicketActionsLambdaRequest> {
            override fun onComplete(result: _Result<TicketActionsLambdaRequest>?) {
                userAlertClient.closeWaitDialog()
                Log.i("newDataFlag","set")
                sharedPrefsClient.setSelectedTicketNewDataFlag(true)
                userAlertClient.showDialogMessage(
                    "Progress Submitted",
                    "Ticket progress successfully submitted",
                    true
                )
            }
        }
}
