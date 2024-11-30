package sukriti.ngo.mis.ui.management.fargments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.gson.JsonObject
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.UpdateEnterpriseBinding
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.SubmitCallback

class UpdateEnterpriseDialog(
    val callback: SubmitCallback
) : DialogFragment() {

    lateinit var binding: UpdateEnterpriseBinding

    companion object {
        private const val TAG = "UpdateEnterpriseDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UpdateEnterpriseBinding.inflate(inflater)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isTabletDevice(requireContext())) {
            Log.i(TAG, "onCreate: tablet device")
            setStyle(STYLE_NO_TITLE, R.style.DialogFragmentStyle)
        } else {
            Log.i(TAG, "onCreate: not tablet device")
            setStyle(STYLE_NO_TITLE, R.style.DialogFragmentStyle2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.close.setOnClickListener {
            dismiss()
        }

        binding.submitEnterpriseUpdate.setOnClickListener {
            createPayload()
        }
    }

    private fun createPayload() {
        val payload = JsonObject()

        if (binding.displayNameValue.text.toString().isNotEmpty()) {
            payload.addProperty("enterpriseDisplayName", binding.displayNameValue.text.toString())
        }

        val contactInfo = JsonObject()

        if (binding.valueDataProtectionOfficerName.text.toString().isNotEmpty()) {
            contactInfo.addProperty(
                "dataProtectionOfficerName",
                binding.valueDataProtectionOfficerName.text.toString()
            )
        }

        if (binding.valueDataProtectionOfficerEmail.text.toString().isNotEmpty()) {
            contactInfo.addProperty(
                "dataProtectionOfficerEmail",
                binding.valueDataProtectionOfficerEmail.text.toString()
            )
        }

        if (binding.valueDataProtectionOfficerPhone.text.toString().isNotEmpty()) {
            contactInfo.addProperty(
                "dataProtectionOfficerPhone",
                binding.valueDataProtectionOfficerPhone.text.toString()
            )
        }

        if (binding.valueEuRepresentativeName.text.toString().isNotEmpty()) {
            contactInfo.addProperty(
                "euRepresentativeName",
                binding.valueEuRepresentativeName.text.toString()
            )
        }

        if (binding.valueEuRepresentativeEmail.text.toString().isNotEmpty()) {
            contactInfo.addProperty(
                "euRepresentativeEmail",
                binding.valueEuRepresentativeEmail.text.toString()
            )
        }

        if (binding.valueEuRepresentativePhone.text.toString().isNotEmpty()) {
            contactInfo.addProperty(
                "euRepresentativePhone",
                binding.valueEuRepresentativePhone.text.toString()
            )
        }

        if (binding.valueContactEmail.text.toString().isNotEmpty()) {
            contactInfo.addProperty("contactEmail", binding.valueContactEmail.text.toString())
        }

        if (contactInfo.size() > 0) {
            payload.add("contactInfo", contactInfo)
        }

        Log.i("updateEnterprise", "Dialog Payload: $payload")

        callback.onSubmit(payload)
        dismiss()
    }

    private fun isTabletDevice(context: Context): Boolean {
        val screenLayout = context.resources.configuration.screenLayout
        val screenSize = screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

        return (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
    }

}