package sukriti.ngo.mis.repository.data;

public class DailyTicketEventCount {
    public String date;
    public String event;
    public int eventCount;


    public DailyTicketEventCount(){

    }

    public DailyTicketEventCount(String date, String event, int eventCount){
        this.date = date;
        this.event = event;
        this.eventCount = eventCount;
    }
}
