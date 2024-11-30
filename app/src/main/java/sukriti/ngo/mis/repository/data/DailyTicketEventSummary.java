package sukriti.ngo.mis.repository.data;

public class DailyTicketEventSummary {
    public String date;
    public int raiseCount;
    public int assignCount;
    public int resolveCount;
    public int reOpenCount;
    public int closeCount;
    public int activeTicketCount;

    public DailyTicketEventSummary(){

    }

    public DailyTicketEventSummary(String date,int raiseCount,int assignCount, int resolveCount, int reOpenCount, int closeCount){
        this.date = date;
        this.raiseCount = raiseCount;
        this.assignCount = assignCount;
        this.resolveCount = resolveCount;
        this.reOpenCount = reOpenCount;
        this.closeCount = -100;
    }
}
