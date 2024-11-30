package sukriti.ngo.mis.ui.reports.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import sukriti.ngo.mis.repository.entity.BwtProfile
import sukriti.ngo.mis.repository.entity.UsageProfile
import sukriti.ngo.mis.repository.utils.TimestampConverter
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import java.util.*

data class UsageReportRawData(
    var cabinType: String,
    var cabinCount: Int,
    var usage: Int,
    var feedback: Float,
    var collection: Float,
    var raisedTicketCount: Int
){
    constructor(cabinType: String, cabinCount: Int,usage: Int,feedback: Float,collection: Float):
            this(cabinType,cabinCount,usage,feedback,collection,0)
}
