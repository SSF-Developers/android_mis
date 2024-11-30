package sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda

interface DeleteScheduledReportUserLambdaResultHandler {

    fun onSuccess(result: DeleteScheduleReportUserLambdaResult)

    fun onError(message: String)
}