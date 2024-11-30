package sukriti.ngo.mis.ui.login

import android.app.Application
import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.*
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler
import com.google.gson.Gson
import sukriti.ngo.mis.dataModel.ExecutionState
import sukriti.ngo.mis.interfaces.LoginLambdaResultHandler
import sukriti.ngo.mis.ui.login.data.*
import sukriti.ngo.mis.utils.AuthenticationClient
import sukriti.ngo.mis.utils.AuthenticationClient.getPool
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.SharedPrefsClient
import java.util.*
import kotlin.collections.HashMap


class LoginViewModel(application: Application) : AndroidViewModel(application) {


    var userProfile = UserProfile()
    private var context: Context = application.applicationContext
    private var lambdaClient: LambdaClient
    private var sharedPrefsClient: SharedPrefsClient
    private val _TAG = "_LoginViewModel"
    private lateinit var newPasswordContinuation: NewPasswordContinuation

    //LIVE DATA
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm
    private val _LoginExecutionState = MutableLiveData<ExecutionState>()
    val loginExecutionState: LiveData<ExecutionState> = _LoginExecutionState
    fun clearLoginExecutionState() {
        _LoginExecutionState.value = ExecutionState()
    }

    private val _loginState = MutableLiveData<LoginResult>()
    val loginState: LiveData<LoginResult> = _loginState
    fun setLoginResult(loginResult: LoginResult) {
        _loginState.value = loginResult
    }

    fun clearLoginResult() {
        _loginState.value = LoginResult()
    }

    private val _errorStateUserDetails = MutableLiveData<HashMap<ValidationError.Companion.FieldNames, ValidationError>>()
    val errorStateUserDetails: LiveData<HashMap<ValidationError.Companion.FieldNames, ValidationError>> = _errorStateUserDetails
    private val _errorStateUserCommunication = MutableLiveData<HashMap<ValidationError.Companion.FieldNames, ValidationError>>()
    val errorStateUserCommunication: LiveData<HashMap<ValidationError.Companion.FieldNames, ValidationError>> = _errorStateUserCommunication


    companion object {
        const val NAV_ACTION_LOGIN = 0
        const val NAV_ACTION_NEW_USER = 1
        const val NAV_ACTION_FORGOT_PASSWORD = 2
    }

    init {
        AuthenticationClient.init(context)
        lambdaClient = LambdaClient(context)
        sharedPrefsClient = SharedPrefsClient(context)
    }


    //Login
    fun findCurrent() {
        val user: CognitoUser = getPool().getCurrentUser()
        val username = user.userId
        if (username != null) {
            AuthenticationClient.setUser(username)
            //inUsername.setText(user.userId)
            Log.i("_loginFlow","findCurrent()")
            user.getSessionInBackground(authenticationHandler)
        }
    }

    fun validateLoginForm(username: String, password: String) {
        Log.d("_loginFlow", "validateLoginForm() called with: username = $username, password = $password")
        val state = LoginFormState()
        state.userName = username
        state.password = password
        state.isValidated = true
        if (username.isEmpty()) {
            state.isUserNameValid = false
            state.usernameError = "Please enter username"
            state.isValidated = false
        }
        else if (password.isEmpty()) {
            state.isPasswordValid = false
            state.passwordError = "Please enter your password"
            state.isValidated = false
        }
        _loginForm.value = state

        if (state.isValidated) {
            Log.i("_loginFlow", "State is validated")
            authenticate()

        }
    }

    private fun authenticate() {
        Log.d("_loginFlow", "authenticate() called")
        val loginState = _loginForm.value ?: return
        val cognitoPool = getPool()
        val cognitoUser = cognitoPool.getUser(loginState.userName)

        Log.i("_loginFlow","authenticate(): Pool ID "+cognitoPool.userPoolId)
        Log.i("_loginFlow","authenticate(): User ID "+cognitoUser.userId)
        cognitoUser.getSessionInBackground(authenticationHandler)
    }

    private var authenticationHandler: AuthenticationHandler = object : AuthenticationHandler {

        override fun onSuccess(cognitoUserSession: CognitoUserSession, device: CognitoDevice?) {
            Log.i("_loginFlow","onSuccess()")
            val loginState = _loginForm.value ?: return

            AuthenticationClient.setUser(loginState.userName)
            Log.i("_loginFlow","Going to set current session")
            Log.i("_loginFlow","Current Session valid? ${cognitoUserSession.isValid}" )

            AuthenticationClient.setCurrSession(cognitoUserSession)
            AuthenticationClient.newDevice(device)
            _loginState.value = LoginResult(LoginResult.STATUS_SUCCESS, "Authenticated successfully")
            Log.i("adminRole!", "1")
            getUserDetails()
        }

        override fun onFailure(e: Exception) {
            Log.i("_loginFlow","onFailure()")
            _loginState.value = LoginResult(
                LoginResult.STATUS_FAILURE,
                "Authentication failed! " + AuthenticationClient.formatException(e)
            )
        }

        override fun getAuthenticationDetails(authenticationContinuation: AuthenticationContinuation, username: String) {
            val loginState = _loginForm.value ?: return

            Log.i("_loginFlow","getAuthenticationDetails()" +loginState.userName +" "
                + loginState.password)

            Locale.setDefault(Locale.US)
            AuthenticationClient.setUser(loginState.userName)
            authenticationContinuation.setAuthenticationDetails(
                AuthenticationDetails(
                    loginState.userName,
                    loginState.password,
                    null
                )
            )
            authenticationContinuation.continueTask()

        }

        override fun authenticationChallenge(continuation: ChallengeContinuation) {
            Log.i("_loginFlow","authenticationChallenge()")
            if ("NEW_PASSWORD_REQUIRED" == continuation.challengeName) {
                newPasswordContinuation = continuation as NewPasswordContinuation
                AuthenticationClient.setUserAttributeForDisplayFirstLogIn(
                    newPasswordContinuation.currentUserAttributes,
                    newPasswordContinuation.requiredAttributes
                )

                _loginState.value = LoginResult(
                    LoginResult.STATUS_NEW_PASSWORD_REQUIRED,
                    "Authentication Successful! Set New Password."
                )
            }
        }

        override fun getMFACode(multiFactorAuthenticationContinuation: MultiFactorAuthenticationContinuation) {
            Log.i("_loginFlow","getMFACode()")
        }
    }

    private var handler: LoginLambdaResultHandler = object : LoginLambdaResultHandler {

        override fun onSuccess(result: LoginLambdaResult?) {
            _LoginExecutionState.value =  ExecutionState(ExecutionState.STATUS_SUCCESS,"")
        }

        override fun onError(ErrorMsg: String?) {
            _LoginExecutionState.value =  ExecutionState(ExecutionState.STATUS_FAILURE,ErrorMsg)
        }
    }

    //User Details
    fun getUserDetails(){
        getPool().getUser(AuthenticationClient.getCurrUser()).getDetailsInBackground(detailsHandler)
    }

    private var detailsHandler: GetDetailsHandler = object: GetDetailsHandler {
        override fun onSuccess(cognitoUserDetails: CognitoUserDetails?) {
            Log.i("_loginFlow", "detailsHandler")
            val map = cognitoUserDetails?.attributes?.attributes
            Log.i("_loginFlow", Gson().toJson(map))
            val userProfile = UserProfile()
            userProfile.organisation.name = map?.get("custom:custom:Organization") ?: ""
//            Log.i("userProfile", userProfile.organisation.name)
            userProfile.organisation.client = map?.get("custom:ClientName") ?: ""
            userProfile.user.userName = AuthenticationClient.getCurrUser()
            userProfile.user.name = map?.get("name") ?: ""
            userProfile.user.gender = map?.get("gender") ?: ""
            userProfile.user.address = map?.get("address") ?: ""
            userProfile.communication.email = map?.get("email") ?: ""
            userProfile.communication.phoneNumber = map?.get("phone_number") ?: ""
            

            val roleName = map?.get("custom:Role") ?: ""
            Log.i("_loginFlow", "roleName "+userProfile.user.name)
            userProfile.role = UserProfile.getRole(roleName)
            isNewLogin(userProfile);
            sharedPrefsClient.saveUserDetails(userProfile)
            Log.i("_loginFlow", "Data saved in shared pref")
            _LoginExecutionState.value =  ExecutionState(ExecutionState.STATUS_SUCCESS,"")
//            lambdaClient.ExecuteLoginLambda(handler, sharedPrefsClient.getUserDetails())
        }

        override fun onFailure(exception: java.lang.Exception?) {
            Log.i("adminRole!", "3")
            Log.i(_TAG,"onFailure: ${exception?.message}")
            _LoginExecutionState.value =  ExecutionState(ExecutionState.STATUS_FAILURE,exception?.message)
        }
    }

    private fun isNewLogin(mUserProfile:UserProfile) {
        val lastUserName = sharedPrefsClient.getUserDetails().user.userName
        if(lastUserName != mUserProfile.user.userName) {
            val status = sharedPrefsClient.getDbSyncStatus()
            status.isNewLogin = true
            sharedPrefsClient.saveDbSyncStatus(status)
        }else{
            val status = sharedPrefsClient.getDbSyncStatus()
            status.isNewLogin = false
            sharedPrefsClient.saveDbSyncStatus(status)
        }
    }

    //Configure User
    fun continueWithFirstTimeSignIn() {
        newPasswordContinuation.setPassword(AuthenticationClient.getPasswordForFirstTimeLogin())
        val newAttributes =
            AuthenticationClient.getUserAttributesForFirstTimeLogin()
        if (newAttributes != null) {
            for ((key, value) in newAttributes) {
                newPasswordContinuation.setUserAttribute(key, value)
            }
        }
        try {
            newPasswordContinuation.continueTask()
        } catch (e: Exception) {
            _loginState.value = LoginResult(
                LoginResult.STATUS_FAILURE,
                "Sign-in failed. " + AuthenticationClient.formatException(e)
            )
        }
    }

    fun setUserProfile() {
        _errorStateUserDetails.value =
            HashMap<ValidationError.Companion.FieldNames, ValidationError>()
        _errorStateUserCommunication.value =
            HashMap<ValidationError.Companion.FieldNames, ValidationError>()

        for (item in AuthenticationClient.getUserAttributesForFirstLogInCheck()) {
            Log.i(_TAG, item.toString())
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
        val errorListUserDetails = HashMap<ValidationError.Companion.FieldNames, ValidationError>()
        
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
                ValidationError("Password Mismatch. Please enter the same password again. " + userProfile.user.password + " != " + userProfile.user.repeatPassword)
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
        } else if (userProfile.communication.phoneNumber.length != 13) {
            errorListUserCommunication[ValidationError.Companion.FieldNames.PhoneNumber] =
                ValidationError("Invalid Format. Please enter your 10 digit phone number with country code, +91...")
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
            Log.i(_TAG, "errorListUserDetails: " + errorListUserDetails.size)
            Log.i(_TAG, "errorListUserCommunication: " + errorListUserCommunication.size)

            _errorStateUserDetails.value = errorListUserDetails
            _errorStateUserCommunication.value = errorListUserCommunication
            false
        }
    }

    //Forgot Password
    fun getResetPasswordCode(userName: String,forgotPasswordHandler: ForgotPasswordHandler) {
        if (userName == null || userName.isEmpty()) {
            val state = LoginFormState()
            state.userName = userName
            state.isValidated = true

            state.isUserNameValid = false
            state.usernameError = "Please enter username"
            state.isValidated = false

            _loginForm.value = state
            return
        }
        Log.i(_TAG,""+userName)
        getPool().getUser(userName).forgotPasswordInBackground(forgotPasswordHandler)
    }
}
