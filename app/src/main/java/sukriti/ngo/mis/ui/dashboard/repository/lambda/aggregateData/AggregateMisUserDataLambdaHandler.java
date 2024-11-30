package sukriti.ngo.mis.ui.dashboard.repository.lambda.aggregateData;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface AggregateMisUserDataLambdaHandler {
    @LambdaFunction
    CabinDataLambdaResult aggregate_mis_user_data(CabinDataLambdaRequest request);
}
