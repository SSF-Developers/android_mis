package sukriti.ngo.mis.ui.administration.lambda.GrantPermission

interface GetPermissionDataResultHandler {

    fun onSuccess(permissionData: ClientPermissionLambdaResult)

    fun onError(message: String);
}