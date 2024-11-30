package sukriti.ngo.mis.ui.dashboard.interfaces;

import sukriti.ngo.mis.repository.data.ChargeCollectionStats;
import sukriti.ngo.mis.repository.data.DailyChargeCollectionData;

public interface ChargeCollectionComparisonRequestHandler {
    public void onSuccess(DailyChargeCollectionData summary);
    public void onError(String message);
}
