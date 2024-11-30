package sukriti.ngo.mis.ui.dashboard.interfaces;

import java.util.List;

import sukriti.ngo.mis.repository.entity.QuickAccess;

public interface CheckQuickAccessRequestHandler {
    public void onResult(boolean hasQuickAccess);
}
