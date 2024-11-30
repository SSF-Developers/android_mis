package sukriti.ngo.mis.repository.entity

import android.util.Log
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.Gson
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData
import sukriti.ngo.mis.utils.Nomenclature.getCmsConfigOptions
import java.util.ArrayList

@Entity
data class CmsConfig(
    @PrimaryKey(autoGenerate = true)
    var _index: Int = 0,
    val _ID: String,
    var Airdryerautoontimer: String,
    var Airdryerdurationtimer: String,
    var Autoairdryerenabled: String,
    var Autofanenabled: String,
    var Autofloorenabled: String,
    var Autofullflushenabled: String,
    var Autolightenabled: String,
    var Autominiflushenabled: String,
    var Autopreflush: String,
    val Characteristic: String,
    val CITY: String,
    val CLIENT: String,
    val COMPLEX: String,
    val DEVICE_TIMESTAMP: String,
    val DISTRICT: String,
    var Exitafterawaytimer: String,
    var Fanautoofftimer: String,
    var Fanautoontimer: String,
    var Floorcleancount: String,
    var Floorcleandurationtimer: String,
    var fullflushactivationtimer: String,
    var fullflushdurationtimer: String,
    var Lightautoofftime: String,
    var Lightautoontimer: String,
    var Miniflushactivationtimer: String,
    var Miniflushdurationtimer: String,
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
        val list: MutableList<PropertyNameValueData> =  ArrayList()

        Log.i("__ConfigList", Gson().toJson(this))

        setDefaultValues()
        replaceBooleanWithValues()

        list.add(PropertyNameValueData("Air Dryer Auto On Timer",Airdryerautoontimer))
        list.add(PropertyNameValueData("Air Dryer Duration Timer",Airdryerdurationtimer))
        list.add(PropertyNameValueData("Auto Air Dryer Enabled",Autoairdryerenabled))
        list.add(PropertyNameValueData("Auto Fan Enabled",Autofanenabled))
        list.add(PropertyNameValueData("Auto Floor Enabled",Autofloorenabled))
        list.add(PropertyNameValueData("Auto Full Flush Enabled",Autofullflushenabled))
        list.add(PropertyNameValueData("Auto Light Enabled",Autolightenabled))
        list.add(PropertyNameValueData("Auto Mini flush Enabled",Autominiflushenabled))
        list.add(PropertyNameValueData("Auto Pre Flush",Autopreflush))
        list.add(PropertyNameValueData("Axit After Away Timer",Exitafterawaytimer))
        list.add(PropertyNameValueData("Fan Auto Off Timer",Fanautoofftimer))
        list.add(PropertyNameValueData("Fan Auto On Timmer",Fanautoontimer))
        list.add(PropertyNameValueData("Floor Clean Count",Floorcleancount))
        list.add(PropertyNameValueData("Floor Clean Duration Timer",Floorcleandurationtimer))
        list.add(PropertyNameValueData("Full Flush Activation Timer",fullflushactivationtimer))
        list.add(PropertyNameValueData("Full Flush Duration Timer",fullflushdurationtimer))
        list.add(PropertyNameValueData("Light Auto Off Time",Lightautoofftime))
        list.add(PropertyNameValueData("Light Auto On Timer",Lightautoontimer))
        list.add(PropertyNameValueData("Mini Flush Activation Timer",Miniflushactivationtimer))
        list.add(PropertyNameValueData("Mini Flush Duration Timer",Miniflushdurationtimer))


        return list
    }


    @Ignore
    private fun setDefaultValues(){
        //Defdault Values
        Airdryerautoontimer = putDefaultValue(Airdryerautoontimer,"","4")
        Airdryerdurationtimer = putDefaultValue(Airdryerdurationtimer,"","15")

        //Default Index
        Autoairdryerenabled = putDefaultValue(Autoairdryerenabled,"","1")
        Autofanenabled = putDefaultValue(Autofanenabled,"","1")
        Autofloorenabled = putDefaultValue(Autofloorenabled,"","1")
        Autofullflushenabled = putDefaultValue(Autofullflushenabled,"","1")
        Autolightenabled = putDefaultValue(Autolightenabled,"","1")
        Autominiflushenabled = putDefaultValue(Autominiflushenabled,"","1")
        Autopreflush = putDefaultValue(Autopreflush,"","1")

        //Defdault Values
        Exitafterawaytimer = putDefaultValue(Exitafterawaytimer,"","10")
        Fanautoofftimer = putDefaultValue(Fanautoofftimer,"","10")
        Fanautoontimer = putDefaultValue(Fanautoontimer,"","45")
        Floorcleancount = putDefaultValue(Floorcleancount,"","5")
        Floorcleandurationtimer = putDefaultValue(Floorcleandurationtimer,"","8")
        fullflushactivationtimer = putDefaultValue(fullflushactivationtimer,"","90")
        fullflushdurationtimer = putDefaultValue(fullflushdurationtimer,"","4")
        Lightautoofftime = putDefaultValue(Lightautoofftime,"","2")
        Lightautoontimer = putDefaultValue(Lightautoontimer,"","0")
        Miniflushactivationtimer = putDefaultValue(Miniflushactivationtimer,"","20")
        Miniflushdurationtimer = putDefaultValue(Miniflushdurationtimer,"","1")
    }

    @Ignore
    fun putDefaultValue(input:String,notSetValue:String,defaultValue:String):String{
        if(input==notSetValue)
            return defaultValue
        else
            return input
    }

    @Ignore
    private fun replaceBooleanWithValues(){
        Log.i("_replaceWithValues",""+Autoairdryerenabled)
        Autoairdryerenabled = replaceWithValue(Autoairdryerenabled)
        Log.i("_replaceWithValues",""+Autoairdryerenabled)

        Autofanenabled = replaceWithValue(Autofanenabled)
        Autofloorenabled = replaceWithValue(Autofloorenabled)
        Autofullflushenabled = replaceWithValue(Autofullflushenabled)
        Autolightenabled = replaceWithValue(Autolightenabled)
        Autominiflushenabled = replaceWithValue(Autominiflushenabled)
        Autopreflush = replaceWithValue(Autopreflush)
    }

    @Ignore
    fun replaceWithValue(input:String):String{
        if(input=="0")
            return getCmsConfigOptions()[0]
        else if(input=="1")
            return getCmsConfigOptions()[1]
        else
            return  "-1"
    }
}
