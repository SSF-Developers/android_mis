package sukriti.ngo.mis.repository.utils;

import android.util.Log;

import com.google.gson.Gson;

import sukriti.ngo.mis.repository.entity.BwtProfile;
import sukriti.ngo.mis.repository.entity.DailyFeedback;
import sukriti.ngo.mis.repository.entity.DailyUsage;
import sukriti.ngo.mis.repository.entity.DailyUsageCharge;
import sukriti.ngo.mis.repository.entity.UsageProfile;

import static sukriti.ngo.mis.repository.utils.DateConverter.toDbDate;
import static sukriti.ngo.mis.repository.utils.DateConverter.toDbTime;
import static sukriti.ngo.mis.repository.utils.DateConverter.toDbTimestamp;
import static sukriti.ngo.mis.utils.Utilities.convertToDbAmountCollected;
import static sukriti.ngo.mis.utils.Utilities.convertToDbCabinType;
import static sukriti.ngo.mis.utils.Utilities.convertToDbDuration;
import static sukriti.ngo.mis.utils.Utilities.convertToDbFeedback;

public class UsageProfileConverter {

    public static DailyUsage getDailyUsage(UsageProfile data) {
        Log.i("getDailyUsage", "getDailyUsage: "+ new Gson().toJson(data));

        return new DailyUsage(data.get_index(),data.get_ID(), toDbDate(Long.valueOf(data.getSERVER_TIMESTAMP())), toDbTime(Long.valueOf(data.getSERVER_TIMESTAMP())),
                toDbTimestamp(data.getSERVER_TIMESTAMP()), data.getTHING_NAME(), data.getCOMPLEX(), data.getCITY(),
                data.getDISTRICT(), data.getSTATE(), data.getCLIENT(), convertToDbCabinType(data
                .getSHORT_THING_NAME()), convertToDbDuration(data.getDuration()));
    }

    public static DailyFeedback getDailyFeedback(UsageProfile data) {
        return new DailyFeedback(data.get_index(),data.get_ID(), toDbDate(Long.valueOf(data.getSERVER_TIMESTAMP())),
                toDbTime(Long.valueOf(data.getSERVER_TIMESTAMP())),
                toDbTimestamp(data.getSERVER_TIMESTAMP()), data.getTHING_NAME(), data.getCOMPLEX(), data.getCITY(),
                data.getDISTRICT(), data.getSTATE(), data.getCLIENT(), convertToDbCabinType(data.getSHORT_THING_NAME()),
                convertToDbDuration(data.getDuration()), convertToDbFeedback(data.getFeedback()));
    }

    public static DailyUsageCharge getDailyUsageCharge(UsageProfile data) {
        return new DailyUsageCharge(data.get_index(),data.get_ID(), toDbDate(Long.valueOf(data.getSERVER_TIMESTAMP())), toDbTime(Long.valueOf(data.getSERVER_TIMESTAMP())),
                toDbTimestamp(data.getSERVER_TIMESTAMP()), data.getTHING_NAME(), data.getCOMPLEX(), data.getCITY(),
                data.getDISTRICT(), data.getSTATE(), data.getCLIENT(), convertToDbCabinType(data
                .getSHORT_THING_NAME()), convertToDbDuration(data.getDuration()), convertToDbAmountCollected(data.getAmountcollected()));
    }

    public static BwtProfile getBwtProfileWithDate(BwtProfile data) {
        data.setDate(toDbDate(Long.valueOf(data.getSERVER_TIMESTAMP())));
        return data;
    }

}
