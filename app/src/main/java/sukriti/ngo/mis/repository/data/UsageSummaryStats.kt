package sukriti.ngo.mis.repository.data

import java.util.*

data class UsageSummaryStats(
    var fromDate: Date,
    var toDate: Date,
    var total: Int,
    var mwc: Int = 0,
    var fwc: Int = 0,
    var pwc: Int,
    var mur: Int,
    var durationLabel: String
) {
}
