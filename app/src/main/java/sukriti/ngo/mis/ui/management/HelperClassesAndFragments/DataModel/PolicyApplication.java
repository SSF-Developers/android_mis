package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel;

public class PolicyApplication {
    String packageName = "";
    String installType = "";
    String defaultPermissionPolicy = "";
    Boolean  lockTaskAllowed= false;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getInstallType() {
        return installType;
    }

    public void setInstallType(String installType) {
        this.installType = installType;
    }

    public String getDefaultPermissionPolicy() {
        return defaultPermissionPolicy;
    }

    public void setDefaultPermissionPolicy(String defaultPermissionPolicy) {
        this.defaultPermissionPolicy = defaultPermissionPolicy;
    }

    public Boolean getLockTaskAllowed() {
        return lockTaskAllowed;
    }

    public void setLockTaskAllowed(Boolean lockTaskAllowed) {
        this.lockTaskAllowed = lockTaskAllowed;
    }
}
