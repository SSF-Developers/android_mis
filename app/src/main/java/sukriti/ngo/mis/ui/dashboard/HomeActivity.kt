package sukriti.ngo.mis.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.google.android.material.navigation.NavigationView
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.HomeActivityBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.administration.AdministrationHomeActivity
import sukriti.ngo.mis.ui.complexes.fragments.acces_tree.ComplexesAccessTree
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COMPLEX
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COMPLEXLIST_CONNECTION_STATS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_HOME
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_INCIDENTS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_REPORTS
import sukriti.ngo.mis.ui.dashboard.fragments.connection.ComplexList_ConnectionStats
import sukriti.ngo.mis.ui.login.LoginActivity
import sukriti.ngo.mis.ui.login.data.UserProfile
import sukriti.ngo.mis.ui.management.DeviceManagementHome
import sukriti.ngo.mis.ui.profile.ProfileHomeActivity
import sukriti.ngo.mis.ui.reports.fragments.ReportsHome
import sukriti.ngo.mis.ui.super_admin.fragments.Dashboard
import sukriti.ngo.mis.ui.tickets.RaiseTicketActivity
import sukriti.ngo.mis.ui.tickets.TicketDetailsActivity
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_RAISE_NEW_TICKET
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_TICKET_HOME
import sukriti.ngo.mis.ui.tickets.fragments.viewList.HomeTicketListClient
import sukriti.ngo.mis.ui.tickets.fragments.viewList.HomeTicketListSuperAdmin
import sukriti.ngo.mis.ui.tickets.fragments.viewList.HomeTicketListVendorAdmin
import sukriti.ngo.mis.ui.tickets.fragments.viewList.HomeTicketListVendorManager
import sukriti.ngo.mis.utils.*
import java.util.*

class HomeActivity : NavigationHandler, AppCompatActivity() {

    private lateinit var viewModel: DashboardViewModel
    private lateinit var binding: HomeActivityBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationClient: NavigationClient
    private lateinit var sharedPrefsClient: SharedPrefsClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }


    private fun init() {
        sharedPrefsClient = SharedPrefsClient(applicationContext)
        viewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        binding = HomeActivityBinding.inflate(layoutInflater)
        navigationClient = NavigationClient(supportFragmentManager)
        setContentView(binding.root)


        //Action bar and Menu Drawer
        setSupportActionBar(binding.toolbar.mainToolbar)
        val mDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar.mainToolbar,
            R.string.Menu,
            R.string.Menu
        )
        binding.drawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()
        binding.navView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            performAction(item)
            true
        })

        binding.actions.bottomNavigation.setOnNavigationItemSelectedListener {

            when (it.itemId) {

                R.id.home -> {
                    navigateTo(NAV_HOME)
                    true
                }

                R.id.complex -> {
                    navigateTo(NAV_COMPLEX)
                    true
                }

                R.id.incidents -> {
                    navigateTo(NAV_INCIDENTS)
                    true
                }

                R.id.reports -> {
                    navigateTo(NAV_REPORTS)
                    true
                }

                else -> false
            }

        }

        userAlertClient = UserAlertClient(this)
        navigateTo(NAV_HOME)
        updateDrawerLabels()

        viewModel.bottomNavEnable.observe(this, bottomNavObserver)
    }

    private var bottomNavObserver: Observer<Boolean> = Observer {
        if (it)
            binding.actions.completeBottomNav.visibility = View.VISIBLE
        else
            binding.actions.completeBottomNav.visibility = View.GONE
    }

    fun updateSyncTimeStamp() {
        Log.i("_lastSync", "save")
        sharedPrefsClient.saveLastSynTimestamp(Calendar.getInstance().timeInMillis)
        updateDrawerLabels()
    }

    private fun updateDrawerLabels() {
        var label = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.userName)
        //       Aug Qa rahul Karn
        when {
            Utilities.getTimeDifference(sharedPrefsClient.getLastSynTimestamp()) == "Not synced yet" -> {
                label.text = "Not synced yet"
            }
            else -> {
                label.text =
                    "Last synced " + Utilities.getTimeDifference(sharedPrefsClient.getLastSynTimestamp())
            }
        }
//        ***********
        label = binding.navView.getHeaderView(0).findViewById(R.id.textViewNavUser)
        label.text = "" + sharedPrefsClient.getUserDetails().user.name
    }

    // navigation by id
    private fun performAction(item: MenuItem) {
        binding.drawerLayout.closeDrawers()
        when (item.itemId) {
            R.id.nav_dashboard -> {
                val home: MenuItem = binding.actions.bottomNavigation.menu.findItem(R.id.home)
                home.isChecked = true
                navigateTo(NAV_HOME)
            }

            R.id.nav_complex -> {
                val complex: MenuItem = binding.actions.bottomNavigation.menu.findItem(R.id.complex)
                complex.isChecked = true
                navigateTo(NAV_COMPLEX)
            }

            R.id.nav_incidence -> {
                val incidence: MenuItem = binding.actions.bottomNavigation.menu.findItem(R.id.incidents)
                incidence.isChecked = true
                navigateTo(NAV_INCIDENTS)
            }

            R.id.nav_reports -> {
                val reports: MenuItem = binding.actions.bottomNavigation.menu.findItem(R.id.reports)
                reports.isChecked = true
                navigateTo(NAV_REPORTS)
            }

            R.id.nav_administration -> {
                if (viewModel.hasAdministrationAccess()) {
                    val intent = Intent(applicationContext, AdministrationHomeActivity::class.java)
                    startActivity(intent)
                } else {
                    userAlertClient.showDialogMessage(
                        "Access Denied",
                        "You do not have administration privileges. Please contact admin for further information.",
                        false
                    )
                }
            }

            R.id.nav_device_managementHome -> {
                val intent = Intent(applicationContext, DeviceManagementHome::class.java)
                startActivity(intent)
            }

            R.id.nav_usageCollection -> {

            }

            R.id.nav_profile -> {
                Log.i("__profile", "profile-0")
                val intent = Intent(applicationContext, ProfileHomeActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_sign_out -> {
                try {
                    val username = AuthenticationClient.getCurrUser()
                    val user: CognitoUser = AuthenticationClient.getPool().getUser(username)
                    user.signOut()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
        }
    }

//    Abstract method response for navigation

    override fun navigateTo(navigationAction: Int) {
        when (navigationAction) {
            NAV_HOME -> {
                navigationClient.loadFragment(
                    Dashboard.getInstance(),
                    binding.toolbar.mainToolbarTitle,
                    "Home",
                    "dashboard",
                    false
                )
            }

            NAV_COMPLEX -> {
                navigationClient.loadFragment(
                    ComplexesAccessTree.getInstance(),
                    binding.toolbar.mainToolbarTitle,
                    "Complexes",
                    "complexes",
                    false
                )
            }

            NAV_INCIDENTS -> {
                navigationClient.loadFragment(
                    getIncidentHome(),
                    binding.toolbar.mainToolbarTitle,
                    "Incidents",
                    "incidents",
                    false
                )
            }

            NAV_REPORTS -> {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.container) // Use your actual container ID here

                if (currentFragment !is ReportsHome) { // Replace `ReportsHome` with your actual fragment class name
                    navigationClient.loadFragment(
                        ReportsHome.getInstance(),
                        binding.toolbar.mainToolbarTitle,
                        "Reports",
                        "reports",
                        false
                    )
                } else {
                    // Optionally handle re-selection if desired, e.g., refresh data, scroll to top, etc.
                }
            }


            NAV_TICKET_HOME -> {
                val intent = Intent(applicationContext, TicketDetailsActivity::class.java)
                startActivity(intent)
            }

            NAV_RAISE_NEW_TICKET -> {
                val intent = Intent(applicationContext, RaiseTicketActivity::class.java)
                startActivity(intent)
            }

            NAV_COMPLEXLIST_CONNECTION_STATS -> {
                navigationClient.loadFragment(
                    ComplexList_ConnectionStats.getInstance(),
                    binding.toolbar.mainToolbarTitle,
                    "Complex Connections",
                    "complexes",
                    true
                )
            }
        }
    }

    private fun getIncidentHome(): Fragment {
        //userRole = UserProfile.Companion.UserRole.SuperAdmin
        return when (sharedPrefsClient.getUserDetails().role) {
            UserProfile.Companion.UserRole.SuperAdmin -> {
                HomeTicketListSuperAdmin.getInstance()
            }
            UserProfile.Companion.UserRole.VendorAdmin -> {
                HomeTicketListVendorAdmin.getInstance()
            }
            UserProfile.Companion.UserRole.VendorManager -> {
                HomeTicketListVendorManager.getInstance()
            }
            else -> {
                //For all client roles; single incidentHome
                HomeTicketListClient.getInstance()
            }
        }

    }
}
