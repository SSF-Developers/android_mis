package sukriti.ngo.mis.repository.data

import java.util.*

data class FeedbackStatsData(
    var fromDate: Date,
    var toDate: Date,
    var total: FeedbackSummary,
    var mwc: FeedbackSummary,
    var fwc: FeedbackSummary,
    var pwc: FeedbackSummary,
    var mur: FeedbackSummary,
    var durationLabel: String
) {
}
