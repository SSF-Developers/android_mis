package sukriti.ngo.mis.repository.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sukriti.ngo.mis.repository.data.ComplexHealthStats;
import sukriti.ngo.mis.repository.entity.BwtHealth;
import sukriti.ngo.mis.repository.entity.DailyFeedback;
import sukriti.ngo.mis.repository.entity.DailyUsage;
import sukriti.ngo.mis.repository.entity.DailyUsageCharge;
import sukriti.ngo.mis.repository.entity.Health;
import sukriti.ngo.mis.repository.entity.UsageProfile;

import static sukriti.ngo.mis.repository.utils.DateConverter.toDateString;
import static sukriti.ngo.mis.repository.utils.DateConverter.toDbDate;
import static sukriti.ngo.mis.repository.utils.DateConverter.toDbTime;
import static sukriti.ngo.mis.repository.utils.DateConverter.toDbTimestamp;
import static sukriti.ngo.mis.utils.Utilities.convertToDbAmountCollected;
import static sukriti.ngo.mis.utils.Utilities.convertToDbCabinType;
import static sukriti.ngo.mis.utils.Utilities.convertToDbDuration;
import static sukriti.ngo.mis.utils.Utilities.convertToDbFeedback;

public class HLWHelper {

    public static List<Health> getList(HashMap<String, Health> data) {
        List<Health> list = new ArrayList<>();
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            list.add(((Health) pair.getValue()));
            it.remove(); // avoids a ConcurrentModificationException
        }

        return list;
    }

    public static List<ComplexHealthStats> getComplexHealthStatsList(HashMap<String, ComplexHealthStats> data) {
        List<ComplexHealthStats> list = new ArrayList<>();
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            list.add(((ComplexHealthStats) pair.getValue()));
            it.remove(); // avoids a ConcurrentModificationException
        }

        return list;
    }

    public static boolean isFaultyDevice(Health item) {
        final String STATUS_OK = "3";
        //return true;
        String date = toDateString(toDbTimestamp(item.getSERVER_TIMESTAMP()));
        Log.i("_isFaultyDevice", item.getCOMPLEX() + ", " + item.getSHORT_THING_NAME() + ": " + date + ": " +
                "dryer:" + item.getAirDryerHealth() + ", " +
                "choke: " + item.getChokeHealth() +", " +
                "fan:" + item.getFanHealth() + ", " +
                "floor: " + item.getFloorCleanHealth() +", " +
                "flush:" + item.getFlushHealth() + ", " +
                "lock:"+item.getLockHealth()+", " +
                "ods: "+item.getOdsHealth() +", " +
                "tap: "+item.getTapHealth() + ", "+
                "light: "+item.getLightHealth()
        );


        if (isFaultyStatus(item.getAirDryerHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getChokeHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getFanHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getFloorCleanHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getFlushHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getLockHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getOdsHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getTapHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getLightHealth())) {
            return true;
        }

        return false;
    }

    public static boolean isFaultyDevice(BwtHealth item) {
        //return true;

        String date = toDateString(toDbTimestamp(item.getSERVER_TIMESTAMP()));
        Log.i("_isFaultyDevice", item.getCOMPLEX() + ", " + item.getSHORT_THING_NAME() + ": " + date + ": " +
                "Filter:" + item.getFilterHealth() + ", " +
                "AlpValue: " + item.getAlpValueHealth() +", " +
                "Blower:" + item.getBlowerHealth() + ", " +
                "FailSafe: " + item.getFailSafeHealth() +", " +
                "Mp1:" + item.getMp1ValueHealth() + ", " +
                "Mp2:"+item.getMp2ValueHealth()+", " +
                "Mp3: "+item.getMp3ValueHealth() +", " +
                "Mp4: "+item.getMp4ValueHealth() + ", "+
                "Ozonator: "+item.getOzonatorHealth()+ ", "+
                "PrimingValue: "+item.getPrimingValueHealth()+ ", "+
                "Pump: "+item.getPumpHealth()
        );


        if (isFaultyStatus(item.getFilterHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getAlpValueHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getBlowerHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getFailSafeHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getMp1ValueHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getMp2ValueHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getMp3ValueHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getMp4ValueHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getOzonatorHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getPrimingValueHealth())) {
            return true;
        }
        if (isFaultyStatus(item.getPumpHealth())) {
            return true;
        }

        return false;
    }

    private static boolean isFaultyStatus(String Status){
        final String STATUS_OK = "3";
        final String STATUS_AVAILABLE_NOT_WORKING = "2";
        final String STATUS_NOT_AVAILABLE = "1";
        final String STATUS_DEFAULT = "0";

        if (Status.compareToIgnoreCase(STATUS_DEFAULT) == 0) {
            return false;
        }

        if (Status.compareToIgnoreCase(STATUS_AVAILABLE_NOT_WORKING) == 0) {
            return true;
        }

        else {
            return false;
        }
    }

    public static int getFaultyComplexCount(List<ComplexHealthStats> data) {
        int faultyComplexCount = 0;
        int faultyCabinCount;
        for (ComplexHealthStats item : data) {
            faultyCabinCount = 0;

            faultyCabinCount += item.getFaultyMwc();
            faultyCabinCount += item.getFaultyFwc();
            faultyCabinCount += item.getFaultyPwc();
            faultyCabinCount += item.getFaultyMur();
            faultyCabinCount += item.getFaultyBwt();

            if (faultyCabinCount > 0) {
                faultyComplexCount++;
            }
        }

        return faultyComplexCount;
    }

    public static int getLowWaterLevelComplexCount(List<Health> data) {
        int lowWaterLevelComplexCount = 0;
        for (Health item : data) {

            if (isLowWaterLevel(item))
                lowWaterLevelComplexCount++;
        }
        return lowWaterLevelComplexCount;
    }

    public static boolean isLowWaterLevel(Health item) {
        //return false;
        final String STATUS_OK = "3";
        final String STATUS_BWT_NOT_AVAILABLE = "-1";

        if (item.getFreshWaterLevel().compareToIgnoreCase(STATUS_OK) != 0) {
            return true;
        }

        if (item.getRecycleWaterLevel().compareToIgnoreCase(STATUS_BWT_NOT_AVAILABLE) != 0 &&
                item.getRecycleWaterLevel().compareToIgnoreCase(STATUS_OK) != 0) {
            return true;
        }
        return false;
    }

}
