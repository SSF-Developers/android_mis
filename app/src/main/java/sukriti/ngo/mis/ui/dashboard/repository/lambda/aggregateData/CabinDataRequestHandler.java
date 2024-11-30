package sukriti.ngo.mis.ui.dashboard.repository.lambda.aggregateData;

public interface CabinDataRequestHandler {
    public void onSuccess(CabinDataLambdaResult result);
    public void onError(String ErrorMsg);
}
