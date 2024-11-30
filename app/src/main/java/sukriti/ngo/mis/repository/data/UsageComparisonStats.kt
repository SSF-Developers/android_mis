package sukriti.ngo.mis.repository.data

import java.util.*

data class UsageComparisonStats(
    var fromDate: Date,
    var toDate: Date,
    var total: List<DailyUsageCount>,
    var male: List<DailyUsageCount>,
    var female: List<DailyUsageCount>,
    var pd: List<DailyUsageCount>,
    var mur: List<DailyUsageCount>,
    var durationLabel: String
)
