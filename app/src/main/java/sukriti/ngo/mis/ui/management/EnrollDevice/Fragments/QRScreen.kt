package sukriti.ngo.mis.ui.management.EnrollDevice.Fragments

import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.email_input_layout.emailEditText
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.QrScreenBinding
import sukriti.ngo.mis.ui.management.EnrollDevice.Navigation.ViewPagerControl
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel
import sukriti.ngo.mis.ui.management.lambda.QrShare.QrShareLambdaResponseHandler
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.UserAlertClient

class QRScreen(
    val viewModel: EnrollDeviceViewModel
) : Fragment() {
    private lateinit var binding: QrScreenBinding
    var interactionListener: ViewPagerControl? = null
    private lateinit var userAlertClient: UserAlertClient
    lateinit var lambdaClient: LambdaClient
    var qrLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("QrScreen", "onCreate() QrScreen")

        Log.i("debugging", "onCreate: QrScreen QrScreen")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = QrScreenBinding.inflate(layoutInflater)
        Log.i("QrScreen", "onCreateView() QrScreen")
        Log.i("debugging", "onCreateView() QrScreen")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userAlertClient = UserAlertClient(activity)
        lambdaClient = LambdaClient(requireActivity())
        Log.i("QrScreen", "onViewCreated() QrScreen")
        Log.i("debugging", "onViewCreated() QrScreen")
        binding.back.setOnClickListener {
            interactionListener?.goToPrevPage()
        }


        binding.shareQr.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.toggle_policy, binding.root, false)
            val emailEditText = dialogView.findViewById<TextInputEditText>(R.id.emailInputEditText)
            val emailLayout = dialogView.findViewById<TextInputLayout>(R.id.emailInputLayout)
            val cancelButton = dialogView.findViewById<MaterialButton>(R.id.cancelEmailDialog)
            val submitButton = dialogView.findViewById<MaterialButton>(R.id.submitEmailButton)
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .create()

            dialog.show()


            submitButton.setOnClickListener {
                if (validateEmailAddress(emailEditText.text.toString())) {
                    emailLayout.isErrorEnabled = false
                    Toast.makeText(
                        requireContext(),
                        emailEditText.text.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    shareQr(emailEditText.text.toString())
                    dialog.dismiss()
                } else {
                    emailLayout.error = "Invalid Email id"
                    emailLayout.isErrorEnabled = true
                }
            }

            cancelButton.setOnClickListener {
                dialog.dismiss()
            }


        }


    }

    override fun onStart() {
        super.onStart()
        Log.i("QrScreen", "onStart() QrScreen")
        Log.i("debugging", "onStart() QrScreen")
    }


    override fun onResume() {
        super.onResume()
        Log.i("QrScreen", "onResume() QrScreen")
        Log.i("QrScreen", "URL: ${viewModel.qrUrl}")
        Log.i("debugging", "onResume() QrScreen")


        val timer = object : CountDownTimer(5000, 5000) {
            override fun onTick(p0: Long) {
                userAlertClient.showWaitDialog("Loading QR")
            }

            override fun onFinish() {
                userAlertClient.closeWaitDialog()
                context?.let {
                    Glide.with(it)
                        .load(viewModel.qrUrl)
                        .error(R.drawable.logo_smile)
                        .into(binding.qrContainer)
                }
                qrLoaded = true
            }
        }

        if (viewModel.qrUrl.isNotEmpty() && !qrLoaded)
            timer.start()

    }

    override fun onPause() {
        super.onPause()
        Log.i("QrScreen", "onPause() QrScreen")
        Log.i("debugging", "onPause() QrScreen")
    }

    override fun onStop() {
        super.onStop()
        Log.i("QrScreen", "onStop() QrScreen")
        Log.i("debugging", "onStop() QrScreen")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        Log.i("QrScreen", "onDestroyView() QrScreen ")
        Log.i("debugging", "onDestroyView() QrScreen")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("QrScreen", "onDestroy() QrScreen")
        Log.i("debugging", "onDestroy() QrScreen")
    }

    private fun validateEmailAddress(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun shareQr(email: String) {
        userAlertClient.showWaitDialog("Sharing Qr")
        val request = JsonObject()
        request.addProperty("email", email)
        request.addProperty("qr", viewModel.qrUrl)
        request.addProperty("serial_number", viewModel.deviceSerialNumber)

        viewModel.shareQr(request, callback)
    }

    val callback = object : QrShareLambdaResponseHandler {
        override fun onSuccess(response: JsonObject) {
            userAlertClient.closeWaitDialog()
            val statusCode = response.get("statusCode").asInt
            if (statusCode == 200) {
                val message = response.get("body").asString
                userAlertClient.showDialogMessage("Qr Shared", message, false)
            } else {
                val message = response.get("body").asString
                userAlertClient.showDialogMessage("Error", message, false)
            }
        }

        override fun onError(message: String) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Something went wrong", message, false)
        }
    }

}