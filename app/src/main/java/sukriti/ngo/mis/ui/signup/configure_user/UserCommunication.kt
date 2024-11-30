package sukriti.ngo.mis.ui.signup.configure_user

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import sukriti.ngo.mis.databinding.ConfigureUserCommunicationBinding
import sukriti.ngo.mis.ui.login.activities.VerifyCommunicationDetails
import sukriti.ngo.mis.ui.login.data.ValidationError
import sukriti.ngo.mis.ui.signup.SignupViewModel
import sukriti.ngo.mis.utils.UserAlertClient

class UserCommunication : Fragment() {
    private lateinit var viewModel: SignupViewModel
    private lateinit var binding: ConfigureUserCommunicationBinding
    private lateinit var userAlertClient: UserAlertClient

    companion object {
        private var INSTANCE: UserCommunication? = null

        fun getInstance(): UserCommunication {
            return INSTANCE
                ?: UserCommunication()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ConfigureUserCommunicationBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.phoneNumber.setText(viewModel.userProfile.communication.phoneNumber)
        binding.email.setText(viewModel.userProfile.communication.email)
    }

    override fun onPause() {
        super.onPause()
        saveForm()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            var verified = data?.getBooleanExtra("verified", false);
            var type = data?.getIntExtra("type", -1);

            if (verified!!) {
                when (type) {
                    1 -> {
                        viewModel.userProfile.communication.isEmailVerified = true
                    }

                    2 -> {
                        viewModel.userProfile.communication.isPhoneNumberVerified = true
                    }
                }
            }
        }
    }

    fun saveForm() {
        viewModel.userProfile.communication.phoneNumber = binding.phoneNumber.text.toString()
        viewModel.userProfile.communication.email = binding.email.text.toString()
    }

    private fun init() {
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireParentFragment()).get(SignupViewModel::class.java)

        binding.verifyEmail.setOnClickListener() {
            if (validateEmail())
                startActivityForResult(
                    Intent(context, VerifyCommunicationDetails::class.java)
                        .putExtra("type", 1)
                        .putExtra("display", viewModel.userProfile.communication.email), 21
                )
        }
        binding.verifyPhoneNumber.setOnClickListener() {
            if (validatePhoneNumber())
                startActivityForResult(
                    Intent(context, VerifyCommunicationDetails::class.java)
                        .putExtra("type", 2)
                        .putExtra("display", viewModel.userProfile.communication.phoneNumber), 21
                )
        }

        viewModel.errorStateUserCommunication.observe(viewLifecycleOwner, Observer {
            var errorList = it
            setValidationErrors(errorList)
        })
    }

    private fun setValidationErrors(errorList: HashMap<ValidationError.Companion.FieldNames, ValidationError>) {
        binding.phoneErr.text = ""
        binding.emailErr.text = ""

        if (errorList[ValidationError.Companion.FieldNames.PhoneNumber] != null)
            binding.phoneErr.text =
                errorList[ValidationError.Companion.FieldNames.PhoneNumber]?.errorMessage
        if (errorList[ValidationError.Companion.FieldNames.Email] != null)
            binding.emailErr.text =
                errorList[ValidationError.Companion.FieldNames.Email]?.errorMessage
    }

    private fun validateEmail(): Boolean {
        var errorList = HashMap<ValidationError.Companion.FieldNames, ValidationError>()
        var email = binding.email.text.toString();
        viewModel.userProfile.communication.email = email
        if (email.isEmpty()) {
            errorList[ValidationError.Companion.FieldNames.Email] =
                ValidationError("Email Required. Please select your email address.")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorList[ValidationError.Companion.FieldNames.Email] =
                ValidationError("Invalid Format. Please enter a valid email..")
        }
        setValidationErrors(errorList)
        return errorList.isEmpty()
    }

    private fun validatePhoneNumber(): Boolean {
        var errorList = HashMap<ValidationError.Companion.FieldNames, ValidationError>()
        var phoneNumber = binding.phoneNumber.text.toString();
        viewModel.userProfile.communication.phoneNumber = phoneNumber
        if (phoneNumber.isEmpty()) {
            errorList[ValidationError.Companion.FieldNames.PhoneNumber] =
                ValidationError("Phone Number Required. Please enter your phone number.")
        } else if (phoneNumber.length != 10) {
            errorList[ValidationError.Companion.FieldNames.PhoneNumber] =
                ValidationError("Invalid Format. Please enter your 10 digit phone number.")
        }
        setValidationErrors(errorList)
        return errorList.isEmpty()
    }


}
