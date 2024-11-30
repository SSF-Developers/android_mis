package sukriti.ngo.mis.ui.tickets.fragments.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.TicketsTicketDetailsHomeBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.tickets.TicketDetailViewModel
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_TICKET_DETAILS
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_TICKET_PROGRESS
import sukriti.ngo.mis.utils.*

class HomeTicketDetail : Fragment(), NavigationHandler {

    private lateinit var viewModel: TicketDetailViewModel
    private lateinit var binding: TicketsTicketDetailsHomeBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var navigationClient: NavigationClient

    private var selectedTabIndex = 1

    companion object {
        private var INSTANCE: HomeTicketDetail? = null

        fun getInstance(): HomeTicketDetail {
            if (INSTANCE == null) {
                INSTANCE =
                    HomeTicketDetail()
            }
            return INSTANCE as HomeTicketDetail
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TicketsTicketDetailsHomeBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)
        viewModel = ViewModelProviders.of(this).get(TicketDetailViewModel::class.java)

        navigateTo(NAV_TICKET_DETAILS)
        binding.tabLayout.getTabAt(0)?.select()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!
                when (tab?.position) {
                    0 -> {
                        navigateTo(NAV_TICKET_DETAILS)
                    }
                    1 -> {
                        navigateTo(NAV_TICKET_PROGRESS)
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun navigateTo(navigationAction: Int) {
        when (navigationAction) {

            NAV_TICKET_DETAILS-> {
                val fragment =
                    TicketDetail.getInstance()
                fragment.setNavigationHandler(this)
                navigationClient.loadFragment(
                    fragment,
                    R.id.container, "ticketDetails", false
                )
            }

            NAV_TICKET_PROGRESS-> {
                val fragment =
                    TicketProgress.getInstance()
                fragment.setNavigationHandler(this)
                navigationClient.loadFragment(
                    fragment,
                    R.id.container, "ticketProgress", false
                )
            }
        }
    }
}
