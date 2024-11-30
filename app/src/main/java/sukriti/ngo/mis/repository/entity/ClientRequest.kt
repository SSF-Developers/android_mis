package sukriti.ngo.mis.repository.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData
import java.util.ArrayList

@Entity
data class ClientRequest(
    @PrimaryKey(autoGenerate = true)
    var _index: Int = 0,
    val AQI_CO: String,
    val AQI_NH3: String,
    val AVERAGE_FEEDBACK: String,
    val CITY: String,
    val CLIENT: String,
    val COMPLEX: String,
    val DEVICE_TIMESTAMP: String,
    val DISTRICT: String,
    val HEALTH_FAN: String,
    val HEALTH_FLOOR_CLEAN: String,
    val HEALTH_FLUSH: String,
    val HEALTH_LIGHT: String,
    val LUMINOSITY: String,
    val SERVER_TIMESTAMP: String,
    val STATE: String,
    val THING_NAME: String,
    val THING_STATUS: String,
    val TOTAL_USAGE: String,
    val WATER_LEVEL: String
){
    @Ignore
    fun getPropertiesList():List<PropertyNameValueData>{
        val list: MutableList<PropertyNameValueData> =
            ArrayList()
        list.add(PropertyNameValueData("Carbon Monoxide",AQI_CO))
        list.add(PropertyNameValueData("Ammonia",AQI_NH3))
        list.add(PropertyNameValueData("Average Feedback",AVERAGE_FEEDBACK))
        list.add(PropertyNameValueData("Fan Health",HEALTH_FAN))
        list.add(PropertyNameValueData("Floor CLean Health",HEALTH_FLOOR_CLEAN))
        list.add(PropertyNameValueData("Flush Health",HEALTH_FLUSH))
        list.add(PropertyNameValueData("Light Health",HEALTH_LIGHT))
        list.add(PropertyNameValueData("Luminosity",LUMINOSITY))
        list.add(PropertyNameValueData("Thing Status",THING_STATUS))
        list.add(PropertyNameValueData("Total Usage",TOTAL_USAGE))
        list.add(PropertyNameValueData("Water Level",WATER_LEVEL))
        return list
    }
}
