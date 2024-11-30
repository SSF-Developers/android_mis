package sukriti.ngo.mis.ui.tickets

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import sukriti.ngo.mis.databinding.TicketsTicketDetailsActivityBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_RAISE_NEW_TICKET
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_TICKET_HOME
import sukriti.ngo.mis.ui.tickets.fragments.detail.HomeTicketDetail
import sukriti.ngo.mis.ui.tickets.fragments.raise.RaiseTicket
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient


class TicketDetailsActivity :  NavigationHandler,AppCompatActivity() {

    private lateinit var viewModel: TicketDetailViewModel
    private lateinit var binding: TicketsTicketDetailsActivityBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationClient: NavigationClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private var currentTab  = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }


    private fun init() {
        sharedPrefsClient = SharedPrefsClient(applicationContext)
        binding = TicketsTicketDetailsActivityBinding.inflate(layoutInflater)
        viewModel = ViewModelProviders.of(this).get(TicketDetailViewModel::class.java)
        navigationClient = NavigationClient(supportFragmentManager)
        userAlertClient = UserAlertClient(this)
        setContentView(binding.root)

        viewModel.selectedTicketData = sharedPrefsClient.getSelectedTicket()

        //Action bar and Menu Drawer
        binding.toolbar.mainToolbar.title = ""
        setSupportActionBar(binding.toolbar.mainToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        navigateTo(NAV_TICKET_HOME)

        binding.toolbar.mainToolbar.setNavigationOnClickListener {
            onBackPressed()
        }


    }

    override fun navigateTo(navigationAction: Int) {
        when(navigationAction){
            NAV_TICKET_HOME -> {
                currentTab = NAV_TICKET_HOME
                navigationClient.loadFragment(HomeTicketDetail.getInstance(),binding.toolbar.title, "Ticket Details", "ticketDetails", false)
            }
        }
    }


}
