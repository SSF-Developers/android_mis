package sukriti.ngo.mis.ui.administration.lambda;

import sukriti.ngo.mis.ui.login.data.LoginLambdaResult;

public interface DefineAccessRequestHandler {
    public void onSuccess(DefineAccessLambdaResult result);
    public void onError(String ErrorMsg);
}
