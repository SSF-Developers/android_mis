package sukriti.ngo.mis.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AqiLumen(
    @PrimaryKey(autoGenerate = true)
    var _index: Int = 0,
    var _ID: String,
    val Characteristic: String,
    val CITY: String,
    val CLIENT: String,
    val COMPLEX: String,
    var concentrationCH4: String,
    var concentrationCO: String,
    var concentrationLuminosityStatus: String,
    var concentrationNH3: String,
    var DEVICE_TIMESTAMP: String,
    val DISTRICT: String,
    val initiatedFor: String,
    val SendToAws: String,
    val SendToDevic: String,
    var SERVER_TIMESTAMP: String,
    val SHORT_THING_NAME: String,
    val STATE: String,
    val THING_NAME: String,
    //val TimeStamp: String,
    //val timestamp: String,
    var ttl: String,
    var version_code: String
)
