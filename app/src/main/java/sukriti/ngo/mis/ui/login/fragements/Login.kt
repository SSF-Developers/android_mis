package sukriti.ngo.mis.ui.login.fragements

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler
import com.google.gson.Gson
import sukriti.ngo.mis.dataModel.ExecutionState
import sukriti.ngo.mis.databinding.LoginBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.login.LoginViewModel
import sukriti.ngo.mis.ui.login.activities.ForgotPassword
import sukriti.ngo.mis.ui.login.data.LoginResult
import sukriti.ngo.mis.ui.login.data.UserProfile
import sukriti.ngo.mis.ui.dashboard.HomeActivity
import sukriti.ngo.mis.utils.AuthenticationClient.formatException
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities

class Login : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: LoginBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var forgotPasswordContinuation: ForgotPasswordContinuation

    companion object {
        private var INSTANCE: Login? = null

        fun getInstance(): Login {
            return INSTANCE ?: Login()
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
        savedInstanceState: Bundle?
    ): View {
        binding = LoginBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Forgot password
        if (resultCode == RESULT_OK) {
            val newPass = data!!.getStringExtra("newPass")
            val code = data.getStringExtra("code")
            if (newPass != null && code != null) {
                if (!newPass.isEmpty() && !code.isEmpty()) {
                    userAlertClient.showWaitDialog("Setting new password...")
                    forgotPasswordContinuation.setPassword(newPass)
                    forgotPasswordContinuation.setVerificationCode(code)
                    forgotPasswordContinuation.continueTask()
                }
            }
        }
    }

    private fun init() {
        //Toast.makeText(context,"0001-BB",Toast.LENGTH_LONG).show()
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel::class.java)
        sharedPrefsClient = SharedPrefsClient(requireContext())

        //Auto Login
//        val user: CognitoUser = AuthenticationClient.getPool().currentUser
//        var username = user.userId
//        if (username != null) {
//            binding.username.setText(user.userId)
//            viewModel.findCurrent()
//        }

        binding.login.setOnClickListener {
            userAlertClient.showWaitDialog("Authenticating...")
            viewModel.validateLoginForm(
                binding.username.text.toString(),
                binding.password.text.toString()
            )
        }

        binding.forgotPassword.setOnClickListener {
            userAlertClient.showWaitDialog("Initiating request...")
            viewModel.getResetPasswordCode(binding.username.text.toString(), forgotPasswordHandler)
        }

        viewModel.loginFormState.observe(viewLifecycleOwner, Observer {
            val loginFormState = it ?: return@Observer

            Utilities.hideKeypad(context, binding.password)
            binding.username.setText(loginFormState.userName)
            binding.password.setText(loginFormState.password)

            if (!loginFormState.isUserNameValid)
                Utilities.setError(
                    binding.username,
                    binding.usernameErr,
                    loginFormState.usernameError
                )
            else
                Utilities.clearError(binding.username, binding.usernameErr)

            if (!loginFormState.isPasswordValid)
                Utilities.setError(
                    binding.password,
                    binding.passwordErr,
                    loginFormState.passwordError
                )
            else
                Utilities.clearError(binding.password, binding.passwordErr)

            if (!loginFormState.isValidated)
                userAlertClient.closeWaitDialog()
        })

        viewModel.loginState.observe(viewLifecycleOwner, Observer {
            val loginState = it ?: return@Observer

            when (loginState.status) {
                LoginResult.STATUS_SUCCESS -> {
                    //getUserDetails()
                }

                LoginResult.STATUS_FAILURE -> {
                    userAlertClient.closeWaitDialog()
                    Utilities.setError(binding.username, binding.usernameErr, loginState.message)
                    userAlertClient.showDialogMessage(
                        "Authentication Failed",
                        loginState.message,
                        false
                    )
                    viewModel.clearLoginResult()
                }

                LoginResult.STATUS_NEW_PASSWORD_REQUIRED -> {
                    userAlertClient.closeWaitDialog()
                    navigationHandler.navigateTo(LoginViewModel.NAV_ACTION_NEW_USER)
                    viewModel.clearLoginResult()
                }

                LoginResult.STATUS_AUTO_LOGIN -> {
                    userAlertClient.showWaitDialog("Authenticating...")
                    viewModel.continueWithFirstTimeSignIn()
                }
            }
        })

        viewModel.loginExecutionState.observe(viewLifecycleOwner, Observer {
            val loginExecutionState = it
            when (loginExecutionState.status) {
                ExecutionState.STATUS_SUCCESS -> {
                    userAlertClient.closeWaitDialog()

//                    var intent = Intent(context, SuperAdminHomeActivity::class.java)
//                    startActivity(intent)
//                    activity?.finish()
//                    viewModel.clearLoginExecutionState()

                    val userProfile = sharedPrefsClient.getUserDetails()
                    Log.i("_TAG","$userProfile")

                    val intent = Intent(context, HomeActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                    viewModel.clearLoginExecutionState()
                }

                ExecutionState.STATUS_FAILURE -> {
                    userAlertClient.closeWaitDialog()
                    userAlertClient.showDialogMessage(
                        "Error Alert",
                        loginExecutionState.message,
                        false
                    )
                    viewModel.clearLoginExecutionState()
                }
            }
        })
    }

    private var forgotPasswordHandler: ForgotPasswordHandler = object : ForgotPasswordHandler {
        override fun onSuccess() {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage(
                "Password Changed",
                "New password updated. Please login to continue.",
                false
            )
            binding.password.setText("")
            binding.password.requestFocus()
        }

        override fun getResetCode(forgotPasswordContinuation: ForgotPasswordContinuation) {
            userAlertClient.closeWaitDialog()
            getForgotPasswordCode(forgotPasswordContinuation)
        }

        override fun onFailure(e: java.lang.Exception) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage(
                "Error Alert",
                "Forgot password request failed. " + formatException(e),
                false
            )
        }
    }

    private fun getForgotPasswordCode(forgotPasswordContinuation: ForgotPasswordContinuation) {
        this.forgotPasswordContinuation = forgotPasswordContinuation
        val intent = Intent(context, ForgotPassword::class.java)
        intent.putExtra("destination", forgotPasswordContinuation.parameters.destination)
        intent.putExtra(
            "deliveryMed",
            forgotPasswordContinuation.parameters.deliveryMedium
        )
        startActivityForResult(intent, 10)
    }
}
