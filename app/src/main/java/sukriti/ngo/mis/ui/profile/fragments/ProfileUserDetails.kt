package sukriti.ngo.mis.ui.profile.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import sukriti.ngo.mis.databinding.MemberDetailsBinding
import sukriti.ngo.mis.databinding.ProfileUserDetailsBinding
import sukriti.ngo.mis.repository.utils.DateConverter.toDateString_FromAwsDateFormat
import sukriti.ngo.mis.ui.profile.ProfileViewModel
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities.*


class ProfileUserDetails : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: ProfileUserDetailsBinding
    private lateinit var userAlertClient: UserAlertClient

    companion object {
        private var INSTANCE: ProfileUserDetails? = null

        fun getInstance(): ProfileUserDetails {
            return INSTANCE
                ?: ProfileUserDetails()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = ProfileUserDetailsBinding.inflate(layoutInflater)
        Log.i("myProfileData", "onCreateView()" )
        Log.i("myProfileData", "calling init()" )
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    private fun init() {
        Log.i("myProfileData", "init()" )
        userAlertClient = UserAlertClient(activity)
        Log.i("myProfileData", "userAlertClient object created" )
        viewModel = ViewModelProviders.of(requireActivity()).get(ProfileViewModel::class.java)
        Log.i("myProfileData", "view model created" )
        setActions()
        val sharedPrefsClient = SharedPrefsClient(context)
        val userDetails = sharedPrefsClient.getUserDetails()

/*
        binding.userName.text = viewModel.getUserDetails().cognitoUser.userName
        binding.accountStatus.text = viewModel.getUserDetails().cognitoUser.accountStatus
        binding.role.text = viewModel.getUserDetails().cognitoUser.role
        binding.client.text = viewModel.getUserDetails().cognitoUser.client
*/

/*        binding.userName.text = userDetails.user.userName
        binding.gender.text = userDetails.user.gender
        binding.phoneNumber.text = userDetails.communication.phoneNumber
        binding.email.text = userDetails.communication.email
        binding.address.text = userDetails.user.address
        binding.name.text = userDetails.user.name
        binding.role.text = userDetails.role.name*/

        val userData = viewModel.getProfileData()
        Log.i("myProfileData", "view model profile data " + Gson().toJson(userData))
        binding.userName.text = userData.userName
        binding.role.text = userData.userRole
        binding.client.text = userData.clientName
        binding.name.text = userDetails.user.name
        binding.phoneNumber.text = userData.phoneNumber
        binding.email.text = userData.email
        binding.address.text = userDetails.user.address
        binding.gender.text = userData.gender
        binding.accountStatus.text = userData.userStatus
        if(userData.userStatus.equals("CONFIRMED")) {
            binding.confirmedUserDetails.visibility = View.VISIBLE
        }
        binding.created.text = userData.createdOn
        binding.userStatus.text = userData.enabled



//        rahul karn
/*
        binding.organisation.text = viewModel.getUserDetails().cognitoUser.organisation
        binding.accessDefined.text = viewModel.getUserDetails().team.assignedBy +" | "+ getDiplayAssigmentType(viewModel.getUserDetails().team.assignmentType)
        binding.accessDefinedDate.text = getDisplayDate(viewModel.getUserDetails().team.assignedOn)
*/
/*        binding.created.text = toDateString_FromAwsDateFormat(viewModel.getUserDetails().cognitoUser.created)
        if(viewModel.getUserDetails().cognitoUser.accountStatus.compareTo("CONFIRMED")==0){
            binding.confirmedUserDetails.visibility = View.VISIBLE
            binding.name.text = viewModel.getUserDetails().cognitoUser.name
            binding.gender.text = viewModel.getUserDetails().cognitoUser.gender
            binding.address.text = viewModel.getUserDetails().cognitoUser.address
            binding.email.text = viewModel.getUserDetails().cognitoUser.email
            binding.phoneNumber.text = viewModel.getUserDetails().cognitoUser.phoneNumber
        }else{
            binding.confirmedUserDetails.visibility = View.GONE
        }*/
    }

    private fun setActions(){
        binding.disableUser.visibility = View.GONE
        binding.enableUser.visibility = View.GONE
        binding.deleteUser.visibility = View.GONE
        binding.userStatusSpace.visibility = View.GONE
        binding.userStatusContainer.visibility = View.GONE
        binding.userStatus.text = ""
    }

}
