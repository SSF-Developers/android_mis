package sukriti.ngo.mis.ui.management.data.device;

import java.util.List;

import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.RegisterCabinPayloadValue;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.RegisterComplexPayloadValue;

public class Device {
   public boolean selectedToDelete = false;
   public AndroidManagementData android_data;

   public boolean CREATED_STATE = false;
   public boolean DEVICE_POLICY_STATE = false;
   public boolean DEVICE_PROV_GET_INFO_PUBLISH = false;
   public boolean DEVICE_PROV_COMPLETED_INFO_RESP_INIT = false;
   public boolean DEVICE_APPLICATION_STATE = false;
   public boolean PROVISIONING_THING_CREATED_STATE = false;
   public boolean CERT_ATTACH_STATE = false;
   public boolean DEVICE_PROV_GET_INFO_RESP_INIT = false;
   public boolean QR_CREATED_STATE = false;
   public String cabin_name = "";
   public String serial_number = "";
   public RegisterComplexPayloadValue complex_details = null;
   public RegisterCabinPayloadValue cabin_details = null;
   public QrDetails qr_details = null;
   public ApplicationDetails application_details = null;
   public PolicyDetails policy_details = null;

   public boolean isKioskEnabled = false;

   public List<Application> applications;
   public KioskCustomization kioskCustomization;
   public boolean kioskCustomLauncherEnabled;
}
