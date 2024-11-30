package sukriti.ngo.mis.utils;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import sukriti.ngo.mis.repository.data.DailyAverageFeedback;
import sukriti.ngo.mis.repository.data.DailyChargeCollection;
import sukriti.ngo.mis.repository.data.DailyFeedbackCount;
import sukriti.ngo.mis.repository.data.DailyTicketEventCount;
import sukriti.ngo.mis.repository.data.DailyTicketEventSummary;
import sukriti.ngo.mis.repository.data.DailyUsageCount;
import sukriti.ngo.mis.repository.data.FeedbackWiseUserCount;
import sukriti.ngo.mis.repository.entity.DailyUsage;

import static sukriti.ngo.mis.repository.utils.DateConverter.toDbDate;
import static sukriti.ngo.mis.repository.utils.DateConverter.toDbDateString;

public class ChartDataHelper {

    public static List<DailyUsageCount> getCompletedDailyUsageCount(Date fromDate, Date toDate,List<DailyUsageCount> data){
        List<DailyUsageCount> completedData = new ArrayList<>();
        HashMap<Date,DailyUsageCount> dataHash = toDailyUsageHashMap(data);

        fromDate = toDbDate(fromDate);
        long millisCounter = fromDate.getTime();
        Date dateCounter = fromDate;
        Calendar cal = Calendar.getInstance();

        while (millisCounter < toDate.getTime()){
            if(dataHash.containsKey(dateCounter))
                completedData.add(dataHash.get(dateCounter));
            else
                completedData.add(new DailyUsageCount(toDbDateString(dateCounter),0));

            //Increment
            cal = Calendar.getInstance();
            cal.setTime(dateCounter);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            millisCounter = cal.getTimeInMillis();
            dateCounter = new Date(millisCounter);
            dateCounter = toDbDate(dateCounter);
        }
        return completedData;
    }

    private static HashMap<Date,DailyUsageCount> toDailyUsageHashMap(List<DailyUsageCount> data){
        HashMap<Date,DailyUsageCount> map = new HashMap<>();
        for(DailyUsageCount item : data){
            map.put(toDbDate(item.date),item);
        }
        return map;
    }

    public static List<DailyUsageCount> getDailyUsageTimeline(Date fromDate, Date toDate,List<DailyUsageCount> data){
        List<DailyUsageCount> completedData = getCompletedDailyUsageCount(fromDate,toDate,data);
        List<DailyUsageCount> timeLineData = new ArrayList<>();
        DailyUsageCount timeLineEntry;
        int cumulativeCount = 0;
        for(DailyUsageCount item:completedData){
            cumulativeCount += item.count;
            timeLineEntry = new DailyUsageCount(item.date,cumulativeCount);
            timeLineData.add(timeLineEntry);
        }
        return timeLineData;
    }

    public static List<DailyFeedbackCount> getCompletedDailyUserCountForFeedback(Date fromDate, Date toDate,List<DailyFeedbackCount> data, int listFeedback){
        List<DailyFeedbackCount> completedData = new ArrayList<>();
        HashMap<Date,DailyFeedbackCount> dataHash = toDailyFeedbackHashMap(data);

        Log.i("_userCountForFeedback","in-data "+data.size());

        fromDate = toDbDate(fromDate);
        long millisCounter = fromDate.getTime();
        Date dateCounter = fromDate;
        Calendar cal = Calendar.getInstance();

        while (millisCounter < toDate.getTime()){
            if(dataHash.containsKey(dateCounter))
                completedData.add(dataHash.get(dateCounter));
            else
                completedData.add(new DailyFeedbackCount(toDbDateString(dateCounter),listFeedback,0));

            //Increment
            cal = Calendar.getInstance();
            cal.setTime(dateCounter);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            millisCounter = cal.getTimeInMillis();
            dateCounter = new Date(millisCounter);
            dateCounter = toDbDate(dateCounter);
        }

        Log.i("_userCountForFeedback","out-data "+completedData.size());
        return completedData;
    }

    public static List<DailyTicketEventSummary> getDailyTicketEventSummary(
            List<DailyTicketEventCount> raised,
            List<DailyTicketEventCount> assigned,
            List<DailyTicketEventCount> resolved,
            List<DailyTicketEventCount> reopened,
            List<DailyTicketEventCount> closed){


        List<DailyTicketEventSummary> completedData = new ArrayList<>();
        DailyTicketEventSummary item;
        for(int i=0; i<raised.size(); i++){
            item = new DailyTicketEventSummary(raised.get(i).date,
                    raised.get(i).eventCount,
                    assigned.get(i).eventCount,
                    resolved.get(i).eventCount,
                    reopened.get(i).eventCount,
                    closed.get(i).eventCount);
            completedData.add(item);
        }

        int previousActiveCount = 0;
        for(int i=0; i<completedData.size(); i++){
            item = completedData.get(i);
            if(i==0)
                item.activeTicketCount = item.raiseCount - item.resolveCount + item.reOpenCount;
            else {
                previousActiveCount = completedData.get(i-1).activeTicketCount;
                item.activeTicketCount = previousActiveCount + item.raiseCount - item.resolveCount + item.reOpenCount;
            }

            Log.i("_dailyEventSummary",item.date+" "+new Gson().toJson(item));
        }

        return completedData;
    }



    private static HashMap<Date,DailyFeedbackCount> toDailyFeedbackHashMap(List<DailyFeedbackCount> data){
        HashMap<Date,DailyFeedbackCount> map = new HashMap<>();
        for(DailyFeedbackCount item : data){
            map.put(toDbDate(item.date),item);
        }
        return map;
    }

   public static List<FeedbackWiseUserCount> getCompletedFeedbackWiseUserCount(List<FeedbackWiseUserCount> data){
       List<FeedbackWiseUserCount> completedData = new ArrayList<>();
       HashMap<Integer,FeedbackWiseUserCount> map = new HashMap<>();

       for(FeedbackWiseUserCount item : data)
            map.put(item.feedback,item);


       for(int feedback=1;feedback<=5;feedback++){
           if(map.containsKey(feedback)){
               completedData.add(map.get(feedback));
           }else {
               completedData.add(new FeedbackWiseUserCount(feedback,0));
           }
       }

       return completedData;
   }

    public static List<DailyAverageFeedback> getCompletedDailyAverageFeedback(Date fromDate, Date toDate, List<DailyAverageFeedback> data){
        List<DailyAverageFeedback> completedData = new ArrayList<>();
        HashMap<Date,DailyAverageFeedback> dataHash = toDailyAverageFeedbackHashMap(data);

        Log.i("_completedDailyAverage","in-data "+data.size());

        fromDate = toDbDate(fromDate);
        long millisCounter = fromDate.getTime();
        Date dateCounter = fromDate;
        Calendar cal = Calendar.getInstance();

        while (millisCounter < toDate.getTime()){
            if(dataHash.containsKey(dateCounter))
                completedData.add(dataHash.get(dateCounter));
            else
                completedData.add(new DailyAverageFeedback(toDbDateString(dateCounter),0,0,0));

            //Increment
            cal = Calendar.getInstance();
            cal.setTime(dateCounter);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            millisCounter = cal.getTimeInMillis();
            dateCounter = new Date(millisCounter);
            dateCounter = toDbDate(dateCounter);
        }

        int cumulativeUserCount = 0;
        int cumulativeFeedback = 0;
        int average;
        DailyAverageFeedback timeLineEntry;
        List<DailyAverageFeedback> timeLineData = new ArrayList<>();
        for(DailyAverageFeedback item:completedData){

            cumulativeUserCount += item.userCount;
            cumulativeFeedback += item.total;


            if(cumulativeUserCount != 0)
                average = cumulativeFeedback/cumulativeUserCount;
            else
                average = 0;

            timeLineEntry = new DailyAverageFeedback(item.date,cumulativeFeedback,average,cumulativeUserCount);
            timeLineData.add(timeLineEntry);

            Log.i("_completedDailyAverage","out-data-timeLineData "+timeLineEntry.date +": "+timeLineEntry.average);
        }

        Log.i("_completedDailyAverage","out-data-timeLineData "+timeLineData.size());


        return timeLineData;
    }

    private static HashMap<Date,DailyAverageFeedback> toDailyAverageFeedbackHashMap(List<DailyAverageFeedback> data){
        HashMap<Date,DailyAverageFeedback> map = new HashMap<>();
        for(DailyAverageFeedback item : data){
            map.put(toDbDate(item.date),item);
        }
        return map;
    }

    public static Long toLongTimestamp(String timestamp) {
        try{
            return Long.valueOf(timestamp);
        }catch (NumberFormatException e)
        {
            return 0L;
        }
    }

    public static List<DailyChargeCollection> getCompletedDailyChargeCollection(Date fromDate, Date toDate, List<DailyChargeCollection> data){
        List<DailyChargeCollection> completedData = new ArrayList<>();
        HashMap<Date,DailyChargeCollection> dataHash = toDailyDailyChargeCollectionHashMap(data);

        Log.i("_userCountForFeedback","in-data "+data.size());

        fromDate = toDbDate(fromDate);
        long millisCounter = fromDate.getTime();
        Date dateCounter = fromDate;
        Calendar cal = Calendar.getInstance();

        while (millisCounter < toDate.getTime()){
            if(dataHash.containsKey(dateCounter))
                completedData.add(dataHash.get(dateCounter));
            else
                completedData.add(new DailyChargeCollection(toDbDateString(dateCounter),0f));

            //Increment
            cal = Calendar.getInstance();
            cal.setTime(dateCounter);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            millisCounter = cal.getTimeInMillis();
            dateCounter = new Date(millisCounter);
            dateCounter = toDbDate(dateCounter);
        }

        Log.i("_userCountForFeedback","out-data "+completedData.size());
        return completedData;
    }

    public static List<DailyChargeCollection> getDailyChargeCollectionTimeline(Date fromDate, Date toDate,List<DailyChargeCollection> data){
        List<DailyChargeCollection> completedData = getCompletedDailyChargeCollection(fromDate,toDate,data);
        List<DailyChargeCollection> timeLineData = new ArrayList<>();
        DailyChargeCollection timeLineEntry;

        float cumulativeAmount = 0;
        for(DailyChargeCollection item:completedData){
            cumulativeAmount += item.amount;
            timeLineEntry = new DailyChargeCollection(item.date,cumulativeAmount);
            timeLineData.add(timeLineEntry);
        }
        return timeLineData;
    }

    private static HashMap<Date,DailyChargeCollection> toDailyDailyChargeCollectionHashMap(List<DailyChargeCollection> data){
        HashMap<Date,DailyChargeCollection> map = new HashMap<>();
        for(DailyChargeCollection item : data){
            map.put(toDbDate(item.date),item);
        }
        return map;
    }

    public static List<DailyTicketEventCount> getCompletedDailyDailyTicketEventCount(
            Date fromDate, Date toDate, List<DailyTicketEventCount> data, String event){
        List<DailyTicketEventCount> completedData = new ArrayList<>();
        HashMap<Date,DailyTicketEventCount> dataHash = toDailyTicketEventCountHashMap(data,event);

        Log.e("_completedDailyTicket",event);
        Log.i("_completedDailyTicket","in-data "+data.size());

        fromDate = toDbDate(fromDate);
        long millisCounter = fromDate.getTime();
        Date dateCounter = fromDate;
        Calendar cal = Calendar.getInstance();

        while (millisCounter < toDate.getTime()){
            if(dataHash.containsKey(dateCounter))
                completedData.add(dataHash.get(dateCounter));
            else
                completedData.add(new DailyTicketEventCount(toDbDateString(dateCounter),event,0));

            //Increment
            cal = Calendar.getInstance();
            cal.setTime(dateCounter);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            millisCounter = cal.getTimeInMillis();
            dateCounter = new Date(millisCounter);
            dateCounter = toDbDate(dateCounter);
        }

        for(DailyTicketEventCount item : completedData){
            Log.i("_completedDailyTicket",item.date +": "+item.eventCount);
        }
        Log.i("_completedDailyTicket","out-data "+completedData.size());
        return completedData;
    }

    private static HashMap<Date,DailyTicketEventCount> toDailyTicketEventCountHashMap(List<DailyTicketEventCount> data, String event){
        HashMap<Date,DailyTicketEventCount> map = new HashMap<>();
        for(DailyTicketEventCount item : data){
            item.event = event;
            map.put(toDbDate(item.date),item);
        }
        return map;
    }

}
