package sukriti.ngo.mis.ui.management.lambda.VerifyPackageName

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject

interface VerifyPackageNameLambda {

    @LambdaFunction
    fun VerifyPackageName(request: JsonObject): JsonObject
}