package sukriti.ngo.mis.ui.management.lambda.EnrollDevice

import com.google.gson.JsonObject

interface ListPolicyLambdaResponseHandler {


    fun onSuccess(response: ListPolicyLambdaResponse)



    fun onError(message: String)
}