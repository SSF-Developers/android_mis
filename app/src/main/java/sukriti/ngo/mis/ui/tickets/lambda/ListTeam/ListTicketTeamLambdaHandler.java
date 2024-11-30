package sukriti.ngo.mis.ui.tickets.lambda.ListTeam;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface ListTicketTeamLambdaHandler {
    @LambdaFunction
    ListTicketTeamLambdaResult mis_list_ticket_team(ListTicketTeamLambdaRequest request);
}
