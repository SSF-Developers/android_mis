package sukriti.ngo.mis.ui.management.lambda.TogglePolicyState

import com.google.gson.JsonObject

interface TogglePolicyStateResponseHandler {
    fun onSuccess(response: JsonObject)

    fun onError(message: String)
}