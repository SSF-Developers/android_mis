package sukriti.ngo.mis.repository;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import sukriti.ngo.mis.dataModel.DbSyncStatus;
import sukriti.ngo.mis.dataModel._Result;
import sukriti.ngo.mis.interfaces.RepositoryCallback;
import sukriti.ngo.mis.repository.data.ChargeCollectionStats;
import sukriti.ngo.mis.repository.data.ComplexHealthStats;
import sukriti.ngo.mis.repository.data.ComplexWiseUsageData;
import sukriti.ngo.mis.repository.data.DailyAverageFeedback;
import sukriti.ngo.mis.repository.data.DailyBwtCollectionData;
import sukriti.ngo.mis.repository.data.DailyChargeCollection;
import sukriti.ngo.mis.repository.data.DailyChargeCollectionData;
import sukriti.ngo.mis.repository.data.DailyFeedbackCount;
import sukriti.ngo.mis.repository.data.DailyTicketEventCount;
import sukriti.ngo.mis.repository.data.DailyTicketEventSummary;
import sukriti.ngo.mis.repository.data.DailyUpiCollectionData;
import sukriti.ngo.mis.repository.data.DailyUsageCount;
import sukriti.ngo.mis.repository.data.DailyWaterRecycled;
import sukriti.ngo.mis.repository.data.DailyWaterRecycledStats;
import sukriti.ngo.mis.repository.data.FeedbackSummary;
import sukriti.ngo.mis.repository.data.FeedbackStatsData;
import sukriti.ngo.mis.repository.data.FeedbackSummaryData;
import sukriti.ngo.mis.repository.data.FeedbackComparisonData;
import sukriti.ngo.mis.repository.data.FeedbackTimelineData;
import sukriti.ngo.mis.repository.data.FeedbackWiseUserCount;
import sukriti.ngo.mis.repository.data.LatestTimestampData;
import sukriti.ngo.mis.repository.data.TicketResolutionTimelineData;
import sukriti.ngo.mis.repository.data.UsageComparisonStats;
import sukriti.ngo.mis.repository.data.UsageSummaryStats;
import sukriti.ngo.mis.repository.entity.AqiLumen;
import sukriti.ngo.mis.repository.entity.BwtConfig;
import sukriti.ngo.mis.repository.entity.BwtHealth;
import sukriti.ngo.mis.repository.entity.BwtProfile;
import sukriti.ngo.mis.repository.entity.Cabin;
import sukriti.ngo.mis.repository.entity.ClientRequest;
import sukriti.ngo.mis.repository.entity.CmsConfig;
import sukriti.ngo.mis.repository.entity.Complex;
import sukriti.ngo.mis.repository.entity.DailyFeedback;
import sukriti.ngo.mis.repository.entity.DailyUsage;
import sukriti.ngo.mis.repository.entity.DailyUsageCharge;
import sukriti.ngo.mis.repository.entity.Health;
import sukriti.ngo.mis.repository.entity.OdsConfig;
import sukriti.ngo.mis.repository.entity.QuickAccess;
import sukriti.ngo.mis.repository.entity.ResetProfile;
import sukriti.ngo.mis.repository.entity.Ticket;
import sukriti.ngo.mis.repository.entity.TicketProgress;
import sukriti.ngo.mis.repository.entity.UcemsConfig;
import sukriti.ngo.mis.repository.entity.UsageProfile;
import sukriti.ngo.mis.ui.administration.interfaces.RequestHandler;
import sukriti.ngo.mis.ui.complexes.data.CabinDetailsData;
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData;
import sukriti.ngo.mis.ui.complexes.data.DisplayBwtProfile;
import sukriti.ngo.mis.ui.complexes.data.DisplayResetProfile;
import sukriti.ngo.mis.ui.complexes.data.DisplayUsageProfile;
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData;
import sukriti.ngo.mis.ui.complexes.interfaces.BwtProfileRequestHandler;
import sukriti.ngo.mis.ui.complexes.interfaces.LatestAccessedCabinHandler;
import sukriti.ngo.mis.ui.complexes.interfaces.LatestAccessedComplexHandler;
import sukriti.ngo.mis.ui.complexes.interfaces.LatestCabinAqiLumenRequestHandler;
import sukriti.ngo.mis.ui.complexes.interfaces.LatestCabinBwtHealthRequestHandler;
import sukriti.ngo.mis.ui.complexes.interfaces.LatestCabinHealthRequestHandler;
import sukriti.ngo.mis.ui.complexes.interfaces.PropertiesListRequestHandler;
import sukriti.ngo.mis.ui.complexes.interfaces.ResetProfileRequestHandler;
import sukriti.ngo.mis.ui.complexes.interfaces.UsageProfileRequestHandler;
import sukriti.ngo.mis.ui.dashboard.interfaces.ChargeCollectionComparisonRequestHandler;
import sukriti.ngo.mis.ui.dashboard.interfaces.ChargeCollectionSummaryRequestHandler;
import sukriti.ngo.mis.ui.dashboard.interfaces.CheckQuickAccessRequestHandler;
import sukriti.ngo.mis.ui.dashboard.interfaces.ComplexWaterLevelRequestHandler;
import sukriti.ngo.mis.ui.dashboard.interfaces.ComplexHealthRequestHandler;
import sukriti.ngo.mis.ui.dashboard.interfaces.FeedbackStatsRequestHandler;
import sukriti.ngo.mis.ui.dashboard.interfaces.FeedbackSummaryRequestHandler;
import sukriti.ngo.mis.ui.dashboard.interfaces.FeedbackComparisonRequestHandler;
import sukriti.ngo.mis.ui.dashboard.interfaces.FeedbackTimelineRequestHandler;
import sukriti.ngo.mis.ui.dashboard.interfaces.QuickAccessRequestHandler;
import sukriti.ngo.mis.ui.dashboard.interfaces.RecycledWaterRequestHandler;
import sukriti.ngo.mis.ui.dashboard.interfaces.UsageComparisonRequestHandler;
import sukriti.ngo.mis.ui.dashboard.interfaces.UsageSummaryRequestHandler;
import sukriti.ngo.mis.ui.dashboard.repository.MisCabinData;
import sukriti.ngo.mis.ui.reports.data.UsageReportData;
import sukriti.ngo.mis.ui.reports.interfaces.ComplexUsageReportRequestHandler;
import sukriti.ngo.mis.ui.tickets.data.TicketDetailsData;
import sukriti.ngo.mis.ui.tickets.data.TicketListData;
import sukriti.ngo.mis.ui.tickets.fragments.detail.TicketDetail;
import sukriti.ngo.mis.utils.Nomenclature;
import sukriti.ngo.mis.utils.SharedPrefsClient;

import static java.lang.Thread.sleep;
import static sukriti.ngo.mis.repository.utils.DateConverter.toDateString;
import static sukriti.ngo.mis.repository.utils.DateConverter.toDbDate;
import static sukriti.ngo.mis.repository.utils.DateConverter.toDbDateString;
import static sukriti.ngo.mis.repository.utils.DateConverter.toDbTime;
import static sukriti.ngo.mis.repository.utils.DateConverter.toDbTimestamp;
import static sukriti.ngo.mis.repository.utils.HLWHelper.isFaultyDevice;
import static sukriti.ngo.mis.repository.utils.HLWHelper.isLowWaterLevel;
import static sukriti.ngo.mis.repository.utils.UsageProfileConverter.getBwtProfileWithDate;
import static sukriti.ngo.mis.repository.utils.UsageProfileConverter.getDailyFeedback;
import static sukriti.ngo.mis.repository.utils.UsageProfileConverter.getDailyUsage;
import static sukriti.ngo.mis.repository.utils.UsageProfileConverter.getDailyUsageCharge;
import static sukriti.ngo.mis.ui.complexes.data.CabinDetailsData.toDbCabinData;
import static sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData.toDbComplexData;
import static sukriti.ngo.mis.utils.ChartDataHelper.getCompletedDailyAverageFeedback;
import static sukriti.ngo.mis.utils.ChartDataHelper.getCompletedDailyChargeCollection;
import static sukriti.ngo.mis.utils.ChartDataHelper.getCompletedDailyDailyTicketEventCount;
import static sukriti.ngo.mis.utils.ChartDataHelper.getCompletedDailyUsageCount;
import static sukriti.ngo.mis.utils.ChartDataHelper.getCompletedDailyUserCountForFeedback;
import static sukriti.ngo.mis.utils.ChartDataHelper.getCompletedFeedbackWiseUserCount;
import static sukriti.ngo.mis.utils.ChartDataHelper.getDailyChargeCollectionTimeline;
import static sukriti.ngo.mis.utils.ChartDataHelper.getDailyTicketEventSummary;
import static sukriti.ngo.mis.utils.ChartDataHelper.getDailyUsageTimeline;
import static sukriti.ngo.mis.utils.ChartDataHelper.toLongTimestamp;
import static sukriti.ngo.mis.utils.Nomenclature.getCabinType;
import static sukriti.ngo.mis.utils.Utilities.getComplexNamesList;
import static sukriti.ngo.mis.utils.Utilities.getUsageProfileSubset;

public class DataRepository {

    //Health.class, BwtHealth.class, AqiLumen.class, UcemsConfig.class,
    //        OdsConfig.class, CmsConfig.class, ClientRequest.class, BwtConfig.class,UsageProfile.class,
    //        ResetProfile.class, BwtProfile.clas
    private DataAccess dataAccess;
    private boolean insertFlag;
    private SharedPrefsClient sharedPrefsClient;
    private LiveData<List<UsageProfile>> mAllData;
    public static final String SELECTED_ALL = "All";

    LiveData<List<UsageProfile>> getAllData() {
        return mAllData;
    }

    public DataRepository(Context application) {
        MisDB misDb = MisDB.getDatabase(application);
        this.dataAccess = misDb.dataDAO();
        sharedPrefsClient = new SharedPrefsClient(application);
    }


    public void countUsage(Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            List<DailyUsageCount> data = dataAccess.countDailyUsage();
            for (DailyUsageCount item : data)
                Log.i("__countUsage", item.date + " " + item.count);

            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void countUsage(Context context, Date fromDate, Date toDate, String durationLabel, UsageSummaryRequestHandler callback, Lifecycle lifecycle) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            String _fromDate = toDbDateString(fromDate);
            String _toDate = toDbDateString(toDate);

            Log.i("_countUsage", " " + _fromDate + "-" + _toDate);
            int totalCount = dataAccess.countUsage(_fromDate, _toDate);
            int mwcCount = dataAccess.countUsage(Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate);
            int fwcCount = dataAccess.countUsage(Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate);
            int pwcCount = dataAccess.countUsage(Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate);
            int murCount = dataAccess.countUsage(Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate);

            UsageSummaryStats summary = new UsageSummaryStats(fromDate, toDate, totalCount, mwcCount, fwcCount, pwcCount, murCount, durationLabel);
            returnCallback = () -> callback.onSuccess(summary);

            //FragmentIllegalState-Fix
            if (lifecycle.getCurrentState() == Lifecycle.State.RESUMED)
                handler.post(returnCallback);
            else
                Log.e("_illegalState", "prevented!!");

        }).start();
    }

    public void getUsageComparisonData(Context context, Date fromDate, Date toDate, String durationLabel, UsageComparisonRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            String _fromDate = toDbDateString(fromDate);
            String _toDate = toDbDateString(toDate);
            Log.i("_countUsageComparison", " " + _fromDate + "-" + _toDate);

            List<DailyUsageCount> totalDailyUsage = getCompletedDailyUsageCount(fromDate, toDate, dataAccess.countDailyUsage(_fromDate, _toDate));
            List<DailyUsageCount> maleDailyUsage = getCompletedDailyUsageCount(fromDate, toDate, dataAccess.countDailyUsage(Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
            List<DailyUsageCount> femaleDailyUsage = getCompletedDailyUsageCount(fromDate, toDate, dataAccess.countDailyUsage(Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
            List<DailyUsageCount> pdDailyUsage = getCompletedDailyUsageCount(fromDate, toDate, dataAccess.countDailyUsage(Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
            List<DailyUsageCount> murDailyUsage = getCompletedDailyUsageCount(fromDate, toDate, dataAccess.countDailyUsage(Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));

            UsageComparisonStats stats = new UsageComparisonStats(fromDate, toDate, totalDailyUsage, maleDailyUsage, femaleDailyUsage, pdDailyUsage, murDailyUsage, durationLabel);
            returnCallback = () -> callback.onSuccess(stats);
            handler.post(returnCallback);
        }).start();
    }

    public void getUsageTimelineData(Context context, Date fromDate, Date toDate, String durationLabel, UsageComparisonRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            String _fromDate = toDbDateString(fromDate);
            String _toDate = toDbDateString(toDate);
            Log.i("_countUsageComparison", " " + _fromDate + "-" + _toDate);

            List<DailyUsageCount> totalDailyUsage = getDailyUsageTimeline(fromDate, toDate, dataAccess.countDailyUsage(_fromDate, _toDate));
            List<DailyUsageCount> maleDailyUsage = getDailyUsageTimeline(fromDate, toDate, dataAccess.countDailyUsage(Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
            List<DailyUsageCount> femaleDailyUsage = getDailyUsageTimeline(fromDate, toDate, dataAccess.countDailyUsage(Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
            List<DailyUsageCount> pdDailyUsage = getDailyUsageTimeline(fromDate, toDate, dataAccess.countDailyUsage(Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
            List<DailyUsageCount> murDailyUsage = getDailyUsageTimeline(fromDate, toDate, dataAccess.countDailyUsage(Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));

            UsageComparisonStats stats = new UsageComparisonStats(fromDate, toDate, totalDailyUsage, maleDailyUsage, femaleDailyUsage, pdDailyUsage, murDailyUsage, durationLabel);
            returnCallback = () -> callback.onSuccess(stats);
            handler.post(returnCallback);
        }).start();
    }

    //Feedback
    public void countTotalFeedback(Context context, Date fromDate, Date toDate, String durationLabel, FeedbackStatsRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            String _fromDate = toDbDateString(fromDate);
            String _toDate = toDbDateString(toDate);

            Log.i("_countFeedback", " " + _fromDate + "-" + _toDate);
            FeedbackSummary totalCount = dataAccess.countTotalFeedback(_fromDate, _toDate);
            FeedbackSummary mwcCount = dataAccess.countTotalFeedback(Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate);
            FeedbackSummary fwcCount = dataAccess.countTotalFeedback(Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate);
            FeedbackSummary pwcCount = dataAccess.countTotalFeedback(Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate);
            FeedbackSummary murCount = dataAccess.countTotalFeedback(Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate);


            FeedbackStatsData summary = new FeedbackStatsData(fromDate, toDate, totalCount, mwcCount, fwcCount, pwcCount, murCount, durationLabel);
            returnCallback = () -> callback.onSuccess(summary);
            handler.post(returnCallback);
        }).start();
    }

    public void countUsersForFeedback(Context context, Date fromDate, Date toDate, String durationLabel, FeedbackSummaryRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            String _fromDate = toDbDateString(fromDate);
            String _toDate = toDbDateString(toDate);

            Log.i("_countUsersForFeedback", " " + _fromDate + "-" + _toDate);
            List<FeedbackWiseUserCount> mwcCount = getCompletedFeedbackWiseUserCount(
                    dataAccess.countUsersForFeedback(Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
            List<FeedbackWiseUserCount> fwcCount = getCompletedFeedbackWiseUserCount(
                    dataAccess.countUsersForFeedback(Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
            List<FeedbackWiseUserCount> pwcCount = getCompletedFeedbackWiseUserCount(
                    dataAccess.countUsersForFeedback(Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
            List<FeedbackWiseUserCount> murCount = getCompletedFeedbackWiseUserCount(
                    dataAccess.countUsersForFeedback(Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));


            FeedbackSummaryData summary = new FeedbackSummaryData(fromDate, toDate, mwcCount, fwcCount, pwcCount, murCount, durationLabel);
            returnCallback = () -> callback.onSuccess(summary);
            handler.post(returnCallback);
        }).start();
    }

    public void countDailyUserCountForFeedback(Context context, Date fromDate, Date toDate, String durationLabel, FeedbackComparisonRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            String _fromDate = toDbDateString(fromDate);
            String _toDate = toDbDateString(toDate);

            Log.i("_userCountForFeedback", " " + _fromDate + "-" + _toDate);

            List<DailyFeedbackCount> _1starData = getCompletedDailyUserCountForFeedback(
                    fromDate, toDate, dataAccess.countDailyUserCountForFeedback(_fromDate, _toDate, 1), 1);
            List<DailyFeedbackCount> _2starData = getCompletedDailyUserCountForFeedback(
                    fromDate, toDate, dataAccess.countDailyUserCountForFeedback(_fromDate, _toDate, 2), 2);
            List<DailyFeedbackCount> _3starData = getCompletedDailyUserCountForFeedback(
                    fromDate, toDate, dataAccess.countDailyUserCountForFeedback(_fromDate, _toDate, 3), 3);
            List<DailyFeedbackCount> _4starData = getCompletedDailyUserCountForFeedback(
                    fromDate, toDate, dataAccess.countDailyUserCountForFeedback(_fromDate, _toDate, 4), 4);
            List<DailyFeedbackCount> _5starData = getCompletedDailyUserCountForFeedback(
                    fromDate, toDate, dataAccess.countDailyUserCountForFeedback(_fromDate, _toDate, 5), 5);


            FeedbackComparisonData summary = new FeedbackComparisonData(fromDate, toDate, _1starData, _2starData, _3starData, _4starData, _5starData, durationLabel);
            returnCallback = () -> callback.onSuccess(summary);
            handler.post(returnCallback);
        }).start();
    }

    public void getFeedbackTimelineData(Context context, Date fromDate, Date toDate, String durationLabel, FeedbackTimelineRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            String _fromDate = toDbDateString(fromDate);
            String _toDate = toDbDateString(toDate);
            Log.i("_countUsageComparison", " " + _fromDate + "-" + _toDate);

            List<DailyAverageFeedback> averageDailyFeedback = getCompletedDailyAverageFeedback(fromDate, toDate,
                    dataAccess.countDailyFeedback(_fromDate, _toDate));
            List<DailyAverageFeedback> averageMwcDailyFeedback = getCompletedDailyAverageFeedback(fromDate, toDate,
                    dataAccess.countDailyFeedback(Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
            List<DailyAverageFeedback> averageFwcDailyFeedback = getCompletedDailyAverageFeedback(fromDate, toDate,
                    dataAccess.countDailyFeedback(Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
            List<DailyAverageFeedback> averagePwcDailyFeedback = getCompletedDailyAverageFeedback(fromDate, toDate,
                    dataAccess.countDailyFeedback(Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
            List<DailyAverageFeedback> averageMurDailyFeedback = getCompletedDailyAverageFeedback(fromDate, toDate,
                    dataAccess.countDailyFeedback(Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));

            FeedbackTimelineData stats = new FeedbackTimelineData(fromDate, toDate, averageDailyFeedback,
                    averageMwcDailyFeedback, averageFwcDailyFeedback, averagePwcDailyFeedback,
                    averageMurDailyFeedback, durationLabel);

            returnCallback = () -> callback.onSuccess(stats);
            handler.post(returnCallback);
        }).start();
    }

    //UsageChargeCollection
    public void countTotalChargeCollection(Context context, Date fromDate, Date toDate, String durationLabel, ChargeCollectionSummaryRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            String _fromDate = toDbDateString(fromDate);
            String _toDate = toDbDateString(toDate);

            Log.i("_countUsage", " " + _fromDate + "-" + _toDate);

            float totalCollection = 0;
            try {
                totalCollection = dataAccess.countTotalCollection(_fromDate, _toDate);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            float mwcCollection = 0;
            try {
                mwcCollection = dataAccess.countTotalCollection(Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            float fwcCollection = 0;
            try {
                fwcCollection = dataAccess.countTotalCollection(Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            float pwcCollection = 0;
            try {
                pwcCollection = dataAccess.countTotalCollection(Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            float murCollection = 0;
            try {
                murCollection = dataAccess.countTotalCollection(Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            ChargeCollectionStats summary = new ChargeCollectionStats(fromDate, toDate, totalCollection,
                    mwcCollection, fwcCollection, pwcCollection, murCollection, durationLabel);
            returnCallback = () -> callback.onSuccess(summary);
            handler.post(returnCallback);
        }).start();
    }

    public void getDailyChargeCollection(Context context, Date fromDate, Date toDate, String durationLabel, ChargeCollectionComparisonRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            String _fromDate = toDbDateString(fromDate);
            String _toDate = toDbDateString(toDate);

            Log.i("_countUsage", " " + _fromDate + "-" + _toDate);
            List<DailyChargeCollection> totalCollection = getCompletedDailyChargeCollection(
                    fromDate, toDate, dataAccess.countDailyCollection(_fromDate, _toDate));
            List<DailyChargeCollection> mwcCollection = getCompletedDailyChargeCollection(
                    fromDate, toDate, dataAccess.countDailyCollection(Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
            List<DailyChargeCollection> fwcCollection = getCompletedDailyChargeCollection(
                    fromDate, toDate, dataAccess.countDailyCollection(Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
            List<DailyChargeCollection> pwcCollection = getCompletedDailyChargeCollection(
                    fromDate, toDate, dataAccess.countDailyCollection(Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
            List<DailyChargeCollection> murCollection = getCompletedDailyChargeCollection(
                    fromDate, toDate, dataAccess.countDailyCollection(Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));

            DailyChargeCollectionData summary = new DailyChargeCollectionData(fromDate, toDate, totalCollection,
                    mwcCollection, fwcCollection, pwcCollection, murCollection, durationLabel);
            returnCallback = () -> callback.onSuccess(summary);
            handler.post(returnCallback);
        }).start();
    }

    public void getTimelineChargeCollection(Context context, Date fromDate, Date toDate, String durationLabel, ChargeCollectionComparisonRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            String _fromDate = toDbDateString(fromDate);
            String _toDate = toDbDateString(toDate);

            Log.i("_countUsage", " " + _fromDate + "-" + _toDate);
            List<DailyChargeCollection> totalCollection = getDailyChargeCollectionTimeline(
                    fromDate, toDate, dataAccess.countDailyCollection(_fromDate, _toDate));
            List<DailyChargeCollection> mwcCollection = getDailyChargeCollectionTimeline(
                    fromDate, toDate, dataAccess.countDailyCollection(Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
            List<DailyChargeCollection> fwcCollection = getDailyChargeCollectionTimeline(
                    fromDate, toDate, dataAccess.countDailyCollection(Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
            List<DailyChargeCollection> pwcCollection = getDailyChargeCollectionTimeline(
                    fromDate, toDate, dataAccess.countDailyCollection(Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
            List<DailyChargeCollection> murCollection = getDailyChargeCollectionTimeline(
                    fromDate, toDate, dataAccess.countDailyCollection(Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));

            DailyChargeCollectionData summary = new DailyChargeCollectionData(fromDate, toDate, totalCollection,
                    mwcCollection, fwcCollection, pwcCollection, murCollection, durationLabel);
            returnCallback = () -> callback.onSuccess(summary);
            handler.post(returnCallback);
        }).start();
    }


    RequestHandler insertHandler = new RequestHandler() {
        @Override
        public void onSuccess() {
            insertFlag = false;
        }

        @Override
        public void onError(String message) {
            insertFlag = false;
        }
    };

    public void insertBulkData(Context context, MisCabinData result) {
        final int sleepTime = 2;
        for (Health item : result.healthTableData) {
            insertFlag = true;
            insertData(item, context, insertHandler);
            while (insertFlag) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (BwtHealth item : result.bwtHealthData) {
            insertFlag = true;
            insertData(item, context, insertHandler);
            while (insertFlag) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (AqiLumen item : result.aqiLumenData) {
            insertFlag = true;
            insertData(item, context, insertHandler);
            while (insertFlag) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (UcemsConfig item : result.ucemsConfigData) {
            insertFlag = true;
            insertData(item, context, insertHandler);
            while (insertFlag) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (OdsConfig item : result.odsConfigData) {
            insertFlag = true;
            insertData(item, context, insertHandler);
            while (insertFlag) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (CmsConfig item : result.cmsConfigData) {
            insertFlag = true;
            insertData(item, context, insertHandler);
            while (insertFlag) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.i("__ClientRequest", "bulkInsertCount: " + result.clientRequestData.size());
        for (ClientRequest item : result.clientRequestData) {
            Log.i("__ClientRequest", "bulkInsert: " + item.getTHING_NAME());
            insertFlag = true;
            insertData(item, context, insertHandler);
            while (insertFlag) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (BwtConfig item : result.bwtConfigData) {
            insertFlag = true;
            insertData(item, context, insertHandler);
            while (insertFlag) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (UsageProfile item : result.usageProfileData) {
            insertFlag = true;
            insertData(item, context, insertHandler);
            while (insertFlag) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (UsageProfile item : result.usageProfileData) {
            insertFlag = true;
            insertData(getDailyUsage(item), context, insertHandler);
            while (insertFlag) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (UsageProfile item : result.usageProfileData) {
            insertFlag = true;
            DailyFeedback feedbackItem = getDailyFeedback(item);
            if (feedbackItem.getFeedback() > 0) {
                insertData(feedbackItem, context, insertHandler);
                while (insertFlag) {
                    try {
                        sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        for (UsageProfile item : result.usageProfileData) {
            insertFlag = true;
            insertData(getDailyUsageCharge(item), context, insertHandler);
            while (insertFlag) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (ResetProfile item : result.resetProfileData) {
            insertFlag = true;
            insertData(item, context, insertHandler);
            while (insertFlag) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (BwtProfile item : result.bwtProfileData) {
            insertFlag = true;
            insertData(getBwtProfileWithDate(item), context, insertHandler);
            while (insertFlag) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Ticket item : result.allTickets) {
            insertFlag = true;
            insertData(item, context, insertHandler);
            while (insertFlag) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (TicketProgress item : result.ticketProgress) {
            insertFlag = true;
            item.setDateTime();
            Log.i("_insertProgress-2", "" + item.getDate());
            insertData(item, context, insertHandler);
            while (insertFlag) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        RequestHandler setLatestTimestampHandler = new RequestHandler() {
            @Override
            public void onSuccess() {
                insertFlag = false;
            }

            @Override
            public void onError(String message) {

            }
        };

        insertFlag = true;
        setLatestTimestamp(context, setLatestTimestampHandler);
        while (insertFlag) try {
            sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //public void insertBulkTicketsData(List<Ticket> allTickets, List<TicketProgress> ticketProgress, final Handler handler, RequestHandler callback) {
    public void insertBulkTicketsData(List<Ticket> allTickets, List<TicketProgress> ticketProgress, final Handler handler, RequestHandler callback) {
        new Thread(() -> {
            Runnable returnCallback;
            for (Ticket ticket : allTickets) {
                Log.i("_insertBulkTicketsData", "" + ticket.getTicket_id());
                dataAccess.insertItem(ticket);
            }

            for (TicketProgress item : ticketProgress) {
                item.setDateTime();
                Log.i("_insertProgress-1", "" + item.getDate());
                dataAccess.insertItem(item);
            }

            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void resetBulkUnQueuedStatus(List<Ticket> unQueuedTickets, final Handler handler, RequestHandler callback) {
        new Thread(() -> {
            Runnable returnCallback;
            dataAccess.setAllTicketsNotQueued();
            for (Ticket ticket : unQueuedTickets) {
                dataAccess.updateTicketQueuedStatus(true, ticket.getTicket_id());
            }

            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void resetBulkQueuedStatus(List<Ticket> queuedTickets, final Handler handler, RequestHandler callback) {
        new Thread(() -> {
            Runnable returnCallback;
            dataAccess.setAllTicketsNotQueued();
            for (Ticket ticket : queuedTickets) {
                dataAccess.updateTicketQueuedStatus(true, ticket.getTicket_id());
            }

            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(Ticket dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            Log.i("__insertData", dataItem.getTicket_id());
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.insertItem(dataItem);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(TicketProgress dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            Log.i("__insertData", dataItem.getTicket_id());
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.insertItem(dataItem);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(Health dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.insertItem(dataItem);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(BwtHealth dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.insertItem(dataItem);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(AqiLumen dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.insertItem(dataItem);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(UcemsConfig dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.insertItem(dataItem);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(OdsConfig dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.insertItem(dataItem);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(CmsConfig dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.insertItem(dataItem);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(ClientRequest dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.insertItem(dataItem);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(BwtConfig dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.insertItem(dataItem);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(UsageProfile dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            long x = dataAccess.insertItem(dataItem);
            //Log.i("__insertBulkData",""+ DateConverter.toDbDate(DateConverter.toDbTimestamp(dataItem.getSERVER_TIMESTAMP())));

            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(ResetProfile dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.insertItem(dataItem);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(BwtProfile dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.insertItem(dataItem);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(DailyUsage dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.insertItem(dataItem);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(DailyFeedback dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.insertItem(dataItem);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void insertData(DailyUsageCharge dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.insertItem(dataItem);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }


    //Health.class, BwtHealth.class, AqiLumen.class, UcemsConfig.class,
    //        OdsConfig.class, CmsConfig.class, ClientRequest.class, BwtConfig.class,UsageProfile.class,
    //        ResetProfile.class, BwtProfile.class

    public void truncateAllTables(Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.truncateHealth();
            dataAccess.truncateBwtHealth();
            dataAccess.truncateAqiLumen();
            dataAccess.truncateUcemsConfig();
            dataAccess.truncateOdsConfig();
            dataAccess.truncateCmsConfig();
            dataAccess.truncateClientRequest();
            dataAccess.truncateBwtConfig();
            dataAccess.truncateUsageProfile();
            dataAccess.truncateResetProfile();
            dataAccess.truncateBwtProfile();
            dataAccess.truncateDailyUsage();
            dataAccess.truncateDailyFeedback();
            dataAccess.truncateDailyUsageCharge();
            dataAccess.truncateQuickAccess();
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void setLatestTimestamp(Context context, RequestHandler callback) {
        new Thread(() -> {
            SharedPrefsClient sharedPrefsClient = new SharedPrefsClient(context);
            DbSyncStatus dbSyncStatus = sharedPrefsClient.getDbSyncStatus();
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            Long healthTimestamp = toLongTimestamp(dataAccess.getLatestTimestampForHealth());
            Long BwtHealthTimestamp = toLongTimestamp(dataAccess.getLatestTimestampForBwtHealth());
            Long AqiLumenTimestamp = toLongTimestamp(dataAccess.getLatestTimestampForAqiLumen());
            Long UcemsConfigTimestamp = toLongTimestamp(dataAccess.getLatestTimestampForUcemsConfig());
            Long OdsConfigTimestamp = toLongTimestamp(dataAccess.getLatestTimestampForOdsConfig());
            Long CmsConfigTimestamp = toLongTimestamp(dataAccess.getLatestTimestampForCmsConfig());
            Long ClientRequestTimestamp = toLongTimestamp(dataAccess.getLatestTimestampForClientRequest());
            Long BwtConfigTimestamp = toLongTimestamp(dataAccess.getLatestTimestampForBwtConfig());
            Long UsageProfileTimestamp = toLongTimestamp(dataAccess.getLatestTimestampForUsageProfile());
            Long ResetProfileTimestamp = toLongTimestamp(dataAccess.getLatestTimestampForResetProfile());
            Long BwtProfileTimestamp = toLongTimestamp(dataAccess.getLatestTimestampForBwtProfile());

            LatestTimestampData data = new LatestTimestampData(
                    healthTimestamp,
                    BwtHealthTimestamp,
                    AqiLumenTimestamp,
                    UcemsConfigTimestamp,
                    OdsConfigTimestamp,
                    CmsConfigTimestamp,
                    ClientRequestTimestamp,
                    BwtConfigTimestamp,
                    UsageProfileTimestamp,
                    ResetProfileTimestamp,
                    BwtProfileTimestamp);

            dbSyncStatus.setLastTimestamp(data);
            dbSyncStatus.setNewLogin(false);
            sharedPrefsClient.saveDbSyncStatus(dbSyncStatus);

            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void listFaultyComplexes(Context context, ComplexHealthRequestHandler callback) {
        new Thread(() -> {
            List<String> cabinNames = dataAccess.getAllCabinsForHealthTable();
            List<String> bwtCabinNames = dataAccess.getAllCabinsForBwtHealthTable();
            List<String> complexNames = dataAccess.getAllComplexesForHealthTable();
            HashMap<String, ComplexHealthStats> healthStats = new HashMap<>();
            for (String complexName : complexNames) {
                healthStats.put(complexName, new ComplexHealthStats(complexName));
            }

            Health item;
            BwtHealth bwtItem;
            String date;
            ComplexHealthStats complexHealth;
            for (String thingName : cabinNames) {
                item = dataAccess.getCurrentHealthStatus(thingName);
                date = toDateString(toDbTimestamp(item.getSERVER_TIMESTAMP()));


                complexHealth = healthStats.get(item.getCOMPLEX());

                if (getCabinType(item.getSHORT_THING_NAME(), Nomenclature.CABIN_TYPE_MWC)) {
                    complexHealth.incrementTotalMwc();
                    if (isFaultyDevice(item)) {
                        complexHealth.incrementFaultyMwc();
                    }
                }

                if (getCabinType(item.getSHORT_THING_NAME(), Nomenclature.CABIN_TYPE_FWC)) {
                    complexHealth.incrementTotalFwc();
                    if (isFaultyDevice(item)) {
                        complexHealth.incrementFaultyFwc();
                    }
                }

                if (getCabinType(item.getSHORT_THING_NAME(), Nomenclature.CABIN_TYPE_PWC)) {
                    complexHealth.incrementTotalPwc();
                    if (isFaultyDevice(item)) {
                        complexHealth.incrementFaultyPwc();
                    }
                }

                if (getCabinType(item.getSHORT_THING_NAME(), Nomenclature.CABIN_TYPE_MUR)) {
                    complexHealth.incrementTotalMur();
                    if (isFaultyDevice(item)) {
                        complexHealth.incrementFaultyMur();
                    }
                }

                healthStats.put(item.getCOMPLEX(), complexHealth);
            }

            for (String thingName : bwtCabinNames) {
                bwtItem = dataAccess.getCurrentBwtHealthStatus(thingName);
                date = toDateString(toDbTimestamp(bwtItem.getSERVER_TIMESTAMP()));
                complexHealth = healthStats.get(bwtItem.getCOMPLEX());

                complexHealth.incrementTotalBwt();
                if (isFaultyDevice(bwtItem)) {
                    complexHealth.incrementFaultyBwt();
                }
            }


            List<ComplexHealthStats> faultyComplexList = new ArrayList<>();
            ComplexHealthStats complexHealthItem;
            Iterator it = healthStats.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                complexHealthItem = ((ComplexHealthStats) pair.getValue());
                Log.i("_isFaultyDevice_", pair.getKey() + ", faults: " + complexHealthItem.getTotalFaultyCount());
                if (complexHealthItem.getTotalFaultyCount() > 0) {
                    faultyComplexList.add(complexHealthItem);
                }
                it.remove();
            }

            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            returnCallback = () -> callback.onSuccess(faultyComplexList);
            handler.post(returnCallback);
        }).start();

    }

    public void listComplexWaterLevelStatus(Context context, ComplexWaterLevelRequestHandler callback) {
        new Thread(() -> {
            List<String> complexNames = dataAccess.getAllComplexesForHealthTable();
            List<Health> complexWaterLevel = new ArrayList<>();
            Health item;
            String date;

            boolean hasBwtInstalled;
            for (String complexName : complexNames) {
                hasBwtInstalled = dataAccess.getBwtProfileCount(complexName) > 0;
                item = dataAccess.getCurrentWaterStatus(complexName);

                if (!hasBwtInstalled)
                    item.setRecycleWaterLevel("-1");

                if (isLowWaterLevel(item))
                    complexWaterLevel.add(item);

                date = toDateString(toDbTimestamp(item.getSERVER_TIMESTAMP()));
                Log.i("_listCurrentWaterData", item.getCOMPLEX() + ", " + item.getSHORT_THING_NAME() + ": " + date + ": fresh:" + item.getFreshWaterLevel() + ", re-cycle: " + item.getRecycleWaterLevel());
            }
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            returnCallback = () -> callback.onSuccess(complexWaterLevel);
            handler.post(returnCallback);
        }).start();
    }

    public void listRecycledWaterStats(Context context, Date fromDate, Date toDate, String durationLabel, RecycledWaterRequestHandler callback) {
        new Thread(() -> {
            DailyWaterRecycledStats stats;
            boolean hasBwtInstalled = dataAccess.getBwtProfileCount() > 0;
            if (hasBwtInstalled) {
                List<DailyWaterRecycled> data = new ArrayList<>();
                stats = new DailyWaterRecycledStats(false, fromDate, toDate, data, durationLabel);
            } else {
                String _fromDate = toDbDateString(fromDate);
                String _toDate = toDbDateString(toDate);
                List<DailyWaterRecycled> data = dataAccess.countDailyWaterRecycled(_fromDate, _toDate);
                stats = new DailyWaterRecycledStats(true, fromDate, toDate, data, durationLabel);

            }

            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            returnCallback = () -> callback.onSuccess(stats);
            handler.post(returnCallback);
        }).start();
    }


    //COMPLEX ACCESS
    Runnable _returnCallback;

    public void insertComplexAccessLog(ComplexDetailsData dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {

            final Handler handler = new Handler(context.getMainLooper());

            int accessLogCount = dataAccess.getComplexAccessCount();
            Log.i("_ComplexAccessLog", "accessLogCount" + accessLogCount);
            if (accessLogCount >= Nomenclature.COMPLEX_ACCESS_LOG_SIZE) {

                deleteOldestComplex(context, new RequestHandler() {
                    @Override
                    public void onSuccess() {
                        Log.i("_ComplexAccessLog", "deleteOldestComplex() - onSuccess");
                        dataAccess.XinsertItem(toDbComplexData(dataItem));
                        _returnCallback = () -> callback.onSuccess();
                        handler.post(_returnCallback);
                    }

                    @Override
                    public void onError(String message) {
                        Log.i("_ComplexAccessLog", "deleteOldestComplex() - onError");
                        _returnCallback = () -> callback.onError(message);
                        handler.post(_returnCallback);
                    }
                });
            } else {
                Complex newComplex = toDbComplexData(dataItem);
                Log.i("_ComplexAccessLog", newComplex.getComplexName());
                dataAccess.XinsertItem(newComplex);

                List<Complex> list = dataAccess.listAllAccessedComplex();
                for (Complex item : list) {
                    Log.i("_ComplexAccessLog", " all: " + item.getComplexName());
                }

                _returnCallback = () -> callback.onSuccess();
                handler.post(_returnCallback);
            }

        }).start();
    }

    public void deleteOldestComplex(Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            Complex oldest = dataAccess.getComplexWithOldestAccessTimestamp();
            dataAccess.deleteComplex(oldest.getComplexName());
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }


    public void getComplexAccessList(Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            Complex oldest = dataAccess.getComplexWithOldestAccessTimestamp();
            dataAccess.deleteComplex(oldest.getComplexName());
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void getLatestAccessedComplex(Context context, LatestAccessedComplexHandler callback) {
        new Thread(() -> {

            List<Complex> list = dataAccess.listAllAccessedComplex();
            for (Complex item : list) {
                Log.i("_setSelection", " all: " + item.getComplexName());
            }

            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            Complex latest = dataAccess.listLatestAccessedComplex();
            returnCallback = () -> callback.getLatestAccessedComplex(latest);
            handler.post(returnCallback);
        }).start();
    }


    public void insertCabinAccessLog(CabinDetailsData dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {

            final Handler handler = new Handler(context.getMainLooper());

            int accessLogCount = dataAccess.getCabinAccessCount();
            if (accessLogCount >= 1) {
                dataAccess.truncateCabin();
            }

            dataAccess.insertItem(toDbCabinData(dataItem));
            _returnCallback = () -> callback.onSuccess();
            handler.post(_returnCallback);


        }).start();
    }

    public void getLatestAccessedCabin(Context context, LatestAccessedCabinHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            Cabin latest = dataAccess.listLatestAccessedCabin();
            returnCallback = () -> callback.getLatestAccessedCabin(latest);
            handler.post(returnCallback);
        }).start();
    }

    //CABIN DETAILS
    public void getCabinHealth(String thingName, Context context, LatestCabinHealthRequestHandler callback) {
        new Thread(() -> {

//            List<String> all = dataAccess.getAllCabinsForHealthTable();
//            for (String item : all) {
//                Log.i("__AllCabinsForHealth", item);
//            }

            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            Health latest = dataAccess.getCabinHealth(thingName);
            if (latest == null) {
                returnCallback = () -> callback.onError("No health data found for the selected cabin");
            } else {
                returnCallback = () -> callback.getData(latest);
            }
            handler.post(returnCallback);
        }).start();
    }

    public void getCabinBwtHealth(String thingName, Context context, LatestCabinBwtHealthRequestHandler callback) {
        new Thread(() -> {

//            List<String> all = dataAccess.getAllCabinsForHealthTable();
//            for (String item : all) {
//                Log.i("__AllCabinsForHealth", item);
//            }

            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            BwtHealth latest = dataAccess.getCabinBwtHealth(thingName);
            if (latest == null) {
                returnCallback = () -> callback.onError("No health data found for the selected cabin");
            } else
                returnCallback = () -> callback.getData(latest);
            handler.post(returnCallback);
        }).start();
    }

    public void getCabinAqiLumen(String thingName, Context context, LatestCabinAqiLumenRequestHandler callback) {
        new Thread(() -> {

            List<String> all = dataAccess.getAllCabinsForAqiLumenTable();
            for (String item : all) {
                Log.i("_AqiLumen", item);
            }

            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            AqiLumen latest = dataAccess.getCabinAqiLumen(thingName);
            if (latest == null) {
                Log.i("_AqiLumen", "" + thingName);
                returnCallback = () -> callback.onError("No data found for the selected cabin");
            } else
                returnCallback = () -> callback.getData(latest);

            handler.post(returnCallback);
        }).start();
    }

    public void getCabinUcemsConfig(String thingName, Context context, PropertiesListRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            UcemsConfig latest = dataAccess.getCabinUcemsConfig(thingName);
            if (latest == null) {
                returnCallback = () -> callback.getData(new ArrayList<PropertyNameValueData>(), "No data found for selected cabin");
            } else {
                Log.i("__stats", new Gson().toJson(latest));
                returnCallback = () -> callback.getData(latest.getPropertiesList(), latest.getDEVICE_TIMESTAMP());
            }
            handler.post(returnCallback);
        }).start();
    }

    public void getCabinOdsConfig(String thingName, Context context, PropertiesListRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            OdsConfig latest = dataAccess.getCabinOdsConfig(thingName);
            if (latest == null) {
                returnCallback = () -> callback.getData(new ArrayList<PropertyNameValueData>(), "No data found for selected cabin");
            } else
                returnCallback = () -> callback.getData(latest.getPropertiesList(), latest.getDEVICE_TIMESTAMP());
            handler.post(returnCallback);
        }).start();
    }

    public void getCabinCmsConfig(String thingName, Context context, PropertiesListRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            CmsConfig latest = dataAccess.getCabinCmsConfig(thingName);
            if (latest == null) {
                returnCallback = () -> callback.getData(new ArrayList<PropertyNameValueData>(), "No data found for selected cabin");
            } else
                returnCallback = () -> callback.getData(latest.getPropertiesList(), latest.getDEVICE_TIMESTAMP());
            handler.post(returnCallback);
        }).start();
    }

    public void getCabinBwtConfig(String thingName, Context context, PropertiesListRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            BwtConfig latest = dataAccess.getCabinBwtConfig(thingName);
            if (latest == null) {
                returnCallback = () -> callback.getData(new ArrayList<PropertyNameValueData>(), "0");
            } else
                returnCallback = () -> callback.getData(latest.getPropertiesList(), latest.getDEVICE_TIMESTAMP());
            handler.post(returnCallback);
        }).start();
    }

    public void getCabinDataShareFields(String thingName, Context context, PropertiesListRequestHandler callback) {
        new Thread(() -> {
            Log.i("__ClientRequest", "getCabinDataShareFields()");

//            int x = dataAccess.getCabinClientRequestCount();
//            Log.i("__ClientRequest"," "+x);

//            List<ClientRequest> all = dataAccess.getCabinClientRequest();
//            for(ClientRequest item : all){
//                Log.i("__ClientRequest",item.getCOMPLEX()+", "+item.getTHING_NAME()+", "+new Gson().toJson(item));
//            }

            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            ClientRequest latest = dataAccess.getCabinClientRequest(thingName);
            if (latest == null) {
                returnCallback = () -> callback.getData(new ArrayList<PropertyNameValueData>(), "0");
            } else
                returnCallback = () -> callback.getData(latest.getPropertiesList(), latest.getDEVICE_TIMESTAMP());

            handler.post(returnCallback);
        }).start();
    }

    public void getDisplayUsageProfile(Complex complex, Cabin cabin, Context context, Date fromDate, Date toDate, UsageProfileRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            List<UsageProfile> data = dataAccess.getProfileUsage(cabin.getThingName(), "" + fromDate.getTime(), "" + toDate.getTime());
            List<DisplayUsageProfile> displayData = new ArrayList<>();
            DisplayUsageProfile displayItem;
            for (UsageProfile item : data) {
                item.setSTATE(complex.getStateCode());
                item.setDISTRICT(complex.getDistrictName());
                item.setCITY(complex.getCityName());
                displayItem = new DisplayUsageProfile(item, toDbDate(toDbTimestamp(item.getSERVER_TIMESTAMP())), toDbTime(toDbTimestamp(item.getSERVER_TIMESTAMP())));
                displayData.add(displayItem);
            }
            returnCallback = () -> callback.getData(displayData);
            handler.post(returnCallback);
        }).start();
    }

    public void getDisplayBwtProfile(Complex complex, Cabin cabin, Context context, Date fromDate, Date toDate, BwtProfileRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            List<BwtProfile> data = dataAccess.getProfileBwt(cabin.getThingName(), "" + fromDate.getTime(), "" + toDate.getTime());
            //List<BwtProfile> data = dataAccess.getProfileBwt();
            List<DisplayBwtProfile> displayData = new ArrayList<>();
            DisplayBwtProfile displayItem;
            for (BwtProfile item : data) {
                displayItem = new DisplayBwtProfile(item, toDbDate(toDbTimestamp(item.getSERVER_TIMESTAMP())), toDbTime(toDbTimestamp(item.getSERVER_TIMESTAMP())));
                displayData.add(displayItem);
            }
            returnCallback = () -> callback.getData(displayData);
            handler.post(returnCallback);
        }).start();
    }

    public void getDisplayResetProfile(Complex complex, Cabin cabin, Context context, Date fromDate, Date toDate, ResetProfileRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            List<ResetProfile> data = dataAccess.getProfileReset(cabin.getThingName(), "" + fromDate.getTime(), "" + toDate.getTime());
            //List<ResetProfile> data = dataAccess.getProfileReset();
            List<DisplayResetProfile> displayData = new ArrayList<>();
            DisplayResetProfile displayItem;
            for (ResetProfile item : data) {
                displayItem = new DisplayResetProfile(item, toDbDate(toDbTimestamp(item.getDEVICE_TIMESTAMP())), toDbTime(toDbTimestamp(item.getDEVICE_TIMESTAMP())));
                displayData.add(displayItem);
            }
            returnCallback = () -> callback.getData(displayData);
            handler.post(returnCallback);
        }).start();
    }

    //Quick Access
    public void insertData(QuickAccess dataItem, Context context, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            int quickAccessListSize = dataAccess.getQuickAccessList().size();
            if (quickAccessListSize < Nomenclature.QuickAccessListLimit) {
                dataAccess.insertItem(dataItem);
                returnCallback = () -> callback.onSuccess();
                handler.post(returnCallback);
            } else {
                returnCallback = () -> callback.onError("Max quick access limit reached, you can remove a cabin from quick access list to continue. Current Quick Access Limit: " + Nomenclature.QuickAccessListLimit + " cabins.");
                handler.post(returnCallback);
            }
        }).start();
    }

    public void deleteQuickAccess(Context context, String ThingName, RequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            dataAccess.deleteQuickAccess(ThingName);
            returnCallback = () -> callback.onSuccess();
            handler.post(returnCallback);
        }).start();
    }

    public void listQuickAccess(Context context, QuickAccessRequestHandler callback) {
        new Thread(() -> {
            List<QuickAccess> quickAccessList = dataAccess.getQuickAccessList();
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            if (quickAccessList.size() > 0)
                returnCallback = () -> callback.onSuccess(quickAccessList);
            else
                returnCallback = () -> callback.onError("No cabins marked for Quick Access as of now.");
            handler.post(returnCallback);
        }).start();
    }

    public void hasQuickAccess(Context context, String ThingName, CheckQuickAccessRequestHandler callback) {
        new Thread(() -> {
            List<QuickAccess> quickAccessList = dataAccess.getQuickAccessList();
            boolean hasQuickAccess = false;
            for (QuickAccess item : quickAccessList) {
                if (item.getThingName().compareToIgnoreCase(ThingName) == 0)
                    hasQuickAccess = true;
            }

            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;
            boolean finalHasQuickAccess = hasQuickAccess;
            returnCallback = () -> callback.onResult(finalHasQuickAccess);
            handler.post(returnCallback);
        }).start();
    }

    //Reports
    public void getUsageReport(Context context, List<String> States, List<String> Districts, List<String> Cities, List<String> Complexes,
                               Date fromDate, Date toDate, UsageProfileRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            List<UsageProfile> data = dataAccess.getProfileReport(States, Districts, Cities, Complexes, "" + fromDate.getTime(), "" + toDate.getTime());
            List<DisplayUsageProfile> displayData = new ArrayList<>();
            DisplayUsageProfile displayItem;
            for (UsageProfile item : data) {
//                item.setSTATE(complex.getStateCode());
//                item.setDISTRICT(complex.getDistrictName());
//                item.setCITY(complex.getCityName());
                displayItem = new DisplayUsageProfile(item, toDbDate(toDbTimestamp(item.getSERVER_TIMESTAMP())), toDbTime(toDbTimestamp(item.getSERVER_TIMESTAMP())));
                displayData.add(displayItem);
            }
            returnCallback = () -> callback.getData(displayData);
            handler.post(returnCallback);
        }).start();
    }

    public void getUsageReportComplexWise(Context context, List<sukriti.ngo.mis.dataModel.dynamo_db.Complex> Complexes,
                                          Date fromDate, Date toDate, String durationLabel, ComplexUsageReportRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            String _fromDate = toDbDateString(fromDate);
            String _toDate = toDbDateString(toDate);

            List<UsageReportData> usageReport = new ArrayList<>();
            if (Complexes.size() > 1)
                usageReport.add(getUsageReportSummary(context, Complexes, fromDate, toDate, durationLabel));

            for (sukriti.ngo.mis.dataModel.dynamo_db.Complex complex : Complexes) {

                //DATA
                List<UsageProfile> usageProfile = dataAccess.getProfileReport(complex.getName(), "" + fromDate.getTime(), "" + toDate.getTime());
                List<UsageProfile> usageProfileMwc = getUsageProfileSubset(usageProfile, Nomenclature.CABIN_TYPE_MWC);
                List<UsageProfile> usageProfileFwc = getUsageProfileSubset(usageProfile, Nomenclature.CABIN_TYPE_FWC);
                List<UsageProfile> usageProfilePwc = getUsageProfileSubset(usageProfile, Nomenclature.CABIN_TYPE_PWC);
                List<UsageProfile> usageProfileMur = getUsageProfileSubset(usageProfile, Nomenclature.CABIN_TYPE_MUR);
                ComplexWiseUsageData usageData = new ComplexWiseUsageData(fromDate, toDate, usageProfile, usageProfileMwc, usageProfileFwc, usageProfilePwc, usageProfileMur, durationLabel);

                //CHART-UsageSummaryStats
                int totalCount = dataAccess.countUsageForComplex(complex.getName(), _fromDate, _toDate);
                int mwcCount = dataAccess.countUsageForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate);
                int fwcCount = dataAccess.countUsageForComplex(complex.getName(), Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate);
                int pwcCount = dataAccess.countUsageForComplex(complex.getName(), Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate);
                int murCount = dataAccess.countUsageForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate);
                UsageSummaryStats usageSummary = new UsageSummaryStats(fromDate, toDate, totalCount, mwcCount, fwcCount, pwcCount, murCount, durationLabel);


                //CHART-UsageComparisonStats
                List<DailyUsageCount> totalDailyUsage = getCompletedDailyUsageCount(fromDate, toDate, dataAccess.countDailyUsageForComplex(complex.getName(), _fromDate, _toDate));
                List<DailyUsageCount> maleDailyUsage = getCompletedDailyUsageCount(fromDate, toDate, dataAccess.countDailyUsageForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
                List<DailyUsageCount> femaleDailyUsage = getCompletedDailyUsageCount(fromDate, toDate, dataAccess.countDailyUsageForComplex(complex.getName(), Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
                List<DailyUsageCount> pdDailyUsage = getCompletedDailyUsageCount(fromDate, toDate, dataAccess.countDailyUsageForComplex(complex.getName(), Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
                List<DailyUsageCount> murDailyUsage = getCompletedDailyUsageCount(fromDate, toDate, dataAccess.countDailyUsageForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));
                UsageComparisonStats usageComparison = new UsageComparisonStats(fromDate, toDate, totalDailyUsage, maleDailyUsage, femaleDailyUsage, pdDailyUsage, murDailyUsage, durationLabel);

                //CHART-DailyUsageTimeline
                totalDailyUsage = getDailyUsageTimeline(fromDate, toDate, dataAccess.countDailyUsageForComplex(complex.getName(), _fromDate, _toDate));
                maleDailyUsage = getDailyUsageTimeline(fromDate, toDate, dataAccess.countDailyUsageForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
                femaleDailyUsage = getDailyUsageTimeline(fromDate, toDate, dataAccess.countDailyUsageForComplex(complex.getName(), Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
                pdDailyUsage = getDailyUsageTimeline(fromDate, toDate, dataAccess.countDailyUsageForComplex(complex.getName(), Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
                murDailyUsage = getDailyUsageTimeline(fromDate, toDate, dataAccess.countDailyUsageForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));
                UsageComparisonStats usageTimeline = new UsageComparisonStats(fromDate, toDate, totalDailyUsage, maleDailyUsage, femaleDailyUsage, pdDailyUsage, murDailyUsage, durationLabel);

                //CHART-FeedbackSummary
                FeedbackSummary feedbackTotal = dataAccess.countTotalFeedbackForComplex(complex.getName(), _fromDate, _toDate);
                FeedbackSummary feedbackMwc = dataAccess.countTotalFeedbackForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate);
                FeedbackSummary feedbackFwc = dataAccess.countTotalFeedbackForComplex(complex.getName(), Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate);
                FeedbackSummary feedbackPwc = dataAccess.countTotalFeedbackForComplex(complex.getName(), Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate);
                FeedbackSummary feedbackMur = dataAccess.countTotalFeedbackForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate);
                FeedbackStatsData feedbackSummary = new FeedbackStatsData(fromDate, toDate, feedbackTotal, feedbackMwc, feedbackFwc, feedbackPwc, feedbackMur, durationLabel);

                //CHART-FeedbackComparison
                List<FeedbackWiseUserCount> feedbackCompareMwc = getCompletedFeedbackWiseUserCount(
                        dataAccess.countUsersForFeedbackForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
                List<FeedbackWiseUserCount> feedbackCompareFwc = getCompletedFeedbackWiseUserCount(
                        dataAccess.countUsersForFeedbackForComplex(complex.getName(), Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
                List<FeedbackWiseUserCount> feedbackComparePwc = getCompletedFeedbackWiseUserCount(
                        dataAccess.countUsersForFeedbackForComplex(complex.getName(), Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
                List<FeedbackWiseUserCount> feedbackCompareMur = getCompletedFeedbackWiseUserCount(
                        dataAccess.countUsersForFeedbackForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));
                FeedbackSummaryData feedbackComparison = new FeedbackSummaryData(fromDate, toDate, feedbackCompareMwc, feedbackCompareFwc, feedbackComparePwc, feedbackCompareMur, durationLabel);

                //CHART-FeedbackDistribution
                List<DailyFeedbackCount> _1starData = getCompletedDailyUserCountForFeedback(
                        fromDate, toDate, dataAccess.countDailyUserCountForFeedbackForComplex(complex.getName(), _fromDate, _toDate, 1), 1);
                List<DailyFeedbackCount> _2starData = getCompletedDailyUserCountForFeedback(
                        fromDate, toDate, dataAccess.countDailyUserCountForFeedbackForComplex(complex.getName(), _fromDate, _toDate, 2), 2);
                List<DailyFeedbackCount> _3starData = getCompletedDailyUserCountForFeedback(
                        fromDate, toDate, dataAccess.countDailyUserCountForFeedbackForComplex(complex.getName(), _fromDate, _toDate, 3), 3);
                List<DailyFeedbackCount> _4starData = getCompletedDailyUserCountForFeedback(
                        fromDate, toDate, dataAccess.countDailyUserCountForFeedbackForComplex(complex.getName(), _fromDate, _toDate, 4), 4);
                List<DailyFeedbackCount> _5starData = getCompletedDailyUserCountForFeedback(
                        fromDate, toDate, dataAccess.countDailyUserCountForFeedbackForComplex(complex.getName(), _fromDate, _toDate, 5), 5);
                FeedbackComparisonData feedbackDistribution = new FeedbackComparisonData(fromDate, toDate, _1starData, _2starData, _3starData, _4starData, _5starData, durationLabel);

                //CHART-FeedbackTimeline
                List<DailyAverageFeedback> averageDailyFeedback = getCompletedDailyAverageFeedback(fromDate, toDate,
                        dataAccess.countDailyFeedbackForComplex(complex.getName(), _fromDate, _toDate));
                List<DailyAverageFeedback> averageMwcDailyFeedback = getCompletedDailyAverageFeedback(fromDate, toDate,
                        dataAccess.countDailyFeedbackForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
                List<DailyAverageFeedback> averageFwcDailyFeedback = getCompletedDailyAverageFeedback(fromDate, toDate,
                        dataAccess.countDailyFeedbackForComplex(complex.getName(), Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
                List<DailyAverageFeedback> averagePwcDailyFeedback = getCompletedDailyAverageFeedback(fromDate, toDate,
                        dataAccess.countDailyFeedbackForComplex(complex.getName(), Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
                List<DailyAverageFeedback> averageMurDailyFeedback = getCompletedDailyAverageFeedback(fromDate, toDate,
                        dataAccess.countDailyFeedbackForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));
                FeedbackTimelineData feedbackTimeline = new FeedbackTimelineData(fromDate, toDate, averageDailyFeedback,
                        averageMwcDailyFeedback, averageFwcDailyFeedback, averagePwcDailyFeedback,
                        averageMurDailyFeedback, durationLabel);

                //CHART-CollectionSummary
                float totalCollection = 0;
                try {
                    totalCollection = dataAccess.countTotalCollectionForComplex(complex.getName(), _fromDate, _toDate);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                float mwcCollection = 0;
                try {
                    mwcCollection = dataAccess.countTotalCollectionForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                float fwcCollection = 0;
                try {
                    fwcCollection = dataAccess.countTotalCollectionForComplex(complex.getName(), Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                float pwcCollection = 0;
                try {
                    pwcCollection = dataAccess.countTotalCollectionForComplex(complex.getName(), Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                float murCollection = 0;
                try {
                    murCollection = dataAccess.countTotalCollectionForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                ChargeCollectionStats collectionSummary = new ChargeCollectionStats(fromDate, toDate, totalCollection,
                        mwcCollection, fwcCollection, pwcCollection, murCollection, durationLabel);

                //CHART-CollectionComparison
                List<DailyChargeCollection> totalCollectionLst = getCompletedDailyChargeCollection(
                        fromDate, toDate, dataAccess.countDailyCollectionForComplex(complex.getName(), _fromDate, _toDate));
                List<DailyChargeCollection> mwcCollectionLst = getCompletedDailyChargeCollection(
                        fromDate, toDate, dataAccess.countDailyCollectionForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
                List<DailyChargeCollection> fwcCollectionLst = getCompletedDailyChargeCollection(
                        fromDate, toDate, dataAccess.countDailyCollectionForComplex(complex.getName(), Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
                List<DailyChargeCollection> pwcCollectionLst = getCompletedDailyChargeCollection(
                        fromDate, toDate, dataAccess.countDailyCollectionForComplex(complex.getName(), Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
                List<DailyChargeCollection> murCollectionLst = getCompletedDailyChargeCollection(
                        fromDate, toDate, dataAccess.countDailyCollectionForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));

                DailyChargeCollectionData collectionComparison = new DailyChargeCollectionData(fromDate, toDate, totalCollectionLst,
                        mwcCollectionLst, fwcCollectionLst, pwcCollectionLst, murCollectionLst, durationLabel);

                //CHART-CollectionTimeline
                totalCollectionLst = getDailyChargeCollectionTimeline(
                        fromDate, toDate, dataAccess.countDailyCollectionForComplex(complex.getName(), _fromDate, _toDate));
                mwcCollectionLst = getDailyChargeCollectionTimeline(
                        fromDate, toDate, dataAccess.countDailyCollectionForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
                fwcCollectionLst = getDailyChargeCollectionTimeline(
                        fromDate, toDate, dataAccess.countDailyCollectionForComplex(complex.getName(), Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
                pwcCollectionLst = getDailyChargeCollectionTimeline(
                        fromDate, toDate, dataAccess.countDailyCollectionForComplex(complex.getName(), Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
                murCollectionLst = getDailyChargeCollectionTimeline(
                        fromDate, toDate, dataAccess.countDailyCollectionForComplex(complex.getName(), Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));

                DailyChargeCollectionData collectionTimeline = new DailyChargeCollectionData(fromDate, toDate, totalCollectionLst,
                        mwcCollectionLst, fwcCollectionLst, pwcCollectionLst, murCollectionLst, durationLabel);

                DailyUpiCollectionData upiCollectionData = new DailyUpiCollectionData(fromDate, toDate, totalCollectionLst,
                        mwcCollectionLst, fwcCollectionLst, pwcCollectionLst, murCollectionLst, durationLabel);

                DailyBwtCollectionData bwtCollectionData = new DailyBwtCollectionData(fromDate, toDate, totalCollectionLst,
                        mwcCollectionLst, fwcCollectionLst, pwcCollectionLst, murCollectionLst, durationLabel);

                //Tickets

//               Aug Qa :Rahul karn
                List<DailyTicketEventSummary> totalResolutionLst = countDailyActiveTickets(complex.getName(), fromDate, toDate, durationLabel);
                List<DailyTicketEventSummary> mwcResolutionLst = countDailyActiveTickets(complex.getName(), fromDate, toDate, durationLabel);
                List<DailyTicketEventSummary> fwcResolutionLst = countDailyActiveTickets(complex.getName(), fromDate, toDate, durationLabel);
                List<DailyTicketEventSummary> pwcResolutionLst = countDailyActiveTickets(complex.getName(), fromDate, toDate, durationLabel);
                List<DailyTicketEventSummary> murResolutionLst = countDailyActiveTickets(complex.getName(), fromDate, toDate, durationLabel);

//                List<DailyTicketEventSummary> totalResolutionLst = countDailyActiveTickets(fromDate, toDate, durationLabel);
//                List<DailyTicketEventSummary> mwcResolutionLst = countDailyActiveTickets(fromDate, toDate, durationLabel);
//                List<DailyTicketEventSummary> fwcResolutionLst = countDailyActiveTickets(fromDate, toDate, durationLabel);
//                List<DailyTicketEventSummary> pwcResolutionLst = countDailyActiveTickets(fromDate, toDate, durationLabel);
//                List<DailyTicketEventSummary> murResolutionLst = countDailyActiveTickets(fromDate, toDate, durationLabel);
//                **********************

                TicketResolutionTimelineData ticketResolutionTimeline = new TicketResolutionTimelineData(fromDate, toDate, totalResolutionLst,
                        mwcResolutionLst, fwcResolutionLst, pwcResolutionLst, murResolutionLst, durationLabel);

                //Merge all into one object
                UsageReportData usageReportItem = new UsageReportData(complex, usageData, usageSummary, usageComparison, usageTimeline,
                        feedbackSummary, feedbackComparison, feedbackDistribution, feedbackTimeline,
                        collectionSummary, collectionComparison, collectionTimeline, collectionSummary,
                        upiCollectionData, upiCollectionData, collectionSummary, bwtCollectionData, bwtCollectionData, ticketResolutionTimeline);
                usageReport.add(usageReportItem);
            }

            returnCallback = () -> callback.getData(usageReport);
            handler.post(returnCallback);
        }).start();
    }

    private UsageReportData getUsageReportSummary(Context context, List<sukriti.ngo.mis.dataModel.dynamo_db.Complex> complexes, Date fromDate, Date toDate, String durationLabel) {
        List<String> complexesList = getComplexNamesList(complexes);
        String _fromDate = toDbDateString(fromDate);
        String _toDate = toDbDateString(toDate);

        //DATA
        List<UsageProfile> usageProfile = dataAccess.getProfileReport(complexesList, "" + fromDate.getTime(), "" + toDate.getTime());
        List<UsageProfile> usageProfileMwc = getUsageProfileSubset(usageProfile, Nomenclature.CABIN_TYPE_MWC);
        List<UsageProfile> usageProfileFwc = getUsageProfileSubset(usageProfile, Nomenclature.CABIN_TYPE_FWC);
        List<UsageProfile> usageProfilePwc = getUsageProfileSubset(usageProfile, Nomenclature.CABIN_TYPE_PWC);
        List<UsageProfile> usageProfileMur = getUsageProfileSubset(usageProfile, Nomenclature.CABIN_TYPE_MUR);
        ComplexWiseUsageData usageData = new ComplexWiseUsageData(fromDate, toDate, usageProfile, usageProfileMwc, usageProfileFwc, usageProfilePwc, usageProfileMur, durationLabel);

        //CHART-UsageSummaryStats
        int totalCount = dataAccess.countUsageForComplex(complexesList, _fromDate, _toDate);
        int mwcCount = dataAccess.countUsageForComplex(complexesList, Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate);
        int fwcCount = dataAccess.countUsageForComplex(complexesList, Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate);
        int pwcCount = dataAccess.countUsageForComplex(complexesList, Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate);
        int murCount = dataAccess.countUsageForComplex(complexesList, Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate);
        UsageSummaryStats usageSummary = new UsageSummaryStats(fromDate, toDate, totalCount, mwcCount, fwcCount, pwcCount, murCount, durationLabel);


        //CHART-UsageComparisonStats
        List<DailyUsageCount> totalDailyUsage = getCompletedDailyUsageCount(fromDate, toDate, dataAccess.countDailyUsageForComplex(complexesList, _fromDate, _toDate));
        List<DailyUsageCount> maleDailyUsage = getCompletedDailyUsageCount(fromDate, toDate, dataAccess.countDailyUsageForComplex(complexesList, Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
        List<DailyUsageCount> femaleDailyUsage = getCompletedDailyUsageCount(fromDate, toDate, dataAccess.countDailyUsageForComplex(complexesList, Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
        List<DailyUsageCount> pdDailyUsage = getCompletedDailyUsageCount(fromDate, toDate, dataAccess.countDailyUsageForComplex(complexesList, Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
        List<DailyUsageCount> murDailyUsage = getCompletedDailyUsageCount(fromDate, toDate, dataAccess.countDailyUsageForComplex(complexesList, Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));
        UsageComparisonStats usageComparison = new UsageComparisonStats(fromDate, toDate, totalDailyUsage, maleDailyUsage, femaleDailyUsage, pdDailyUsage, murDailyUsage, durationLabel);

        //CHART-DailyUsageTimeline
        totalDailyUsage = getDailyUsageTimeline(fromDate, toDate, dataAccess.countDailyUsageForComplex(complexesList, _fromDate, _toDate));
        maleDailyUsage = getDailyUsageTimeline(fromDate, toDate, dataAccess.countDailyUsageForComplex(complexesList, Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
        femaleDailyUsage = getDailyUsageTimeline(fromDate, toDate, dataAccess.countDailyUsageForComplex(complexesList, Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
        pdDailyUsage = getDailyUsageTimeline(fromDate, toDate, dataAccess.countDailyUsageForComplex(complexesList, Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
        murDailyUsage = getDailyUsageTimeline(fromDate, toDate, dataAccess.countDailyUsageForComplex(complexesList, Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));
        UsageComparisonStats usageTimeline = new UsageComparisonStats(fromDate, toDate, totalDailyUsage, maleDailyUsage, femaleDailyUsage, pdDailyUsage, murDailyUsage, durationLabel);

        //CHART-FeedbackSummary
        FeedbackSummary feedbackTotal = dataAccess.countTotalFeedbackForComplex(complexesList, _fromDate, _toDate);
        FeedbackSummary feedbackMwc = dataAccess.countTotalFeedbackForComplex(complexesList, Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate);
        FeedbackSummary feedbackFwc = dataAccess.countTotalFeedbackForComplex(complexesList, Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate);
        FeedbackSummary feedbackPwc = dataAccess.countTotalFeedbackForComplex(complexesList, Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate);
        FeedbackSummary feedbackMur = dataAccess.countTotalFeedbackForComplex(complexesList, Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate);
        FeedbackStatsData feedbackSummary = new FeedbackStatsData(fromDate, toDate, feedbackTotal, feedbackMwc, feedbackFwc, feedbackPwc, feedbackMur, durationLabel);

        //CHART-FeedbackComparison
        List<FeedbackWiseUserCount> feedbackCompareMwc = getCompletedFeedbackWiseUserCount(
                dataAccess.countUsersForFeedbackForComplex(complexesList, Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
        List<FeedbackWiseUserCount> feedbackCompareFwc = getCompletedFeedbackWiseUserCount(
                dataAccess.countUsersForFeedbackForComplex(complexesList, Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
        List<FeedbackWiseUserCount> feedbackComparePwc = getCompletedFeedbackWiseUserCount(
                dataAccess.countUsersForFeedbackForComplex(complexesList, Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
        List<FeedbackWiseUserCount> feedbackCompareMur = getCompletedFeedbackWiseUserCount(
                dataAccess.countUsersForFeedbackForComplex(complexesList, Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));
        FeedbackSummaryData feedbackComparison = new FeedbackSummaryData(fromDate, toDate, feedbackCompareMwc, feedbackCompareFwc, feedbackComparePwc, feedbackCompareMur, durationLabel);

        //CHART-FeedbackDistribution
        List<DailyFeedbackCount> _1starData = getCompletedDailyUserCountForFeedback(
                fromDate, toDate, dataAccess.countDailyUserCountForFeedbackForComplex(complexesList, _fromDate, _toDate, 1), 1);
        List<DailyFeedbackCount> _2starData = getCompletedDailyUserCountForFeedback(
                fromDate, toDate, dataAccess.countDailyUserCountForFeedbackForComplex(complexesList, _fromDate, _toDate, 2), 2);
        List<DailyFeedbackCount> _3starData = getCompletedDailyUserCountForFeedback(
                fromDate, toDate, dataAccess.countDailyUserCountForFeedbackForComplex(complexesList, _fromDate, _toDate, 3), 3);
        List<DailyFeedbackCount> _4starData = getCompletedDailyUserCountForFeedback(
                fromDate, toDate, dataAccess.countDailyUserCountForFeedbackForComplex(complexesList, _fromDate, _toDate, 4), 4);
        List<DailyFeedbackCount> _5starData = getCompletedDailyUserCountForFeedback(
                fromDate, toDate, dataAccess.countDailyUserCountForFeedbackForComplex(complexesList, _fromDate, _toDate, 5), 5);
        FeedbackComparisonData feedbackDistribution = new FeedbackComparisonData(fromDate, toDate, _1starData, _2starData, _3starData, _4starData, _5starData, durationLabel);

        //CHART-FeedbackTimeline
        List<DailyAverageFeedback> averageDailyFeedback = getCompletedDailyAverageFeedback(fromDate, toDate,
                dataAccess.countDailyFeedbackForComplex(complexesList, _fromDate, _toDate));
        List<DailyAverageFeedback> averageMwcDailyFeedback = getCompletedDailyAverageFeedback(fromDate, toDate,
                dataAccess.countDailyFeedbackForComplex(complexesList, Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
        List<DailyAverageFeedback> averageFwcDailyFeedback = getCompletedDailyAverageFeedback(fromDate, toDate,
                dataAccess.countDailyFeedbackForComplex(complexesList, Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
        List<DailyAverageFeedback> averagePwcDailyFeedback = getCompletedDailyAverageFeedback(fromDate, toDate,
                dataAccess.countDailyFeedbackForComplex(complexesList, Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
        List<DailyAverageFeedback> averageMurDailyFeedback = getCompletedDailyAverageFeedback(fromDate, toDate,
                dataAccess.countDailyFeedbackForComplex(complexesList, Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));
        FeedbackTimelineData feedbackTimeline = new FeedbackTimelineData(fromDate, toDate, averageDailyFeedback,
                averageMwcDailyFeedback, averageFwcDailyFeedback, averagePwcDailyFeedback,
                averageMurDailyFeedback, durationLabel);

        //CHART-CollectionSummary
        float totalCollection = 0;
        try {
            totalCollection = dataAccess.countTotalCollectionForComplex(complexesList, _fromDate, _toDate);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        float mwcCollection = 0;
        try {
            mwcCollection = dataAccess.countTotalCollectionForComplex(complexesList, Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        float fwcCollection = 0;
        try {
            fwcCollection = dataAccess.countTotalCollectionForComplex(complexesList, Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        float pwcCollection = 0;
        try {
            pwcCollection = dataAccess.countTotalCollectionForComplex(complexesList, Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        float murCollection = 0;
        try {
            murCollection = dataAccess.countTotalCollectionForComplex(complexesList, Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        ChargeCollectionStats collectionSummary = new ChargeCollectionStats(fromDate, toDate, totalCollection,
                mwcCollection, fwcCollection, pwcCollection, murCollection, durationLabel);

        //CHART-CollectionComparison
        List<DailyChargeCollection> totalCollectionLst = getCompletedDailyChargeCollection(
                fromDate, toDate, dataAccess.countDailyCollectionForComplex(complexesList, _fromDate, _toDate));
        List<DailyChargeCollection> mwcCollectionLst = getCompletedDailyChargeCollection(
                fromDate, toDate, dataAccess.countDailyCollectionForComplex(complexesList, Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
        List<DailyChargeCollection> fwcCollectionLst = getCompletedDailyChargeCollection(
                fromDate, toDate, dataAccess.countDailyCollectionForComplex(complexesList, Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
        List<DailyChargeCollection> pwcCollectionLst = getCompletedDailyChargeCollection(
                fromDate, toDate, dataAccess.countDailyCollectionForComplex(complexesList, Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
        List<DailyChargeCollection> murCollectionLst = getCompletedDailyChargeCollection(
                fromDate, toDate, dataAccess.countDailyCollectionForComplex(complexesList, Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));

        DailyChargeCollectionData collectionComparison = new DailyChargeCollectionData(fromDate, toDate, totalCollectionLst,
                mwcCollectionLst, fwcCollectionLst, pwcCollectionLst, murCollectionLst, durationLabel);

        DailyUpiCollectionData upiCollectionData = new DailyUpiCollectionData(fromDate, toDate, totalCollectionLst,
                mwcCollectionLst, fwcCollectionLst, pwcCollectionLst, murCollectionLst, durationLabel);

        DailyBwtCollectionData bwtCollectionData = new DailyBwtCollectionData(fromDate, toDate, totalCollectionLst,
                mwcCollectionLst, fwcCollectionLst, pwcCollectionLst, murCollectionLst, durationLabel);

        //CHART-CollectionTimeline
        totalCollectionLst = getDailyChargeCollectionTimeline(
                fromDate, toDate, dataAccess.countDailyCollectionForComplex(complexesList, _fromDate, _toDate));
        mwcCollectionLst = getDailyChargeCollectionTimeline(
                fromDate, toDate, dataAccess.countDailyCollectionForComplex(complexesList, Nomenclature.CABIN_TYPE_MWC, _fromDate, _toDate));
        fwcCollectionLst = getDailyChargeCollectionTimeline(
                fromDate, toDate, dataAccess.countDailyCollectionForComplex(complexesList, Nomenclature.CABIN_TYPE_FWC, _fromDate, _toDate));
        pwcCollectionLst = getDailyChargeCollectionTimeline(
                fromDate, toDate, dataAccess.countDailyCollectionForComplex(complexesList, Nomenclature.CABIN_TYPE_PWC, _fromDate, _toDate));
        murCollectionLst = getDailyChargeCollectionTimeline(
                fromDate, toDate, dataAccess.countDailyCollectionForComplex(complexesList, Nomenclature.CABIN_TYPE_MUR, _fromDate, _toDate));

        DailyChargeCollectionData collectionTimeline = new DailyChargeCollectionData(fromDate, toDate, totalCollectionLst,
                mwcCollectionLst, fwcCollectionLst, pwcCollectionLst, murCollectionLst, durationLabel);

        sukriti.ngo.mis.dataModel.dynamo_db.Complex complex = new sukriti.ngo.mis.dataModel.dynamo_db.Complex("Summary", "Information summarized for the selected " + complexesList.size() + " complexes.");

        //Tickets
        List<DailyTicketEventSummary> totalResolutionLst = countDailyActiveTickets(fromDate, toDate, durationLabel);
        List<DailyTicketEventSummary> mwcResolutionLst = countDailyActiveTickets(fromDate, toDate, durationLabel);
        List<DailyTicketEventSummary> fwcResolutionLst = countDailyActiveTickets(fromDate, toDate, durationLabel);
        List<DailyTicketEventSummary> pwcResolutionLst = countDailyActiveTickets(fromDate, toDate, durationLabel);
        List<DailyTicketEventSummary> murResolutionLst = countDailyActiveTickets(fromDate, toDate, durationLabel);
        TicketResolutionTimelineData ticketResolutionTimeline = new TicketResolutionTimelineData(fromDate, toDate, totalResolutionLst,
                mwcResolutionLst, fwcResolutionLst, pwcResolutionLst, murResolutionLst, durationLabel);

        //Merge all into one object
        UsageReportData usageReportItem = new UsageReportData(complex, usageData, usageSummary, usageComparison, usageTimeline,
                feedbackSummary, feedbackComparison, feedbackDistribution, feedbackTimeline,
                collectionSummary, collectionComparison, collectionTimeline,
                collectionSummary, upiCollectionData, upiCollectionData, collectionSummary, bwtCollectionData,
                bwtCollectionData, ticketResolutionTimeline);


        return usageReportItem;
    }


    public void getResetReport(Context context, List<String> States, List<String> Districts, List<String> Cities, List<String> Complexes,
                               Date fromDate, Date toDate, ResetProfileRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            List<ResetProfile> data = dataAccess.getResetReport(States, Districts, Cities, Complexes, "" + fromDate.getTime(), "" + toDate.getTime());
            //List<ResetProfile> data = dataAccess.getProfileReset();
            List<DisplayResetProfile> displayData = new ArrayList<>();
            DisplayResetProfile displayItem;
            for (ResetProfile item : data) {
                displayItem = new DisplayResetProfile(item, toDbDate(toDbTimestamp(item.getDEVICE_TIMESTAMP())), toDbTime(toDbTimestamp(item.getDEVICE_TIMESTAMP())));
                displayData.add(displayItem);
            }
            returnCallback = () -> callback.getData(displayData);
            handler.post(returnCallback);
        }).start();
    }

    public void getBwtReport(Context context, List<String> States, List<String> Districts, List<String> Cities, List<String> Complexes,
                             Date fromDate, Date toDate, BwtProfileRequestHandler callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            List<BwtProfile> data = dataAccess.getBwtReport(States, Districts, Cities, Complexes, "" + fromDate.getTime(), "" + toDate.getTime());
            //List<BwtProfile> data = dataAccess.getProfileBwt();
            List<DisplayBwtProfile> displayData = new ArrayList<>();
            DisplayBwtProfile displayItem;
            for (BwtProfile item : data) {
                displayItem = new DisplayBwtProfile(item, toDbDate(toDbTimestamp(item.getSERVER_TIMESTAMP())), toDbTime(toDbTimestamp(item.getSERVER_TIMESTAMP())));
                displayData.add(displayItem);
            }
            returnCallback = () -> callback.getData(displayData);
            handler.post(returnCallback);
        }).start();
    }

    //TICKET-PROGRESS
    public void countDailyActiveTickets(Context context, Date fromDate, Date toDate, String durationLabel, RepositoryCallback callback) {
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            String _fromDate = toDbDateString(fromDate);
            String _toDate = toDbDateString(toDate);

            Log.i("_completedDailyTicket", " " + _fromDate + "-" + _toDate);

            List<DailyTicketEventCount> raisedTicketEvents = getCompletedDailyDailyTicketEventCount(
                    fromDate, toDate, dataAccess.getDailyTicketEventCount(Nomenclature.TICKET_EVENT_RAISED, _fromDate, _toDate),
                    Nomenclature.TICKET_EVENT_RAISED);
            List<DailyTicketEventCount> assignedTicketEvents = getCompletedDailyDailyTicketEventCount(
                    fromDate, toDate, dataAccess.getDailyTicketEventCount(Nomenclature.TICKET_EVENT_ASSIGNED, _fromDate, _toDate),
                    Nomenclature.TICKET_EVENT_ASSIGNED);
            List<DailyTicketEventCount> resolvedTicketEvents = getCompletedDailyDailyTicketEventCount(
                    fromDate, toDate, dataAccess.getDailyTicketEventCount(Nomenclature.TICKET_EVENT_RESOLVED, _fromDate, _toDate),
                    Nomenclature.TICKET_EVENT_RESOLVED);
            List<DailyTicketEventCount> reOpenTicketEvents = getCompletedDailyDailyTicketEventCount(
                    fromDate, toDate, dataAccess.getDailyTicketEventCount(Nomenclature.TICKET_EVENT_REOPENED, _fromDate, _toDate),
                    Nomenclature.TICKET_EVENT_REOPENED);
            List<DailyTicketEventCount> closeTicketEvents = getCompletedDailyDailyTicketEventCount(
                    fromDate, toDate, dataAccess.getDailyTicketEventCount(Nomenclature.TICKET_EVENT_CLOSED, _fromDate, _toDate),
                    Nomenclature.TICKET_EVENT_CLOSED);
            List<DailyTicketEventSummary> ticketEventSummary = getDailyTicketEventSummary(raisedTicketEvents,
                    assignedTicketEvents, resolvedTicketEvents, reOpenTicketEvents, closeTicketEvents);

            returnCallback = () -> callback.onComplete(new _Result.Success(ticketEventSummary));
            handler.post(returnCallback);
        }).start();
    }

    public List<DailyTicketEventSummary> countDailyActiveTickets(Date fromDate, Date toDate, String durationLabel) {
        String _fromDate = toDbDateString(fromDate);
        String _toDate = toDbDateString(toDate);

        Log.i("_completedDailyTicket", " " + _fromDate + "-" + _toDate);

        List<DailyTicketEventCount> raisedTicketEvents = getCompletedDailyDailyTicketEventCount(
                fromDate, toDate, dataAccess.getDailyTicketEventCount(Nomenclature.TICKET_EVENT_RAISED, _fromDate, _toDate),
                Nomenclature.TICKET_EVENT_RAISED);
        List<DailyTicketEventCount> assignedTicketEvents = getCompletedDailyDailyTicketEventCount(
                fromDate, toDate, dataAccess.getDailyTicketEventCount(Nomenclature.TICKET_EVENT_RAISED, _fromDate, _toDate),
                Nomenclature.TICKET_EVENT_ASSIGNED);
        List<DailyTicketEventCount> resolvedTicketEvents = getCompletedDailyDailyTicketEventCount(
                fromDate, toDate, dataAccess.getDailyTicketEventCount(Nomenclature.TICKET_EVENT_ASSIGNED, _fromDate, _toDate),
                Nomenclature.TICKET_EVENT_ASSIGNED);
        List<DailyTicketEventCount> reOpenTicketEvents = getCompletedDailyDailyTicketEventCount(
                fromDate, toDate, dataAccess.getDailyTicketEventCount(Nomenclature.TICKET_EVENT_ASSIGNED, _fromDate, _toDate),
                Nomenclature.TICKET_EVENT_ASSIGNED);
        List<DailyTicketEventCount> closeTicketEvents = getCompletedDailyDailyTicketEventCount(
                fromDate, toDate, dataAccess.getDailyTicketEventCount(Nomenclature.TICKET_EVENT_ASSIGNED, _fromDate, _toDate),
                Nomenclature.TICKET_EVENT_ASSIGNED);
        List<DailyTicketEventSummary> ticketEventSummary = getDailyTicketEventSummary(raisedTicketEvents,
                assignedTicketEvents, resolvedTicketEvents, reOpenTicketEvents, closeTicketEvents);

        return ticketEventSummary;
    }


    //    Aug Qa :rahul Karn
//    public List<DailyTicketEventSummary> countDailyActiveTickets(String cabinType, Date fromDate, Date toDate, String durationLabel) {
    public List<DailyTicketEventSummary> countDailyActiveTickets(String complex, Date fromDate, Date toDate, String durationLabel) {
//        ************
        String _fromDate = toDbDateString(fromDate);
        String _toDate = toDbDateString(toDate);

        Log.i("_completedDailyTicket", " " + _fromDate + "-" + _toDate);

        List<String> activeTicketID = dataAccess.getAllActiveTicketsByComplex(complex);

        List<DailyTicketEventCount> raisedTicketEvents = getCompletedDailyDailyTicketEventCount(
                fromDate, toDate, dataAccess.getDailyTicketEventCount(Nomenclature.TICKET_EVENT_RAISED, _fromDate, _toDate, activeTicketID),
                Nomenclature.TICKET_EVENT_RAISED);
        List<DailyTicketEventCount> assignedTicketEvents = getCompletedDailyDailyTicketEventCount(
                fromDate, toDate, dataAccess.getDailyTicketEventCount(Nomenclature.TICKET_EVENT_RAISED, _fromDate, _toDate, activeTicketID),
                Nomenclature.TICKET_EVENT_ASSIGNED);
        List<DailyTicketEventCount> resolvedTicketEvents = getCompletedDailyDailyTicketEventCount(
                fromDate, toDate, dataAccess.getDailyTicketEventCount(Nomenclature.TICKET_EVENT_ASSIGNED, _fromDate, _toDate, activeTicketID),
                Nomenclature.TICKET_EVENT_ASSIGNED);
        List<DailyTicketEventCount> reOpenTicketEvents = getCompletedDailyDailyTicketEventCount(
                fromDate, toDate, dataAccess.getDailyTicketEventCount(Nomenclature.TICKET_EVENT_ASSIGNED, _fromDate, _toDate, activeTicketID),
                Nomenclature.TICKET_EVENT_ASSIGNED);
        List<DailyTicketEventCount> closeTicketEvents = getCompletedDailyDailyTicketEventCount(
                fromDate, toDate, dataAccess.getDailyTicketEventCount(Nomenclature.TICKET_EVENT_ASSIGNED, _fromDate, _toDate, activeTicketID),
                Nomenclature.TICKET_EVENT_ASSIGNED);
        List<DailyTicketEventSummary> ticketEventSummary = getDailyTicketEventSummary(raisedTicketEvents,
                assignedTicketEvents, resolvedTicketEvents, reOpenTicketEvents, closeTicketEvents);

        return ticketEventSummary;
    }

    //TICKET-LIST
    public void listAllTickets(Handler handler, RepositoryCallback callback) {
        new Thread(() -> {
            List<Ticket> allTickets = dataAccess.getAllTickets();
            Runnable returnCallback;
            _Result<List<Ticket>> result = new _Result.Success<>(allTickets);
            returnCallback = () -> callback.onComplete(result);
            handler.post(returnCallback);
        }).start();
    }

    public void listAllTicketsWithStatus(String status, Handler handler, RepositoryCallback callback) {
        new Thread(() -> {
            List<Ticket> allTickets = dataAccess.getAllTicketsWithStatus(status);

            Runnable returnCallback;
            _Result<List<Ticket>> result = new _Result.Success<>(allTickets);
            returnCallback = () -> callback.onComplete(result);
            handler.post(returnCallback);
        }).start();
    }

    public void listAllActiveTickets(Handler handler, RepositoryCallback callback, String ticketStatus) {
        new Thread(() -> {
            List<Ticket> allTickets;
            if (ticketStatus.equals(SELECTED_ALL))
                allTickets = dataAccess.getAllActiveTickets();
            else
                allTickets = dataAccess.getAllActiveTickets(ticketStatus);
            Runnable returnCallback;
            _Result<List<Ticket>> result = new _Result.Success<>(allTickets);
            returnCallback = () -> callback.onComplete(result);
            handler.post(returnCallback);
        }).start();
    }

    public void listUnQueuedTickets(Handler handler, RepositoryCallback callback) {
        new Thread(() -> {
            List<Ticket> allTickets = dataAccess.getUnQueuedTickets();
            Runnable returnCallback;
            _Result<List<Ticket>> result = new _Result.Success<>(allTickets);
            returnCallback = () -> callback.onComplete(result);
            handler.post(returnCallback);
        }).start();
    }

    public void listQueuedTickets(Handler handler, RepositoryCallback callback) {
        new Thread(() -> {
            List<Ticket> allTickets = dataAccess.getQueuedTickets();
            Log.i("_misTest", "listQueuedTickets: " + allTickets.size());
            Runnable returnCallback;
            _Result<List<Ticket>> result = new _Result.Success<>(allTickets);
            returnCallback = () -> callback.onComplete(result);
            handler.post(returnCallback);
        }).start();
    }

    public void listRaisedTickets(Handler handler, RepositoryCallback callback, String ticketStatus) {
        new Thread(() -> {
            String userName = sharedPrefsClient.getUserDetails().getUser().getUserName();
            List<Ticket> allTickets;
            if (ticketStatus.equals(SELECTED_ALL))
                allTickets = dataAccess.getRaisedAndNotClosedTickets(userName);
            else
                allTickets = dataAccess.getRaisedAndNotClosedTickets(userName, ticketStatus);

            Runnable returnCallback;
            _Result<List<Ticket>> result = new _Result.Success<>(allTickets);
            returnCallback = () -> callback.onComplete(result);
            handler.post(returnCallback);
        }).start();
    }

    public void listClosedTickets(Handler handler, RepositoryCallback callback) {
        new Thread(() -> {
            List<Ticket> allTickets = dataAccess.getClosedTickets();
            Runnable returnCallback;
            _Result<List<Ticket>> result = new _Result.Success<>(allTickets);
            returnCallback = () -> callback.onComplete(result);
            handler.post(returnCallback);
        }).start();
    }

    public void listAssignedTickets(Handler handler, RepositoryCallback callback, String ticketStatus) {
        new Thread(() -> {
            String userName = sharedPrefsClient.getUserDetails().getUser().getUserName();
            List<Ticket> allTickets;
            if (ticketStatus.equals(SELECTED_ALL))
                allTickets = dataAccess.getAssignedTickets(userName);
            else
                allTickets = dataAccess.getAssignedTickets(userName, ticketStatus);
            Runnable returnCallback;
            _Result<List<Ticket>> result = new _Result.Success<>(allTickets);
            returnCallback = () -> callback.onComplete(result);
            handler.post(returnCallback);
        }).start();
    }

    public void listTeamAssignedTickets(Handler handler, RepositoryCallback callback, String ticketStatus) {
        new Thread(() -> {
            String userName = sharedPrefsClient.getUserDetails().getUser().getUserName();
            List<Ticket> allTickets;
            if (ticketStatus.equals(SELECTED_ALL))
                allTickets = dataAccess.getTeamAssignedTickets(userName);
            else
                allTickets = dataAccess.getTeamAssignedTickets(userName, ticketStatus);
            Runnable returnCallback;
            _Result<List<Ticket>> result = new _Result.Success<>(allTickets);
            returnCallback = () -> callback.onComplete(result);
            handler.post(returnCallback);
        }).start();
    }


}