package sukriti.ngo.mis.ui.management.lambda.EnrollDevice

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject

interface ListPolicyLambda {

    @LambdaFunction
    fun Enterprises_List_Policies_Android_Management(request: ListPolicyLambdaRequest): ListPolicyLambdaResponse


}