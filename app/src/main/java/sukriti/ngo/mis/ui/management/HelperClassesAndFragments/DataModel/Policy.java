package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel;


import java.util.List;

import sukriti.ngo.mis.ui.management.data.device.Application;

public class Policy {

    boolean isSelected = false;
    String name;
    String version;
    Boolean cameraDisabled = false;
    Boolean addUserDisabled = false;
    Boolean factoryResetDisabled = false;
    Boolean mountPhysicalMediaDisabled = false;
    Boolean modifyAccountsDisabled = false;
    Boolean safeBootDisabled = false;
    Boolean uninstallAppsDisabled = false;
    Boolean vpnConfigDisabled = false;
    Boolean networkResetDisabled = false;
    Boolean smsDisabled = false;
    Boolean removeUserDisabled = false;
    Boolean outgoingCallsDisabled = false;
    Boolean bluetoothConfigDisabled = false;
    Boolean kioskCustomLauncherEnabled = false;
    KioskCustomization kioskCustomization = new KioskCustomization();
    List<Application> applications;
    String kioskPackage = "";
    DeviceConnectivityManagement deviceConnectivityManagement;

    public Boolean getCameraDisabled() {
        return cameraDisabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setCameraDisabled(Boolean cameraDisabled) {
        this.cameraDisabled = cameraDisabled;
    }

    public Boolean getAddUserDisabled() {
        return addUserDisabled;
    }

    public void setAddUserDisabled(Boolean addUserDisabled) {
        this.addUserDisabled = addUserDisabled;
    }

    public Boolean getFactoryResetDisabled() {
        return factoryResetDisabled;
    }

    public void setFactoryResetDisabled(Boolean factoryResetDisabled) {
        this.factoryResetDisabled = factoryResetDisabled;
    }

    public Boolean getMountPhysicalMediaDisabled() {
        return mountPhysicalMediaDisabled;
    }

    public void setMountPhysicalMediaDisabled(Boolean mountPhysicalMediaDisabled) {
        this.mountPhysicalMediaDisabled = mountPhysicalMediaDisabled;
    }

    public Boolean getModifyAccountsDisabled() {
        return modifyAccountsDisabled;
    }

    public void setModifyAccountsDisabled(Boolean modifyAccountsDisabled) {
        this.modifyAccountsDisabled = modifyAccountsDisabled;
    }

    public Boolean getSafeBootDisabled() {
        return safeBootDisabled;
    }

    public void setSafeBootDisabled(Boolean safeBootDisabled) {
        this.safeBootDisabled = safeBootDisabled;
    }

    public Boolean getUninstallAppsDisabled() {
        return uninstallAppsDisabled;
    }

    public void setUninstallAppsDisabled(Boolean uninstallAppsDisabled) {
        this.uninstallAppsDisabled = uninstallAppsDisabled;
    }

    public Boolean getVpnConfigDisabled() {
        return vpnConfigDisabled;
    }

    public void setVpnConfigDisabled(Boolean vpnConfigDisabled) {
        this.vpnConfigDisabled = vpnConfigDisabled;
    }

    public Boolean getNetworkResetDisabled() {
        return networkResetDisabled;
    }

    public void setNetworkResetDisabled(Boolean networkResetDisabled) {
        this.networkResetDisabled = networkResetDisabled;
    }

    public Boolean getSmsDisabled() {
        return smsDisabled;
    }

    public void setSmsDisabled(Boolean smsDisabled) {
        this.smsDisabled = smsDisabled;
    }

    public Boolean getRemoveUserDisabled() {
        return removeUserDisabled;
    }

    public void setRemoveUserDisabled(Boolean removeUserDisabled) {
        this.removeUserDisabled = removeUserDisabled;
    }

    public Boolean getOutgoingCallsDisabled() {
        return outgoingCallsDisabled;
    }

    public void setOutgoingCallsDisabled(Boolean outgoingCallsDisabled) {
        this.outgoingCallsDisabled = outgoingCallsDisabled;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public void setApplications(List<Application> applications) {
        this.applications = applications;
    }

    public Boolean getBluetoothConfigDisabled() {
        return bluetoothConfigDisabled;
    }

    public void setBluetoothConfigDisabled(Boolean bluetoothConfigDisabled) {
        this.bluetoothConfigDisabled = bluetoothConfigDisabled;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Boolean getKioskCustomLauncherEnabled() {
        return kioskCustomLauncherEnabled;
    }

    public void setKioskCustomLauncherEnabled(Boolean kioskCustomLauncherEnabled) {
        this.kioskCustomLauncherEnabled = kioskCustomLauncherEnabled;
    }

    public KioskCustomization getKioskCustomization() {
        return kioskCustomization;
    }

    public void setKioskCustomization(KioskCustomization kioskCustomization) {
        this.kioskCustomization = kioskCustomization;
    }

    public String getKioskPackage() {
        return kioskPackage;
    }

    public void setKioskPackage(String kioskPackage) {
        this.kioskPackage = kioskPackage;
    }

    public DeviceConnectivityManagement getDeviceConnectivityManagement() {
        return deviceConnectivityManagement;
    }

    public void setDeviceConnectivityManagement(DeviceConnectivityManagement deviceConnectivityManagement) {
        this.deviceConnectivityManagement = deviceConnectivityManagement;
    }
}
