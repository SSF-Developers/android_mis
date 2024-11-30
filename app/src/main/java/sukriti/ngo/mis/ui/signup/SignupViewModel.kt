package sukriti.ngo.mis.ui.signup

import android.app.Application
import android.content.Context
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import sukriti.ngo.mis.ui.login.data.*
import sukriti.ngo.mis.utils.AuthenticationClient
import sukriti.ngo.mis.utils.SharedPrefsClient
import kotlin.collections.HashMap


class SignupViewModel(application: Application) : AndroidViewModel(application) {

    var userProfile = UserProfile()
    private var context: Context = application.applicationContext
    private var sharedPrefsClient: SharedPrefsClient
    private val _tag = "_ProfileViewModel"

    //LIVE DATA
    private val _errorStateUserDetails =
        MutableLiveData<HashMap<ValidationError.Companion.FieldNames, ValidationError>>()
    val errorStateUserDetails: LiveData<HashMap<ValidationError.Companion.FieldNames, ValidationError>> =
        _errorStateUserDetails
    private val _errorStateUserCommunication =
        MutableLiveData<HashMap<ValidationError.Companion.FieldNames, ValidationError>>()
    val errorStateUserCommunication: LiveData<HashMap<ValidationError.Companion.FieldNames, ValidationError>> =
        _errorStateUserCommunication


    init {
        AuthenticationClient.init(context)
        sharedPrefsClient = SharedPrefsClient(context)
    }


    fun setUserProfile() {
        _errorStateUserDetails.value =
            HashMap<ValidationError.Companion.FieldNames, ValidationError>()
        _errorStateUserCommunication.value =
            HashMap<ValidationError.Companion.FieldNames, ValidationError>()

        for (item in AuthenticationClient.getUserAttributesForFirstLogInCheck()) {
            when (item.labelText) {
                "name" -> {
                    userProfile.user.name = item.dataText
                }
                "gender" -> {
                    userProfile.user.gender = item.dataText
                }
                "address" -> {
                    userProfile.user.address = item.dataText
                }
                "phone_number" -> {
                    userProfile.communication.phoneNumber = item.dataText
                }
                "email" -> {
                    userProfile.communication.email = item.dataText
                }
                "custom:custom:Organization" -> {
                    userProfile.organisation.name = item.dataText
                }
                "custom:ClientName" -> {
                    userProfile.organisation.client = item.dataText
                }
                "custom:Role" -> {
                    userProfile.role = UserProfile.getRole(item.dataText)
                }
            }
        }
    }

    fun validateUserConfigurationForm(): Boolean {
        var errorListUserDetails = HashMap<ValidationError.Companion.FieldNames, ValidationError>()
        if (userProfile.user.name.isEmpty()) {
            errorListUserDetails[ValidationError.Companion.FieldNames.Name] =
                ValidationError("Name Required. Please enter your name.")
        }
        if (userProfile.user.gender.isEmpty()) {
            errorListUserDetails[ValidationError.Companion.FieldNames.Gender] =
                ValidationError("Gender Required. Please select your gender")
        }
        if (userProfile.user.password.isEmpty()) {
            errorListUserDetails[ValidationError.Companion.FieldNames.Password] =
                ValidationError("Password Required. Please enter a password for your account.")
        } else if (userProfile.user.password.length < 6) {
            errorListUserDetails[ValidationError.Companion.FieldNames.Password] =
                ValidationError("Password Invalid. Please enter a password with minimum 6 characters.")
        }
        if (userProfile.user.repeatPassword.isEmpty()) {
            errorListUserDetails[ValidationError.Companion.FieldNames.RepeatPassword] =
                ValidationError("Repeat Password. Please enter the same password again.")
        } else if (userProfile.user.password.compareTo(userProfile.user.repeatPassword) != 0) {
            errorListUserDetails[ValidationError.Companion.FieldNames.RepeatPassword] =
                ValidationError("Password Mismatch. Please enter the same password again.")
        }
        if (userProfile.user.address.isEmpty()) {
            errorListUserDetails[ValidationError.Companion.FieldNames.Address] =
                ValidationError("Address Required. Please enter your address.")
        }

        var errorListUserCommunication =
            HashMap<ValidationError.Companion.FieldNames, ValidationError>()
        if (userProfile.communication.phoneNumber.isEmpty()) {
            errorListUserCommunication[ValidationError.Companion.FieldNames.PhoneNumber] =
                ValidationError("Phone Number Required. Please enter your phone number.")
        } else if (userProfile.communication.phoneNumber.length != 10) {
            errorListUserCommunication[ValidationError.Companion.FieldNames.PhoneNumber] =
                ValidationError("Invalid Format. Please enter your 10 digit phone number")
        }else{
            userProfile.communication.phoneNumber = "+91"+userProfile.communication.phoneNumber
        }
//        else if(!userProfile.communication.isPhoneNumberVerified) {
//            errorListUserCommunication[ValidationError.Companion.FieldNames.PhoneNumber] = ValidationError("Phone Number Not Verified. Please tap on 'Verify' button to initiate verification.")
//        }
        if (userProfile.communication.email.isEmpty()) {
            errorListUserCommunication[ValidationError.Companion.FieldNames.Email] =
                ValidationError("Email Required. Please select your email address")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userProfile.communication.email).matches()) {
            errorListUserCommunication[ValidationError.Companion.FieldNames.Email] =
                ValidationError("Invalid Format. Please enter a valid email.")
        }
//        else if(!userProfile.communication.isEmailVerified) {
//            errorListUserCommunication[ValidationError.Companion.FieldNames.Email] = ValidationError("Email Not Verified. Please tap on 'Verify' button to initiate verification.")
//        }

        return if (errorListUserCommunication.isEmpty() && errorListUserDetails.isEmpty()) {
            AuthenticationClient.setPasswordForFirstTimeLogin(userProfile.user.password)
            AuthenticationClient.setUserAttributeForFirstTimeLogin("name", userProfile.user.name)
            AuthenticationClient.setUserAttributeForFirstTimeLogin(
                "gender",
                userProfile.user.gender
            )
            AuthenticationClient.setUserAttributeForFirstTimeLogin(
                "address",
                userProfile.user.address
            )
            AuthenticationClient.setUserAttributeForFirstTimeLogin(
                "email",
                userProfile.communication.email
            )
            AuthenticationClient.setUserAttributeForFirstTimeLogin(
                "phone_number",
                userProfile.communication.phoneNumber
            )
            true
        } else {
            _errorStateUserDetails.value = errorListUserDetails
            _errorStateUserCommunication.value = errorListUserCommunication
            false
        }
    }

}
