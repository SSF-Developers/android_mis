package sukriti.ngo.mis.ui.management.lambda.DeleteComplex

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject

interface DeleteComplexLambda {

    @LambdaFunction
    fun Enterprise_Crud_Iot_ComplexTree(request: JsonObject): JsonObject
}