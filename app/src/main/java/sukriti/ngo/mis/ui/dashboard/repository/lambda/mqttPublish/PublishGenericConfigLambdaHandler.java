package sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface PublishGenericConfigLambdaHandler {
    @LambdaFunction
    IotPublishLambdaResult mis_publish_config_generic(IotPublishLambdaRequest request);
}
