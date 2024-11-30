package sukriti.ngo.mis.ui.management.lambda.PatchDevice

import com.google.gson.JsonObject

interface PatchDeviceLambdaResponse {

    fun onSuccess(response: JsonObject)

    fun onError(message: String)

}