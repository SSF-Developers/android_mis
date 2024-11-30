package sukriti.ngo.mis.ui.dashboard.interfaces;

import java.util.List;

import sukriti.ngo.mis.repository.entity.Health;
import sukriti.ngo.mis.repository.entity.QuickAccess;

public interface QuickAccessRequestHandler {
    public void onSuccess(List<QuickAccess> data);
    public void onError(String message);
}
