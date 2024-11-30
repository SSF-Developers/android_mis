package sukriti.ngo.mis.ui.tickets.data

import android.net.Uri
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.ui.login.data.UserProfile
import java.util.*

data class TicketDetailsData(
    var ticket_id:String,
    var ticket_status:String,
    var creator_role: String,
    var creator_id: String,

    var complex_name: String,
    var thing_name: String,
    var short_thing_name: String,
    var state_name: String,
    var district_name: String,
    var city_name: String,
    var state_code: String,
    var district_code: String,
    var city_code: String,
    var client_name: String,

    var title: String,
    var description: String,
    var criticality: String,
    var fileList : ArrayList<TicketFile>,

    var yearMonthCode: String,
    var timestamp: Long,

    var assignment_type: String,
    var assignment_comment: String,
    var assigned_by: String,
    var lead_id : String,
    var lead_role: String
){

    constructor():
            this("","","","","",
                "","","","","",
                "","","","","","",
                "",arrayListOf(),"",0,"","","","","")

    constructor(complex: ComplexDetailsData, creator: UserProfile, title: String, description: String, criticality: String):
            this("","",UserProfile.getRoleName(creator.role),creator.user.userName,complex.ComplexName,"","",complex.StateName,complex.DistrictName,complex.CityName,
            complex.StateCode,complex.DistrictCode,complex.CityCode,complex.Client,title,description,criticality,
                arrayListOf(),
                getYearMonth(Calendar.getInstance().timeInMillis),0,"","","","","")

    companion object{
        data class TicketFile(
            var fileType: String,
            var path: Uri,
            var name: String
        )

        fun getYearMonth(timestamp: Long):String{
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp
            var year =  cal[Calendar.YEAR].toString().subSequence(2,4).toString()
            var monthCodes =  arrayOf("A","B","C","D","E","F","G","H","I","J","K","L")
            var monthCode = monthCodes[cal[Calendar.MONTH]]
            return ("$year-$monthCode")
        }
    }
}
