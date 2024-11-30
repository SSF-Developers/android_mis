package sukriti.ngo.mis.ui.tickets.data

import android.net.Uri
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.ui.login.data.UserProfile
import java.util.*

data class TicketAction(
    var actionId: Int,
    var actionName: String,
    var actionDescription: String
)