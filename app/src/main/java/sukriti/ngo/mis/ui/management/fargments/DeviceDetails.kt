package sukriti.ngo.mis.ui.management.fargments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.JsonObject
import sukriti.ngo.mis.R
import sukriti.ngo.mis.communication.SimpleHandler
import sukriti.ngo.mis.databinding.DeviceDetailsScreenBinding
import sukriti.ngo.mis.interfaces.DialogActionHandler
import sukriti.ngo.mis.interfaces.DialogSingleActionHandler
import sukriti.ngo.mis.ui.management.EnrollDevice.Interface.RefreshDeviceList
import sukriti.ngo.mis.ui.management.EnrollDevice.Navigation.Walkthrough
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ComplexDetails
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ThingDetails
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.SubmitCallback
import sukriti.ngo.mis.ui.management.ManagementViewModel
import sukriti.ngo.mis.ui.management.data.device.ApplicationDetails
import sukriti.ngo.mis.ui.management.data.device.Device
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaRequest
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponseHandler
import sukriti.ngo.mis.ui.management.lambda.PatchDevice.PatchDeviceLambdaResponse
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.UserAlertClient

class DeviceDetails(
    val device: Device?,
    val deleteDeviceListener: DeleteDevice,
    val viewModel: ManagementViewModel,
    val refreshDeviceList: RefreshDeviceList
) : BottomSheetDialogFragment() {

    lateinit var binding: DeviceDetailsScreenBinding
    lateinit var userAlertClient: UserAlertClient
    lateinit var enrollDeviceViewModel: EnrollDeviceViewModel
    lateinit var lambdaClient: LambdaClient
    private lateinit var modalBehavior: BottomSheetBehavior<FrameLayout>

    companion object {
        private const val TAG = "DeviceDetails"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DeviceDetailsScreenBinding.inflate(layoutInflater)
        userAlertClient = UserAlertClient(activity)
        lambdaClient = LambdaClient(requireContext())

        if (dialog == null) {
            Log.i(TAG, "onCreateView: dialog is null")
        } else {
            modalBehavior = (dialog as BottomSheetDialog).behavior
            modalBehavior.isDraggable = true
            modalBehavior.peekHeight = ViewGroup.LayoutParams.MATCH_PARENT
            modalBehavior.isFitToContents = true

            Log.i(TAG, "onCreateView: dialog is not null")
        }

        enrollDeviceViewModel = ViewModelProviders.of(this).get(EnrollDeviceViewModel::class.java)

        binding.deleteDevice.setOnClickListener {
            Log.i("deleteDevice", "on click: ${Gson().toJson(device)}")
            modalBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            deleteDeviceListener.onDeleteRequest(device)
        }

        binding.updateDevice.setOnClickListener {
            val updateDialog = UpdateDeviceDialog(context, viewModel, updateDeviceCallback)
            updateDialog.isCancelable = false
            updateDialog.show(childFragmentManager, "Update Device")
        }


//            if(device?.QR_CREATED_STATE == false) {
//                val reInitiateDialog = ReInitiateHome(device, viewModel)
//                reInitiateDialog.show(parentFragmentManager, "reinitiate")

        enrollDeviceViewModel.lambdaClient = lambdaClient
        val thingDetails = ThingDetails()
        thingDetails.Name = device?.cabin_details?.name
        thingDetails.attributes = device?.cabin_details?.attributes
        thingDetails.DefaultClientId = device?.cabin_details?.defaultClientId
        thingDetails.thingGroup = device?.cabin_details?.thingGroup
        thingDetails.thingType = device?.cabin_details?.thingType
        thingDetails.billingGroup = device?.cabin_details?.billingGroup
        enrollDeviceViewModel.cabinDetails = thingDetails

        val complexDetails = ComplexDetails()

        device?.complex_details?.attributes?.forEach {
            val attribute = it
            val key = attribute.Name
            val value = attribute.Value
            Log.i("mapping", "Key $key : Value $value")
            val stringValue = value.toString()
            when (key) {
                "STATE_NAME" -> complexDetails.stateName = stringValue
                "DISTRICT_NAME" -> complexDetails.districtName = stringValue
                "CITY_NAME" -> complexDetails.cityName = stringValue
                "STATE_CODE" -> complexDetails.stateCode = stringValue
                "DISTRICT_CODE" -> complexDetails.districtCode = stringValue
                "CITY_CODE" -> complexDetails.cityCode = stringValue
                "LATT" -> complexDetails.latitude = stringValue
                "LONG" -> complexDetails.longitude = stringValue
                "CLNT" -> complexDetails.client = stringValue
                "BILL" -> complexDetails.billingGroup = stringValue
                "DATE" -> complexDetails.date = stringValue
                "DEVT" -> complexDetails.deviceType = stringValue
                "SLVL" -> complexDetails.smartness = stringValue
                "QMWC" -> complexDetails.wCCountMale = stringValue
                "QFWC" -> complexDetails.wCCountFemale = stringValue
                "QPWC" -> complexDetails.wCCountPD = stringValue
                "QURI" -> complexDetails.urinals = stringValue
                "QURC" -> complexDetails.urinalCabins = stringValue
                "QBWT" -> complexDetails.bwt = stringValue
                "COCO" -> complexDetails.commissioningStatus = stringValue
                "QSNV" -> complexDetails.napkinVmCount = stringValue
                "QSNI" -> complexDetails.napkinIncineratorCount = stringValue
                "MSNI" -> complexDetails.napkinIncineratorManufacturer = stringValue
                "MSNV" -> complexDetails.napkinVmManufacturer = stringValue
                "AR_K" -> complexDetails.kioskArea = stringValue
                "CWTM" -> complexDetails.waterAtmCapacity = stringValue
                "ARSR" -> complexDetails.supervisorRoomSize = stringValue
                "MANU" -> complexDetails.manufacturer = stringValue
                "CIVL" -> complexDetails.civilPartner = stringValue
                "TECH" -> complexDetails.techProvider = stringValue
                "ONMP" -> complexDetails.oMPartner = stringValue
                "UUID" -> complexDetails.uuid = stringValue
                "ROUTER_MOBILE" -> complexDetails.routerMobile = stringValue
                "ROUTER_IMEI" -> complexDetails.routerImei = stringValue
                "MODIFIED_BY" -> complexDetails.modifiedBy = stringValue
                "THINGGROUPTYPE" -> complexDetails.thingGroupType = stringValue
                "ADDR" -> complexDetails.address = stringValue
            }
        }

        enrollDeviceViewModel.complexDetails = complexDetails
        enrollDeviceViewModel.deviceSerialNumber = device?.serial_number!!
        enrollDeviceViewModel.selectedEnterprise = viewModel.getSelectedEnterprise().name
        enrollDeviceViewModel.ddbEntryCreated = true
        enrollDeviceViewModel.ddbEntryUpdated = true

        if (device.DEVICE_POLICY_STATE) {
            enrollDeviceViewModel.policyDdbUpdated = true
            enrollDeviceViewModel.stepFourCompleted = true
            Log.i("reInitiateFeature", "Selected Policy: ${device.policy_details?.policy_name}")
            enrollDeviceViewModel.selectedPolicy.name = device.policy_details?.policy_name
            Log.i(
                "reInitiateFeature",
                "View Model Policy: ${enrollDeviceViewModel.selectedPolicy.name}"
            )

        }
        if (device.DEVICE_APPLICATION_STATE) {
            enrollDeviceViewModel.appDetails = device.application_details
            enrollDeviceViewModel.applicationDetailsUpdated = true
            enrollDeviceViewModel.stepFiveCompleted = true
        }

        binding.reinitiate.setOnClickListener {
            if (!device.QR_CREATED_STATE) {
                fetchPolicies()
            } else if (device.QR_CREATED_STATE && !device.DEVICE_PROV_GET_INFO_PUBLISH) {
                val str =
                    "1. Scan QR and enroll device \n2. Launch Sukriti Iot Admin app and enter serial number (if asked) and sign in into the control panel"
                userAlertClient.showDialogMessage("Do One of the following things", str, false)
            }
        }

        setUpGeneralDetails()

        return binding.root
    }

    private val updateDeviceCallback = object : SubmitCallback {
        override fun onSubmit(json: JsonObject) {
            userAlertClient.showWaitDialog("Updating device...")
            viewModel.patchDevice(json, device?.android_data?.name, device?.android_data?.hardwareInfo?.serialNumber, patchDeviceCallback)
        }
    }

    private val patchDeviceCallback = object : PatchDeviceLambdaResponse {
        override fun onSuccess(response: JsonObject) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage(
                "Update successful",
                "Device updated successfully",
                object : DialogSingleActionHandler {
                    override fun onAction() {
                        modalBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        refreshDeviceList.refreshList()
                    }
                }
            )
        }

        override fun onError(message: String) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Update Failed", message, false)
        }
    }

    private fun setUpGeneralDetails() {
        if (device != null) {

            if (device.android_data != null) {
                val nameArray = device.android_data.name.split("/")
                val name = nameArray[nameArray.size - 1]

                val policyNameArray = device.android_data.policyName.split("/")
                val policyName = policyNameArray[policyNameArray.size - 1]

                val enrollmentTokenNameArray = device.android_data.enrollmentTokenName.split("/")
                val enrollmentTokenName =
                    enrollmentTokenNameArray[enrollmentTokenNameArray.size - 1]

                val userNameArray = device.android_data.userName.split("/")
                val userName = userNameArray[userNameArray.size - 1]

                val appliedPolicyNameArray = device.android_data.appliedPolicyName.split("/")
                val appliedPolicyName = appliedPolicyNameArray[appliedPolicyNameArray.size - 1]

                binding.managementModeValue.text = device.android_data.managementMode.toString()
                binding.nameValue.text = name
                binding.stateValue.text = device.android_data.state.toString()
                binding.enrollmentTimeValue.text = device.android_data.enrollmentTime.toString()
                binding.policyNameValue.text = policyName
                binding.apiLevelValue.text = device.android_data.apiLevel.toString()
                binding.policyCompliantValue.text = device.android_data.policyCompliant.toString()
                binding.userNameValue.text = userName
                binding.lastStatusReportTimeValue.text =
                    device.android_data.lastStatusReportTime.toString()
                binding.enrollmentTokenDataValue.text =
                    device.android_data.enrollmentTokenData.toString()
                binding.enrollmentTokenNameValue.text = enrollmentTokenName
                binding.appliedPolicyNameValue.text = appliedPolicyName
                binding.appliedStateValue.text = device.android_data.appliedState.toString()
                binding.appliedPolicyVersionValue.text =
                    device.android_data.appliedPolicyVersion.toString()
                binding.ownershipValue.text = device.android_data.ownership.toString()
                binding.lastPolicySyncTimeValue.text =
                    device.android_data.lastPolicySyncTime.toString()


                binding.modelValue.text = device.android_data.hardwareInfo.model.toString()
                binding.serialNumberValue.text =
                    device.android_data.hardwareInfo.serialNumber.toString()
                binding.manufacturerValue.text =
                    device.android_data.hardwareInfo.manufacturer.toString()
                binding.brandValue.text = device.android_data.hardwareInfo.brand.toString()
                binding.hardwareValue.text = device.android_data.hardwareInfo.hardware.toString()
                binding.deviceBasebandVersionValue.text =
                    device.android_data.hardwareInfo.deviceBasebandVersion.toString()

            }

            if (device.qr_details?.qr?.isNotEmpty() == true) {
                context.let {
                    if (it != null) {
                        Glide.with(it)
                            .load(device.qr_details?.qr)
                            .into(binding.enrollmentQr)

                    }

                }
            } else {
                binding.enrollmentQr.setImageResource(R.drawable.error2)
            }

            binding.entryCreateValue.text = device.CREATED_STATE.toString()
            binding.thingCreatedValue.text = device.PROVISIONING_THING_CREATED_STATE.toString()
            binding.certAttachedValue.text = device.CERT_ATTACH_STATE.toString()
            binding.policyDataSavedValue.text = device.DEVICE_POLICY_STATE.toString()
            binding.appDetailsSavedValue.text = device.DEVICE_APPLICATION_STATE.toString()
            binding.qrCreatedValue.text = device.QR_CREATED_STATE.toString()
            binding.topicHitValue.text = device.DEVICE_PROV_GET_INFO_PUBLISH.toString()
            binding.dataPublishedValue.text = device.DEVICE_PROV_GET_INFO_RESP_INIT.toString()
            binding.awsCommissioningCompletedValue.text =
                device.DEVICE_PROV_COMPLETED_INFO_RESP_INIT.toString()

            binding.complexNameValue.text = device.complex_details.name.toString()

        }
    }

    interface DeleteDevice {
        fun onDeleteRequest(device: Device?)
    }

    private fun fetchPolicies() {
//        userAlertClient.closeWaitDialog()
        val request = ListPolicyLambdaRequest(viewModel.getSelectedEnterprise().name)
        val callback: ListPolicyLambdaResponseHandler = object : ListPolicyLambdaResponseHandler {
            override fun onSuccess(response: ListPolicyLambdaResponse) {
                userAlertClient.closeWaitDialog();
                if (response.statusCode == 200) {
                    for (i in response.body.indices) {
                        Log.i("Policies", "Policies: " + Gson().toJson(response.body[i]))
                    }
                    enrollDeviceViewModel.policyListItem = ArrayList(response.body)

                    if (device?.DEVICE_POLICY_STATE == true) {
                        var trimmedName = enrollDeviceViewModel.selectedPolicy.name.split("/")[3]
                        trimmedName = trimmedName.replace("_KIOSK_LAUNCHER", "")
                        Log.i("reInitiateFeature", "Trimmed Policy Name: $trimmedName")
                        enrollDeviceViewModel.policyListItem.forEach {
                            Log.i("reInitiateFeature", "Policy Name: ${it.name}")
                            it.isSelected = trimmedName == it.name
                            Log.i("reInitiateFeature", "${it.isSelected}")
                        }
                    }
                    val walkthrough = Walkthrough(
                        requireContext(),
                        parentFragmentManager,
                        lifecycle,
                        enrollDeviceViewModel,
                        viewModel,
                        LambdaClient(requireContext()),
                        refreshList
                    )
                    if (device?.DEVICE_POLICY_STATE == false)
                        walkthrough.jumpToScreen = 3
                    else if (device?.DEVICE_APPLICATION_STATE == false)
                        walkthrough.jumpToScreen = 4
                    walkthrough.show(parentFragmentManager, "walkthrough")

//                    setupAndShowComplexDetails(enrollViewModel.complexDetails)
                } else {
                    userAlertClient.closeWaitDialog()
                    Log.e("listPolicies", "onSuccess: error in policies lambda response")
//                    setupAndShowComplexDetails(enrollViewModel.complexDetails)
                }
            }

            override fun onError(message: String) {
                userAlertClient.closeWaitDialog()
                userAlertClient.showDialogMessage("Error", message, false)
            }
        }
        userAlertClient.showWaitDialog("Please wait")
        lambdaClient.ExecuteListPolicyLambda(request, callback)
    }

    private val refreshList = object : RefreshDeviceList {
        override fun refreshList() {
            modalBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            refreshDeviceList.refreshList()
        }
    }

}