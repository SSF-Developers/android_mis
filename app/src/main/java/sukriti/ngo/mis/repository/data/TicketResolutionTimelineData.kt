package sukriti.ngo.mis.repository.data

import java.util.*

data class TicketResolutionTimelineData(
    var fromDate: Date, var toDate: Date, var total: List<DailyTicketEventSummary>,
    var male: List<DailyTicketEventSummary>, var female: List<DailyTicketEventSummary>,
    var pd: List<DailyTicketEventSummary>,
    var mur: List<DailyTicketEventSummary>, var durationLabel: String
)
