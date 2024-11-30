package sukriti.ngo.mis.ui.management.fargments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.google.gson.JsonObject
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.DeviceUpdateBinding
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.SubmitCallback
import sukriti.ngo.mis.ui.management.ManagementViewModel

class UpdateDeviceDialog(
    val ctx: Context?,
    val viewModel: ManagementViewModel,
    val callback: SubmitCallback
): DialogFragment() {

    lateinit var binding: DeviceUpdateBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DeviceUpdateBinding.inflate(inflater)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogFragmentStyle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeDialog.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        val deviceState = resources.getStringArray(R.array.deviceState)
        val deviceStateAdapter : ArrayAdapter<String>? =
            ctx?.let { ArrayAdapter(it, R.layout.dropdown, deviceState) }
        binding.deviceStateValue.setAdapter(deviceStateAdapter)

        val policiesList = mutableListOf<String>()
        if(viewModel.policyListItems.isNotEmpty()) {
            for(i in 0 until viewModel.policyListItems.size) {
                val name = viewModel.policyListItems[i].name.split("/")
                policiesList.add(name[name.size-1])
            }
        }

        val policyAdapter : ArrayAdapter<String>? = ctx?.let {
            ArrayAdapter(it, R.layout.dropdown, policiesList)
        }

        binding.policyNameValue.setAdapter(policyAdapter)

        binding.submitDeviceUpdate.setOnClickListener {
            createPayload()
        }

    }

    private fun createPayload(){
        val payload = JsonObject()

        val disabledReason = JsonObject()
        val localizedMessages = JsonObject()
        val state = binding.deviceStateValue.text.toString()
        localizedMessages.addProperty("key", binding.disabledReasonValue.text.toString())
        disabledReason.add("localizedMessages", localizedMessages)
        disabledReason.addProperty("defaultMessage", "Disabled by admin")

        payload.add("disabledReason", disabledReason)
        payload.addProperty("state", state)
        payload.addProperty("policyName", binding.policyNameValue.text.toString())
        val tag = "patchDevice"
        Log.i(tag, "createPayload: $payload" )
        callback.onSubmit(payload)
        dismiss()

    }

}