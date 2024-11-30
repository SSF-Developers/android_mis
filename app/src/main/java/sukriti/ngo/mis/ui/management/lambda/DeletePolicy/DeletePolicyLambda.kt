package sukriti.ngo.mis.ui.management.lambda.DeletePolicy

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject

interface DeletePolicyLambda {

    @LambdaFunction
    fun Enterprises_Delete_Policies_Android_Management(request: JsonObject): JsonObject
}