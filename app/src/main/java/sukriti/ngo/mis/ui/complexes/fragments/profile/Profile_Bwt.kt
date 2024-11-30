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
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.CabinProfileBwtBinding
import sukriti.ngo.mis.databinding.CabinProfileResetBinding
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.adapters.AdapterProfileBwt
import sukriti.ngo.mis.ui.complexes.adapters.AdapterProfileReset
import sukriti.ngo.mis.ui.complexes.bwtData.UsageProfile
import sukriti.ngo.mis.ui.complexes.data.DisplayBwtProfile
import sukriti.ngo.mis.ui.complexes.interfaces.BwtProfileRequestHandler
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient

class Profile_Bwt : Fragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinProfileBwtBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private var _tag: String = "_CabinDetails"

    companion object {
        private var INSTANCE: Profile_Bwt? = null

        fun getInstance(): Profile_Bwt {
            return INSTANCE
                ?: Profile_Bwt()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CabinProfileBwtBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        userAlertClient.showWaitDialog("Loading Profiles..")
        viewModel.bwtUsageProfile.observe(viewLifecycleOwner, bwtUsageProfileObserver)
    }

    private fun init() {
        Log.i(_tag, "init()")
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

    }

    private var bwtUsageProfileObserver: Observer<ArrayList<UsageProfile>> = Observer {
        val bwtUsageProfiles = it
        viewModel.mutableDisplayBwtUsageprofile(bwtUsageProfiles, requestHandler)

    }
    var durationSelectionListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i("_durationSelection", " - ")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                var duration = Nomenclature.getDuration(index)
//            userAlertClient.showWaitDialog("Loading data...")
//            viewModel.getDisplayBwtProfileForDays(viewModel.getSelectedComplex(), viewModel.getSelectedCabin(),duration,requestHandler)
            }

        }

    var requestHandler: BwtProfileRequestHandler = object : BwtProfileRequestHandler {
        override fun getData(data: MutableList<DisplayBwtProfile>?) {
            userAlertClient.closeWaitDialog()
            if (data?.size == 0) {
                binding.gridContainer.visibility = View.GONE
                binding.noDataContainer.visibility = View.VISIBLE
                binding.noDataLabel.text = "No data available for the selected cabin"
            } else {
                binding.gridContainer.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE

                Log.i(_tag, "requestHandler: " + data?.size)
                setTitles()
                val gridLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = AdapterProfileBwt(context, data)
                binding.grid.adapter = mwcCabinListAdapter
            }
        }
    }

    fun setTitles() {

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
            R.id.title_11,
            R.id.title_12,
            R.id.title_13,
            R.id.title_14,
            R.id.title_15
        )

        val titles = arrayListOf<String>(
            "Date",
            "Time",
            "Cabin Type",
            "Water Recycled",
            "Air Blower Run Time",
            "Current Water Level",
            "Filter State",
            "Ozonator Run Time",
            "TP Run Time",
            "Turbidity Level",
            "Client",
            "Complex",
            "State",
            "District",
            "City"
        )

        val fields = arrayOfNulls<TextView>(ids.size)

        for (i in ids.indices) {
            fields[i] = binding.root.findViewById(ids[i])
            fields[i]?.text = titles[i]
        }
    }
}
