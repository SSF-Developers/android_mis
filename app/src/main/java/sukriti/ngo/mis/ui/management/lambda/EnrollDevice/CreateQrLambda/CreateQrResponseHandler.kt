package sukriti.ngo.mis.ui.management.lambda.EnrollDevice.CreateQrLambda

interface CreateQrResponseHandler {

    fun onSuccess(response: CreateQrResponse)

    fun onError(message: String)
}