package sukriti.ngo.mis.ui.management.lambda.PatchEnterprise

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject
import sukriti.ngo.mis.ui.management.lambda.ListDevices.ListDeviceLambdaRequest
import sukriti.ngo.mis.ui.management.lambda.ListDevices.ListDeviceLambdaResponse

interface PatchEnterpriseLambda {

    @LambdaFunction
    fun Android_Management_Device(payload: JsonObject): JsonObject

    @LambdaFunction
    fun Android_Management_Device(request: ListDeviceLambdaRequest): ListDeviceLambdaResponse
}