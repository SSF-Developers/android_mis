package sukriti.ngo.mis.ui.complexes.fragments.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Ignore
import com.google.gson.Gson
import sukriti.ngo.mis.databinding.CabinHealthBinding
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.adapters.BwtSettingsListAdapter
import sukriti.ngo.mis.ui.complexes.adapters.OdsSettingsListAdapter
import sukriti.ngo.mis.ui.complexes.adapters.PropertiesListAdapterLA
import sukriti.ngo.mis.ui.complexes.bwtData.BwtConfig
import sukriti.ngo.mis.ui.complexes.bwtData.Data
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData
import sukriti.ngo.mis.ui.complexes.interfaces.PropertiesListRequestHandler
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import java.util.*

class Config_BWT : Fragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinHealthBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var configData: MutableList<PropertyNameValueData>

    private var _tag: String = "_CabinDetails"

    companion object {
        private var INSTANCE: Config_BWT? = null

        fun getInstance(): Config_BWT {
            return INSTANCE
                ?: Config_BWT()
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
        viewModel._bwtConfig.observe(viewLifecycleOwner,bwtConfigObserver)
    }

    private fun init() {
        Log.i(_tag,"init()")
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(CabinViewModel::class.java)

//        viewModel.getCabinBwtConfig(viewModel.getSelectedCabin().ThingName,requestHandler)
    }

    var bwtConfigObserver : Observer<BwtConfig> = Observer {
        val bwt = it as BwtConfig
        val data = bwt.data
        Log.i("cabinDetails", "ucemsObserver: "+ Gson().toJson(data))
        Log.i("cabinDetails", "ucemsObserver: "+ Gson().toJson(data))
        userAlertClient.closeWaitDialog()
        requestHandler.getData(getPropertiesList(data),"")
    }

    var requestHandler: PropertiesListRequestHandler = object : PropertiesListRequestHandler{
        override fun getData(data: MutableList<PropertyNameValueData>?, TimeStamp: String) {

            if(data?.size == 0){
                binding.grid.visibility = View.GONE
                binding.noDataContainer.visibility = View.VISIBLE
                binding.noDataLabel.text = "No data available for the selected cabin"
            }else{
                binding.grid.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE

                Log.i(_tag,"requestHandler: "+data?.size)
                val gridLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = BwtSettingsListAdapter(context,data)
                mwcCabinListAdapter.setChangeHandler(changeHandler)
                binding.grid.adapter = mwcCabinListAdapter
                if (data != null) {
                    configData = data
                }
            }

        }

    }

    var changeHandler: BwtSettingsListAdapter.ChangeHandler = BwtSettingsListAdapter.ChangeHandler {
        var data = it
        binding.submit.visibility = View.VISIBLE
        if (data != null) {
            configData = data
        }
        Log.i("__change", Gson().toJson(data))
    }

    fun getPropertiesList(data :Data): List<PropertyNameValueData> {
        setDefaultValues(data)

        val list: MutableList<PropertyNameValueData> =
            ArrayList()

        list.add(PropertyNameValueData("Back Wash Count",data.backWashCount))
        list.add(PropertyNameValueData("Blower Dosage Factor",data.blowerDosageFactor))
        list.add(PropertyNameValueData("Blower Hourly Sec",data.blowerHourlySec))
        list.add(PropertyNameValueData("Blower MaxRun Time",data.blowerMaxRunTime))
        list.add(PropertyNameValueData("Blower Rest Time",data.blowerRestTime))
        list.add(PropertyNameValueData("Blower Test Time",data.blowerTestTime))
        list.add(PropertyNameValueData("Drain Count",data.drainCount))
        list.add(PropertyNameValueData("Filter Type",data.filterType))
        list.add(PropertyNameValueData("Ozonator Priority Level",data.ozonatorPriorityLevel))
        list.add(PropertyNameValueData("Ozonator Test Time",data.ozonatorTestTime))
        list.add(PropertyNameValueData("Pump High Level",data.pumpHighLevel))
        list.add(PropertyNameValueData("Pump Low Level",data.pumpLowLevel))
        list.add(PropertyNameValueData("Pump Test Time",data.pumpTestTime))
        list.add(PropertyNameValueData("Sampling Rate Time",data.samplingRateTime))
        list.add(PropertyNameValueData("SV Alp Duration",data.svAlpDuration))
        list.add(PropertyNameValueData("SV Test Time",data.svTestTime))

        return list
    }

    private fun setDefaultValues(data: Data){
        data.backWashCount = "2000"
        data.blowerDosageFactor= "7"
        data.blowerHourlySec= "500"
        data.blowerMaxRunTime= "1800000"
        data.blowerRestTime= "600"
        data.blowerTestTime= "100"
        data.drainCount= "5"
        data.filterType= "0"
        data.ozonatorPriorityLevel= "9000"
        data.ozonatorTestTime= "50"
        data.pumpHighLevel= "100"
        data.pumpLowLevel= "10"
        data.pumpTestTime= "100"
        data.samplingRateTime= "10"
        data.svAlpDuration= "10"
        data.svTestTime= "15"
    }

}
