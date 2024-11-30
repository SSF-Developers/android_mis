package sukriti.ngo.mis.repository.data

import java.util.*

data class FeedbackTimelineData(
    var fromDate: Date,
    var toDate: Date,
    var averageData: List<DailyAverageFeedback>,
    var mwcData: List<DailyAverageFeedback>,
    var fwcData: List<DailyAverageFeedback>,
    var pwcData: List<DailyAverageFeedback>,
    var murData: List<DailyAverageFeedback>,
    var durationLabel: String
) {
}
