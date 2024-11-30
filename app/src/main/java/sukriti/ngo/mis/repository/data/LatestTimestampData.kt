package sukriti.ngo.mis.repository.data

import sukriti.ngo.mis.utils.ChartDataHelper
import java.util.*

data class LatestTimestampData(
    var healthTimestamp: Long,
    var BwtHealthTimestamp: Long,
    var AqiLumenTimestamp: Long,
    var UcemsConfigTimestamp: Long,
    var OdsConfigTimestamp: Long,
    var CmsConfigTimestamp: Long,
    var ClientRequestTimestamp: Long,
    var BwtConfigTimestamp: Long,
    var UsageProfileTimestamp: Long,
    var ResetProfileTimestamp: Long,
    var BwtProfileTimestamp: Long) {
}
