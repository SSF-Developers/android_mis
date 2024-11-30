package sukriti.ngo.mis.ui.management.lambda.ListEnterprise;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface ListEnterpriseLambdaHandler {
    @LambdaFunction
    ListEnterpriseResponse List_Enterprises_Android_Management(ListEnterpriseLambdaRequest request);
}
