package sukriti.ngo.mis.ui.complexes.fragments.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import sukriti.ngo.mis.communication.CommunicationHelper
import sukriti.ngo.mis.communication.SimpleHandler
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.databinding.CabinDetailsBinding
import sukriti.ngo.mis.databinding.CabinHealthBinding
import sukriti.ngo.mis.databinding.ComplexesHomeBinding
import sukriti.ngo.mis.repository.entity.Cabin
import sukriti.ngo.mis.repository.entity.Complex
import sukriti.ngo.mis.repository.utils.DateConverter
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData
import sukriti.ngo.mis.ui.administration.interfaces.ProvisioningTreeRequestHandler
import sukriti.ngo.mis.ui.administration.interfaces.TreeInteractionListener
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.ComplexesViewModel
import sukriti.ngo.mis.ui.complexes.adapters.*
import sukriti.ngo.mis.ui.complexes.data.*
import sukriti.ngo.mis.ui.complexes.interfaces.CabinComplexSelectionHandler
import sukriti.ngo.mis.ui.complexes.interfaces.ComplexDetailsRequestHandler
import sukriti.ngo.mis.ui.complexes.interfaces.PropertiesListRequestHandler
import sukriti.ngo.mis.ui.dashboard.repository.lambda.mqttPublish.IotPublishLambdaRequest
import sukriti.ngo.mis.ui.login.data.UserProfile
import sukriti.ngo.mis.ui.login.data.UserProfile.Companion.isClientSpecificRole
import sukriti.ngo.mis.ui.profile.ProfileViewModel
import sukriti.ngo.mis.ui.profile.interfaces.UserProfileRequestHandler
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities

class Config_ODS : Fragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinHealthBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var configData: MutableList<PropertyNameValueData>
    private var _tag: String = "_CabinDetails"

    companion object {
        private var INSTANCE: Config_ODS? = null

        fun getInstance(): Config_ODS {
            return INSTANCE
                ?: Config_ODS()
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

//        viewModel.getCabinOdsConfig(viewModel.getSelectedCabin().ThingName, requestHandler)
        viewModel.odsConfig.observe(viewLifecycleOwner,odsObserver)

        //Listeners
        binding.submit.setOnClickListener(View.OnClickListener {

            if (UserProfile.hasWriteAccess(sharedPrefsClient.getUserDetails().role)) {
                userAlertClient.showWaitDialog("Updating cabin config...")
                var shortThingName = viewModel.getSelectedCabin().ShortThingName
                var metadata = PayloadMetaData(
                    viewModel.getSelectedCabin().ThingName,
                    Nomenclature.getCabinType(shortThingName),
                    Nomenclature.getUserType(shortThingName)
                )
                var payloadJO = CommunicationHelper.getOdsPayload(configData, metadata)
                var payloadInfoJO = CommunicationHelper.getConfigInfo(
                    "ODS",
                    viewModel.getSelectedCabin(),
                    sharedPrefsClient.getUserDetails()
                )
                var topicName = CommunicationHelper.getTopicName(
                    Nomenclature.PUB_TOPIC.ODS_CONFIG,
                    viewModel.getSelectedComplex(),
                    viewModel.getSelectedCabin()
                )
                var request =
                    IotPublishLambdaRequest(
                        topicName,
                        payloadJO.toString(),
                        payloadInfoJO.toString()
                    )
                viewModel.publishConfig(request, publishHandler)
            } else {
                userAlertClient.showDialogMessage(
                    "Access Denied",
                    "You do not have the required privileges to perform this action. Please contact your supervisor.",
                    false
                )
            }


        })
    }

    var odsObserver :Observer<OdsConfig> = Observer {
        val odsConfig =it as OdsConfig
        val data = odsConfig.data
        Log.i("cabinDetails", "odsObserver: "+ Gson().toJson(data))
        var uiOdsConfig = sukriti.ngo.mis.repository.entity.OdsConfig(
            0,"","","","","","","","","","","","","","","","")
       uiOdsConfig.Ambientfloorfactor=data.Ambientfloorfactor?:""
       uiOdsConfig.Ambientseatfactor=data.Ambientseatfactor?:""
       uiOdsConfig.Seatfloorratio=data.Seatfloorratio?:""
       uiOdsConfig.Seatthreshold=data.Seatthreshold?:""

//        var uiOdsConfig = sukriti.ngo.mis.repository.entity.OdsConfig(
//            data._index,data.iD,data.ambientfloorfactor,data.ambientseatfactor,data.characteristic,data.cITY,
//            data.cLIENT,data.cOMPLEX,data.dEVICE_TIMESTAMP,data.dISTRICT,data.seatfloorratio,data.seatthreshold,
//            data.sendToAws,data.sendToDevic,data.sHORT_THING_NAME,data.sTATE,data.tHING_NAME
//        )
        Log.i("cabinDetails", "odsObserver: "+ Gson().toJson(uiOdsConfig))
        requestHandler.getData(uiOdsConfig.getPropertiesList(),uiOdsConfig.DEVICE_TIMESTAMP)
    }
    var requestHandler: PropertiesListRequestHandler = object : PropertiesListRequestHandler {
        override fun getData(data: MutableList<PropertyNameValueData>?, TimeStamp: String) {
            if (data?.size == 0) {
                var data = Nomenclature.getDefaultOdsConfig()
                binding.timeStamp.text = "Default Values"
                binding.grid.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE
                Log.i(_tag, "requestHandler: " + data?.size)
                val gridLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = OdsSettingsListAdapter(context, data)
                mwcCabinListAdapter.setChangeHandler(changeHandler)
                binding.grid.setItemViewCacheSize(mwcCabinListAdapter.itemCount);
                binding.grid.adapter = mwcCabinListAdapter
                if (data != null) {
                    configData = data
                }

//                binding.grid.visibility = View.GONE
//                binding.noDataContainer.visibility = View.VISIBLE
//                binding.noDataLabel.text = TimeStamp
            } else {
                binding.timeStamp.text = DateConverter.getElapsedTimeLabel(TimeStamp)
                binding.grid.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE
                Log.i(_tag, "requestHandler: " + data?.size)
                val gridLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = OdsSettingsListAdapter(context, data)
                mwcCabinListAdapter.setChangeHandler(changeHandler)
                binding.grid.setItemViewCacheSize(mwcCabinListAdapter.itemCount);
                binding.grid.adapter = mwcCabinListAdapter
                if (data != null) {
                    configData = data
                }
            }
        }
    }

    var changeHandler: OdsSettingsListAdapter.ChangeHandler = OdsSettingsListAdapter.ChangeHandler {
        var data = it
        binding.submit.visibility = View.VISIBLE
        if (data != null) {
            configData = data
        }
        Log.i("__change", Gson().toJson(data))
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
}
