package sukriti.ngo.mis.repository.data;

import sukriti.ngo.mis.repository.entity.DailyUsage;

public class DailyUsageCount {

    public DailyUsageCount(){

    }

    public DailyUsageCount(String Date, int Count){
        this.date = Date;
        this.count = Count;
    }

    public int count;
    public String date;
}
