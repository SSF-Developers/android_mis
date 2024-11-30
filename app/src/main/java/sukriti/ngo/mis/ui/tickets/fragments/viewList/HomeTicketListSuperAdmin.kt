package sukriti.ngo.mis.ui.tickets.fragments.viewList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.databinding.TicketsHomeSaBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.ui.tickets.RaiseTicketActivity
import sukriti.ngo.mis.ui.tickets.TicketDetailsActivity
import sukriti.ngo.mis.ui.tickets.TicketsViewModel
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_ALL_ASSIGNED_TICKETS
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_ALL_CLOSED_TICKET
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_ALL_OPEN_TICKETS
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_ALL_QUEUED_TICKETS
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_ALL_RESOLVED_TICKETS
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_ALL_UN_QUEUED_TICKETS
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_ASSIGNED_TICKETS
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_CLOSED_TICKET
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_QUEUED_TICKETS
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_TICKET_HOME
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_UN_QUEUED_TICKETS
import sukriti.ngo.mis.ui.tickets.data.TicketListData
import sukriti.ngo.mis.ui.tickets.interfaces.UpdateTicketListHandler
import sukriti.ngo.mis.ui.tickets.lambda.ListTicket.ListTicketsLambdaRequest
import sukriti.ngo.mis.utils.*

class HomeTicketListSuperAdmin : Fragment(), NavigationHandler, UpdateTicketListHandler {

    private lateinit var viewModel: TicketsViewModel
    private lateinit var binding: TicketsHomeSaBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var navigationClient: NavigationClient

    private var selectedTabIndex = 1

    companion object {
        private var INSTANCE: HomeTicketListSuperAdmin? = null

        fun getInstance(): HomeTicketListSuperAdmin {
            if (INSTANCE == null) {
                INSTANCE =
                    HomeTicketListSuperAdmin()
            }
            return INSTANCE as HomeTicketListSuperAdmin
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TicketsHomeSaBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.i("newDataFlag","check")
//        if (sharedPrefsClient.getSelectedTicketNewDataFlag())
            requestTickets()
    }

    private fun init() {
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)
        viewModel = ViewModelProviders.of(this).get(TicketsViewModel::class.java)

//        navigateTo(NAV_UN_QUEUED_TICKETS)
//        binding.tabLayout.getTabAt(0)?.select()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!
                when (tab?.position) {
                    0 -> {
                        navigateTo(NAV_ALL_UN_QUEUED_TICKETS)
                    }
                    1 -> {
                        navigateTo(NAV_ALL_QUEUED_TICKETS)
                    }
                    2 -> {
                        navigateTo(NAV_ALL_ASSIGNED_TICKETS)
                    }

                    3 -> {
                        navigateTo(NAV_ALL_OPEN_TICKETS)
                    }
                    4 -> {
                        navigateTo(NAV_ALL_RESOLVED_TICKETS)
                    }
                    5 -> {
                        navigateTo(NAV_ALL_CLOSED_TICKET)
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
        var unQueuedTicketList: TicketListNormal? = null
        var queuedTicketList: TicketListNormal? = null
        var assignedTicketList: TicketListNormal? = null
        var openTicketList: TicketListNormal? = null
        var resolvedTicketList: TicketListNormal? = null
        var closedTicketList: TicketListNormal? = null

        when (navigationAction) {

            TicketsViewModel.NAV_RAISE_NEW_TICKET -> {
                val intent = Intent(requireContext(), RaiseTicketActivity::class.java)
                startActivity(intent)
            }

            NAV_TICKET_HOME -> {
                sharedPrefsClient.setSelectedTicket(viewModel.selectedTicketData)
                var intent = Intent(requireContext(), TicketDetailsActivity::class.java)
                startActivity(intent)
            }

            NAV_ALL_UN_QUEUED_TICKETS -> {
                if (unQueuedTicketList == null) {
                    unQueuedTicketList = TicketListNormal.getInstance(NAV_ALL_UN_QUEUED_TICKETS)
                    unQueuedTicketList.setNavigationHandler(this)
                }

                navigationClient.loadFragment(
                    unQueuedTicketList,
                    R.id.container, "NAV_ALL_UN_QUEUED_TICKETS", false
                )
            }

            NAV_ALL_QUEUED_TICKETS -> {
                if (queuedTicketList == null) {
                    queuedTicketList = TicketListNormal.getInstance(NAV_ALL_QUEUED_TICKETS)
                    queuedTicketList.setNavigationHandler(this)
                }

                navigationClient.loadFragment(
                    queuedTicketList,
                    R.id.container, "NAV_ALL_QUEUED_TICKETS", false
                )
            }

            NAV_ALL_ASSIGNED_TICKETS -> {
                if (assignedTicketList == null) {
                    assignedTicketList = TicketListNormal.getInstance(NAV_ALL_ASSIGNED_TICKETS)
                    assignedTicketList.setNavigationHandler(this)
                }

                navigationClient.loadFragment(
                    assignedTicketList,
                    R.id.container, "NAV_ALL_ASSIGNED_TICKETS", false
                )
            }

            NAV_ALL_OPEN_TICKETS -> {
                if (openTicketList == null) {
                    openTicketList = TicketListNormal.getInstance(NAV_ALL_OPEN_TICKETS)
                    openTicketList.setNavigationHandler(this)
                }

                navigationClient.loadFragment(
                    openTicketList,
                    R.id.container, "NAV_ALL_OPEN_TICKETS", false
                )
            }

            NAV_ALL_RESOLVED_TICKETS -> {
                if (resolvedTicketList == null) {
                    resolvedTicketList = TicketListNormal.getInstance(NAV_ALL_RESOLVED_TICKETS)
                    resolvedTicketList.setNavigationHandler(this)
                }

                navigationClient.loadFragment(
                    resolvedTicketList,
                    R.id.container, "NAV_ALL_RESOLVED_TICKETS", false
                )
            }

            NAV_ALL_CLOSED_TICKET -> {
                if (closedTicketList == null) {
                    closedTicketList = TicketListNormal.getInstance(NAV_ALL_CLOSED_TICKET)
                    closedTicketList.setNavigationHandler(this)
                }

                navigationClient.loadFragment(
                    closedTicketList,
                    R.id.container, "NAV_ALL_CLOSED_TICKET", false
                )
            }
        }
    }

    private fun requestTickets() {
        userAlertClient.showWaitDialog("Fetching your tickets from server...")
        viewModel.fetchTicketsFromServer(ListTicketsLambdaRequest(sharedPrefsClient.getUserDetails()), listTicketsRequestHandler)
    }

    private var listTicketsRequestHandler: RepositoryCallback<String> = RepositoryCallback { result ->
            Log.i("loadTicketFlow", "00")
            userAlertClient.closeWaitDialog()
            if (result is _Result.Success<String>) {
                //Clear Flag
                Log.i("newDataFlag","clear")
                sharedPrefsClient.setSelectedTicketNewDataFlag(false)

                Log.i("loadTicketFlow", "01")
                navigateTo(NAV_ALL_UN_QUEUED_TICKETS)
                binding.tabLayout.getTabAt(0)?.select()

            } else {
                Log.i("loadTicketFlow", "02")
                val err = result as _Result.Error<TicketListData>
                userAlertClient.showDialogMessage("Error Alert", err.message, false)
            }
        }
}
