package sukriti.ngo.mis.ui.administration.lambda;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;


public interface DefineAccessLambdaHandler {
    @LambdaFunction
    DefineAccessLambdaResult mis_define_access(DefineAccessLambdaRequest request);

    @LambdaFunction
    DefineAccessLambdaResult mis_administration_defineAccess(DefineAccessLambdaRequest request);
}
