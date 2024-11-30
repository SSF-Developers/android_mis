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
import kotlinx.android.synthetic.main.login.*
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.CabinUpiCollectionBinding

import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.adapters.AdapterProfileUpi
import sukriti.ngo.mis.ui.complexes.data.UpiPaymentList
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient


class Profile_Upi_Collection : Fragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinUpiCollectionBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private var _tag: String = "_CabinUpiDetails"

    companion object {
        private var INSTANCE: Profile_Upi_Collection? = null

        fun getInstance(): Profile_Upi_Collection {
            return INSTANCE
                ?: Profile_Upi_Collection()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CabinUpiCollectionBinding.inflate(layoutInflater)
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

        val items = Nomenclature.getDurationLabels()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_duration_selection, items)
        binding.timeInterval.adapter = adapter
        binding.timeInterval.onItemSelectedListener = durationSelectionListener
        binding.timeInterval.setSelection(Nomenclature.DURATION_DEFAULT_SELECTION)
        binding.timeIntervalContainer.setOnClickListener(View.OnClickListener {
            binding.timeInterval.performClick()
        })
        viewModel._upiPaymentList.observe(viewLifecycleOwner, upiProfileObserver)
    }

    var durationSelectionListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i("_durationSelection", " - ")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                var duration = Nomenclature.getDuration(index)
//            userAlertClient.showWaitDialog("Loading data...")
//            viewModel.getDisplayUsageProfileForDays(viewModel.getSelectedComplex(), viewModel.getSelectedCabin(),duration,requestHandler)

//            viewModel.usageProfile.observe(viewLifecycleOwner,usageProfileObserver)
            }

        }

    var upiProfileObserver: Observer<ArrayList<UpiPaymentList>> = Observer {
        val upiProfiles = it as ArrayList<UpiPaymentList>
        Log.i("cabinDetails", "usageProfile: " + Gson().toJson(upiProfiles))
//        viewModel.mutableDisplayUsageprofile(usageProfiles)
//        requestHandler.getData(viewModel.mutableDisplayUsageprofile(usageProfiles))


        userAlertClient.closeWaitDialog()
        setAdapter(upiProfiles)

    }

    fun setAdapter(data: ArrayList<UpiPaymentList>) {
//        val data = viewModel.toMutableUpiList(upiProfiles)
        if (data?.size == 0) {
            binding.gridContainer.visibility = View.GONE
            binding.noDataContainer.visibility = View.VISIBLE
            binding.noDataLabel.text = "No Upi recorded for selected duration."
        } else {
            binding.gridContainer.visibility = View.VISIBLE
            binding.noDataContainer.visibility = View.GONE

            Log.i(_tag, "requestHandler: " + data?.size)
            setTitles()
            binding.grid.layoutManager =LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            var adapter = AdapterProfileUpi(data)
            binding.grid.adapter =adapter

        }

    }


//    var requestHandler: UsageProfileRequestHandler = object : UsageProfileRequestHandler {
//        override fun getData(data: MutableList<DisplayUsageProfile>?) {
//            userAlertClient.closeWaitDialog()
//            if (data?.size == 0) {
//                binding.gridContainer.visibility = View.GONE
//                binding.noDataContainer.visibility = View.VISIBLE
//                binding.noDataLabel.text = "No usage recorded for selected duration."
//            } else {
//                displayUsage = data
//                binding.gridContainer.visibility = View.VISIBLE
//                binding.noDataContainer.visibility = View.GONE
//
//                Log.i(_tag, "requestHandler: " + data?.size)
//                setTitles()
//                val gridLayoutManager =
//                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//                binding.grid.layoutManager = gridLayoutManager
//                var mwcCabinListAdapter = AdapterProfileUsage(context, data)
//                binding.grid.adapter = mwcCabinListAdapter
//                toExcel()
//            }
//        }
//    }

    fun setTitles() {
        val ids = intArrayOf(
            R.id.title_01,
            R.id.title_02,
            R.id.title_03,
            R.id.title_04,
            R.id.title_05,
            R.id.title_06,
            R.id.title_07,
            R.id.title_08

        )
        val titles = arrayListOf<String>(
            "Date",
            "Time",
            "Amount Received",
            "Amount Transferred",
            "Sukriti Fee",
            "Vendor Fee",
            "Vpa",
            "Tax"
        )
        val fields = arrayOfNulls<TextView>(ids.size)

        for (i in ids.indices) {
            fields[i] = binding.root.findViewById(ids[i])
            fields[i]?.text = titles[i]
        }
    }

}
