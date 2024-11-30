package sukriti.ngo.mis.ui.management.lambda.DeleteComplex

import com.google.gson.JsonObject

interface DeleteComplexResponseHandler {

    fun onSuccess(response: JsonObject)

    fun onError(message: String)
}