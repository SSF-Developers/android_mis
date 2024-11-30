package sukriti.ngo.mis.communication;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttSubscriptionStatusCallback;
import com.amazonaws.regions.Region;
import com.amazonaws.services.cognitoidentity.model.NotAuthorizedException;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPolicyRequest;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.ListAttachedPoliciesRequest;
import com.amazonaws.services.iot.model.ListAttachedPoliciesResult;
import com.amazonaws.services.iot.model.ListPoliciesRequest;
import com.amazonaws.services.iot.model.ListPoliciesResult;
import com.amazonaws.services.iot.model.Policy;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import sukriti.ngo.mis.utils.AuthenticationClient;
import sukriti.ngo.mis.utils.SharedPrefsClient;

import static java.lang.Thread.sleep;
import static sukriti.ngo.mis.AWSConfig.cognitoRegion;
import static sukriti.ngo.mis.AWSConfig.identityPoolID;
import static sukriti.ngo.mis.AWSConfig.providerName;
import static sukriti.ngo.mis.communication.CommunicationConfig.AWS_PING_INTERVAL;
import static sukriti.ngo.mis.communication.CommunicationConfig.POOLING_SLEEP_SHORT;
import static sukriti.ngo.mis.communication.CommunicationConfig.RECONNECT_INITIAL_DELAY;
import static sukriti.ngo.mis.communication.CommunicationConfig.STABLE_CONNECTION_THRESHOLD;
import static sukriti.ngo.mis.communication.CommunicationConfig.TIMEOUT_THRESHOLD_CONNECT_ACTION;

public class AWSCommunicationClient {
    private static Timer connectedTimer;
    private static AWSIotMqttClientStatus lastConnectStatus = AWSIotMqttClientStatus.Connecting;
    private CognitoCachingCredentialsProvider credentialsProvider;

    private static AWSIotMqttManager awsIotMqttManager;
    private SharedPrefsClient sharedPrefsClient;
    private Runnable returnCallback;
    private static Context context;
    private boolean isInitialized = false;
    private int Time = 0;
    private int connectedCount = 0;
    private int connectingCount = 0;
    private int connectLostCount = 0;

    private final String TAG = "AWSCommClient";

    private int CALLBACK_STATUS;
    private final int CALLBACK_STATUS_PROCESSING = 0;
    private final int CALLBACK_STATUS_SUCCESS = 1;
    private final int CALLBACK_STATUS_ERR = -1;
    private final int CALLBACK_STATUS_TIMEOUT = -2;

    private int PUBLISH_STATUS;
    private final int PUBLISH_FLAG_PROCESSING = 0;
    private final int PUBLISH_FLAG_SUCCESS = 1;
    private final int PUBLISH_FLAG_ERR = -1;

    private int SUBSCRIBE_STATUS;
    private final int SUBSCRIBE_FLAG_CONNECTING = 0;
    private final int SUBSCRIBE_FLAG_SUCCESS = 1;
    private final int SUBSCRIBE_FLAG_ERR = -1;

    public AWSCommunicationClient(Context context) {
        sharedPrefsClient = new SharedPrefsClient(context);
        this.context = context;
    }

    public Boolean isInitialized() {
        return isInitialized;
    }

    public AWSCommunicationClientStatus getStatus() {
        AWSCommunicationClientStatus status = new AWSCommunicationClientStatus();
        status.isInitialized = isInitialized;
        status.lastConnectStatus = lastConnectStatus;
        status.lifeTimeSpan = Time;

        status.hasStableConnection = false;
        status.statusTimeSpan = 0;
        if (lastConnectStatus.name().compareToIgnoreCase(AWSIotMqttClientStatus.Connected.name()) == 0) {
            status.statusTimeSpan = connectedCount;
            if (connectedCount > STABLE_CONNECTION_THRESHOLD)
                status.hasStableConnection = true;
        } else if (lastConnectStatus.name().compareToIgnoreCase(AWSIotMqttClientStatus.Connecting.name()) == 0 ||
                lastConnectStatus.name().compareToIgnoreCase(AWSIotMqttClientStatus.Reconnecting.name()) == 0) {
            status.statusTimeSpan = connectingCount;
        } else if (lastConnectStatus.name().compareToIgnoreCase(AWSIotMqttClientStatus.ConnectionLost.name()) == 0) {
            status.statusTimeSpan = connectLostCount;
        }
        return status;
    }

    public void Initialize(final SimpleHandler callback) {
        final Handler handler = new Handler(context.getMainLooper());
        Runnable returnCallback;

            String userName = sharedPrefsClient.getUserDetails().getUser().getUserName();
            awsIotMqttManager = new AWSIotMqttManager(userName, CommissioningConfig.awsIotEndPoint);
            awsIotMqttManager.setReconnectRetryLimits(RECONNECT_INITIAL_DELAY, TIMEOUT_THRESHOLD_CONNECT_ACTION);
            awsIotMqttManager.setKeepAlive(AWS_PING_INTERVAL);
            awsIotMqttManager.setConnectionStabilityTime(STABLE_CONNECTION_THRESHOLD);

            //Persistent Connection
            awsIotMqttManager.setCleanSession(false);
            returnCallback = new Runnable() {
                @Override
                public void run() {
                    isInitialized = true;
                    callback.onSuccess();
                }
            };

        handler.post(returnCallback);
    }


    public synchronized void Connect(final SimpleHandler callback) {

        CALLBACK_STATUS = CALLBACK_STATUS_PROCESSING;
        resetCounts();
        startTimer();
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (AuthenticationClient.getCurrSession() != null) {
                    try {
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
                        String identityId = credentialsProvider.getIdentityId();
                        Log.i(TAG,"identityId: "+identityId);

                        //Policy Test
                        AWSIotClient awsIotClient =  new AWSIotClient(credentialsProvider);
                        awsIotClient.setRegion(Region.getRegion(cognitoRegion));

//                        String randomPolicyName = "";
//                        ListPoliciesRequest listPolicyRequest = new ListPoliciesRequest()
//                                .withAscendingOrder(false)
//                                .withPageSize(100);
//                        ListPoliciesResult listResult = awsIotClient.listPolicies(listPolicyRequest);
//                        int count = listResult.getPolicies().size();
//                        Log.i("_policyList","cont: "+count);
//                        for(Policy policy : listResult.getPolicies()) {
//                            Log.i("_policyList",policy.getPolicyName());
//                            randomPolicyName = policy.getPolicyName();
//                        }


//                        AttachPolicyRequest attachPolicyReq = new AttachPolicyRequest()
//                                .withPolicyName("mis-SSF-dev_rahul02")
//                                .withTarget(credentialsProvider.getCachedIdentityId());
//                        awsIotClient.attachPolicy(attachPolicyReq);


                        ListAttachedPoliciesRequest policyRequest = new ListAttachedPoliciesRequest()
                                .withTarget(credentialsProvider.getCachedIdentityId())
                                .withRecursive(false);
                        ListAttachedPoliciesResult result = awsIotClient.listAttachedPolicies(policyRequest);
                        int count = result.getPolicies().size();
                        Log.i("_policyCheck","cont: "+count);
                        for(Policy policy : result.getPolicies()) {
                            Log.i("_policyCheck",policy.getPolicyName());
                        }


                        int waitCount = 0;
                        Runnable returnCallback = null;
                        final Handler handler = new Handler(Looper.getMainLooper());//new Handler(context.getMainLooper());
                        try {
                            Log.e(TAG,"Start Connection Attempt");
                            awsIotMqttManager.connect(credentialsProvider, new AWSIotMqttClientStatusCallback() {
                                @Override
                                public void onStatusChanged(final AWSIotMqttClientStatus currentStatus,
                                                            final Throwable throwable) {
                                    Log.d(TAG, "CurrentStatus = " + currentStatus.name() + " " + "  LastConnectStatus = " + lastConnectStatus.name());

                                    if (currentStatus == AWSIotMqttClientStatus.Connecting) {
                                        resetCounts();
                                        startTimer();
                                    }

                                    if (lastConnectStatus.name().compareToIgnoreCase(currentStatus.name()) != 0)
                                        resetCounts();
                                    lastConnectStatus = currentStatus;
                                }
                            });
                            while (CALLBACK_STATUS == CALLBACK_STATUS_PROCESSING) {
                                try {
                                    waitCount++;
                                    sleep(POOLING_SLEEP_SHORT);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (final Exception e) {
                            Log.e(TAG,"Connect Error: "+e.getMessage() );
                            e.printStackTrace();
                            CALLBACK_STATUS = CALLBACK_STATUS_ERR;
                        }

                        if (CALLBACK_STATUS == CALLBACK_STATUS_SUCCESS) {
//                        Subscribe("dev/test/01");
//                        IotApplication.getCommunicationClientInstance().Subscribe("dev/test/01");
                            returnCallback = new Runnable() {
                                @Override
                                public void run() {
                                    callback.onSuccess();
                                }
                            };
                        } else if (CALLBACK_STATUS == CALLBACK_STATUS_TIMEOUT) {
                            returnCallback = new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError("Connect timeout.");
                                }
                            };
                        } else if (CALLBACK_STATUS == CALLBACK_STATUS_ERR) {
                            returnCallback = new Runnable() {
                                @Override
                                public void run() {
                                    callback.onError("Internal error while connecting.");
                                }
                            };
                        }
                        handler.post(returnCallback);

                    }  catch (NotAuthorizedException e) {
                        e.printStackTrace();
                        returnCallback = () -> {
                            callback.onError(e.getMessage());
                        };
                    }
                }

            }
        }).start();

    }

    public void Publish(final String Topic, final JSONObject JO, final AWSPublishHandler callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler(context.getMainLooper());
                try {
                    awsIotMqttManager.publishString(JO.toString(), Topic, AWSIotMqttQos.QOS1);
                    returnCallback = new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(JO);
                        }
                    };
                } catch (final Exception e) {
                    e.printStackTrace();
                    returnCallback = new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(JO,e.getMessage());
                        }
                    };
                }
                handler.post(returnCallback);
            }


        }).start();
    }

    public void Subscribe(final String TopicFilter, final SimpleHandler callback) {
        SUBSCRIBE_STATUS = SUBSCRIBE_FLAG_CONNECTING;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler(context.getMainLooper());
                try {
                    awsIotMqttManager.subscribeToTopic(TopicFilter, AWSIotMqttQos.QOS1, new AWSIotMqttSubscriptionStatusCallback() {
                        @Override
                        public void onSuccess() {
                            SUBSCRIBE_STATUS = SUBSCRIBE_FLAG_SUCCESS;
                            returnCallback = new Runnable() {
                                @Override
                                public void run() {
                                    callback.onSuccess();
                                }
                            };
                        }

                        @Override
                        public void onFailure(final Throwable exception) {
                            SUBSCRIBE_STATUS = SUBSCRIBE_FLAG_ERR;
                            returnCallback = new Runnable() {
                                @Override
                                public void run() {
                                    Log.e(TAG, exception.getMessage());
                                    callback.onError(exception.getMessage());
                                }
                            };
                        }
                    }, awsIotMqttNewMessageCallback);
                } catch (Exception e) {
                    SUBSCRIBE_STATUS = SUBSCRIBE_FLAG_ERR;
                    e.printStackTrace();
                }
                while (SUBSCRIBE_STATUS == SUBSCRIBE_FLAG_CONNECTING) {
                        try {
                            sleep(POOLING_SLEEP_SHORT);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                }
                handler.post(returnCallback);
            }
        }).start();
    }

    public void Subscribe(String TopicFilter) {
        try {
            awsIotMqttManager.subscribeToTopic(TopicFilter, AWSIotMqttQos.QOS1, awsIotMqttNewMessageCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        connectedTimer.cancel();
        resetCounts();
        try {
            awsIotMqttManager.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final AWSIotMqttNewMessageCallback awsIotMqttNewMessageCallback = new AWSIotMqttNewMessageCallback() {
        @Override
        public void onMessageArrived(final String topic, final byte[] data) {
            String TAG = "_AWSMessageArrived";
            Log.d(TAG, "Topic: " + topic);
            String message = null;
            try {
                message = new String(data, "UTF-8");
                Log.d(TAG, "Message: " + message);
                JSONObject payload = new JSONObject(message);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void startTimer() {
        Log.i(TAG, "startTimer");
        Time = 0;
        if (connectedTimer != null) {
            connectedTimer.cancel();
        }
        connectedTimer = new Timer();
        connectedTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                Time++;
                if (lastConnectStatus.name().compareToIgnoreCase(AWSIotMqttClientStatus.Connected.name()) == 0) {
                    connectedCount++;
                    Log.i(TAG, "connected:" + connectedCount + "  Time:" + Time + " Secs  Status:" + lastConnectStatus.name() + " CALLBACK_STATUS: " + CALLBACK_STATUS);
                } else if (lastConnectStatus.name().compareToIgnoreCase(AWSIotMqttClientStatus.Connecting.name()) == 0 ||
                        lastConnectStatus.name().compareToIgnoreCase(AWSIotMqttClientStatus.Reconnecting.name()) == 0) {
                    connectingCount++;
                    Log.i(TAG, "connecting...: " + connectingCount + "  Time:" + Time + " Secs  Status:" + lastConnectStatus.name());
                } else if (lastConnectStatus.name().compareToIgnoreCase(AWSIotMqttClientStatus.ConnectionLost.name()) == 0) {
                    connectLostCount++;
                    Log.i(TAG, "connectLost: " + connectLostCount + "  Time:" + Time + " Secs  Status:" + lastConnectStatus.name());
                }

                if (CALLBACK_STATUS == CALLBACK_STATUS_PROCESSING && connectedCount >= STABLE_CONNECTION_THRESHOLD) {
                    CALLBACK_STATUS = CALLBACK_STATUS_SUCCESS;
                }

                if (CALLBACK_STATUS == CALLBACK_STATUS_PROCESSING && Time >= TIMEOUT_THRESHOLD_CONNECT_ACTION) {
                    CALLBACK_STATUS = CALLBACK_STATUS_TIMEOUT;
                    Log.e(TAG,"CALLBACK_STATUS_TIMEOUT");
                }

            }
        }, 0, 1000);
    }


    private void resetCounts() {
        Log.i(TAG, "   resetCounts");
        connectedCount = 0;
        connectingCount = 0;
        connectLostCount = 0;
    }

}

