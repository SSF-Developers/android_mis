package sukriti.ngo.mis.repository.data

import java.util.*

data class DailyBwtCollectionData(
    var fromDate: Date,
    var toDate: Date,
    var total: List<DailyChargeCollection>,
    var male: List<DailyChargeCollection>,
    var female: List<DailyChargeCollection>,
    var pd: List<DailyChargeCollection>,
    var mur: List<DailyChargeCollection>,
    var durationLabel: String
)
