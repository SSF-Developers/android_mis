package sukriti.ngo.mis.repository.utils;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateConverter {

    private static final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String getTimeStamp() {
        Long timeStamp = Calendar.getInstance().getTimeInMillis();
        Log.i("_timeStamp","curr: "+timeStamp);
        Date date = new Date(timeStamp);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        return sdf.format(date);
    }

    public static Long toDbTimestamp(String timestamp) {
        try{
            return Long.valueOf(timestamp);
        }catch (NumberFormatException e){
            Log.i("cabinDetails", "getElapsedTimeLabel exception : "+e.getMessage());
            return Long.valueOf("0");
        }
    }

    public static String getElapsedTimeLabel(String timeStamp){
        Log.i("cabinDetails", "getElapsedTimeLabel: "+toDbTimestamp(timeStamp));
        Date fromDate = new Date(toDbTimestamp(timeStamp));
        String niceDateStr = DateUtils.getRelativeTimeSpanString(fromDate.getTime() , Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
        return niceDateStr;
    }

    public static String toDateString(Long timeStamp) {
        Date date = new Date(timeStamp);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy ");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        return sdf.format(date);
    }


    public static String toDateString_FromAwsDateFormat(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
        try {
            Date date = formatter.parse(dateStr);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy ");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
            return sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "-";
        }
    }

    public static String toDbDateString(Date date) {
        return dbDateFormat.format(date);
    }

    public static Date toDbDate(String dateStr) {
        try {
            Log.i("print", "try toDbDate: "+dateStr);
            Date date = dbDateFormat.parse(dateStr);
            Log.i("print", "toDbDate: "+date);
            return date;
        } catch (ParseException e) {
            Log.i("print", "toDbDate: ParseException  :  "+e.getMessage());
            e.printStackTrace();
        }
        return new Date(0);
    }

    public static  Date lambdaDate (String dateStr){
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date ;
        try {
            date = originalFormat.parse(dateStr);
            return  date;

        } catch (ParseException ex) {
            // Handle Exception.
            return new Date(0);
        }
    }


    public static Date toDbDate(Date inDate) {
        Date date = new Date(inDate.getTime());
        String dateStr = dbDateFormat.format(date);
        try {
            return dbDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date(0);
    }

    //Correction for faulty Dynamo DB timestamp
    public static String toDbDate(Long timeStamp) {
//        Calendar today = Calendar.getInstance();
//        Calendar _8Month = Calendar.getInstance();
//        _8Month.add(Calendar.MONTH, -8);
//        long diff = today.getTimeInMillis() - _8Month.getTimeInMillis();
//        timeStamp = timeStamp + diff;

        Date date = new Date(timeStamp);
        String dateStr = dbDateFormat.format(date);
        return dateStr;
    }

    //Correction for faulty Dynamo DB timestamp
    public static String toDbTime(Long timeStamp) {
//        Calendar today = Calendar.getInstance();
//        Calendar _8Month = Calendar.getInstance();
//        _8Month.add(Calendar.MONTH, -8);
//        long diff = today.getTimeInMillis() - _8Month.getTimeInMillis();
//        timeStamp = timeStamp + diff;

        Date date = new Date(timeStamp);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        return sdf.format(date);
    }

}
