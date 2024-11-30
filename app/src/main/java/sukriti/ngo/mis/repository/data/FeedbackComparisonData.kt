package sukriti.ngo.mis.repository.data

import java.util.*

data class FeedbackComparisonData(
    var fromDate: Date,
    var toDate: Date,
    var _1starData: List<DailyFeedbackCount>,
    var _2starData: List<DailyFeedbackCount>,
    var _3starData: List<DailyFeedbackCount>,
    var _4starData: List<DailyFeedbackCount>,
    var _5starData: List<DailyFeedbackCount>,
    var durationLabel: String
) {
}
