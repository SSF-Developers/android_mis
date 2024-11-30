package sukriti.ngo.mis.ui.complexes

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.repository.DataRepository
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData
import sukriti.ngo.mis.ui.administration.data.TreeEdge
import sukriti.ngo.mis.ui.administration.interfaces.RequestHandler
import sukriti.ngo.mis.ui.complexes.data.CabinDetailsData
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.ui.complexes.interfaces.ComplexDetailsRequestHandler
import sukriti.ngo.mis.ui.complexes.lambda.CabinComposition_fetchdata.ComplexCompositionLambdaRequest
import sukriti.ngo.mis.ui.complexes.lambda.CabinComposition_fetchdata.ComplexCompositionLambdaResult
import sukriti.ngo.mis.ui.complexes.lambda.CabinDetailsLambdaClient
import sukriti.ngo.mis.utils.*
import sukriti.ngo.mis.utils.Nomenclature.*
import java.util.*

class ComplexesViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var selectedUser: MemberDetailsData
    private var context: Context = application.applicationContext
    private var sharedPrefsClient: SharedPrefsClient
    private var provisioningClient: AWSIotProvisioningClient
    private var administrationClient: AdministrationClient
    private var dynamoDbClient: AdministrationDbHelper
    private var userDetails: MemberDetailsData = MemberDetailsData()
    private val tag = "_ComplexesVM"

    lateinit var cabinDetailsLambdaClient: CabinDetailsLambdaClient


    companion object {
        const val NAV_CABIN_HOME = 1
        const val NAV_CABIN_HOME_BWT = 2

        const val NAV_TAB_MWC = 10
        const val NAV_TAB_FWC = 11
        const val NAV_TAB_PWC = 12
        const val NAV_TAB_MUR = 13
        const val NAV_TAB_BWT = 14
    }

    init {
        AuthenticationClient.init(context)
        sharedPrefsClient = SharedPrefsClient(context)
        provisioningClient = AWSIotProvisioningClient(context)
        administrationClient = AdministrationClient()
        dynamoDbClient = AdministrationDbHelper(context)

        cabinDetailsLambdaClient = CabinDetailsLambdaClient(context)
    }

    fun setSelection(
        SelectedComplex: ComplexDetailsData,
        SelectedCabin: CabinDetailsData,
        fragmentCallback: RequestHandler
    ) {

        var dataRepository = DataRepository(context)

        var insertCabinLogRequestHandler: RequestHandler = object : RequestHandler {
            override fun onSuccess() {
                fragmentCallback.onSuccess()
            }

            override fun onError(message: String?) {
                fragmentCallback.onError(message)
            }
        }

        var insertComplexLogRequestHandler: RequestHandler = object : RequestHandler {
            override fun onSuccess() {
                dataRepository.insertCabinAccessLog(
                    SelectedCabin,
                    context,
                    insertCabinLogRequestHandler
                )
            }

            override fun onError(message: String?) {
                fragmentCallback.onError(message)
            }
        }

        dataRepository.insertComplexAccessLog(
            SelectedComplex,
            context,
            insertComplexLogRequestHandler
        )
    }


    fun getCabinId(
        country: Country,
        treeEdge: TreeEdge
    ): String {
        var uuid =
            country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].cities[treeEdge.cityIndex].complexes[treeEdge.complexIndex].uuid
        return uuid.split("_")[0]
    }

    fun getComplexDetails(complexName: String, fragmentCallback: ComplexDetailsRequestHandler) {
        Log.i(tag, "getComplexDetails() - $complexName")
        val request = ComplexCompositionLambdaRequest(complexName)


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
                        data.complexComposition.murCabins.size > 0 -> complexDetails = ComplexDetailsData.getComplexDetailsFromCabinAttributes_lambda(complexDetails,data.complexComposition.murCabins[0])
                        data.complexComposition.mwcCabins.size > 0 -> complexDetails = ComplexDetailsData.getComplexDetailsFromCabinAttributes_lambda(complexDetails,data.complexComposition.mwcCabins[0])
                        data.complexComposition.fwcCabins.size > 0 -> complexDetails = ComplexDetailsData.getComplexDetailsFromCabinAttributes_lambda(complexDetails,data.complexComposition.fwcCabins[0])
                        data.complexComposition.pwcCabins.size > 0 -> complexDetails = ComplexDetailsData.getComplexDetailsFromCabinAttributes_lambda(complexDetails,data.complexComposition.pwcCabins[0])
                        data.complexComposition.bwtCabins.size > 0 -> complexDetails = ComplexDetailsData.getComplexDetailsFromCabinAttributes_lambda(complexDetails,data.complexComposition.bwtCabins[0])
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

        cabinDetailsLambdaClient.ExecuteComplexCompositionLambda(request, callback)

//        var complexDetailsRequestHandler : GetThingsInThingGroupHandler = object : GetThingsInThingGroupHandler{
//            override fun onResult(List: ArrayList<ThingDetails>?) {
//                var complexDetails: ComplexDetailsData = ComplexDetailsData()
//                var cabinDetails: CabinDetailsData
//                if (List != null) {
//                    for(item in List){
//                        Log.i(tag, "totalCabins - ${complexDetails.totalCabins}" )
//
//                        if(complexDetails.totalCabins == 0) {
//                            Log.i(tag, "complexDetails - init")
//                            complexDetails = ComplexDetailsData.getComplexDetailsFromCabinAttributes(item)
//                        }
//                        Log.i("composition", "onResult: "+Gson().toJson(item))
//
//                        cabinDetails = CabinDetailsData.getCabinDetails(item)
//                        if (Nomenclature.getCabinType(cabinDetails.ShortThingName, CABIN_TYPE_MWC)) {
//                            Log.i(tag, "CABIN_TYPE_MWC")
//                            complexDetails.mwcCabins.add(cabinDetails)
//                            complexDetails.totalCabins++
//                        }
//                        else if (Nomenclature.getCabinType(cabinDetails.ShortThingName, CABIN_TYPE_FWC)) {
//                            Log.i(tag, "CABIN_TYPE_FWC")
//                            complexDetails.fwcCabins.add(cabinDetails)
//                            complexDetails.totalCabins++
//                        }
//                        else if (Nomenclature.getCabinType(cabinDetails.ShortThingName, CABIN_TYPE_PWC)) {
//                            Log.i(tag, "CABIN_TYPE_PWC")
//                            complexDetails.pwcCabins.add(cabinDetails)
//                            complexDetails.totalCabins++
//                        }
//                        else if (Nomenclature.getCabinType(cabinDetails.ShortThingName, CABIN_TYPE_MUR)) {
//                            Log.i(tag, "CABIN_TYPE_MUR")
//                            complexDetails.murCabins.add(cabinDetails)
//                            complexDetails.totalCabins++
//                        }
//                        else if (Nomenclature.getCabinType(cabinDetails.ShortThingName, CABIN_TYPE_BWT)) {
//                            Log.i(tag, "CABIN_TYPE_BWT")
//                            complexDetails.bwtCabins.add(cabinDetails)
//                            complexDetails.totalCabins++
//                        }else{
//                            Log.i(tag, "NONE")
//                        }
//                    }
//                }
//
//                Log.i(tag, "totalCabins- ${complexDetails.totalCabins}")
//                Log.i(tag, "mwc- ${complexDetails.fwcCabins.size}")
//                Log.i(tag, "fwc- ${complexDetails.mwcCabins.size}")
//                Log.i(tag, "pwc- ${complexDetails.pwcCabins.size}")
//                Log.i(tag, "mur- ${complexDetails.murCabins.size}")
//                Log.i(tag, "bwt- ${complexDetails.bwtCabins.size}")
//
//                fragmentCallback.onSuccess(complexDetails)
//            }
//
//            override fun onError(message: String?) {
//                fragmentCallback.onError(message)
//            }
//
//        }
//
//        var provisioningClientAuthHandler: AuthorisationHandler = object : AuthorisationHandler {
//            override fun onResult(Result: ValidationResult?) {
//                if (Result != null) {
//                    if (Result.isValidated) {
//                        provisioningClient.getThingsInThingGroup(complexName,complexDetailsRequestHandler)
//                    } else {
//                        fragmentCallback.onError(Result.message)
//                    }
//                }
//            }
//        }
//
//        if (provisioningClient.hasValidAccessToken)
//            provisioningClient.getThingsInThingGroup(complexName,complexDetailsRequestHandler)
//        else
//            provisioningClient.Authorize(provisioningClientAuthHandler)
    }


}
