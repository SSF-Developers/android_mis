package sukriti.ngo.mis.ui.tickets.fragments.detail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.databinding.TicketsProgressBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.ui.tickets.TicketDetailViewModel
import sukriti.ngo.mis.ui.tickets.adapters.TicketProgressAdapter
import sukriti.ngo.mis.ui.tickets.data.TicketFileItem
import sukriti.ngo.mis.ui.tickets.data.TicketProgressData
import sukriti.ngo.mis.ui.tickets.interfaces.SubmitTicketHandler
import sukriti.ngo.mis.ui.tickets.lambda.ListProgress.ListTicketProgressLambdaRequest
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities


class TicketProgress : Fragment() {
    private lateinit var viewModel: TicketDetailViewModel
    private lateinit var binding: TicketsProgressBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var navigationClient: NavigationClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
//    private lateinit var submitTicketHandler: SubmitTicketHandler
    private lateinit var mNavigationHandler: NavigationHandler
//    private var REQUEST_PERMISSION = 1

    companion object {
        private var INSTANCE: TicketProgress? = null

        fun getInstance(): TicketProgress {
            if (INSTANCE == null) {
                Log.i("__loadData", "usageReport:GraphicalReport()")
                INSTANCE =
                    TicketProgress()
            }
            return INSTANCE as TicketProgress
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
    ): View {
        Log.i("__profile", "profile")
        binding = TicketsProgressBinding.inflate(layoutInflater)
        init()
        return binding.root
    }


    private fun init() {
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(TicketDetailViewModel::class.java)
        navigationClient = NavigationClient(childFragmentManager)

        loadData()
    }

    private fun setNoData(message: String) {
        binding.grid.visibility = View.GONE
        binding.noDataContainer.noDataContainer.visibility = View.VISIBLE
        binding.noDataContainer.noDataLabel.text = message
        binding.header.visibility = View.GONE
    }

    fun loadData() {
        userAlertClient.showWaitDialog("Loading ticket progress logs...")
        viewModel.getTicketProgressList(
            ListTicketProgressLambdaRequest(viewModel.selectedTicketData.ticket_id),getTicketProgressRequestHandler
        )

        binding.ticketRow.status.text = viewModel.selectedTicketData.ticket_status
        binding.ticketRow.ticketId.text = viewModel.selectedTicketData.ticket_id
        binding.ticketRow.complex.text = viewModel.selectedTicketData.complex_name
        binding.ticketRow.state.text = viewModel.selectedTicketData.state_code + ":" + viewModel.selectedTicketData.district_name
        binding.ticketRow.city.text = viewModel.selectedTicketData.city_name
        binding.ticketRow.userRole.text = viewModel.selectedTicketData.creator_role
        binding.ticketRow.userName.text = viewModel.selectedTicketData.creator_id
        binding.ticketRow.date.text = Utilities.getTimeDifference(viewModel.selectedTicketData.timestamp)
        binding.ticketRow.critical.text = viewModel.selectedTicketData.criticality
        binding.ticketRow.criticalIv.setBackgroundResource(R.drawable.surface_circle_red)
        binding.ticketRow.title.text = viewModel.selectedTicketData.title

    }

    private var getTicketProgressRequestHandler: RepositoryCallback<List<TicketProgressData>> =
        RepositoryCallback { result ->
            userAlertClient.closeWaitDialog()
            val mTicketProgressData = (result as _Result.Success<List<TicketFileItem>>).data as List<TicketProgressData>

            if(mTicketProgressData.isEmpty()){
                setNoData("No progress logged on the ticket.")
            }else{

                val gridLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                val accessTreeAdapter =
                    TicketProgressAdapter(
                        mTicketProgressData
                    )
                binding.grid.layoutManager = gridLayoutManager
                binding.grid.adapter = accessTreeAdapter
                binding.grid.visibility = View.VISIBLE
                binding.noDataContainer.noDataContainer.visibility = View.GONE
                binding.header.visibility = View.VISIBLE
            }
        }
}
