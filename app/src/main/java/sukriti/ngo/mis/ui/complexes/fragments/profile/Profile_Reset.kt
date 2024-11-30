package sukriti.ngo.mis.ui.complexes.fragments.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.CabinProfileResetBinding
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.adapters.AdapterProfileReset
import sukriti.ngo.mis.ui.complexes.adapters.AdapterProfileUsage
import sukriti.ngo.mis.ui.complexes.data.DisplayResetProfile
import sukriti.ngo.mis.ui.complexes.data.ResetProfile
import sukriti.ngo.mis.ui.complexes.interfaces.ResetProfileRequestHandler
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient

class Profile_Reset : Fragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinProfileResetBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private var _tag: String = "_CabinDetails"

    companion object {
        private var INSTANCE: Profile_Reset? = null

        fun getInstance(): Profile_Reset {
            return INSTANCE
                ?: Profile_Reset()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CabinProfileResetBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    private fun init() {
        Log.i(_tag,"init()")
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(CabinViewModel::class.java)

        val items = Nomenclature.getDurationLabels()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_duration_selection, items)
        binding.timeInterval.adapter = adapter
        binding.timeInterval.onItemSelectedListener = durationSelectionListener
        binding.timeInterval.setSelection(Nomenclature.DURATION_DEFAULT_SELECTION)
        binding.timeIntervalContainer.setOnClickListener(View.OnClickListener {
            binding.timeInterval.performClick()
        })
        viewModel.resetProfile.observe(viewLifecycleOwner,resetProfileObserver)
    }

    var durationSelectionListener: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(p0: AdapterView<*>?) {
            Log.i("_durationSelection", " - ")
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
            var duration = Nomenclature.getDuration(index)
//            userAlertClient.showWaitDialog("Loading data...")
//            viewModel.getDisplayResetProfileForDays(viewModel.getSelectedComplex(), viewModel.getSelectedCabin(),duration,requestHandler)
//            viewModel.resetProfile.observe(viewLifecycleOwner,resetProfileObserver)
        }

    }
    var resetProfileObserver :Observer<ArrayList<ResetProfile>> = Observer {
        val resetProfiles = it as ArrayList<ResetProfile>
        Log.i("cabinDetails", "resetProfiles: "+ Gson().toJson(resetProfiles))
        userAlertClient.closeWaitDialog()
        requestHandler.getData(viewModel.mutableDisplayResetProfile(resetProfiles))

    }
    var requestHandler: ResetProfileRequestHandler = object : ResetProfileRequestHandler{

        override fun getData(data: MutableList<DisplayResetProfile>?) {
            userAlertClient.closeWaitDialog()
            if(data?.size == 0){
                binding.gridContainer.visibility = View.GONE
                binding.noDataContainer.visibility = View.VISIBLE
                binding.noDataLabel.text = "No data available for the selected cabin"
            }else{
                binding.gridContainer.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE

                Log.i(_tag,"requestHandler: "+data?.size)
                setTitles()
                val gridLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = AdapterProfileReset(context,data)
                binding.grid.adapter = mwcCabinListAdapter
            }
        }
    }
    
    fun setTitles(){
        val ids = intArrayOf(
            R.id.title_01,
            R.id.title_02,
            R.id.title_03,
            R.id.title_04,
            R.id.title_05,
            R.id.title_06,
            R.id.title_07,
            R.id.title_08,
            R.id.title_09,
            R.id.title_10,
            R.id.title_11
        )
        val titles = arrayListOf<String>(
            "Date","Time","Cabin Type","User Id","Board Id","Reset Resource",
            "Client","Complex","State","District","City"
        )
        val fields = arrayOfNulls<TextView>(ids.size)

        for (i in ids.indices) {
            fields[i] = binding.root.findViewById(ids[i])
            fields[i]?.text = titles[i]
        }
    }
}
