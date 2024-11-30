package sukriti.ngo.mis.ui.management.lambda.GetPolicyDataLambda

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject

interface GetPolicyLambda {

    @LambdaFunction
    fun Enterprises_get_Policies_Android_Management(request: JsonObject): GetPolicyLambdaResponse
}