package sukriti.ngo.mis.repository.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.ui.login.data.UserProfile
import java.util.*

@Entity
data class Ticket(
    @PrimaryKey(autoGenerate = false)
    var ticket_id:String,
    var ticket_status:String,
    var creator_role: String,
    var creator_id: String,
    var complex_name: String,
    var state_name: String,
    var district_name: String,
    var city_name: String,
    var state_code: String,
    var district_code: String,
    var city_code: String,
    var client_name: String,
    var title: String,
    @ColumnInfo(defaultValue = "")
    var description: String,
    var criticality: String,
    var yearMonthCode: String,
    var timestamp: Long,
    @ColumnInfo(defaultValue = "")
    var assignment_type: String,
    @ColumnInfo(defaultValue = "")
    var assignment_comment: String,
    @ColumnInfo(defaultValue = "")
    var assigned_by: String,
    @ColumnInfo(defaultValue = "")
    var lead_id : String,
    @ColumnInfo(defaultValue = "")
    var lead_role: String,
    @ColumnInfo(defaultValue = "false")
    var isQueuedForUser : Boolean,
    @ColumnInfo(defaultValue = "false")
    var isUnQueued : Boolean
){
    @Ignore
    constructor(): this("","","","","",
        "","","","","","","",
    "","","","",0,"","","",
    "","",false,false)
}
