package sukriti.ngo.mis.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import sukriti.ngo.mis.repository.utils.TimestampConverter
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import java.util.*

@Entity
data class
UsageProfile(
    @PrimaryKey(autoGenerate = true)
    var _index: Int = 0,
    val _ID: String,
    val Airdryer: String,
    val Amountcollected: String,
    val Amountremaining: String,
    val Characteristic: String,
    var CITY: String,
    val CLIENT: String,
    val COMPLEX: String,
    val DEVICE_TIMESTAMP: String,
    var DISTRICT: String,
    val Duration: String,
    val END_TIME: String,
    val Entrytype: String,
    val Fantime: String,
    val feedback: String,
    val Floorclean: String,
    val Fullflush: String,
    val Lighttime: String,
    val Manualflush: String,
    val Miniflush: String,
    val Preflush: String,
    val RFID: String,
    val SendToAws: String,
    val SendToDevic: String,
    val SERVER_TIMESTAMP: String,
    val SHORT_THING_NAME: String,
    val START_TIME: String,
    var STATE: String,
    val THING_NAME: String,
    //val timestamp: String,
    val TimeStamp: String,
    val ttl: String,
    val version_code: String
)
