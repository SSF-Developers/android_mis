package sukriti.ngo.mis.ui.management.lambda.EnrollDevice

import com.google.gson.JsonObject
import org.json.JSONObject
import sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise.DeleteEnterpriseLambdaResponse

interface ListIotStateDistrictCityResponseHandler {

    fun onSuccess(response: JsonObject)

    fun onError(message: String?)
}