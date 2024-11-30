package sukriti.ngo.mis.ui.reports.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
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
import sukriti.ngo.mis.databinding.ReportsUsageDataBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.complexes.adapters.AdapterProfileUsage
import sukriti.ngo.mis.ui.complexes.data.DisplayUsageProfile
import sukriti.ngo.mis.ui.complexes.interfaces.UsageProfileRequestHandler
import sukriti.ngo.mis.ui.reports.ReportsViewModel
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.ACTION_DOWNLOAD
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.ACTION_EMAIL
import sukriti.ngo.mis.utils.*
import sukriti.ngo.mis.utils.ExcelClient.usageProfileToCsv


class RawData : Fragment() {

    private lateinit var mThis: Fragment
    private lateinit var viewModel: ReportsViewModel
    private lateinit var binding: ReportsUsageDataBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var mNavigationHandler: NavigationHandler
    private var gridData: List<DisplayUsageProfile> = listOf<DisplayUsageProfile>()
    private var userDurationSelection = false

    private var _tag: String = "_CabinDetails"
    val titles = arrayListOf<String>(
        "Date",
        "Time",
        "Cabin Type",
        "Duration",
        "Usage Charge",
        "Feedback",
        "Entry Type",
        "Air Dryer",
        "Fan Time",
        "Floor Clean",
        "Full Flush",
        "Manual Flush",
        "Light Time",
        "Mini Flush",
        "Pre Flush",
        "RFID",
        "Client",
        "Complex",
        "State",
        "District",
        "City"
    )

    companion object {
        private var INSTANCE: RawData? = null

        fun getInstance(): RawData {
            return INSTANCE
                ?: RawData()
        }
    }

    fun setNavigationHandler(handler: NavigationHandler) {
        mNavigationHandler = handler
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ReportsUsageDataBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mThis = this as Fragment
    }

    private fun init() {
        Log.i(_tag, "init()")
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireParentFragment()).get(ReportsViewModel::class.java)
        binding.timeInterval.setOnTouchListener(durationTouchListener)
        val items = Nomenclature.getDurationLabels()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_duration_selection, items)
        binding.timeInterval.adapter = adapter
        binding.timeInterval.onItemSelectedListener = durationSelectionListener
        binding.timeInterval.setSelection(
            Nomenclature.getIndex(
                viewModel.getSelectedDuration(),
                items
            )
        )
        binding.timeIntervalContainer.setOnClickListener(View.OnClickListener {
            binding.timeInterval.performClick()
        })

        val reportActions = Nomenclature.getReportActions()
        val actionsAdapter =
            ArrayAdapter(requireContext(), R.layout.item_duration_selection, reportActions)
        binding.actions.adapter = actionsAdapter
        binding.actions.onItemSelectedListener = actionSelectionListener
        binding.actions.setSelection(0)

        viewModel.selectionTree.observe(viewLifecycleOwner, Observer {
            val selectionTree = it ?: return@Observer
            loadData()
        })
    }

    var durationTouchListener: View.OnTouchListener = object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            userDurationSelection = true
            return false
        }
    }

    var durationSelectionListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i("_durationSelection", " - ")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                if (userDurationSelection) {
                    var selection = Nomenclature.getDurationLabels()[index]
                    Log.i("_selection", "selection: $selection")
                    viewModel.setSelectedDuration(selection)
                    if (sharedPrefsClient.getSelectionTreeStatus()) {
                        loadData()
                    } else {
                        setNoData("No unit selected in the Selection Tree. Please select units to view reports.")
                    }
                }

            }
        }

    private var actionSelectionListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i("_actionSelection", " - ")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                when (index) {
                    0 -> {

                    }
                    1 -> {
                        viewModel.createFile(mThis, ACTION_DOWNLOAD, "UsageReport.csv")
                    }
                    2 -> {
                        viewModel.createFile(mThis, ACTION_EMAIL, "UsageReport.csv")
                    }
                }
            }
        }

    var requestHandler: UsageProfileRequestHandler = object : UsageProfileRequestHandler {
        override fun getData(data: MutableList<DisplayUsageProfile>?) {
            userAlertClient.closeWaitDialog()
            if (data?.size == 0) {
                setNoData("No usage recorded for selected duration.")
            } else {
                binding.gridContainer.visibility = View.VISIBLE
                binding.actionsContainer.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE

                Log.i(_tag, "requestHandler: " + data?.size)
                setTitles()
                if (data != null) {
                    gridData = data
                }

                val gridLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = AdapterProfileUsage(context, data)
                binding.grid.adapter = mwcCabinListAdapter
            }
        }
    }

    fun loadData() {
        userAlertClient.showWaitDialog("Loading data...")
        var selectedIndex =
            Nomenclature.getIndex(viewModel.getSelectedDuration(), Nomenclature.getDurationLabels())
        var duration = Nomenclature.getDuration(selectedIndex)
        Log.i("_selection", "" + duration)
        viewModel.getProfileReportForDays(
            Utilities.getSelectionData(viewModel.selectionTree.value),
            duration,
            requestHandler
        )
    }

    fun setNoData(message: String) {
        binding.gridContainer.visibility = View.GONE
        binding.actionsContainer.visibility = View.GONE
        binding.noDataContainer.visibility = View.VISIBLE
        binding.noDataLabel.text = message
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
            R.id.title_15,
            R.id.title_16,
            R.id.title_17,
            R.id.title_18,
            R.id.title_19,
            R.id.title_20,
            R.id.title_21
        )

        val fields = arrayOfNulls<TextView>(ids.size)

        for (i in ids.indices) {
            fields[i] = binding.root.findViewById(ids[i])
            fields[i]?.text = titles[i]
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.handleResult(
            requireActivity(),
            usageProfileToCsv(gridData, titles),
            requestCode,
            requestCode,
            data
        )
        binding.actions.setSelection(0)

        if (requestCode == ACTION_EMAIL) {

        } else if (requestCode == ACTION_DOWNLOAD) {
            userAlertClient.showDialogMessage(
                "File Saved",
                "The file was successfully saved.",
                false
            )
        }
    }
}
