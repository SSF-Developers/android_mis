package sukriti.ngo.mis.ui.administration.lambda.CreateUser;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface GetClientListLambdaHandler {

    @LambdaFunction
    ClientListLambdaResult mis_administration_listClients (ClientListLambdaRequest request);
}
