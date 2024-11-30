package sukriti.ngo.mis.ui.complexes.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import sukriti.ngo.mis.databinding.CabinHealthBinding
import sukriti.ngo.mis.repository.entity.AqiLumen
import sukriti.ngo.mis.repository.utils.DateConverter.*
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.adapters.*
import sukriti.ngo.mis.ui.complexes.interfaces.LatestCabinAqiLumenRequestHandler
import sukriti.ngo.mis.ui.dashboard.data.UiResult
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import java.util.*

class CabinAqiLumen : Fragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinHealthBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var uiResult :UiResult
    private var _tag: String = "_CabinDetails"

    companion object {
        private var INSTANCE: CabinAqiLumen? = null

        fun getInstance(): CabinAqiLumen {
            return INSTANCE
                ?: CabinAqiLumen()
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
        userAlertClient.showWaitDialog("Loading Air Quality Index..")
        viewModel.aqiLumen.observe(viewLifecycleOwner,aqiObserver)
    }

    private fun init() {
        Log.i(_tag, "init()")
        sharedPrefsClient = SharedPrefsClient(context)
        uiResult = sharedPrefsClient.getUiResult()
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(CabinViewModel::class.java)


//        viewModel.getCabinAqiLumen(viewModel.getSelectedCabin().ThingName, requestHandler)

    }

    var aqiObserver: Observer<sukriti.ngo.mis.ui.complexes.data.AqiLumen> = Observer {
        val aqiLumen = it as sukriti.ngo.mis.ui.complexes.data.AqiLumen
        val data = aqiLumen.data
        Log.i("cabinDetails", "aqiObserver: "+ Gson().toJson(data))

        val uiAqiLumen = Nomenclature.getDefaultAqiLumen()
        uiAqiLumen._index = data._index
        uiAqiLumen._ID=""+data._index
        uiAqiLumen.concentrationCH4=data.concentrationCH4
        uiAqiLumen.concentrationCO=data.concentrationCO
        uiAqiLumen.concentrationLuminosityStatus=data.concentrationLuminosityStatus
        uiAqiLumen.concentrationNH3=data.concentrationNH3
//        uiAqiLumen.DEVICE_TIMESTAMP=data.timeStamp.toString()


    //        val uiAqiLumen = AqiLumen(
//            0, "", data.characteristic, data.cITY,data.cLIENT, data.cOMPLEX,
//            data.concentrationCH4, data.concentrationCO, data.concentrationLuminosityStatus,
//            data.concentrationNH3, data.dEVICE_TIMESTAMP, data.dISTRICT, data.iNITIATEDFOR,
//            data.sendToAws, data.sendToDevic, data.timeStamp, data.sHORT_THING_NAME, data.sTATE,
//            data.tHING_NAME, data.ttl.toString(), data.version_code.toString()
//        )
        Log.i("cabinDetails", "aqiObserver: "+ Gson().toJson(uiAqiLumen))
        userAlertClient.closeWaitDialog()
        requestHandler.getData(uiAqiLumen)
    }
    var requestHandler: LatestCabinAqiLumenRequestHandler =
        object : LatestCabinAqiLumenRequestHandler {
            override fun getData(data: AqiLumen?) {
                binding.grid.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE

                val gridLayoutManager = GridLayoutManager(context, 1)
                binding.timeStamp.text = getElapsedTimeLabel(data?.DEVICE_TIMESTAMP)
                //binding.timeStamp.text = getElapsedTimeLabel(""+(Calendar.getInstance().timeInMillis - 1000 * 60 * 60* 10))
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = AqiLumenAdapter(context, data)
                binding.grid.adapter = mwcCabinListAdapter
            }

            override fun onError(message: String?) {
                var data = Nomenclature.getDefaultAqiLumen()
                binding.grid.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE

                val gridLayoutManager = GridLayoutManager(context, 1)
                binding.timeStamp.text = "Default Values"
                //binding.timeStamp.text = getElapsedTimeLabel(""+(Calendar.getInstance().timeInMillis - 1000 * 60 * 60* 10))
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = AqiLumenAdapter(context, data)
                binding.grid.adapter = mwcCabinListAdapter

//                binding.grid.visibility = View.GONE
//                binding.noDataContainer.visibility = View.VISIBLE
//                binding.noDataLabel.text = message
            }
        }
}
