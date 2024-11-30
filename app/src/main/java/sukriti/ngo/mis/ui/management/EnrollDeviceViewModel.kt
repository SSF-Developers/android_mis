package sukriti.ngo.mis.ui.management

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.JsonObject
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ComplexDetails
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Policy
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.PolicyListItem
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ThingDetails
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.RefreshListCallback
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.NameCodeModel
import sukriti.ngo.mis.ui.management.data.device.ApplicationDetails
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListIotStateDistrictCityResponseHandler
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaRequest
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponseHandler
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ManagementLambdaRequest
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.RequestManagementLambda
import sukriti.ngo.mis.ui.management.lambda.QrShare.QrShareLambdaResponseHandler
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.SharedPrefsClient
import java.io.IOException

class EnrollDeviceViewModel(application: Application) : AndroidViewModel(application) {
    var selectedState = -1
    var selectedDistrict = -1
    var selectedCity = -1
    var selectedComplex = -1
    var selectedEnterprise = ""

    var complexDetails = ComplexDetails()
    private var complexDetailsMutableLiveData = MutableLiveData<ComplexDetails>()
    var complexDetailsLiveData = complexDetailsMutableLiveData as LiveData<ComplexDetails>
    var thingsList = mutableListOf<String>()
    var thingDetailsList = mutableListOf<ThingDetails>()
    var policies = mutableListOf<Policy>()
    var policyListItem = mutableListOf<PolicyListItem>()
    var TAG = "Enrollment"
    var deviceSerialNumber = ""
    var qrUrl = ""
    var appDetails = ApplicationDetails()


    var ddbEntryCreated = false
    var ddbEntryUpdated = false
    var policyDdbUpdated = false
    var applicationDetailsUpdated = false

    var iotStatesList = mutableListOf<NameCodeModel>()
    private var iotStatesListMutableLiveData = MutableLiveData<Array<String>>()
    var iotStateListLiveData = iotStatesListMutableLiveData as LiveData<Array<String>>

    var iotDistrictsList = mutableListOf<NameCodeModel>()
    private var iotDistrictsListMutableLiveData = MutableLiveData<Array<String>>()
    var iotDistrictsListLiveData = iotDistrictsListMutableLiveData as LiveData<Array<String>>

    var iotCitiesList = mutableListOf<NameCodeModel>()
    private var iotCitiesListMutableLiveData = MutableLiveData<Array<String>>()
    var iotCitiesListLiveData = iotCitiesListMutableLiveData as LiveData<Array<String>>

    var iotComplexesList = emptyArray<String>()
    private var iotComplexesListMutableLiveData = MutableLiveData<Array<String>>()
    var iotComplexesListLiveData = iotComplexesListMutableLiveData as LiveData<Array<String>>

    private var dataFetchCompletedMutableLiveData = MutableLiveData<Boolean>()
    var dataFetchCompleteLiveData = dataFetchCompletedMutableLiveData as LiveData<Boolean>

    private val cabinListMutableLiveData = MutableLiveData<Array<String>>()
    var cabinListLiveData = cabinListMutableLiveData as LiveData<Array<String>>

    private val cabinDetailsListMutableLiveData = MutableLiveData<MutableList<ThingDetails>>()
    var cabinDetailsListLiveData = cabinDetailsListMutableLiveData as LiveData<MutableList<ThingDetails>>

//    private val policyListMutableLiveData = MutableLiveData<MutableList<Policy>>()
//    var policyListLiveData = policyListMutableLiveData as LiveData<MutableList<Policy>>

    private val policyListMutableLiveData = MutableLiveData<MutableList<PolicyListItem>>()
    var policyListLiveData = policyListMutableLiveData as LiveData<MutableList<PolicyListItem>>

    private val fetchedCabinDetailsUpToPosition = IntArray(1)
    var lambdaClient = LambdaClient(getApplication<Application>().applicationContext)

    var sharedPrefsClient = SharedPrefsClient(getApplication<Application>().applicationContext)
    var cabinDetails = ThingDetails()
    var selectedPolicy = Policy()

    var stepOneCompleted = false
    var stepTwoCompleted = false
    var stepThreeCompleted = false
    var stepFourCompleted = false
    var stepFiveCompleted = false

    fun fetchListOf(
        request: ManagementLambdaRequest,
        callback: ListIotStateDistrictCityResponseHandler,
        action: String
    ) {
        Log.d(
            TAG,
            "EnrollmentViewModel: fetchListOf() called with: request = $request, callback = $callback, action = $action"
        )
        when (action) {
            "list-iot-state" -> {
                lambdaClient.ExecuteManagementLambda(request, callback)
            }

            "list-iot-district" -> {
                lambdaClient.ExecuteManagementLambda(request, callback)
            }

            "list-iot-city" -> {
                lambdaClient.ExecuteManagementLambda(request, callback)
            }

            "list-iot-clientName" -> {
                lambdaClient.ExecuteManagementLambda(request, callback)
            }

            "list-ddb-state" -> {
                lambdaClient.ExecuteManagementLambda(request, callback)
            }

            "list-ddb-district" -> {
                lambdaClient.ExecuteManagementLambda(request, callback)
            }

            "list-ddb-city" -> {
                lambdaClient.ExecuteManagementLambda(request, callback)
            }

        }

    }

    fun fetchListOf(
        request: RequestManagementLambda,
        callback: ListIotStateDistrictCityResponseHandler,
        action: String
    ) {
        Log.d(
            TAG,
            "EnrollmentViewModel: fetchListOf() called with: request = $request, callback = $callback, action = $action"
        )
        if (action == "list-ddb-city") {
            lambdaClient.ExecuteManagementLambda(request, callback)
        }
    }

    fun getSmartnessList(): java.util.ArrayList<String> {
        Log.d(TAG, "getSmartnessList() called")
        val list = java.util.ArrayList<String>()
        list.add("None")
        list.add("Basic")
        list.add("Premium")
        list.add("Extra-Premium")
        return list
    }


    fun fetchIotStatesList(callback: ListIotStateDistrictCityResponseHandler) {
        Log.d(TAG, "EnrollmentViewModel: fetchIotStatesList() called with: callback = $callback")
        val request = ManagementLambdaRequest(
            sharedPrefsClient.getUserDetails().user.userName,
            "list-iot-state"
        )

        stepOneCompleted = false
        lambdaClient.ExecuteManagementLambda(
            request,
            object : ListIotStateDistrictCityResponseHandler {
                override fun onSuccess(response: JsonObject) {
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
                            Log.i(
                                TAG,
                                "EnrollmentViewModel: fetchIotStatesList(): onSuccess: posing vlaue on live data"
                            )
                            iotStatesListMutableLiveData.postValue(list)
                        } else {
                            callback.onError("No States Found")
                        }
                    } else {
                        val message = response.get("body").asString
                        callback.onError(message)
                    }

                }

                override fun onError(message: String?) {
                    Log.d(
                        TAG,
                        "EnrollmentViewModel: fetchIotStatesList: onError() called with: message = $message"
                    )
                    callback.onError(message)
                }

            })
    }


    fun fetchIotDistrictList(callback: ListIotStateDistrictCityResponseHandler) {
        Log.d(TAG, "EnrollmentViewModel: fetchIotDistrictList() called with: callback = $callback")
        val request = ManagementLambdaRequest(
            sharedPrefsClient.getUserDetails().user.userName,
            "list-iot-district",
            iotStatesList[selectedState].CODE
        )
        stepOneCompleted = false

        lambdaClient.ExecuteManagementLambda(
            request,
            object : ListIotStateDistrictCityResponseHandler {
                override fun onSuccess(response: JsonObject) {
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
                            iotDistrictsListMutableLiveData.postValue(list)
                        } else {
                            callback.onError("No Districts Found")
                        }
                    } else {
                        val message = response.get("body").asString
                        callback.onError(message)
                    }

                }

                override fun onError(message: String?) {
                    Log.d(
                        TAG,
                        "EnrollmentViewModel: fetchIotDistrictList: onError() called with: message = $message"
                    )
                    callback.onError(message)
                }

            })
    }

    fun fetchIotCityList(callback: ListIotStateDistrictCityResponseHandler) {
        Log.d(TAG, "EnrollmentViewModel: fetchIotCityList() called with: callback = $callback")
        val request = ManagementLambdaRequest(
            sharedPrefsClient.getUserDetails().user.userName,
            "list-iot-city",
            iotDistrictsList[selectedDistrict].CODE
        )
        stepOneCompleted = false

        lambdaClient.ExecuteManagementLambda(
            request,
            object : ListIotStateDistrictCityResponseHandler {
                override fun onSuccess(response: JsonObject) {

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
                            iotCitiesListMutableLiveData.postValue(list)
                        } else {
                            callback.onError("No Cities Found")
                        }
                    } else {
                        val message = response.get("body").asString
                        callback.onError(message)
                    }

                }

                override fun onError(message: String?) {
                    callback.onError(message)
                }
            })
    }

    fun fetchIotComplexList(callback: ListIotStateDistrictCityResponseHandler) {
        Log.d(TAG, "EnrollmentViewModel: fetchIotComplexList() called with: callback = $callback")
        val request = ManagementLambdaRequest(
            sharedPrefsClient.getUserDetails().user.userName,
            "list-iot-complex",
            iotCitiesList[selectedCity].CODE
        )
        stepOneCompleted = false

        lambdaClient.ExecuteManagementLambda(
            request,
            object : ListIotStateDistrictCityResponseHandler {
                override fun onSuccess(response: JsonObject) {

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
                            iotComplexesListMutableLiveData.postValue(tempComplexList)
                        } else {
                            callback.onError("No Complexes Found")
                        }
                    } else {
                        val message = response.get("body").asString
                        callback.onError(message)
                    }

                }

                override fun onError(message: String?) {
                    callback.onError(message)
                }

            })
    }

    fun fetchComplexDetails(enterpriseName: String, position: Int) {
        // this will be used to fetch policies and their respective data
        selectedEnterprise = enterpriseName
        selectedComplex = position

        val request = ManagementLambdaRequest(
            sharedPrefsClient.getUserDetails().user.userName,
            "list-iot-complexDetail",
            iotComplexesList[position]
        )

        Log.i("step1", "Fetching complex details")
        lambdaClient.ExecuteManagementLambda(request, fetchComplexDetailsCallback)

    }

    private val fetchComplexDetailsCallback = object : ListIotStateDistrictCityResponseHandler {
        override fun onSuccess(response: JsonObject) {
            Log.i("complexDetails", response.toString())
            Log.i("step1", "Complex details fetch completed")
            val gson = Gson()
            val jsonObject = gson.fromJson(
                response["body"],
                JsonObject::class.java
            )
            Log.i("complexDetails", jsonObject.toString())

            parseComplexDetails(jsonObject)
        }

        override fun onError(message: String?) {
            Log.i("dataFetchCompleted", "False")
            Log.i("step1", "Fetching complex details failed")
            dataFetchCompletedMutableLiveData.postValue(false)
        }
    }


    private fun parseComplexDetails(details: JsonObject) {
        Log.d(TAG, "Fragment One: parseResponse() called with: response = $details")
        Log.i("step1", "Parsing Complex details")
        val complexDetails = ComplexDetails()

        for ((key, value) in details.entrySet()) {
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

        complexDetails.complexName = iotComplexesList[selectedComplex]
        this.complexDetails = complexDetails
        Log.i("step1", "Parsing complex details completed")
        // fetch cabin list
        fetchCabinList(sharedPrefsClient.getUserDetails().user.userName, false)
    }

    private fun fetchCabinList(userName: String, isRefreshingList: Boolean) {
        Log.i("step1", "Fetching cabins list")
        val request = ManagementLambdaRequest(
            userName,
            "list-iot-thing",
            complexDetails.complexName
        )
        if(isRefreshingList) {
            lambdaClient.ExecuteIotCabinLambda(request, refreshCabinListHandler)
        } else {
            lambdaClient.ExecuteIotCabinLambda(request, cabinListHandler)
        }

    }

    private val cabinListHandler = object: ListIotStateDistrictCityResponseHandler {
        override fun onSuccess(response: JsonObject) {
            thingsList.clear()
            thingDetailsList.clear()
            fetchedCabinDetailsUpToPosition[0] = 0
            val body = response["body"].asJsonObject
            val thingsArray = body["things"].asJsonArray
            Log.i("thingsArray", thingsArray.toString())
            Log.i("step1", "Fetching cabin list completed")
            for (i in 0 until thingsArray.size()) {
                val thing = thingsArray[i].asString
                Log.i("thingsList", "$i -> $thing")
                thingsList.add(thing)
            }

            if(thingsArray.size() > 0) {
                // fetch details of every thing in thingsList
                fetchCabinDetails(false)
            } else {
                // no cabin found
                // continue to fetch policies and things list will be empty and message will be shown on screen that thing list is empty
                Log.i("step1", "Fetching policies")
                fetchPolicies(selectedEnterprise, false)
            }

        }

        override fun onError(message: String?) {
            Log.i("dataFetchCompleted", "False")
            Log.i("step1", "Fetching cabins list failed")
            dataFetchCompletedMutableLiveData.postValue(false)
        }
    }

    private val refreshCabinListHandler = object : ListIotStateDistrictCityResponseHandler {
        override fun onSuccess(response: JsonObject) {
            thingsList.clear()
            thingDetailsList.clear()
            fetchedCabinDetailsUpToPosition[0] = 0
            val body = response["body"].asJsonObject
            val thingsArray = body["things"].asJsonArray
            Log.i("thingsArray", thingsArray.toString())
            Log.i("step1", "Fetching cabin list completed")
            for (i in 0 until thingsArray.size()) {
                val thing = thingsArray[i].asString
                Log.i("thingsList", "$i -> $thing")
                thingsList.add(thing)
            }

            if(thingsArray.size() > 0) {
                // fetch details of every thing in thingsList
                fetchCabinDetails(true)
            }

        }

        override fun onError(message: String?) {

        }
    }

    private fun fetchCabinDetails(isRefreshingList : Boolean) {
        if (fetchedCabinDetailsUpToPosition[0] < thingsList.size) {
            if (fetchedCabinDetailsUpToPosition[0] == 0) {
                thingDetailsList.clear()
            }
            val request = ManagementLambdaRequest(
                sharedPrefsClient.getUserDetails().user.userName,
                "describe-iot-thing",
                thingsList[fetchedCabinDetailsUpToPosition[0]]
            )
            Log.i("step1", "Fetching cabin details for index ${fetchedCabinDetailsUpToPosition[0]}")
            if(isRefreshingList) {
                lambdaClient.ExecuteIotCabinLambda(request, refreshCabinDetailsHandler)
            } else {
                lambdaClient.ExecuteIotCabinLambda(request, cabinDetailsHandler)
            }
        }
    }

    private val cabinDetailsHandler: ListIotStateDistrictCityResponseHandler =
        object : ListIotStateDistrictCityResponseHandler {
            override fun onSuccess(response: JsonObject) {
                Log.i("step1", "Fetching cabin details for index ${fetchedCabinDetailsUpToPosition[0]} success")
                Log.i("CabinDetails", "Details -: " + Gson().toJson(response))
                val statusCode = response["statusCode"].asInt
                if (statusCode == 200) {
                    val details = ThingDetails()
                    details.attributesMap = HashMap()
                    val body = response["body"].asJsonObject
                    details.defaultClientId = body["defaultClientId"].asString
                    Log.i("CabinDetails", "Default client ID " + details.defaultClientId)
                    details.name = body["thingName"].asString
                    Log.i("CabinDetails", "Thing Name " + details.name)
                    details.id = body["thingId"].asString
                    details.arn = body["thingArn"].asString
                    details.thingType = body["thingTypeName"].asString
                    details.version = body["version"].asLong
                    val objectMapper = ObjectMapper()
                    val jsonNode: JsonNode
                    try {
                        jsonNode = objectMapper.readTree(response.toString())
                        val attributeNode = jsonNode["body"]["attributes"]
                        if (!attributeNode.isEmpty) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                attributeNode.fields()
                                    .forEachRemaining { (key, value): Map.Entry<String?, JsonNode> ->
                                        details.attributesMap[key] = value.asText()
                                    }
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    thingDetailsList.add(details)
                    fetchedCabinDetailsUpToPosition[0] = fetchedCabinDetailsUpToPosition[0] + 1
                    if (fetchedCabinDetailsUpToPosition[0] < thingsList.size) {
                        fetchCabinDetails(false)
                    } else {
                        fetchPolicies(selectedEnterprise, false)
                    }
                } else {
                    fetchedCabinDetailsUpToPosition[0] = fetchedCabinDetailsUpToPosition[0] + 1
                    fetchCabinDetails(false)
                }
            }

            override fun onError(message: String?) {
                // I currently have no idea what to do here
                Log.i("step1", "Fetching cabin details for index ${fetchedCabinDetailsUpToPosition[0]} failed")
                fetchedCabinDetailsUpToPosition[0] += 1
                fetchCabinDetails(false)
            }
        }

    private val refreshCabinDetailsHandler = object: ListIotStateDistrictCityResponseHandler {
        override fun onSuccess(response: JsonObject) {
            val statusCode = response["statusCode"].asInt
            if (statusCode == 200) {
                val details = ThingDetails()
                details.attributesMap = HashMap()
                val body = response["body"].asJsonObject
                details.defaultClientId = body["defaultClientId"].asString
                Log.i("CabinDetails", "Default client ID " + details.defaultClientId)
                details.name = body["thingName"].asString
                Log.i("CabinDetails", "Thing Name " + details.name)
                details.id = body["thingId"].asString
                details.arn = body["thingArn"].asString
                details.thingType = body["thingTypeName"].asString
                details.version = body["version"].asLong
                val objectMapper = ObjectMapper()
                val jsonNode: JsonNode
                try {
                    jsonNode = objectMapper.readTree(response.toString())
                    val attributeNode = jsonNode["body"]["attributes"]
                    if (!attributeNode.isEmpty) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            attributeNode.fields()
                                .forEachRemaining { (key, value): Map.Entry<String?, JsonNode> ->
                                    details.attributesMap[key] = value.asText()
                                }
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                thingDetailsList.add(details)
                fetchedCabinDetailsUpToPosition[0] = fetchedCabinDetailsUpToPosition[0] + 1
                if (fetchedCabinDetailsUpToPosition[0] < thingsList.size) {
                    fetchCabinDetails(true)
                } else {
                    cabinDetailsListMutableLiveData.postValue(thingDetailsList)
                }
            } else {
                fetchedCabinDetailsUpToPosition[0] = fetchedCabinDetailsUpToPosition[0] + 1
                fetchCabinDetails(true)
            }
        }

        override fun onError(message: String?) {
            fetchedCabinDetailsUpToPosition[0] += 1
            fetchCabinDetails(true)
        }
    }

    private fun fetchPolicies(enterpriseName: String, isRefreshingList: Boolean) {
        Log.i("step1", "Fetching policies")
        val request = ListPolicyLambdaRequest(enterpriseName)

        val callback: ListPolicyLambdaResponseHandler = object : ListPolicyLambdaResponseHandler {
            override fun onSuccess(response: ListPolicyLambdaResponse) {
                if (response.statusCode == 200) {
                    Log.i("step1", "Fetching policies success")
                    for (i in response.body.indices) {
                        Log.i("Policies", "Policies: " + Gson().toJson(response.body[i]))
                    }
//                    policies = ArrayList(response.body)
                    policyListItem = ArrayList(response.body)
                    Log.i("dataFetchCompleted", "True")
                    stepOneCompleted = true
                    dataFetchCompletedMutableLiveData.postValue(true)
                } else {
                    Log.e("listPolicies", "onSuccess: error in policies lambda response")
                    Log.i("dataFetchCompleted", "False")
                    dataFetchCompletedMutableLiveData.postValue(false)
                }
            }

            override fun onError(message: String) {
                Log.i("dataFetchCompleted", "False")
                Log.i("step1", "Fetching policy failed")
                dataFetchCompletedMutableLiveData.postValue(false)
            }
        }

        if(isRefreshingList) {
            lambdaClient.ExecuteListPolicyLambda(request, refreshPoliciesListCallback)
        } else {
            lambdaClient.ExecuteListPolicyLambda(request, callback)
        }

    }

    fun refreshPolicies() {
        fetchPolicies(selectedEnterprise, true)
    }


    fun resetViewModelState() {
        Log.d(TAG, "EnrollmentViewModel: resetViewModelState() called")
        selectedState = -1
        selectedDistrict = -1
        selectedCity = -1
        selectedComplex = -1


        complexDetails = ComplexDetails()
        cabinDetails = ThingDetails()
        thingsList.clear()
        thingDetailsList.clear()
        policies.clear()
        policyListItem.clear()
        selectedPolicy = Policy()

        iotStatesList.clear()
        iotDistrictsList.clear()
        iotCitiesList.clear()
        iotComplexesList = emptyArray()


        iotStatesListMutableLiveData.value = null
        iotDistrictsListMutableLiveData.value = null
        iotCitiesListMutableLiveData.value = null
        iotComplexesListMutableLiveData.value = null
        dataFetchCompletedMutableLiveData.value = null

        ddbEntryCreated = false
        ddbEntryUpdated = false
        applicationDetailsUpdated = false
        policyDdbUpdated = false
        qrUrl = ""
        deviceSerialNumber = ""

        stepOneCompleted = false
        stepTwoCompleted = false
        stepThreeCompleted = false
        stepFourCompleted = false
        stepFiveCompleted = false
    }

    private val refreshPoliciesListCallback = object: ListPolicyLambdaResponseHandler {
        override fun onSuccess(response: ListPolicyLambdaResponse) {
            if (response.statusCode == 200) {
                Log.i("step1", "Fetching policies success")
                for (i in response.body.indices) {
                    Log.i("Policies", "Policies: " + Gson().toJson(response.body[i]))
                }
//                policies = ArrayList(response.body)

                policyListItem = ArrayList(response.body)
                policyListMutableLiveData.postValue(policyListItem)
            } else {
                Log.e("listPolicies", "onSuccess: error in policies lambda response")
            }
        }

        override fun onError(message: String) {
            Log.i("dataFetchCompleted", "False")
            Log.i("step1", "Fetching policy failed")
        }
    }

    fun verifyComplexSelected(): Boolean {
        return complexDetails.complexName.isBlank()

    }

    fun setupPolicies() {

    }
    fun refreshCabinList() {
        fetchCabinList(sharedPrefsClient.getUserDetails().user.userName, true)
    }

    private val refreshPoliciesList = object: RefreshListCallback {
        override fun refreshList() {

        }
    }

    public fun shareQr(request: JsonObject, callback: QrShareLambdaResponseHandler) {
        lambdaClient.ExecuteShareQrLambda(request, callback)
    }

}