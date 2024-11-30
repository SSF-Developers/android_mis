package sukriti.ngo.mis.ui.administration.interfaces;

import sukriti.ngo.mis.dataModel.dynamo_db.UserAccess;

public interface UserAccessTreeRequestHandler {
    public void onSuccess( UserAccess userAccess );
    public void onError(String message);
}
