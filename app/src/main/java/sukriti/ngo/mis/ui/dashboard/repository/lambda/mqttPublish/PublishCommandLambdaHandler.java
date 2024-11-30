package sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface PublishCommandLambdaHandler {
    @LambdaFunction
    IotPublishLambdaResult mis_publish_command(IotPublishLambdaRequest request);
}
