package sukriti.ngo.mis.ui.management.lambda.EnterpriseDetails

import com.google.gson.JsonObject

interface EnterpriseDetailsResponseHandler {

    fun onSuccess(response: JsonObject)

    fun onError(message: String)
}