package sukriti.ngo.mis.ui.dashboard.fragments.quickConfig

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import sukriti.ngo.mis.R
import sukriti.ngo.mis.communication.CommunicationHelper
import sukriti.ngo.mis.communication.SimpleHandler
import sukriti.ngo.mis.databinding.ConfigDataRequestBinding
import sukriti.ngo.mis.repository.entity.QuickAccess
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.adapters.CommandsAdapter
import sukriti.ngo.mis.ui.complexes.data.MisCommand
import sukriti.ngo.mis.ui.login.data.UserProfile
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient

class HomeDataRequestConfig : DialogFragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: ConfigDataRequestBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var commandsAdapter: CommandsAdapter
    private lateinit var quickAccess: QuickAccess
    private var _tag: String = "_ConfigDataReq"

    companion object {
        private var INSTANCE: HomeDataRequestConfig? = null

        fun getInstance(): HomeDataRequestConfig {
            return INSTANCE
                ?: HomeDataRequestConfig()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,

        savedInstanceState: Bundle?
    ): View? {
        binding = ConfigDataRequestBinding.inflate(layoutInflater)
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

        if (!UserProfile.isClientSpecificRole(sharedPrefsClient.getUserDetails().role)) {
            binding.contScopeCont.visibility = View.VISIBLE
            binding.scopeCont.clientSelectionContainer.visibility = View.VISIBLE
            var clientList = sharedPrefsClient.getClientList()
            clientList.add(0,"Select Client")
            val adapter = ArrayAdapter(requireContext(), R.layout.item_client_selection, clientList)
            binding.scopeCont.clientSelection.adapter = adapter
            binding.scopeCont.clientSelection.setSelection(0)
        }
    }

    private var commandSubmitHandler:CommandsAdapter.CommandSubmitHandler = object : CommandsAdapter.CommandSubmitHandler{
        override fun onSubmit(command: MisCommand?) {
            userAlertClient.showWaitDialog("Submitting command configuration...")
//            var configDataJo = CommunicationHelper.getCommandPayload(command,sharedPrefsClient.getUserDetails().user.userName)
//            var topicName = CommunicationHelper.getTopicName(
//                Nomenclature.PUB_TOPIC.COMMAND,
//                quickAccess.getComplex(),
//                quickAccess.getCabin()
//            )
//            Log.i("__submit", "topicName: $topicName")
//            Log.i("__submit", "configDataJo: $configDataJo")
//
//            viewModel.publishPayload(requireContext(),topicName,configDataJo,publishHandler)
        }
    }

    var publishHandler: SimpleHandler = object : SimpleHandler {
        override fun onSuccess() {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Success","Command config submitted successfully.",false)
            commandsAdapter.respondToSuccessfulSubmit()
        }

        override fun onError(ErrorMsg: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!",ErrorMsg,false)
        }

    }
}
