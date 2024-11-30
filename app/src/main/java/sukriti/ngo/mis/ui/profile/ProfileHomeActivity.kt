package sukriti.ngo.mis.ui.profile

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import sukriti.ngo.mis.databinding.AdministrationHomeActivityBinding
import sukriti.ngo.mis.databinding.ProfileHomeActivityBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.administration.fragments.CreateUser
import sukriti.ngo.mis.ui.administration.fragments.DefineAccess
import sukriti.ngo.mis.ui.administration.fragments.ManageTeam
import sukriti.ngo.mis.ui.administration.fragments.MemberDetailsHome
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.profile.ProfileViewModel.Companion.NAV_PROFILE_HOME
import sukriti.ngo.mis.ui.profile.fragments.ProfileHome
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient

class ProfileHomeActivity : NavigationHandler,AppCompatActivity() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: ProfileHomeActivityBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationClient: NavigationClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }


    private fun init() {
        Log.i("__profile","profile-1");
        binding = ProfileHomeActivityBinding.inflate(layoutInflater)
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        navigationClient = NavigationClient(supportFragmentManager)
        userAlertClient = UserAlertClient(this)
        setContentView(binding.root)

        //Action bar and Menu Drawer
        binding.toolbar.mainToolbar.title = ""
        setSupportActionBar(binding.toolbar.mainToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        navigateTo(NAV_PROFILE_HOME)

        binding.toolbar.mainToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    override fun navigateTo(navigationAction: Int) {
        when(navigationAction){
            NAV_PROFILE_HOME -> {
                navigationClient.loadFragment(ProfileHome.getInstance(),binding.toolbar.mainToolbarTitle, "Profile", "profile", false)
            }
        }
    }
}
