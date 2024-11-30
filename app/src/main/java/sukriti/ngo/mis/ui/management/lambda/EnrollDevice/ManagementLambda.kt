package sukriti.ngo.mis.ui.management.lambda.EnrollDevice

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject
import org.json.JSONObject
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.RegisterCabinPayload
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.RegisterComplexPayload
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.UpdateComplexPayload

interface ManagementLambda {

    @LambdaFunction
    fun Enterprise_Crud_Iot_ComplexTree(request: ManagementLambdaRequest): JsonObject
    @LambdaFunction
    fun Enterprise_Crud_Iot_ComplexTree(request: UpdateComplexPayload): JsonObject

    @LambdaFunction
    fun Enterprise_Crud_Iot_ComplexTree(request: RequestManagementLambda): JsonObject

    @LambdaFunction
    fun Enterprise_Crud_Iot_ComplexTree(request: RegisterComplexPayload): JsonObject

    @LambdaFunction
    fun Enterprise_Crud_Iot_Cabin(request: ManagementLambdaRequest): JsonObject
    @LambdaFunction
    fun Enterprise_Crud_Iot_Cabin(request: RegisterCabinPayload): JsonObject

    @LambdaFunction
    fun Enterprise_Crud_Iot_Cabin(request: JsonObject): JsonObject

}