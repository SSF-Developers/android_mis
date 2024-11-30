package sukriti.ngo.mis.interfaces;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

import sukriti.ngo.mis.ui.login.data.LoginLambdaRequest;
import sukriti.ngo.mis.ui.login.data.LoginLambdaResult;

public interface LoginLambdaHandler {
    @LambdaFunction
    LoginLambdaResult node_function_login(LoginLambdaRequest request);
}
