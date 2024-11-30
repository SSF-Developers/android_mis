package sukriti.ngo.mis.ui.tickets.lambda.CreateTicket;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface CreateTicketLambdaHandler {
    @LambdaFunction
    CreateTicketLambdaResult mis_create_ticket(CreateTicketLambdaRequest request);
}
