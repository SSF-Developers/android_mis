package sukriti.ngo.mis.ui.signup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import sukriti.ngo.mis.databinding.ConfigureUserHomeBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.login.LoginViewModel
import sukriti.ngo.mis.ui.login.data.LoginResult
import sukriti.ngo.mis.ui.signup.configure_user.UserCommunication
import sukriti.ngo.mis.ui.signup.configure_user.UserDetails
import sukriti.ngo.mis.ui.signup.configure_user.UserOrganisation
import sukriti.ngo.mis.utils.*

class ConfigureUserHome : Fragment() {

    private lateinit var viewModel: SignupViewModel
    private lateinit var parentViewModel: LoginViewModel
    private lateinit var binding: ConfigureUserHomeBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var navigationClient: NavigationClient
    private var fragmentUserDetails: Fragment? = null
    private var fragmentUserCommunication: Fragment? = null
    private var fragmentUserOrganisation: Fragment? = null
    private var selectedTabIndex = 1;
    companion object {
        private var INSTANCE: ConfigureUserHome? = null

        fun getInstance(): ConfigureUserHome {
            return INSTANCE
                ?: ConfigureUserHome()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationHandler) {
            navigationHandler = context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement NavigationHandler"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = ConfigureUserHomeBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(this).get(SignupViewModel::class.java)
        parentViewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel::class.java)
        viewModel.setUserProfile()
        navigationClient = NavigationClient(childFragmentManager)
        if(fragmentUserDetails == null) fragmentUserDetails = UserDetails.getInstance()
        navigationClient.loadFragment(fragmentUserDetails!!,"userDetails",true)
        binding.tabLayout.getTabAt(1)?.select()


        binding.appbar.confirm.setOnClickListener {

            if(fragmentUserDetails!= null) (fragmentUserDetails as UserDetails).saveForm()
            if(fragmentUserCommunication!= null) (fragmentUserCommunication as UserCommunication).saveForm()

            if(viewModel.validateUserConfigurationForm()){
                parentViewModel.setLoginResult(LoginResult(LoginResult.STATUS_AUTO_LOGIN,"User configuration validated. Logging in..." ))
                navigationHandler.navigateTo(LoginViewModel.NAV_ACTION_LOGIN)
            }else{
                //Toggle to appropriate tab to show errors
                if(selectedTabIndex == 1){
                    if(viewModel.errorStateUserDetails.value?.isNotEmpty()!!){

                    }else{
                        if(fragmentUserCommunication== null) fragmentUserCommunication = UserCommunication.getInstance()
                        navigationClient.loadFragment(fragmentUserCommunication!!,"UserCommunication",true)
                        binding.tabLayout.getTabAt(selectedTabIndex)?.select()
                    }
                }

                if(selectedTabIndex == 2){
                    if(viewModel.errorStateUserCommunication.value?.isNotEmpty()!!){

                    }else{
                        if(fragmentUserDetails == null) fragmentUserDetails = UserDetails.getInstance()
                        navigationClient.loadFragment(fragmentUserDetails!!,"userDetails",true)
                        binding.tabLayout.getTabAt(selectedTabIndex)?.select()
                    }
                }

                if(selectedTabIndex == 0){
                    if(viewModel.errorStateUserDetails.value?.isNotEmpty()!!){
                        if(fragmentUserDetails == null) fragmentUserDetails = UserDetails.getInstance()
                        navigationClient.loadFragment(fragmentUserDetails!!,"userDetails",true)
                        selectedTabIndex = 1
                        binding.tabLayout.getTabAt(selectedTabIndex)?.select()
                    }else{
                        if(fragmentUserCommunication== null) fragmentUserCommunication = UserCommunication.getInstance()
                        navigationClient.loadFragment(fragmentUserCommunication!!,"UserCommunication",true)
                        selectedTabIndex = 2
                        binding.tabLayout.getTabAt(selectedTabIndex)?.select()
                    }
                }
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!

                when(tab?.position){
                    0->{
                        if(fragmentUserOrganisation == null) fragmentUserOrganisation = UserOrganisation.getInstance()
                        navigationClient.loadFragment(fragmentUserOrganisation!!,"userOrganisation",true)
                    }

                    1->{
                        if(fragmentUserDetails == null) fragmentUserDetails = UserDetails.getInstance()
                        navigationClient.loadFragment(fragmentUserDetails!!,"userDetails",true)
                    }

                    2 ->{
                        if(fragmentUserCommunication== null) fragmentUserCommunication = UserCommunication.getInstance()
                        navigationClient.loadFragment(fragmentUserCommunication!!,"UserCommunication",true)
                    }
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }
}
