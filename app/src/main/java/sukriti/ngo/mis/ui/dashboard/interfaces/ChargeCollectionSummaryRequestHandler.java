package sukriti.ngo.mis.ui.dashboard.interfaces;

import sukriti.ngo.mis.repository.data.ChargeCollectionStats;
import sukriti.ngo.mis.repository.data.UsageComparisonStats;

public interface ChargeCollectionSummaryRequestHandler {
    public void onSuccess(ChargeCollectionStats summary);
    public void onError(String message);
}
