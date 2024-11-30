package sukriti.ngo.mis.ui.tickets.lambda.ListProgress;

public class ListTicketProgressLambdaRequest {
    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    String ticketId;

    public ListTicketProgressLambdaRequest(String ticketId) {
        this.ticketId = ticketId;
    }

    public ListTicketProgressLambdaRequest() {

    }
}
