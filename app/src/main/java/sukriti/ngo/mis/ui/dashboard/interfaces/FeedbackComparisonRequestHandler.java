package sukriti.ngo.mis.ui.dashboard.interfaces;

import sukriti.ngo.mis.repository.data.FeedbackComparisonData;

public interface FeedbackComparisonRequestHandler {
    public void onSuccess(FeedbackComparisonData chartData);
    public void onError(String message);
}
