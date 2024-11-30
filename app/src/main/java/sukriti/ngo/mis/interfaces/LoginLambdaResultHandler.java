package sukriti.ngo.mis.interfaces;

import sukriti.ngo.mis.ui.login.data.LoginLambdaResult;

public interface LoginLambdaResultHandler {
    public void onSuccess(LoginLambdaResult result);
    public void onError(String ErrorMsg);
}
