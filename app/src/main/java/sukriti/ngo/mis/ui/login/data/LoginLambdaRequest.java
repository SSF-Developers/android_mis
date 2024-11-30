package sukriti.ngo.mis.ui.login.data;

public class LoginLambdaRequest {
    String identityId;
    String userPolicy;

    public LoginLambdaRequest(String identityId,String userPolicy) {
        this.identityId = identityId;
        this.userPolicy = userPolicy;
    }

    public LoginLambdaRequest() {
    }

    public String getIdentityId() {
        return identityId;
    }
    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getUserPolicy() {
        return userPolicy;
    }
    public void setUserPolicy(String userPolicy) {
        this.userPolicy = userPolicy;
    }
}
