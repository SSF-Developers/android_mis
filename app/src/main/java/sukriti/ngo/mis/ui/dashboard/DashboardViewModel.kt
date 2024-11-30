package sukriti.ngo.mis.ui.dashboard

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import sukriti.ngo.mis.communication.SimpleHandler
import sukriti.ngo.mis.dataModel.DataDuration
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.interfaces.ClientNameListHandler
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.repository.DataRepository
import sukriti.ngo.mis.repository.data.ComplexHealthStats
import sukriti.ngo.mis.repository.data.DailyTicketEventSummary
import sukriti.ngo.mis.repository.entity.Ticket
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import sukriti.ngo.mis.ui.administration.interfaces.RequestHandler
import sukriti.ngo.mis.ui.complexes.lambda.CabinDetailsLambdaClient
import sukriti.ngo.mis.ui.complexes.lambda.Complex_fetchAccessTree.AccessTreeLambdaRequest
import sukriti.ngo.mis.ui.dashboard.data.*
import sukriti.ngo.mis.ui.dashboard.interfaces.*
import sukriti.ngo.mis.ui.dashboard.lambda.DashBoardLambdaClient
import sukriti.ngo.mis.ui.dashboard.lambda.Dashboard_fetchData.DashBoardLambdaRequest
import sukriti.ngo.mis.ui.dashboard.lambda.Dashboard_fetchData.DashBoardLambdaResult
import sukriti.ngo.mis.ui.dashboard.repository.lambda.aggregateData.CabinDataLambdaRequest
import sukriti.ngo.mis.ui.dashboard.repository.lambda.aggregateData.CabinDataLambdaResult
import sukriti.ngo.mis.ui.dashboard.repository.lambda.aggregateData.CabinDataRequestHandler
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.IotPublishLambdaRequest
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.IotPublishLambdaResult
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.IotPublishRequestHandler
import sukriti.ngo.mis.ui.login.data.UserProfile
import sukriti.ngo.mis.utils.*
import java.util.*
import kotlin.collections.ArrayList

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private var context: Context = application.applicationContext
    private var sharedPrefsClient: SharedPrefsClient
    private var lambdaClient: LambdaClient
    private lateinit var dashboardNavigationHandler: NavigationHandler
    private var provisioningClient: AWSIotProvisioningClient

    private var dashBoardLambdaClient: DashBoardLambdaClient

    //live data for all fragments

    private var _dashboardChartData: MutableLiveData<DashboardChartData> = MutableLiveData()
    private var _pieChartData: MutableLiveData<PieChartData> = MutableLiveData()
    private var _dataSummary: MutableLiveData<DataSummary> = MutableLiveData()
    private var _faultyComplexes: MutableLiveData<ArrayList<FaultyComplex>> = MutableLiveData()
    private var _connectionStatus: MutableLiveData<ArrayList<ConnectionStatus>> = MutableLiveData()
    private var _dis_Complexes: MutableLiveData<ArrayList<FaultyComplex>> = MutableLiveData()
    private var _lowWaterComplexes: MutableLiveData<ArrayList<LowWaterComplex>> = MutableLiveData()
    private var _activeTickets: MutableLiveData<ArrayList<ActiveTicket>> = MutableLiveData()
    private var _uiResult: MutableLiveData<UiResult> = MutableLiveData()
    private var _bwtDashboardChartData: MutableLiveData<BwtdashboardChartData> = MutableLiveData()
    private var _bwtdataSummary: MutableLiveData<BwtdataSummary> = MutableLiveData()
    private var _bwtpieChartData: MutableLiveData<BwtpieChartData> = MutableLiveData()
    private var _bottomNavEnable: MutableLiveData<Boolean> = MutableLiveData()
    private var _detailConnection :MutableLiveData<ConnectionStatus> = MutableLiveData()

    var dashboardChartData: LiveData<DashboardChartData> = _dashboardChartData
    var pieChartData: LiveData<PieChartData> = _pieChartData
    var dataSummary: LiveData<DataSummary> = _dataSummary
    var faultyComplexes: LiveData<ArrayList<FaultyComplex>> = _faultyComplexes
    var connectionStatus: LiveData<ArrayList<ConnectionStatus>> = _connectionStatus
    var dis_Complexes: LiveData<ArrayList<FaultyComplex>> = _dis_Complexes
    var lowWaterComplexes: LiveData<ArrayList<LowWaterComplex>> = _lowWaterComplexes
    var activeTickets: LiveData<ArrayList<ActiveTicket>> = _activeTickets
    var uiResult: LiveData<UiResult> = _uiResult
    var bwtdashboardChartData :LiveData<BwtdashboardChartData> =_bwtDashboardChartData
    var bwtdataSummary :LiveData<BwtdataSummary> =_bwtdataSummary
    var bwtpieChartData :LiveData<BwtpieChartData> =_bwtpieChartData
    var bottomNavEnable: LiveData<Boolean> = _bottomNavEnable
    var detailConnection :LiveData<ConnectionStatus> = _detailConnection



    var duration = 15


    //LIVE DATA
    private val _syncStatus = MutableLiveData<Boolean>()
    val syncStatus: LiveData<Boolean> = _syncStatus

    init {
        sharedPrefsClient = SharedPrefsClient(context)
        lambdaClient = LambdaClient(context)
        dashBoardLambdaClient = DashBoardLambdaClient(context)
        provisioningClient = AWSIotProvisioningClient(context)
    }


    companion object {
        const val NAV_HOME = 0
        const val NAV_COMPLEX = 1
        const val NAV_INCIDENTS = 2
        const val NAV_REPORTS = 3

        const val NAV_LOADING_SCREEN = 10
        const val NAV_DATA_SCREEN = 11

        const val NAV_USAGE_STATS = 100
        const val NAV_USAGE_DETAILS = 101
        const val NAV_FEEDBACK_STATS = 200
        const val NAV_FEEDBACK_DETAILS = 201
        const val NAV_COLLECTION_STATS = 300
        const val NAV_COLLECTION_DETAILS = 301
        const val NAV_UPI_COLLECTION_STATS = 350
        const val NAV_UPI_COLLECTION_DETAILS = 351
        const val NAV_COMPLEX_HEALTH_STATS = 400
        const val NAV_COMPLEX_CONNECTION_STATS = 401
        const val NAV_COMPLEXLIST_CONNECTION_STATS = 402
        const val NAV_WATER_LEVEL_STATS = 500
        const val NAV_FLIPPING_CARD = 600
        const val NAV_QUICK_ACCESS = 700
        const val NAV_QUICK_ACCESS_ACTIONS = 701
        const val NAV_QUICK_CONFIG = 800
        const val NAV_QUICK_CONFIG_USAGE_CHARGE = 801
        const val NAV_QUICK_CONFIG_FLUSH = 802
        const val NAV_QUICK_CONFIG_FLOOR_CLEAN = 803
        const val NAV_QUICK_CONFIG_LIGHT_FAN = 804
        const val NAV_QUICK_CONFIG_DATA_REQUEST = 805
        const val NAV_COMPLEX_ACTIVE_TICKETS = 900

        const val NAV_USAGE_DETAILS_SUMMARY = 1001
        const val NAV_USAGE_DETAILS_COMPARISON = 1002
        const val NAV_USAGE_DETAILS_TIMELINE = 1003

        const val NAV_FEEDBACK_DETAILS_SUMMARY = 2001
        const val NAV_FEEDBACK_DETAILS_COMPARISON = 2002
        const val NAV_FEEDBACK_DETAILS_TIMELINE = 2003

        const val NAV_COLLECTION_DETAILS_SUMMARY = 3001
        const val NAV_COLLECTION_DETAILS_COMPARISON = 3002
        const val NAV_COLLECTION_DETAILS_TIMELINE = 3003

        const val NAV_QUICK_CONFIG_LIGHT_AND_FAN_FAN = 4001
        const val NAV_QUICK_CONFIG_LIGHT_AND_FAN_LIGHT = 4002

        const val NAV_QUICK_CONFIG_FLUSH_FULL = 5001
        const val NAV_QUICK_CONFIG_FLUSH_MINI = 5002
        const val NAV_QUICK_CONFIG_FLUSH_PRE = 5003


        const val NAV_UPI_COLLECTION_DETAILS_SUMMARY = 6001
        const val NAV_UPI_COLLECTION_DETAILS_COMPARISON = 6002
        const val NAV_UPI_COLLECTION_DETAILS_TIMELINE = 6003


        const val NAV_BWT_DETAILS_SUMMARY = 7001
        const val NAV_BWT_DETAILS_COMPARISON =7002
        const val NAV_BWT_DETAILS_TIMELINE = 7003

        const val NAV_BWT_STATS = 7101
        const val NAV_BWT_DETAILS = 7102
    }

    fun setDashboardNavigationHandler(handler: NavigationHandler) {
        dashboardNavigationHandler = handler
    }

    fun getDashboardNavigationHandler(): NavigationHandler {
        return dashboardNavigationHandler
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun enqueuePublishWorker(context: Context?) {
        //ExistingWorkPolicy.KEEP
    }

    fun setSynInProgress(status: Boolean) {
        _syncStatus.value = status
    }

    fun getSynInProgress(): Boolean {
        return _syncStatus.value ?: false
    }

    //verify for administration access
    fun hasAdministrationAccess(): Boolean {
        when (sharedPrefsClient.getUserDetails().role) {
            UserProfile.Companion.UserRole.VendorManager -> {
                return false
            }
            UserProfile.Companion.UserRole.ClientManager -> {
                return false
            }
            UserProfile.Companion.UserRole.Undefined -> {
                return true
            }
            UserProfile.Companion.UserRole.SuperAdmin -> {
                return true
            }
            UserProfile.Companion.UserRole.VendorAdmin -> {
                return true
            }
            UserProfile.Companion.UserRole.ClientAdmin -> {
                return true
            }
            UserProfile.Companion.UserRole.ClientSuperAdmin -> {
                return true
            }
        }
    }

    fun getMisUserData(handler: RequestHandler) {
        Log.i("__ExecuteLambda", "getMisUserData()")
        val request = CabinDataLambdaRequest()
        request.timeStamp = Gson().toJson(sharedPrefsClient.getDbSyncStatus().lastTimestamp)
        request.clientCode = sharedPrefsClient.getUserDetails().organisation.client
        request.complexes = sharedPrefsClient.getAccessibleComplexList()
        request.userName = sharedPrefsClient.getUserDetails().user.userName
        request.requestTimeStamp = "" + Calendar.getInstance().timeInMillis

        val lambdaRequestHandler: CabinDataRequestHandler = object : CabinDataRequestHandler {
            override fun onSuccess(result: CabinDataLambdaResult?) {
                handler.onSuccess()
            }

            override fun onError(ErrorMsg: String?) {
                handler.onError(ErrorMsg)
            }
        }
        lambdaClient.ExecuteAggregateMisUserDataLambda(request, lambdaRequestHandler)
    }

    fun getMisVendorData(handler: RequestHandler) {
        Log.i("__ExecuteLambda", "getMisVendorData()")

        val request =
            CabinDataLambdaRequest()
        request.timeStamp = Gson().toJson(sharedPrefsClient.getDbSyncStatus().lastTimestamp)
        request.clientCode = sharedPrefsClient.getUserDetails().organisation.client
        request.complexes = sharedPrefsClient.getAccessibleComplexList()
        request.userName = sharedPrefsClient.getUserDetails().user.userName
        request.requestTimeStamp = "" + Calendar.getInstance().timeInMillis

        val lambdaRequestHandler: CabinDataRequestHandler = object :
            CabinDataRequestHandler {
            override fun onSuccess(result: CabinDataLambdaResult?) {
                handler.onSuccess()
            }

            override fun onError(ErrorMsg: String?) {
                Log.i("__ExecuteLambda", ErrorMsg)
                handler.onError(ErrorMsg)
            }
        }
        lambdaClient.ExecuteAggregateVendorUserDataLambda(request, lambdaRequestHandler)
    }

    fun truncateAllTables(handler: RequestHandler) {
        sharedPrefsClient.clearDbSyncStatus()
        val dataRepository = DataRepository(context)
        dataRepository.truncateAllTables(context, handler)
    }

    fun getAccessibleComplexList(viewModel: AdministrationViewModel, handler: AccessibleComplexRequestHandler) {
        if (UserProfile.isClientSpecificRole(sharedPrefsClient.getUserDetails().role)) {
            viewModel.getAccessibleComplexList(
                sharedPrefsClient.getUserDetails().organisation.client,
                sharedPrefsClient.getUserDetails().user.userName,
                handler
            )
        }
        else {
            viewModel.getAccessibleComplexList(
                sharedPrefsClient.getUserDetails().user.userName,
                handler
            )
        }
    } //


    //Usage //FragmentIllegalState-Fix
    fun countUsageForDays(
        duration: Int,
        handler: UsageSummaryRequestHandler,
        lifecycle: Lifecycle
    ) {
        val dataRepository = DataRepository(context)
        val dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.countUsage(
            context,
            dataDuration.from,
            dataDuration.to,
            dataDuration.label,
            handler,
            lifecycle
        )
    }

    fun getDataWithDuration(duration: Int,callback :RepositoryCallback<String>){
        this.duration = duration
        getDashboardData(callback)
    }

    fun getDataDuration(): DataDuration {
        return Nomenclature.getDataDuration(duration)
    }

    fun getUsageComparisonForDays(duration: Int, handler: UsageComparisonRequestHandler) {
        val dataRepository = DataRepository(context)
        val dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.getUsageComparisonData(
            context,
            dataDuration.from,
            dataDuration.to,
            dataDuration.label,
            handler
        )
    }

    fun getUsageTimelineForDays(duration: Int, handler: UsageComparisonRequestHandler) {
        val dataRepository = DataRepository(context)
        val dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.getUsageTimelineData(
            context,
            dataDuration.from,
            dataDuration.to,
            dataDuration.label,
            handler
        )
    }


    //Feedback
    fun countFeedbackSummaryForDays(duration: Int, handler: FeedbackStatsRequestHandler) {
        val dataRepository = DataRepository(context)
        val dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.countTotalFeedback(
            context,
            dataDuration.from,
            dataDuration.to,
            dataDuration.label,
            handler
        )
    }

    fun countUsersForFeedbackForDays(duration: Int, handler: FeedbackSummaryRequestHandler) {
        val dataRepository = DataRepository(context)
        val dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.countUsersForFeedback(
            context,
            dataDuration.from,
            dataDuration.to,
            dataDuration.label,
            handler
        )
    }

    fun countDailyUserCountForFeedbackForDays(
        duration: Int,
        handler: FeedbackComparisonRequestHandler
    ) {
        val dataRepository = DataRepository(context)
        val dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.countDailyUserCountForFeedback(
            context,
            dataDuration.from,
            dataDuration.to,
            dataDuration.label,
            handler
        )
    }

    fun getFeedbackTimelineForDays(duration: Int, handler: FeedbackTimelineRequestHandler) {
        val dataRepository = DataRepository(context)
        val dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.getFeedbackTimelineData(
            context,
            dataDuration.from,
            dataDuration.to,
            dataDuration.label,
            handler
        )
    }

    //UsageCharge
    fun getTotalUsageChargeCollectionForDays(
        duration: Int,
        handler: ChargeCollectionSummaryRequestHandler
    ) {
        val dataRepository = DataRepository(context)
        val dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.countTotalChargeCollection(
            context,
            dataDuration.from,
            dataDuration.to,
            dataDuration.label,
            handler
        )
    }

    fun getDailyUsageChargeCollectionComparisonForDays(
        duration: Int,
        handler: ChargeCollectionComparisonRequestHandler
    ) {
        val dataRepository = DataRepository(context)
        val dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.getDailyChargeCollection(
            context,
            dataDuration.from,
            dataDuration.to,
            dataDuration.label,
            handler
        )
    }

    fun getUsageChargeCollectionTimelineForDays(
        duration: Int,
        handler: ChargeCollectionComparisonRequestHandler
    ) {
        val dataRepository = DataRepository(context)
        val dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.getTimelineChargeCollection(
            context,
            dataDuration.from,
            dataDuration.to,
            dataDuration.label,
            handler
        )
    }

    //    Aug Qa: rahul karn
    //List All Ticket
    fun listAllTickets(callback: RepositoryCallback<List<Ticket>>) {
        val dataRepository = DataRepository(context)
        dataRepository.listAllTickets(Handler(context.mainLooper), callback)
    }

    //list Resolved Tickets
    fun listAllResolvedTicket(callback: RepositoryCallback<List<Ticket>>) {
        val dataRepository = DataRepository(context)
        dataRepository.listClosedTickets(Handler(context.mainLooper), callback)
    }
    //    ***********

    //Active Tickets
    fun listActiveTickets(callback: RepositoryCallback<List<Ticket>>) {
        val dataRepository = DataRepository(context)
        dataRepository.listAllActiveTickets(Handler(context.mainLooper), callback, "All")
    }

    //Tickets-Progress-Summary
    fun getDailyTicketEventSummary(
        duration: Int,
        callback: RepositoryCallback<List<DailyTicketEventSummary>>
    ) {
        val dataRepository = DataRepository(context)
        val dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.countDailyActiveTickets(
            context,
            dataDuration.from,
            dataDuration.to,
            dataDuration.label,
            callback
        )
    }


    //Health-Lock-Water
    fun listFaultyComplexes(handler: ComplexHealthRequestHandler) {
        val dataRepository = DataRepository(context)
        dataRepository.listFaultyComplexes(context, handler)
    }

    fun listComplexWaterLevelStatus(handler: ComplexWaterLevelRequestHandler) {
        val dataRepository = DataRepository(context)
        dataRepository.listComplexWaterLevelStatus(context, handler)
    }


    //Banner
    fun listRecycledWaterStatsForDays(duration: Int, handler: RecycledWaterRequestHandler) {
        val dataRepository = DataRepository(context)
        val dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.listRecycledWaterStats(
            context,
            dataDuration.from,
            dataDuration.to,
            dataDuration.label,
            handler
        )
    }

    //Quick Access
    fun listQuickAccessCabins(handler: QuickAccessRequestHandler) {
        val dataRepository = DataRepository(context)
        dataRepository.listQuickAccess(context, handler)
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
        lambdaClient.ExecutePublishGenericConfigLambda(request, lambdaRequestHandler)
    }

    fun getClientList(callback: ClientNameListHandler) {
        val mAccessTreeClient = AccessTreeClient(provisioningClient, sharedPrefsClient, context)
        mAccessTreeClient.getClientList(callback)
    }



    // get Data from lambda for dash board (aggregate data)
    private fun getDashboardData(callback: RepositoryCallback<String>) {
        val userName = sharedPrefsClient.getUserDetails().user.userName
        val request = DashBoardLambdaRequest("all", duration.toString(), userName)

        val callback: RepositoryCallback<DashBoardLambdaResult> = RepositoryCallback { result ->
            Log.i("dashboardLambda", "ExecuteCabinDetailsLambda: 5")
            Log.i("dashboardLambda", "onComplete: 5 :" + Gson().toJson(result))
            if (result is _Result.Success) {
                val data = result.data
                val dashboardChartData = data.dashboardChartData
                val pieChartData = data.pieChartData
                val dataSummary = data.dataSummary
                val faultyComplexes = data.faultyComplexes
                val connectionStatus =data.connectionStatus
                val lowWaterComplexes = data.lowWaterComplexes
                val activeTickets = data.activeTickets
                val uiResult = data.uiResult
                val bwtdashboardChartData = data.bwtdashboardChartData
                val bwtpieChartData = data.bwtpieChartData
                val bwtdataSummary = data.bwtdataSummary
                Log.i("dashboardLambda", "getDashboardData: "+Gson().toJson(dashboardChartData.upiCollection))
                Log.i("dashboardLambda", "getpiechart: "+Gson().toJson(dashboardChartData.upiCollection))
                Log.e("dashboardLambda", "getbwtpiechart: "+Gson().toJson(dashboardChartData.upiCollection))
                Log.e("dashboardLambda", "getbwtDataChart: "+Gson().toJson(dashboardChartData.upiCollection))
                Log.e("dashboardLambda", "getbwtSummarychart: "+Gson().toJson(dashboardChartData.upiCollection))

                _dashboardChartData.postValue(dashboardChartData)
                _pieChartData.postValue(pieChartData)
                _dataSummary.postValue(dataSummary)
                _faultyComplexes.postValue(faultyComplexes)
                _connectionStatus.postValue(connectionStatus)
                _lowWaterComplexes.postValue(lowWaterComplexes)
                _activeTickets.postValue(activeTickets)
                _uiResult.postValue(uiResult)
                _bwtDashboardChartData.postValue(bwtdashboardChartData)
                _bwtdataSummary.postValue(bwtdataSummary)
                _bwtpieChartData.postValue(bwtpieChartData)


                sharedPrefsClient.saveUiResult(uiResult)

                callback.onComplete(_Result.Success("Success"))
            }else{
                val err = result as _Result.Error<String>
                callback.onComplete(_Result.Error<String>(-1,err.message))
            }
        }
        Log.i("dashboardLambda", "getDashboardData: 2")
        dashBoardLambdaClient.ExecuteDashBoardLambda(request, callback)
    }

    // Refactor lambda to required ui
    fun getListOfTicket(fetchTickets: ArrayList<ActiveTicket>): List<Ticket> {
        var tickets: List<Ticket> = emptyList()
        for (t in fetchTickets) {
            tickets += Ticket(
                t.ticket_id,
                t.ticket_status,
                t.creator_role,
                t.creator_id,
                t.complex_name,
                t.state_name,
                t.district_name,
                t.city_name,
                t.state_code,
                t.district_code,
                t.city_code,
                t.client_name,
                t.title,
                t.description,
                t.criticality,
                t.yearMonthCode,
                t.timestamp.toLong(),
                "",
                "",
                "",
                "",
                "",
                false,
                false
            )
        }
        return tickets
    }

    // Refactor lambda to required ui
    fun getComplexHealthStatusByFaultyComplex(faultyComplexes: ArrayList<FaultyComplex>): MutableList<ComplexHealthStats> {
        val complexes: MutableList<ComplexHealthStats> = mutableListOf()

        for (f in faultyComplexes) {
            val complex = f.complex
            // Note : we share total cabin count in mwc  to not break the code structure and flow
            val complexHealthStats = ComplexHealthStats(
                complex.name, f.count,
                f.count, 0, 0,
                0, 0, 0,
                0, 0, 0
            )
            complexes.add(complexHealthStats)
        }

        Log.i("myComplexes", Gson().toJson(complexes))
        return complexes
    }


    fun setBottomNavEnable(b: Boolean) {
        _bottomNavEnable.postValue(b)
    }

    fun postDetailConnection(model: ConnectionStatus){
        _detailConnection.postValue(model)
    }


}















