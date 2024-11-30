package sukriti.ngo.mis.ui.management.lambda.FetchWifiDetails

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import com.google.gson.JsonObject

interface FetchWifiDetails {

    @LambdaFunction
    fun fetchWifiDetails() : FetchWifiDetailsResponse
}