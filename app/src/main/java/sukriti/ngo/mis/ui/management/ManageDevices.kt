package sukriti.ngo.mis.ui.management

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.JsonObject
import sukriti.ngo.mis.R
import sukriti.ngo.mis.communication.SimpleHandler
import sukriti.ngo.mis.databinding.FragmentManageDevicesBinding
import sukriti.ngo.mis.interfaces.DialogSingleActionHandler
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.management.EnrollDevice.Interface.RefreshDeviceList
import sukriti.ngo.mis.ui.management.EnrollDevice.Navigation.Walkthrough
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.PolicyToggle
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.NameCodeModel
import sukriti.ngo.mis.ui.management.adapters.DeviceAdapter
import sukriti.ngo.mis.ui.management.data.device.Device
import sukriti.ngo.mis.ui.management.fargments.DeviceDetails
import sukriti.ngo.mis.ui.management.lambda.DeleteDevice.DeleteDeviceResponseHandler
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListIotStateDistrictCityResponseHandler
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ManagementLambdaRequest
import sukriti.ngo.mis.ui.management.lambda.ListDevices.ListDeviceLambdaRequest
import sukriti.ngo.mis.ui.management.lambda.ListDevices.ListDeviceLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.ListDevices.ListDeviceResponseHandler
import sukriti.ngo.mis.ui.management.lambda.TogglePolicyState.TogglePolicyStateResponseHandler
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset


class ManageDevices : Fragment() {
    private lateinit var binding: FragmentManageDevicesBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var lambdaClient: LambdaClient
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var viewModel: ManagementViewModel
    private lateinit var enrollDeviceViewModel: EnrollDeviceViewModel
    private lateinit var adapter: DeviceAdapter
    private lateinit var sharedPrefsClient: SharedPrefsClient

    var iotStatesList = mutableListOf<NameCodeModel>()
    var iotDistrictsList = mutableListOf<NameCodeModel>()
    var iotCitiesList = mutableListOf<NameCodeModel>()
    var iotComplexesList = emptyArray<String>()

    private var selectedState = NameCodeModel()
    private var selectedDistrict = NameCodeModel()
    private var selectedCity = NameCodeModel()
    private var selectedComplex = ""
    private  var walkthrough: Walkthrough? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView: ")
        binding = FragmentManageDevicesBinding.inflate(layoutInflater)

        userAlertClient = UserAlertClient(activity)
        lambdaClient = LambdaClient(activity?.applicationContext)
        viewModel = ViewModelProviders.of(requireActivity()).get(ManagementViewModel::class.java)
        enrollDeviceViewModel =
            ViewModelProviders.of(requireActivity()).get(EnrollDeviceViewModel::class.java)
        sharedPrefsClient = SharedPrefsClient(requireContext())
        Log.i("onCreateView", "Manage Devices: onCreateView()")
        setAdapter(viewModel.devicesList)

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(TAG, "onAttach: ")
        if (context is NavigationHandler) {
            navigationHandler = context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement NavigationHandler"
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated: ")

        /*        binding.filterViews.onItemClickListener =
                    AdapterView.OnItemClickListener { parent, v, position, id ->
                        Log.d(
                            TAG,
                            "onItemClick() called with: position = $position, id = $id"
                        )
                        if(position == 0) {
                            binding.selectStateLayout.visibility = View.GONE
                            binding.selectDistrictLayout.visibility = View.GONE
                            binding.selectCityLayout.visibility = View.GONE
                            binding.selectComplexLayout.visibility = View.GONE

                            binding.devicesRecyclerView.visibility = View.VISIBLE
                        } else {
                            binding.selectStateLayout.visibility = View.VISIBLE
                            binding.selectDistrictLayout.visibility = View.VISIBLE
                            binding.selectCityLayout.visibility = View.VISIBLE
                            binding.selectComplexLayout.visibility = View.VISIBLE

                            binding.devicesRecyclerView.visibility = View.GONE

                        }
                    }*/

        binding.selectStateTv.setOnClickListener {
            binding.selectStateTv.dismissDropDown()
            userAlertClient.showWaitDialog("Getting States")
            fetchIotStatesList()
        }

        binding.selectDistrictTv.setOnClickListener {
            if (binding.selectStateTv.text.toString().isNotEmpty()) {
                binding.selectStateLayout.isErrorEnabled = false
                binding.selectDistrictTv.dismissDropDown()
                userAlertClient.showWaitDialog("Getting Districts")
                fetchIotDistrictList()
            } else {
                binding.selectDistrictTv.dismissDropDown()
                binding.selectStateLayout.isErrorEnabled = true
                binding.selectStateLayout.error = "Please select state"
            }
        }

        binding.selectCityTv.setOnClickListener {
            if (binding.selectDistrictTv.text.toString().isNotEmpty()) {
                binding.selectDistrictLayout.isErrorEnabled = false
                binding.selectCityTv.dismissDropDown()
                userAlertClient.showWaitDialog("Getting Cities")
                fetchIotCityList()
            } else {
                binding.selectDistrictLayout.isErrorEnabled = true
                binding.selectCityTv.dismissDropDown()
                binding.selectDistrictLayout.error = "Please select district"
            }
        }

        binding.selectComplexTv.setOnClickListener {
            if (binding.selectCityTv.text.toString().isNotEmpty()) {
                binding.selectCityLayout.isErrorEnabled = false
                binding.selectComplexTv.dismissDropDown()
                userAlertClient.showWaitDialog("Getting Complexes")
                fetchIotComplexList()
            } else {
                binding.selectCityLayout.isErrorEnabled = true
                binding.selectComplexTv.dismissDropDown()
                binding.selectCityLayout.error = "Please select city"
            }
        }

        binding.enrollDevice.setOnClickListener {

            val ctx = context
            if (ctx != null) {
                walkthrough = Walkthrough(
                    ctx,
                    childFragmentManager,
                    lifecycle,
                    enrollDeviceViewModel,
                    viewModel,
                    lambdaClient,
                    refreshDeviceList
                )
                walkthrough!!.show(childFragmentManager, "enroll")
                walkthrough!!.isCancelable = false
            }

        }
    }


    override fun onStart() {
        super.onStart()

        Log.i(TAG, "onStart: ")
//        setAdapter(viewModel.devicesList)
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: ")
    }

    companion object {
        private const val SELECT_STATE = 1
        private const val SELECT_DISTRICT = 2
        private const val SELECT_CITY = 3
        private const val SELECT_COMPLEX = 4
        private const val TAG = "ManageDevices"

        private var INSTANCE: ManageDevices? = null

        fun getInstance(): ManageDevices {
            return INSTANCE ?: ManageDevices()
        }
    }

    private fun setAdapter(deviceList: ArrayList<Device>) {
        if (deviceList.isEmpty()) {
            binding.noDeviceFound.visibility = View.VISIBLE
        } else {
            binding.noDeviceFound.visibility = View.GONE
            adapter = DeviceAdapter(context, deviceList, itemClickListener, policyToggleListener ,viewModel)
            val layoutManager = LinearLayoutManager(context)
            binding.devicesRecyclerView.layoutManager = layoutManager
            binding.devicesRecyclerView.adapter = adapter

//            binding.enrolledDevices.text = deviceList.size.toString()
        }
    }

    private fun getDeviceList() {
        userAlertClient.showWaitDialog("Getting device list")


        val request = ListDeviceLambdaRequest()
        request.command = "get_web_device"
        request.complex = selectedComplex
        viewModel.getDeviceList(request, deviceListCallback)

    }

    private val deviceListCallback = object : ListDeviceResponseHandler {
        override fun onSuccess(response: ListDeviceLambdaResponse) {
            userAlertClient.closeWaitDialog()
            val logFile = File(context?.filesDir, "DevicesData.txt")
            if (!logFile.exists()) {
                logFile.createNewFile()
            }

            try {
                Log.i("createLogFile", "Inside Try block")
                val json = Gson().toJson(response)
                Log.i("createLogFile", "JSON Created")
                val outputStream = FileOutputStream(logFile)
                Log.i("createLogFile", "File Output Stream created")
                outputStream.write(json.toString().toByteArray(Charset.defaultCharset()))
                Log.i("createLogFile", "Output written successfully")
                outputStream.close()
                Log.i("createLogFile", "Output stream closed")
            } catch (exception: IOException) {
                exception.message?.let { Log.i("createLogFile", it) }
            }

            setAdapter(viewModel.devicesList)
        }

        override fun onError(message: String?) {
            userAlertClient.closeWaitDialog()
            if (message.equals("No Device Found")) {
//                    binding.cancelIcon.visibility = View.VISIBLE
                binding.noDeviceFound.visibility = View.VISIBLE
                binding.devicesRecyclerView.visibility = View.GONE
            } else {
//                    binding.cancelIcon.visibility = View.GONE
                binding.noDeviceFound.visibility = View.GONE
                binding.devicesRecyclerView.visibility = View.VISIBLE
                userAlertClient.showDialogMessage("Error", message, false)
            }
        }

    }

    private val itemClickListener: DeviceAdapter.DeviceItemClickListener =
        object : DeviceAdapter.DeviceItemClickListener {
            override fun onClick(device: Device?) {
                Toast.makeText(
                    context,
                    "${device?.android_data?.name} selected",
                    Toast.LENGTH_SHORT
                ).show()
                if (device != null) {
                    viewModel.setSelectedDevice(device)
                }
                val deviceDetailsDialog = DeviceDetails(device, deleteDevice, viewModel, refreshDeviceList)
                deviceDetailsDialog.show(childFragmentManager, "Device Details")
            }
        }

    private val policyToggleListener: PolicyToggle = object : PolicyToggle {
        override fun onClick(device: Device, position: Int, state: Boolean) {
/*
            userAlertClient.showConfirmActionDialog(
                childFragmentManager,
                "Toggle Policy?",
                "Do you want to toggle policy state? Type YES to confirm",
                "YES",
                object : SimpleHandler {
                    override fun onSuccess() {
                        // Write logic to toggle policy
                        Log.i("policyToggle", "onSuccess()")
                    }

                    override fun onError(message: String?) {
                        Log.i("policyToggle", "onError()")
//                        viewModel.devicesList.get(position).se
                        viewModel.devicesList[position].kiosk = !state
                        adapter.notifyItemChanged(position)
                    }
                }

            )
*/

            Log.i("policyToggleSwitch", "Position: $position , State: $state" )
            var message = ""
            message = if(state) {
                "Do you want to switch device to Handover mode?"
            } else {
                "Do you want to switch device to maintenance mode?"
            }
            val dialog = MaterialAlertDialogBuilder(requireContext(),R.style.materialAlertDialog)
                .setTitle("Toggle Policy")
                .setMessage(message)
                .setIcon(R.drawable.toggle)
//                .setBackground(ResourcesCompat.getDrawable(resources,R.drawable.round_corner_black,null))
                .setCancelable(false)
                .setPositiveButton("Yes"
                ) { dialogInterface, pos -> // Write logic to toggle policy
                    Log.i("policyToggle", "onSuccess()")

                    val request = JsonObject()
                    request.addProperty("command", "kiosk_device")
                    request.addProperty("serial_number", device.serial_number)
                    request.addProperty("kiosk_device", state)
                    userAlertClient.showWaitDialog("Switching device mode")
                    lambdaClient.ExecuteTogglePolicyStateLambda(request, object: TogglePolicyStateResponseHandler {
                        override fun onSuccess(response: JsonObject) {
                            userAlertClient.closeWaitDialog()
                            val statusCode = response.get("statusCode").asInt
                            if(statusCode == 200) {
                                var msg = ""
                                msg = if(state)
                                    "Device successfully switched to handover mode"
                                else
                                    "Device successfully switched to maintenance mode"

                                userAlertClient.showDialogMessage("Success", msg) {
                                    viewModel.devicesList[position].isKioskEnabled = state
                                    adapter.notifyItemChanged(position)
                                }
                            }
                        }

                        override fun onError(message: String) {
                            userAlertClient.closeWaitDialog()
                            userAlertClient.showDialogMessage("Error", message, false)
                        }
                    })
                }
                .setNegativeButton("No"
                ) { dialogInterface, pos ->
//                    viewModel.devicesList[position].isKioskEnabled = !state
                    adapter.notifyItemChanged(position)
                }.create()


            dialog.show()

        }

    }

    private val togglePolicyState = object: SimpleHandler {
        override fun onSuccess() {
            // Write logic to toggle policy

        }

        override fun onError(message: String?) {

        }
    }

    private val deleteDevice: DeviceDetails.DeleteDevice = object : DeviceDetails.DeleteDevice {
        override fun onDeleteRequest(device: Device?) {
            userAlertClient.showConfirmActionDialog(
                childFragmentManager,
                "Delete Device",
                "Do you want to delete device? Type 'DELETE' to continue.",
                "Delete",
                object : SimpleHandler {
                    override fun onSuccess() {
                        // Call Device Delete Lambda
                        userAlertClient.showWaitDialog("Deleting device...")
                        Log.i("deleteDevice", "onSuccess: Delete Device callback: Device Name: ${device?.android_data?.name}}" )
                        Log.i("deleteDevice", "onSuccess: Delete Device callback: Device Serial Number: ${device?.android_data?.hardwareInfo?.serialNumber}}" )

//                        val deleteRequest = DeleteDeviceLambdaRequest(
//                            "delete_device",
//                            viewModel.getSelectedEnterprise().name,
//                            device?.android_data?.name,
//                            device?.android_data?.hardwareInfo?.serialNumber
//                        )
//
                        val deleteRequest = JsonObject()
                        if(device?.android_data == null) {
                            // device is abandoned
                            deleteRequest.addProperty("abandonDevice", true )
                            deleteRequest.addProperty("command", "delete_device")
                            deleteRequest.addProperty("enterpriseId", viewModel.getSelectedEnterprise().name)
                            deleteRequest.addProperty("serial_number", device?.serial_number)
                        } else {
                            // device is not abandoned
                            deleteRequest.addProperty("abandonDevice", false)
                            deleteRequest.addProperty("command", "delete_device")
                            deleteRequest.addProperty("enterpriseId", viewModel.getSelectedEnterprise().name)
                            val value = JsonObject()
                            value.addProperty(device.serial_number, device.android_data?.name)
                            deleteRequest.add("value", value)
                        }
                        Log.i("deleteDevice", "onSuccess: Delete Device callback: ${Gson().toJson(deleteRequest)}" )
                        lambdaClient.ExecuteDeleteDeviceLambda(
                            deleteRequest,
                            deleteDeviceResponseHandler
                        )
                        Toast.makeText(context, "Deleting Device", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(error: String?) {
                        userAlertClient.showDialogMessage("Cannot delete device.", error, false)
                    }
                }
            )
        }
    }

    private val deleteDeviceResponseHandler = object : DeleteDeviceResponseHandler {
        override fun onSuccess(response: JsonObject) {
            userAlertClient.closeWaitDialog()
            if (response.get("statusCode").asInt == 200) {
                userAlertClient.showDialogMessage(
                    "Operation successful",
                    "Device deleted successfully",
                    false
                )
                getDeviceList()
            } else {
                userAlertClient.showDialogMessage(
                    "Operation failed !",
                    response.get("body").asString,
                    false
                )
            }
        }

        override fun onError(message: String) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error", message, false)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop: ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        walkthrough?.onDestroy()
        Log.i(TAG, "onDestroyView: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i(TAG, "onDetach: ")
    }
    private fun fetchIotStatesList() {
        val request = ManagementLambdaRequest(
            sharedPrefsClient.getUserDetails().user.userName,
            "list-iot-state"
        )

        lambdaClient.ExecuteManagementLambda(request, object :
            ListIotStateDistrictCityResponseHandler {
            override fun onSuccess(response: JsonObject) {
                userAlertClient.closeWaitDialog()
                Log.d(
                    TAG,
                    "EnrollmentViewModel: fetchIotStatesList: onSuccess() called with: response = $response"
                )
                val statusCode = response.get("statusCode").asInt
                if (statusCode == 200) {
                    val jsonArray = response.getAsJsonArray("body")
                    if (jsonArray.size() > 0) {
                        var list = emptyArray<String>()
                        val tempStateList = mutableListOf<NameCodeModel>()
                        for (i in 0 until jsonArray.size()) {
                            val obj = jsonArray.get(i).asJsonObject
                            val tempObj = NameCodeModel()
                            tempObj.NAME = obj.get("NAME").asString
                            tempObj.CODE = obj.get("CODE").asString
                            tempStateList.add(tempObj)
                            list += obj.get("NAME").asString
                        }
                        iotStatesList = tempStateList
                        setAdapterForSelectionFields(SELECT_STATE, list)
                        Log.i(
                            TAG,
                            "EnrollmentViewModel: fetchIotStatesList(): onSuccess: posing value on live data"
                        )
                    } else {
                        userAlertClient.showDialogMessage("Error", "No States Found", false)
                    }
                } else {
                    val message = response.get("body").asString
                    userAlertClient.showDialogMessage("Error", message, false)
                }

            }

            override fun onError(message: String?) {
                userAlertClient.closeWaitDialog()
                Log.d(
                    TAG,
                    "EnrollmentViewModel: fetchIotStatesList: onError() called with: message = $message"
                )
            }

        })
    }

    private fun fetchIotDistrictList() {
        val request = ManagementLambdaRequest(
            sharedPrefsClient.getUserDetails().user.userName,
            "list-iot-district",
            selectedState.CODE
        )

        lambdaClient.ExecuteManagementLambda(
            request,
            object : ListIotStateDistrictCityResponseHandler {
                override fun onSuccess(response: JsonObject) {
                    userAlertClient.closeWaitDialog()
                    Log.d(
                        TAG,
                        "EnrollmentViewModel: fetchIotDistrictList: onSuccess() called with: response = $response"
                    )
                    val statusCode = response.get("statusCode").asInt
                    if (statusCode == 200) {
                        val jsonArray = response.getAsJsonArray("body")
                        if (jsonArray.size() > 0) {

                            var list = emptyArray<String>()
                            val tempDistrictList = mutableListOf<NameCodeModel>()
                            for (i in 0 until jsonArray.size()) {
                                val obj = jsonArray.get(i).asJsonObject
                                val tempObj = NameCodeModel()
                                tempObj.NAME = obj.get("NAME").asString
                                tempObj.CODE = obj.get("CODE").asString
                                tempDistrictList.add(tempObj)
                                list += obj.get("NAME").asString
                            }
                            iotDistrictsList = tempDistrictList
                            setAdapterForSelectionFields(SELECT_DISTRICT, list)
                        } else {
                            userAlertClient.showDialogMessage("Error", "No Districts found", false)
                        }
                    } else {
                        val message = response.get("body").asString
                        userAlertClient.showDialogMessage("Error", message, false)
                    }

                }

                override fun onError(message: String?) {
                    Log.d(
                        TAG,
                        "EnrollmentViewModel: fetchIotDistrictList: onError() called with: message = $message"
                    )
                    userAlertClient.closeWaitDialog()
                    userAlertClient.showDialogMessage("Error", message, false)
                }

            })
    }

    private fun fetchIotCityList() {
        val request = ManagementLambdaRequest(
            sharedPrefsClient.getUserDetails().user.userName,
            "list-iot-city",
            selectedDistrict.CODE
        )

        lambdaClient.ExecuteManagementLambda(
            request,
            object : ListIotStateDistrictCityResponseHandler {
                override fun onSuccess(response: JsonObject) {
                    userAlertClient.closeWaitDialog()
                    val statusCode = response.get("statusCode").asInt
                    if (statusCode == 200) {
                        val jsonArray = response.getAsJsonArray("body")

                        if (jsonArray.size() > 0) {
                            var list = emptyArray<String>()
                            val tempCityList = mutableListOf<NameCodeModel>()
                            for (i in 0 until jsonArray.size()) {
                                val obj = jsonArray.get(i).asJsonObject
                                val tempObj = NameCodeModel()
                                tempObj.NAME = obj.get("NAME").asString
                                tempObj.CODE = obj.get("CODE").asString
                                tempCityList.add(tempObj)
                                list += obj.get("NAME").asString
                            }
                            iotCitiesList = tempCityList
                            setAdapterForSelectionFields(SELECT_CITY, list)
                        } else {
                            userAlertClient.showDialogMessage("Error", "No Cities found", false)
                        }
                    } else {
                        val message = response.get("body").asString
                        userAlertClient.showDialogMessage("Error", message, false)
                    }

                }

                override fun onError(message: String?) {
                    userAlertClient.closeWaitDialog()
                    userAlertClient.showDialogMessage("Error", message, false)
                }
            })
    }

    private fun fetchIotComplexList() {
        val request = ManagementLambdaRequest(
            sharedPrefsClient.getUserDetails().user.userName,
            "list-iot-complex",
            selectedCity.CODE
        )

        lambdaClient.ExecuteManagementLambda(
            request,
            object : ListIotStateDistrictCityResponseHandler {
                override fun onSuccess(response: JsonObject) {
                    userAlertClient.closeWaitDialog()
                    val statusCode = response.get("statusCode").asInt
                    if (statusCode == 200) {
                        val jsonArray = response.getAsJsonArray("body")

                        if (jsonArray.size() > 0) {
                            var tempComplexList = emptyArray<String>()

                            for (i in 0 until jsonArray.size()) {
                                val complexNameObj = jsonArray.get(i).asJsonObject
                                val complexName = complexNameObj.get("Name").asString
                                tempComplexList += complexName
                                Log.i("complexName", complexName.toString())
                            }

                            iotComplexesList = tempComplexList
                            setAdapterForSelectionFields(SELECT_COMPLEX, tempComplexList)
                        } else {
                            userAlertClient.showDialogMessage("Error", "No Complexes Found", false)
                        }
                    } else {
                        val message = response.get("body").asString
                        userAlertClient.showDialogMessage("Error", message, false)
                    }

                }

                override fun onError(message: String?) {
                    userAlertClient.closeWaitDialog()
                    userAlertClient.showDialogMessage("Error", message, false)
                }

            })
    }

    private fun setAdapterForSelectionFields(action: Int, list: Array<String>) {
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.dropdown, list)
        when (action) {
            SELECT_STATE -> {
                binding.selectStateTv.setAdapter(adapter)
                binding.selectStateTv.onItemClickListener = stateSelectedListener
                binding.selectStateTv.showDropDown()
            }

            SELECT_DISTRICT -> {
                binding.selectDistrictTv.setAdapter(adapter)
                binding.selectDistrictTv.onItemClickListener = districtSelectedListener
                binding.selectDistrictTv.showDropDown()
            }

            SELECT_CITY -> {
                binding.selectCityTv.setAdapter(adapter)
                binding.selectCityTv.onItemClickListener = citySelectedListener
                binding.selectCityTv.showDropDown()
            }

            SELECT_COMPLEX -> {
                binding.selectComplexTv.setAdapter(adapter)
                binding.selectComplexTv.onItemClickListener = complexSelectedListener
                binding.selectComplexTv.showDropDown()
            }
        }
    }


    private var stateSelectedListener =
        AdapterView.OnItemClickListener { _, _, position, _ ->
            selectedState = iotStatesList[position]
            binding.selectDistrictTv.setText("")
            binding.selectCityTv.setText("")
            binding.selectComplexTv.setText("")

            binding.selectStateTv.setAdapter(null)
        }


    private var districtSelectedListener =
        AdapterView.OnItemClickListener { _, _, position, _ ->
            selectedDistrict = iotDistrictsList[position]
            binding.selectCityTv.setText("")
            binding.selectComplexTv.setText("")
            binding.selectDistrictTv.setAdapter(null)
        }

    private var citySelectedListener =
        AdapterView.OnItemClickListener { _, _, position, _ ->
            binding.selectCityTv.setAdapter(null)
            selectedCity = iotCitiesList[position]
            binding.selectComplexTv.setText("")
        }


    private var complexSelectedListener =
        AdapterView.OnItemClickListener { _, _, position, _ ->
            selectedComplex = iotComplexesList[position]
            binding.selectComplexTv.setAdapter(null)
            binding.selectComplexTv.clearFocus()
            getDeviceList()
        }

    private var refreshDeviceList = object : RefreshDeviceList {
        override fun refreshList() {
            getDeviceList()
        }
    }

}