package sukriti.ngo.mis.communication

import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.amazonaws.mobileconnectors.iot.*
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import sukriti.ngo.mis.AWSConfig.*
import sukriti.ngo.mis.ui.complexes.data.ConnectionResponse
import sukriti.ngo.mis.utils.SharedPrefsClient
import java.io.UnsupportedEncodingException
import java.util.*
import kotlin.math.log


class ConnectionStatusService : IntentService("ConnectionStatusService") {
    private val TAG: String = "ConnectionStatus"
    var returnCallback: Runnable = Runnable { }


    lateinit var credentialsProvider: CognitoCachingCredentialsProvider
    lateinit var mqttManager: AWSIotMqttManager
    lateinit var sharedPreferences: SharedPrefsClient

    companion object {

        const val POOLING_SLEEP_SHORT = 400 //Milli-Seconds
        private var SUBSCRIBE_STATUS = 0
        private const val SUBSCRIBE_FLAG_CONNECTING = 0
        private const val SUBSCRIBE_FLAG_SUCCESS = 1
        private const val SUBSCRIBE_FLAG_ERR = -1


        var _connectionResponse: MutableLiveData<ConnectionResponse> = MutableLiveData()
        var connectionResponse: LiveData<ConnectionResponse> = _connectionResponse

    }


    var topic = "$" + "aws/events/presence/#"


    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "onCreate: ")
        //        initialize()
        init()
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.i(TAG, "onHandleIntent: ")
        Thread { runOnUiThread(Runnable { connect() }) }.start()
    }


    fun connect() {
        try {
            mqttManager.connect(credentialsProvider) { status, throwable ->
                Log.d(TAG, "Status = $status")
                runOnUiThread {
                    when (status) {
                        AWSIotMqttClientStatus.Connecting -> {
                            Log.i(TAG, "Connecting... ")
                        }
                        AWSIotMqttClientStatus.Connected -> {
                            Log.i(TAG, "Connected... ")
                            subscribe()
                            //publish()
                        }
                        AWSIotMqttClientStatus.Reconnecting -> {
                            if (throwable != null) {
                                Log.e(TAG, "Connection error.", throwable)
                            }
                            Log.i(TAG, "Reconnecting... ")
                        }
                        AWSIotMqttClientStatus.ConnectionLost -> {
                            if (throwable != null) {
                                Log.e(TAG, "Connection error.", throwable)
                                throwable.printStackTrace()
                            }
                            Log.i(TAG, "Disconnected... ")
                        }
                        else -> {
                            Log.i(TAG, "Disconnected... ")
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Connection error.", e)
        }
    }

    fun init() {
        sharedPreferences = SharedPrefsClient(applicationContext)
        val user = sharedPreferences.getUserDetails().user.userName
        val current_timestamp = Calendar.getInstance().timeInMillis.toString()
        val mqttClientID = user + current_timestamp

        credentialsProvider = CognitoCachingCredentialsProvider(
            applicationContext,
            identityPoolID,
            cognitoRegion
        )

        // MQTT Client
        mqttManager = AWSIotMqttManager(mqttClientID, awsIotEndPoint)
    }


    fun publish() {
        Thread {
            Log.i(TAG, "publish: yrt to publish ")
            mqttManager?.publishString("Hello Publish", topic, AWSIotMqttQos.QOS1)
            Log.i(TAG, "publised ")

        }.start()
    }

    fun subscribe() {
        Log.i(TAG, "Subscribe: ")
        SUBSCRIBE_STATUS = SUBSCRIBE_FLAG_CONNECTING
        Thread {
            val handler = Handler(applicationContext.mainLooper)
            try {
                Log.i(TAG, "try: ")
                mqttManager?.subscribeToTopic(
                    topic, AWSIotMqttQos.QOS1, object : AWSIotMqttSubscriptionStatusCallback {
                        override fun onSuccess() {
                            SUBSCRIBE_STATUS = SUBSCRIBE_FLAG_SUCCESS
                            returnCallback = Runnable {
                                Log.i(TAG, "run: success")
//                                callback.onSuccess()
                            }
                        }

                        override fun onFailure(exception: Throwable) {
                            SUBSCRIBE_STATUS = SUBSCRIBE_FLAG_ERR
                            returnCallback = Runnable {
                                Log.e(TAG, exception.message)
//                                callback.onError(exception.message)
                            }
                        }
                    }, awsIotMqttNewMessageCallback
                )
                Log.i(TAG, "try: out ")
            } catch (e: Exception) {
                SUBSCRIBE_STATUS = SUBSCRIBE_FLAG_ERR
                e.printStackTrace()
                Log.i(TAG, "Exception: " + e)
            }
            while (SUBSCRIBE_STATUS == SUBSCRIBE_FLAG_CONNECTING) {
                try {
                    Thread.sleep(POOLING_SLEEP_SHORT.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                Log.i(TAG, "SUBSCRIBE_STATUS:  " + SUBSCRIBE_STATUS)
            }
            handler.post(returnCallback)
        }.start()
    }

    val awsIotMqttNewMessageCallback =
        AWSIotMqttNewMessageCallback { topic, data ->
            Log.d(TAG, "Topic: $topic")
            var message: String? = null
            try {
                message = String(data, charset("UTF-8"))
                Log.d(TAG, "Message: $message")
                val payload = JSONObject(message)
                messageToModel(payload)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

    fun unsubscribe() {
        Log.i(TAG, "unsubscribe: ")
        try {
            mqttManager.unsubscribeTopic(topic)
        } catch(exception: Exception) {
            Log.e(TAG, "error while unsubscribing")
        }
    }

    private fun disConnect() {
        Log.i(TAG, "disConnect: ")
        unsubscribe()
        mqttManager.disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
        disConnect()
    }

    fun messageToModel(payload: JSONObject) {
        val gson = Gson()
        val type = object : TypeToken<ConnectionResponse?>() {}.type
        val response: ConnectionResponse = gson.fromJson(payload.toString(), type)
        Log.i(TAG, "messageToModel: " + Gson().toJson(response))
        _connectionResponse.postValue(response)

    }

}