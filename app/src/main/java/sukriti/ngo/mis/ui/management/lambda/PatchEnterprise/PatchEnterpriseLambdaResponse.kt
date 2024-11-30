package sukriti.ngo.mis.ui.management.lambda.PatchEnterprise

import com.google.gson.JsonObject

interface PatchEnterpriseLambdaResponse {

    fun onSuccess(response: JsonObject)

    fun onError(message: String)


}