package sukriti.ngo.mis.communication;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus;

public class AWSCommunicationClientStatus {
    public boolean isInitialized;
    public boolean hasStableConnection;
    public AWSIotMqttClientStatus lastConnectStatus;
    public int lifeTimeSpan,statusTimeSpan;
}
