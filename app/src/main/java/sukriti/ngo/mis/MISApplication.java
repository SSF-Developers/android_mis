package sukriti.ngo.mis;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import sukriti.ngo.mis.communication.AWSCommunicationClient;
import sukriti.ngo.mis.communication.SimpleHandler;
import sukriti.ngo.mis.ui.dashboard.data.UiResult;
import sukriti.ngo.mis.utils.NavigationClient;

import static java.lang.Thread.sleep;
import static sukriti.ngo.mis.communication.CommunicationConfig.POOLING_SLEEP_SHORT;

public class MISApplication extends Application {


    private static MISApplication mInstance;
    private static AWSCommunicationClient communicationClient;

    public void onCreate()
    {
        super.onCreate();
        mInstance=this;
        communicationClient = new AWSCommunicationClient(getApplicationContext());
    }

    public static synchronized MISApplication getInstance()
    {
        return mInstance;
    }

    //MQTT
    static boolean waiting = true;
    public static synchronized AWSCommunicationClient getCommunicationClientInstance() {
        if(!communicationClient.isInitialized()) {
            Log.e("IotApplication","NOT INIT");
            communicationClient.Initialize(new SimpleHandler() {
                @Override
                public void onSuccess() {
                    waiting = false;
                }

                @Override
                public void onError(String ErrorMsg) {
                    waiting = false;
                }
            });
            while(waiting){
                try {
                    sleep(POOLING_SLEEP_SHORT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return communicationClient;
    }

}