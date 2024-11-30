package sukriti.ngo.mis.ui.management.lambda.CreateEnterprise

import sukriti.ngo.mis.ui.management.lambda.ListDevices.ListDeviceLambdaResponse

interface CreateEnterpriseResponseHandler {
    fun onSuccess(response: CreateEnterpriseLambdaResponse)

    fun onError(message: String?)
}