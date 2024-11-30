package sukriti.ngo.mis.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.utils.Nomenclature

@Entity
data class Complex(
    @PrimaryKey(autoGenerate = true)
    var _index: Int = 0,
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
    val lastAccessTimestamp: String
){
    constructor() :
            this(0,"","","","","",
                "","","","","","",""
            ,"","") {
    }
}
