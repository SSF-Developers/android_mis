package sukriti.ngo.mis.ui.management.lambda.DeleteDevice

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject

interface DeleteDeviceLambdaHandler {

    @LambdaFunction
    fun Android_Management_Device(request: JsonObject): JsonObject
}