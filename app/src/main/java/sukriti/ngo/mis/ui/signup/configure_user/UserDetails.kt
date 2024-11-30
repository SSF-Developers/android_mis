package sukriti.ngo.mis.ui.signup.configure_user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import sukriti.ngo.mis.databinding.ConfigureUserUserDetailsBinding
import sukriti.ngo.mis.interfaces.SimpleSelectionHandler
import sukriti.ngo.mis.ui.login.data.ValidationError
import sukriti.ngo.mis.ui.signup.SignupViewModel
import sukriti.ngo.mis.utils.*

class UserDetails : Fragment() {
    private var _TAG = "_UserDetails"
    private lateinit var viewModel: SignupViewModel
    private lateinit var binding: ConfigureUserUserDetailsBinding
    private lateinit var userAlertClient: UserAlertClient

    companion object {
        private var INSTANCE: UserDetails? = null

        fun getInstance(): UserDetails {
            return INSTANCE
                ?: UserDetails()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ConfigureUserUserDetailsBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.name.setText(viewModel.userProfile.user.name)
        binding.gender.text = viewModel.userProfile.user.gender
        binding.password.setText(viewModel.userProfile.user.password)
        binding.repeatPassword.setText(viewModel.userProfile.user.repeatPassword)
        binding.address.setText(viewModel.userProfile.user.address)
    }

    override fun onPause() {
        super.onPause()
        saveForm()
    }

    private fun init() {
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireParentFragment()).get(SignupViewModel::class.java)

        binding.gender.setOnClickListener {
            userAlertClient.showSingleSelectionDialog(
                "Gender",
                binding.gender.text.toString(),
                arrayOf("Male", "Female"),
                SimpleSelectionHandler {
                    binding.gender.text = it
                })
        }

        viewModel.errorStateUserDetails.observe(viewLifecycleOwner, Observer {
            var errorList = it
            setValidationErrors(errorList)
        })
    }

    private fun setValidationErrors(errorList: HashMap<ValidationError.Companion.FieldNames, ValidationError>) {
        binding.nameErr.text = ""
        binding.genderErr.text = ""
        binding.passwordErr.text = ""
        binding.repeatPasswordErr.text = ""
        binding.addressErr.text = ""

        if (errorList[ValidationError.Companion.FieldNames.Name] != null)
            binding.nameErr.text =
                errorList[ValidationError.Companion.FieldNames.Name]?.errorMessage
        if (errorList[ValidationError.Companion.FieldNames.Gender] != null)
            binding.genderErr.text =
                errorList[ValidationError.Companion.FieldNames.Gender]?.errorMessage
        if (errorList[ValidationError.Companion.FieldNames.Password] != null)
            binding.passwordErr.text =
                errorList[ValidationError.Companion.FieldNames.Password]?.errorMessage
        if (errorList[ValidationError.Companion.FieldNames.RepeatPassword] != null)
        {
            binding.repeatPasswordErr.text =
                errorList[ValidationError.Companion.FieldNames.RepeatPassword]?.errorMessage
            binding.repeatPassword.setText("")
        }
        if (errorList[ValidationError.Companion.FieldNames.Address] != null)
            binding.addressErr.text =
                errorList[ValidationError.Companion.FieldNames.Address]?.errorMessage
    }

    fun saveForm() {
        viewModel.userProfile.user.name = binding.name.text.toString()
        viewModel.userProfile.user.gender = binding.gender.text.toString()
        viewModel.userProfile.user.password = binding.password.text.toString()
        viewModel.userProfile.user.repeatPassword = binding.repeatPassword.text.toString()
        viewModel.userProfile.user.address = binding.address.text.toString()

        Log.i(_TAG, "saveForm " + viewModel.userProfile.user)
    }
}
