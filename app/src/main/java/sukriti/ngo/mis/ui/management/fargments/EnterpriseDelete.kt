package sukriti.ngo.mis.ui.management.fargments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import sukriti.ngo.mis.R
import sukriti.ngo.mis.communication.SimpleHandler
import sukriti.ngo.mis.databinding.FragmentEnterpriseDeleteBinding
import sukriti.ngo.mis.interfaces.DialogSingleActionHandler
import sukriti.ngo.mis.ui.management.ManagementViewModel
import sukriti.ngo.mis.ui.management.adapters.DeleteEnterpriseDeviceListAdapter
import sukriti.ngo.mis.ui.management.data.device.Device
import sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise.DeleteEnterpriseLambdaRequest
import sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise.DeleteEnterpriseLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise.DeleteEnterpriseResponseHandler
import sukriti.ngo.mis.ui.management.lambda.EnterpriseDetails.EnterpriseDetailsResponseHandler
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.UserAlertClient

class EnterpriseDelete(
    val managementViewModel: ManagementViewModel
): DialogFragment() {

    lateinit var binding: FragmentEnterpriseDeleteBinding
    lateinit var userAlertClient: UserAlertClient
    lateinit var lambdaClient: LambdaClient
    lateinit var adapter: DeleteEnterpriseDeviceListAdapter
    var devicesList = ArrayList<Device>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnterpriseDeleteBinding.inflate(inflater)

        userAlertClient = UserAlertClient(activity)
        lambdaClient = LambdaClient(context)

        fetchDevices()

        binding.delete.setOnClickListener {
            delete()
        }

        binding.confirmDeleteEditText.addTextChangedListener(textChangeWatcher)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogFragmentStyle2)
    }

    private fun fetchDevices() {
        val request = JsonObject()
        request.addProperty("enterpriseId", managementViewModel.getSelectedEnterprise().name)
        userAlertClient.showWaitDialog("Fetching Devices")
        lambdaClient.ExecuteEnterpriseDetailsLambda(request, fetchDeviceCallback)
    }

    private val fetchDeviceCallback = object: EnterpriseDetailsResponseHandler {
        override fun onSuccess(response: JsonObject) {
            userAlertClient.closeWaitDialog()
            val statusCode = response.get("statusCode").asInt
            if(statusCode == 200) {
                val body = response.get("body").asJsonObject
                val list = body.get("DynamoDB").asJsonArray
                list.forEach {
                    Log.i("deleteEnterprise", "Device: ${Gson().toJson(it)}" )
                    devicesList.add(Gson().fromJson(it, Device::class.java))
                }
                setAdapter()
            }
            else {
                userAlertClient.showDialogMessage("Error", "Something went wrong", false)
            }
        }

        override fun onError(message: String) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error", message, false)
        }
    }

    private fun setAdapter() {
        adapter = DeleteEnterpriseDeviceListAdapter(devicesList)
        binding.devicesListRecyclerView.adapter = adapter
        binding.devicesListRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    }

    private val textChangeWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            if (editable.toString().compareTo("DELETE", ignoreCase = true) == 0) {
                binding.delete.visibility = View.VISIBLE
            } else {
                binding.delete.visibility = View.INVISIBLE
            }
        }
    }

    fun delete() {
        userAlertClient.showWaitDialog("Deleting Enterprise")
        val deleteRequest = DeleteEnterpriseLambdaRequest()
        deleteRequest.name = managementViewModel.getSelectedEnterprise().name
        deleteRequest.soft_delete = "true"
        lambdaClient.ExecuteDeleteEnterpriseLambda(deleteRequest, object:
            DeleteEnterpriseResponseHandler {
            override fun onSuccess(response: DeleteEnterpriseLambdaResponse) {
                userAlertClient.closeWaitDialog()
                if(response.statusCode == 200) {
                    userAlertClient.showDialogMessage("Delete successful", "Enterprise deleted successfully"
                    ) { dismiss() }
                }
                else {
                    userAlertClient.showDialogMessage("Delete failed", "Enterprise deletion failed"
                    ) { dismiss() }
                }
            }

            override fun onError(message: String?) {
                userAlertClient.closeWaitDialog()
                userAlertClient.showDialogMessage("Error", message, false)
            }
        })

    }

    private val deleteEnterpriseCallback  = object: SimpleHandler {
        override fun onSuccess() {
            // call delete lambda
        }

        override fun onError(message: String?) {
        }
    }

}