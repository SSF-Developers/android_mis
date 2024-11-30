package sukriti.ngo.mis.ui.management.EnrollDevice.Lambda.DDBSaveLambda

import com.google.gson.JsonObject

interface DynamoDbDataWriterResponseCallback {

    fun onSuccess(response: JsonObject)

    fun onError(message: String)
}