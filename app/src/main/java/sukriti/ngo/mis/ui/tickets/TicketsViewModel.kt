package sukriti.ngo.mis.ui.tickets

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazonaws.AmazonClientException
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtilityOptions
import com.amazonaws.regions.RegionUtils
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import io.reactivex.rxjava3.schedulers.Schedulers
import sukriti.ngo.mis.AWSConfig
import sukriti.ngo.mis.AWSConfig.cognitoRegion
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.repository.DataRepository
import sukriti.ngo.mis.repository.entity.Ticket
import sukriti.ngo.mis.ui.administration.interfaces.ProvisioningTreeRequestHandler
import sukriti.ngo.mis.ui.administration.interfaces.RequestHandler
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.ui.complexes.interfaces.ComplexDetailsRequestHandler
import sukriti.ngo.mis.ui.tickets.data.TicketDetailsData
import sukriti.ngo.mis.ui.tickets.data.TicketFileItem
import sukriti.ngo.mis.ui.tickets.data.TicketListData
import sukriti.ngo.mis.ui.tickets.fragments.viewList.TicketListNormal
import sukriti.ngo.mis.ui.tickets.lambda.CreateTicket.CreateTicketLambdaRequest
import sukriti.ngo.mis.ui.tickets.lambda.ListTicket.ListTicketsLambdaRequest
import sukriti.ngo.mis.utils.*
import sukriti.ngo.mis.utils.Nomenclature.TICKET_FILE_LIST_SIZE
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*

class TicketsViewModel(application: Application) : AndroidViewModel(application) {

    private var context: Context = application.applicationContext
    private var sharedPrefsClient: SharedPrefsClient
    private var provisioningClient: AWSIotProvisioningClient
    private var administrationClient: AdministrationClient
    private var lambdaClient: TicketsLambdaClient
    private var dynamoDbClient: AdministrationDbHelper
    private var accessTreeClient: AccessTreeClient
    private var dataRepository : DataRepository
    private val ticketFileList = arrayListOf<TicketFileItem>()
    private var ticketStatusSelection = Nomenclature.getTicketStatusList()[0]
    private val _ticketListData = MutableLiveData<TicketListData>()
    val ticketListData: LiveData<TicketListData> = _ticketListData

    var newTicketId = ""
    var selectedTicketData:Ticket = Ticket()

    companion object {
        const val NAV_TICKET_HOME = 100001 //Called from HomeActivity and HomeTicketList-Client,SA,etc
        const val NAV_RAISE_NEW_TICKET = 200001 //Called from HomeActivity and HomeTicketList-Client,SA,etc

        const val NAV_RAISED_TICKETS = 10
        const val NAV_QUEUED_TICKETS = 11
        const val NAV_UN_QUEUED_TICKETS = 12
        const val NAV_ASSIGNED_TICKETS = 13
        const val NAV_CLOSED_TICKET = 14
        const val NAV_TEAM_ASSIGNED_TICKET = 15
        const val NAV_ALL_ACTIVE_TICKET = 16

        const val NAV_ALL_UN_QUEUED_TICKETS = 21
        const val NAV_ALL_QUEUED_TICKETS = 22
        const val NAV_ALL_ASSIGNED_TICKETS = 23
        const val NAV_ALL_OPEN_TICKETS = 24
        const val NAV_ALL_RESOLVED_TICKETS = 25
        const val NAV_ALL_CLOSED_TICKET = 26

        const val NAV_TICKET_DETAILS = 101
        const val NAV_TICKET_PROGRESS = 102

        const val ACTION_PICK_TICKET_FILES = 1
    }

    init {
        AuthenticationClient.init(context)
        sharedPrefsClient = SharedPrefsClient(context)
        provisioningClient = AWSIotProvisioningClient(context)
        administrationClient = AdministrationClient()
        dynamoDbClient = AdministrationDbHelper(context)
        lambdaClient = TicketsLambdaClient(context)
        accessTreeClient = AccessTreeClient(
            context,
            provisioningClient,
            sharedPrefsClient,
            administrationClient,
            dynamoDbClient
        )
        dataRepository = DataRepository(context)
    }

    //HomeTicketList
    fun fetchTicketsFromServer(request: ListTicketsLambdaRequest, callback: RepositoryCallback<String>) {
        Log.i("__fetchTicketsServer","fetchTicketsFromServer()")
        var fetchTicketsData: TicketListData
        val handler = Handler(context.mainLooper)

        val resetBulkTicketStatusHandler: RequestHandler = object : RequestHandler{
            override fun onSuccess() {
                callback.onComplete(_Result.Success("Success"))
            }
            override fun onError(message: String?) {
                //Un-Reachable
                Log.i("_misTest", "resetBulkTicketStatusHandler: exp : $message")
                callback.onComplete(_Result.Error<String>(-1,message))
            }
        }

        val listTicketsRequestHandler: RepositoryCallback<TicketListData> = RepositoryCallback { result ->
                if(result is _Result.Success<TicketListData>){
                    fetchTicketsData = result.data
                    if(request.userRole == "Super Admin") {
                        //fetchTicketsData has list of unqueued tickets
                        dataRepository.resetBulkUnQueuedStatus(fetchTicketsData.unQueuedTicketData,handler,resetBulkTicketStatusHandler)
                    }else if(request.userRole == "Vendor Admin") {
                        //fetchTicketsData has list of queued tickets for user
                        dataRepository.resetBulkQueuedStatus(fetchTicketsData.queuedTickets,handler,resetBulkTicketStatusHandler)
                    }else{
                        callback.onComplete(_Result.Success("Success"))
                    }
                }else{
                    val err = result as _Result.Error<TicketListData>
                    Log.i("_misTest", "listTicketsRequestHandler: exp : "+err.exception +"  msg:"+err.message +"  code: "+err.errorCode)
                    callback.onComplete(_Result.Error<String>(-1,err.message))
                }
            }

        lambdaClient.ExecuteListTicketsLambda(request, listTicketsRequestHandler)
    }

    fun getTicketListData(request: Int,handler: Handler, callback: RepositoryCallback<List<Ticket>>) {
        Log.i("getTicketListData",""+ request)

        when(request) {
            NAV_RAISED_TICKETS -> {
                dataRepository.listRaisedTickets(handler,callback,ticketStatusSelection)
            }

            NAV_UN_QUEUED_TICKETS -> {
                dataRepository.listUnQueuedTickets(handler,callback)
            }

            NAV_QUEUED_TICKETS -> {
                dataRepository.listQueuedTickets(handler,callback)
            }

            NAV_CLOSED_TICKET -> {
                dataRepository.listClosedTickets(handler,callback)
            }

            NAV_ASSIGNED_TICKETS -> {
                dataRepository.listAssignedTickets(handler,callback,ticketStatusSelection)
            }

            NAV_TEAM_ASSIGNED_TICKET -> {
                dataRepository.listTeamAssignedTickets(handler,callback,ticketStatusSelection)
            }

            NAV_ALL_ACTIVE_TICKET -> {
                dataRepository.listAllActiveTickets(handler,callback,ticketStatusSelection)
            }

            NAV_ALL_UN_QUEUED_TICKETS ->{
                dataRepository.listAllTicketsWithStatus( Nomenclature.TICKET_STATUS_UNQUEUED, handler,callback)
            }

            NAV_ALL_QUEUED_TICKETS ->{
                dataRepository.listAllTicketsWithStatus( Nomenclature.TICKET_STATUS_QUEUED, handler,callback)
            }

            NAV_ALL_ASSIGNED_TICKETS ->{
                dataRepository.listAllTicketsWithStatus( Nomenclature.TICKET_STATUS_ASSIGNED, handler,callback)
            }

            NAV_ALL_OPEN_TICKETS ->{
                dataRepository.listAllTicketsWithStatus( Nomenclature.TICKET_STATUS_OPEN, handler,callback)
            }

            NAV_ALL_RESOLVED_TICKETS ->{
                dataRepository.listAllTicketsWithStatus( Nomenclature.TICKET_STATUS_RESOLVED, handler,callback)
            }

            NAV_ALL_CLOSED_TICKET ->{
                dataRepository.listAllTicketsWithStatus( Nomenclature.TICKET_STATUS_CLOSED, handler,callback)
            }

            else ->{
                callback.onComplete(_Result.Error<List<Ticket>>(-1,"None condition matched"))
            }
        }
    }

    //TicketListRaised
    fun getTicketStatusSelection(): String {
        return ticketStatusSelection
    }

    fun setTicketStatusSelection(status: String) {
        ticketStatusSelection = status
    }

    //Raise Ticket
    private val _selectedComplex = MutableLiveData<ComplexDetailsData>()

    val selectedComplex: LiveData<ComplexDetailsData> = _selectedComplex

    fun loadAccessTreeForUser(userName: String, fragmentCallback: ProvisioningTreeRequestHandler) {
        accessTreeClient.loadAccessTreeForUser(userName, fragmentCallback)
    }

    fun getComplexDetails(complexName: String, fragmentCallback: ComplexDetailsRequestHandler) {
        accessTreeClient.getComplexDetails(complexName, fragmentCallback)
    }


    fun setComplexRaiseTicket(complexDetails: ComplexDetailsData) {
        _selectedComplex.value = complexDetails
    }

    fun setTicketsData(ticketListData: TicketListData) {
        _ticketListData.value = ticketListData
    }

    fun addFile(fileItem: TicketFileItem): _Result<String> {
        var insertIndex = -1
        loop@ for ((index, fileItem) in ticketFileList.withIndex()) {
            if (fileItem.fileType == TicketFileItem.Companion.FileType.Empty) {
                insertIndex = index
                break@loop
            }
        }

        if (insertIndex != -1) {
            ticketFileList[insertIndex] = fileItem
            return _Result.Success("Inserted successfully.")
        }

        return _Result.Error(
            -1,
            "Maximum of $TICKET_FILE_LIST_SIZE files can only be added with a ticket."
        )
    }

    fun removeFile(index: Int) {
        ticketFileList[index] =
            TicketFileItem(TicketFileItem.Companion.FileType.Empty, Uri.EMPTY, "")
    }

    fun getFileList(): ArrayList<TicketFileItem>? {
        if (ticketFileList.isEmpty()) {
            for (i in 1..TICKET_FILE_LIST_SIZE) {
                ticketFileList.add(
                    TicketFileItem(
                        TicketFileItem.Companion.FileType.Empty,
                        Uri.EMPTY,
                        "index: $i"
                    )
                )
            }
        }
        return ticketFileList
    }

    fun getFileListCount(): Int {
        var fileListCount = 0
        for ((index, fileItem) in ticketFileList.withIndex()) {
            if (fileItem.fileType != TicketFileItem.Companion.FileType.Empty) {
                fileListCount++
            }
        }
        return fileListCount
    }

    fun uploadTicketFiles(
        credentialsProvider: CognitoCachingCredentialsProvider,
        callback: RepositoryCallback<String>,
        formType:String
    ) {
        Thread(Runnable {
            val handler = Handler(context.mainLooper)
            var returnCallback: Runnable
            returnCallback =
                Runnable { callback.onComplete(_Result.Success<String>("initialized")) }

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    //val idToken = AuthenticationClient.getCurrSession().idToken.jwtToken
                    //val logins: MutableMap<String, String> = HashMap()
                    //logins[AWSConfig.providerName] = idToken
                    //credentialsProvider.logins = logins

                    val s3Client = AmazonS3Client(
                        credentialsProvider,
                        RegionUtils.getRegion(cognitoRegion.getName())
                    )

                    var ticketFolder = s3Client.listObjectsV2(AWSConfig.misTicketFilesBucketName,
                        "$newTicketId/"
                    )
                    if(ticketFolder.keyCount == 0){
                        //folder does not exist
                        createFolder(AWSConfig.misTicketFilesBucketName,newTicketId,s3Client)
                    }

                    val transferUtilityBuilder = TransferUtility.builder()
                    transferUtilityBuilder.s3Client(s3Client)
                    transferUtilityBuilder.defaultBucket(AWSConfig.misTicketFilesBucketName)
                    transferUtilityBuilder.context(context)
                    transferUtilityBuilder.transferUtilityOptions(TransferUtilityOptions())
                    var transferUtility = transferUtilityBuilder.build()


                    Log.i("_uploadTicketFiles", "size ${ticketFileList.size}")
                    val multiUploadHashMap = mutableMapOf<String,File>()
                    for((index,item) in ticketFileList.withIndex()){
                        var file = File(getPath(ticketFileList[0].uri))
                        var fileIndex = 100 + ticketFolder.keyCount + index
                        var fileName = formType+"-"+ticketFileList[0].fileType.name +"-"+ fileIndex
                        fileName = "$newTicketId/$fileName"
                        Log.i("_uploadTicketFiles", "file ${file.exists()} ${file.absoluteFile}")
                        multiUploadHashMap[fileName] = file
                    }
                    Log.i("_uploadTicketFiles", "size ${multiUploadHashMap.size}")


                    MultiUploaderS3Client(AWSConfig.misTicketFilesBucketName).uploadMultiple(multiUploadHashMap, transferUtility)
                        ?.subscribeOn(Schedulers.io())
                        ?.observeOn(Schedulers.io())
                        ?.subscribe {
                            Runnable { callback.onComplete(_Result.Success<String>("uploaded successfully")) }
                            handler.post(returnCallback)
                        }

//                    var file = File(getPath(ticketFileList[0].uri))
//                    var fileIndex = 100 + ticketFolder.keyCount
//                    var fileName = ticketFileList[0].fileType.name +"-"+ fileIndex
//
//                    Log.i("_uploadTicketFiles", "file ${file.exists()} ${file.absoluteFile}")
//
//                    var observer =  transferUtility.upload("$newTicketId/$fileName", file)
//
//                    observer.setTransferListener(object : TransferListener {
//                        override fun onStateChanged(id: Int, state: TransferState) {
//                            Log.i("_uploadTicketFiles", "id: $id state ${state.name}")
//
//                            Runnable { callback.onComplete(_Result.Success<String>("uploaded successfully")) }
//                            handler.post(returnCallback)
//                        }
//
//                        override fun onProgressChanged(
//                            id: Int,
//                            bytesCurrent: Long,
//                            bytesTotal: Long
//                        ) {
//                            val percentage = (bytesCurrent / bytesTotal * 100).toInt()
//                            Log.i("_uploadTicketFiles", "percentage: $percentage")
//                        }
//
//                        override fun onError(id: Int, ex: Exception) {
//                            Log.i("_uploadTicketFiles", "id: $id exeption: ${ex.message}")
//                        }
//                    })

//                    var objectsV2 = s3Client.listObjectsV2(AWSConfig.misTicketFilesBucketName,
//                        "$newTicketId/"
//                    )
//                    for(item in objectsV2.objectSummaries){
//                        Log.i("_uploadTicketFiles",""+item.key)
//                    }
                } catch (e: LambdaFunctionException) {
                    e.printStackTrace()
                    returnCallback =
                        Runnable { callback.onComplete(_Result.Error<String>(-2, e.message, e)) }
                    handler.post(returnCallback)
                } catch (e: NotAuthorizedException) {
                    e.printStackTrace()
                    returnCallback =
                        Runnable { callback.onComplete(_Result.Error<String>(-3, e.message, e)) }
                    handler.post(returnCallback)
                } catch (e: AmazonClientException) {
                    e.printStackTrace()
                    returnCallback =
                        Runnable { callback.onComplete(_Result.Error<String>(-4, e.message, e)) }
                    handler.post(returnCallback)
                } catch (e: IOException) {
                    e.printStackTrace()
                    returnCallback =
                        Runnable { callback.onComplete(_Result.Error<String>(-5, e.message, e)) }
                    handler.post(returnCallback)
                }
            } else {
                returnCallback = Runnable {
                    callback.onComplete(
                        _Result.Error<String>(
                            -1,
                            "AuthenticationClient->CurrSession is invalid"
                        )
                    )
                }
                handler.post(returnCallback)
            }

        }).start()
    }

    private fun createFolder(
        bucketName: String?,
        folderName: String,
        client: AmazonS3Client
    ) {
        val metadata = ObjectMetadata()
        metadata.contentLength = 0
        val emptyContent: InputStream = ByteArrayInputStream(ByteArray(0))
        val putObjectRequest = PutObjectRequest(
            bucketName,
            "$folderName/",
            emptyContent,
            metadata
        )
        client.putObject(putObjectRequest)
    }

    fun getPath(uri: Uri): String? {
        val projection =
            arrayOf<String>(MediaStore.Images.Media.DATA)
        val cursor: Cursor =
            context.contentResolver.query(uri, projection, null, null, null) ?: return null
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s: String = cursor.getString(column_index)
        cursor.close()
        return s
    }

    fun createTicket(request: CreateTicketLambdaRequest, handler: RepositoryCallback<String>) {
        Log.i("__ExecuteLambda","createTicket()")
        lambdaClient.ExecuteCreateTicketLambda(request, handler)
    }

}
