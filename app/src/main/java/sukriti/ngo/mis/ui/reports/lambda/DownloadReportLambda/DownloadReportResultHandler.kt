package sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda

interface DownloadReportResultHandler {

    fun onSuccess(result: DownloadReportLambdaResult)

    fun onError(message: String)
}