package sukriti.ngo.mis.ui.tickets.fragments.viewList

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.databinding.TicketsHomeClientBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.ui.tickets.RaiseTicketActivity
import sukriti.ngo.mis.ui.tickets.TicketDetailsActivity
import sukriti.ngo.mis.ui.tickets.TicketsViewModel
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_ALL_ACTIVE_TICKET
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_CLOSED_TICKET
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_RAISED_TICKETS
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_TICKET_HOME
import sukriti.ngo.mis.ui.tickets.data.TicketListData
import sukriti.ngo.mis.ui.tickets.interfaces.UpdateTicketListHandler
import sukriti.ngo.mis.ui.tickets.lambda.ListTicket.ListTicketsLambdaRequest
import sukriti.ngo.mis.utils.*

class HomeTicketListClient : Fragment(), NavigationHandler, UpdateTicketListHandler {

    private lateinit var viewModel: TicketsViewModel
    private lateinit var binding: TicketsHomeClientBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var navigationClient: NavigationClient

    private var selectedTabIndex = 1

    companion object {
        private var INSTANCE: HomeTicketListClient? = null

        fun getInstance(): HomeTicketListClient {
            if (INSTANCE == null) {
                INSTANCE =
                    HomeTicketListClient()
            }
            return INSTANCE as HomeTicketListClient
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TicketsHomeClientBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requestTickets()
    }

    private fun init() {
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)
        viewModel = ViewModelProviders.of(this).get(TicketsViewModel::class.java)


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!
                when (tab?.position) {
                    0 -> {
                        navigateTo(NAV_RAISED_TICKETS)
                    }

                    1 -> {
                        navigateTo(NAV_ALL_ACTIVE_TICKET)
                    }

                    2 -> {
                        navigateTo(NAV_CLOSED_TICKET)
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })

    }

    override fun refreshTicketList() {
        requestTickets()
    }

    override fun navigateTo(navigationAction: Int) {
        var allActiveTickets: TicketListNormal? = null
        var closedTicketList: TicketListNormal? = null
        var raisedTicketList: TicketListNormal? = null

        when (navigationAction) {
            TicketsViewModel.NAV_RAISE_NEW_TICKET ->{
                val intent = Intent(requireContext(), RaiseTicketActivity::class.java)
                startActivity(intent)
            }

            NAV_TICKET_HOME ->{
                sharedPrefsClient.setSelectedTicket(viewModel.selectedTicketData)
                val intent = Intent(requireContext(), TicketDetailsActivity::class.java)
                startActivity(intent)
            }

            NAV_RAISED_TICKETS-> {
                if(raisedTicketList == null){
                    raisedTicketList = TicketListNormal.getInstance(NAV_RAISED_TICKETS)
                    raisedTicketList.setNavigationHandler(this)
                }

                navigationClient.loadFragment(
                    raisedTicketList,
                    R.id.container, "NAV_RAISED_TICKETS", false
                )
            }

            NAV_ALL_ACTIVE_TICKET -> {
                if (allActiveTickets == null) {
                    allActiveTickets = TicketListNormal.getInstance(TicketsViewModel.NAV_ALL_ACTIVE_TICKET)
                    allActiveTickets.setNavigationHandler(this)
                }

                navigationClient.loadFragment(
                    allActiveTickets,
                    R.id.container, "NAV_ALL_ACTIVE_TICKET", false
                )
            }

            NAV_CLOSED_TICKET-> {
                if(closedTicketList == null){
                    closedTicketList = TicketListNormal.getInstance(NAV_CLOSED_TICKET)
                    closedTicketList.setNavigationHandler(this)
                }

                navigationClient.loadFragment(
                    closedTicketList,
                    R.id.container, "NAV_CLOSED_TICKET", false
                )
            }
        }
    }

    private fun requestTickets(){
        userAlertClient.showWaitDialog("Fetching your tickets from server...")
        viewModel.fetchTicketsFromServer(ListTicketsLambdaRequest(sharedPrefsClient.getUserDetails()),listTicketsRequestHandler)
    }

    private var listTicketsRequestHandler: RepositoryCallback<String> =
        object : RepositoryCallback<String> {
            override fun onComplete(result: _Result<String>?) {
                userAlertClient.closeWaitDialog()
                if(result is _Result.Success<String>){
                    navigateTo(NAV_RAISED_TICKETS)
                    binding.tabLayout.getTabAt(0)?.select()

                }else{
                    var err = result as _Result.Error<TicketListData>
                    userAlertClient.showDialogMessage("Error Alert",err.message,false)
                }

            }
        }
}
