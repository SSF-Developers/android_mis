package sukriti.ngo.mis.ui.dashboard.fragments.quickAccess

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import sukriti.ngo.mis.adapters.QuickCommandsAdapter
import sukriti.ngo.mis.communication.CommunicationHelper
import sukriti.ngo.mis.communication.SimpleHandler
import sukriti.ngo.mis.databinding.QuickAccessCommandsBinding
import sukriti.ngo.mis.repository.entity.QuickAccess
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.data.MisCommand
import sukriti.ngo.mis.ui.complexes.data.PayloadMetaData
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.IotPublishLambdaRequest
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.Nomenclature.PUB_TARGET_CABIN
import sukriti.ngo.mis.utils.Nomenclature.PUB_TYPE_COMMAND
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient

class QuickAccessCommands : DialogFragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: QuickAccessCommandsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var commandsAdapter: QuickCommandsAdapter
    private lateinit var quickAccess: QuickAccess
    private var _tag: String = "_QCabinCommands"

    companion object {
        private var INSTANCE: QuickAccessCommands? = null

        fun getInstance(): QuickAccessCommands {
            return INSTANCE
                ?: QuickAccessCommands()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = QuickAccessCommandsBinding.inflate(layoutInflater)
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

        binding.grid.visibility = View.VISIBLE
        val gridLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.grid.layoutManager = gridLayoutManager
        commandsAdapter = QuickCommandsAdapter(context, Nomenclature.getQuickAccessCommandList())
        commandsAdapter.setCommandSubmitHandler(commandSubmitHandler)

        binding.grid.adapter = commandsAdapter
        binding.grid.setItemViewCacheSize(commandsAdapter.itemCount);

        binding.back.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }

    private var commandSubmitHandler:QuickCommandsAdapter.CommandSubmitHandler = object : QuickCommandsAdapter.CommandSubmitHandler{
        override fun onSubmit(command: MisCommand?) {
            userAlertClient.showWaitDialog("Submitting command configuration...")
            var shortThingName = quickAccess.getCabin().ShortThingName
            var metadata = PayloadMetaData(quickAccess.getCabin().ThingName,Nomenclature.getCabinType(shortThingName),Nomenclature.getUserType(shortThingName))
            var payloadJO = CommunicationHelper.getCommandPayload(command,metadata)
            var payloadInfoJo = CommunicationHelper.getCommandInfo(quickAccess.getCabin(),sharedPrefsClient.getUserDetails())
            var topicName = CommunicationHelper.getTopicName(
                Nomenclature.PUB_TOPIC.COMMAND,
                quickAccess.getComplex(),
                quickAccess.getCabin()
            )
            Log.i("__submit", "topicName: $topicName")
            Log.i("__submit", "configDataJo: $payloadJO")

            var request =
                IotPublishLambdaRequest(
                    topicName,
                    payloadJO.toString(),
                    payloadInfoJo.toString()
                )
            viewModel.publishCommand(request, publishHandler)
        }
    }

    var publishHandler: SimpleHandler = object : SimpleHandler {
        override fun onSuccess() {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Success","Command config submitted successfully.",false)
        }

        override fun onError(ErrorMsg: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!",ErrorMsg,false)
        }
    }
}
