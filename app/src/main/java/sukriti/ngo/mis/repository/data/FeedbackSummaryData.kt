package sukriti.ngo.mis.repository.data

import java.util.*

data class FeedbackSummaryData(
    var fromDate: Date,
    var toDate: Date,
    var mwc: List<FeedbackWiseUserCount>,
    var fwc: List<FeedbackWiseUserCount>,
    var pwc: List<FeedbackWiseUserCount>,
    var mur: List<FeedbackWiseUserCount>,
    var durationLabel: String
) {
}
