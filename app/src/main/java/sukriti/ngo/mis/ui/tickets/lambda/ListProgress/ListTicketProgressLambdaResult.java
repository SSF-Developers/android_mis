package sukriti.ngo.mis.ui.tickets.lambda.ListProgress;

public class ListTicketProgressLambdaResult {

    public int result;
    public String message;
    public String ticketProgressList;


    public ListTicketProgressLambdaResult() {
    }

    public ListTicketProgressLambdaResult(int result, String message, String ticketProgressList) {
        this.result = result;
        this.message = message;
        this.ticketProgressList = ticketProgressList;


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

    public String getTicketProgressList() {
        return ticketProgressList;
    }

    public void setTicketProgressList(String ticketProgressList) {
        this.ticketProgressList = ticketProgressList;
    }

}
