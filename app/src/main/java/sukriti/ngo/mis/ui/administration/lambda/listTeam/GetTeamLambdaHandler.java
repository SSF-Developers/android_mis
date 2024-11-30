package sukriti.ngo.mis.ui.administration.lambda.listTeam;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface GetTeamLambdaHandler {
    @LambdaFunction
    GetTeamLambdaResult mis_adminisatration_listTeam(GetTeamLambdaRequest request);
}
