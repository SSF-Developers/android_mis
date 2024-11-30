package sukriti.ngo.mis.ui.management.lambda.FetchClientLogo

import com.google.gson.JsonObject

interface FetchClientLogoResponseHandler {

    fun onSuccess(response: JsonObject)

    fun onError(error: String)

}