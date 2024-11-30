package sukriti.ngo.mis.ui.complexes.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import sukriti.ngo.mis.communication.CommunicationHelper
import sukriti.ngo.mis.communication.SimpleHandler
import sukriti.ngo.mis.databinding.CabinHealthBinding
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.adapters.CommandsAdapter
import sukriti.ngo.mis.ui.complexes.data.MisCommand
import sukriti.ngo.mis.ui.complexes.data.PayloadMetaData
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.IotPublishLambdaRequest
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient

class CabinCommands : Fragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinHealthBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var commandsAdapter: CommandsAdapter
    private var _tag: String = "_CabinCommands"

    companion object {
        private var INSTANCE: CabinCommands? = null

        fun getInstance(): CabinCommands {
            return INSTANCE
                ?: CabinCommands()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CabinHealthBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    private fun init() {
        Log.i(_tag, "init()")
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(CabinViewModel::class.java)

        binding.grid.visibility = View.VISIBLE
        binding.noDataContainer.visibility = View.GONE
        binding.timeStamp.text = ""
        val gridLayoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.grid.layoutManager = gridLayoutManager

       val  data = if (Nomenclature.getCabinType(Nomenclature.CABIN_TYPE_BWT, viewModel.forCabin.substring(20, 23)))
            Nomenclature.getBWTCommandList()
        else
            Nomenclature.getCommandList()
        
        commandsAdapter = CommandsAdapter(context,data)
        commandsAdapter.setCommandSubmitHandler(commandSubmitHandler)

        binding.grid.adapter = commandsAdapter
        binding.grid.setItemViewCacheSize(commandsAdapter.itemCount);
    }

    private var commandSubmitHandler: CommandsAdapter.CommandSubmitHandler =
        CommandsAdapter.CommandSubmitHandler { command ->
            userAlertClient.showWaitDialog("Submitting command configuration...")
            val shortThingName =  viewModel.getSelectedCabin().ShortThingName
            val metadata = PayloadMetaData( viewModel.getSelectedCabin().ThingName,Nomenclature.getCabinType(shortThingName),Nomenclature.getUserType(shortThingName))
            val payloadJo = CommunicationHelper.getCommandPayload(command,metadata)
            val payloadInfoJo = CommunicationHelper.getCommandInfo(
                viewModel.getSelectedCabin(),
                sharedPrefsClient.getUserDetails()
            )
            val topicName = CommunicationHelper.getTopicName(
                Nomenclature.PUB_TOPIC.COMMAND,
                viewModel.getSelectedComplex(),
                viewModel.getSelectedCabin()
            )
            Log.i("__submit", "topicName: $topicName")
            Log.i("__submit", "configDataJo: $payloadJo")

            //viewModel.publishPayload(requireContext(),topicName,configDataJo,publishHandler)
            val request = IotPublishLambdaRequest(
                topicName,
                payloadJo.toString(),
                payloadInfoJo.toString()
            )
            Log.i("__submit", "request generated successfully")
            Log.i("__submit", "publishing command")
            viewModel.publishCommand(request, publishHandler)
        }

    var publishHandler: SimpleHandler = object : SimpleHandler {
        override fun onSuccess() {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage(
                "Success",
                "Command config submitted successfully.",
                false
            )
            commandsAdapter.respondToSuccessfulSubmit()
        }

        override fun onError(ErrorMsg: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!", ErrorMsg, false)
        }

    }
}
