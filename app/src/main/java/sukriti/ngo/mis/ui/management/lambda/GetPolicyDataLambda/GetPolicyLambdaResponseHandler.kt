package sukriti.ngo.mis.ui.management.lambda.GetPolicyDataLambda

interface GetPolicyLambdaResponseHandler {

    fun onSuccess(response: GetPolicyLambdaResponse)

    fun onError(message: String)

}