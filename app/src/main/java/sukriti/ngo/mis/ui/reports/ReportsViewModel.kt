package sukriti.ngo.mis.ui.reports

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import sukriti.ngo.mis.dataModel.DataDuration
import sukriti.ngo.mis.dataModel.ValidationResult
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.dataModel.dynamo_db.Complex
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.dataModel.dynamo_db.UserAccess
import sukriti.ngo.mis.interfaces.AuthorisationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.repository.DataRepository
import sukriti.ngo.mis.repository.data.*
import sukriti.ngo.mis.repository.entity.Ticket
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData
import sukriti.ngo.mis.ui.administration.interfaces.ProvisioningTreeRequestHandler
import sukriti.ngo.mis.ui.administration.interfaces.UserAccessTreeRequestHandler
import sukriti.ngo.mis.ui.complexes.interfaces.BwtProfileRequestHandler
import sukriti.ngo.mis.ui.complexes.interfaces.ResetProfileRequestHandler
import sukriti.ngo.mis.ui.complexes.interfaces.UsageProfileRequestHandler
import sukriti.ngo.mis.ui.reports.PDF_ReportGeneration.SelectionStructure.StateStructure
import sukriti.ngo.mis.ui.reports.data.*
import sukriti.ngo.mis.ui.reports.data.Collection
import sukriti.ngo.mis.ui.reports.interfaces.ComplexUsageReportRequestHandler
import sukriti.ngo.mis.ui.reports.lambda.ReportLambdaClient
import sukriti.ngo.mis.ui.reports.lambda.report_fetchDateWaiseUsageData.ReportLambdaRequest
import sukriti.ngo.mis.ui.reports.lambda.report_fetchDateWaiseUsageData.ReportLambdaResult
import sukriti.ngo.mis.utils.*
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ReportsViewModel(application: Application) : AndroidViewModel(application) {


    private var context: Context = application.applicationContext
    private var sharedPrefsClient: SharedPrefsClient
    private var accessTreeClient: AccessTreeClient
    private var provisioningClient: AWSIotProvisioningClient
    private var administrationClient: AdministrationClient
    private var dynamoDbClient: AdministrationDbHelper
    private var selectedDuration: String =
        Nomenclature.getDurationLabels()[Nomenclature.DURATION_DEFAULT_SELECTION]
    private val tag = "_ComplexesVM"

    var duration = Nomenclature.DURATION_DEFAULT_SELECTION

    private var reportLambdaClient: ReportLambdaClient
    private var _UsagesReportData: MutableLiveData<MutableList<UsageReportData>> = MutableLiveData()
    public var usageReportData: MutableLiveData<MutableList<UsageReportData>> = _UsagesReportData

    var selectionState = ArrayList<StateStructure>()

    private var userDetails: MemberDetailsData = MemberDetailsData()
    private var complexList = arrayListOf<String>()
    private val _selectionTree = MutableLiveData<Country>()
    val selectionTree: LiveData<Country> = _selectionTree
    fun setSelectionTree(tree: Country) {
        _selectionTree.value = tree
    }

    fun getComplexList(): ArrayList<String> {
        return complexList
    }

    companion object {
        const val NAV_GRAPHICAL_REPORT = 1
        const val NAV_DATA_REPORT = 2
        const val NAV_RAW_DATA = 3
        const val NAV_CONFIG_LOG = 4
        const val NAV_COMMAND_LOG = 5
        const val NAV_SELECTION_TREE = 6
        const val NAV_DOWNLOAD_REPORT = 7

        const val ACTION_DOWNLOAD = 10
        const val ACTION_EMAIL = 11
        const val ACTION_DOWNLOAD_PDF = 12
        const val ACTION_EMAIL_PDF = 13


        val BOTHCHECKED=100
        val TOILETCHECKED=101
        val BWTCHECKED=102

    }

    init {
        AuthenticationClient.init(context)
        sharedPrefsClient = SharedPrefsClient(context)
        provisioningClient = AWSIotProvisioningClient(context)
        administrationClient = AdministrationClient()
        dynamoDbClient = AdministrationDbHelper(context)
        accessTreeClient = AccessTreeClient(
            context,
            provisioningClient,
            sharedPrefsClient,
            administrationClient,
            dynamoDbClient
        )
        reportLambdaClient = ReportLambdaClient(context)
        var empty = arrayListOf<String>()
    }


    fun setSelectedDuration(duration: String) {
        selectedDuration = duration
    }

    fun getSelectedDuration(): String {
        return selectedDuration
    }

    fun loadAccessTreeForUser(userName: String, fragmentCallback: ProvisioningTreeRequestHandler) {
        accessTreeClient.loadAccessTreeForUser(userName, fragmentCallback)
    }

/*
    fun loadAccessTreeForUser(userName: String, fragmentCallback: ProvisioningTreeRequestHandler) {
        var tag = "_getUserDetails"

        var provisioningTreeRequestHandler: ProvisioningTreeRequestHandler =
            object : ProvisioningTreeRequestHandler {
                override fun onSuccess(mCountry: Country?) {
                    if (mCountry != null) {
                        sharedPrefsClient.saveAccessTree(mCountry)
                        fragmentCallback.onSuccess(mCountry)
                    } else {
                        fragmentCallback.onError("Something went wrong, please try again.")
                    }
                }

                override fun onError(message: String?) {
                    fragmentCallback.onError(message)
                }
            }

        var userAccessTreeRequestHandler: UserAccessTreeRequestHandler = object :
            UserAccessTreeRequestHandler {
            override fun onSuccess(userAccess: UserAccess?) {

                userDetails.userAccess = userAccess?.permissions?.country
                Log.i("__definedUserAccess", Gson().toJson(userDetails))

                if (UserProfile.isClientSpecificRole(sharedPrefsClient.getUserDetails().role)) {
                    if (userDetails != null) {
                        getCompleteUserAccessTree(
                            userDetails.cognitoUser.client,
                            userDetails.cognitoUser.userName,
                            provisioningTreeRequestHandler
                        )
                    }
                } else {
                    if (userDetails != null) {
                        getCompleteUserAccessTree(
                            userDetails.cognitoUser.userName,
                            provisioningTreeRequestHandler
                        )
                    }
                }
            }

            override fun onError(message: String?) {
                fragmentCallback.onError(message)
            }
        }

        var dynamoDbAuthHandler: AuthorisationHandler = object : AuthorisationHandler {
            override fun onResult(Result: ValidationResult?) {
                if (Result != null) {
                    if (Result.isValidated) {
                        dynamoDbClient.getUserAccessTree(
                            userName,
                            userAccessTreeRequestHandler
                        )
                    } else {
                        fragmentCallback.onError(Result.message)
                    }
                }
            }
        }

        var cognitoUserDetailsRequestHandler: CognitoUserDetailsRequestHandler = object :
            CognitoUserDetailsRequestHandler {
            override fun onSuccess(user: CognitoUser?) {
                userDetails.cognitoUser = user
                if (!dynamoDbClient.hasValidAccessToken)
                    dynamoDbClient.Authorize(dynamoDbAuthHandler)
                else {
                    dynamoDbClient.getUserAccessTree(
                        userName,
                        userAccessTreeRequestHandler
                    )
                }
            }

            override fun onError(message: String?) {
                fragmentCallback.onError(message)
            }

        }

        administrationClient.getUserDetails(context, userName, cognitoUserDetailsRequestHandler)
    }
*/

    fun getCompleteUserAccessTree(userName: String, fragmentCallback: ProvisioningTreeRequestHandler) {
        lateinit var provisioningTree: Country
        val provisioningTreeTag = "__definedUserAccess"

        Log.i("__definedUserAccess", "loadCompleted from Root")

        Log.i(provisioningTreeTag, "userName: " + userName)

        val userAccessTreeRequestHandler: UserAccessTreeRequestHandler =
            object : UserAccessTreeRequestHandler {
                override fun onSuccess(userAccess: UserAccess?) {

                    Log.i(provisioningTreeTag, "userDetails: " + Gson().toJson(userDetails))
                    Log.i(provisioningTreeTag, "userAccess: " + Gson().toJson(userAccess))
                    Log.i(
                        provisioningTreeTag,
                        "provisioningTree: " + Gson().toJson(provisioningTree)
                    )

                    var accessTree = userAccess?.permissions?.country
                    if (accessTree != null) {
                        var completedAccessTree: Country
                        if (userDetails != null)
                            completedAccessTree = Utilities.getCompletedAccessTree(
                                provisioningTree,
                                accessTree,
                                userDetails.userAccess
                            )
                        else
                            completedAccessTree =
                                Utilities.getCompletedAccessTree(provisioningTree, accessTree, null)

                        Log.i(
                            provisioningTreeTag,
                            "completedAccessTree: " + Gson().toJson(completedAccessTree)
                        )
                        fragmentCallback.onSuccess(completedAccessTree)
                    } else {
                        fragmentCallback.onError("You do not have access defined for yourself. Please contact your admin to define your access, before you define access for your team member. ")
                    }
                }

                override fun onError(message: String?) {
                    fragmentCallback.onError(message)
                }

            }

        val provisioningTreeRequestHandler: ProvisioningTreeRequestHandler =
            object : ProvisioningTreeRequestHandler {

                var dynamoDbAuthHandler: AuthorisationHandler = object : AuthorisationHandler {
                    override fun onResult(Result: ValidationResult?) {
                        if (Result != null) {
                            if (Result.isValidated) {
                                dynamoDbClient.getUserAccessTree(
                                    userName,
                                    userAccessTreeRequestHandler
                                )
                            } else {
                                fragmentCallback.onError(Result.message)
                            }
                        }
                    }
                }

                override fun onSuccess(mCountry: Country?) {
                    if (mCountry != null) {
                        provisioningTree = mCountry

                        if (!dynamoDbClient.hasValidAccessToken)
                            dynamoDbClient.Authorize(dynamoDbAuthHandler)
                        else {
                            dynamoDbClient.getUserAccessTree(
                                userName,
                                userAccessTreeRequestHandler
                            )
                        }
                    }
                }

                override fun onError(message: String?) {
                    fragmentCallback.onError(message)
                }
            }

        val provisioningClientAuthHandler: AuthorisationHandler = object : AuthorisationHandler {
            override fun onResult(Result: ValidationResult?) {
                if (Result != null) {
                    if (Result.isValidated) {
                        provisioningClient.getProvisioningTree(provisioningTreeRequestHandler)
                    } else {
                        fragmentCallback.onError(Result.message)
                    }
                }
            }
        }

        if (provisioningClient.hasValidAccessToken)
            provisioningClient.getProvisioningTree(provisioningTreeRequestHandler)
        else
            provisioningClient.Authorize(provisioningClientAuthHandler)

    }

    fun getCompleteUserAccessTree(clientCode: String, userName: String, fragmentCallback: ProvisioningTreeRequestHandler) {
        lateinit var provisioningTree: Country
        var provisioningTreeTag = "TreeTag"

        Log.i("__definedUserAccess", "loadCompleted for client $clientCode")

        Log.i(provisioningTreeTag, "userName: " + userName)
        var userAccessTreeRequestHandler: UserAccessTreeRequestHandler =
            object : UserAccessTreeRequestHandler {
                override fun onSuccess(userAccess: UserAccess?) {
                    Log.i(
                        "CompletedAccessTree",
                        "getCompletedAccessTree()"
                    )
                    var accessTree = userAccess?.permissions?.country
                    if (accessTree != null) {
                        var completedAccessTree: Country
                        if (userDetails != null)
                            completedAccessTree = Utilities.getCompletedAccessTree(
                                provisioningTree,
                                accessTree,
                                userDetails.userAccess
                            )
                        else
                            completedAccessTree =
                                Utilities.getCompletedAccessTree(provisioningTree, accessTree, null)
                        fragmentCallback.onSuccess(completedAccessTree)
                    } else {
                        fragmentCallback.onError("You do not have access defined for yourself. Please contact your admin to define your access, before you define access for your team member. ")
                    }
                }

                override fun onError(message: String?) {
                    fragmentCallback.onError(message)
                }

            }

        var provisioningTreeRequestHandler: ProvisioningTreeRequestHandler =
            object : ProvisioningTreeRequestHandler {

                var dynamoDbAuthHandler: AuthorisationHandler = object : AuthorisationHandler {
                    override fun onResult(Result: ValidationResult?) {
                        if (Result != null) {
                            if (Result.isValidated) {
                                dynamoDbClient.getUserAccessTree(
                                    userName,
                                    userAccessTreeRequestHandler
                                )
                            } else {
                                fragmentCallback.onError(Result.message)
                            }
                        }
                    }
                }

                override fun onSuccess(mCountry: Country?) {
                    if (mCountry != null) {
                        provisioningTree = mCountry
                        if (!dynamoDbClient.hasValidAccessToken)
                            dynamoDbClient.Authorize(dynamoDbAuthHandler)
                        else {
                            dynamoDbClient.getUserAccessTree(
                                userName,
                                userAccessTreeRequestHandler
                            )
                        }
                    }
                }

                override fun onError(message: String?) {
                    fragmentCallback.onError(message)
                }
            }

        val provisioningClientAuthHandler: AuthorisationHandler = object : AuthorisationHandler {
            override fun onResult(Result: ValidationResult?) {
                if (Result != null) {
                    if (Result.isValidated) {
                        provisioningClient.getClientProvisioningTree(
                            provisioningTreeRequestHandler,
                            clientCode
                        )
                    } else {
                        fragmentCallback.onError(Result.message)
                    }
                }
            }
        }

        if (provisioningClient.hasValidAccessToken)
            provisioningClient.getClientProvisioningTree(provisioningTreeRequestHandler, clientCode)
        else
            provisioningClient.Authorize(provisioningClientAuthHandler)
    }

    //Reports
    fun getProfileReportForDays(selectionData: SelectionData, duration: Int, handler: UsageProfileRequestHandler) {

        Log.i("_selection", Gson().toJson(selectionData))

        val dataRepository = DataRepository(context)
        val dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.getUsageReport(
            context,
            selectionData.states,
            selectionData.districts,
            selectionData.cities,
            selectionData.complexes,
            dataDuration.from,
            dataDuration.to,
            handler
        )
    }

    fun getUsageReportForComplexes(dataDuration: DataDuration, complexes: List<Complex>, handler: ComplexUsageReportRequestHandler) {

        Log.i("__UsageReport", "" + Gson().toJson(dataDuration))
        Log.i("__UsageReport", "" + Gson().toJson(complexes))

        val dataRepository = DataRepository(context)
        dataRepository.getUsageReportComplexWise(
            context,
            complexes,
            dataDuration.from,
            dataDuration.to,
            dataDuration.label,
            handler
        )
    }

    fun getResetReportForDays(
        selectionData: SelectionData,
        duration: Int,
        handler: ResetProfileRequestHandler
    ) {
        var dataRepository = DataRepository(context)
        var dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.getResetReport(
            context,
            selectionData.states,
            selectionData.districts,
            selectionData.cities,
            selectionData.complexes,
            dataDuration.from,
            dataDuration.to,
            handler
        )
    }

    fun getBwtReportForDays(
        selectionData: SelectionData,
        duration: Int,
        handler: BwtProfileRequestHandler
    ) {
        var dataRepository = DataRepository(context)
        var dataDuration = Nomenclature.getDataDuration(duration)
        dataRepository.getBwtReport(
            context,
            selectionData.states,
            selectionData.districts,
            selectionData.cities,
            selectionData.complexes,
            dataDuration.from,
            dataDuration.to,
            handler
        )
    }

    fun createFile(context: Fragment, Action: Int, fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/*"
        intent.putExtra(Intent.EXTRA_TITLE, fileName)


        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        context.startActivityForResult(intent, Action)
    }

    //    rahul karn
    fun chooseFileToMail(context: Fragment, Action: Int) {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        context.startActivityForResult(Intent.createChooser(intent, "Select pdf.."), Action)

    }
//    *****

    fun handleResult(
        activity: Activity,
        fileData: String,
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        Log.i("_download", "requestCode: $requestCode")

        if (requestCode == ACTION_DOWNLOAD) {
            var uri: Uri? = data?.data
            if (data != null && uri != null) {
                writeToFile(activity, uri, fileData)
            }
        }

        if (requestCode == ACTION_EMAIL || requestCode == ACTION_EMAIL_PDF) {
            var uri: Uri? = data?.data
            Log.i("_download", "pre-addEmailAttachment: $uri")

            if (data != null && uri != null) {
//                writeToFile(activity,uri,fileData)
                addEmailAttachment(activity, uri)
            }
        }
    }

    private fun writeToFile(activity: Activity, uri: Uri, fileData: String) {
        try {
            val pfd =
                activity.contentResolver.openFileDescriptor(uri!!, "w")
            val fileOutputStream = FileOutputStream(pfd!!.fileDescriptor)
            fileOutputStream.write(fileData.toByteArray())
            // Let the document provider know you're done by closing the stream.
            fileOutputStream.close()
            pfd!!.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun addEmailAttachment(activity: Activity, uri: Uri) {

        Log.i("_download", "addEmailAttachment")

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "application/pdf"
//        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@example.com"))
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject here")
//        emailIntent.putExtra(Intent.EXTRA_TEXT, "body text")
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
        emailIntent.setPackage("com.google.android.gm");
        activity.startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"))
    }

    fun getReportData(callback: RepositoryCallback<String>) {

        val userName = sharedPrefsClient.getUserDetails().user.userName
        val listOfComplex = getListOfComplex(selectionTree.value)
        Log.i("reportLambda", "getReportData: list of complex"+Gson().toJson(listOfComplex))
        Log.i("reportLambda", "getReportData: selected duration $duration")
        Log.i("reportLambda", "getReportData: username $userName")
        val request = ReportLambdaRequest(listOfComplex,duration.toString(), userName)
        Log.i("reportLambda", Gson().toJson(request))


        val lambdaCallback: RepositoryCallback<ReportLambdaResult> = RepositoryCallback { result ->
                Log.i("reportLambda", "ExecuteCabinDetailsLambda: 5")
                Log.i("reportLambda", "onComplete: 5 :" + Gson().toJson(result))
                if (result is _Result.Success) {
                    val data = result.data
                    val complexesReports = data.getComplexReports()
                    val usageReportData = usageReportData(complexesReports)
                    _UsagesReportData.postValue(usageReportData)
                    Log.i("reportLambda", "getReportData: " + Gson().toJson(complexesReports))
                    Log.i("reportLambda", "pieChart: " + Gson().toJson(complexesReports[0].pieChartData))
                    callback.onComplete(_Result.Success("Success"))
                } else {
                    val err = result as _Result.Error<String>
                    callback.onComplete(_Result.Error<String>(-1, err.message))
                }
            }
        Log.i("reportLambda", "getDashboardData: 2")
        reportLambdaClient.ExecuteReportLambda(request, lambdaCallback)


    }

    public fun getListOfComplex(accessTree: Country?): ArrayList<String> {
        val returnComplexesName: ArrayList<String> = ArrayList()
        val complexes = Utilities.getSelectedComplexes(accessTree)
        for ( complex in complexes){
            returnComplexesName.add(complex.name)
        }
        Log.i("selectionTree", Gson().toJson(returnComplexesName))
        complexList = returnComplexesName
        return returnComplexesName

    }




    private fun usageReportData(complexes: ArrayList<Root>): MutableList<UsageReportData> {
        val usageReportDataList: MutableList<UsageReportData> = mutableListOf()
        val dur = Nomenclature.getDataDuration(duration)

        Log.i("reportLambda", "usageReportData: " + Gson().toJson(complexes))

        for (data in complexes ) {
            Log.i("reportLambda", "Feedback: "+Gson().toJson(data))
            Log.i("reportLambda", "Feedback: "+Gson().toJson(data.dashboardChartData.feedback))
            var complex = Complex().apply { name = data.complexName }
            var usageData = ComplexWiseUsageData(
                dur.from,
                dur.to,
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList(),
                dur.label
            )
            var usageSummary = usageSummaryStats(data.pieChartData.usage)
            var usageComparison = usagesComparisonStats(data.dashboardChartData.usage)
            var usageTimeline = usagesComparisonStats(data.dashboardChartData.usage)
            var feedbackSummary = feedBackStatsData(data.pieChartData.feedback)
//            var feedbackComparison = feedbackSummaryData(data.pieChartData.feedback)
            var feedbackComparison = feedbackSummaryData(data.dashboardChartData.feedback)
            var feedbackDistribution = feedbackComparisonData(data.dashboardChartData.feedback)
            var feedbackTimeline = feedbackTimelineData(data.dashboardChartData.feedback)
            var collectionSummary = chargeCollectionStats(data.pieChartData.collection)
            var collectionComparison = collectionsComparisonStats(data.dashboardChartData.collection)
            var collectionTimeline = collectionsComparisonStats(data.dashboardChartData.collection)
            var upiCollectionSummary = upiChargeCollectionStats(data.pieChartData.upiCollection)
            var upiCollectionComparison = upiCollectionsComparisonStats(data.dashboardChartData.upiCollection)
            var upiCollectionTimeline = upiCollectionsComparisonStats(data.dashboardChartData.upiCollection)
            var bwtCollectionSummary = bwtChargeCollectionStats(data.bwtpieChartData.usage)
            var bwtCollectionComparison = bwtCollectionsComparisonStats(data.bwtdashboardChartData.waterRecycled)
            var bwtCollectionTimeline = bwtCollectionsComparisonStats(data.bwtdashboardChartData.waterRecycled)
            var ticketResolutionTimeline = TicketResolutionTimelineData(
                dur.from,
                dur.to,
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList(),
                emptyList(),
                dur.label
            )

            usageReportDataList.add(
                UsageReportData(
                    complex,
                    usageData,
                    usageSummary,
                    usageComparison,
                    usageTimeline,
                    feedbackSummary,
                    feedbackComparison,
                    feedbackDistribution,
                    feedbackTimeline,
                    collectionSummary,
                    collectionComparison,
                    collectionTimeline,
                    upiCollectionSummary,
                    upiCollectionComparison,
                    upiCollectionTimeline,
                    bwtCollectionSummary,
                    bwtCollectionComparison,
                    bwtCollectionTimeline,
                    ticketResolutionTimeline
                )
            )

        }
        return usageReportDataList
    }


    private fun usagesComparisonStats(usages: ArrayList<Usage>): UsageComparisonStats {
        var dataDuration = Nomenclature.getDataDuration(duration)
        var total: List<DailyUsageCount> = emptyList()
        var male: List<DailyUsageCount> = emptyList()
        var female: List<DailyUsageCount> = emptyList()
        var pd: List<DailyUsageCount> = emptyList()
        var mur: List<DailyUsageCount> = emptyList()
        for (data in usages) {
            Log.i("reportViewModel", "data: " + Gson().toJson(data))
            total += DailyUsageCount(data.date, data.all.toInt())
            male += DailyUsageCount(data.date, data.mwc.toInt())
            female += DailyUsageCount(data.date, data.fwc.toInt())
            pd += DailyUsageCount(data.date, data.pwc.toInt())
            mur += DailyUsageCount(data.date, data.mur.toInt())
        }

        var usagesComparisonStats = UsageComparisonStats(
            dataDuration.from, dataDuration.to,
            total, male, female, pd, mur, dataDuration.label
        )
        return usagesComparisonStats
    }

    private fun usageSummaryStats(pieChartUsages: ArrayList<Usage>): UsageSummaryStats {

        var duration = Nomenclature.getDataDuration(duration)

        var male = 0.0
        var female = 0.0
        var pwc = 0.0
        var mur = 0.0
        var total = 0.0

        for (data in pieChartUsages) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            when (data.name) {
                "MWC" -> male += data.value
                "FWC" -> female += data.value
                "PWC" -> pwc += data.value
                "MUR" -> mur += data.value
            }
        }


        total = male + female + pwc + mur
        Log.i(
            "dashboardLambda",
            "data: male :$male \nfemale :$female\npwc :$pwc\nmur :$mur\ntotal :$total"
        )

        var usageSummaryStats = UsageSummaryStats(
            duration.from, duration.to,
            total.toInt(), male.toInt(), female.toInt(), pwc.toInt(), mur.toInt(), duration.label
        )
        return usageSummaryStats
    }


    private fun feedBackStatsData(feedback: ArrayList<Feedback>): FeedbackStatsData {
        var dataDuration = Nomenclature.getDataDuration(duration)
        var pieChartFeedback = feedback
        var male = 0f
        var female = 0f
        var pwc = 0f
        var mur = 0f
        var total = 0f

        for (data in pieChartFeedback) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            when (data.name) {
                "MWC" -> male += data.value.toFloat()
                "FWC" -> female += data.value.toFloat()
                "PWC" -> pwc += data.value.toFloat()
                "MUR" -> mur += data.value.toFloat()
            }
        }
        total = (male + female + pwc + mur) / pieChartFeedback.size


        Log.i(
            "dashboardLambda",
            "data: male :$male \nfemale :$female\npwc :$pwc\nmur :$mur\ntotal :$total"
        )

        var feedBackStatsData = FeedbackStatsData(
            dataDuration.from, dataDuration.to,
            FeedbackSummary(total, 0, 0),
            FeedbackSummary(male, 0, 0),
            FeedbackSummary(female, 0, 0),
            FeedbackSummary(pwc, 0, 0),
            FeedbackSummary(mur, 0, 0),
            dataDuration.label
        )

        return feedBackStatsData
    }

    private fun feedbackSummaryData(feedback: ArrayList<Feedback>): FeedbackSummaryData {
        var dataDuration = Nomenclature.getDataDuration(duration)
        var pieChartFeedBack = feedback
        var male = 0.0
        var female = 0.0
        var pwc = 0.0
        var mur = 0.0
        var total = 0.0

        Log.i("feedbackSummaryData", "feedbackSummaryData 1: "+Gson().toJson(feedback))

        var lMale: List<FeedbackWiseUserCount> = emptyList()
        var lFemale: List<FeedbackWiseUserCount> = emptyList()
        var lPwc: List<FeedbackWiseUserCount> = emptyList()
        var lMur: List<FeedbackWiseUserCount> = emptyList()
        var lTotal: List<FeedbackWiseUserCount> = emptyList()
        Log.i(
            "feedbackSummaryData",
            "pieChartFeedBack: "+Gson().toJson(pieChartFeedBack)
        )
        for (data in pieChartFeedBack) {
            Log.i("feedbackSummaryData 2", "data: " + Gson().toJson(data))
              male += data.mwc
              female += data.fwc
              pwc += data.pwc
              mur += data.mur

        }

//        total = (male+female+pwc+mur)/pieChartFeedBack.size

        if (pieChartFeedBack.size>0) {
            lMale += FeedbackWiseUserCount(male.toInt() / pieChartFeedBack.size, 0)
            lFemale += FeedbackWiseUserCount(female.toInt() / pieChartFeedBack.size, 0)
            lPwc += FeedbackWiseUserCount(pwc.toInt() / pieChartFeedBack.size, 0)
            lMur += FeedbackWiseUserCount(mur.toInt() / pieChartFeedBack.size, 0)
        } else {
            lMale += FeedbackWiseUserCount(male.toInt() , 0)
            lFemale += FeedbackWiseUserCount(female.toInt() , 0)
            lPwc += FeedbackWiseUserCount(pwc.toInt() , 0)
            lMur += FeedbackWiseUserCount(mur.toInt() , 0)
        }
//    lTotal +=FeedbackWiseUserCount(total.toInt(),0)
        Log.i(
            "feedbackSummaryData",
            "data 3 : male :$male \nfemale :$female\npwc :$pwc\nmur :$mur\ntotal :$total"
        )

        var feedbackSummaryData = FeedbackSummaryData(
            dataDuration.from, dataDuration.to,
            lMale, lFemale, lPwc, lMur, dataDuration.label
        )

        Log.i("feedbackSummaryData", "feedbackSummaryData 4: "+Gson().to(feedbackSummaryData))
        return feedbackSummaryData
    }

    private fun feedbackComparisonData(feedback: ArrayList<Feedback>): FeedbackComparisonData {

        var dataDuration = Nomenclature.getDataDuration(duration)
        var feedback = feedback
        var total: List<DailyFeedbackCount> = emptyList()
        var mwc: List<DailyFeedbackCount> = emptyList()
        var fwc: List<DailyFeedbackCount> = emptyList()
        var pd: List<DailyFeedbackCount> = emptyList()
        var mur: List<DailyFeedbackCount> = emptyList()
        for (data in feedback) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            total += DailyFeedbackCount(data.date, 0, data.all.toFloat().toInt())
            mwc += DailyFeedbackCount(data.date, 0, data.mwc.toFloat().toInt())
            fwc += DailyFeedbackCount(data.date, 0.toInt(), data.fwc.toFloat().toInt())
            pd += DailyFeedbackCount(data.date, 0, data.pwc.toFloat().toInt())
            mur += DailyFeedbackCount(data.date, 0, data.mur.toFloat().toInt())
        }


        var feedbackComparisonData = FeedbackComparisonData(
            dataDuration.from, dataDuration.to,
            total, mwc, fwc, pd, mur, dataDuration.label
        )


        return feedbackComparisonData
    }

    private fun feedbackTimelineData(feedback: java.util.ArrayList<Feedback>): FeedbackTimelineData {
        var dataDuration = Nomenclature.getDataDuration(duration)
        var feedback = feedback
        var total: List<DailyAverageFeedback> = emptyList()
        var male: List<DailyAverageFeedback> = emptyList()
        var female: List<DailyAverageFeedback> = emptyList()
        var pd: List<DailyAverageFeedback> = emptyList()
        var mur: List<DailyAverageFeedback> = emptyList()
        for (data in feedback) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            total += DailyAverageFeedback(data.date, 0f, data.all.toFloat(), 0)
            male += DailyAverageFeedback(data.date, 0f, data.mwc.toFloat(), 0)
            female += DailyAverageFeedback(data.date, 0f, data.fwc.toFloat(), 0)
            pd += DailyAverageFeedback(data.date, 0f, data.pwc.toFloat(), 0)
            mur += DailyAverageFeedback(data.date, 0f, data.mur.toFloat(), 0)
        }

        Log.i(
            "dashboardLambda",
            "data: male :$male \nfemale :$female\npwc :$pd\nmur :$mur\ntotal :$total"
        )

        var feedbackTimelineData = FeedbackTimelineData(
            dataDuration.from, dataDuration.to,
            total, male, female, pd, mur, dataDuration.label
        )

        return feedbackTimelineData
    }

    private fun chargeCollectionStats(collection: java.util.ArrayList<sukriti.ngo.mis.ui.reports.data.Collection>): ChargeCollectionStats {
        var dataDuration = Nomenclature.getDataDuration(duration)
        var pieChartUsages = collection
        var male = 0.0
        var female = 0.0
        var pwc = 0.0
        var mur = 0.0
        var total = 0.0

        for (data in pieChartUsages) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            when (data.name) {
                "MWC" -> male += data.value
                "FWC" -> female += data.value
                "PWC" -> pwc += data.value
                "MUR" -> mur += data.value
            }
        }

        total = male + female + pwc + mur
        Log.i(
            "dashboardLambda",
            "data: male :$male \nfemale :$female\npwc :$pwc\nmur :$mur\ntotal :$total"
        )

        var chargeCollectionStats = ChargeCollectionStats(
            dataDuration.from,
            dataDuration.to,
            total.toFloat(),
            male.toFloat(),
            female.toFloat(),
            pwc.toFloat(),
            mur.toFloat(),
            dataDuration.label
        )

        return chargeCollectionStats
    }
    private fun upiChargeCollectionStats(collection: java.util.ArrayList<UpiCollection>): ChargeCollectionStats {
        var dataDuration = Nomenclature.getDataDuration(duration)
        var pieChartUsages = collection
        var male = 0.0
        var female = 0.0
        var pwc = 0.0
        var mur = 0.0
        var total = 0.0

        for (data in pieChartUsages) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            when (data.name) {
                "MWC" -> male += data.value
                "FWC" -> female += data.value
                "PWC" -> pwc += data.value
                "MUR" -> mur += data.value
            }
        }

        total = male + female + pwc + mur
        Log.i(
            "dashboardLambda",
            "data: male :$male \nfemale :$female\npwc :$pwc\nmur :$mur\ntotal :$total"
        )

        var chargeCollectionStats = ChargeCollectionStats(
            dataDuration.from,
            dataDuration.to,
            total.toFloat(),
            male.toFloat(),
            female.toFloat(),
            pwc.toFloat(),
            mur.toFloat(),
            dataDuration.label
        )

        return chargeCollectionStats
    }
    private fun bwtChargeCollectionStats(collection: java.util.ArrayList<Usage>): ChargeCollectionStats {
        var dataDuration = Nomenclature.getDataDuration(duration)
        var pieChartUsages = collection
        var bwt = 0.0
        var female = 0.0
        var pwc = 0.0
        var mur = 0.0
        var total = 0.0

        for (data in pieChartUsages) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            when (data.name) {
                "BWT" -> bwt += data.value
//                "FWC" -> female += data.value
//                "PWC" -> pwc += data.value
//                "MUR" -> mur += data.value
            }
        }

//        total = bwt
        Log.i(
            "dashboardLambda",
            "data: male :$bwt \nfemale :$female\npwc :$pwc\nmur :$mur\ntotal :$total"
        )

        var chargeCollectionStats = ChargeCollectionStats(
            dataDuration.from,
            dataDuration.to,
            total.toFloat(),
            bwt.toFloat(),
            female.toFloat(),
            pwc.toFloat(),
            mur.toFloat(),
            dataDuration.label
        )

        return chargeCollectionStats
    }

    private fun collectionsComparisonStats(collection: ArrayList<Collection>): DailyChargeCollectionData {
        var dataDuration = Nomenclature.getDataDuration(duration)
        var collections = collection
        var total: List<DailyChargeCollection> = emptyList()
        var male: List<DailyChargeCollection> = emptyList()
        var female: List<DailyChargeCollection> = emptyList()
        var pd: List<DailyChargeCollection> = emptyList()
        var mur: List<DailyChargeCollection> = emptyList()
        for (data in collections) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            total += DailyChargeCollection(data.date, data.all.toFloat())
            male += DailyChargeCollection(data.date, data.mwc.toFloat())
            female += DailyChargeCollection(data.date, data.fwc.toFloat())
            pd += DailyChargeCollection(data.date, data.pwc.toFloat())
            mur += DailyChargeCollection(data.date, data.mur.toFloat())
        }

        Log.i(
            "dashboardLambda",
            "data: male :$male \nfemale :$female\npwc :$pd\nmur :$mur\ntotal :$total"
        )

        var collectionsComparisonStats = DailyChargeCollectionData(
            dataDuration.from, dataDuration.to,
            total, male, female, pd, mur, dataDuration.label
        )

        return collectionsComparisonStats
    }

    private fun upiCollectionsComparisonStats(collection: java.util.ArrayList<UpiCollection>): DailyUpiCollectionData {
        var dataDuration = Nomenclature.getDataDuration(duration)
        var collections = collection
        var total: List<DailyChargeCollection> = emptyList()
        var male: List<DailyChargeCollection> = emptyList()
        var female: List<DailyChargeCollection> = emptyList()
        var pd: List<DailyChargeCollection> = emptyList()
        var mur: List<DailyChargeCollection> = emptyList()
        for (data in collections) {
            Log.i("dashboardLambda", "upi collections: " + Gson().toJson(data))
            total += DailyChargeCollection(data.date, data.all.toFloat())
            male += DailyChargeCollection(data.date, data.mwc.toFloat())
            female += DailyChargeCollection(data.date, data.fwc.toFloat())
            pd += DailyChargeCollection(data.date, data.pwc.toFloat())
            mur += DailyChargeCollection(data.date, data.mur.toFloat())
        }

        Log.i(
            "dashboardLambda",
            "upiCollectionsComparisonStats: male :$male \nfemale :$female\npwc :$pd\nmur :$mur\ntotal :$total"
        )

        var collectionsComparisonStats = DailyUpiCollectionData(
            dataDuration.from, dataDuration.to,
            total, male, female, pd, mur, dataDuration.label
        )

        return collectionsComparisonStats
    }

    private fun bwtCollectionsComparisonStats(collection: java.util.ArrayList<WaterRecycled>): DailyBwtCollectionData {
        var dataDuration = Nomenclature.getDataDuration(duration)
        var collections = collection
        var total: List<DailyChargeCollection> = emptyList()
        var waterRecycled: List<DailyChargeCollection> = emptyList()
        var female: List<DailyChargeCollection> = emptyList()
        var pd: List<DailyChargeCollection> = emptyList()
        var mur: List<DailyChargeCollection> = emptyList()
        for (data in collections) {
            Log.i("dashboardLambda", "upi collections: " + Gson().toJson(data))
            total += DailyChargeCollection(data.date, data.all.toFloat())
            waterRecycled += DailyChargeCollection(data.date, data.bwt.toFloat())
            female += DailyChargeCollection(data.date, 0f)
            pd += DailyChargeCollection(data.date, 0f)
            mur += DailyChargeCollection(data.date, 0f)
        }

        Log.i(
            "dashboardLambda",
            "upiCollectionsComparisonStats: male :$waterRecycled \nfemale :$female\npwc :$pd\nmur :$mur\ntotal :$total"
        )

        var collectionsComparisonStats = DailyBwtCollectionData(
            dataDuration.from, dataDuration.to,
            total, waterRecycled, female, pd, mur, dataDuration.label
        )

        return collectionsComparisonStats
    }


    //    Aug Qa: rahul karn
    //List All Ticket
    fun listRaisedTickets(callback: RepositoryCallback<List<Ticket>>) {
        var dataRepository = DataRepository(context)
        dataRepository.listRaisedTickets(Handler(context.mainLooper), callback, "All")
    }
    //list Resolved Tickets
    fun listAllResolvedTicket(callback: RepositoryCallback<List<Ticket>>) {
        val dataRepository = DataRepository(context)
        dataRepository.listClosedTickets(Handler(context.mainLooper), callback)
    }

}
