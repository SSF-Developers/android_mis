package sukriti.ngo.mis.ui.tickets;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunctionException;
import com.amazonaws.mobileconnectors.lambdainvoker.LambdaInvokerFactory;
import com.amazonaws.regions.Regions;
//import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import sukriti.ngo.mis.ui.login.data.UserProfile;
import sukriti.ngo.mis.ui.tickets.data.TicketDetailsData;
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

public class TicketsLambdaClient {

    private CognitoCachingCredentialsProvider credentialsProvider;
    private Context context;
    private DataRepository dataRepository;

    public TicketsLambdaClient(Context context) {
        this.context = context;
        this.dataRepository = new DataRepository(context);
    }

    public void ExecuteCreateTicketLambda(CreateTicketLambdaRequest request, final RepositoryCallback callback) {

        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    CreateTicketLambdaHandler mCreateTicketLambdaHandler = factory.build(CreateTicketLambdaHandler.class);
                    CreateTicketLambdaResult result = mCreateTicketLambdaHandler.mis_create_ticket(request);

                    Log.i("__ExecuteLambda", "response: " + new Gson().toJson(result));
                    Log.i("__misTest", "response: " + new Gson().toJson(result));

                    if (result.result == 1) {
                        //Success response
                        returnCallback = () -> {
                            callback.onComplete(new _Result.Success<String>(result.ticketId));
                        };
                    } else {
                        returnCallback = () -> {
                            callback.onComplete(new _Result.Error<String>(-1, result.message));
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


    public void ExecuteListTicketsLambda(ListTicketsLambdaRequest request, final RepositoryCallback callback) {

        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback  = () -> {
                callback.onComplete(new _Result.Error<>(-1, "VOID"));
            };

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    ListTicketsLambdaHandler mLambdaHandler = factory.build(ListTicketsLambdaHandler.class);
                    ListTicketsLambdaResult result = mLambdaHandler.mis_list_tickets_by_access(request);

                    Log.i("__ListTicketsLambda", "response: " + new Gson().toJson(result));

                    if (result.result == 1) {
                        //Success response
                        List<Ticket> allTickets = new Gson().fromJson(
                                result.allTickets, new TypeToken<List<Ticket>>() {
                                }.getType());
                        List<TicketProgress> ticketProgress = new Gson().fromJson(
                                result.ticketProgress, new TypeToken<List<TicketProgress>>() {
                                }.getType());

                        //Set all tickets
                        TicketListData ticketsListData = new TicketListData();
                        ticketsListData.setAllTickets(allTickets);

                        if(request.getUserRole().compareToIgnoreCase("Vendor Admin")==0)
                        {
                            //Set queued tickets
                            List<Ticket> queuedTickets = new Gson().fromJson(
                                    result.queuedTickets, new TypeToken<List<Ticket>>() {
                                    }.getType());
                            ticketsListData.setQueuedTickets(queuedTickets);
                        }
                        if(request.getUserRole().compareToIgnoreCase("Super Admin")==0){
                            //Set unQueued tickets
                            List<Ticket> unQueuedTickets = new Gson().fromJson(
                                    result.unQueuedTickets, new TypeToken<List<Ticket>>() {
                                    }.getType());
                            ticketsListData.setUnQueuedTicketData(unQueuedTickets);
                        }

                        RequestHandler bulkInsertCallback = new RequestHandler() {
                            @Override
                            public void onSuccess() {
                                Runnable returnCallback = () -> {
                                    callback.onComplete(new _Result.Success<>(ticketsListData));
                                };
                                handler.post(returnCallback);
                            }

                            @Override
                            public void onError(String message) {
                                Runnable returnCallback = () -> {
                                    callback.onComplete(new _Result.Error<String>(-1, message));
                                };
                                handler.post(returnCallback);
                            }
                        };

                        Log.i("_insertProgress-0", "" + ticketProgress.size());
                        dataRepository.insertBulkTicketsData(allTickets,ticketProgress,handler,bulkInsertCallback );

                    } else {
                        returnCallback = () -> {
                            callback.onComplete(new _Result.Error<>(-1, result.message));
                        };
                        handler.post(returnCallback);
                    }


                } catch (LambdaFunctionException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        Log.i("_misTest", "ExecuteListTicketsLambda: "+e);
                        callback.onComplete(new _Result.Error<>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                } catch (NotAuthorizedException e) {
                    Log.i("__ExecuteLambda", "NotAuthorizedException");
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                } catch (AmazonClientException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        e.printStackTrace();
                        callback.onComplete(new _Result.Error<>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                }
            } else {
                returnCallback = () -> {
                    callback.onComplete(new _Result.Error<>(-1, "Session Invalid"));
                };
                handler.post(returnCallback);
            }
        }).start();
    }

    public void ExecuteListTicketProgressLambda(ListTicketProgressLambdaRequest request,
                                                final RepositoryCallback<List<TicketProgressData>> callback) {

        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    ListTicketProgressLambdaHandler mLambdaHandler = factory.build(ListTicketProgressLambdaHandler.class);
                    ListTicketProgressLambdaResult result = mLambdaHandler.mis_list_ticket_progress(request);

                    Log.i("__ExecuteLambda", "response: " + new Gson().toJson(result));

                    if (result.result == 1) {
                        //Success response
                        List<TicketProgressData> ticketProgressList = new Gson().fromJson(
                                result.ticketProgressList, new TypeToken<List<TicketProgressData>>() {
                                }.getType());


                        returnCallback = () -> {
                            callback.onComplete(new _Result.Success<List<TicketProgressData>>(ticketProgressList));
                        };
                    } else {
                        returnCallback = () -> {
                            callback.onComplete(new _Result.Error<List<TicketProgressData>>(-1, result.message));
                        };
                    }
                    handler.post(returnCallback);


                } catch (LambdaFunctionException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<List<TicketProgressData>>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                } catch (NotAuthorizedException e) {
                    Log.i("__ExecuteLambda", "NotAuthorizedException");
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<List<TicketProgressData>>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                } catch (AmazonClientException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        e.printStackTrace();
                        callback.onComplete(new _Result.Error<List<TicketProgressData>>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                }
            } else {
                returnCallback = () -> {
                    callback.onComplete(new _Result.Error<List<TicketProgressData>>(-1, "Session Invaled"));
                };
                handler.post(returnCallback);
            }
        }).start();
    }

    public void ExecutePerformTicketActionLambda(TicketActionsLambdaRequest request,
                                              final RepositoryCallback<TicketActionsLambdaRequest> callback) {

        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    TicketActionsLambdaHandler mLambdaHandler = factory.build(TicketActionsLambdaHandler.class);
                    TicketActionsLambdaResult result = mLambdaHandler.mis_ticket_actions(request);

                    Log.i("__ExecuteLambda", "response: " + new Gson().toJson(result));

                    if (result.result == 1) {
                        //Success response
                        returnCallback = () -> {
                            callback.onComplete(new _Result.Success<TicketActionsLambdaRequest>(request));
                        };
                    } else {
                        returnCallback = () -> {
                            callback.onComplete(new _Result.Error<TicketActionsLambdaRequest>(-1, result.message));
                        };
                    }
                    handler.post(returnCallback);


                } catch (LambdaFunctionException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<TicketActionsLambdaRequest>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                } catch (NotAuthorizedException e) {
                    Log.i("__ExecuteLambda", "NotAuthorizedException");
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<TicketActionsLambdaRequest>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                } catch (AmazonClientException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        e.printStackTrace();
                        callback.onComplete(new _Result.Error<TicketActionsLambdaRequest>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                }
            } else {
                returnCallback = () -> {
                    callback.onComplete(new _Result.Error<TicketActionsLambdaRequest>(-1,"Session Invalid"));
                };
                handler.post(returnCallback);
            }
        }).start();
    }

    public void ExecuteListTicketTeamLambda(ListTicketTeamLambdaRequest request,
                                            final RepositoryCallback<List<TicketTeamData>> callback) {

        new Thread(() -> {
            final Handler handler = new Handler(context.getMainLooper());
            Runnable returnCallback;

            if (AuthenticationClient.getCurrSession() != null) {
                try {
                    LambdaInvokerFactory factory = initLambdaClient();
                    ListTicketTeamLambdaHandler mLambdaHandler = factory.build(ListTicketTeamLambdaHandler.class);
                    ListTicketTeamLambdaResult result = mLambdaHandler.mis_list_ticket_team(request);

                    Log.i("__ExecuteLambda", "response: " + new Gson().toJson(result));

                    if (result.result == 1) {
                        //Success response
                        List<TicketTeamData> ticketTeamList = new Gson().fromJson(
                                result.ticketTeamList, new TypeToken<List<TicketTeamData>>() {
                                }.getType());
                        returnCallback = () -> {
                            callback.onComplete(new _Result.Success<List<TicketTeamData>>(ticketTeamList));
                        };
                    } else {
                        returnCallback = () -> {
                            callback.onComplete(new _Result.Error<List<TicketTeamData>>(-1, result.message));
                        };
                    }
                    handler.post(returnCallback);


                } catch (LambdaFunctionException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<List<TicketTeamData>>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                } catch (NotAuthorizedException e) {
                    Log.i("__ExecuteLambda", "NotAuthorizedException");
                    e.printStackTrace();
                    returnCallback = () -> {
                        callback.onComplete(new _Result.Error<List<TicketTeamData>>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                } catch (AmazonClientException e) {
                    e.printStackTrace();
                    returnCallback = () -> {
                        e.printStackTrace();
                        callback.onComplete(new _Result.Error<List<TicketTeamData>>(-1, e.getMessage(), e));
                    };
                    handler.post(returnCallback);
                }
            } else {
                returnCallback = () -> {
                    callback.onComplete(new _Result.Error<List<TicketTeamData>>(-1,"Session Invalid"));
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
