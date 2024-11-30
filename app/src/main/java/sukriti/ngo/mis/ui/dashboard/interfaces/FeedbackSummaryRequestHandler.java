package sukriti.ngo.mis.ui.dashboard.interfaces;

import sukriti.ngo.mis.repository.data.FeedbackStatsData;
import sukriti.ngo.mis.repository.data.FeedbackSummaryData;

public interface FeedbackSummaryRequestHandler {
    public void onSuccess(FeedbackSummaryData summary);
    public void onError(String message);
}
