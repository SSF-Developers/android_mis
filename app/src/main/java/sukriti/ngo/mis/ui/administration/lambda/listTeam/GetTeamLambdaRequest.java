package sukriti.ngo.mis.ui.administration.lambda.listTeam;

public class GetTeamLambdaRequest {
    String userName;

    public GetTeamLambdaRequest(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
