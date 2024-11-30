package sukriti.ngo.mis.repository.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import sukriti.ngo.mis.repository.utils.DateConverter

@Entity(primaryKeys = ["ticket_id","timestamp"])
data class TicketProgress(
    var comments:String,
    var current_status: String,
    var event: String,
    var previous_status: String,
    var ticket_id: String,
    var timestamp: String,
    var user_id: String,
    var user_role: String,
    var visibility: String,
    @ColumnInfo(defaultValue = "")
    var date: String,
    @ColumnInfo(defaultValue = "")
    var time: String
){
    fun setDateTime(){
        this.date = DateConverter.toDbDate(java.lang.Long.valueOf(timestamp))
        this.time = DateConverter.toDbTime(java.lang.Long.valueOf(timestamp))
    }
}
