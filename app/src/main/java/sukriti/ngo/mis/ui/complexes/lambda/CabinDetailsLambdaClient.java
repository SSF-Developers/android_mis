package sukriti.ngo.mis.ui.complexes.lambda;

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

import java.util.HashMap;
import java.util.Map;

import sukriti.ngo.mis.dataModel._Result;
import sukriti.ngo.mis.interfaces.RepositoryCallback;
import sukriti.ngo.mis.repository.DataRepository;
import sukriti.ngo.mis.ui.complexes.lambda.CabinComposition_fetchdata.ComplexCompositionLambdaHandler;
import sukriti.ngo.mis.ui.complexes.lambda.CabinComposition_fetchdata.ComplexCompositionLambdaRequest;
import sukriti.ngo.mis.ui.complexes.lambda.CabinComposition_fetchdata.ComplexCompositionLambdaResult;
import sukriti.ngo.mis.ui.complexes.lambda.Cabin_fetchData.CabinDetailsLambdaHandler;
import sukriti.ngo.mis.ui.complexes.lambda.Cabin_fetchData.CabinDetailsLambdaRequest;
import sukriti.ngo.mis.ui.complexes.lambda.Cabin_fetchData.CabinDetailsLambdaResult;
import sukriti.ngo.mis.ui.complexes.lambda.Complex_fetchAccessTree.AccessTreeLambdaHandler;
import sukriti.ngo.mis.ui.complexes.lambda.Complex_fetchAccessTree.AccessTreeLambdaRequest;
import sukriti.ngo.mis.ui.complexes.lambda.Complex_fetchAccessTree.AccessTreeLambdaResult;
import sukriti.ngo.mis.ui.complexes.lambda.bwt_fetchCabinDetails.BwtCabinDetailsLambdaHandler;
import sukriti.ngo.mis.ui.complexes.lambda.bwt_fetchCabinDetails.BwtCabinDetailsLambdaRequest;
import sukriti.ngo.mis.ui.complexes.lambda.bwt_fetchCabinDetails.BwtCabinDetailsLambdaResult;
import sukriti.ngo.mis.utils.AuthenticationClient;

import static sukriti.ngo.mis.AWSConfig.LAMBDA_TIMEOUT;
import static sukriti.ngo.mis.AWSConfig.cognitoRegion;
import static sukriti.ngo.mis.AWSConfig.identityPoolID;
import static sukriti.ngo.mis.AWSConfig.providerName;

public class CabinDetailsLambdaClient {
    private CognitoCachingCredentialsProvider credentialsProvider;
    private Context context;
    private DataRepository dataRepository;

    public CabinDetailsLambdaClient(Context context) {
        this.context = context;
        this.dataRepository = new DataRepository(context);
    }

    public void ExecuteBwtCabinDetailsLambda(BwtCabinDetailsLambdaRequest request, final RepositoryCallback callback) {

        Log.i("cabinDetails", "ExecuteCabinDetailsLambda: 3");
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    BwtCabinDetailsLambdaHandler cabinDetailsLambdaHandler = factory.build(BwtCabinDetailsLambdaHandler.class);
                    BwtCabinDetailsLambdaResult result = cabinDetailsLambdaHandler.mis_administration_BWT_getCabinDetails(request);

                    Log.i("cabinDetails", "response: " + new Gson().toJson(result));
                    Log.i("__misTest", "response: " + new Gson().toJson(result));

                    Log.i("cabinDetails", "ExecuteCabinDetailsLambda: 4 \n  Lambda"+new Gson().toJson(result));

                    if (result.status == 1) {
                        returnCallback = () -> {
                            Log.i("cabinDetails", "ExecuteCabinDetailsLambda: 4.1 \n  success Lambda" + new Gson().toJson(result));
                            callback.onComplete(new _Result.Success<BwtCabinDetailsLambdaResult>(result));
                        };
                    }else{
                        returnCallback = () -> {
                            callback.onComplete(new _Result.Error<String>(-1, "Status :"+result.status));
                        };

                    }
                    handler.post(returnCallback);


                } catch (LambdaFunctionException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<String>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                } catch (NotAuthorizedException e) {
                    Log.i("clientDetails", "NotAuthorizedException");
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

    public void ExecuteComplexCompositionLambda(ComplexCompositionLambdaRequest request, final RepositoryCallback callback) {

        Log.i("cabinDetails", "ExecuteCabinDetailsLambda: 3");
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    ComplexCompositionLambdaHandler complexCompositionLambdaHandler = factory.build(ComplexCompositionLambdaHandler.class);
                    ComplexCompositionLambdaResult result = complexCompositionLambdaHandler.mis_adminisatration_getComplexComposition(request);

                    Log.i("cabinDetails", "response: " + new Gson().toJson(result));
                    Log.i("__misTest", "response: " + new Gson().toJson(result));

                    Log.i("cabinDetails", "ExecuteCabinDetailsLambda: 4 \n  Lambda"+new Gson().toJson(result));

                    if (result.status == 1) {
                        returnCallback = () -> {
                            Log.i("cabinDetails", "ExecuteCabinDetailsLambda: 4.1 \n  success Lambda" + new Gson().toJson(result));
                            callback.onComplete(new _Result.Success<>(result));
                        };
                    }else{
                        returnCallback = () -> {
                            callback.onComplete(new _Result.Error<String>(-1, "Status :"+result.status));
                        };

                    }
                    handler.post(returnCallback);


                } catch (LambdaFunctionException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<String>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                } catch (NotAuthorizedException e) {
                    Log.i("clientDetails", "NotAuthorizedException");
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

    public void ExecuteCabinDetailsLambda(CabinDetailsLambdaRequest request, final RepositoryCallback callback) {

        Log.i("cabinDetails", "ExecuteCabinDetailsLambda: 3");
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    CabinDetailsLambdaHandler cabinDetailsLambdaHandler = factory.build(CabinDetailsLambdaHandler.class);
                    CabinDetailsLambdaResult result = cabinDetailsLambdaHandler.mis_adminisatration_getCabinDetails(request);

                    Log.i("cabinDetails", "response: " + new Gson().toJson(result));
                    Log.i("__misTest", "response: " + new Gson().toJson(result));

                    Log.i("cabinDetails", "ExecuteCabinDetailsLambda: 4 \n  Lambda"+new Gson().toJson(result));

                    if (result.status == 1) {
                        returnCallback = () -> {
                            Log.i("cabinDetails", "ExecuteCabinDetailsLambda: 4.1 \n  success Lambda" + new Gson().toJson(result));
                            callback.onComplete(new _Result.Success<CabinDetailsLambdaResult>(result));
                        };
                    }else{
                        returnCallback = () -> {
                            callback.onComplete(new _Result.Error<String>(-1, "Status :"+result.status));
                        };

                    }
                    handler.post(returnCallback);


                } catch (LambdaFunctionException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<String>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                } catch (NotAuthorizedException e) {
                    Log.i("clientDetails", "NotAuthorizedException");
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

    public void ExecuteAccessTreeLambda(AccessTreeLambdaRequest request, final RepositoryCallback callback) {
        Log.i("LambdaExecution", "ExecuteCabinDetailsLambda: 3");
        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    AccessTreeLambdaHandler accessTreeLambdaHandler = factory.build(AccessTreeLambdaHandler.class);
                    AccessTreeLambdaResult result = accessTreeLambdaHandler.mis_adminisatration_getCompletedAccessTree(request);

                    Log.i("accessLambda", "response: " + new Gson().toJson(result));
                    Log.i("__misTest", "response: " + new Gson().toJson(result));

//                    Log.i("accessLambda", "ExecuteCabinDetailsLambda: 4 \n  Lambda"+new Gson().toJson(result));

                    if (result.status == 1) {
                        returnCallback = () -> {
                            Log.i("accessLambda", "ExecuteCabinDetailsLambda: 4.1 \n  success Lambda" + new Gson().toJson(result));
                            callback.onComplete(new _Result.Success<>(result));
                        };
                    }else{
                        returnCallback = () -> {
                            callback.onComplete(new _Result.Error<String>(-1, "Status :"+result.status));
                        };

                    }
                    handler.post(returnCallback);


                } catch (LambdaFunctionException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<String>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                } catch (NotAuthorizedException e) {
                    Log.i("accessLambda", "NotAuthorizedException");
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
