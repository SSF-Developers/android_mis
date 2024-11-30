package sukriti.ngo.mis.ui.administration.lambda.CreateUser;

public interface GetClientListRequestHandler {

    void onSuccess(ClientListLambdaResult result);

    void onError(String errorMessage);
}
