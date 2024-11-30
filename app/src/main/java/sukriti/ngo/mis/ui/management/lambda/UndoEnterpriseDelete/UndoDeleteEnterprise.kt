package sukriti.ngo.mis.ui.management.lambda.UndoEnterpriseDelete

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction

interface UndoDeleteEnterprise {

    @LambdaFunction
    fun undo_soft_delete_enterprise(request: UndoEnterpriseDeleteLambdaRequest) : UndoEnterpriseDeleteResponse
}