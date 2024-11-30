package sukriti.ngo.mis.repository.entity

import android.util.Log
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData
import sukriti.ngo.mis.utils.Nomenclature
import java.util.*

@Entity
data class UcemsConfig(
    @PrimaryKey(autoGenerate = true)
    var _index: Int = 0,
    val _ID: String,
    var Cabinpaymentmode: String,
    val Characteristic: String,
    val CITY: String,
    val CLIENT: String,
    var Collexpirytime: String,
    val COMPLEX: String,
    val DEVICE_TIMESTAMP: String,
    val DISTRICT: String,
    var Edis_airDryr: String,
    var Edis_choke: String,
    var Edis_cms: String,
    var Edis_fan: String,
    var Edis_floor: String,
    var Edis_flush: String,
    var Edis_freshWtr: String,
    var Edis_light: String,
    var Edis_lock: String,
    var Edis_ods: String,
    var Edis_recWtr: String,
    var Edis_tap: String,
    var Entrychargeamount: String,
    var Exitdoortriggertimer: String,
    var Feedbackexpirytime: String,
    var Occwaitexpirytime: String,
    val SendToAws: String,
    val SendToDevic: String,
    val SHORT_THING_NAME: String,
    val STATE: String,
    val THING_NAME: String
    //val timestamp: String,
    //val TimeStamp: String
){
    @Ignore
    fun getPropertiesList():List<PropertyNameValueData>{

        Log.i("__ConfigList",Gson().toJson(this))

        setDefaultValues()
        replaceCodesWithText()

        val list: MutableList<PropertyNameValueData> = ArrayList()
        list.add(PropertyNameValueData("Entry Charge",Entrychargeamount))
        list.add(PropertyNameValueData("Payment Mode",Cabinpaymentmode))
        list.add(PropertyNameValueData("Air Dryer",Edis_airDryr))
        list.add(PropertyNameValueData("Choke",Edis_choke))
        list.add(PropertyNameValueData("CMS",Edis_cms))
        list.add(PropertyNameValueData("Fan",Edis_fan))
        list.add(PropertyNameValueData("Floor",Edis_floor))
        list.add(PropertyNameValueData("Flush",Edis_flush))
        list.add(PropertyNameValueData("Fresh Water",Edis_freshWtr))
        list.add(PropertyNameValueData("Recycled Water",Edis_recWtr))
        list.add(PropertyNameValueData("Light",Edis_light))
        list.add(PropertyNameValueData("Lock",Edis_lock))
        list.add(PropertyNameValueData("Ods",Edis_ods))
        list.add(PropertyNameValueData("Tap",Edis_tap))
        list.add(PropertyNameValueData("Exit Door Trigger Timer",Exitdoortriggertimer))
        list.add(PropertyNameValueData("Feedback Expiry Time",Feedbackexpirytime))
        list.add(PropertyNameValueData("Occ Wait Expiry Time",Occwaitexpirytime))
        list.add(PropertyNameValueData("Collect Expiry Time",Collexpirytime))

        return list
    }

    @Ignore
    private fun setDefaultValues(){
        Entrychargeamount = putDefaultValue(Entrychargeamount,"","5")
        Cabinpaymentmode = putDefaultValue(Cabinpaymentmode,"","0")
        Edis_airDryr = putDefaultValue(Edis_airDryr,"","1")
        Edis_choke = putDefaultValue(Edis_choke,"","1")
        Edis_cms = putDefaultValue(Edis_cms,"","1")
        Edis_fan = putDefaultValue(Edis_fan,"","1")
        Edis_floor = putDefaultValue(Edis_floor,"","1")
        Edis_flush = putDefaultValue(Edis_flush,"","1")
        Edis_freshWtr = putDefaultValue(Edis_freshWtr,"","1")
        Edis_recWtr = putDefaultValue(Edis_recWtr,"","1")
        Edis_light = putDefaultValue(Edis_light,"","1")
        Edis_lock = putDefaultValue(Edis_lock,"","1")
        Edis_ods = putDefaultValue(Edis_ods,"","1")
        Edis_tap = putDefaultValue(Edis_tap,"","1")
        Collexpirytime = putDefaultValue(Collexpirytime,"","20")
        Exitdoortriggertimer = putDefaultValue(Exitdoortriggertimer,"","5")
        Feedbackexpirytime = putDefaultValue(Feedbackexpirytime,"","20")
        Occwaitexpirytime = putDefaultValue(Occwaitexpirytime,"","20")
    }

    @Ignore
    private fun replaceCodesWithText(){
        Log.i("_replaceWithValues",""+Cabinpaymentmode)
        Cabinpaymentmode = replacePaymentModeWithValue(Cabinpaymentmode)
        Log.i("_replaceWithValues",""+Cabinpaymentmode)

        Log.i("_replaceWithValues",""+Edis_airDryr)
        Edis_airDryr = replaceCriticalWithValue(Edis_airDryr)
        Log.i("_replaceWithValues",""+Edis_airDryr)

        Edis_choke = replaceCriticalWithValue(Edis_choke)
        Edis_cms = replaceCriticalWithValue(Edis_cms)
        Edis_fan = replaceCriticalWithValue(Edis_fan)
        Edis_floor = replaceCriticalWithValue(Edis_floor)
        Edis_flush = replaceCriticalWithValue(Edis_flush)
        Edis_freshWtr = replaceCriticalWithValue(Edis_freshWtr)
        Edis_recWtr = replaceCriticalWithValue(Edis_recWtr)
        Edis_light = replaceCriticalWithValue(Edis_light)
        Edis_lock = replaceCriticalWithValue(Edis_lock)
        Edis_ods = replaceCriticalWithValue(Edis_ods)
        Edis_tap = replaceCriticalWithValue(Edis_tap)
    }

    @Ignore
    fun replacePaymentModeWithValue(input:String):String{
        if(input=="0")
            return Nomenclature.getPaymentModesOptions()[0]
        else if(input=="1")
            return Nomenclature.getPaymentModesOptions()[1]
        else if(input=="2")
            return Nomenclature.getPaymentModesOptions()[2]
        else if(input=="3")
            return Nomenclature.getPaymentModesOptions()[3]
        else
            return  "-1"
    }

    @Ignore
    fun replaceCriticalWithValue(input:String):String{
        if(input=="0")
            return Nomenclature.getUcemsConfigOptions()[0]
        else if(input=="1")
            return Nomenclature.getCmsConfigOptions()[1]
        else
            return  "-1"
    }


    @Ignore
    fun putDefaultValue(input:String,notSetValue:String,defaultValue:String):String{
        if(input==notSetValue)
           return defaultValue
        else
            return input
    }
}
