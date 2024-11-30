package sukriti.ngo.mis.ui.tickets.data

import android.net.Uri
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.ui.login.data.UserProfile
import java.util.*

data class TicketProgressData(


    var ticket_id: String,
    var user_role: String,
    var user_id: String,
    var timestamp: String,
    var comments: String,
    var current_status: String,
    var previous_status: String,
    var event: String
) {

    constructor() :
            this(
                "", "", "", "", "",
                "", "", "")

}
