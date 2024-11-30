package sukriti.ngo.mis.ui.administration.lambda.DeleteUser;

import sukriti.ngo.mis.ui.tickets.data.TicketDetailsData;

public class DeleteUserLambdaRequest {
    String userName;

    public DeleteUserLambdaRequest(String userName) {
        this.userName = userName;
    }

    public DeleteUserLambdaRequest() {

    }

    public String getUserName() {return userName;}

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
