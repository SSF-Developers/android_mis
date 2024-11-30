package sukriti.ngo.mis.ui.management.EnrollDevice.Fragments

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.google.gson.JsonObject
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.NewApplicationLayoutBinding
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.EditAppClickCallback
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.SubmitApplication
import sukriti.ngo.mis.ui.management.ManagementViewModel
import sukriti.ngo.mis.ui.management.data.device.Application
import sukriti.ngo.mis.ui.management.lambda.VerifyPackageName.VerifyPackageNameResponseHandler
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.UserAlertClient

class AddNewApplicationDialog(
    val ctx: Context,
    val callback: SubmitApplication,
    val lambdaClient: LambdaClient,
    val managementViewModel: ManagementViewModel,
    val application: Application? = null,
    val index: Int = -1
) : DialogFragment() {

    lateinit var binding: NewApplicationLayoutBinding
    lateinit var userAlertClient: UserAlertClient
    var packageNameVerified = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NewApplicationLayoutBinding.inflate(layoutInflater)

        binding.packageNameLayout.endIconDrawable?.setVisible(false, false)
        userAlertClient = UserAlertClient(activity)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogFragmentStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.close.setOnClickListener {
            dismiss()
        }

        binding.packageNameLayout.setEndIconOnClickListener {
            verifyPackageName()
        }

        setTextWatcher()
    }

    override fun onResume() {
        super.onResume()

        if (application != null) {
            binding.packageNameEditText.setText(application.packageName)
            binding.installTypeTv.setText(application.installType)
            binding.autoUpdateModeLayoutTv.setText(application.autoUpdateMode)
            binding.userControlSettingsTv.setText(application.userControlSettings)
            binding.permissionPolicyTv.setText(application.defaultPermissionPolicy)
            packageNameVerified = true
        }

        val installType = resources.getStringArray(R.array.applicationInstallType)
        val permissions = resources.getStringArray(R.array.defaultPermissionPolicy)
        val autoUpdateMode = resources.getStringArray(R.array.autoUpdateMode)
        val userControl = resources.getStringArray(R.array.userControlSettings)


        val install: ArrayAdapter<String> = ArrayAdapter(ctx, R.layout.dropdown, installType)
        val permission: ArrayAdapter<String> = ArrayAdapter(ctx, R.layout.dropdown, permissions)
        val update: ArrayAdapter<String> = ArrayAdapter(ctx, R.layout.dropdown, autoUpdateMode)
        val control: ArrayAdapter<String> = ArrayAdapter(ctx, R.layout.dropdown, userControl)

        binding.autoUpdateModeLayoutTv.setAdapter(update)
        binding.installTypeTv.setAdapter(install)
        binding.permissionPolicyTv.setAdapter(permission)
        binding.userControlSettingsTv.setAdapter(control)

        binding.submitButton.setOnClickListener {
            if (verifyDetails()) {
                if (packageNameVerified) {
                    binding.packageNameLayout.isErrorEnabled = false
                    val newApp = Application()
                    newApp.autoUpdateMode = binding.autoUpdateModeLayoutTv.text.toString()
                    newApp.userControlSettings = binding.userControlSettingsTv.text.toString()
                    newApp.installType = binding.installTypeTv.text.toString()
                    newApp.packageName = binding.packageNameEditText.text.toString()
                    newApp.defaultPermissionPolicy = binding.permissionPolicyTv.text.toString()
                    if(application == null) {
                        Log.i("editApp", "adding new app as application obj is null")
                        callback.submitApp(newApp)
                    } else {
                        Log.i("editApp", "Editing app as application obj is not null and index is $index" )
                        if(index != -1) {
                            callback.editApp(newApp, index)
                        }
                    }

                    dismiss()
                } else {
                    binding.packageNameLayout.error = "Verify package name"
                    binding.packageNameLayout.isErrorEnabled = true

                }
            }
        }

    }

    private fun verifyDetails(): Boolean {
        val installType: Boolean
        val permission: Boolean
        val userControl: Boolean
        val autoUpdate: Boolean
        val packageName: Boolean

        if (binding.installTypeTv.text.toString().isEmpty()) {
            binding.installTypeLayout.isErrorEnabled = true
            binding.installTypeLayout.error = "Field required"
            installType = false
        } else {
            binding.installTypeLayout.isErrorEnabled = false
            installType = true
        }

        if (binding.autoUpdateModeLayoutTv.text.toString().isEmpty()) {
            binding.autoUpdateModeLayout.isErrorEnabled = true
            binding.autoUpdateModeLayout.error = "Field required"
            autoUpdate = false
        } else {
            binding.autoUpdateModeLayout.isErrorEnabled = false
            autoUpdate = true
        }
        if (binding.permissionPolicyTv.text.toString().isEmpty()) {
            binding.permissionPolicyLayout.isErrorEnabled = true
            binding.permissionPolicyLayout.error = "Field required"
            permission = false
        } else {
            binding.permissionPolicyLayout.isErrorEnabled = false
            permission = true
        }

        if (binding.userControlSettingsTv.text.toString().isEmpty()) {
            binding.userControlSettingsLayout.isErrorEnabled = true
            binding.userControlSettingsLayout.error = "Field required"
            userControl = false
        } else {
            binding.userControlSettingsLayout.isErrorEnabled = false
            userControl = true
        }

        if (binding.packageNameEditText.text.toString().isEmpty()) {
            binding.packageNameLayout.isErrorEnabled = true
            binding.packageNameLayout.error = "Field Required"
            packageName = false
        } else {
            binding.packageNameLayout.isErrorEnabled = false
            packageName = true
        }

        return installType && autoUpdate && permission && packageName && userControl
    }

    private fun verifyPackageName() {
        userAlertClient.showWaitDialog("Verifying package name")

        val request = JsonObject()
        request.addProperty("EnterpriseId", managementViewModel.getSelectedEnterprise().name)
        request.addProperty("packageName", binding.packageNameEditText.text.toString())

        val verifyPackageNameLambdaCallback = object : VerifyPackageNameResponseHandler {
            override fun onSuccess(response: JsonObject) {
                userAlertClient.closeWaitDialog()
                val statusCode = response.get("statusCode").asInt
                if (statusCode == 200) {
                    packageNameVerified = true
                    binding.packageNameLayout.endIconDrawable =
                        ResourcesCompat.getDrawable(resources, R.drawable.green_check, null)
                    binding.packageNameLayout.endIconDrawable?.setVisible(true, false)
                    binding.packageNameLayout.helperText = "Package name verified"
                    binding.packageNameLayout.isHelperTextEnabled = true
                } else {
                    packageNameVerified = false
                    binding.packageNameLayout.isHelperTextEnabled = false
                    binding.packageNameLayout.endIconDrawable =
                        ResourcesCompat.getDrawable(resources, R.drawable.ic_check_availability, null)
                    binding.packageNameLayout.endIconDrawable?.setVisible(true, false)
                    binding.packageNameLayout.error = "Invalid package name"
                    binding.packageNameLayout.isErrorEnabled = true
                }
            }

            override fun onError(message: String) {
                packageNameVerified = false
                userAlertClient.closeWaitDialog()
                userAlertClient.showDialogMessage(
                    "Error",
                    "Something went wrong. Please try again",
                    false
                )
            }
        }

        lambdaClient.ExecuteVerifyPackageNameLambda(request, verifyPackageNameLambdaCallback)
    }

    private fun setTextWatcher() {
        binding.packageNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.packageNameLayout.isErrorEnabled = false
                binding.packageNameLayout.isHelperTextEnabled = false
                binding.packageNameLayout.endIconDrawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_check_availability,null)
                binding.packageNameLayout.endIconDrawable?.setVisible(true, false)

                packageNameVerified = false
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }


}