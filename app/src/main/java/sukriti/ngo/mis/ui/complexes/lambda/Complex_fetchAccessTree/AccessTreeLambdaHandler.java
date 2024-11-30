package sukriti.ngo.mis.ui.complexes.lambda.Complex_fetchAccessTree;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface AccessTreeLambdaHandler {
    @LambdaFunction
    AccessTreeLambdaResult mis_adminisatration_getCompletedAccessTree(AccessTreeLambdaRequest request);
}
