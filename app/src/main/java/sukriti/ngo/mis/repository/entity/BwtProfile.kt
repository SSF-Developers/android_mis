package sukriti.ngo.mis.repository.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class BwtProfile(
    @PrimaryKey(autoGenerate = true)
    var _index: Int = 0,
    val _ID: String,
    val CITY: String,
    val CLIENT: String,
    val COMPLEX: String,
    val DEVICE_TIMESTAMP: String,
    val SERVER_TIMESTAMP: String,
    val DISTRICT: String,
    val SendToAws: String,
    val SendToDevic: String,
    val SHORT_THING_NAME: String,
    val STATE: String,
    val THING_NAME: String,
    val ttl: String,
    val version_code: String,
    val waterRecycled: String,
    val airBlowerRunTime: String,
    val currentWaterLevel: String,
    val filterState: String,
    val ozonatorRunTime: String,
    val tpRunTime: String,
    val turbidityLevel: String,
    val Characteristic: String,
    var date: String
)
