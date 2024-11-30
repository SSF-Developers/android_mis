package sukriti.ngo.mis.ui.administration.interfaces;

import java.util.List;

import sukriti.ngo.mis.dataModel.CognitoUser;
import sukriti.ngo.mis.dataModel.dynamo_db.Team;

public interface CognitoUserDetailsRequestHandler {
    public void onSuccess(CognitoUser user);
    public void onError(String message);
}
