package sukriti.ngo.mis.ui.tickets.lambda.ListTicket;

public class ListTicketsLambdaResult {

    public int result;
    public String message;
    public String allTickets;
    public String queuedTickets;
    public String unQueuedTickets;
    public String ticketProgress;

    public ListTicketsLambdaResult() {
    }

    public ListTicketsLambdaResult(int result, String message, String allTickets, String queuedTickets,String unQueuedTickets) {
        this.result = result;
        this.message = message;
        this.allTickets = allTickets;
        this.queuedTickets = queuedTickets;
        this.unQueuedTickets = unQueuedTickets;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAllTickets() {
        return allTickets;
    }

    public void setAllTickets(String allTickets) {
        this.allTickets = allTickets;
    }

    public String getQueuedTickets() {
        return queuedTickets;
    }

    public void setQueuedTickets(String queuedTickets) {
        this.queuedTickets = queuedTickets;
    }

    public String getUnQueuedTickets() {
        return unQueuedTickets;
    }

    public void setUnQueuedTickets(String unQueuedTickets) {
        this.unQueuedTickets = unQueuedTickets;
    }

    public String getTicketProgress() {
        return ticketProgress;
    }

    public void setTicketProgress(String ticketProgress) {
        this.ticketProgress = ticketProgress;
    }
}
