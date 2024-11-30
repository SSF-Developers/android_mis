package sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise

import sukriti.ngo.mis.ui.management.lambda.CreateEnterprise.CreateEnterpriseLambdaResponse

interface DeleteEnterpriseResponseHandler {

    fun onSuccess(response: DeleteEnterpriseLambdaResponse)

    fun onError(message: String?)
}