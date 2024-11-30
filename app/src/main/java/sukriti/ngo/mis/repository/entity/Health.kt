package sukriti.ngo.mis.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Health(
    @PrimaryKey(autoGenerate = true)
    var _index: Int = 0,
    val _ID: String,
    val airDryerHealth: String,
    val Characteristic: String,
    val chokeHealth: String,
    val CITY: String,
    val CLIENT: String,
    val COMPLEX: String,
    val DEVICE_TIMESTAMP: String,
    val DISTRICT: String,
    val fanHealth: String,
    val floorCleanHealth: String,
    val flushHealth: String,
    val freshWaterLevel: String,
    val initiatedFor: String,
    val lightHealth: String,
    val lockHealth: String,
    val lockStatus: String,
    val odsHealth: String,
    var recycleWaterLevel: String,
    val SendToAws: String,
    val SendToDevic: String,
    val SERVER_TIMESTAMP: String,
    val SHORT_THING_NAME: String,
    val STATE: String,
    val tapHealth: String,
    val THING_NAME: String,
    //val timestamp: String,
    //val TimeStamp: String,
    val ttl: String,
    val version_code: String
)
