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
import kotlinx.android.synthetic.main.login.*
import sukriti.ngo.mis.databinding.CabinHealthBinding
import sukriti.ngo.mis.repository.utils.DateConverter
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.adapters.HealthPropertiesListAdapter
import sukriti.ngo.mis.ui.complexes.data.Health
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData
import sukriti.ngo.mis.ui.complexes.interfaces.PropertiesListRequestHandler
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient

class CabinHealth : Fragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinHealthBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private var _tag: String = "_CabinDetails"

    companion object {
        private var INSTANCE: CabinHealth? = null

        fun getInstance(): CabinHealth {
            return INSTANCE
                ?: CabinHealth()
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
        userAlertClient.showWaitDialog("Loading Health..")
        viewModel.health.observe(viewLifecycleOwner,healthObserver)
    }

    private fun init() {
        Log.i(_tag, "init()")
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(CabinViewModel::class.java)

//        viewModel.getCabinHealth(viewModel.getSelectedCabin().ThingName, requestHandler)

    }

     var healthObserver:Observer<Health> = Observer {
         val health = it as Health
         Log.i("cabinDetails", "health observer: "+ Gson().toJson(health.data))
         var properties = arrayListOf<PropertyNameValueData>()
         properties.add(PropertyNameValueData("Air Dryer",viewModel.getStatusLabel(health.data.airDryerHealth)))
         properties.add(PropertyNameValueData("Choke",viewModel.getStatusLabel(health.data.chokeHealth)))
         properties.add(PropertyNameValueData("Fan",viewModel.getStatusLabel(health.data.fanHealth)))
         properties.add(PropertyNameValueData("Floor Clean",viewModel.getStatusLabel(health.data.floorCleanHealth)))
         properties.add(PropertyNameValueData("Flush",viewModel.getStatusLabel(health.data.flushHealth)))
         properties.add(PropertyNameValueData("Light",viewModel.getStatusLabel(health.data.lightHealth)))
         properties.add(PropertyNameValueData("Lock",viewModel.getStatusLabel(health.data.lockHealth)))
         properties.add(PropertyNameValueData("ODS",viewModel.getStatusLabel(health.data.odsHealth)))
         properties.add(PropertyNameValueData("Tap",viewModel.getStatusLabel(health.data.tapHealth)))
         Log.i("cabinDetails", "health observer: "+ Gson().toJson(properties))
         userAlertClient.closeWaitDialog()
         requestHandler.getData(properties,"") //todo assign timestamp

     }

    var requestHandler: PropertiesListRequestHandler = object : PropertiesListRequestHandler {
        override fun getData(data: MutableList<PropertyNameValueData>?, timeStamp: String) {

            if (data?.size == 0) {
                var data = Nomenclature.getDefaultHealth()
                binding.grid.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE
                binding.timeStamp.text = "Default Values"
                val gridLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = HealthPropertiesListAdapter(context, data)
                binding.grid.adapter = mwcCabinListAdapter

//                binding.grid.visibility = View.GONE
//                binding.noDataContainer.visibility = View.VISIBLE
//                binding.noDataLabel.text = timeStamp
            } else {

                binding.grid.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE
                binding.timeStamp.text = DateConverter.getElapsedTimeLabel(timeStamp)
                val gridLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = HealthPropertiesListAdapter(context, data)
                binding.grid.adapter = mwcCabinListAdapter
            }
        }

    }
}
