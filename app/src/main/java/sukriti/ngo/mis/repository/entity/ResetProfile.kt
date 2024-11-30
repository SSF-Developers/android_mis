package sukriti.ngo.mis.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ResetProfile(
    @PrimaryKey(autoGenerate = true)
    var _index: Int = 0,
    val userId: Long,
    val _ID: String,
    val BoardId: String,
    val Characteristic: String,
    val CITY: String,
    val CLIENT: String,
    val COMPLEX: String,
    val DEVICE_TIMESTAMP: String,
    val DISTRICT: String,
    val Resetsource: String,
    val SendToAws: String,
    val SendToDevic: String,
    val SHORT_THING_NAME: String,
    val STATE: String,
    val THING_NAME: String,
    //val TimeStamp: String,
    //val timestamp: String,
    val ttl: String,
    val version_code: String
)
