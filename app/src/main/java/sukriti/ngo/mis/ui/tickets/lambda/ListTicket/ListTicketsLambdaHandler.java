package sukriti.ngo.mis.ui.tickets.lambda.ListTicket;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface ListTicketsLambdaHandler {
    @LambdaFunction
    ListTicketsLambdaResult mis_list_tickets_by_access(ListTicketsLambdaRequest request);
}
