package sukriti.ngo.mis.ui.dashboard.lambda.Dashboard_fetchData;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

import sukriti.ngo.mis.ui.tickets.lambda.CreateTicket.CreateTicketLambdaRequest;
import sukriti.ngo.mis.ui.tickets.lambda.CreateTicket.CreateTicketLambdaResult;

public interface DashBoardLambdaHandler {
    @LambdaFunction
    DashBoardLambdaResult mis_adminisatration_fetchDateWaiseUsageData(DashBoardLambdaRequest request);
}
