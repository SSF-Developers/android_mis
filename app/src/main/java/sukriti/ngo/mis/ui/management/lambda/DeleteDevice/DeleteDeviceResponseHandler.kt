package sukriti.ngo.mis.ui.management.lambda.DeleteDevice

import com.google.gson.JsonObject

interface DeleteDeviceResponseHandler {
    fun onSuccess(response: JsonObject)

    fun onError(message: String)
}