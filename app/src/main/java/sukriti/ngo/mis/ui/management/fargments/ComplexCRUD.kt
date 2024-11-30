package sukriti.ngo.mis.ui.management.fargments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.google.gson.JsonObject
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.FragmentComplexCrudBinding
import sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.RegisterComplex
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ComplexDetails
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.NameCodeModel
import sukriti.ngo.mis.ui.management.ManagementViewModel
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListIotStateDistrictCityResponseHandler
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ManagementLambdaRequest
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient

class ComplexCRUD : Fragment() {
    lateinit var binding: FragmentComplexCrudBinding
    var iotStatesList = mutableListOf<NameCodeModel>()
    var iotDistrictsList = mutableListOf<NameCodeModel>()
    var iotCitiesList = mutableListOf<NameCodeModel>()
    var iotComplexesList = emptyArray<String>()
    private lateinit var viewModel: ManagementViewModel
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var lambdaClient: LambdaClient
    private lateinit var sharedPrefsClient: SharedPrefsClient

    private var selectedState = NameCodeModel()
    private var selectedDistrict = NameCodeModel()
    private var selectedCity = NameCodeModel()
    var selectedComplex = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentComplexCrudBinding.inflate(inflater)
        userAlertClient = UserAlertClient(activity)
        lambdaClient = LambdaClient(requireContext())
        viewModel = ViewModelProviders.of(requireActivity()).get(ManagementViewModel::class.java)
        sharedPrefsClient = SharedPrefsClient(requireContext())

        return binding.root
    }

    companion object {
        private const val SELECT_STATE = 1
        private const val SELECT_DISTRICT = 2
        private const val SELECT_CITY = 3
        private const val SELECT_COMPLEX = 4
        private const val TAG = "ComplexCRUD"

        private var Instance: ComplexCRUD? = null
        fun getInstance(): ComplexCRUD {
            return Instance ?: ComplexCRUD()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.creteNewComplexButton.setOnClickListener{
            val frag = RegisterComplex()
            frag.show(getParentFragmentManager(), "RegisterComplex")
        }

        binding.selectStateTv.setOnClickListener {
            userAlertClient.showWaitDialog("Fetching states...")
            fetchIotStatesList()
        }

        binding.selectDistrictTv.setOnClickListener {
            if(binding.selectStateTv.text.toString().isNotEmpty()) {
                binding.selectStateLayout.isErrorEnabled = false
                binding.selectDistrictTv.dismissDropDown()
                userAlertClient.showWaitDialog("Fetching districts")
                fetchIotDistrictList()
            } else {
                binding.selectDistrictTv.dismissDropDown()
                binding.selectStateLayout.isErrorEnabled = true
                binding.selectStateLayout.error = "Please select state"
            }
        }

        binding.selectCityTv.setOnClickListener {
            if(binding.selectDistrictTv.text.toString().isNotEmpty()) {
                binding.selectDistrictLayout.isErrorEnabled = false
                binding.selectCityTv.dismissDropDown()
                userAlertClient.showWaitDialog("Fetching cities")
                fetchIotCityList()
            } else {
                binding.selectDistrictLayout.isErrorEnabled = true
                binding.selectCityTv.dismissDropDown()
                binding.selectDistrictLayout.error = "Please select district"
            }
        }

        binding.selectComplexTv.setOnClickListener {
            if(binding.selectCityTv.text.toString().isNotEmpty()) {
                binding.selectCityLayout.isErrorEnabled = false
                binding.selectComplexTv.dismissDropDown()
                userAlertClient.showWaitDialog("Fetching complexes")
                fetchIotComplexList()
            } else {
                binding.selectCityLayout.isErrorEnabled = true
                binding.selectComplexTv.dismissDropDown()
                binding.selectCityLayout.error = "Please select city"
            }
        }

    }

    private fun fetchIotStatesList() {
        val request = ManagementLambdaRequest(sharedPrefsClient.getUserDetails().user.userName, "list-iot-state")

        lambdaClient.ExecuteManagementLambda(request, object :
            ListIotStateDistrictCityResponseHandler {
            override fun onSuccess(response: JsonObject) {
                userAlertClient.closeWaitDialog()
                Log.d(TAG, "EnrollmentViewModel: fetchIotStatesList: onSuccess() called with: response = $response")
                val statusCode = response.get("statusCode").asInt
                if (statusCode == 200) {
                    val jsonArray = response.getAsJsonArray("body")
                    if(jsonArray.size() > 0) {
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
                        Log.i(TAG, "EnrollmentViewModel: fetchIotStatesList(): onSuccess: posing vlaue on live data")
                    }
                    else {
                        userAlertClient.showDialogMessage("Error", "No States Found", false)
                    }
                }
                else {
                    val message = response.get("body").asString
                    userAlertClient.showDialogMessage("Error", message, false)
                }

            }

            override fun onError(message: String?) {
                userAlertClient.closeWaitDialog()
                Log.d(TAG, "EnrollmentViewModel: fetchIotStatesList: onError() called with: message = $message")
            }

        })
    }

    private fun fetchIotDistrictList() {
        val request = ManagementLambdaRequest(sharedPrefsClient.getUserDetails().user.userName, "list-iot-district", selectedState.CODE)

        lambdaClient.ExecuteManagementLambda(request, object :
            ListIotStateDistrictCityResponseHandler {
            override fun onSuccess(response: JsonObject) {
                userAlertClient.closeWaitDialog()
                Log.d(TAG, "EnrollmentViewModel: fetchIotDistrictList: onSuccess() called with: response = $response")
                val statusCode = response.get("statusCode").asInt
                if (statusCode == 200) {
                    val jsonArray = response.getAsJsonArray("body")
                    if(jsonArray.size() > 0) {

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
                }
                else {
                    val message = response.get("body").asString
                    userAlertClient.showDialogMessage("Error", message, false)
                }

            }

            override fun onError(message: String?) {
                Log.d(TAG, "EnrollmentViewModel: fetchIotDistrictList: onError() called with: message = $message")
                userAlertClient.closeWaitDialog()
                userAlertClient.showDialogMessage("Error", message, false)
            }

        })
    }

    private fun fetchIotCityList() {
        val request = ManagementLambdaRequest(sharedPrefsClient.getUserDetails().user.userName, "list-iot-city", selectedDistrict.CODE)

        lambdaClient.ExecuteManagementLambda(request, object :
            ListIotStateDistrictCityResponseHandler {
            override fun onSuccess(response: JsonObject) {
                userAlertClient.closeWaitDialog()
                val statusCode = response.get("statusCode").asInt
                if (statusCode == 200) {
                    val jsonArray = response.getAsJsonArray("body")

                    if(jsonArray.size() > 0) {
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
                    }
                    else {
                        userAlertClient.showDialogMessage("Error", "No Cities found", false)
                    }
                }
                else {
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
        val request = ManagementLambdaRequest(sharedPrefsClient.getUserDetails().user.userName, "list-iot-complex", selectedCity.CODE)

        lambdaClient.ExecuteManagementLambda(request, object :
            ListIotStateDistrictCityResponseHandler {
            override fun onSuccess(response: JsonObject) {
                userAlertClient.closeWaitDialog()
                val statusCode = response.get("statusCode").asInt
                if (statusCode == 200) {
                    val jsonArray = response.getAsJsonArray("body")

                    if(jsonArray.size() > 0) {
                        var tempComplexList = emptyArray<String>()

                        for (i in 0 until jsonArray.size()) {
                            val complexNameObj = jsonArray.get(i).asJsonObject
                            val complexName = complexNameObj.get("Name").asString
                            tempComplexList += complexName
                            Log.i("complexName", complexName.toString())
                        }

                        iotComplexesList = tempComplexList
                        setAdapterForSelectionFields(SELECT_COMPLEX, tempComplexList)
                    }
                    else {
                        userAlertClient.showDialogMessage("Error", "No Complexes Found", false)
                    }
                }
                else {
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
        val adapter : ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.dropdown, list)
        when(action) {
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
            fetchComplexDetails()
        }

    private fun fetchComplexDetails() {
        userAlertClient.showWaitDialog("Loading complex details")

        val callback: ListIotStateDistrictCityResponseHandler =
            object : ListIotStateDistrictCityResponseHandler {
                override fun onSuccess(response: JsonObject) {
                    userAlertClient.closeWaitDialog()
                    Log.i("complexDetails", response.toString())
                    val gson = Gson()
                    val jsonObject = gson.fromJson(response["body"], JsonObject::class.java)
                    Log.i("complexDetails", jsonObject.toString())
                    parseResponse(jsonObject)
                }

                override fun onError(message: String?) {
                    userAlertClient.closeWaitDialog()
                    if (message != null) {
                        Log.i("complexDetails", message)
                    }
                }
            }


        val request = ManagementLambdaRequest(
            sharedPrefsClient.getUserDetails().user.userName,
            "list-iot-complexDetail",
            selectedComplex
        )

        lambdaClient.ExecuteManagementLambda(request, callback)

    }

    private fun parseResponse(response: JsonObject) {
        Log.d(TAG, "Fragment One: parseResponse() called with: response = $response")
        val complexDetails = ComplexDetails()
        for ((key, value) in response.entrySet()) {
            Log.i("mapping", "Key $key : Value $value")
            val stringValue = value.asString
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
        complexDetails.complexName = selectedComplex
/*
                val request = ManagementLambdaRequest(
            userName,
            "list-iot-thing",
            enrollViewModel.complexDetails.complexName
        )
        lambdaClient.ExecuteIotCabinLambda(request, cabinListHandler)
        Log.i("dialog", Gson().toJson(complexDetails))*/
         setupAndShowComplexDetails(complexDetails);

    }

    private fun setupAndShowComplexDetails(complexDetails: ComplexDetails) {
        val dialog = ComplexDetailsDialog()
        dialog.managementViewModel = viewModel
        dialog.isCancelable = false
        dialog.setRetrievedComplexDetails(complexDetails)

        dialog.show(childFragmentManager, "ComplexDetails")
    }

}