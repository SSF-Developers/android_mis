package sukriti.ngo.mis.ui.complexes.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import sukriti.ngo.mis.databinding.CabinHealthBinding
import sukriti.ngo.mis.repository.utils.DateConverter
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.adapters.HealthPropertiesListAdapter
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData
import sukriti.ngo.mis.ui.complexes.interfaces.PropertiesListRequestHandler
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient

class CabinBwtHealth : Fragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinHealthBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private var _tag: String = "_CabinDetails"

    companion object {
        private var INSTANCE: CabinBwtHealth? = null

        fun getInstance(): CabinBwtHealth {
            return INSTANCE
                ?: CabinBwtHealth()
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
        userAlertClient.showWaitDialog("Loading Health...")
        viewModel.bwtHealth.observe(viewLifecycleOwner,healthObserver)
    }

    private fun init() {
        Log.i(_tag, "init()")
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(CabinViewModel::class.java)


//        viewModel.getCabinBwtHealth(viewModel.getSelectedCabin().ThingName, requestHandler)
    }

    var healthObserver: Observer<sukriti.ngo.mis.ui.complexes.bwtData.Health> = Observer {
        val health = it as sukriti.ngo.mis.ui.complexes.bwtData.Health
        Log.i("cabinDetails", "health observer: "+ Gson().toJson(health.data))
        var properties = arrayListOf<PropertyNameValueData>()
        properties.add(PropertyNameValueData("ALP Valve",viewModel.getStatusLabel(health.data.ALPValveHealth)))
        properties.add(PropertyNameValueData("Blower",viewModel.getStatusLabel(health.data.BlowerHealth)))
        properties.add(PropertyNameValueData("Fail safe",viewModel.getStatusLabel(health.data.FailsafeHealth)))
        properties.add(PropertyNameValueData("Filter",viewModel.getStatusLabel(health.data.FilterHealth)))
        properties.add(PropertyNameValueData("MP1 Valve",viewModel.getStatusLabel(health.data.MP1ValveHealth)))
        properties.add(PropertyNameValueData("MP2 Valve",viewModel.getStatusLabel(health.data.MP2ValveHealth)))
        properties.add(PropertyNameValueData("MP3 Valve",viewModel.getStatusLabel(health.data.MP3ValveHealth)))
        properties.add(PropertyNameValueData("MP4 Valve",viewModel.getStatusLabel(health.data.MP4ValveHealth)))
        properties.add(PropertyNameValueData("Ozonator",viewModel.getStatusLabel(health.data.OzonatorHealth)))
        properties.add(PropertyNameValueData("Priming Valve",viewModel.getStatusLabel(health.data.PrimingValveHealth)))
        properties.add(PropertyNameValueData("Pump",viewModel.getStatusLabel(health.data.PumpHealth)))
        Log.i("cabinDetails", "health observer: "+ Gson().toJson(properties))
        userAlertClient.closeWaitDialog()
        requestHandler.getData(properties,"") //todo assign timestamp

    }

    var requestHandler: PropertiesListRequestHandler = object : PropertiesListRequestHandler {
        override fun getData(data: MutableList<PropertyNameValueData>?, timeStamp: String) {

            userAlertClient.closeWaitDialog()
            if (data?.size == 0) {
                binding.grid.visibility = View.GONE
                binding.noDataContainer.visibility = View.VISIBLE
                binding.noDataLabel.text = timeStamp
            } else {
                binding.grid.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE
                binding.timeStamp.text = DateConverter.getElapsedTimeLabel(timeStamp)
                val gridLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = HealthPropertiesListAdapter(context, data)
                binding.grid.adapter = mwcCabinListAdapter
            }
        }

    }
}
