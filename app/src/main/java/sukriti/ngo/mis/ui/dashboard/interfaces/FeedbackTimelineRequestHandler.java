package sukriti.ngo.mis.ui.dashboard.interfaces;

import sukriti.ngo.mis.repository.data.FeedbackComparisonData;
import sukriti.ngo.mis.repository.data.FeedbackTimelineData;

public interface FeedbackTimelineRequestHandler {
    public void onSuccess(FeedbackTimelineData chartData);
    public void onError(String message);
}
