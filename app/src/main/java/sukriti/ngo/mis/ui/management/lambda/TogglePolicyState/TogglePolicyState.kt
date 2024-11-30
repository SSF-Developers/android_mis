package sukriti.ngo.mis.ui.management.lambda.TogglePolicyState

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject

interface TogglePolicyState {

    @LambdaFunction
    fun Android_Management_Device(request: JsonObject): JsonObject
}