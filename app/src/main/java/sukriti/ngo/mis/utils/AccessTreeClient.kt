package sukriti.ngo.mis.utils

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import sukriti.ngo.mis.AWSConfig
import sukriti.ngo.mis.dataModel.ThingDetails
import sukriti.ngo.mis.dataModel.ThingGroupDetails
import sukriti.ngo.mis.dataModel.ValidationResult
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.dataModel.dynamo_db.UserAccess
import sukriti.ngo.mis.interfaces.*
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData
import sukriti.ngo.mis.ui.administration.interfaces.ProvisioningTreeRequestHandler
import sukriti.ngo.mis.ui.administration.interfaces.UserAccessTreeRequestHandler
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.ClientListLambdaResult
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.GetClientListResultHandler
import sukriti.ngo.mis.ui.complexes.data.CabinDetailsData
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.ui.complexes.data.lambdaData.City
import sukriti.ngo.mis.ui.complexes.data.lambdaData.Complex
import sukriti.ngo.mis.ui.complexes.data.lambdaData.District
import sukriti.ngo.mis.ui.complexes.data.lambdaData.State
import sukriti.ngo.mis.ui.complexes.interfaces.ComplexDetailsRequestHandler
import sukriti.ngo.mis.ui.complexes.lambda.CabinComposition_fetchdata.ComplexCompositionLambdaRequest
import sukriti.ngo.mis.ui.complexes.lambda.CabinComposition_fetchdata.ComplexCompositionLambdaResult
import sukriti.ngo.mis.ui.complexes.lambda.CabinDetailsLambdaClient
import sukriti.ngo.mis.ui.complexes.lambda.Complex_fetchAccessTree.AccessTreeLambdaRequest
import sukriti.ngo.mis.ui.complexes.lambda.Complex_fetchAccessTree.AccessTreeLambdaResult
import java.util.*


class AccessTreeClient() {
    private lateinit var provisioningClient: AWSIotProvisioningClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var context: Context
    private lateinit var administrationClient: AdministrationClient
    private lateinit var dynamoDbClient: AdministrationDbHelper
    private lateinit var accessTreeLambdaClient: CabinDetailsLambdaClient
    private var userDetails: MemberDetailsData = MemberDetailsData()

    constructor(
        provisioningClient: AWSIotProvisioningClient,
        sharedPrefsClient: SharedPrefsClient
    ) : this() {
        this.provisioningClient = provisioningClient
        this.sharedPrefsClient = sharedPrefsClient
    }

    constructor(
        provisioningClient: AWSIotProvisioningClient,
        sharedPrefsClient: SharedPrefsClient,
        context: Context
    ) : this() {
        this.provisioningClient = provisioningClient
        this.sharedPrefsClient = sharedPrefsClient
        this.context = context
    }


    constructor(
        context: Context,
        provisioningClient: AWSIotProvisioningClient,
        sharedPrefsClient: SharedPrefsClient,
        administrationClient: AdministrationClient,
        dynamoDbClient: AdministrationDbHelper
    ) : this() {
        this.context = context
        this.provisioningClient = provisioningClient
        this.sharedPrefsClient = sharedPrefsClient
        this.administrationClient = administrationClient
        this.dynamoDbClient = dynamoDbClient
    }


    fun getClientList(callback: ClientNameListHandler) {

        val cachedDataLifeSpan =
            Calendar.getInstance().timeInMillis - sharedPrefsClient.getClientListTimestamp()

        if (cachedDataLifeSpan < Nomenclature.LIFETIME_CLIENT_LIST) {
            //Lifetime not expired; reuse cached list
            callback.onResult(sharedPrefsClient.getClientList())
        } else {
            //Lifetime expired; fetch fresh list from Aws
            val thingGroupChildrenHandler: GetThingGroupChildrenHandler =
                object : GetThingGroupChildrenHandler {
                    override fun onResult(
                        Parent: String?,
                        List: ArrayList<ThingGroupDetails>?,
                        Type: Int
                    ) {
                        if (List != null) {
                            val items = Utilities.getNameList(List);
                            sharedPrefsClient.saveClientList(items)
                            callback.onResult(items)
                        }
                    }

                    override fun onError(message: String?) {
                        callback.onError(message)
                    }
                }

            val getClientListResultHandler: GetClientListResultHandler =
                object : GetClientListResultHandler {
                    override fun onSuccess(result: ClientListLambdaResult?) {
                        if (result != null) {
                            val items = Utilities.getNameList(result.list)
                            sharedPrefsClient.saveClientList(items)
                            callback.onResult(items)
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        callback.onError(errorMessage)
                    }

                }

            val provisioningClientAuthHandler: AuthorisationHandler =
                AuthorisationHandler { result ->
                    if (result != null) {
                        if (result.isValidated) {
                            /*                            provisioningClient.getChildrenThingGroups(
                                AWSConfig.THING_GROUP_CLIENT_ROOT,
                                thingGroupChildrenHandler
                            )*/
                            val lambdaClient = LambdaClient(context)
                            lambdaClient.ExecuteGetClientListLambda(getClientListResultHandler)
                        } else
                            callback.onError(result.message)
                    }
                }



            if (!provisioningClient.hasValidAccessToken)
                provisioningClient.Authorize(provisioningClientAuthHandler)
            else {
                /*                provisioningClient.getChildrenThingGroups(
                                    AWSConfig.THING_GROUP_CLIENT_ROOT,
                                    thingGroupChildrenHandler
                                )*/
                val lambdaClient = LambdaClient(context)
                lambdaClient.ExecuteGetClientListLambda(getClientListResultHandler)
            }
        }
    }

    fun loadAccessTreeForUser(userName: String, fragmentCallback: ProvisioningTreeRequestHandler) {
        Log.i("LambdaExecution", "executing AccessTreeClient method for loadAccessTreeForUser ");

        val cachedDataLifeSpan =
            Calendar.getInstance().timeInMillis - sharedPrefsClient.getAccessTreeTimestamp()

        /*        if (cachedDataLifeSpan < Nomenclature.LIFETIME_ACCESS_TREE) {
                    //Lifetime not expired; reuse cached list
                    fragmentCallback.onSuccess(sharedPrefsClient.getAccessTree())
                }
                else {*/
        Log.i("_cachedDataLifeSpan", "" + cachedDataLifeSpan)
        var tag = "_getUserDetails"
        accessTreeLambdaClient = CabinDetailsLambdaClient(context)
        userDetails = MemberDetailsData()

        val provisioningTreeRequestHandler: ProvisioningTreeRequestHandler =
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
                    Log.i("_misTree", "onError: provisioningTreeRequestHandler :" + message)
                    fragmentCallback.onError(message)
                }
            }

        val request = AccessTreeLambdaRequest(userName)
        val callback: RepositoryCallback<AccessTreeLambdaResult> =
            RepositoryCallback { result ->
                Log.i("accessLamda", "ExecuteCabinDetailsLambda: 5")
                Log.i("accessLamda", "onComplete: 5 :" + Gson().toJson(result))
                if (result is _Result.Success) {
                    val data = result.data.accessTree.country

                    val country = getCountry(data)
                    provisioningTreeRequestHandler.onSuccess(country)
                } else {
                    provisioningTreeRequestHandler.onError("You do not have access defined for yourself. Please contact your admin to define your access, before you define access for your team member. ")
                }

            }
        accessTreeLambdaClient.ExecuteAccessTreeLambda(request, callback)

//            var userAccessTreeRequestHandler: UserAccessTreeRequestHandler = object :
//                UserAccessTreeRequestHandler {
//                override fun onSuccess(userAccess: UserAccess?) {
//
//                    userDetails.userAccess = userAccess?.permissions?.country
//                    Log.i("__definedUserAccess", Gson().toJson(userDetails))
//
//                    if (UserProfile.isClientSpecificRole(sharedPrefsClient.getUserDetails().role)) {
//                        if (userDetails != null) {
//                            getCompleteUserAccessTree(
//                                userDetails.cognitoUser.client,
//                                userDetails.cognitoUser.userName,
//                                provisioningTreeRequestHandler
//                            )
//                        }
//                    } else {
//                        if (userDetails != null) {
//                            getCompleteUserAccessTree(
//                                userDetails.cognitoUser.userName,
//                                provisioningTreeRequestHandler
//                            )
//                        }
//                    }
//                }
//
//                override fun onError(message: String?) {
//                    Log.i("_misTree", "onError: userAccessTreeRequestHandler :"+message )
//                    fragmentCallback.onError(message)
//                }
//            }
//
//            var dynamoDbAuthHandler: AuthorisationHandler = object : AuthorisationHandler {
//                override fun onResult(Result: ValidationResult?) {
//                    if (Result != null) {
//                        if (Result.isValidated) {
//                            dynamoDbClient.getUserAccessTree(
//                                userName,
//                                userAccessTreeRequestHandler
//                            )
//                        } else {
//                            Log.i("_misTree", "onError: dynamoDbAuthHandler :"+Result.message )
//                            fragmentCallback.onError(Result.message)
//                        }
//                    }
//                }
//            }
//
//            var cognitoUserDetailsRequestHandler: CognitoUserDetailsRequestHandler = object :
//                CognitoUserDetailsRequestHandler {
//                override fun onSuccess(user: CognitoUser?) {
//                    userDetails.cognitoUser = user
//                    if (!dynamoDbClient.hasValidAccessToken)
//                        dynamoDbClient.Authorize(dynamoDbAuthHandler)
//                    else {
//                        dynamoDbClient.getUserAccessTree(
//                            userName,
//                            userAccessTreeRequestHandler
//                        )
//                    }
//                }
//
//                override fun onError(message: String?) {
//                    Log.i("_misTree", "onError: cognitoUserDetailsRequestHandler :"+message )
//                    fragmentCallback.onError(message)
//                }
//
//            }
//
//            administrationClient.getUserDetails(context, userName, cognitoUserDetailsRequestHandler)
//        }

    }

    private fun getCountry(country: sukriti.ngo.mis.ui.complexes.data.lambdaData.Country?): Country {

        val returnCountry = Country()

        if (country != null) {
            val states = mutableListOf<sukriti.ngo.mis.dataModel.dynamo_db.State>()
            for (state in country.states) {
                states.add(getState(state))
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
            for (district in state.districts) {
                districts.add(getDistrict(district))
            }
            Log.e("accessLamda", "getDistrict: " + Gson().toJson(districts))
            returnState = sukriti.ngo.mis.dataModel.dynamo_db.State(
                state.name,
                state.code,
                0,
                districts
            )
        }
//        Log.i("accessLamda", "getState: "+Gson().toJson(returnState))
        return returnState
    }

    private fun getDistrict(district: District): sukriti.ngo.mis.dataModel.dynamo_db.District {
        var returnDistrict = sukriti.ngo.mis.dataModel.dynamo_db.District()
        if (district != null) {
            val cities = mutableListOf<sukriti.ngo.mis.dataModel.dynamo_db.City>()
            for (city in district.cities) {
                getCity(city)?.let { cities.add(it) }
            }
            returnDistrict = sukriti.ngo.mis.dataModel.dynamo_db.District(
                district.name,
                district.code,
                0,
                cities
            )
        }
        Log.i("accessLamda", "getDistrict: " + Gson().toJson(district.name))
        Log.i("accessLamda", "code: " + Gson().toJson(district.code))
        Log.i("accessLamda", "recursive: " + Gson().toJson(district.recursive))
        Log.i("accessLamda", "getDistrict: " + Gson().toJson(returnDistrict))
        return returnDistrict
    }

    private fun getCity(city: City): sukriti.ngo.mis.dataModel.dynamo_db.City {
        var returnCity = sukriti.ngo.mis.dataModel.dynamo_db.City()
        if (city != null) {
            val complexes = mutableListOf<sukriti.ngo.mis.dataModel.dynamo_db.Complex>()
            for (complex in city.complexes) {
                complexes.add(getComplex(complex))
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

    fun getCompleteUserAccessTree(
        userName: String,
        fragmentCallback: ProvisioningTreeRequestHandler
    ) {
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

                    val accessTree = userAccess?.permissions?.country
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

                var dynamoDbAuthHandler: AuthorisationHandler =
                    AuthorisationHandler { result ->
                        if (result != null) {
                            if (result.isValidated) {
                                dynamoDbClient.getUserAccessTree(
                                    userName,
                                    userAccessTreeRequestHandler
                                )
                            } else {
                                fragmentCallback.onError(result.message)
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

        val provisioningClientAuthHandler: AuthorisationHandler =
            AuthorisationHandler { Result ->
                if (Result != null) {
                    if (Result.isValidated) {
                        provisioningClient.getProvisioningTree(provisioningTreeRequestHandler)
                    } else {
                        fragmentCallback.onError(Result.message)
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

        Log.i("__definedUserAccess", "loadCompleted for client $clientCode")

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
                            "completedAccessTree: ee " + Gson().toJson(completedAccessTree)
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

    fun getComplexDetails(complexName: String, fragmentCallback: ComplexDetailsRequestHandler) {
        val tag = "__getComplexDetails"
        Log.i(tag, "getComplexDetails() - $complexName")

        val complexDetailsRequestHandler: GetThingsInThingGroupHandler =
            object : GetThingsInThingGroupHandler {
                override fun onResult(List: ArrayList<ThingDetails>?) {
                    var complexDetails = ComplexDetailsData()
                    var cabinDetails: CabinDetailsData
                    if (List != null) {
                        for (item in List) {
                            Log.i(tag, "totalCabins - ${complexDetails.totalCabins}")

                            if (complexDetails.totalCabins == 0) {
                                Log.i(tag, "complexDetails - init")
                                complexDetails =
                                    ComplexDetailsData.getComplexDetailsFromCabinAttributes(item)
                            }

                            cabinDetails = CabinDetailsData.getCabinDetails(item)
                            if (Nomenclature.getCabinType(
                                    cabinDetails.ShortThingName,
                                    Nomenclature.CABIN_TYPE_MWC
                                )
                            ) {
                                Log.i(tag, "CABIN_TYPE_MWC")
                                complexDetails.mwcCabins.add(cabinDetails)
                                complexDetails.totalCabins++
                            } else if (Nomenclature.getCabinType(
                                    cabinDetails.ShortThingName,
                                    Nomenclature.CABIN_TYPE_FWC
                                )
                            ) {
                                Log.i(tag, "CABIN_TYPE_FWC")
                                complexDetails.fwcCabins.add(cabinDetails)
                                complexDetails.totalCabins++
                            } else if (Nomenclature.getCabinType(
                                    cabinDetails.ShortThingName,
                                    Nomenclature.CABIN_TYPE_PWC
                                )
                            ) {
                                Log.i(tag, "CABIN_TYPE_PWC")
                                complexDetails.pwcCabins.add(cabinDetails)
                                complexDetails.totalCabins++
                            } else if (Nomenclature.getCabinType(
                                    cabinDetails.ShortThingName,
                                    Nomenclature.CABIN_TYPE_MUR
                                )
                            ) {
                                Log.i(tag, "CABIN_TYPE_MUR")
                                complexDetails.murCabins.add(cabinDetails)
                                complexDetails.totalCabins++
                            } else if (Nomenclature.getCabinType(
                                    cabinDetails.ShortThingName,
                                    Nomenclature.CABIN_TYPE_BWT
                                )
                            ) {
                                Log.i(tag, "CABIN_TYPE_BWT")
                                complexDetails.bwtCabins.add(cabinDetails)
                                complexDetails.totalCabins++
                            } else {
                                Log.i(tag, "NONE")
                            }
                        }
                    }

                    Log.i(tag, "totalCabins- ${complexDetails.totalCabins}")
                    Log.i(tag, "mwc- ${complexDetails.fwcCabins.size}")
                    Log.i(tag, "fwc- ${complexDetails.mwcCabins.size}")
                    Log.i(tag, "pwc- ${complexDetails.pwcCabins.size}")
                    Log.i(tag, "mur- ${complexDetails.murCabins.size}")
                    Log.i(tag, "bwt- ${complexDetails.bwtCabins.size}")

                    fragmentCallback.onSuccess(complexDetails)
                }

                override fun onError(message: String?) {
                    fragmentCallback.onError(message)
                }

            }

        val callback: RepositoryCallback<ComplexCompositionLambdaResult> =
            RepositoryCallback { result ->
                Log.i("cabinDetails", "ExecuteCabinDetailsLambda: 5")
                Log.i("cabinDetails", "onComplete: 5 :" + Gson().toJson(result))
                if (result is _Result.Success) {
                    val data = result.data
                    var complexDetails = ComplexDetailsData()
                    var cabinDetails: CabinDetailsData


                    Log.i(
                        "connection",
                        "onComplete: 6 :result success : " + Gson().toJson(result.data)
                    )



                    if (data.complexComposition.murCabins.size > 0) {
                        for (detail in data.complexComposition.murCabins) {
                            cabinDetails = CabinDetailsData.getCabinDetailsLambda(detail)
                            complexDetails.murCabins.add(cabinDetails)
                            complexDetails.totalCabins++
                        }
                    }
                    if (data.complexComposition.mwcCabins.size > 0) {
                        for (detail in data.complexComposition.mwcCabins) {
                            cabinDetails = CabinDetailsData.getCabinDetailsLambda(detail)
                            complexDetails.mwcCabins.add(cabinDetails)
                            complexDetails.totalCabins++
                        }
                    }
                    if (data.complexComposition.fwcCabins.size > 0) {
                        for (detail in data.complexComposition.fwcCabins) {
                            cabinDetails = CabinDetailsData.getCabinDetailsLambda(detail)
                            complexDetails.fwcCabins.add(cabinDetails)
                            complexDetails.totalCabins++
                        }
                    }
                    if (data.complexComposition.pwcCabins.size > 0) {
                        for (detail in data.complexComposition.pwcCabins) {
                            cabinDetails = CabinDetailsData.getCabinDetailsLambda(detail)
                            complexDetails.pwcCabins.add(cabinDetails)
                            complexDetails.totalCabins++
                        }
                    }
                    if (data.complexComposition.bwtCabins.size > 0) {
                        for (detail in data.complexComposition.bwtCabins) {
                            cabinDetails = CabinDetailsData.getCabinDetailsLambda(detail)
                            complexDetails.bwtCabins.add(cabinDetails)
                            complexDetails.totalCabins++
                        }
                    }

                    when {
                        data.complexComposition.murCabins.size > 0 -> complexDetails =
                            ComplexDetailsData.getComplexDetailsFromCabinAttributes_lambda(
                                complexDetails,
                                data.complexComposition.murCabins[0]
                            )

                        data.complexComposition.mwcCabins.size > 0 -> complexDetails =
                            ComplexDetailsData.getComplexDetailsFromCabinAttributes_lambda(
                                complexDetails,
                                data.complexComposition.mwcCabins[0]
                            )

                        data.complexComposition.fwcCabins.size > 0 -> complexDetails =
                            ComplexDetailsData.getComplexDetailsFromCabinAttributes_lambda(
                                complexDetails,
                                data.complexComposition.fwcCabins[0]
                            )

                        data.complexComposition.pwcCabins.size > 0 -> complexDetails =
                            ComplexDetailsData.getComplexDetailsFromCabinAttributes_lambda(
                                complexDetails,
                                data.complexComposition.pwcCabins[0]
                            )

                        data.complexComposition.bwtCabins.size > 0 -> complexDetails =
                            ComplexDetailsData.getComplexDetailsFromCabinAttributes_lambda(
                                complexDetails,
                                data.complexComposition.bwtCabins[0]
                            )
                    }

                    Log.i(
                        "connection",
                        "onComplete: 6 :result success : " + Gson().toJson(complexDetails)
                    )
                    fragmentCallback.onSuccess(complexDetails)
                } else {
                    val err = result as _Result.Error<*>
                    fragmentCallback.onError(err.message)
                }
            }

        /*        val provisioningClientAuthHandler: AuthorisationHandler =
                    AuthorisationHandler { Result ->
                        if (Result != null) {
                            if (Result.isValidated) {
                                provisioningClient.getThingsInThingGroup(
                                    complexName,
                                    complexDetailsRequestHandler
                                )
                            } else {
                                fragmentCallback.onError(Result.message)
                            }
                        }
                    }

                if (provisioningClient.hasValidAccessToken)
                    provisioningClient.getThingsInThingGroup(complexName, complexDetailsRequestHandler)
                else
                    provisioningClient.Authorize(provisioningClientAuthHandler)*/
        val cabinDetailsLambdaClient = CabinDetailsLambdaClient(context)
        val request = ComplexCompositionLambdaRequest(complexName)
        cabinDetailsLambdaClient.ExecuteComplexCompositionLambda(request, callback)
    }
}