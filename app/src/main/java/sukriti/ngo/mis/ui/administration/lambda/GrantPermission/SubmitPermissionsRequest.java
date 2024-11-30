package sukriti.ngo.mis.ui.administration.lambda.GrantPermission;

public class SubmitPermissionsRequest {
    SubmitPermissionData payload;

    public SubmitPermissionsRequest(SubmitPermissionData data) {
        this.payload = data;
    }

    public SubmitPermissionData getData() {
        return payload;
    }

    public void setData(SubmitPermissionData data) {
        this.payload = data;
    }
}
