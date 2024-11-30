package sukriti.ngo.mis.repository.data;

public class DailyFeedbackCount {
    public  int totalFeedback;
    public int userCount;
    public String date;

    public DailyFeedbackCount(){

    }

    public DailyFeedbackCount(String Date, int totalFeedback, int userCount){
        this.date = Date;
        this.totalFeedback = totalFeedback;
        this.userCount = userCount;
    }
}
