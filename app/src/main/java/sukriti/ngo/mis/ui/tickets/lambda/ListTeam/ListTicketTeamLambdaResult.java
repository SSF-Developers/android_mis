package sukriti.ngo.mis.ui.tickets.lambda.ListTeam;

public class ListTicketTeamLambdaResult {

    public int result;
    public String message;
    public String ticketTeamList;


    public ListTicketTeamLambdaResult() {
    }

    public ListTicketTeamLambdaResult(int result, String message, String ticketTeamList) {
        this.result = result;
        this.message = message;
        this.ticketTeamList = ticketTeamList;


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

    public String getTicketTeamList() {
        return ticketTeamList;
    }

    public void setTicketTeamList(String ticketTeamList) {
        this.ticketTeamList = ticketTeamList;
    }
}
