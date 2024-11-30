package sukriti.ngo.mis.ui.tickets.lambda.ListProgress;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface ListTicketProgressLambdaHandler {
    @LambdaFunction
    ListTicketProgressLambdaResult mis_list_ticket_progress(ListTicketProgressLambdaRequest request);
}
