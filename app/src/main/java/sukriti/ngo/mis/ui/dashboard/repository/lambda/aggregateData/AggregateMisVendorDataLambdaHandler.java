package sukriti.ngo.mis.ui.dashboard.repository.lambda.aggregateData;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface AggregateMisVendorDataLambdaHandler {
    @LambdaFunction
    CabinDataLambdaResult aggregate_mis_vendor_data(CabinDataLambdaRequest request);
}
