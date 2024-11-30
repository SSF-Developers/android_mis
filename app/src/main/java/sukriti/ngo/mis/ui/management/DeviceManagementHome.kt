package sukriti.ngo.mis.ui.management

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.ActivityDeviceManagementHomeBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.management.fargments.EnterpriseHome
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient

class DeviceManagementHome : NavigationHandler, AppCompatActivity() {

    private lateinit var binding: ActivityDeviceManagementHomeBinding
    private lateinit var navigationClient: NavigationClient
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var menuItem: MenuItem

    private val NAV_MANAGE_ENTERPRISE = 0
    private val NAV_MANAGE_DEVICES = 1
    private val NAV_ENTERPRISE_HOME = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceManagementHomeBinding.inflate(layoutInflater)
        init()
    }

    private fun init() {
        navigationClient = NavigationClient(supportFragmentManager)
        userAlertClient = UserAlertClient(this)
        setContentView(binding.root)

        binding.toolbar.mainToolbar.title = ""
        setSupportActionBar(binding.toolbar.mainToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        navigateTo(NAV_MANAGE_ENTERPRISE)


        binding.toolbar.mainToolbar.setNavigationOnClickListener {
            try {
                onBackPressed()
            } catch (exception: Exception) {
                Log.i("Exception", "Device Management Home: ${exception.message}")
            }
        }
    }

    override fun navigateTo(navigationAction: Int) {

        when (navigationAction) {

            NAV_MANAGE_ENTERPRISE -> {

                navigationClient.loadFragment(
                    ManageEnterprises.getInstance(),
                    binding.toolbar.mainToolbarTitle,
                    "Management",
                    "management",
                    false
                )
            }

            NAV_MANAGE_DEVICES -> {
                navigationClient.loadFragment(
                    ManageDevices.getInstance(),
                    binding.toolbar.mainToolbarTitle,
                    "Manage Devices",
                    "manageDevices",
                    true
                )

            }

            NAV_ENTERPRISE_HOME -> {
                navigationClient.loadFragment(
                    EnterpriseHome.getInstance(),
                    binding.toolbar.mainToolbarTitle,
                    "Enterprise Home",
                    "enterpriseHOme",
                    true
                )
            }

        }
    }


}