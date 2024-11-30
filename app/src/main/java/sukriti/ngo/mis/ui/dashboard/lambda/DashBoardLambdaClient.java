package sukriti.ngo.mis.ui.dashboard.lambda;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sukriti.ngo.mis.dataModel._Result;
import sukriti.ngo.mis.interfaces.RepositoryCallback;
import sukriti.ngo.mis.repository.DataRepository;
import sukriti.ngo.mis.repository.entity.Ticket;
import sukriti.ngo.mis.repository.entity.TicketProgress;
import sukriti.ngo.mis.ui.administration.interfaces.RequestHandler;
import sukriti.ngo.mis.ui.dashboard.data.ActiveTicket;
import sukriti.ngo.mis.ui.dashboard.data.DashboardChartData;
import sukriti.ngo.mis.ui.dashboard.data.DashboardData;
import sukriti.ngo.mis.ui.dashboard.data.DataSummary;
import sukriti.ngo.mis.ui.dashboard.data.FaultyComplex;
import sukriti.ngo.mis.ui.dashboard.data.PieChartData;
import sukriti.ngo.mis.ui.dashboard.data.UiResult;
import sukriti.ngo.mis.ui.dashboard.lambda.Dashboard_fetchData.DashBoardLambdaHandler;
import sukriti.ngo.mis.ui.dashboard.lambda.Dashboard_fetchData.DashBoardLambdaRequest;
import sukriti.ngo.mis.ui.dashboard.lambda.Dashboard_fetchData.DashBoardLambdaResult;
import sukriti.ngo.mis.ui.tickets.data.TicketListData;
import sukriti.ngo.mis.ui.tickets.data.TicketProgressData;
import sukriti.ngo.mis.ui.tickets.data.TicketTeamData;
import sukriti.ngo.mis.ui.tickets.lambda.CreateTicket.CreateTicketLambdaHandler;
import sukriti.ngo.mis.ui.tickets.lambda.CreateTicket.CreateTicketLambdaRequest;
import sukriti.ngo.mis.ui.tickets.lambda.CreateTicket.CreateTicketLambdaResult;
import sukriti.ngo.mis.ui.tickets.lambda.ListProgress.ListTicketProgressLambdaHandler;
import sukriti.ngo.mis.ui.tickets.lambda.ListProgress.ListTicketProgressLambdaRequest;
import sukriti.ngo.mis.ui.tickets.lambda.ListProgress.ListTicketProgressLambdaResult;
import sukriti.ngo.mis.ui.tickets.lambda.ListTeam.ListTicketTeamLambdaHandler;
import sukriti.ngo.mis.ui.tickets.lambda.ListTeam.ListTicketTeamLambdaRequest;
import sukriti.ngo.mis.ui.tickets.lambda.ListTeam.ListTicketTeamLambdaResult;
import sukriti.ngo.mis.ui.tickets.lambda.ListTicket.ListTicketsLambdaHandler;
import sukriti.ngo.mis.ui.tickets.lambda.ListTicket.ListTicketsLambdaRequest;
import sukriti.ngo.mis.ui.tickets.lambda.ListTicket.ListTicketsLambdaResult;
import sukriti.ngo.mis.ui.tickets.lambda.TicketActions.TicketActionsLambdaHandler;
import sukriti.ngo.mis.ui.tickets.lambda.TicketActions.TicketActionsLambdaRequest;
import sukriti.ngo.mis.ui.tickets.lambda.TicketActions.TicketActionsLambdaResult;
import sukriti.ngo.mis.utils.AuthenticationClient;

import static sukriti.ngo.mis.AWSConfig.LAMBDA_TIMEOUT;
import static sukriti.ngo.mis.AWSConfig.cognitoRegion;
import static sukriti.ngo.mis.AWSConfig.identityPoolID;
import static sukriti.ngo.mis.AWSConfig.providerName;

public class DashBoardLambdaClient {
    private CognitoCachingCredentialsProvider credentialsProvider;
    private Context context;
    private DataRepository dataRepository;

    public DashBoardLambdaClient(Context context) {
        this.context = context;
        this.dataRepository = new DataRepository(context);
    }

    public void ExecuteDashBoardLambda(DashBoardLambdaRequest request, final RepositoryCallback callback) {

        Log.i("dashboardLambda", "ExecuteCabinDetailsLambda: 3");
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    DashBoardLambdaHandler mDashBoardLambdaHandler = factory.build(DashBoardLambdaHandler.class);
                    DashBoardLambdaResult result = mDashBoardLambdaHandler.mis_adminisatration_fetchDateWaiseUsageData(request);

                    Log.i("__ExecuteLambda", "response: " + new Gson().toJson(result));
                    Log.i("__misTest", "response: " + new Gson().toJson(result));

                    Log.i("dashboardLambda", "ExecuteCabinDetailsLambda: 4 \n DashBoard Lambda" + new Gson().toJson(result.dashboardChartData));
                    Log.i("dashboardLambda", "ExecuteCabinDetailsLambda: 4 \n DashBoard Lambda :Upi :" + new Gson().toJson(result.dashboardChartData.upiCollection));
                    Log.i("dashboardLambda", "ExecuteCabinDetailsLambda: 4 \n DashBoard Lambda :Upi :" + new Gson().toJson(result.pieChartData.upiCollection));
                    returnCallback = () -> {
                        Log.i("dashboardLambda", "ExecuteCabinDetailsLambda: 4.1 \n DashBoard success Lambda" + new Gson().toJson(result));
                        callback.onComplete(new _Result.Success<DashBoardLambdaResult>(result));
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
