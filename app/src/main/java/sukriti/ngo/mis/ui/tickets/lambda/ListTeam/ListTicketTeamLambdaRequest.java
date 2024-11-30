package sukriti.ngo.mis.ui.tickets.lambda.ListTeam;

import sukriti.ngo.mis.repository.entity.Ticket;
import sukriti.ngo.mis.ui.tickets.data.TicketDetailsData;

public class ListTicketTeamLambdaRequest {
    Ticket ticketDetailsData;

    public ListTicketTeamLambdaRequest(Ticket ticketDetailsData) {
        this.ticketDetailsData = ticketDetailsData;
    }

    public ListTicketTeamLambdaRequest() {

    }

    public Ticket getTicketDetailsData(){return ticketDetailsData;}

    public void setTicketDetailsData(Ticket ticketDetailsData) {
        this.ticketDetailsData = ticketDetailsData;
    }

}
