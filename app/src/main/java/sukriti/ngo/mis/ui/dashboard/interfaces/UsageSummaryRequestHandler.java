package sukriti.ngo.mis.ui.dashboard.interfaces;

import sukriti.ngo.mis.repository.data.UsageSummaryStats;

public interface UsageSummaryRequestHandler {
    public void onSuccess(UsageSummaryStats summary);
    public void onError(String message);
}
