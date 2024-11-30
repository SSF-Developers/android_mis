package sukriti.ngo.mis.repository.data;

public class GroupedFeedbackCount {

    public  int totalFeedback;
    public int userCount;
    public String date;
    public String type;

    public GroupedFeedbackCount(){

    }

    public GroupedFeedbackCount(String Date,String type, int totalFeedback, int userCount){
        this.date = Date;
        this.totalFeedback = totalFeedback;
        this.userCount = userCount;
        this.type = type;
    }

}
