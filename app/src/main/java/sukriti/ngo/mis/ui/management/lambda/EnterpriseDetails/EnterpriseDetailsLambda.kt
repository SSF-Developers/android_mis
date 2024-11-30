package sukriti.ngo.mis.ui.management.lambda.EnterpriseDetails

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject

interface EnterpriseDetailsLambda {

    @LambdaFunction
    fun Get_Enterprises_Android_Management(request: JsonObject): JsonObject
}