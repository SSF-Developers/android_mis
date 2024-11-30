package sukriti.ngo.mis.ui.management.EnrollDevice.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.KioskCustomizationDialogBinding
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.KioskCustomization
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.SubmitKioskCustomization

class KioskCustomizationDialog(
    val ctx: Context,
    val callback: SubmitKioskCustomization
) : DialogFragment() {
    lateinit var binding: KioskCustomizationDialogBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = KioskCustomizationDialogBinding.inflate(layoutInflater)

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
    }

    override fun onResume() {
        super.onResume()
        val powerButtonAction = resources.getStringArray(R.array.powerButtonAction)
        val systemErrorWarnings = resources.getStringArray(R.array.SystemErrorWarnings)
        val systemNavigation = resources.getStringArray(R.array.SystemNavigation)
        val statusBar = resources.getStringArray(R.array.StatusBar)
        val deviceSettings = resources.getStringArray(R.array.DeviceSettings)

        val powerButtonAdapter: ArrayAdapter<String> =
            ArrayAdapter(ctx, R.layout.dropdown, powerButtonAction)
        val systemErrorWarningsAdapter: ArrayAdapter<String> =
            ArrayAdapter(ctx, R.layout.dropdown, systemErrorWarnings)
        val systemNavigationAdapter: ArrayAdapter<String> =
            ArrayAdapter(ctx, R.layout.dropdown, systemNavigation)
        val statusBarAdapter: ArrayAdapter<String> = ArrayAdapter(ctx, R.layout.dropdown, statusBar)
        val deviceSettingsAdapter: ArrayAdapter<String> =
            ArrayAdapter(ctx, R.layout.dropdown, deviceSettings)


        binding.powerButtonActionTv.setAdapter(powerButtonAdapter)
        binding.systemErrorWarningTv.setAdapter(systemErrorWarningsAdapter)
        binding.systemNavigationTv.setAdapter(systemNavigationAdapter)
        binding.statusBarTv.setAdapter(statusBarAdapter)
        binding.deviceSettingsLayoutTv.setAdapter(deviceSettingsAdapter)


        binding.submitButton.setOnClickListener {
            if(verifyDetails()) {
                val kioskCustomization = KioskCustomization()
                val powerButtonText = binding.powerButtonActionTv.text.toString()
                val systemErrorWarningText = binding.systemErrorWarningTv.text.toString()
                val systemNavigationText = binding.systemNavigationTv.text.toString()
                val statusBarText = binding.statusBarTv.text.toString()
                val deviceSettingsText = binding.deviceSettingsLayoutTv.text.toString()

                kioskCustomization.powerButtonActions = powerButtonText
                kioskCustomization.systemErrorWarnings = systemErrorWarningText
                kioskCustomization.systemNavigation = systemNavigationText
                kioskCustomization.statusBar = statusBarText
                kioskCustomization.deviceSettings = deviceSettingsText

                Log.i(
                    "kioskDialog",
                    "onResume: Kiosk Customization: " + Gson().toJson(kioskCustomization)
                )
                callback.submitKioskCustomization(kioskCustomization)
                dismiss()
            }
        }


    }

    private fun verifyDetails(): Boolean {
        var powerButton = true
        var systemError = true
        var systemNavigation = true
        var statusBar = true
        var deviceSettings = true

        if (binding.powerButtonActionTv.text.isEmpty()) {
            binding.powerButtonActionLayout.isErrorEnabled = true
            binding.powerButtonActionLayout.error = "Field required"
            powerButton = false
        } else {
            binding.powerButtonActionLayout.isErrorEnabled = false
            powerButton = true
        }

        if (binding.systemErrorWarningTv.text.isEmpty()) {
            binding.systemErrorWarningLayout.isErrorEnabled = true
            binding.systemErrorWarningLayout.error = "Field required"
            systemError = false
        } else {
            binding.systemErrorWarningLayout.isErrorEnabled = false
            systemError = true
        }

        if (binding.systemNavigationTv.text.isEmpty()) {
            binding.systemNavigationLayout.isErrorEnabled = true
            binding.systemNavigationLayout.error = "Field required"
            systemNavigation = false
        } else {
            binding.systemNavigationLayout.isErrorEnabled = false
            systemNavigation = true
        }

        if (binding.statusBarTv.text.isEmpty()) {
            binding.statusBarLayout.isErrorEnabled = true
            binding.statusBarLayout.error = "Field required"
            statusBar = false
        } else {
            binding.statusBarLayout.isErrorEnabled = false
            statusBar = true
        }

        if (binding.deviceSettingsLayoutTv.text.isEmpty()) {
            binding.deviceSettingsLayout.isErrorEnabled = true
            binding.deviceSettingsLayout.error = "Field required"
            deviceSettings = false
        } else {
            binding.deviceSettingsLayout.isErrorEnabled = false
            deviceSettings = true
        }


        return powerButton && systemError && systemNavigation && statusBar && deviceSettings
    }

}