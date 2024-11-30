package sukriti.ngo.mis.ui.signup.configure_user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import sukriti.ngo.mis.databinding.ConfigureUserOrganisationDetailsBinding
import sukriti.ngo.mis.ui.login.data.UserProfile
import sukriti.ngo.mis.ui.signup.SignupViewModel
import sukriti.ngo.mis.utils.*

class UserOrganisation : Fragment() {
    private lateinit var viewModel: SignupViewModel
    private lateinit var binding: ConfigureUserOrganisationDetailsBinding
    private lateinit var userAlertClient: UserAlertClient

    companion object {
        private var INSTANCE: UserOrganisation? = null

        fun getInstance(): UserOrganisation {
            return INSTANCE
                ?: UserOrganisation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = ConfigureUserOrganisationDetailsBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.organisation.text = viewModel.userProfile.organisation.name
        binding.client.text = viewModel.userProfile.organisation.client
        binding.role.text = UserProfile.getRoleName(viewModel.userProfile.role)
    }

    private fun init() {
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireParentFragment()).get(SignupViewModel::class.java)

    }

}
