package sukriti.ngo.mis.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData
import sukriti.ngo.mis.utils.Nomenclature
import java.util.*

@Entity
data class QuickAccess(
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

    val ComplexName: String,
    val Address: String,
    val Lat: String,
    val Lon: String,
    val Date: String,
    val Client: String,
    val StateName: String,
    val DistrictName: String,
    val CityName: String,
    val StateCode: String,
    val DistrictCode: String,
    val CityCode: String,
    val BillingGroup: String,

    var markedForQuickAccessTimestamp: String

) {
    constructor() :
            this(
                0, "", "", "", "", "",
                "", "", "", "", "", "", "", "", "", "", "",
                "", "", "", "", "", "", "", ""
            ) {
    }

    constructor(complex: Complex, cabin: Cabin, TimeStamp: String) :
            this(
                0,
                cabin.ThingName,
                cabin.CabinType,
                cabin.ShortThingName,
                cabin.SmartnessLevel,
                cabin.UserType,
                cabin.UsageChargeType,
                cabin.Suffix,
                cabin.UrinalCount,
                cabin.BwtCapacity,
                cabin.BwtLevel,
                complex.ComplexName,
                complex.Address,
                complex.Lat,
                complex.Lon,
                complex.Date,
                complex.Client,
                complex.StateName,
                complex.DistrictName,
                complex.CityName,
                complex.StateCode,
                complex.DistrictCode,
                complex.CityCode,
                complex.BillingGroup,
                TimeStamp
            ) {
    }

    fun getComplex(): Complex {
        return Complex(
            -1,
            ComplexName,
            Address,
            Lat,
            Lon,
            Date,
            Client,
            StateName,
            DistrictName,
            CityName,
            StateCode,
            DistrictCode,
            CityCode,
            BillingGroup,
            "-1"
        )
    }

    fun getCabin(): Cabin {
        return Cabin(
            -1,
            ThingName,
            CabinType,
            ShortThingName,
            SmartnessLevel,
            UserType,
            UsageChargeType,
            Suffix,
            UrinalCount,
            BwtCapacity,
            BwtLevel,
            "-1"
        )
    }
}
