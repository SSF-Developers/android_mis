package sukriti.ngo.mis.ui.administration.interfaces;

import java.util.List;

import sukriti.ngo.mis.ui.administration.data.MemberDetailsData;

public interface DetailedTeamRequestHandler {
    public void onSuccess(List<MemberDetailsData> itemList);
    public void onError(String message);
}
