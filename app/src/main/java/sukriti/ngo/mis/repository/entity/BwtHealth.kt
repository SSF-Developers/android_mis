package sukriti.ngo.mis.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BwtHealth(
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
    val FilterHealth: String,
    val alpValueHealth: String,
    val blowerHealth: String,
    val failSafeHealth: String,
    val mp1ValueHealth: String,
    val mp2ValueHealth: String,
    val mp3ValueHealth: String,
    val mp4ValueHealth: String,
    val ozonatorHealth: String,
    val primingValueHealth: String,
    val pumpHealth: String
)
