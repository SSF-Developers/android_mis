package sukriti.ngo.mis.ui.dashboard.fragments.quickConfig

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import kotlinx.android.synthetic.main.login.*
import org.json.JSONArray
import org.json.JSONObject
import sukriti.ngo.mis.R
import sukriti.ngo.mis.communication.CommunicationHelper
import sukriti.ngo.mis.communication.SimpleHandler
import sukriti.ngo.mis.databinding.ConfigUsageChargeBinding
import sukriti.ngo.mis.interfaces.ClientNameListHandler
import sukriti.ngo.mis.repository.entity.QuickAccess
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.adapters.CommandsAdapter
import sukriti.ngo.mis.ui.complexes.data.MisCommand
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.IotPublishLambdaRequest
import sukriti.ngo.mis.ui.login.data.UserProfile
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities
import java.util.ArrayList

class HomeUsageChargeConfig : DialogFragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: ConfigUsageChargeBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var commandsAdapter: CommandsAdapter
    private lateinit var quickAccess: QuickAccess
    private var _tag: String = "_ConfigDataReq"

    companion object {
        private var INSTANCE: HomeUsageChargeConfig? = null

        fun getInstance(): HomeUsageChargeConfig {
            return INSTANCE
                ?: HomeUsageChargeConfig()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,

        savedInstanceState: Bundle?
    ): View? {
        binding = ConfigUsageChargeBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

    private fun init() {
        Log.i(_tag, "init()")
        sharedPrefsClient = SharedPrefsClient(context)
        quickAccess = sharedPrefsClient.getQuickAccessSelection()
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(CabinViewModel::class.java)

        binding.back.setOnClickListener(View.OnClickListener {
            dismiss()
        })

        binding.scopeCont.label.text = "Config Scope"
        binding.chargeTypeCont.label.text = "Payment Mode"
        binding.usageChargeCont.label.text = "Usage Charge"
        binding.description.text = getString(R.string.description_usage_charge_config)
        val criticalAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            requireContext(),
            R.layout.item_config_selection,
            Nomenclature.getPaymentModesOptions() as List<Any?>
        )

        binding.chargeTypeCont.options.adapter = criticalAdapter
        binding.chargeTypeCont.options.setSelection(0)
        binding.chargeTypeCont.options.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                i: Int,
                l: Long
            ) {
                val selection = Nomenclature.getPaymentModesOptions()[i]

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        binding.submit.setOnClickListener(View.OnClickListener {

            var scopeSelection = getSelectedCabinTypes()
            if (isSubmissionValidated()) {
                userAlertClient.showWaitDialog("Updating cabin config...")

                var targetClient = getTargetClient()

                Log.i("__ExecuteLambda", "init: targetClient : $targetClient")

                val index = binding.chargeTypeCont.options.selectedItemPosition
                val selection = Nomenclature.getPaymentModesOptions()[index]
                val payloadJA = JSONArray()
                val payloadInfoJA = JSONArray()

                for (index in 0 until scopeSelection.length()) {
                    val payloadJO = JSONObject()
                    payloadJO.put("Cabinpaymentmode", selection)
                    payloadJO.put(
                        "Entrychargeamount",
                        binding.usageChargeCont.amount.editText?.text.toString()
                    )
                    payloadJO.put("THING_NAME", targetClient + "_ALL")
                    payloadJO.put(
                        "cabin_type",
                        Nomenclature.getCabinType(scopeSelection.getString(index))
                    )
                    payloadJO.put(
                        "user_type",
                        Nomenclature.getUserType(scopeSelection.getString(index))
                    )
                    payloadJA.put(payloadJO)

                    var payloadInfoJO = CommunicationHelper.getGenericConfigInfo(
                        "UCEMS/USAGE-CHARGE",
                        scopeSelection.getString(index),
                        targetClient,
                        sharedPrefsClient.getUserDetails()
                    )
                    payloadInfoJA.put(payloadInfoJO)
                }


                var topicName = CommunicationHelper.getTopicName(
                    Nomenclature.PUB_TOPIC.UCEMS_CONFIG_GENERIC,
                    targetClient
                )

                var request =
                    IotPublishLambdaRequest(
                        topicName,
                        payloadJA.toString(),
                        payloadInfoJA.toString()
                    )
                Log.i("__ExecuteLambda", "init: "+Gson().toJson(request))


                viewModel.publishGenericConfig(request, publishHandler)
            }
        })

        if (!UserProfile.isClientSpecificRole(sharedPrefsClient.getUserDetails().role)) {
            binding.scopeCont.clientSelectionContainer.visibility = View.VISIBLE
            var clientList = sharedPrefsClient.getClientList()
            clientList.add(0, "Select Client")
            val adapter = ArrayAdapter(requireContext(), R.layout.item_client_selection, clientList)
            binding.scopeCont.clientSelection.adapter = adapter
            binding.scopeCont.clientSelection.setSelection(0)
        }
    }

    var publishHandler: SimpleHandler = object : SimpleHandler {
        override fun onSuccess() {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage(
                "Success",
                "Updated config submitted successfully.",
                false
            )
        }

        override fun onError(ErrorMsg: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!", ErrorMsg, false)
        }

    }

    fun getSelectedCabinTypes(): JSONArray {
        var JA = JSONArray()
        if ((binding.scopeCont.grid[0] as CheckBox).isChecked) {
            JA.put(Nomenclature.CABIN_TYPE_MWC)
        }
        if ((binding.scopeCont.grid[1] as CheckBox).isChecked) {
            JA.put(Nomenclature.CABIN_TYPE_FWC)
        }
        if ((binding.scopeCont.grid[2] as CheckBox).isChecked) {
            JA.put(Nomenclature.CABIN_TYPE_PWC)
        }
        if ((binding.scopeCont.grid[3] as CheckBox).isChecked) {
            JA.put(Nomenclature.CABIN_TYPE_MUR)
        }
        return JA;
    }

    private fun isSubmissionValidated(): Boolean {
        var scopeSelection = getSelectedCabinTypes()
        if (!UserProfile.isClientSpecificRole(sharedPrefsClient.getUserDetails().role)) {
            var selectedIndex = binding.scopeCont.clientSelection.selectedItemPosition
            if (selectedIndex == 0) {
                userAlertClient.showDialogMessage(
                    "Validation Error",
                    "No client selected selected. Choose a client for whom you would like to trigger this configuration.",
                    false
                )
                return false
            }
        } else if (scopeSelection.length() == 0) {
            userAlertClient.showDialogMessage(
                "Validation Error",
                "No scope for the config request selected. Choose at least one item in Config Scope section to proceed",
                false
            )
            return false
        }

        return true;
    }

    private fun getTargetClient(): String {
        return if (!UserProfile.isClientSpecificRole(sharedPrefsClient.getUserDetails().role)) {
            var selectedIndex = binding.scopeCont.clientSelection.selectedItemPosition-1
            Log.i("__ExecuteLambda", "selectedIndex: "+ selectedIndex)
            Log.i("__ExecuteLambda", "List: "+Gson().toJson(sharedPrefsClient.getClientList()))
            sharedPrefsClient.getClientList()[selectedIndex]
        } else {
            Log.i(
                "__ExecuteLambda",
                "organisation.client: " + sharedPrefsClient.getUserDetails().organisation.client
            )
            sharedPrefsClient.getUserDetails().organisation.client
        }
    }
}
