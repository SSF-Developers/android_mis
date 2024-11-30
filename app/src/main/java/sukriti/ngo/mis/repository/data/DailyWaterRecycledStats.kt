package sukriti.ngo.mis.repository.data

import java.util.*

data class DailyWaterRecycledStats(
    var hasBwtData: Boolean, var fromDate: Date, var toDate:Date,var data:List<DailyWaterRecycled>,
    var durationLabel:String){

    fun getTotal(stats:DailyWaterRecycledStats): Int{
        var total = 0f;
        for(item in  stats.data){
            total += item.quantity
        }
        return Math.round(total);
    }
}
