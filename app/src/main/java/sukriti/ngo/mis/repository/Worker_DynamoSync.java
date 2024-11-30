package sukriti.ngo.mis.repository;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.ForegroundInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.ui.dashboard.repository.lambda.aggregateData.CabinDataLambdaRequest;
import sukriti.ngo.mis.ui.dashboard.repository.lambda.aggregateData.CabinDataLambdaResult;
import sukriti.ngo.mis.ui.dashboard.repository.lambda.aggregateData.CabinDataRequestHandler;
import sukriti.ngo.mis.ui.login.data.UserProfile;
import sukriti.ngo.mis.utils.LambdaClient;
import sukriti.ngo.mis.utils.SharedPrefsClient;

import static java.lang.Thread.sleep;


public class Worker_DynamoSync extends Worker {
    private final String TAG = "Worker_DynamoSync";
    private final int WORKER_LIFE_SPAN = 5;
    private Timer LifeSpanTimer;
    private int Time = 0;
    private SharedPrefsClient sharedPrefsClient;
    private NotificationCompat.Builder builder;
    private int LIFESPAN_STATUS;
    private final int LIFESPAN_FLAG_COUNTING = 1;
    private final int LIFESPAN_FLAG_END = -1;
    private Context context;

    public Worker_DynamoSync(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        sharedPrefsClient = new SharedPrefsClient(context);
    }

    @Override
    public Result doWork() {
        //setProgressAsync(new Data.Builder().putInt("PROGRESS", 0).build());
        //List<Uri> mTriggeredContentUris = getTriggeredContentUris();
        //String progress = "Publishing to AWS...";
        //setForegroundAsync(createForegroundInfo(progress));
//        String progress = "Connected to AWS";
//        setForegroundAsync(createForegroundInfo(progress));

        getCabinStats();
        startLifeSpanTimer();

        return Result.success();
    }


    @Override
    public void onStopped() {

        super.onStopped();
    }

    @NonNull
    private ForegroundInfo createForegroundInfo(@NonNull String progress) {
        Context context = getApplicationContext();
        // This PendingIntent can be used to cancel the worker
        PendingIntent intent = WorkManager.getInstance(context)
                .createCancelPendingIntent(getId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

        builder = new NotificationCompat.Builder(context, "02")
                .setContentTitle("Data refresh service")
                .setTicker(progress)
                .setSmallIcon(R.drawable.eco_status)
                .setOngoing(true);

        Notification notification = builder.build();

        return new ForegroundInfo(2, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        CharSequence name = "Data refresh action for MIS app";
        String description = "";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("02", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void getCabinStats() {

        CabinDataLambdaRequest request = new CabinDataLambdaRequest();
        request.setTimeStamp(new Gson().toJson(sharedPrefsClient.getDbSyncStatus().getLastTimestamp()));
        request.setClientCode(sharedPrefsClient.getUserDetails().getOrganisation().getClient());
        request.setComplexes(sharedPrefsClient.getAccessibleComplexList());
        request.setUserName(sharedPrefsClient.getUserDetails().getUser().getUserName());
        request.setRequestTimeStamp(""+ Calendar.getInstance().getTimeInMillis());

        CabinDataRequestHandler lambdaRequestHandler = new CabinDataRequestHandler() {
            @Override
            public void onSuccess(CabinDataLambdaResult result) {
                Log.i(TAG, "Result: Success");
                stopWorker();
            }

            @Override
            public void onError(String ErrorMsg) {
                Log.i(TAG, "Result: Error");
                stopWorker();
            }
        };
        LambdaClient lambdaClient = new LambdaClient(context);

        if (UserProfile.Companion.isClientSpecificRole(sharedPrefsClient.getUserDetails().getRole())) {
            Log.i("__ExecuteLambda", "ExecuteAggregateMisUserDataLambda()");
            lambdaClient.ExecuteAggregateMisUserDataLambda(request, lambdaRequestHandler);
        } else {
            Log.i("__ExecuteLambda", "ExecuteAggregateVendorUserDataLambda()");
            lambdaClient.ExecuteAggregateVendorUserDataLambda(request, lambdaRequestHandler);
        }
    }

    private void startLifeSpanTimer() {
        LIFESPAN_STATUS = LIFESPAN_FLAG_COUNTING;
        Time = 0;
        if (LifeSpanTimer != null) {
            LifeSpanTimer.cancel();
        }
        LifeSpanTimer = new Timer();
        LifeSpanTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Time++;
//                if (Time >= WORKER_LIFE_SPAN) {
//                    stopWorker();
//                }
            }
        }, 0, 1000);

        int waitCount = 0;
        while (LIFESPAN_STATUS == LIFESPAN_FLAG_COUNTING) {
            try {
                waitCount++;
                Log.i(TAG, "Waiting in LifeSpan: " + waitCount);
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopWorker() {
        if (LifeSpanTimer != null)
            LifeSpanTimer.cancel();
        LIFESPAN_STATUS = LIFESPAN_FLAG_END;
    }

}
