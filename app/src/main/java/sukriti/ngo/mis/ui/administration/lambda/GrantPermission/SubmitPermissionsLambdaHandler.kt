package sukriti.ngo.mis.ui.administration.lambda.GrantPermission

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction

interface SubmitPermissionsLambdaHandler {
    @LambdaFunction
    fun mis_administration_ui (request: SubmitPermissionsRequest?): SubmitPermissionResult?
}