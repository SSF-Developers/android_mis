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
import kotlinx.android.synthetic.main.login.*
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.databinding.TicketsHomeVaBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.ui.tickets.RaiseTicketActivity
import sukriti.ngo.mis.ui.tickets.TicketDetailsActivity
import sukriti.ngo.mis.ui.tickets.TicketsViewModel
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_ALL_ACTIVE_TICKET
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_ASSIGNED_TICKETS
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_CLOSED_TICKET
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_QUEUED_TICKETS
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_RAISED_TICKETS
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_RAISE_NEW_TICKET
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_TEAM_ASSIGNED_TICKET
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_TICKET_HOME
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_UN_QUEUED_TICKETS
import sukriti.ngo.mis.ui.tickets.data.TicketListData
import sukriti.ngo.mis.ui.tickets.interfaces.UpdateTicketListHandler
import sukriti.ngo.mis.ui.tickets.lambda.ListTicket.ListTicketsLambdaRequest
import sukriti.ngo.mis.utils.*

class HomeTicketListVendorAdmin : Fragment(), NavigationHandler, UpdateTicketListHandler {

    private lateinit var viewModel: TicketsViewModel
    private lateinit var binding: TicketsHomeVaBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var navigationClient: NavigationClient

    private var selectedTabIndex = 1

    companion object {
        private var INSTANCE: HomeTicketListVendorAdmin? = null

        fun getInstance(): HomeTicketListVendorAdmin {
            if (INSTANCE == null) {
                INSTANCE =
                    HomeTicketListVendorAdmin()
            }
            return INSTANCE as HomeTicketListVendorAdmin
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TicketsHomeVaBinding.inflate(layoutInflater)
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
                        navigateTo(NAV_QUEUED_TICKETS)
                    }
                    2 -> {
                        navigateTo(NAV_ASSIGNED_TICKETS)
                    }

                    3 -> {
                        navigateTo(NAV_TEAM_ASSIGNED_TICKET)
                    }
                    4 -> {
                        navigateTo(NAV_ALL_ACTIVE_TICKET)
                    }
                    5 -> {
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
        var raisedTicketList: TicketListNormal? = null
        var queuedTicketList: TicketListNormal? = null
        var selfAssignedTicketList: TicketListNormal? = null
        var teamAssignedTicketList: TicketListNormal? = null
        var allActiveTickets: TicketListNormal? = null
        var closedTicketList: TicketListNormal? = null

        when (navigationAction) {

            NAV_RAISE_NEW_TICKET -> {
                var intent = Intent(requireContext(), RaiseTicketActivity::class.java)
                startActivity(intent)
            }

            NAV_TICKET_HOME -> {
                sharedPrefsClient.setSelectedTicket(viewModel.selectedTicketData)
                var intent = Intent(requireContext(), TicketDetailsActivity::class.java)
                startActivity(intent)
            }

            NAV_RAISED_TICKETS -> {
                if (raisedTicketList == null) {
                    raisedTicketList = TicketListNormal.getInstance(NAV_RAISED_TICKETS)
                    raisedTicketList.setNavigationHandler(this)
                }

                navigationClient.loadFragment(
                    raisedTicketList,
                    R.id.container, "NAV_RAISED_TICKETS", false
                )
            }

            NAV_QUEUED_TICKETS -> {
                if (queuedTicketList == null) {
                    queuedTicketList = TicketListNormal.getInstance(NAV_QUEUED_TICKETS)
                    queuedTicketList.setNavigationHandler(this)
                }

                navigationClient.loadFragment(
                    queuedTicketList,
                    R.id.container, "NAV_QUEUED_TICKETS", false
                )
            }

            NAV_ASSIGNED_TICKETS -> {
                if (selfAssignedTicketList == null) {
                    selfAssignedTicketList = TicketListNormal.getInstance(NAV_ASSIGNED_TICKETS)
                    selfAssignedTicketList.setNavigationHandler(this)
                }

                navigationClient.loadFragment(
                    selfAssignedTicketList,
                    R.id.container, "NAV_SELF_ASSIGNED_TICKET", false
                )
            }

            NAV_TEAM_ASSIGNED_TICKET -> {
                if (teamAssignedTicketList == null) {
                    teamAssignedTicketList = TicketListNormal.getInstance(NAV_TEAM_ASSIGNED_TICKET)
                    teamAssignedTicketList.setNavigationHandler(this)
                }

                navigationClient.loadFragment(
                    teamAssignedTicketList,
                    R.id.container, "NAV_TEAM_ASSIGNED_TICKET", false
                )
            }

            NAV_CLOSED_TICKET -> {
                if (closedTicketList == null) {
                    closedTicketList = TicketListNormal.getInstance(NAV_CLOSED_TICKET)
                    closedTicketList.setNavigationHandler(this)
                }

                navigationClient.loadFragment(
                    closedTicketList,
                    R.id.container, "NAV_CLOSED_TICKET", false
                )
            }
            NAV_ALL_ACTIVE_TICKET -> {
                if (allActiveTickets == null) {
                    allActiveTickets = TicketListNormal.getInstance(NAV_ALL_ACTIVE_TICKET)
                    allActiveTickets.setNavigationHandler(this)
                }

                navigationClient.loadFragment(
                    allActiveTickets,
                    R.id.container, "NAV_ALL_TICKET", false
                )
            }
        }
    }

    private fun requestTickets() {
        userAlertClient.showWaitDialog("Fetching your tickets from server...")
        viewModel.fetchTicketsFromServer(
            ListTicketsLambdaRequest(sharedPrefsClient.getUserDetails()),
            listTicketsRequestHandler
        )
    }

    private var listTicketsRequestHandler: RepositoryCallback<String> =
        object : RepositoryCallback<String> {
            override fun onComplete(result: _Result<String>?) {
                userAlertClient.closeWaitDialog()
                if (result is _Result.Success<String>) {
                    navigateTo(NAV_RAISED_TICKETS)
                    binding.tabLayout.getTabAt(0)?.select()

                } else {
                    var err = result as _Result.Error<TicketListData>
                    Log.i("_misTest", "onComplete: exp : "+err.exception +"  msg: "+ err.message+"  code: "+err.errorCode)
                    userAlertClient.showDialogMessage("Error Alert", err.message, false)
                }

            }
        }
}
