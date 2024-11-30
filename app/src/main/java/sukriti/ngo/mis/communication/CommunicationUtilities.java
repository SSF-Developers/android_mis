package sukriti.ngo.mis.communication;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;

import org.json.JSONObject;

import sukriti.ngo.mis.MISApplication;
import sukriti.ngo.mis.utils.Utilities;

import static java.lang.Thread.sleep;
import static sukriti.ngo.mis.communication.CommunicationConfig.POOLING_SLEEP;

public class CommunicationUtilities {

    private static int CONNECT_STATUS;
    private static final int CONNECT_FLAG_CONNECTING = 0;
    private static final int CONNECT_FLAG_CONNECT_SUCCESS = 1;
    private static final int CONNECT_FLAG_CONNECT_ERR = -1;
    private static int successCount = 0;
    private static int un_successCount = 0;


    public void publishPayload(Context context, String Topic, JSONObject payload, final SimpleHandler fragmentCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Handler handler = new Handler(context.getMainLooper());
                Runnable returnCallback;

                boolean error = false;
                String errorMsg = "";
                if (!MISApplication.getCommunicationClientInstance().getStatus().isInitialized) {
                    error = true;
                    errorMsg = "CommunicationClient initialization error";
                }

                if (!Utilities.isNetworkAvailable(context)) {
                    error = true;
                    errorMsg = "No internet connectivity available";
                }

                if (!MISApplication.getCommunicationClientInstance().getStatus().hasStableConnection) {
                    connect();
                    if (CONNECT_STATUS == CONNECT_FLAG_CONNECT_ERR) {
                        error = true;
                        errorMsg = "Error while connecting the client";
                    }
                }

                if (error) {
                    String finalErrorMsg = errorMsg;
                    returnCallback = new Runnable() {
                        @Override
                        public void run() {
                            fragmentCallback.onError(finalErrorMsg);
                        }
                    };

                } else {
                    successCount = 0;
                    un_successCount = 0;
                    int publishCount = 1;

                    MISApplication.getCommunicationClientInstance().Publish(Topic, payload, new AWSPublishHandler() {
                        @Override
                        public void onSuccess(Object o) {
                            successCount++;
                        }

                        @Override
                        public void onError(Object o, String ErrorMsg) {
                            un_successCount++;
                        }
                    });


                    //WAIT
                    while (successCount + un_successCount != publishCount) {
                        try {
                            sleep(POOLING_SLEEP);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (successCount == publishCount) {
                        returnCallback = new Runnable() {
                            @Override
                            public void run() {
                                fragmentCallback.onSuccess();
                            }
                        };
                    } else {
                        returnCallback = new Runnable() {
                            @Override
                            public void run() {
                                fragmentCallback.onError("" + un_successCount + " attempt(s) failed out of total " + publishCount + " attempt(s).");
                            }
                        };
                    }
                }
                MISApplication.getCommunicationClientInstance().disconnect();
                handler.post(returnCallback);
            }
        }).start();
    }

    private static void connect() {
        CONNECT_STATUS = CONNECT_FLAG_CONNECTING;
        MISApplication.getCommunicationClientInstance().Connect(new SimpleHandler() {
            @Override
            public void onSuccess() {
                Log.i("__connect", "CONNECT_FLAG_CONNECT_SUCCESS");
                CONNECT_STATUS = CONNECT_FLAG_CONNECT_SUCCESS;
            }

            @Override
            public void onError(String ErrorMsg) {
                Log.i("__connect", "CONNECT_FLAG_CONNECT_ERR");
                CONNECT_STATUS = CONNECT_FLAG_CONNECT_ERR;
            }
        });

        int waitCount = 0;
        while (CONNECT_STATUS == CONNECT_FLAG_CONNECTING) {
            try {
                waitCount++;
                Log.i("__connect", "Waiting to connect " + waitCount);
                sleep(POOLING_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
