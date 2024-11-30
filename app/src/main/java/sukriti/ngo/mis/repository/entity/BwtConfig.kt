package sukriti.ngo.mis.repository.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData
import java.util.ArrayList

@Entity
data class BwtConfig(
    @PrimaryKey(autoGenerate = true)
    var _index: Int = 0,
    val _ID: String,
    val CITY: String,
    val CLIENT: String,
    val COMPLEX: String,
    val DEVICE_TIMESTAMP: String,
    val DISTRICT: String,
    val SendToAws: String,
    val SendToDevic: String,
    val SHORT_THING_NAME: String,
    val STATE: String,
    val THING_NAME: String
    //val TimeStamp: String,
    //val timestamp: String
){
    @Ignore
    fun getPropertiesList():List<PropertyNameValueData>{
        val list: MutableList<PropertyNameValueData> =
            ArrayList()
        return list
    }
}
