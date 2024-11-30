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

class Config_CMS : Fragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinHealthBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var configData: MutableList<PropertyNameValueData>
    private var _tag: String = "_CabinDetails"

    companion object {
        private var INSTANCE: Config_CMS? = null

        fun getInstance(): Config_CMS {
            return INSTANCE
                ?: Config_CMS()
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

//        viewModel.getCabinCmsConfig(viewModel.getSelectedCabin().ThingName, requestHandler)

        viewModel.cmsConfig.observe(viewLifecycleOwner,cmsObserver)
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
                var payloadJO = CommunicationHelper.getCmsPayload(configData, metadata)
                var payloadInfoJO = CommunicationHelper.getConfigInfo(
                    "CMS",
                    viewModel.getSelectedCabin(),
                    sharedPrefsClient.getUserDetails()
                )
                var topicName = CommunicationHelper.getTopicName(
                    Nomenclature.PUB_TOPIC.CMS_CONFIG,
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

    var cmsObserver :Observer<CmsConfig> = Observer {
        val cmsConfig = it as CmsConfig
        val data = cmsConfig.data
        Log.i("cabinDetails", "cms observer: "+Gson().toJson(data))
        val uiCmsConfig = sukriti.ngo.mis.repository.entity.CmsConfig(
      0,"","","","","","","","","","","","","","","","","","","","","","","","","","","",
            "","","","","")
        uiCmsConfig.Airdryerautoontimer=data.Airdryerautoontimer ?:""
        uiCmsConfig.Airdryerdurationtimer=data.Airdryerdurationtimer?:""
        uiCmsConfig.Autoairdryerenabled=data.Autoairdryerenabled?:""
        uiCmsConfig.Autofanenabled=data.Autofanenabled?:""
        uiCmsConfig.Autofloorenabled=data.Autofloorenabled?:""
        uiCmsConfig.Autofullflushenabled=data.Autofullflushenabled?:""
        uiCmsConfig.Autolightenabled=data.Autolightenabled?:""
        uiCmsConfig.Autominiflushenabled=data.Autominiflushenabled?:""
        uiCmsConfig.Autopreflush=data.Autopreflush?:""
        uiCmsConfig.Exitafterawaytimer=data.Exitafterawaytimer?:""
        uiCmsConfig.Fanautoofftimer=data.Fanautoofftimer?:""
        uiCmsConfig.Fanautoontimer=data.Fanautoontimer?:""
        uiCmsConfig.Floorcleancount=data.Floorcleancount?:""
        uiCmsConfig.Floorcleandurationtimer=data.Floorcleandurationtimer?:""
        uiCmsConfig.fullflushactivationtimer=data.fullflushactivationtimer?:""
        uiCmsConfig.fullflushdurationtimer=data.fullflushdurationtimer?:""
        uiCmsConfig.Lightautoofftime=data.Lightautoofftime?:""
        uiCmsConfig.Lightautoontimer=data.Lightautoontimer?:""
        uiCmsConfig.Miniflushactivationtimer=data.Miniflushactivationtimer?:""
        uiCmsConfig.Miniflushdurationtimer=data.Miniflushdurationtimer?:""
//        val uiCmsConfig = sukriti.ngo.mis.repository.entity.CmsConfig(
//            data._index,data.iD,data.airdryerautoontimer,data.airdryerdurationtimer,data.autoairdryerenabled,
//            data.autofanenabled,data.autofloorenabled,data.autofullflushenabled,data.autolightenabled,data.autominiflushenabled,
//            data.autopreflush,data.characteristic,data.cITY,data.cLIENT,data.cOMPLEX,"",data.dISTRICT,data.exitafterawaytimer,
//            data.fanautoofftimer,data.fanautoontimer,data.floorcleancount,data.floorcleandurationtimer,data.fullflushactivationtimer,
//            data.fullflushdurationtimer,data.lightautoofftime,data.lightautoontimer,data.miniflushactivationtimer,data.miniflushdurationtimer,
//            data.sendToAws,data.sendToDevic,data.sHORT_THING_NAME,data.sTATE,data.tHING_NAME
//        )
        Log.i("cabinDetails", "cms observer: "+Gson().toJson(uiCmsConfig.getPropertiesList()))
        requestHandler.getData(uiCmsConfig.getPropertiesList(),"")
    }
    var requestHandler: PropertiesListRequestHandler = object : PropertiesListRequestHandler {
        override fun getData(data: MutableList<PropertyNameValueData>?, TimeStamp: String) {
            if (data?.size == 0) {
                var data = Nomenclature.getDefaultCmsConfig()
                binding.timeStamp.text = "Default Values"
                binding.grid.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE

                Log.i(_tag, "requestHandler: " + data?.size)
                val gridLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = CmsSettingsListAdapter(context, data)
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
                var mwcCabinListAdapter = CmsSettingsListAdapter(context, data)
                mwcCabinListAdapter.setChangeHandler(changeHandler)
                binding.grid.setItemViewCacheSize(mwcCabinListAdapter.itemCount);
                binding.grid.adapter = mwcCabinListAdapter
                if (data != null) {
                    configData = data
                }
            }
        }
    }

    var changeHandler: CmsSettingsListAdapter.ChangeHandler = CmsSettingsListAdapter.ChangeHandler {
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
