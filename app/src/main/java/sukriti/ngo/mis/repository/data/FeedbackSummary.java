package sukriti.ngo.mis.repository.data;

public class FeedbackSummary {
    public float averageFeedback;
    public  int totalFeedback;
    public int userCount;

    public FeedbackSummary(float averageFeedback, int totalFeedback, int userCount) {
        this.averageFeedback = averageFeedback;
        this.totalFeedback = totalFeedback;
        this.userCount = userCount;
    }

    public FeedbackSummary(){

    }

}
