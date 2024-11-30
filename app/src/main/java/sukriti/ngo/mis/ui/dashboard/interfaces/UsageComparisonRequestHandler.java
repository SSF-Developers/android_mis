package sukriti.ngo.mis.ui.dashboard.interfaces;

import sukriti.ngo.mis.repository.data.UsageComparisonStats;

public interface UsageComparisonRequestHandler {
    public void onSuccess(UsageComparisonStats summary);
    public void onError(String message);
}
