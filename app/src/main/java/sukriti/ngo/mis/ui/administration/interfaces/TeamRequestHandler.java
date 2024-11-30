package sukriti.ngo.mis.ui.administration.interfaces;

import java.util.List;

import sukriti.ngo.mis.dataModel.dynamo_db.Team;
import sukriti.ngo.mis.dataModel.dynamo_db.UserAccess;

public interface TeamRequestHandler {
    public void onSuccess(List<Team> itemList);
    public void onError(String message);
}
