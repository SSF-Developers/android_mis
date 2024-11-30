package sukriti.ngo.mis.ui.reports.lambda;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sukriti.ngo.mis.dataModel._Result;
import sukriti.ngo.mis.interfaces.RepositoryCallback;
import sukriti.ngo.mis.ui.reports.data.Root;
import sukriti.ngo.mis.ui.reports.lambda.report_fetchDateWaiseUsageData.ReportLambdaHandler;
import sukriti.ngo.mis.ui.reports.lambda.report_fetchDateWaiseUsageData.ReportLambdaRequest;
import sukriti.ngo.mis.ui.reports.lambda.report_fetchDateWaiseUsageData.ReportLambdaResult;
import sukriti.ngo.mis.utils.AuthenticationClient;

import static sukriti.ngo.mis.AWSConfig.LAMBDA_TIMEOUT;
import static sukriti.ngo.mis.AWSConfig.cognitoRegion;
import static sukriti.ngo.mis.AWSConfig.identityPoolID;
import static sukriti.ngo.mis.AWSConfig.providerName;

public class ReportLambdaClient {
    private CognitoCachingCredentialsProvider credentialsProvider;
    private Context context;

    public ReportLambdaClient(Context context) {
        this.context = context;
    }

    public void ExecuteReportLambda(ReportLambdaRequest request, final RepositoryCallback callback) {

        Log.i("reportLambda", "ExecuteCabinDetailsLambda: 3");
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    ReportLambdaHandler mReportLambdaHandler = factory.build(ReportLambdaHandler.class);
                    ArrayList <Root> response = mReportLambdaHandler.mis_report_fetchDateWaiseUsageData(request);
//                    ArrayList <ReportLambdaResult> result = mReportLambdaHandler.mis_report_fetchDateWiseUsageData(request);
//
//                    Log.i("reportLambda", "response: " + new Gson().toJson(response));

                    Type typeMyType = new TypeToken<ArrayList<Root>>(){}.getType();

                    ArrayList<Root> responseObj = new Gson().fromJson(new Gson().toJson(response), typeMyType);

                    ReportLambdaResult result = new ReportLambdaResult(responseObj);

                    Log.i("__ExecuteLambda", "response: " + new Gson().toJson(result));
                    Log.i("__misTest", "response: " + new Gson().toJson(result));

                    Log.i("reportLambda", "ExecuteCabinDetailsLambda: 4 \n DashBoard Lambda"+new Gson().toJson(result));
                    returnCallback =()->{
                        Log.i("reportLambda", "ExecuteCabinDetailsLambda: 4.1 \n DashBoard success Lambda"+new Gson().toJson(result));
                        callback.onComplete(new _Result.Success<>(result));
                    };
                    handler.post(returnCallback);

                } catch (LambdaFunctionException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<String>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                } catch (NotAuthorizedException e) {
                    Log.i("__ExecuteLambda", "NotAuthorizedException");
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<String>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                } catch (AmazonClientException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        e.printStackTrace();
                        callback.onComplete(new _Result.Error<String>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                }
            } else {
                returnCallback = () -> {
                    callback.onComplete(new _Result.Error<String>(-1, "Session Invaled"));
                };
                handler.post(returnCallback);
            }
        }).start();
    }



    private LambdaInvokerFactory initLambdaClient() {
        String idToken = AuthenticationClient.getCurrSession().getIdToken().getJWTToken();
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                identityPoolID,
                cognitoRegion
        );
        Map<String, String> logins = new HashMap<String, String>();
        logins.put(providerName, idToken);
        credentialsProvider.setLogins(logins);
        credentialsProvider.getCredentials();

        ClientConfiguration config = new ClientConfiguration();
        config.setSocketTimeout(LAMBDA_TIMEOUT);

        LambdaInvokerFactory factory = new LambdaInvokerFactory(context,
                Regions.AP_SOUTH_1, credentialsProvider, config);

        return factory;
    }
}
