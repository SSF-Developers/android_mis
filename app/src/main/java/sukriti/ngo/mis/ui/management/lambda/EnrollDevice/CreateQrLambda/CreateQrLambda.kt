package sukriti.ngo.mis.ui.management.lambda.EnrollDevice.CreateQrLambda

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction

interface CreateQrLambda {
    @LambdaFunction
    fun Create_QR_API(request: CreateQrRequest): CreateQrResponse
}