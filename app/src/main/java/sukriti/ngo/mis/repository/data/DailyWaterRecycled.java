package sukriti.ngo.mis.repository.data;

public class DailyWaterRecycled {
    public float quantity;
    public String date;

    public DailyWaterRecycled(){

    }

    public DailyWaterRecycled(String Date, float quantity){
        this.date = Date;
        this.quantity = quantity;
    }


}
