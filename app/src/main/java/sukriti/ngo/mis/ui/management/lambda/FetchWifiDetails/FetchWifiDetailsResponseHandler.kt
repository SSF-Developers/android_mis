package sukriti.ngo.mis.ui.management.lambda.FetchWifiDetails

import com.google.gson.JsonObject

interface FetchWifiDetailsResponseHandler {

    fun onSuccess(response: FetchWifiDetailsResponse)

    fun onError(message: String)
}