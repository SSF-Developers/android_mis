package sukriti.ngo.mis.repository.data;

public class DailyAverageFeedback {
    public float average;
    public float total;
    public int userCount;
    public String date;

    public DailyAverageFeedback(){

    }

    public DailyAverageFeedback(String date, float total, float average,int userCount ){
        this.date = date;
        this.total = total;
        this.average = average;
        this.userCount = userCount;
    }

}
