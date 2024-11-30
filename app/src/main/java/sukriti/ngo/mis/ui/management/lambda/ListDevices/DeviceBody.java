package sukriti.ngo.mis.ui.management.lambda.ListDevices;

import java.util.List;

import sukriti.ngo.mis.ui.management.data.device.Device;

public class DeviceBody {

    List<Device> devices;

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
