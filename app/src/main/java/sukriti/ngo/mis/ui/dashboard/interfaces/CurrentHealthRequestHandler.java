package sukriti.ngo.mis.ui.dashboard.interfaces;

import java.util.List;

import sukriti.ngo.mis.repository.data.UsageSummaryStats;
import sukriti.ngo.mis.repository.entity.Health;

public interface CurrentHealthRequestHandler {
    public void onSuccess(List<Health> data);
    public void onError(String message);
}
