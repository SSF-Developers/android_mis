package sukriti.ngo.mis.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import sukriti.ngo.mis.repository.utils.TimestampConverter
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import java.util.*

@Entity
data class DailyUsageCharge(
    @PrimaryKey(autoGenerate = true)
    var _index: Int = 0,
    val _ID: String,
    val date: String,
    val time: String,
    val timestamp: Long,
    var cabin: String,
    var complex: String,
    var city: String,
    var district: String,
    var state: String,
    var client: String,
    val type: String,
    var duration: Int,
    var amount: Double
)
