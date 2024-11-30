package sukriti.ngo.mis.ui.profile.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.configure_user_verify_communication.*
import sukriti.ngo.mis.databinding.ProfileHomeBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData
import sukriti.ngo.mis.ui.administration.fragments.MemberAccess
import sukriti.ngo.mis.ui.administration.fragments.MemberDetails
import sukriti.ngo.mis.ui.administration.fragments.MemberDetailsHome
import sukriti.ngo.mis.ui.profile.ProfileViewModel
import sukriti.ngo.mis.ui.profile.interfaces.UserProfileRequestHandler
import sukriti.ngo.mis.utils.*

class ProfileHome : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: ProfileHomeBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var navigationClient : NavigationClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private var userDetails: Fragment? = null
    private var userAccess: Fragment? = null
    private var selectedTabIndex = 1

    companion object {
        private var INSTANCE: ProfileHome? = null

        fun getInstance(): ProfileHome {
            return INSTANCE
                ?: ProfileHome()
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
        Log.i("__profile","profile");
        binding = ProfileHomeBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(ProfileViewModel::class.java)
        navigationClient = NavigationClient(childFragmentManager)

        userAlertClient.showWaitDialog("Loading user profile...")
//        viewModel.fetchUserDetails(sharedPrefsClient.getUserDetails().user.userName,userProfileRequestHandler)
        viewModel.fetchUserDetails(sharedPrefsClient.getUserDetails().user.userName, profileRequestHandler)


/*        binding.container.visibility=View.VISIBLE
        if(userDetails == null) userDetails = ProfileUserDetails.getInstance()
        navigationClient.loadFragment(userDetails!!,"User Details",true)
        binding.tabLayout.getTabAt(0)?.select()*/

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!

                when(tab?.position){
                    0->{
                        if(userDetails == null) userDetails = ProfileUserDetails.getInstance()
                        navigationClient.loadFragment(userDetails!!,"User Details",true)
                    }

                    1->{
                        if(userAccess == null) userAccess = ProfileUserAccess.getInstance()
                        navigationClient.loadFragment(userAccess!!,"User Access",true)
                    }
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    private var userProfileRequestHandler : UserProfileRequestHandler = object : UserProfileRequestHandler{
        override fun onSuccess(userProfile: MemberDetailsData?) {
            userAlertClient.closeWaitDialog()
            binding.container.visibility = View.VISIBLE
            if(userDetails == null) userDetails = ProfileUserDetails.getInstance()
            navigationClient.loadFragment(userDetails!!,"User Details",true)
            binding.tabLayout.getTabAt(0)?.select()
        }

        override fun onError(error: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!",error,true)
        }

    }

    private var profileRequestHandler : GetProfileDataResultHandler = object : GetProfileDataResultHandler{
        override fun onSuccess(userProfile: UserProfileDataLambdaResult?) {
            Log.i("myProfileData", "Profile Home -> onSuccess()")
            userAlertClient.closeWaitDialog()
            Log.i("myProfileData", "Profile Home -> wait dialog closed")
            binding.container.visibility=View.VISIBLE
            Log.i("myProfileData", "container visibility set")
            if(userDetails == null) userDetails = ProfileUserDetails.getInstance()
            Log.i("myProfileData", "userDetail fragment created " + (userDetails == null).toString())
            Log.i("myProfileData", "Profile Home -> calling load fragment")
            navigationClient.loadFragment(userDetails!!,"User Details",true)
            binding.tabLayout.getTabAt(0)?.select()
        }

        override fun onError(error: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!",error,true)
        }

    }
}
