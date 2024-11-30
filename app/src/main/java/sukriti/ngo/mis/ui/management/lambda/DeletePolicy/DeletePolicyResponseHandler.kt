package sukriti.ngo.mis.ui.management.lambda.DeletePolicy

import com.google.gson.JsonObject

interface DeletePolicyResponseHandler {

    fun onSuccess(response: JsonObject)

    fun onError(message: String?)
}