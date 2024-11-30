package sukriti.ngo.mis.ui.administration.lambda.CreateUser;

public interface GetClientListResultHandler {
    void onSuccess(ClientListLambdaResult result);

    void onError(String errorMessage);
}
