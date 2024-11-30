package sukriti.ngo.mis.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData
import sukriti.ngo.mis.utils.Nomenclature

@Entity
data class Cabin(
    @PrimaryKey(autoGenerate = true)
    var _index: Int = 0,
    val ThingName: String,
    val CabinType: String,
    val ShortThingName: String,
    val SmartnessLevel: String,
    val UserType: String,
    val UsageChargeType: String,
    val Suffix: String,
    val UrinalCount: String,
    val BwtCapacity: String,
    val BwtLevel: String,
    val lastAccessTimestamp: String

){
    constructor() :
            this(0,"","","","","",
                "","","","","","") {
    }
}
