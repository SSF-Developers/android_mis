package sukriti.ngo.mis.ui.tickets.lambda.CreateTicket;

public class CreateTicketLambdaResult {
    public int result;
    public String message;
    public String ticketId;

    public CreateTicketLambdaResult() {
    }

    public CreateTicketLambdaResult(int result, String message, String ticketId) {
        this.result = result;
        this.message = message;
        this.ticketId = ticketId;
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

    public String getTicketId() {return ticketId;}
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

}
