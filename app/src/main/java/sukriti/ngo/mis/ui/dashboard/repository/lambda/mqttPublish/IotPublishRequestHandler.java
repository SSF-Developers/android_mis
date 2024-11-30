package sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish;

public interface IotPublishRequestHandler {
    public void onSuccess(IotPublishLambdaResult result);
    public void onError(String ErrorMsg);
}
