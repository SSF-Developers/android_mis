package sukriti.ngo.mis.ui.management.lambda.CreateNewPolicy

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject
import org.json.JSONObject

interface CreatePolicyLambdaHandler {

    @LambdaFunction
    fun Enterprises_Create_Policies_Android_Management(request: JsonObject): JsonObject
}