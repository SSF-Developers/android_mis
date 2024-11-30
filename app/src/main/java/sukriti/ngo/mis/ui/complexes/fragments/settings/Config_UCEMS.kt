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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoJWTParser.getPayload
import com.google.gson.Gson
import kotlinx.android.synthetic.main.cabin_health.*
import org.json.JSONObject
import sukriti.ngo.mis.communication.CommunicationHelper
import sukriti.ngo.mis.communication.CommunicationHelper.getTopicName
import sukriti.ngo.mis.communication.SimpleHandler
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.databinding.CabinDetailsBinding
import sukriti.ngo.mis.databinding.CabinHealthBinding
import sukriti.ngo.mis.databinding.ComplexesHomeBinding
import sukriti.ngo.mis.repository.entity.Cabin
import sukriti.ngo.mis.repository.entity.Complex
import sukriti.ngo.mis.repository.entity.UcemsConfig
import sukriti.ngo.mis.repository.utils.DateConverter
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData
import sukriti.ngo.mis.ui.administration.interfaces.ProvisioningTreeRequestHandler
import sukriti.ngo.mis.ui.administration.interfaces.TreeInteractionListener
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.ComplexesViewModel
import sukriti.ngo.mis.ui.complexes.adapters.*
import sukriti.ngo.mis.ui.complexes.data.CabinDetailsData
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.ui.complexes.data.PayloadMetaData
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData
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

class Config_UCEMS : Fragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinHealthBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var configData: MutableList<PropertyNameValueData>
    private var _tag: String = "_CabinDetails"

    companion object {
        private var INSTANCE: Config_UCEMS? = null

        fun getInstance(): Config_UCEMS {
            return INSTANCE
                ?: Config_UCEMS()
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
        userAlertClient.showWaitDialog("Loading Settings..")
        viewModel._ucemsConfig.observe(viewLifecycleOwner,ucemsObserver)
    }

    private fun init() {
        Log.i(_tag, "init()")
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(CabinViewModel::class.java)


        //Listeners
        binding.submit.setOnClickListener(View.OnClickListener {

            if (UserProfile.hasWriteAccess(sharedPrefsClient.getUserDetails().role)) {
                userAlertClient.showWaitDialog("Updating cabin config...")

                val shortThingName = viewModel.getSelectedCabin().ShortThingName
                Log.i("ucemsConfig", "Short Thing Name -> $shortThingName")
                val metadata = PayloadMetaData(
                    viewModel.getSelectedCabin().ThingName,
                    Nomenclature.getCabinType(shortThingName),
                    Nomenclature.getUserType(shortThingName)
                )
                Log.i("ucemsConfig", "Metadata -> ${Gson().toJson(metadata)}")

                val payloadJO = CommunicationHelper.getUcemsPayload(configData, metadata)
                val payloadInfoJO = CommunicationHelper.getConfigInfo(
                    "UCEMS",
                    viewModel.getSelectedCabin(),
                    sharedPrefsClient.getUserDetails()
                )
                val topicName = getTopicName(
                    Nomenclature.PUB_TOPIC.UCEMS_CONFIG,
                    viewModel.getSelectedComplex(),
                    viewModel.getSelectedCabin()
                )

                Log.i("ucemsConfig", "Payload Jo -> "+Gson().toJson(payloadJO))
                Log.i("ucemsConfig", "Payload Info Jo -> "+Gson().toJson(payloadInfoJO))
                Log.i("ucemsConfig", "Topic Name -> $topicName")

                val request =
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

        //Flow
//        viewModel.getCabinUcemsConfig(viewModel.getSelectedCabin().ThingName, requestHandler)

    }

    var ucemsObserver :Observer<sukriti.ngo.mis.ui.complexes.data.UcemsConfig> = Observer {
        val ucems = it as sukriti.ngo.mis.ui.complexes.data.UcemsConfig
        val data = ucems.data
        Log.i("cabinDetails", "ucemsObserver: "+ Gson().toJson(data))
        val uiUcems =UcemsConfig(0,"","","","","","","","","","","","","","","",
            "","","","","","","","","","",
            "","","","","")
        uiUcems.Cabinpaymentmode= data.Cabinpaymentmode?:""
        uiUcems.Collexpirytime=data.Collexpirytime?:""
        uiUcems.Edis_airDryr=data.Edis_airDryr?:""
        uiUcems.Edis_choke=data.Edis_choke?:""
        uiUcems.Edis_cms=data.Edis_cms?:""
        uiUcems.Edis_fan=data.Edis_fan?:""
        uiUcems.Edis_floor=data.Edis_floor?:""
        uiUcems.Edis_flush=data.Edis_flush?:""
        uiUcems.Edis_freshWtr=data.Edis_freshWtr?:""
        uiUcems.Edis_light=data.Edis_light?:""
        uiUcems.Edis_lock=data.Edis_lock?:""
        uiUcems.Edis_ods=data.Edis_ods?:""
        uiUcems.Edis_recWtr=data.Edis_recWtr?:""
        uiUcems.Edis_tap=data.Edis_tap?:""
        uiUcems.Entrychargeamount=data.Entrychargeamount?:""
        uiUcems.Exitdoortriggertimer=data.Exitdoortriggertimer?:""
        uiUcems.Feedbackexpirytime=data.Feedbackexpirytime?:""
        uiUcems.Occwaitexpirytime=data.Occwaitexpirytime?:""

//        val uiUcems =UcemsConfig(
//            data._index,data.iD,data.cabinpaymentmode,data.characteristic,data.cITY,data.cLIENT,data.collexpirytime,
//            data.cOMPLEX,"",data.dISTRICT,data.edis_airDryr,data.edis_choke,data.edis_cms,data.edis_fan,
//            data.edis_floor,data.freshWaterLevel,data.freshWaterLevel,data.edis_light,data.edis_lock,data.edis_ods,data.edis_recWtr,data.edis_tap,
//            data.entrychargeamount,data.exitdoortriggertimer,data.feedbackexpirytime,data.occwaitexpirytime,data.sendToAws,
//            data.sendToDevic,data.sHORT_THING_NAME,data.sTATE,data.tHING_NAME
//        )
        Log.i("cabinDetails", "ucemsObserver: "+ Gson().toJson(uiUcems))
        userAlertClient.closeWaitDialog()
        requestHandler.getData(uiUcems.getPropertiesList(),"")
    }
    var requestHandler: PropertiesListRequestHandler = object : PropertiesListRequestHandler {
        override fun getData(data: MutableList<PropertyNameValueData>?, TimeStamp: String) {

            Log.i("__ucemsConfig", Gson().toJson(data))
            if (data?.size == 0) {
                var data = Nomenclature.getDefaultUcemsConfig()
                binding.timeStamp.text = "Default Values"
                binding.grid.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE
                Log.i(_tag, "requestHandler: " + data?.size)
                val gridLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = UcemsSettingsListAdapter(context, data)
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
                Log.i("_misTest", "getData: "+TimeStamp)
                binding.timeStamp.text = DateConverter.getElapsedTimeLabel(TimeStamp)
                binding.grid.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE
                Log.i(_tag, "requestHandler: " + data?.size)
                val gridLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = UcemsSettingsListAdapter(context, data)
                mwcCabinListAdapter.setChangeHandler(changeHandler)
                binding.grid.setItemViewCacheSize(mwcCabinListAdapter.itemCount);
                binding.grid.adapter = mwcCabinListAdapter
                if (data != null) {
                    configData = data
                }
            }
        }
    }

    var changeHandler: UcemsSettingsListAdapter.ChangeHandler =
        UcemsSettingsListAdapter.ChangeHandler {
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
