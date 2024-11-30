package sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish;

public class IotPublishLambdaRequest {
    String topic;
    String payload;
    String info;

    public IotPublishLambdaRequest(String topic, String payload,String info) {
        this.topic = topic;
        this.payload = payload;
        this.info = info;
    }

    public IotPublishLambdaRequest() {

    }

    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }

    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPayload() {
        return payload;
    }
    public void setPayload(String payload) {
        this.payload = payload;
    }
}
