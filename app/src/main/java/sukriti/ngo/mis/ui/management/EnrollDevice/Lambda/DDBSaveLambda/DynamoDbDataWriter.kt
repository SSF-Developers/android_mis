package sukriti.ngo.mis.ui.management.EnrollDevice.Lambda.DDBSaveLambda

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject

interface DynamoDbDataWriter {

    @LambdaFunction
    fun Enrollment_device_crud_details(payload: JsonObject): JsonObject
}