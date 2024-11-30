package sukriti.ngo.mis.ui.management.data.device;

import java.util.ArrayList;
import java.util.List;

public class AndroidManagementData {
    public String name = "";
    public String managementMode = "";
    public String state = "";
    public String enrollmentTime = "";
    public String lastPolicySyncTime = "";
    public String enrollmentTokenData = "";
    public SoftwareInfo softwareInfo = new SoftwareInfo();
    public HardwareInfo hardwareInfo = new HardwareInfo();
    public String policyName = "";
    public MemoryInfo memoryInfo = new MemoryInfo();
    public String userName = "";
    public String enrollmentTokenName = "";
    public List<String> previousDeviceNames = new ArrayList<>();
    public String ownership = "";

    public String appliedState = "";
    public boolean policyCompliant = false;
    public boolean lastStatusReportTime = false;
    public int appliedPolicyVersion = 0;
    public int apiLevel = 0;
    public String appliedPolicyName = "";

}
