package sukriti.ngo.mis.ui.tickets.lambda.TicketActions;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface TicketActionsLambdaHandler {
    @LambdaFunction
    TicketActionsLambdaResult mis_ticket_actions(TicketActionsLambdaRequest request);
}
