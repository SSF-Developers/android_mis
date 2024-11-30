package sukriti.ngo.mis.repository.entity

import android.util.Log
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData
import java.util.ArrayList

@Entity
data class OdsConfig(
    @PrimaryKey(autoGenerate = true)
    var _index: Int = 0,
    val _ID: String,
    var Ambientfloorfactor: String,
    var Ambientseatfactor: String,
    val Characteristic: String,
    val CITY: String,
    val CLIENT: String,
    val COMPLEX: String,
    val DEVICE_TIMESTAMP: String,
    val DISTRICT: String,
    var Seatfloorratio: String,
    var Seatthreshold: String,
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
        val list: MutableList<PropertyNameValueData> = ArrayList()
        Log.i("__ConfigList", Gson().toJson(this))

        setDefaultValues()

        list.add(PropertyNameValueData("Ambient Floor Factor",Ambientfloorfactor))
        list.add(PropertyNameValueData("Ambient Seat Factor",Ambientseatfactor))
        list.add(PropertyNameValueData("Seat Floor Ratio",Seatfloorratio))
        list.add(PropertyNameValueData("Seat Threshold",Seatthreshold))

        return list
    }

    @Ignore
    private fun setDefaultValues(){
        //Defdault Values
        Ambientfloorfactor = putDefaultValue(Ambientfloorfactor,"","35")
        Ambientseatfactor = putDefaultValue(Ambientseatfactor,"","9")
        Seatfloorratio = putDefaultValue(Seatfloorratio,"","6")
        Seatthreshold = putDefaultValue(Seatthreshold,"","5")
    }

    @Ignore
    fun putDefaultValue(input:String,notSetValue:String,defaultValue:String):String{
        if(input==notSetValue)
            return defaultValue
        else
            return input
    }

}
