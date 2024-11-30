package sukriti.ngo.mis.ui.super_admin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import org.json.JSONArray
import org.json.JSONObject
import sukriti.ngo.mis.R
import sukriti.ngo.mis.communication.CommunicationHelper
import sukriti.ngo.mis.communication.SimpleHandler
import sukriti.ngo.mis.databinding.ConfigLightBinding
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.IotPublishLambdaRequest
import sukriti.ngo.mis.ui.login.data.UserProfile
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient

class FanConfig : Fragment() {
    private lateinit var binding: ConfigLightBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient : NavigationClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private var selectedTabIndex = 1
    companion object {
        private var INSTANCE: FanConfig? = null

        fun getInstance(): FanConfig {
            return INSTANCE
                ?: FanConfig()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ConfigLightBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()

    }

    private fun init() {
        viewModel =
            ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)
        sharedPrefsClient = SharedPrefsClient(context)

        binding.scopeCont.label.text = "Config Scope"
        binding.enabledCont.label.text = "Automatic Fan"
        binding.onCont.label.text = "Auto On Time"
        binding.offCont.label.text = "Auto Off Time"
        binding.description.text = getString(R.string.description_fan_config)

        val criticalAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            requireContext(),
            R.layout.item_config_selection,
            Nomenclature.getCmsConfigOptions() as List<Any?>
        )
        binding.enabledCont.options.adapter = criticalAdapter
        binding.enabledCont.options.setSelection(0)
        binding.enabledCont.options.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                i: Int,
                l: Long
            ) {
                val selection = Nomenclature.getUcemsConfigOptions()[i]
                if (i == 0) {
                    binding.enabledCont.icon.setBackgroundResource(R.drawable.ic_status_fault)
                    binding.onCont.icon.setImageResource(R.drawable.ic_timer_greyed)
                    binding.offCont.icon.setImageResource(R.drawable.ic_timer_greyed)
                    binding.onCont.time.visibility = View.INVISIBLE
                    binding.offCont.time.visibility = View.INVISIBLE

                    binding.offCont.label.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey_150
                        )
                    )
                    binding.onCont.label.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey_150
                        )
                    )


                } else if (i == 1) {
                    binding.enabledCont.icon.setBackgroundResource(R.drawable.ic_status_working)
                    binding.onCont.icon.setImageResource(R.drawable.ic_timer)
                    binding.offCont.icon.setImageResource(R.drawable.ic_timer)
                    binding.onCont.time.visibility = View.VISIBLE
                    binding.offCont.time.visibility = View.VISIBLE

                    binding.offCont.label.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.primary_text
                        )
                    )
                    binding.onCont.label.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.primary_text
                        )
                    )
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        binding.submit.setOnClickListener(View.OnClickListener {

            var scopeSelection = getSelectedCabinTypes()
            if(isSubmissionValidated()){
                userAlertClient.showWaitDialog("Updating cabin config...")
                var targetClient = getTargetClient()



                val index = binding.enabledCont.options.selectedItemPosition
                val selection = Nomenclature.getCmsConfigOptions()[index]
                val payloadJA = JSONArray()
                val payloadInfoJA = JSONArray()

                for(index  in 0 until scopeSelection.length()){
                    val payloadJO = JSONObject()
                    payloadJO.put("Autofanenabled", selection)
                    payloadJO.put("Fanautoofftimer", binding.offCont.time.editText?.text.toString())
                    payloadJO.put("Fanautoontimer", binding.onCont.time.editText?.text.toString())
                    payloadJO.put("THING_NAME", targetClient+"_ALL")
                    payloadJO.put("cabin_type", Nomenclature.getCabinType(scopeSelection.getString(index)))
                    payloadJO.put("user_type", Nomenclature.getUserType(scopeSelection.getString(index)))
                    payloadJA.put(payloadJO)

                    var payloadInfoJO = CommunicationHelper.getGenericConfigInfo("CMS/FAN",
                        scopeSelection.getString(index),
                        targetClient,
                        sharedPrefsClient.getUserDetails())
                    payloadInfoJA.put(payloadInfoJO)
                }


                var topicName = CommunicationHelper.getTopicName(
                    Nomenclature.PUB_TOPIC.CMS_CONFIG_GENERIC,
                    targetClient
                )

                var request =
                    IotPublishLambdaRequest(
                        topicName,
                        payloadJA.toString(),
                        payloadInfoJA.toString()
                    )
                viewModel.publishGenericConfig(request, publishHandler)
            }

        })

        if (!UserProfile.isClientSpecificRole(sharedPrefsClient.getUserDetails().role)) {
            binding.scopeCont.clientSelectionContainer.visibility = View.VISIBLE
            var clientList = sharedPrefsClient.getClientList()
            clientList.add(0,"Select Client")
            val adapter = ArrayAdapter(requireContext(), R.layout.item_client_selection, clientList)
            binding.scopeCont.clientSelection.adapter = adapter
            binding.scopeCont.clientSelection.setSelection(0)
        }
    }
    var publishHandler: SimpleHandler = object : SimpleHandler {
        override fun onSuccess() {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Success","Updated config submitted successfully.",false)
        }

        override fun onError(ErrorMsg: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!",ErrorMsg,false)
        }

    }

    fun getSelectedCabinTypes():JSONArray {
        var JA = JSONArray()
        if((binding.scopeCont.grid[0] as CheckBox).isChecked){
            JA.put(Nomenclature.CABIN_TYPE_MWC)
        }
        if((binding.scopeCont.grid[1] as CheckBox).isChecked){
            JA.put(Nomenclature.CABIN_TYPE_FWC)
        }
        if((binding.scopeCont.grid[2] as CheckBox).isChecked){
            JA.put(Nomenclature.CABIN_TYPE_PWC)
        }
        if((binding.scopeCont.grid[3] as CheckBox).isChecked){
            JA.put(Nomenclature.CABIN_TYPE_MUR)
        }
        return JA;
    }

    private fun isSubmissionValidated():Boolean{
        var scopeSelection = getSelectedCabinTypes()
        if (!UserProfile.isClientSpecificRole(sharedPrefsClient.getUserDetails().role)) {
            var selectedIndex =  binding.scopeCont.clientSelection.selectedItemPosition
            if(selectedIndex == 0) {
                userAlertClient.showDialogMessage("Validation Error","No client selected selected. Choose a client for whom you would like to trigger this configuration.",false)
               return false
            }
        }
        else if(scopeSelection.length() == 0){
            userAlertClient.showDialogMessage("Validation Error","No scope for the config request selected. Choose at least one item in Config Scope section to proceed",false)
            return false
        }

        return true;
    }

    private fun getTargetClient():String{
        return if (!UserProfile.isClientSpecificRole(sharedPrefsClient.getUserDetails().role)) {
            var selectedIndex =  binding.scopeCont.clientSelection.selectedItemPosition -1
            sharedPrefsClient.getClientList()[selectedIndex]
        }else
            sharedPrefsClient.getUserDetails().organisation.client
    }
}
