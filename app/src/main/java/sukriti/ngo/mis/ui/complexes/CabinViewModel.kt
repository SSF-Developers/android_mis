package sukriti.ngo.mis.ui.complexes

import android.app.Application
import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import org.json.JSONObject
import sukriti.ngo.mis.communication.CommunicationUtilities
import sukriti.ngo.mis.communication.ConnectionStatusService
import sukriti.ngo.mis.communication.SimpleHandler
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.repository.DataRepository
import sukriti.ngo.mis.repository.entity.*
import sukriti.ngo.mis.repository.entity.Health
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData
import sukriti.ngo.mis.ui.complexes.bwtData.BwtConfig
import sukriti.ngo.mis.ui.complexes.data.*
import sukriti.ngo.mis.ui.complexes.data.AqiLumen
import sukriti.ngo.mis.ui.complexes.data.CmsConfig
import sukriti.ngo.mis.ui.complexes.data.OdsConfig
import sukriti.ngo.mis.ui.complexes.data.ResetProfile
import sukriti.ngo.mis.ui.complexes.data.UcemsConfig
import sukriti.ngo.mis.ui.complexes.data.UsageProfile
import sukriti.ngo.mis.ui.complexes.data.lambdaData.ConnectionStatus
import sukriti.ngo.mis.ui.complexes.interfaces.*
import sukriti.ngo.mis.ui.complexes.lambda.CabinDetailsLambdaClient
import sukriti.ngo.mis.ui.complexes.lambda.Cabin_fetchData.CabinDetailsLambdaRequest
import sukriti.ngo.mis.ui.complexes.lambda.Cabin_fetchData.CabinDetailsLambdaResult
import sukriti.ngo.mis.ui.complexes.lambda.bwt_fetchCabinDetails.BwtCabinDetailsLambdaRequest
import sukriti.ngo.mis.ui.complexes.lambda.bwt_fetchCabinDetails.BwtCabinDetailsLambdaResult
import sukriti.ngo.mis.ui.dashboard.interfaces.CheckQuickAccessRequestHandler
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.IotPublishLambdaRequest
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.IotPublishLambdaResult
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.IotPublishRequestHandler
import sukriti.ngo.mis.utils.*
import java.util.*
import kotlin.collections.ArrayList

class CabinViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var selectedUser: MemberDetailsData
    private var context: Context = application.applicationContext
    private var sharedPrefsClient: SharedPrefsClient
    private var provisioningClient: AWSIotProvisioningClient
    private var administrationClient: AdministrationClient
    private var dynamoDbClient: AdministrationDbHelper
    private lateinit var selectedComplex: Complex
    private lateinit var selectedCabin: Cabin
    private var userDetails: MemberDetailsData = MemberDetailsData()
    private val tag = "_ComplexesVM"

    private lateinit var cabinDetailsLambdaClient: CabinDetailsLambdaClient
    var forCabin = ""

    //live data for all fragments

    var _aqiLumen: MutableLiveData<AqiLumen> = MutableLiveData()
    var _health: MutableLiveData<sukriti.ngo.mis.ui.complexes.data.Health> = MutableLiveData()
    var _ucemsConfig: MutableLiveData<UcemsConfig> = MutableLiveData()
    var _cmsConfig: MutableLiveData<CmsConfig> = MutableLiveData()
    var _odsConfig: MutableLiveData<OdsConfig> = MutableLiveData()
    var _usageProfile: MutableLiveData<ArrayList<UsageProfile>> = MutableLiveData()
    var _resetProfile: MutableLiveData<ArrayList<ResetProfile>> = MutableLiveData()
    var _upiPaymentList: MutableLiveData<ArrayList<UpiPaymentList>> = MutableLiveData()
    var _usageAndFeedback: MutableLiveData<UsageAndFeedback> = MutableLiveData()

    var _bwtHealth: MutableLiveData<sukriti.ngo.mis.ui.complexes.bwtData.Health> = MutableLiveData()
    var _bwtUsage: MutableLiveData<ArrayList<sukriti.ngo.mis.ui.complexes.bwtData.UsageProfile>> =
        MutableLiveData()
    var _bwtReset: MutableLiveData<ArrayList<sukriti.ngo.mis.ui.complexes.bwtData.ResetProfile>> =
        MutableLiveData()
    var _bwtConfig: MutableLiveData<BwtConfig> = MutableLiveData()


    var aqiLumen: LiveData<AqiLumen> = _aqiLumen
    var health: LiveData<sukriti.ngo.mis.ui.complexes.data.Health> = _health
    var ucemsConfig: LiveData<UcemsConfig> = _ucemsConfig
    var cmsConfig: LiveData<CmsConfig> = _cmsConfig
    var odsConfig: LiveData<OdsConfig> = _odsConfig
    var usageProfile: LiveData<ArrayList<UsageProfile>> = _usageProfile
    var resetProfile: LiveData<ArrayList<ResetProfile>> = _resetProfile
    var upiPaymentList: LiveData<ArrayList<UpiPaymentList>> = _upiPaymentList
//    var usageAndFeedback: LiveData<UsageAndFeedback> = _usageAndFeedback

    var bwtHealth: LiveData<sukriti.ngo.mis.ui.complexes.bwtData.Health> = _bwtHealth
    var bwtUsageProfile: LiveData<ArrayList<sukriti.ngo.mis.ui.complexes.bwtData.UsageProfile>> =
        _bwtUsage
    var bwtReset: LiveData<ArrayList<sukriti.ngo.mis.ui.complexes.bwtData.ResetProfile>> = _bwtReset
    var bwtConfig: LiveData<BwtConfig> = _bwtConfig

    companion object {
        const val NAV_STATUS = 1
        const val NAV_HEALTH = 2
        const val NAV_SETTINGS = 3
        const val NAV_PROFILE = 4
        const val NAV_COMMANDS = 5

        const val SETTINGS_NAV_UCEMS = 10
        const val SETTINGS_NAV_CMS = 11
        const val SETTINGS_NAV_ODS = 12
        const val SETTINGS_NAV_BWT = 13
        const val SETTINGS_NAV_SHARED_DATA = 14

        const val PROFILE_NAV_USAGE = 20
        const val PROFILE_NAV_BWT = 21
        const val PROFILE_NAV_RESET = 22
        const val PROFILE_NAV_UPI_COLLECTION = 23
        const val PROFILE_NAV_BWT_USAGE = 24

    }

    init {
        AuthenticationClient.init(context)
        sharedPrefsClient = SharedPrefsClient(context)
        provisioningClient = AWSIotProvisioningClient(context)
        cabinDetailsLambdaClient = CabinDetailsLambdaClient(context)
        administrationClient = AdministrationClient()
        dynamoDbClient = AdministrationDbHelper(context)
    }

    fun getSelectedCabinAndComplex(fragmentCallback: CabinComplexSelectionHandler) {
        val dataRepository = DataRepository(context)

        val latestAccessedCabinHandler: LatestAccessedCabinHandler =
            object : LatestAccessedCabinHandler {
                override fun getLatestAccessedCabin(data: Cabin?) {
                    if (data != null) {
                        selectedCabin = data
                    }

                    fragmentCallback.onSelection(selectedComplex, selectedCabin)
                }

            }

        var latestAccessedComplexHandler: LatestAccessedComplexHandler =
            object : LatestAccessedComplexHandler {
                override fun getLatestAccessedComplex(data: Complex?) {
                    if (data != null) {
                        selectedComplex = data
                    }
                    dataRepository.getLatestAccessedCabin(context, latestAccessedCabinHandler)
                }
            }

        dataRepository.getLatestAccessedComplex(context, latestAccessedComplexHandler)
    }

    fun publishPayload(
        context: Context,
        topic: String,
        payload: JSONObject,
        callback: SimpleHandler
    ) {
        CommunicationUtilities().publishPayload(context, topic, payload, callback)
    }

    //Publish
    fun publishCommand(request: IotPublishLambdaRequest, handler: SimpleHandler) {
        val lambdaClient = LambdaClient(context)
        Log.i("__ExecuteLambda", "publishCommand()")
        Log.i("__submit", "publishCommand()")

        val lambdaRequestHandler: IotPublishRequestHandler = object :
            IotPublishRequestHandler {
            override fun onSuccess(result: IotPublishLambdaResult?) {
                handler.onSuccess()
            }

            override fun onError(ErrorMsg: String?) {
                Log.i("__ExecuteLambda", ErrorMsg)
                handler.onError(ErrorMsg)
            }
        }
        Log.i("__submit", "executing publish command lambda")
        lambdaClient.ExecutePublishCommandLambda(request, lambdaRequestHandler)
    }

    fun publishConfig(request: IotPublishLambdaRequest, handler: SimpleHandler) {
        val lambdaClient = LambdaClient(context)
        Log.i("__ExecuteLambda", "publishCommand()")

        val lambdaRequestHandler: IotPublishRequestHandler = object :
            IotPublishRequestHandler {
            override fun onSuccess(result: IotPublishLambdaResult?) {
                handler.onSuccess()
            }

            override fun onError(ErrorMsg: String?) {
                Log.i("__ExecuteLambda", ErrorMsg)
                handler.onError(ErrorMsg)
            }
        }
        lambdaClient.ExecutePublishConfigLambda(request, lambdaRequestHandler)
    }

    fun publishGenericConfig(request: IotPublishLambdaRequest, handler: SimpleHandler) {
        var lambdaClient = LambdaClient(context)
        Log.i("__ExecuteLambda", "publishCommand()")

        var lambdaRequestHandler: IotPublishRequestHandler = object :
            IotPublishRequestHandler {
            override fun onSuccess(result: IotPublishLambdaResult?) {
                handler.onSuccess()
            }

            override fun onError(ErrorMsg: String?) {
                Log.i("__ExecuteLambda", ErrorMsg)
                handler.onError(ErrorMsg)
            }
        }
        lambdaClient.ExecutePublishGenericConfigLambda(request, lambdaRequestHandler)
    }

    fun getSelectedComplex(): Complex {
        return selectedComplex
    }

    fun getSelectedCabin(): Cabin {
        return selectedCabin
    }

    fun getCabinHealth(thingName: String, fragmentCallback: PropertiesListRequestHandler) {
        var cabin: Cabin = Cabin()
        var complex: Complex = Complex()

        var dataRepository = DataRepository(context)

        var requestHandler: LatestCabinHealthRequestHandler =
            object : LatestCabinHealthRequestHandler {
                override fun getData(data: Health?) {
                    var properties = arrayListOf<PropertyNameValueData>()
                    properties.add(
                        PropertyNameValueData(
                            "Air Dryer",
                            getStatusLabel(data?.airDryerHealth)
                        )
                    )
                    properties.add(
                        PropertyNameValueData(
                            "Choke",
                            getStatusLabel(data?.chokeHealth)
                        )
                    )
                    properties.add(PropertyNameValueData("Fan", getStatusLabel(data?.fanHealth)))
                    properties.add(
                        PropertyNameValueData(
                            "Floor Clean",
                            getStatusLabel(data?.floorCleanHealth)
                        )
                    )
                    properties.add(
                        PropertyNameValueData(
                            "Flush",
                            getStatusLabel(data?.flushHealth)
                        )
                    )
                    properties.add(
                        PropertyNameValueData(
                            "Light",
                            getStatusLabel(data?.lightHealth)
                        )
                    )
                    properties.add(PropertyNameValueData("Lock", getStatusLabel(data?.lockHealth)))
                    properties.add(PropertyNameValueData("ODS", getStatusLabel(data?.odsHealth)))
                    properties.add(PropertyNameValueData("Tap", getStatusLabel(data?.tapHealth)))
                    fragmentCallback.getData(properties, data?.DEVICE_TIMESTAMP)
                }

                override fun onError(message: String?) {
                    fragmentCallback.getData(ArrayList(), message)
                }
            }

        dataRepository.getCabinHealth(thingName, context, requestHandler)
    }

    fun getCabinBwtHealth(thingName: String, fragmentCallback: PropertiesListRequestHandler) {
        var cabin: Cabin = Cabin()
        var complex: Complex = Complex()

        var dataRepository = DataRepository(context)

        var requestHandler: LatestCabinBwtHealthRequestHandler =
            object : LatestCabinBwtHealthRequestHandler {

                override fun getData(data: BwtHealth?) {
                    var properties = arrayListOf<PropertyNameValueData>()
                    properties.add(
                        PropertyNameValueData(
                            "Filter",
                            getStatusLabel(data?.FilterHealth)
                        )
                    )
                    properties.add(
                        PropertyNameValueData(
                            "Alp Value",
                            getStatusLabel(data?.alpValueHealth)
                        )
                    )
                    properties.add(
                        PropertyNameValueData(
                            "Blower",
                            getStatusLabel(data?.blowerHealth)
                        )
                    )
                    properties.add(
                        PropertyNameValueData(
                            "Fail Safe",
                            getStatusLabel(data?.failSafeHealth)
                        )
                    )
                    properties.add(
                        PropertyNameValueData(
                            "Mp1 Value",
                            getStatusLabel(data?.mp1ValueHealth)
                        )
                    )
                    properties.add(
                        PropertyNameValueData(
                            "Mp2 Value",
                            getStatusLabel(data?.mp2ValueHealth)
                        )
                    )
                    properties.add(
                        PropertyNameValueData(
                            "Mp3 Value",
                            getStatusLabel(data?.mp3ValueHealth)
                        )
                    )
                    properties.add(
                        PropertyNameValueData(
                            "Mp4 Value",
                            getStatusLabel(data?.mp4ValueHealth)
                        )
                    )
                    properties.add(
                        PropertyNameValueData(
                            "Ozonator",
                            getStatusLabel(data?.ozonatorHealth)
                        )
                    )
                    properties.add(
                        PropertyNameValueData(
                            "Priming Value",
                            getStatusLabel(data?.primingValueHealth)
                        )
                    )
                    properties.add(PropertyNameValueData("Pump", getStatusLabel(data?.pumpHealth)))
                    fragmentCallback.getData(properties, data?.DEVICE_TIMESTAMP)
                }

                override fun onError(message: String?) {
                    fragmentCallback.getData(ArrayList(), message)
                }
            }

        dataRepository.getCabinBwtHealth(thingName, context, requestHandler)
    }

    fun getStatusLabel(statusStr: String?): String {
        //Log.i("_stats",""+statusStr)
        var status = statusStr?.toInt()
        when (status) {
            1 -> return "Feature is not installed"
            2 -> return "Fault detected in the unit"
            3 -> return "Unit working fine"
            0 -> return "Feature is not installed"
        }
        return "-$status-"
    }


    fun getCabinAqiLumen(thingName: String, fragmentCallback: LatestCabinAqiLumenRequestHandler) {
        var cabin: Cabin = Cabin()
        var complex: Complex = Complex()


        var dataRepository = DataRepository(context)
        dataRepository.getCabinAqiLumen(thingName, context, fragmentCallback)
    }

    fun getCabinUcemsConfig(thingName: String, fragmentCallback: PropertiesListRequestHandler) {
        var dataRepository = DataRepository(context)
        dataRepository.getCabinUcemsConfig(thingName, context, fragmentCallback)
    }

    fun getCabinCmsConfig(thingName: String, fragmentCallback: PropertiesListRequestHandler) {
        var dataRepository = DataRepository(context)
        dataRepository.getCabinCmsConfig(thingName, context, fragmentCallback)
    }

    fun getCabinOdsConfig(thingName: String, fragmentCallback: PropertiesListRequestHandler) {
        var dataRepository = DataRepository(context)
        dataRepository.getCabinOdsConfig(thingName, context, fragmentCallback)
    }

    fun getCabinBwtConfig(thingName: String, fragmentCallback: PropertiesListRequestHandler) {
        var dataRepository = DataRepository(context)
        dataRepository.getCabinBwtConfig(thingName, context, fragmentCallback)
    }

    fun getCabinShareConfig(thingName: String, fragmentCallback: PropertiesListRequestHandler) {
        var dataRepository = DataRepository(context)
        dataRepository.getCabinDataShareFields(thingName, context, fragmentCallback)
    }

    fun getDisplayUsageProfileForDays(
        complex: Complex,
        cabin: Cabin,
        duration: Int,
        handler: UsageProfileRequestHandler
    ) {
        var dataRepository = DataRepository(context)
        var dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.getDisplayUsageProfile(
            complex,
            cabin,
            context,
            dataDuration.from,
            dataDuration.to,
            handler
        )
    }

    fun getDisplayResetProfileForDays(
        complex: Complex,
        cabin: Cabin,
        duration: Int,
        handler: ResetProfileRequestHandler
    ) {
        var dataRepository = DataRepository(context)
        var dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.getDisplayResetProfile(
            complex,
            cabin,
            context,
            dataDuration.from,
            dataDuration.to,
            handler
        )
    }

    fun getDisplayBwtProfileForDays(
        complex: Complex,
        cabin: Cabin,
        duration: Int,
        handler: BwtProfileRequestHandler
    ) {
        var dataRepository = DataRepository(context)
        var dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.getDisplayBwtProfile(
            complex,
            cabin,
            context,
            dataDuration.from,
            dataDuration.to,
            handler
        )
    }

    //Quick Access
    fun hasQuickAccess(ThingName: String, handler: CheckQuickAccessRequestHandler) {
        var dataRepository = DataRepository(context)
        dataRepository.hasQuickAccess(context, ThingName, handler)
    }

    fun addQuickAccessItem(
        item: QuickAccess,
        handler: sukriti.ngo.mis.ui.administration.interfaces.RequestHandler
    ) {
        var dataRepository = DataRepository(context)
        item.markedForQuickAccessTimestamp = "" + Calendar.getInstance().timeInMillis
        dataRepository.insertData(item, context, handler)
    }

    fun removeQuickAccess(
        ThingName: String,
        handler: sukriti.ngo.mis.ui.administration.interfaces.RequestHandler
    ) {
        var dataRepository = DataRepository(context)
        dataRepository.deleteQuickAccess(context, ThingName, handler)
    }


    fun getCabinData(callback: RepositoryCallback<String>) {
        var userName = sharedPrefsClient.getUserDetails().user.userName
//        var request = BwtCabinDetailsLambdaRequest("HP0501_03092021_000_MWC_001")
        var request =
            CabinDetailsLambdaRequest(
                forCabin
            )
        Log.i("cabinDetails", "getCabinData cabin: " + forCabin)
        Log.i("cabinDetails", "getSelectedCabin cabin: " + getSelectedCabin().ThingName)

        var callback: RepositoryCallback<CabinDetailsLambdaResult> = RepositoryCallback { result ->
            Log.i("cabinDetails", "ExecuteCabinDetailsLambda: 5")
            Log.i("cabinDetails", "onComplete: 5 :" + Gson().toJson(result))
            if (result is _Result.Success) {
                var data = result.data
                Log.i(
                    "cabinDetails",
                    "onComplete: 6 :result success : " + Gson().toJson(result.data)
                )

//                val status = data.status
                val aqiLumen = data.aqiLumen
                val health = data.health
                val ucemsConfig = data.ucemsConfig
                val cmsConfig = data.cmsConfig
                val odsConfig = data.odsConfig
                val usageProfile = data.usageProfile
                val resetProfile = data.resetProfile
                val upiPaymentList = data.upiPaymentList
                val connectionStatus =data.liveStatusResult.data
//                val usageAndFeedback = data.usageAndFeedback


                _aqiLumen.postValue(aqiLumen)
                _health.postValue(health)
                _ucemsConfig.postValue(ucemsConfig)
                _cmsConfig.postValue(cmsConfig)
                _odsConfig.postValue(odsConfig)
                _usageProfile.postValue(usageProfile)
                _resetProfile.postValue(resetProfile)
                _upiPaymentList.postValue(upiPaymentList)
//                _usageAndFeedback.postValue(usageAndFeedback)

                ConnectionStatusService._connectionResponse.postValue(connectionResponse(connectionStatus))


                callback.onComplete(_Result.Success("Success"))
            } else {
                var err = result as _Result.Error<String>
                callback.onComplete(_Result.Error<String>(-1, err.message))
            }
        }
        Log.i("cabinDetails", "getDashboardData: 2")

        cabinDetailsLambdaClient.ExecuteCabinDetailsLambda(request, callback)
    }

    fun connectionResponse(status :ConnectionStatus): ConnectionResponse {
        var response=ConnectionResponse()

        response.timestamp= status.timestamp
        response.versionNumber = 0
        response.disconnectReason =status.DISCONNECT_REASON
        response.clientId = status.THING_NAME
        response.ipAddress =status.IP_ADDRESS
        if (status.CONNECTION_STATUS =="ONLINE"){
            response.eventType= "connected"
        }else if (status.CONNECTION_STATUS =="OFFLINE"){
            response.eventType="disconnected"
        }

        Log.i("connection.............", "connectionResponse: "+Gson().toJson(response))
        return response
    }
    fun getBwtCabinData(callback: RepositoryCallback<String>) {
        var userName = sharedPrefsClient.getUserDetails().user.userName
//        var request = BwtCabinDetailsLambdaRequest("HP0501_03092021_000_MWC_001")
        var request =
            BwtCabinDetailsLambdaRequest(
                forCabin
            )
        Log.i("cabinDetails", "getCabinData cabin: " + forCabin)
        Log.i("cabinDetails", "getSelectedCabin cabin: " + getSelectedCabin().ThingName)

        var callback: RepositoryCallback<BwtCabinDetailsLambdaResult> =
            RepositoryCallback { result ->
                Log.i("cabinDetails", "ExecuteCabinDetailsLambda: 5")
                Log.i("cabinDetails", "onComplete: 5 :" + Gson().toJson(result))
                if (result is _Result.Success) {
                    var data = result.data
                    Log.i(
                        "cabinDetails",
                        "onComplete: 6 :result success : " + Gson().toJson(result.data)
                    )

//                val status = data.status
                    val health = data.health
                    val bwtConfig = data.bwtConfig
                    val usageProfile = data.usageProfile
                    val resetProfile = data.resetProfile


                    _bwtHealth.postValue(health)
                    _bwtConfig.postValue(bwtConfig)
                    _bwtUsage.postValue(usageProfile)
                    _resetProfile.postValue(resetProfile)

                    callback.onComplete(_Result.Success("Success"))
                } else {
                    var err = result as _Result.Error<String>
                    callback.onComplete(_Result.Error<String>(-1, err.message))
                }
            }
        Log.i("cabinDetails", "getDashboardData: 2")

        cabinDetailsLambdaClient.ExecuteBwtCabinDetailsLambda(request, callback)
    }

    fun mutableDisplayBwtUsageprofile(
        bwtUsageProfiles: ArrayList<sukriti.ngo.mis.ui.complexes.bwtData.UsageProfile>,
        requestHandler: BwtProfileRequestHandler
    ) {
        Thread {
            val handler = Handler(context.mainLooper)
            val returnCallback: Runnable

            var displayBwtUsageProfiles: MutableList<DisplayBwtProfile> = mutableListOf()
            for (profile in bwtUsageProfiles) {
                try {
                    Log.i("cabinDetails", "mutableDisplayBwtUsageprofile: "+Gson().toJson(profile))
                    val bwtUsageProfile = sukriti.ngo.mis.repository.entity.BwtProfile(
                        0,
                        "0",
                        profile.CITY,
                        profile.CLIENT,
                        profile.COMPLEX,
                        profile.DEVICE_TIMESTAMP,
                        "",
                        profile.DISTRICT,
                        profile.SendToAws,
                        profile.SendToDevic,
                        profile.SHORT_THING_NAME,
                        profile.STATE,
                        profile.THING_NAME,
                        profile.ttl.toString(),
                        profile.version_code.toString(),
                        profile.waterRecycled,
                        profile.airBlowerRunTime,
                        profile.currentWaterLevel,
                        profile.filterState,
                        profile.ozonatorRunTime,
                        profile.tpRunTime,
                        profile.turbidityLevel,
                        profile.Characteristic,
                        ""
                    )

                    val displayBwtUsageProfile =
                        DisplayBwtProfile(
                            bwtUsageProfile,
                            profile.DEVICE_TIMESTAMP,
                            profile.TimeStamp
                        )
                    Log.i(
                        "cabinDetails",
                        "displayUsageProfile: " + Gson().toJson(displayBwtUsageProfile)
                    )
                    displayBwtUsageProfiles.add(displayBwtUsageProfile)
                } catch (e: Exception) {
                    Log.e("cabinDetails", "mutableDisplayUsageprofile: " + e.message)
                }
            }
            Log.i("cabinDetails", "usageProfile: " + Gson().toJson(displayBwtUsageProfiles))
            returnCallback = Runnable { requestHandler.getData(displayBwtUsageProfiles) }
            handler.post(returnCallback)
        }.start()

//        return displayUsageProfiles
    }

    fun mutableDisplayUsageprofile(
        usageProfiles: ArrayList<UsageProfile>,
        requestHandler: UsageProfileRequestHandler
    ) {
        Thread {
            val handler = Handler(context.mainLooper)
            val returnCallback: Runnable

            var displayUsageProfiles: MutableList<DisplayUsageProfile> = mutableListOf()
            for (profile in usageProfiles) {
                try {
                    val usageProfile = sukriti.ngo.mis.repository.entity.UsageProfile(
                        0,
                        "",
                        profile.Airdryer ?: "0",
                        profile.Amountcollected,
                        profile.Amountremaining,
                        profile.Characteristic,
                        profile.CITY,
                        profile.CLIENT,
                        profile.COMPLEX,
                        profile.DEVICE_TIMESTAMP,
                        profile.DISTRICT,
                        profile.Duration,
                        profile.END_TIME,
                        profile.Entrytype,
                        profile.Fantime,
                        profile.feedback,
                        profile.Floorclean,
                        profile.Fullflush,
                        profile.Lighttime,
                        profile.Manualflush,
                        profile.Miniflush,
                        profile.Preflush,
                        profile.RFID,
                        "",
                        "",
                        profile.SERVER_TIMESTAMP.toString(),
                        profile.SHORT_THING_NAME,
                        profile.START_TIME.toString(),
                        profile.STATE,
                        profile.THING_NAME,
                        profile.TimeStamp,
                        profile.ttl.toString(),
                        profile.version_code.toString()
                    )

                    val displayUsageProfile =
                        DisplayUsageProfile(
                            usageProfile,
                            profile.DEVICE_TIMESTAMP,
                            profile.TimeStamp
                        )
                    Log.i(
                        "cabinDetails",
                        "displayUsageProfile: " + Gson().toJson(displayUsageProfiles)
                    )
                    displayUsageProfiles.add(displayUsageProfile)
                } catch (e: Exception) {
                    Log.e("cabinDetails", "mutableDisplayUsageprofile: " + e.message)
                }
            }
            Log.i("cabinDetails", "usageProfile: " + Gson().toJson(displayUsageProfiles))
            returnCallback = Runnable { requestHandler.getData(displayUsageProfiles) }
            handler.post(returnCallback)
        }.start()

//        return displayUsageProfiles
    }

    fun mutableDisplayResetProfile(resetProfiles: ArrayList<ResetProfile>): MutableList<DisplayResetProfile>? {
        var displayResetProfiles: MutableList<DisplayResetProfile> = mutableListOf()

        for (profile in resetProfiles) {
            val resetProfile = sukriti.ngo.mis.repository.entity.ResetProfile(
                0,
                0,
                "",
                profile.BoardId,
                profile.Characteristic,
                profile.CITY,
                profile.CLIENT,
                profile.COMPLEX,
                profile.DEVICE_TIMESTAMP,
                profile.DISTRICT,
                profile.Resetsource,
                profile.SendToAws,
                profile.SendToDevic,
                profile.SHORT_THING_NAME,
                profile.STATE,
                profile.THING_NAME,
                profile.ttl.toString(),
                profile.version_code.toString()
            )
            val displayResetProfile = DisplayResetProfile(
                resetProfile,
                resetProfile.DEVICE_TIMESTAMP,
                resetProfile.DEVICE_TIMESTAMP
            )

            displayResetProfiles.add(displayResetProfile)
        }
        Log.i("cabinDetails", "resetProfiles: " + Gson().toJson(displayResetProfiles))
        return displayResetProfiles
    }

    fun toMutableUpiList(upiProfiles: ArrayList<UpiPaymentList>): MutableList<UpiPaymentList>? {
        var upiMutableList = mutableListOf<UpiPaymentList>()

        for (upiList in upiProfiles) {
            upiMutableList.add(upiList)
        }
        Log.i("cabinDetails", "toMutableUpiList: " + Gson().toJson(upiMutableList))

        return upiMutableList
    }

}
