package sukriti.ngo.mis.ui.management.lambda.VerifyPackageName

import com.google.gson.JsonObject

interface VerifyPackageNameResponseHandler {
    fun onSuccess(response: JsonObject)

    fun onError(message: String)
}