package sukriti.ngo.mis.ui.administration.lambda.GrantPermission

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.ClientListLambdaRequest
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.ClientListLambdaResult

interface GetPermissionDataLambdaHandler {

    @LambdaFunction
    fun mis_administration_getpermission_ui(request: ClientPermissionLambdaRequest?): ClientPermissionLambdaResult?
}