package sukriti.ngo.mis.ui.administration.interfaces;

import com.amazonaws.services.cognitoidentityprovider.model.UserType;

public interface ClientSuperAdminRequestHandler {
    public void onSuccess(boolean hasClientSuperAdmin, UserType user);
    public void onError(String message);
}
