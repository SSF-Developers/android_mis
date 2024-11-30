package sukriti.ngo.mis.ui.administration.lambda.GrantPermission

interface SubmitPermissionResultHandler {

    fun onSuccess(result: SubmitPermissionResult)

    fun onError(message: String)
}