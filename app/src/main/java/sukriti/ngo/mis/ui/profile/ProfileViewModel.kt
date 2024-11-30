package sukriti.ngo.mis.ui.profile

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import sukriti.ngo.mis.dataModel.CognitoUser
import sukriti.ngo.mis.dataModel.ValidationResult
import sukriti.ngo.mis.dataModel.dynamo_db.UserAccess
import sukriti.ngo.mis.interfaces.AuthorisationHandler
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData
import sukriti.ngo.mis.ui.administration.interfaces.*
import sukriti.ngo.mis.ui.profile.fragments.GetProfileDataResultHandler
import sukriti.ngo.mis.ui.profile.fragments.ProfileDataLambdaRequest
import sukriti.ngo.mis.ui.profile.fragments.UserData
import sukriti.ngo.mis.ui.profile.fragments.UserProfileDataLambdaResult
import sukriti.ngo.mis.ui.profile.interfaces.UserProfileRequestHandler
import sukriti.ngo.mis.utils.*

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var selectedUser : MemberDetailsData
    private var context: Context = application.applicationContext
    private var sharedPrefsClient: SharedPrefsClient
    private var provisioningClient: AWSIotProvisioningClient
    private var administrationClient: AdministrationClient
    private var accessTreeClient: AccessTreeClient
    private var dynamoDbClient: AdministrationDbHelper
    private var userDetails: MemberDetailsData = MemberDetailsData()

    private val tag = "_Profile"


    companion object {
        const val NAV_PROFILE_HOME = 0
        const val NAV_TAB_ORGANISATION = 1
        const val NAV_TAB_DETAILS = 2
        const val NAV_TAB_COMMUNICATION = 3
        private var profileData: UserData = UserData()

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
    }

    fun setUserDetails(details: MemberDetailsData){
        userDetails = details
    }

    fun getUserDetails(): MemberDetailsData{
        return userDetails
    }

    fun setProfileData(userData: UserData) {
        profileData = userData
    }
    fun getProfileData(): UserData {
        return profileData
    }

    fun loadAccessTreeForUser(userName: String, fragmentCallback: ProvisioningTreeRequestHandler) {
        Log.i("LambdaExecution", "viewModel class Method to load access tree");
        accessTreeClient.loadAccessTreeForUser(userName, fragmentCallback)
    }

    fun fetchUserDetails(userName: String, fragmentCallback: UserProfileRequestHandler) {
        var tag = "_getUserDetails"
        val userDetails = MemberDetailsData()
        val userAccessTreeRequestHandler : UserAccessTreeRequestHandler = object : UserAccessTreeRequestHandler{
            override fun onSuccess(userAccess: UserAccess?) {
                userDetails.userAccess = userAccess?.permissions?.country
                setUserDetails(userDetails)
                fragmentCallback.onSuccess(userDetails)
            }

            override fun onError(message: String?) {
                fragmentCallback.onError(message)
            }
        }

        val dynamoDbAuthHandler = AuthorisationHandler { Result ->
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

        val cognitoUserDetailsRequestHandler : CognitoUserDetailsRequestHandler = object : CognitoUserDetailsRequestHandler{
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

        administrationClient.getUserDetails(context,userName,cognitoUserDetailsRequestHandler)
    }

    fun fetchUserDetails(userName: String, fragmentCallback: GetProfileDataResultHandler) {

        val request = ProfileDataLambdaRequest("actionGetUserDetails", userName)
        val lambdaClient = LambdaClient(context)

        val profileRequestHandler : GetProfileDataResultHandler = object : GetProfileDataResultHandler{
            override fun onSuccess(result: UserProfileDataLambdaResult?) {
                if(result != null) {
                    if (result.status == 1) {
                        Log.i("myProfileData", "profileRequestHandler -> status = 1")
                        setProfileData(result.user)
                        Log.i("myProfileData", "view model profile data object updated")
                        Log.i("myProfileData", "calling fragmentCallback onSuccess() method")
                        fragmentCallback.onSuccess(result)
                    } else {
                        Log.i("myProfileData", "profileRequestHandler -> status = ${result.status}")
                    }
                }
            }

            override fun onError(errorMessage: String?) {
                fragmentCallback.onError(errorMessage)
            }

        }


            lambdaClient.ExecuteProfileDataLambda(request, profileRequestHandler)
    }


    }
