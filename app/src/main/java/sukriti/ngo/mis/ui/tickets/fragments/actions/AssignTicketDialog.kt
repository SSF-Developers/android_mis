package sukriti.ngo.mis.ui.super_admin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.databinding.AssignTicketDialogBinding
import sukriti.ngo.mis.databinding.DashboardUsageDetailsBinding
import sukriti.ngo.mis.databinding.TicketActionDialogBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COLLECTION_DETAILS_COMPARISON
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COLLECTION_DETAILS_SUMMARY
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COLLECTION_DETAILS_TIMELINE
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_USAGE_DETAILS_COMPARISON
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_USAGE_DETAILS_SUMMARY
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_USAGE_DETAILS_TIMELINE
import sukriti.ngo.mis.ui.tickets.TicketDetailViewModel
import sukriti.ngo.mis.ui.tickets.adapters.TicketFilesAdapter
import sukriti.ngo.mis.ui.tickets.adapters.TicketTeamAdapter
import sukriti.ngo.mis.ui.tickets.data.TicketAction
import sukriti.ngo.mis.ui.tickets.data.TicketDetailsData
import sukriti.ngo.mis.ui.tickets.data.TicketFileItem
import sukriti.ngo.mis.ui.tickets.data.TicketTeamData
import sukriti.ngo.mis.ui.tickets.interfaces.AssignTicketDialogHandler
import sukriti.ngo.mis.ui.tickets.interfaces.TicketActionDialogHandler
import sukriti.ngo.mis.ui.tickets.lambda.ListTeam.ListTicketTeamLambdaRequest
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities

class AssignTicketDialog : DialogFragment() {
    private lateinit var binding: AssignTicketDialogBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: TicketDetailViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var mAssignTicketDialogHandler: AssignTicketDialogHandler
    private lateinit var ticketTeam: List<TicketTeamData>
    private lateinit var ticketAction: TicketAction
    private var selectedTabIndex = 1

    companion object {
        private var INSTANCE: AssignTicketDialog? = null

        fun getInstance(): AssignTicketDialog {
            return INSTANCE
                ?: AssignTicketDialog()
        }
    }

    fun setData(
        ticketAction: TicketAction,
        ticketTeam: List<TicketTeamData>,
        mAssignTicketDialogHandler: AssignTicketDialogHandler
    ) {
        this.ticketAction = ticketAction
        this.mAssignTicketDialogHandler = mAssignTicketDialogHandler
        this.ticketTeam = ticketTeam
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AssignTicketDialogBinding.inflate(inflater, container, false)
        binding.header.text = "Collection Stats"
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

    private fun init() {
        viewModel =
            ViewModelProviders.of(requireActivity()).get(TicketDetailViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)

        binding.header.text = ticketAction.actionName
        binding.actionDescription.text = Nomenclature.getActionDescription(ticketAction.actionId)
        binding.confirmAction.visibility = View.VISIBLE
        binding.confirmAction.hint = "Validate Action"

        binding.close.setOnClickListener(View.OnClickListener {
            dismiss()
        })

        binding.submit.setOnClickListener(View.OnClickListener {
            Utilities.hideKeypad(requireContext(), binding.comment.editText)
            if (validate()) {
                var comment = binding.comment.editText?.text.toString()
                var user = binding.selectUSer.editText?.text.toString()
                var assigneeId = user.split(":")[0].trim()
                var assigneeRole = user.split(": ")[1].trim()
                mAssignTicketDialogHandler.onSubmit(ticketAction, comment, assigneeId, assigneeRole)
                dismiss()
            }
        })

        val items = getList(ticketTeam)
        val adapter =
            ArrayAdapter(requireContext(), R.layout.item_role_selection, items)
        (binding.selectUSerAct as? AutoCompleteTextView)?.setAdapter(adapter)
        (binding.selectUSerAct as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, pos, id ->
                Log.i("tag", items[pos].toString())
                Utilities.hideKeypad(context, binding.comment.editText)
            }
    }

    fun validate(): Boolean {
        var validated = true

        if (binding.selectUSer.editText?.text.toString().isEmpty()) {
            validated = false
            setError(
                binding.comment,
                "Select a user to proceed"
            )
        } else {
            clearError(binding.selectUSer)
        }

        if (binding.comment.editText?.text.toString().isEmpty()) {
            validated = false
            setError(
                binding.comment,
                "A comment describing your intent is mandatory."
            )
        } else {
            clearError(binding.comment)
        }

        if (binding.confirmAction.editText?.text.toString() != "assign lead") {
            validated = false
            setError(
                binding.confirmAction,
                "Type 'assign lead' to accept ticket lead role."
            )
        } else {
            clearError(binding.confirmAction)
        }

        return validated
    }

    fun setError(textInput: TextInputLayout, error: String) {
        textInput.error = error
    }

    fun clearError(textInput: TextInputLayout) {
        textInput.isErrorEnabled = false
    }


    fun getList(ticketTeam: List<TicketTeamData>): ArrayList<String> {
        var list = arrayListOf<String>()
        for (item in ticketTeam) {
            list.add(item.user + ": " + item.role)
        }

        return list
    }
}
