package sukriti.ngo.mis.ui.management

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Policy
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.PolicyListItem
import sukriti.ngo.mis.ui.management.data.Enterprise
import sukriti.ngo.mis.ui.management.data.device.Device
import sukriti.ngo.mis.ui.management.lambda.CreateEnterprise.CreateEnterpriseLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.CreateEnterprise.CreateEnterpriseResponseHandler
import sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise.DeleteEnterpriseLambdaRequest
import sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise.DeleteEnterpriseLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise.DeleteEnterpriseResponseHandler
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaRequest
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponseHandler
import sukriti.ngo.mis.ui.management.lambda.EnterpriseDetails.EnterpriseDetailsResponseHandler
import sukriti.ngo.mis.ui.management.lambda.ListDevices.ListDeviceLambdaRequest
import sukriti.ngo.mis.ui.management.lambda.ListDevices.ListDeviceLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.ListDevices.ListDeviceResponseHandler
import sukriti.ngo.mis.ui.management.lambda.ListEnterprise.ListEnterpriseResponse
import sukriti.ngo.mis.ui.management.lambda.ListEnterprise.ListEnterpriseResponseHandler
import sukriti.ngo.mis.ui.management.lambda.PatchDevice.PatchDeviceLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.PatchEnterprise.PatchEnterpriseLambdaResponse
import sukriti.ngo.mis.utils.LambdaClient
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset

class ManagementViewModel(application: Application): AndroidViewModel(application) {

    private var lambdaClient: LambdaClient
    var enterpriseList : ArrayList<Enterprise>
    var devicesList: ArrayList<Device>
    var policyList: ArrayList<Policy>
    var policyListItems : ArrayList<PolicyListItem>
    val deleteEnterpriseList = mutableListOf<Int>()
    var deviceToDelete: Device? = null
    private var selectedEnterprise = Enterprise()
    private var selectedDevice = Device()


    companion object {
        const val NAV_MANAGE_ENTERPRISE = 0
    }

    init {
        lambdaClient = LambdaClient(application.applicationContext)
        enterpriseList = ArrayList()
        devicesList = ArrayList()
        policyList = ArrayList()
        policyListItems = ArrayList()
    }

    fun getEnterpriseList(callback: ListEnterpriseResponseHandler) {

        val lambdaCallback = object: ListEnterpriseResponseHandler {
            override fun onSuccess(response: ListEnterpriseResponse?) {
                if(response != null) {
                    if(response.statusCode == 400) {
                        callback.onError("No Enterprise Found")
                    } else {
                        if(response.body != null) {
                            if(response.body.enterprises != null) {
                                enterpriseList = response.body.enterprises as ArrayList<Enterprise>
                                callback.onSuccess(response)
                            } else {
                                callback.onError("No Enterprise Found")
                            }
                        } else {
                            callback.onError("Body is null")
                        }
                    }

                } else {
                    callback.onError("Response is null")
                }
            }

            override fun onError(message: String?) {
                callback.onError(message)
            }

        }

        lambdaClient.ExecuteListEnterpriseLambda(lambdaCallback)
    }

    fun getDeviceList(request: ListDeviceLambdaRequest, callback: ListDeviceResponseHandler) {
        val lambdaCallback = object: ListDeviceResponseHandler {
            override fun onSuccess(response: ListDeviceLambdaResponse) {
                Log.i("getDeviceList", "onSuccess: ${Gson().toJson(response)}")
                if(response.statusCode == 400) {
                    devicesList.clear()
                    callback.onError("No Device Found")
                } else {
                    if(response.body != null ) {
                        if(response.body != null) {
                            devicesList = response.body as ArrayList<Device>
                            Log.i("getDeviceList", "Size: ${devicesList.size}")
                            callback.onSuccess(response)
                        } else {
                            //
                            devicesList.clear()
                            callback.onError("No Device Found")
                        }
                    } else {
                        devicesList.clear()
                        callback.onError("Body is null")
                    }
                }

            }


            override fun onError(message: String?) {
                callback.onError(message)
            }

        }

        request.enterpriseId = getSelectedEnterprise().name
        Log.d("getDeviceList", "getDeviceList() called with: request = ${Gson().toJson(request)}")

        lambdaClient.ExecuteListDeviceLambda(request, lambdaCallback)
    }

    fun getPoliciesList(callback: ListPolicyLambdaResponseHandler) {

        val lambdaCallback: ListPolicyLambdaResponseHandler = object : ListPolicyLambdaResponseHandler {
            override fun onSuccess(response: ListPolicyLambdaResponse) {
                if (response.statusCode == 200) {
                    Log.i("listPolicy", "Status Code: 200")
                    if(response.body.isNotEmpty()) {
                        Log.i("listPolicy", "Status Code: Body not empty")
                        for (i in response.body.indices) {
                            Log.i("listPolicy", "Policies: " + Gson().toJson(response.body[i]))
                        }
//                        policyList.clear()
                        Log.i("listPolicy", "Policy list cleared")
//                        policyList = ArrayList(response.body)
                        policyListItems = ArrayList(response.body)
                        Log.i("listPolicy", "Policy list updated")
                        callback.onSuccess(response)
                    }
                    else {
                        Log.i("listPolicy", "Status Code 200 & body is empty")
//                        policyList.clear()
                        policyListItems.clear()
                        callback.onError("No policies found")
                    }
                } else {
                    Log.e("listPolicy", "Status Code ${response.statusCode}")
//                    policyList.clear()
                    policyListItems.clear()
                    Log.e("listPolicy", "Status Code ${response.statusCode} Policy cleared")
                    callback.onError("Something went wrong")
                }
            }

            override fun onError(message: String) {
                callback.onError(message)
           }
        }


        val request = ListPolicyLambdaRequest(getSelectedEnterprise().name)
        lambdaClient.ExecuteListPolicyLambda(request, lambdaCallback)

    }

    fun getEnterpriseDetails(name: String, callback: EnterpriseDetailsResponseHandler) {
        val request = JsonObject()
        request.addProperty("enterpriseId", name)
        lambdaClient.ExecuteEnterpriseDetailsLambda(request, callback)
    }

    fun writePolicyToFile(response: ListPolicyLambdaResponse, context: Context?) {
        Log.d("writePolicy", "writePolicyToFile() called with: response = $response")
        val logFile = File(context?.filesDir, "PoliciesData.txt")
        if(!logFile.exists()) {
            logFile.createNewFile()
            Log.d("writePolicy", "File not existed, now created")
        }else {
            Log.d("writePolicy", "File already existed")
        }

        try {
            Log.i("writePolicy", "Inside Try block")
            val json = Gson().toJson(response)
            Log.i("writePolicy", "JSON Created")
            val outputStream = FileOutputStream(logFile)
            Log.i("writePolicy", "File Output Stream created")
            outputStream.write(json.toString().toByteArray(Charset.defaultCharset()))
            Log.i("writePolicy", "Output written successfully")
            outputStream.close()
            Log.i("writePolicy", "Output stream closed")
        } catch (exception: IOException) {
            exception.message?.let { Log.i("writePolicy", it) }
        }
    }

    fun createEnterprise(callback: ManageEnterprises.createEnterpriseCall) {
        val lambdaCallback = object : CreateEnterpriseResponseHandler {
            override fun onSuccess(response: CreateEnterpriseLambdaResponse) {
                Log.i("createEnterprise", "onSuccess: ${Gson().toJson(response)}")
                val url = Uri.parse(response.body.signupUrl)
                Log.i("createEnterprise", "onSuccess: URL = $url")
                val intent = Intent(Intent.ACTION_VIEW, url)
                callback.onSuccess(intent, response.body.signupUrl )
            }

            override fun onError(message: String?) {
                callback.onError(message)
            }

        }

        lambdaClient.ExecuteCreateEnterpriseLambda(lambdaCallback)
    }

    fun deleteEnterprise(callback: ManageEnterprises.deleteEnterpriseCall) {
        val lambdaCallback = object : DeleteEnterpriseResponseHandler {
            override fun onSuccess(response: DeleteEnterpriseLambdaResponse) {
                Log.i("deleteEnterprise", "onSuccess: ${response.body}")
                callback.onSuccess(response.body)
            }

            override fun onError(message: String?) {
                if(message != null) {
                    Log.e("deleteEnterprise", "onError: $message")
                    callback.onError(message)
                } else {
                    Log.e("deleteEnterprise", "onError: message is null")
                    callback.onError("Something went wrong")
                }
            }

        }
        val request = DeleteEnterpriseLambdaRequest()
        request.name = enterpriseList[deleteEnterpriseList[0]].name
        lambdaClient.ExecuteDeleteEnterpriseLambda(request, lambdaCallback)
    }

    fun setSelectedEnterprise(enterprise: Enterprise) {
        selectedEnterprise = enterprise
    }

    fun getSelectedEnterprise(): Enterprise {
        return selectedEnterprise
    }

    fun setSelectedDevice(device: Device) {
        selectedDevice = device
    }

    fun patchDevice(requestBody: JsonObject, deviceId: String?, serialNumber: String?, callback: PatchDeviceLambdaResponse) {

        val payload = JsonObject()
        payload.addProperty("command", "patch_device")
        payload.addProperty("enterpriseId", getSelectedEnterprise().name)
        payload.addProperty("serial_number", serialNumber)
        payload.addProperty("deviceId", deviceId)
        payload.add("requestBody", requestBody)

        Log.i("patchDevice", "Payload: $payload" )

        lambdaClient.ExecutePatchDeviceLambda(payload, object: PatchDeviceLambdaResponse {
            override fun onSuccess(response: JsonObject) {
                val statusCode = response.get("statusCode").asInt
                if(statusCode == 200) {
                    callback.onSuccess(response)
                } else {
                    val errorMessage = response.get("body").asString
                    callback.onError(errorMessage)
                }
            }

            override fun onError(message: String) {
                callback.onError(message)
            }
        })
    }

    fun patchEnterprise(requestBody: JsonObject, callback: PatchEnterpriseLambdaResponse) {

        lambdaClient.ExecutePatchEnterpriseLambda(requestBody, object: PatchEnterpriseLambdaResponse {
            override fun onSuccess(response: JsonObject) {
                val statusCode = response.get("statusCode").asInt
                if(statusCode == 200) {
                    callback.onSuccess(response)
                } else {
                    val errorMessage = response.get("body").asString
                    callback.onError(errorMessage)
                }
            }

            override fun onError(message: String) {
                callback.onError(message)
            }
        })
    }

    fun getSelectedDevice(): Device {
        return selectedDevice
    }



}