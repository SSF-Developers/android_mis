package sukriti.ngo.mis.ui.management.lambda.PatchDevice

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject

interface PatchDeviceLambda {

    @LambdaFunction
    fun Android_Management_Device(request: JsonObject): JsonObject


}