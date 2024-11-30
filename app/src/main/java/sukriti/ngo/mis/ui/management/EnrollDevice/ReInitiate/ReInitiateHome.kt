package sukriti.ngo.mis.ui.management.EnrollDevice.ReInitiate

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.gson.JsonObject
import sukriti.ngo.mis.databinding.ReinitiateHomeBinding
import sukriti.ngo.mis.ui.management.ManagementViewModel
import sukriti.ngo.mis.ui.management.data.device.Device
import sukriti.ngo.mis.utils.LambdaClient

class ReInitiateHome(
    val device: Device?,
    val managementViewModel: ManagementViewModel
) : DialogFragment() {
    lateinit var binding: ReinitiateHomeBinding
    lateinit var adapter: ReInitiateAdapter
    val viewModel: ReInitiateViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ReinitiateHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.reInitiateData.value?.selectedEnterprise = managementViewModel.getSelectedEnterprise().name
        viewModel.reInitiateData.value?.serialNumber = device?.serial_number!!
        viewModel.policyList = managementViewModel.policyListItems

        if(device.DEVICE_APPLICATION_STATE) {
            setApplicationDetails()
        }
        else if (device.DEVICE_POLICY_STATE) {
            viewModel.reInitiateData.value?.policy = device.policy_details?.policy_name!!
        }

        adapter = ReInitiateAdapter(this,viewModel)
        binding.viewPager2.adapter = adapter

    }

    private fun setApplicationDetails() {
        viewModel.reInitiateData.value?.appDetails = device?.application_details!!
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }



}