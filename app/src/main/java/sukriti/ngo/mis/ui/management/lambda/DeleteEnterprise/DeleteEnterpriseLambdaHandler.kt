package sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction

interface DeleteEnterpriseLambdaHandler {

    @LambdaFunction
    fun Soft_delete_enterprise(request: DeleteEnterpriseLambdaRequest): DeleteEnterpriseLambdaResponse
}