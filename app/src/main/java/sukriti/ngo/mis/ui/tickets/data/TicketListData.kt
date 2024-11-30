package sukriti.ngo.mis.ui.tickets.data

import sukriti.ngo.mis.repository.entity.Ticket


data class TicketListData(
    var allTickets:List<Ticket>,
    var queuedTickets:List<Ticket>,
    var unQueuedTicketData:List<Ticket>
){
    constructor(): this(mutableListOf<Ticket>(),mutableListOf<Ticket>(),
        mutableListOf<Ticket>())
}
