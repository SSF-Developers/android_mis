package sukriti.ngo.mis.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import sukriti.ngo.mis.databinding.ActivityLogin2Binding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.signup.ConfigureUserHome
import sukriti.ngo.mis.ui.login.fragements.Login
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient

class LoginActivity : NavigationHandler,AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: ActivityLogin2Binding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationClient: NavigationClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        userAlertClient = UserAlertClient(this)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        navigationClient = NavigationClient(supportFragmentManager)
        //navigationClient.loadFragment(CreateUser(), "ClientList", true)
        navigationClient.loadFragment(Login.getInstance(),"login", false)
    }


    override fun navigateTo(navigationAction: Int) {
        when(navigationAction){
            LoginViewModel.NAV_ACTION_LOGIN->{
                navigationClient.loadFragment(Login.getInstance(),"login", false)
            }
            LoginViewModel.NAV_ACTION_NEW_USER->{
                navigationClient.loadFragment(ConfigureUserHome.getInstance(),"configureUserHome", true)
            }
        }
    }
}
