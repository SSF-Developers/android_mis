package sukriti.ngo.mis.ui.management.lambda.ListDevices

import sukriti.ngo.mis.ui.management.lambda.ListEnterprise.ListEnterpriseResponse

interface ListDeviceResponseHandler {
    fun onSuccess(response: ListDeviceLambdaResponse)

    fun onError(message: String?)
}