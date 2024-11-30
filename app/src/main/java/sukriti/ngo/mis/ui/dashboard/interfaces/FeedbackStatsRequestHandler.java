package sukriti.ngo.mis.ui.dashboard.interfaces;

import sukriti.ngo.mis.repository.data.FeedbackStatsData;

public interface FeedbackStatsRequestHandler {
    public void onSuccess(FeedbackStatsData summary);
    public void onError(String message);
}
