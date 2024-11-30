package sukriti.ngo.mis.ui.administration

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.amazonaws.services.cognitoidentityprovider.model.UserType
import com.google.gson.Gson
import sukriti.ngo.mis.dataModel.*
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.dataModel.dynamo_db.Team
import sukriti.ngo.mis.dataModel.dynamo_db.UserAccess
import sukriti.ngo.mis.interfaces.AuthorisationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.repository.utils.DateConverter.getTimeStamp
import sukriti.ngo.mis.ui.administration.data.*
import sukriti.ngo.mis.ui.administration.data.ValidationError
import sukriti.ngo.mis.ui.administration.interfaces.*
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.ClientList
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.ClientListLambdaResult
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.GetClientListResultHandler
import sukriti.ngo.mis.ui.administration.lambda.DefineAccessLambdaRequest
import sukriti.ngo.mis.ui.administration.lambda.DefineAccessLambdaResult
import sukriti.ngo.mis.ui.administration.lambda.DefineAccessRequestHandler
import sukriti.ngo.mis.ui.administration.lambda.DeleteUser.DeleteUserLambdaRequest
import sukriti.ngo.mis.ui.administration.lambda.listTeam.GetTeamLambdaRequest
import sukriti.ngo.mis.ui.administration.lambda.listTeam.GetTeamLambdaResult
import sukriti.ngo.mis.ui.dashboard.interfaces.AccessibleComplexRequestHandler
import sukriti.ngo.mis.ui.login.data.UserProfile
import sukriti.ngo.mis.ui.login.data.UserProfile.Companion.getRoleName
import sukriti.ngo.mis.utils.*
import sukriti.ngo.mis.utils.Utilities.*

class AdministrationViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var selectedUser: MemberDetailsData
    private var context: Context = application.applicationContext
    private var sharedPrefsClient: SharedPrefsClient
    private var provisioningClient: AWSIotProvisioningClient
    private var administrationClient: AdministrationClient
    private var dynamoDbClient: AdministrationDbHelper
    private var lambdaClient: LambdaClient
    private var _loadTeamListDataFlag = true
    public var accessTreeClient: AccessTreeClient

    private val tag = "_Administration"


    companion object {
        const val NAV_MANAGE_TEAM = 0
        const val NAV_DEFINE_ACCESS = 1
        const val NAV_MEMBER_DETAILS = 2
        const val NAV_CREATE_USER = 3
        const val NAV_GRANT_PERMISSION = 5
        const val NAV_VENDOR_DETAILS = 4

        const val TREE_NODE_STATE = 0
        const val TREE_NODE_DISTRICT = 1
        const val TREE_NODE_CITY = 2
        const val TREE_NODE_COMPLEX = 3

    }

    init {
        AuthenticationClient.init(context)
        sharedPrefsClient = SharedPrefsClient(context)
        provisioningClient = AWSIotProvisioningClient(context)
        administrationClient = AdministrationClient()
        dynamoDbClient = AdministrationDbHelper(context)
        lambdaClient = LambdaClient(context)
        accessTreeClient = AccessTreeClient(context, provisioningClient, sharedPrefsClient, administrationClient, dynamoDbClient)
    }

    fun setSelectedUser(memberDetailsData: MemberDetailsData) {
        selectedUser = setAllComplexesSelected(memberDetailsData)
    }

    fun getSelectedUser(): MemberDetailsData {
        return selectedUser
    }

    fun setLoadTeamListDataFlag(status: Boolean) {
        _loadTeamListDataFlag = status
    }

    fun getLoadTeamListDataFlag(): Boolean {
        return _loadTeamListDataFlag;
    }


    fun createUser(createUserRequest: CreateUserRequest, fragmentCallback: FormSubmitHandler) {
        val errorList = HashMap<ValidationError.Companion.FieldNames, ValidationError>()
        if (createUserRequest.UserName.isNullOrEmpty()) {
            errorList[ValidationError.Companion.FieldNames.UserName] =
                ValidationError("User Name Required. Please provide a User Name.")
        }
        else if (createUserRequest.UserName.length < 10) {
            errorList[ValidationError.Companion.FieldNames.UserName] =
                ValidationError("User Name Required. Please provide a User Name with at least 10 Characters..")
        }

        if (createUserRequest.Password.isNullOrEmpty()) {
            errorList[ValidationError.Companion.FieldNames.Password] =
                ValidationError("Password Required. Please provide a temporary password.")
        }
        else if (createUserRequest.Password.length < 10) {
            errorList[ValidationError.Companion.FieldNames.Password] =
                ValidationError("Password Required. Please provide a Password with at least 10 Characters..")
        }

        if (createUserRequest.Role == null) {
            errorList[ValidationError.Companion.FieldNames.Role] =
                ValidationError("Role Required. Please select a role.")
        }

        if (createUserRequest.ClientName.isNullOrEmpty()) {
            errorList[ValidationError.Companion.FieldNames.Client] =
                ValidationError("Client Selection Required. Please select a client.")
        }

        if (createUserRequest.OrganizationName.isNullOrEmpty()) {
            errorList[ValidationError.Companion.FieldNames.Organisation] =
                ValidationError("Organisation Selection Required. Please select a Organisation.")
        }

        val createUserCallback = object : AuthorisationHandler {
            override fun onResult(Result: ValidationResult?) {
                if (Result != null && Result.isValidated) {
                    fragmentCallback.onSuccess()
                } else {
                    fragmentCallback.onServerError(Result?.message)
                }
            }
        }

        val clientSuperAdminRequestHandler: ClientSuperAdminRequestHandler =
            object : ClientSuperAdminRequestHandler {
                override fun onSuccess(hasClientSuperAdmin: Boolean, user: UserType?) {
                    if (!hasClientSuperAdmin)
                        administrationClient.createUser(
                            context,
                            createUserRequest,
                            createUserCallback
                        )
                    else
                        fragmentCallback.onServerError(
                            "Client cannot have multiple Client Super Admins. This client already has a 'Client Super Admin' defined with " +
                                    "UserName: '" + user?.username + "', " +
                                    "Created On: " + user?.userCreateDate + " "
                        )
                }

                override fun onError(message: String?) {
                    fragmentCallback.onServerError(message)
                }

            }

        if (errorList.isEmpty()) {
            if (createUserRequest.Role == getRoleName(UserProfile.Companion.UserRole.ClientSuperAdmin)) {
                administrationClient.getClientSuperAdmin(
                    context,
                    createUserRequest.ClientName,
                    clientSuperAdminRequestHandler
                )
            } else {
                administrationClient.createUser(context, createUserRequest, createUserCallback)
            }
        } else
            fragmentCallback.onValidationError(errorList)
    }

    private fun createTeamMemberRequest(createUserRequest: CreateUserRequest): Team? {
        val teamMember = Team()
        teamMember.member = createUserRequest.UserName
        teamMember.admin = sharedPrefsClient.getUserDetails().user.userName
        teamMember.adminRole = UserProfile.getRoleName(sharedPrefsClient.getUserDetails().role)
        teamMember.memberRole = createUserRequest.Role
        teamMember.assignedBy = sharedPrefsClient.getUserDetails().user.userName
        teamMember.assignedOn = getTimeStamp()
        teamMember.assignmentType = "on_create"

        Log.i("_createUser", "teamMember.member: " + teamMember.member)
        Log.i("_createUser", "teamMember.admin: " + teamMember.admin)
        Log.i("_createUser", "teamMember.adminRole: " + teamMember.adminRole)
        Log.i("_createUser", "teamMember.memberRole: " + teamMember.memberRole)
        Log.i("_createUser", "teamMember.assignedBy: " + teamMember.assignedBy)
        Log.i("_createUser", "teamMember.assignedOn: " + teamMember.assignedOn)
        Log.i("_createUser", "teamMember.assignmentType: " + teamMember.assignmentType)


        return teamMember;
    }


    fun initProvisioningClient(callback: AuthorisationHandler) {

        provisioningClient.Authorize(callback)
    }

    fun getClientList(callback: GetClientListResultHandler) {
        var clientList: ArrayList<ClientList> = arrayListOf()
/*        val handler: GetThingGroupChildrenHandler = object : GetThingGroupChildrenHandler {
            override fun onResult(Parent: String?, List: ArrayList<ThingGroupDetails>?, Type: Int) {
                if (List != null) {
                    clientList = List
                    Log.i(tag, "" + List.size)
                    callback.onResult(List)
                }
            }

            override fun onError(message: String?) {
                callback.onError(message)
            }
        }*/

        var list: List<ClientList> = arrayListOf()

        val getClientListResultHandler: GetClientListResultHandler =
            object : GetClientListResultHandler {
                override fun onSuccess(result: ClientListLambdaResult?) {
                    if (result != null) {
                        list = result.list
                    } else {
                        Log.i("getClientList", "Result is empty")
                    }

                    Log.i("getClientList", "client list is not empty")
                    Log.i("getClientList", "isEmpty() ${list.isEmpty()}")
                    if (result != null) {
                        Log.i("getClientList", "Client list isEmpty() ${result.list.isEmpty()}")
                    } else {
                        Log.i("getClientList", "Result is empty")
                    }
                    if (result != null) {
                        for (client in result.list) {
                            Log.i("getClientList", "Name -> ${client.name}")
                            Log.i("getClientList", "Description -> ${client.description}")
                            Log.i("getClientList", "Creation Date -> ${client.creationDate}")
                            Log.i("getClientList", "Organization -> ${client.organization}")
                            Log.i("getClientList", "===========================================")
                        }
                    }

                    clientList = list as ArrayList<ClientList>

                    callback.onSuccess(result)
                }

                override fun onError(errorMessage: String?) {
                    Log.i("getClientList", "Something went wrong: $errorMessage")
                    callback.onError(errorMessage)
                }

            }
//        provisioningClient.getChildrenThingGroups(THING_GROUP_CLIENT_ROOT, handler)
        Log.i("getClientList", "executing lambda")
        lambdaClient.ExecuteGetClientListLambda(getClientListResultHandler)
    }


    fun getCreateRoleList(): MutableList<String> {
        val role = sharedPrefsClient.getUserDetails().role
        var roleList: MutableList<String> = arrayListOf()

        if (role == UserProfile.Companion.UserRole.SuperAdmin) {
            roleList = mutableListOf(
                getRoleName(UserProfile.Companion.UserRole.ClientSuperAdmin),
                getRoleName(UserProfile.Companion.UserRole.VendorAdmin)
            )
        } else if (role == UserProfile.Companion.UserRole.ClientSuperAdmin) {
            roleList = mutableListOf(
                getRoleName(UserProfile.Companion.UserRole.ClientAdmin),
                getRoleName(UserProfile.Companion.UserRole.ClientManager)
            )
        } else if (role == UserProfile.Companion.UserRole.VendorAdmin) {
            roleList = mutableListOf(
                getRoleName(UserProfile.Companion.UserRole.ClientSuperAdmin),
                getRoleName(UserProfile.Companion.UserRole.VendorManager)
            )
        } else if (role == UserProfile.Companion.UserRole.ClientAdmin) {
            roleList = mutableListOf(getRoleName(UserProfile.Companion.UserRole.ClientManager))
        } else if (role == UserProfile.Companion.UserRole.ClientManager) {
            roleList = mutableListOf()
        } else if (role == UserProfile.Companion.UserRole.VendorManager) {
            roleList = mutableListOf()
        } else {
            //Developer
            roleList = mutableListOf(
                getRoleName(UserProfile.Companion.UserRole.SuperAdmin),
                getRoleName(UserProfile.Companion.UserRole.ClientSuperAdmin),
                getRoleName(UserProfile.Companion.UserRole.ClientAdmin),
                getRoleName(UserProfile.Companion.UserRole.VendorAdmin),
                getRoleName(UserProfile.Companion.UserRole.VendorManager),
                getRoleName(UserProfile.Companion.UserRole.ClientManager)
            )
        }


        return roleList
    }


    fun getCompleteUserAccessTree(
        userName: String,
        fragmentCallback: ProvisioningTreeRequestHandler
    ) {
        lateinit var provisioningTree: Country
        val provisioningTreeTag = "TreeTag"

        Log.i(provisioningTreeTag, "userName: $userName")

        val userAccessTreeRequestHandler: UserAccessTreeRequestHandler = object : UserAccessTreeRequestHandler {
                override fun onSuccess(userAccess: UserAccess?) {
                    Log.i(
                        "CompletedAccessTree",
                        "getCompletedAccessTree()"
                    )
                    val accessTree = userAccess?.permissions?.country
                    if (accessTree != null) {
                        val completedAccessTree: Country = if (selectedUser != null)
                            getCompletedAccessTree(
                                provisioningTree,
                                accessTree,
                                selectedUser.userAccess
                            )
                        else
                            getCompletedAccessTree(provisioningTree, accessTree, null)
                        fragmentCallback.onSuccess(completedAccessTree)
                    } else {
                        fragmentCallback.onError("You do not have access defined for yourself. Please contact your admin to define your access, before you define access for your team member. ")
                    }
                }

                override fun onError(message: String?) {
                    fragmentCallback.onError(message)
                }

            }

        val provisioningTreeRequestHandler: ProvisioningTreeRequestHandler = object : ProvisioningTreeRequestHandler {

                var dynamoDbAuthHandler: AuthorisationHandler = AuthorisationHandler { Result ->
                        if (Result != null) {
                            if (Result.isValidated) {
                                dynamoDbClient.getUserAccessTree(
                                    userName,
                                    userAccessTreeRequestHandler
                                )
                            }
                            else {
                                fragmentCallback.onError(Result.message)
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

        val provisioningClientAuthHandler = AuthorisationHandler { result ->
                if (result != null) {
                    if (result.isValidated) {
                        provisioningClient.getProvisioningTree(provisioningTreeRequestHandler)
                    } else {
                        fragmentCallback.onError(result.message)
                    }
                }
            }

        if (provisioningClient.hasValidAccessToken)
            provisioningClient.getProvisioningTree(provisioningTreeRequestHandler)
        else
            provisioningClient.Authorize(provisioningClientAuthHandler)

    }

    fun getCompleteUserAccessTree(
        clientCode: String,
        userName: String,
        fragmentCallback: ProvisioningTreeRequestHandler
    ) {
        lateinit var provisioningTree: Country
        val provisioningTreeTag = "TreeTag"

        Log.i(provisioningTreeTag, "userName: $userName")
        val userAccessTreeRequestHandler: UserAccessTreeRequestHandler =
            object : UserAccessTreeRequestHandler {
                override fun onSuccess(userAccess: UserAccess?) {
                    Log.i(
                        "CompletedAccessTree",
                        "getCompletedAccessTree()"
                    )
                    val accessTree = userAccess?.permissions?.country
                    if (accessTree != null) {
                        val completedAccessTree: Country
                        if (selectedUser != null)
                            completedAccessTree = getCompletedAccessTree(
                                provisioningTree,
                                accessTree,
                                selectedUser.userAccess
                            )
                        else
                            completedAccessTree =
                                getCompletedAccessTree(provisioningTree, accessTree, null)
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


    fun getAccessibleComplexList(
        userName: String,
        fragmentCallback: AccessibleComplexRequestHandler
    ) {
        lateinit var provisioningTree: Country

        val userAccessTreeRequestHandler: UserAccessTreeRequestHandler =
            object : UserAccessTreeRequestHandler {
                override fun onSuccess(userAccess: UserAccess?) {
                    val accessTree = userAccess?.permissions?.country
                    if (accessTree != null) {

                        Log.i(
                            "__getAccessibleComplex",
                            "provisioningTree: " + Gson().toJson(provisioningTree)
                        )
                        Log.i("__getAccessibleComplex", "accessTree: " + Gson().toJson(accessTree))


                        val completedAccessTree: Country =
                            getCompletedAccessTree(provisioningTree, accessTree, null)
                        fragmentCallback.onSuccess(getAccessibleComplexList(completedAccessTree))
                    } else {
                        fragmentCallback.onError("You do not have access defined for yourself. Please contact your admin to define your access to continue. ")
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

    fun getAccessibleComplexList(
        clientCode: String,
        userName: String,
        fragmentCallback: AccessibleComplexRequestHandler
    ) {
        lateinit var provisioningTree: Country
        val provisioningTreeTag = "TreeTag"

        Log.i(provisioningTreeTag, "userName: " + userName)
        val userAccessTreeRequestHandler: UserAccessTreeRequestHandler =
            object : UserAccessTreeRequestHandler {
                override fun onSuccess(userAccess: UserAccess?) {
                    Log.i(
                        "CompletedAccessTree",
                        "getCompletedAccessTree()"
                    )
                    val accessTree = userAccess?.permissions?.country
                    if (accessTree != null) {
                        val completedAccessTree: Country
                        completedAccessTree =
                            getCompletedAccessTree(provisioningTree, accessTree, null)

                        Log.i("__ClientRequest", Gson().toJson(provisioningTree))
                        Log.i("__ClientRequest", Gson().toJson(accessTree))
                        Log.i("__ClientRequest", Gson().toJson(completedAccessTree))

                        fragmentCallback.onSuccess(getAccessibleComplexList(completedAccessTree))
                    } else {
                        fragmentCallback.onError("You do not have access defined for yourself. Please contact your admin to define your access to proceed. ")
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
    } // No Use

    fun getClientProvisioningTree(
        clientCode: String,
        fragmentCallback: ProvisioningTreeRequestHandler
    ) {
        var provisioningTreeTag = "TreeTag"
        val provisioningTreeRequestHandler: ProvisioningTreeRequestHandler =
            object : ProvisioningTreeRequestHandler {
                override fun onSuccess(mCountry: Country?) {
                    if (mCountry != null) {
                        if (mCountry.states.size == 0) {
                            //No Access Defined
                            fragmentCallback.onSuccess(null)
                        } else {
                            fragmentCallback.onSuccess(mCountry)
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

    fun updateUserAccessTree(
        request: DefineAccessLambdaRequest,
        user: MemberDetailsData,
        country: Country,
        handler: RequestHandler
    ) {
        val lambdaRequestHandler: DefineAccessRequestHandler = object : DefineAccessRequestHandler {
            override fun onSuccess(result: DefineAccessLambdaResult?) {

                handler.onSuccess()
//                getSelectedUser().userAccess = getTrimmedAccessTree(country)
//
//                if(dynamoDbClient.hasValidAccessToken) {
//                    dynamoDbClient.updateUserAccessTree(user, getSelectedUser().userAccess, handler)
//                } else {
//                    val authorizationHandler: AuthorisationHandler = AuthorisationHandler {
//                        if(it != null ){
//                            if(it.isValidated) {
//                                dynamoDbClient.updateUserAccessTree(user, getSelectedUser().userAccess, handler)
//                            } else {
//                                handler.onError(it.message)
//                            }
//                        }
//                    }
//
//                    dynamoDbClient.Authorize(authorizationHandler)
//                }
            }

            override fun onError(errorMsg: String?) {
                handler.onError(errorMsg)
            }
        }
//        Log.i("_updateUserAccessTree", request.policyName + ": " + request.policyDocument)
        lambdaClient.ExecuteDefineAccessLambda(request, lambdaRequestHandler)
    }


    fun getTeam(adminName: String, fragmentCallback: DetailedTeamRequestHandler) {
        var tag = "_getTeam"
        val teamList: MutableList<MemberDetailsData> = arrayListOf()
        lateinit var memberDetailsData: MemberDetailsData

        val teamRequestHandler: TeamRequestHandler = object : TeamRequestHandler {
            override fun onSuccess(itemList: MutableList<Team>?) {
                if (itemList != null) {
                    Log.i("AWSDynamoDbClient", "teamRequestHandler item list is not null")
                    for (member in itemList) {
                        memberDetailsData =
                            MemberDetailsData()
                        memberDetailsData.team = member
                        teamList.add(memberDetailsData)
                    }

                    if (teamList.size == 0) {
                        Log.i("AWSDynamoDbClient", "onSuccess: team not assigned")
                        fragmentCallback.onError("Team not assigned to user '$adminName'.")
                    } else {
                        val accessTreeRequestHandler: DetailedTeamRequestHandler =
                            object : DetailedTeamRequestHandler {
                                override fun onSuccess(itemList: MutableList<MemberDetailsData>?) {
                                    Log.i("AWSDynamoDbClient", "onSuccess: found team")
                                    fragmentCallback.onSuccess(teamList)
                                }

                                override fun onError(message: String?) {
                                    Log.i(
                                        "AWSDynamoDbClient",
                                        "onSuccess: error finding team : " + message
                                    )
                                    Log.i("_getTeam", message)
                                }

                            }


                        val cognitoUserDetailsHandler: DetailedTeamRequestHandler =
                            object : DetailedTeamRequestHandler {
                                override fun onSuccess(itemList: MutableList<MemberDetailsData>?) {
                                    Log.i("_getTeam", itemList?.get(0)?.cognitoUser?.accountStatus)
                                    var helper = ViewModelHelper()
                                    helper.getAccessTree(
                                        context,
                                        teamList,
                                        dynamoDbClient,
                                        accessTreeRequestHandler
                                    )
                                }

                                override fun onError(message: String?) {
                                    Log.i("_getTeam", message)
                                }
                            }

                        val helper = ViewModelHelper()
                        helper.getCognitoDetails(
                            context,
                            teamList,
                            administrationClient,
                            cognitoUserDetailsHandler
                        )
                    }

                }
            }

            override fun onError(message: String?) {
                fragmentCallback.onError((message))
            }
        }


        var dynamoDbAuthHandler: AuthorisationHandler = object : AuthorisationHandler {
            override fun onResult(Result: ValidationResult?) {
                if (Result != null) {
                    if (Result.isValidated) {
                        dynamoDbClient.getTeam(adminName, teamRequestHandler)
                    } else {
                        fragmentCallback.onError(Result.message)
                    }
                }
            }
        }


        if (!dynamoDbClient.hasValidAccessToken)
            dynamoDbClient.Authorize(dynamoDbAuthHandler)
        else
            dynamoDbClient.getTeam(adminName, teamRequestHandler)

    }

    fun disableUser(userName: String, callBack: RequestHandler) {
        administrationClient.disableUser(context, userName, callBack)
    }

    fun enableUser(userName: String, callBack: RequestHandler) {
        administrationClient.enableUser(context, userName, callBack)
    }

    fun deleteUser(userName: String, callBack: RequestHandler) {
        administrationClient.deleteUser(context, userName, callBack)
    }

    fun deleteUserFromDynamoDb(userName: String, callBack: RequestHandler) {
        val request = DeleteUserLambdaRequest(userName)
        val repositoryCallback: RepositoryCallback<String> = RepositoryCallback { result ->
                if (result is _Result.Success) {
                    callBack.onSuccess()
                } else {
                    callBack.onError((result as _Result.Error).message)
                }
            }
        lambdaClient.ExecuteDeleteUserLambda(request, repositoryCallback)
    }


    fun getTeamFromLambda(adminName: String?, callback: DetailedTeamRequestHandler) {

        Log.i("administrationViewModel", "adminName: $adminName")
        val request = GetTeamLambdaRequest(adminName)

        val returnCallback: RepositoryCallback<GetTeamLambdaResult> = RepositoryCallback { result ->
            Log.i("dashboardLambda", "ExecuteCabinDetailsLambda: 5")
            Log.i("dashboardLambda", "onComplete: 5 :" + Gson().toJson(result))
            if (result is _Result.Success) {
                val data = result.data

                val itemList = handleTeam(data.teamDetails)

                callback?.onSuccess(itemList)
            } else {
                val err = result as _Result.Error<String>
                callback?.onError(err.message)
            }
        }

        lambdaClient.ExecuteGetTeamLambda(request, returnCallback)
    }

    private fun handleTeam(
        teamDetails: ArrayList<TeamDetail>
    ): List<MemberDetailsData> {

        var itemList: List<MemberDetailsData> = ArrayList()

        for (fromTeam in teamDetails) {
            val item = MemberDetailsData()
            val cognitoUser = CognitoUser(fromTeam.userName ?: "")

            var team = Team()
            team.admin = fromTeam.admin
            team.member = fromTeam.userName
            team.adminRole = fromTeam.adminRole
            team.memberRole = fromTeam.userRole
            team.assignedBy = fromTeam.assignedBy
            team.assignedOn = fromTeam.assignedOn
            team.assignmentType = fromTeam.assignedBy

            var lambdaCountry = Country()
            if(fromTeam.permissions.country != null)
                lambdaCountry = countryConversion(fromTeam.permissions.country)


            cognitoUser.userName = fromTeam.userName ?: ""
            Log.i("DetailTeam", "UserName: ${fromTeam.userName}" )
            cognitoUser.accountStatus = fromTeam.userStatus ?: ""
            cognitoUser.isEnabled = fromTeam.enabled
            Log.i("DetailTeam", "Enabled: ${fromTeam.enabled}" )
            cognitoUser.role = fromTeam.userRole ?: ""
            cognitoUser.client = fromTeam.clientName ?: ""
            cognitoUser.organisation = fromTeam.organisation ?: ""
            cognitoUser.lastModified = fromTeam.lastModifiedOn.toString()
            cognitoUser.created = fromTeam.createdOn.toString()
            cognitoUser.name = fromTeam.clientName ?: ""
            cognitoUser.gender = fromTeam.gender ?: ""
            cognitoUser.address = ""
            cognitoUser.phoneNumber = fromTeam.phoneNumber ?: ""
            cognitoUser.email = fromTeam.email ?: ""

            item.userAccess = lambdaCountry
            item.team = team
            item.cognitoUser = cognitoUser

            itemList += item

        }

        return itemList
    }


    private fun countryConversion(lambdaCountry: sukriti.ngo.mis.ui.administration.data.Country): Country {
        val returnCountry = Country()

        if (lambdaCountry != null) {
            val states = mutableListOf<sukriti.ngo.mis.dataModel.dynamo_db.State>()
            if (lambdaCountry.states != null) {
                for (state in lambdaCountry.states) {
                    states.add(getState(state))
                }
            }
            returnCountry.name = "India"
            returnCountry.recursive = 0
            returnCountry.states = states
        }
        return returnCountry
    }


    private fun getState(state: State?): sukriti.ngo.mis.dataModel.dynamo_db.State {
        var returnState = sukriti.ngo.mis.dataModel.dynamo_db.State()
        if (state != null) {
            val districts = mutableListOf<sukriti.ngo.mis.dataModel.dynamo_db.District>()
            if (state.districts != null) {
                for (district in state.districts) {
                    districts.add(getDistrict(district))
                }
            }
            Log.e("accessLamda", "getDistrict: " + Gson().toJson(districts))
            returnState = sukriti.ngo.mis.dataModel.dynamo_db.State(
                state.name,
                state.code,
                0,
                districts
            )
        }
        return returnState
    }

    private fun getDistrict(district: District): sukriti.ngo.mis.dataModel.dynamo_db.District {
        var returnDistrict = sukriti.ngo.mis.dataModel.dynamo_db.District()
        if (district != null) {
            val cities = mutableListOf<sukriti.ngo.mis.dataModel.dynamo_db.City>()
            if (district.cities != null) {
                for (city in district.cities) {
                    getCity(city)?.let { cities.add(it) }
                }
            }

            returnDistrict = sukriti.ngo.mis.dataModel.dynamo_db.District(
                district.name,
                district.code,
                0,
                cities
            )
        }

        Log.i("administrationViewModel", "getDistrict: " + Gson().toJson(district.name))
        Log.i("administrationViewModel", "code: " + Gson().toJson(district.code))
        Log.i("administrationViewModel", "recursive: " + Gson().toJson(district.recursive))
        Log.i("administrationViewModel", "getDistrict: " + Gson().toJson(returnDistrict))

        return returnDistrict
    }

    private fun getCity(city: City): sukriti.ngo.mis.dataModel.dynamo_db.City {
        var returnCity = sukriti.ngo.mis.dataModel.dynamo_db.City()
        if (city != null) {
            val complexes = mutableListOf<sukriti.ngo.mis.dataModel.dynamo_db.Complex>()
            if (city.complexes != null) {
                for (complex in city.complexes) {
                    complexes.add(getComplex(complex))
                }
            }
            returnCity = sukriti.ngo.mis.dataModel.dynamo_db.City(
                city.name,
                city.code,
                0,
                complexes
            )
        }
//        Log.i("accessLamda", "getCity: "+Gson().toJson(returnCity))
        return returnCity
    }

    private fun getComplex(complex: Complex): sukriti.ngo.mis.dataModel.dynamo_db.Complex {
        var returnComplex = sukriti.ngo.mis.dataModel.dynamo_db.Complex()
        if (complex != null) {
            returnComplex = sukriti.ngo.mis.dataModel.dynamo_db.Complex(
                complex.name,
                complex.address,
                complex.uuid,
                complex.coco,
                complex.lat,
                complex.lon
            )
//            if (!complex.isSelected) returnComplex.isSelected = 0 else returnComplex.isSelected =1
        }
//        Log.i("accessLamda", "getComplex: "+Gson().toJson(returnComplex))
        return returnComplex
    }


    fun isSuperAdmin(): Boolean {
        return sharedPrefsClient.getUserDetails().role == UserProfile.Companion.UserRole.SuperAdmin
    }
}
