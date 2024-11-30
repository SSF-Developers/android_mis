package sukriti.ngo.mis.ui.management.lambda.ListDevices;

import java.util.List;

import sukriti.ngo.mis.ui.management.data.device.Device;

public class ListDeviceLambdaResponse {
    int statusCode;
//    DeviceBody body;

    List<Device> body;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<Device> getBody() {
        return body;
    }

    public void setBody(List<Device> body) {
        this.body = body;
    }
}
