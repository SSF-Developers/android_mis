package sukriti.ngo.mis.ui.management.lambda.FetchClientLogo

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject

interface FetchClientLogoLambda {

    @LambdaFunction
    fun fetchClientLogo(request: JsonObject): JsonObject
}