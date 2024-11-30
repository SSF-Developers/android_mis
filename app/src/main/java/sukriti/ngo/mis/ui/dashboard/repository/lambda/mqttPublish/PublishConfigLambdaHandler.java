package sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface PublishConfigLambdaHandler {
    @LambdaFunction
    IotPublishLambdaResult mis_publish_config(IotPublishLambdaRequest request);
}
