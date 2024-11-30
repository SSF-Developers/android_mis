package sukriti.ngo.mis.ui.management.lambda.QrShare

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject

interface QrShareLambda {

    @LambdaFunction
    fun shareQR(request: JsonObject): JsonObject
}