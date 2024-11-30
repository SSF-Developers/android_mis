package sukriti.ngo.mis.ui.management.lambda.QrShare

import com.google.gson.JsonObject

interface QrShareLambdaResponseHandler {

    fun onSuccess(response: JsonObject)

    fun onError(message: String )
}