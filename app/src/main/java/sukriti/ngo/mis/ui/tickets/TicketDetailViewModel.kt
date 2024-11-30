package sukriti.ngo.mis.ui.tickets

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.amazonaws.AmazonClientException
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException
import com.amazonaws.regions.RegionUtils
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import sukriti.ngo.mis.AWSConfig
import sukriti.ngo.mis.AWSConfig.cognitoRegion
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.repository.entity.Ticket
import sukriti.ngo.mis.ui.tickets.data.TicketDetailsData
import sukriti.ngo.mis.ui.tickets.data.TicketFileItem
import sukriti.ngo.mis.ui.tickets.data.TicketProgressData
import sukriti.ngo.mis.ui.tickets.data.TicketTeamData
import sukriti.ngo.mis.ui.tickets.lambda.CreateTicket.CreateTicketLambdaRequest
import sukriti.ngo.mis.ui.tickets.lambda.ListProgress.ListTicketProgressLambdaRequest
import sukriti.ngo.mis.ui.tickets.lambda.ListTeam.ListTicketTeamLambdaRequest
import sukriti.ngo.mis.ui.tickets.lambda.TicketActions.TicketActionsLambdaRequest
import sukriti.ngo.mis.utils.*
import java.io.IOException
import java.net.URL
import java.util.*

class TicketDetailViewModel(application: Application) : AndroidViewModel(application) {

    private var context: Context = application.applicationContext
    private var sharedPrefsClient: SharedPrefsClient
    private var provisioningClient: AWSIotProvisioningClient
    private var administrationClient: AdministrationClient
    private var lambdaClient: TicketsLambdaClient
    private var dynamoDbClient: AdministrationDbHelper
    private var accessTreeClient: AccessTreeClient

    var selectedTicketData: Ticket = Ticket()

    companion object {
        //const val NAV_RAISE_NEW_TICKET = 0

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
    }

    fun getTicketProgressList(request: ListTicketProgressLambdaRequest, handler: RepositoryCallback<List<TicketProgressData>> ) {
        Log.i("__ExecuteLambda","getTicketProgressList()")
        lambdaClient.ExecuteListTicketProgressLambda(request, handler)
    }

    fun performTicketAction(request: TicketActionsLambdaRequest, handler: RepositoryCallback<TicketActionsLambdaRequest> ) {
        Log.i("__ExecuteLambda","getTicketProgressList()")
        lambdaClient.ExecutePerformTicketActionLambda(request, handler)
    }

    fun listTicketTeam(request: ListTicketTeamLambdaRequest, handler: RepositoryCallback<List<TicketTeamData>> ) {
        Log.i("__ExecuteLambda","getTicketProgressList()")
        lambdaClient.ExecuteListTicketTeamLambda(request, handler)
    }

    fun getTicketFiles(
        credentialsProvider: CognitoCachingCredentialsProvider,
        callback: RepositoryCallback<List<TicketFileItem>>,
        ticketId: String,
        formType: String

    ) {
        Thread(Runnable {
            val handler = Handler(context.mainLooper)
            var returnCallback: Runnable


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

                    val ticketFiles = s3Client.listObjectsV2(
                        AWSConfig.misTicketFilesBucketName,
                        "$ticketId/$formType"
                    )

                    Log.i("__ticketFiles","count: "+ticketFiles.keyCount);
                    val fileUrls = mutableListOf<TicketFileItem>()
                    var mTicketFileItem: TicketFileItem
                    if (ticketFiles.keyCount > 0) {
                        val expires = Date(Date().time + 15 * 1000 * 60) // 15 minutes to expire
                        var generatePresignedUrlRequest: GeneratePresignedUrlRequest
                        var url: URL

                        for (item in ticketFiles.objectSummaries) {
                             generatePresignedUrlRequest =
                                GeneratePresignedUrlRequest(
                                    AWSConfig.misTicketFilesBucketName,
                                    item.key
                                )
                            generatePresignedUrlRequest.expiration = expires
                            url = s3Client.generatePresignedUrl(generatePresignedUrlRequest)
                            mTicketFileItem = TicketFileItem(TicketFileItem._urlPhoto, Uri.parse(url.toString()),item.key)
                            fileUrls.add(mTicketFileItem)
                            Log.i("__ticketFiles","url: "+url.path);
                            Log.i("__ticketFiles", "url: $url");
                        }

                        returnCallback =
                            Runnable { callback.onComplete(_Result.Success<List<TicketFileItem>>(fileUrls)) }
                        handler.post(returnCallback)
                    }else{
                        returnCallback =
                            Runnable { callback.onComplete(_Result.Error<List<TicketFileItem>>(-100, "")) }
                        handler.post(returnCallback)
                    }


                } catch (e: LambdaFunctionException) {
                    e.printStackTrace()
                    returnCallback =
                        Runnable { callback.onComplete(_Result.Error<List<TicketFileItem>>(-2, e.message, e)) }
                    handler.post(returnCallback)
                } catch (e: NotAuthorizedException) {
                    e.printStackTrace()
                    returnCallback =
                        Runnable { callback.onComplete(_Result.Error<List<TicketFileItem>>(-3, e.message, e)) }
                    handler.post(returnCallback)
                } catch (e: AmazonClientException) {
                    e.printStackTrace()
                    returnCallback =
                        Runnable { callback.onComplete(_Result.Error<List<TicketFileItem>>(-4, e.message, e)) }
                    handler.post(returnCallback)
                } catch (e: IOException) {
                    e.printStackTrace()
                    returnCallback =
                        Runnable { callback.onComplete(_Result.Error<List<TicketFileItem>>(-5, e.message, e)) }
                    handler.post(returnCallback)
                }
            } else {
                returnCallback = Runnable {
                    callback.onComplete(
                        _Result.Error<List<TicketFileItem>>(
                            -1,
                            "AuthenticationClient->CurrSession is invalid"
                        )
                    )
                }
                handler.post(returnCallback)
            }

        }).start()
    }


}
