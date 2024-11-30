package sukriti.ngo.mis.repository.data

import java.util.*

data class ChargeCollectionStats(
    var fromDate: Date,
    var toDate: Date,
    var total: Float,
    var male: Float,
    var female: Float,
    var pd: Float,
    var mur: Float,
    var durationLabel: String
)
