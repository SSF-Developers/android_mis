package sukriti.ngo.mis.ui.tickets.lambda.TicketActions;

public class TicketActionsLambdaResult {

    public int result;
    public String message;


    public TicketActionsLambdaResult() {
    }

    public TicketActionsLambdaResult(int result, String message) {
        this.result = result;
        this.message = message;


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



}
