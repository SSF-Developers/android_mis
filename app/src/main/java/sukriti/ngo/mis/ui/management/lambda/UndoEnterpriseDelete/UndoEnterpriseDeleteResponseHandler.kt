package sukriti.ngo.mis.ui.management.lambda.UndoEnterpriseDelete

interface UndoEnterpriseDeleteResponseHandler {

    fun onSuccess(response: UndoEnterpriseDeleteResponse)

    fun onError(message: String)
}