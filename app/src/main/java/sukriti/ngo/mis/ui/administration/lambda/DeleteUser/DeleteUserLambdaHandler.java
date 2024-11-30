package sukriti.ngo.mis.ui.administration.lambda.DeleteUser;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface DeleteUserLambdaHandler {
    @LambdaFunction
    DeleteUserLambdaResult mis_delete_user(DeleteUserLambdaRequest request);
}
