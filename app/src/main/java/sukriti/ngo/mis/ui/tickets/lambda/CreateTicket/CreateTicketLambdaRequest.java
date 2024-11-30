package sukriti.ngo.mis.ui.tickets.lambda.CreateTicket;

import sukriti.ngo.mis.ui.tickets.data.TicketDetailsData;

public class CreateTicketLambdaRequest {
    TicketDetailsData ticketDetailsData;

    public CreateTicketLambdaRequest(TicketDetailsData ticketDetailsData) {
        this.ticketDetailsData = ticketDetailsData;
    }

    public CreateTicketLambdaRequest() {

    }

    public TicketDetailsData getTicketDetailsData(){return ticketDetailsData;}

    public void setTicketDetailsData(TicketDetailsData ticketDetailsData) {
        this.ticketDetailsData = ticketDetailsData;
    }

}
