package sukriti.ngo.mis.repository.data;

public class DailyChargeCollection {
    public float amount;
    public String date;

    public DailyChargeCollection(){

    }

    public DailyChargeCollection(String Date, float Amount){
        this.date = Date;
        this.amount = Amount;
    }


}
