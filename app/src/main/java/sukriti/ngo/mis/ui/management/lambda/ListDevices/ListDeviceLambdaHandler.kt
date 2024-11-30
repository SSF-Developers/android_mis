package sukriti.ngo.mis.ui.management.lambda.ListDevices

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction

interface ListDeviceLambdaHandler {
    @LambdaFunction
    fun Android_Management_Device(request: ListDeviceLambdaRequest): ListDeviceLambdaResponse
}