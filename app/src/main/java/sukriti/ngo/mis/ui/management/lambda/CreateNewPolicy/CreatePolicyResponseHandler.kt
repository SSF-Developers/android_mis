package sukriti.ngo.mis.ui.management.lambda.CreateNewPolicy

import com.google.gson.JsonObject
import org.json.JSONObject

interface CreatePolicyResponseHandler {

    fun onSuccess(response: JsonObject)

    fun onError(error: String)
}