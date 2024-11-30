package sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda

class DownloadReportRequest {
    var userName = ""
    var clientName = ""
    var bwt = false
    var email = ""
    var schedule = false
    var rateValue = ""
    var rateUnit = ""
    var scheduleDuration = ""
    var startDate : Long = 1L
    var endDate : Long = 1L
    var scheduleStartDate = 1L
    var scheduleEndDate = 1L
    var usageStats = false
    var collectionStats = false
    var upiStats = false
    var feedbackStats = false
    var bwtStats = false
    var complex = arrayListOf<String>()
    var duration = 41
}