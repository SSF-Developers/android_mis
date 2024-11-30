package sukriti.ngo.mis.ui.dashboard.interfaces;

import sukriti.ngo.mis.repository.data.DailyWaterRecycledStats;
import sukriti.ngo.mis.repository.data.UsageComparisonStats;

public interface RecycledWaterRequestHandler {
    public void onSuccess(DailyWaterRecycledStats summary);
    public void onError(String message);
}
