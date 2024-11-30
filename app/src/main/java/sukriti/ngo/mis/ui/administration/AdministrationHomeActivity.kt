package sukriti.ngo.mis.ui.administration

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import sukriti.ngo.mis.databinding.AdministrationHomeActivityBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.administration.AdministrationViewModel.Companion.NAV_CREATE_USER
import sukriti.ngo.mis.ui.administration.AdministrationViewModel.Companion.NAV_DEFINE_ACCESS
import sukriti.ngo.mis.ui.administration.AdministrationViewModel.Companion.NAV_MANAGE_TEAM
import sukriti.ngo.mis.ui.administration.AdministrationViewModel.Companion.NAV_MEMBER_DETAILS
import sukriti.ngo.mis.ui.administration.AdministrationViewModel.Companion.NAV_GRANT_PERMISSION
import sukriti.ngo.mis.ui.administration.fragments.CreateUser
import sukriti.ngo.mis.ui.administration.fragments.DefineAccess
import sukriti.ngo.mis.ui.administration.fragments.GrantPermission
import sukriti.ngo.mis.ui.administration.fragments.ManageTeam
import sukriti.ngo.mis.ui.administration.fragments.MemberDetailsHome
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient

class AdministrationHomeActivity : NavigationHandler,AppCompatActivity() {

    private lateinit var binding: AdministrationHomeActivityBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationClient: NavigationClient

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("AdministrationHome", "onCreate()")
        super.onCreate(savedInstanceState)
        init()
    }


    private fun init() {
        Log.i("AdministrationHome", "init()")
        binding = AdministrationHomeActivityBinding.inflate(layoutInflater)
        navigationClient = NavigationClient(supportFragmentManager)
        userAlertClient = UserAlertClient(this)
        setContentView(binding.root)

        //Action bar and Menu Drawer
        binding.toolbar.mainToolbar.title = ""
        setSupportActionBar(binding.toolbar.mainToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        navigateTo(NAV_MANAGE_TEAM)

        binding.toolbar.mainToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    override fun navigateTo(navigationAction: Int) {
        Log.i("AdministrationHome", "navigateTo()")
        when(navigationAction){
            NAV_MANAGE_TEAM -> {
                Log.i("AdministrationHome", "navigateTo() Team")
                navigationClient.loadFragment(ManageTeam.getInstance(),binding.toolbar.mainToolbarTitle, "Administration", "administration", false)
            }

            NAV_CREATE_USER ->{
                Log.i("AdministrationHome", "navigateTo() Create User")
                navigationClient.loadFragment(CreateUser.getInstance(),binding.toolbar.mainToolbarTitle, "Add Team Member", "addTeamMember", true)
            }

            NAV_MEMBER_DETAILS ->{
                Log.i("AdministrationHome", "navigateTo() Member Details()")
                navigationClient.loadFragment(MemberDetailsHome.getInstance(),binding.toolbar.mainToolbarTitle, "Member Details", "memberDetails", true)
            }

            NAV_DEFINE_ACCESS->{
                Log.i("AdministrationHome", "navigateTo() define access")
                navigationClient.loadFragment(DefineAccess.getInstance(),binding.toolbar.mainToolbarTitle, "Define Access", "defineAccess", true)
            }

            NAV_GRANT_PERMISSION -> {
                Log.i("AdministrationHome", "navigateTo() define access")
                navigationClient.loadFragment(GrantPermission.newInstance(),binding.toolbar.mainToolbarTitle, "Grant Permission", "grantPermission", true)
            }
        }
    }

}
