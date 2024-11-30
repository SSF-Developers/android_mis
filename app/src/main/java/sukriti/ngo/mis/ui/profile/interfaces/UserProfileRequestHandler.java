package sukriti.ngo.mis.ui.profile.interfaces;

import sukriti.ngo.mis.ui.administration.data.MemberDetailsData;

public interface UserProfileRequestHandler {
    void onSuccess(MemberDetailsData userDetails);
    void onError(String error);
}
