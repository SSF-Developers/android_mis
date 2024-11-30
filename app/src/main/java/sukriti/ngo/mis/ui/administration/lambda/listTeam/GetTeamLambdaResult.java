package sukriti.ngo.mis.ui.administration.lambda.listTeam;

import java.util.ArrayList;

import sukriti.ngo.mis.ui.administration.data.TeamDetail;

public class GetTeamLambdaResult {
    public int status;
    public ArrayList<TeamDetail> teamDetails;

    public GetTeamLambdaResult(int status, ArrayList<TeamDetail> teamDetails) {
        this.status = status;
        this.teamDetails = teamDetails;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<TeamDetail> getTeamDetails() {
        return teamDetails;
    }

    public void setTeamDetails(ArrayList<TeamDetail> teamDetails) {
        this.teamDetails = teamDetails;
    }
}
