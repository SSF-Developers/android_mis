package sukriti.ngo.mis.ui.tickets

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import sukriti.ngo.mis.databinding.TicketsRaiseTicketActivityBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_RAISE_NEW_TICKET
import sukriti.ngo.mis.ui.tickets.fragments.raise.RaiseTicket
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient


class RaiseTicketActivity :  NavigationHandler,AppCompatActivity() {

    private lateinit var viewModel: TicketsViewModel
    private lateinit var binding: TicketsRaiseTicketActivityBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationClient: NavigationClient
    private var currentTab  = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }


    private fun init() {
        binding = TicketsRaiseTicketActivityBinding.inflate(layoutInflater)
        viewModel = ViewModelProviders.of(this).get(TicketsViewModel::class.java)
        navigationClient = NavigationClient(supportFragmentManager)
        userAlertClient = UserAlertClient(this)
        setContentView(binding.root)

        //Action bar and Menu Drawer
        binding.toolbar.mainToolbar.title = ""
        setSupportActionBar(binding.toolbar.mainToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        navigateTo(NAV_RAISE_NEW_TICKET)

        binding.toolbar.mainToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.toolbar.submitTicket.setOnClickListener(View.OnClickListener {
            if (currentTab == NAV_RAISE_NEW_TICKET)
                RaiseTicket.getInstance().createTicket()
        })
    }

    override fun navigateTo(navigationAction: Int) {
        when(navigationAction){
            NAV_RAISE_NEW_TICKET -> {
                currentTab = NAV_RAISE_NEW_TICKET
                navigationClient.loadFragment(RaiseTicket.getInstance(),binding.toolbar.title, "Ticket Details", "raiseTicket", false)
            }
        }
    }


}
