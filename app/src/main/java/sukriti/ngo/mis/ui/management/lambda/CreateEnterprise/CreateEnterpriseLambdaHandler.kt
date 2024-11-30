package sukriti.ngo.mis.ui.management.lambda.CreateEnterprise

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction

interface CreateEnterpriseLambdaHandler {
    @LambdaFunction
    fun Create_Enterprises_API(): CreateEnterpriseLambdaResponse
}