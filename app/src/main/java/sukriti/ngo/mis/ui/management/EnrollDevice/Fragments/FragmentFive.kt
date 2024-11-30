package sukriti.ngo.mis.ui.management.EnrollDevice.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.json.JSONObject
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.FragmentFiveBinding
import sukriti.ngo.mis.ui.management.EnrollDevice.Lambda.DDBSaveLambda.DynamoDbDataWriterResponseCallback
import sukriti.ngo.mis.ui.management.EnrollDevice.Navigation.ViewPagerControl
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Policy
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.WifiDetails
import sukriti.ngo.mis.ui.management.ManagementViewModel
import sukriti.ngo.mis.ui.management.adapters.WifiListAdapter2
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.CreateQrLambda.CreateQrRequest
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.CreateQrLambda.CreateQrResponse
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.CreateQrLambda.CreateQrResponseHandler
import sukriti.ngo.mis.ui.management.lambda.FetchWifiDetails.FetchWifiDetailsResponse
import sukriti.ngo.mis.ui.management.lambda.FetchWifiDetails.FetchWifiDetailsResponseHandler
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.UserAlertClient

class FragmentFive(
    val viewModel: ManagementViewModel,
    val lambdaClient: LambdaClient,
    val enrollDeviceViewModel: EnrollDeviceViewModel
) : Fragment()
{
    lateinit var binding: FragmentFiveBinding
    private val debugging = "debugging"
    private lateinit var userAlertClient: UserAlertClient
    var interactionListener: ViewPagerControl? = null
    var wifiList =  ArrayList<WifiDetails>()
    val selectedWifiList =  ArrayList<WifiDetails>()

    companion object {
        private const val TAG = "debugging"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView: FragmentFive")
        binding = FragmentFiveBinding.inflate(inflater)
        userAlertClient = UserAlertClient(activity)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated: FragmentFive")
        binding.applicationTypeTv.setOnClickListener {
            val optionsArray = resources.getStringArray(R.array.applicationType)
            val adapter : ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.dropdown, optionsArray)
            binding.applicationTypeTv.setAdapter(adapter)
            binding.applicationTypeTv.showDropDown()
        }

        binding.upiPaymentStatusTv.setOnClickListener {
            val optionsArray = arrayOf("True", "False")
            val adapter : ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.dropdown, optionsArray)
            binding.upiPaymentStatusTv.setAdapter(adapter)
            binding.upiPaymentStatusTv.showDropDown()
        }

        binding.languageTv.setOnClickListener {
            val optionsArray = resources.getStringArray(R.array.available_languages)
            val adapter : ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.dropdown, optionsArray)
            binding.languageTv.setAdapter(adapter)
            binding.languageTv.showDropDown()
        }

        binding.next.setOnClickListener {
            if(enrollDeviceViewModel.stepFiveCompleted) {
                interactionListener?.goToNextPage()
            }
            else {
                if (verifyDetails()) {
                    saveDataInDynamoDb()
                }
            }
        }

        binding.isAmsEnabledTv?.setOnClickListener {
            val optionsArray = arrayOf("True", "False")
            val adapter : ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.dropdown, optionsArray)
            binding.isAmsEnabledTv!!.setAdapter(adapter)
            binding.isAmsEnabledTv!!.showDropDown()
        }


        binding.back.setOnClickListener {
            interactionListener?.goToPrevPage()
        }

        binding.deviceUnattendedTimerLayout.setEndIconOnClickListener {
            binding.deviceUnattendedTimerLayout.isHelperTextEnabled = !binding.deviceUnattendedTimerLayout.isHelperTextEnabled
        }

        setDefaultValues()

    }

    private fun setDefaultValues() {
        val time = "20"
        val language = "Hindi"
        val upi = "False"
        val appType = "Cabin Automation System without BWT"
        val margin = "0"
        val isAmsEnabled = "False"
        binding.deviceUnattendedTimerEditText.setText(time)
        binding.languageTv.setText(language)
        binding.upiPaymentStatusTv.setText(upi)
        binding.applicationTypeTv.setText(appType)
        binding.marginTopEditText.setText(margin)
        binding.marginBottomEditText.setText(margin)
        binding.marginLeftEditText.setText(margin)
        binding.marginRightEditText.setText(margin)
        binding.isAmsEnabledTv?.setText(isAmsEnabled)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: FragmentFive")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: FragmentFive")
    }

    override fun onResume() {
        super.onResume()
        fetchWifiDetails()
        Log.i(TAG, "onResume: FragmentFive")
    }

    private fun getPolicyJson(policy: Policy): JsonObject {
        val policyJson = JsonObject()
        policyJson.addProperty("name", policy.name)
        policyJson.addProperty("cameraDisabled", policy.cameraDisabled)
        policyJson.addProperty("addUserDisabled", policy.addUserDisabled)
        policyJson.addProperty("factoryResetDisabled", policy.factoryResetDisabled)
        policyJson.addProperty("mountPhysicalMediaDisabled", policy.mountPhysicalMediaDisabled)
        policyJson.addProperty("mountPhysicalMediaDisabled", policy.mountPhysicalMediaDisabled)
        policyJson.addProperty("safeBootDisabled", policy.safeBootDisabled)
        policyJson.addProperty("uninstallAppsDisabled", policy.uninstallAppsDisabled)
        policyJson.addProperty("vpnConfigDisabled", policy.vpnConfigDisabled)
        policyJson.addProperty("networkResetDisabled", policy.networkResetDisabled)
        policyJson.addProperty("smsDisabled", policy.smsDisabled)
        policyJson.addProperty("removeUserDisabled", policy.removeUserDisabled)
        policyJson.addProperty("outgoingCallsDisabled", policy.outgoingCallsDisabled)
        policyJson.addProperty("bluetoothConfigDisabled", policy.bluetoothConfigDisabled)

        if(policy.applications != null) {
            val applicationArray = JsonArray()
            for (i in 0 until policy.applications.size) {
                val app = policy.applications[i]
                val obj = JsonObject()
                obj.addProperty("installType", app.installType)
                obj.addProperty("packageName", app.packageName)
                applicationArray.add(obj)
            }

            policyJson.add("applications", applicationArray)
        }
        return policyJson
    }

    private fun saveDataInDynamoDb() {
        val payload = JsonObject()
        payload.addProperty("serial_number", enrollDeviceViewModel.deviceSerialNumber)
        payload.addProperty("cabin_name", enrollDeviceViewModel.cabinDetails.Name)
        payload.addProperty("command", "update-data")

        enrollDeviceViewModel.appDetails.unattended_timmer = binding.deviceUnattendedTimerEditText.text.toString()
        enrollDeviceViewModel.appDetails.application_type = binding.applicationTypeTv.text.toString()
        enrollDeviceViewModel.appDetails.upi_payment_status = binding.upiPaymentStatusTv.text.toString()
        enrollDeviceViewModel.appDetails.language = binding.languageTv.text.toString()
        enrollDeviceViewModel.appDetails.margin_top = binding.marginTopEditText.text.toString()
        enrollDeviceViewModel.appDetails.margin_bottom = binding.marginBottomEditText.text.toString()
        enrollDeviceViewModel.appDetails.margin_left = binding.marginLeftEditText.text.toString()
        enrollDeviceViewModel.appDetails.margin_right = binding.marginRightEditText.text.toString()
        enrollDeviceViewModel.appDetails.isAmsEnabled = binding.isAmsEnabledTv?.text.toString()

        val applicationLevelDetails = JsonObject()
        applicationLevelDetails.addProperty("unattended_timmer", enrollDeviceViewModel.appDetails.unattended_timmer)
        applicationLevelDetails.addProperty("language", enrollDeviceViewModel.appDetails.language)
        applicationLevelDetails.addProperty("application_type", enrollDeviceViewModel.appDetails.application_type)
        applicationLevelDetails.addProperty("upi_payment_status", enrollDeviceViewModel.appDetails.upi_payment_status)
        applicationLevelDetails.addProperty("margin_top", enrollDeviceViewModel.appDetails.margin_top)
        applicationLevelDetails.addProperty("margin_bottom", enrollDeviceViewModel.appDetails.margin_bottom)
        applicationLevelDetails.addProperty("margin_start", enrollDeviceViewModel.appDetails.margin_left)
        applicationLevelDetails.addProperty("margin_end", enrollDeviceViewModel.appDetails.margin_right)
        applicationLevelDetails.addProperty("isAmsEnabled", enrollDeviceViewModel.appDetails.isAmsEnabled)

        payload.add("application_details", applicationLevelDetails)
        payload.addProperty("serial_number", enrollDeviceViewModel.deviceSerialNumber)
        payload.addProperty("command", "update-data")
        payload.addProperty("details_type", "application_details")

        val wifiArray = JsonArray()
        for(i in 0 until selectedWifiList.size) {
            val wifi = JsonObject()
            wifi.addProperty("name", selectedWifiList[i].name)
            wifi.addProperty("password", selectedWifiList[i].password)

            wifiArray.add(wifi)
        }

        val defaultWifi = JsonObject()
        defaultWifi.addProperty("name", selectedWifiList[0].name)
        defaultWifi.addProperty("password", selectedWifiList[0].password)

        applicationLevelDetails.add("wifiCredentials", wifiArray)
        applicationLevelDetails.add("defaultWifi", defaultWifi)
        Log.i("ddbPayload", "Second Payload: "+ Gson().toJson(payload))
        userAlertClient.showWaitDialog("Processing...")
        lambdaClient.ExecuteDynamoDbDataWriter(payload, object: DynamoDbDataWriterResponseCallback {
            override fun onSuccess(response: JsonObject) {
                userAlertClient.closeWaitDialog()
                userAlertClient.showWaitDialog("Generating QR")
                val request = CreateQrRequest(viewModel.getSelectedEnterprise().name, enrollDeviceViewModel.selectedPolicy.name, enrollDeviceViewModel.deviceSerialNumber)

                Log.i("QrRequest", "onSuccess: Create Qr Request:  "+ Gson().toJson(request))

                lambdaClient.ExecuteCreateQrLambda(request, object: CreateQrResponseHandler {
                    override fun onSuccess(response: CreateQrResponse) {
                        userAlertClient.closeWaitDialog()
                        Log.i("createQr", "Response: "+ Gson().toJson(response))
                        if(response.statusCode == 200) {
                            val obj = JSONObject(response.body)
                            Log.i("CreateQr", "Obj: $obj")
                            try {
                                val url = obj.getString("imageUrl")
                                Log.i("CreateQr", "URL: $url")
                                enrollDeviceViewModel.qrUrl = url
                                Log.i("QrScreen", "Frag Five url: ${enrollDeviceViewModel.qrUrl}" )
                                enrollDeviceViewModel.applicationDetailsUpdated = true
                                enrollDeviceViewModel.stepFiveCompleted = true
                                interactionListener?.goToNextPage()
                            } catch (exception: Exception) {
                                exception.message?.let { it1 -> Log.i("CreateQr", it1) }
                            }
                        } else {
                            userAlertClient.showDialogMessage("Error", "Something went wrong", false)
                        }
                    }

                    override fun onError(message: String) {
                        userAlertClient.closeWaitDialog()
                        userAlertClient.showDialogMessage("Error", message, false)
                    }
                })
            }

            override fun onError(message: String) {
                userAlertClient.closeWaitDialog()
                userAlertClient.showDialogMessage("Please try again", message, false)
            }
        })
    }

    private fun verifyDetails(): Boolean {
        var timer = true
        var appType = true
        var upi = true
        var lang = true
        var marginTop = true
        var marginBottom = true
        var marginStart = true
        var marginEnd = true
        var isAmsEnabled = true

        if(binding.deviceUnattendedTimerEditText.text.toString().isEmpty()) {
            timer =  false
            binding.deviceUnattendedTimerLayout.error = "Field is required"
            binding.deviceUnattendedTimerLayout.isErrorEnabled = true
        } else {
            binding.deviceUnattendedTimerLayout.isErrorEnabled = false
        }

        if(binding.applicationTypeTv.text.toString().isEmpty()) {
            appType = false
            binding.applicationTypeLayout.error = "Field is required"
            binding.applicationTypeLayout.isErrorEnabled = true
        } else {
            binding.applicationTypeLayout.isErrorEnabled = false
        }

        if(binding.upiPaymentStatusTv.text.toString().isEmpty()) {
            upi =  false
            binding.upiPaymentStatusLayout.error = "Field is required"
            binding.upiPaymentStatusLayout.isErrorEnabled = true
        } else {
            binding.upiPaymentStatusLayout.isErrorEnabled = false
        }

        if(binding.languageTv.text.toString().isEmpty()) {
            lang = false
            binding.languageLayout.error = "Field is required"
            binding.languageLayout.isErrorEnabled = true
        } else {
            binding.languageLayout.isErrorEnabled = true
        }

        if(binding.marginTopEditText.text.toString().isEmpty()) {
            marginTop = false
            binding.marginTopLayout.error = "Field is required"
            binding.marginTopLayout.isErrorEnabled = true
        }
        else {
            binding.marginTopLayout.isErrorEnabled = false
        }

        if(binding.marginBottomEditText.text.toString().isEmpty()) {
            marginBottom = false
            binding.marginBottomEditText.error = "Field is required"
            binding.marginBottomLayout.isErrorEnabled = true
        }
        else {
            binding.marginBottomLayout.isErrorEnabled = true
        }

        if(binding.marginLeftEditText.text.toString().isEmpty()) {
            marginStart = false
            binding.marginLeftLayout.error = "Field is required"
            binding.marginRightLayout.isErrorEnabled = true
        }
        else {
            binding.marginRightLayout.isErrorEnabled = true
        }

        if(binding.marginRightEditText.text.toString().isEmpty()) {
            marginEnd = false
            binding.marginRightLayout.error = "Field is required"
            binding.marginRightLayout.isErrorEnabled = true
        }
        else {
            binding.marginRightLayout.isErrorEnabled = true
        }

        if(binding.isAmsEnabledTv?.text.toString().isEmpty()) {
            isAmsEnabled =  false
            binding.isAmsEnabledLayout?.error = "Field is required"
            binding.isAmsEnabledLayout?.isErrorEnabled = true
        } else {
            binding.isAmsEnabledLayout?.isErrorEnabled = false
        }

        return timer && appType && upi && lang && marginTop && marginBottom && marginStart && marginEnd && isAmsEnabled
    }

    fun setWifiRecyclerView(){
        binding.wifiRecyclerView?.layoutManager = LinearLayoutManager(context)


//        wifiList.add(WifiDetails("John", "rtertetre"))
//        wifiList.add(WifiDetails("John", "rtertetre"))
//        wifiList.add(WifiDetails("John", "rtertetre"))

        //val wifiListAdapter = WifiListAdapter2(wifiList)
        val wifiAdapter = WifiListAdapter2(wifiList, selectedWifiList )
        // Set the adapter to the RecyclerView
        binding.wifiRecyclerView?.adapter = wifiAdapter

        binding.ChooseDefaultButton.setOnClickListener {
            if (!selectedWifiList.isEmpty() ) {
                wifiAdapter.setRadioButtonVisibility(true)
            }
        }
    }

    private fun fetchWifiDetails() {
        userAlertClient.showWaitDialog("Fetching Wi-Fi Details")
        lambdaClient.ExecuteFetchWifiDetailsLambda(fetchWifiDetailsResponseHandler)
    }

    private val fetchWifiDetailsResponseHandler = object : FetchWifiDetailsResponseHandler {
        override fun onSuccess(response: FetchWifiDetailsResponse) {
            userAlertClient.closeWaitDialog()

            if(response.statusCode == 200) {
                wifiList = response.body
                setWifiRecyclerView()
            }
            else {
                Log.i("fetchWifiDetails", "StatusCode: ${response.statusCode}")
            }
/*
            val statusCode = response.get("statusCode").asInt
            if(statusCode == 200) {
                val body = response.get("body").asJsonArray
                body.forEach {
                    val json = it.asJsonObject
                    Log.i("wifi", "${json.get("name")} ${json.get("password")}")
                }
            }
*/
        }

        override fun onError(message: String) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error", message, false)
        }
    }

    fun chooseDefault(){
        binding.ChooseDefaultButton?.setOnClickListener(View.OnClickListener {
        })
    }

    override fun onPause() {
        super.onPause()
        Log.i(debugging, "onPause: FragmentFive ")
    }

    override fun onStop() {
        super.onStop()
        Log.i(debugging, "onStop: FragmentFive ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(debugging, "onDestroyView: FragmentFive ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(debugging, "onDestroy: FragmentFive ")
    }


}