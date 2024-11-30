package sukriti.ngo.mis.ui.dashboard.fragments.Bwt

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_faulty_cabin.*
import kotlinx.android.synthetic.main.login.*
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.FragmentBwtStatsBinding
import sukriti.ngo.mis.repository.data.UsageSummaryStats
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_BWT_DETAILS
import sukriti.ngo.mis.ui.dashboard.data.BwtpieChartData
import sukriti.ngo.mis.ui.dashboard.data.PieChartData
import sukriti.ngo.mis.ui.dashboard.interfaces.UsageSummaryRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.UserAlertClient

class BwtStat : Fragment() {
    private lateinit var binding: FragmentBwtStatsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var chartData: UsageSummaryStats
    private var isChartInitialized = false

    companion object {
        private var INSTANCE: BwtStat? = null

        fun getInstance(): BwtStat {
            return INSTANCE
                ?: BwtStat()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBwtStatsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel = ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        //FragmentIllegalState-Fix
        userAlertClient = UserAlertClient(activity,lifecycle)
        navigationClient = NavigationClient(childFragmentManager)

        val items = Nomenclature.getDurationLabels()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_duration_selection, items)
        binding.timeInterval.adapter = adapter
        binding.timeInterval.onItemSelectedListener = durationSelectionListener
        binding.timeInterval.setSelection(Nomenclature.DURATION_DEFAULT_SELECTION)
        binding.timeIntervalContainer.setOnClickListener(View.OnClickListener {
            binding.timeInterval.performClick()
        })

        binding.details.setOnClickListener(View.OnClickListener {
            viewModel.getDashboardNavigationHandler()
                .navigateTo(NAV_BWT_DETAILS)
        })

        viewModel.bwtpieChartData.observe(viewLifecycleOwner,bwtPieChartObaerver)
    }

    var bwtPieChartObaerver : Observer<BwtpieChartData> = Observer {
        var pieChartData = it as BwtpieChartData
        var dataDuration = viewModel.getDataDuration()
        var bwtpieChartDataUsage = pieChartData.usage
        var bwt =0
        var male =0
        var female = 0
        var pwc = 0
        var mur = 0
        var total = 0

        for(data in bwtpieChartDataUsage) {

            when (data.name) {
                "BWT" -> {
                    Log.e("dashboardLambda", "BWT: " + Gson().toJson(data))
                    bwt += data.value
                }
            }
        }

        total = male+female+pwc+mur
        Log.i("dashboardLambda", "data: bwt :$bwt \nfemale :$female\npwc :$pwc\nmur :$mur\ntotal :$total")

        var usageSummaryStats = UsageSummaryStats(dataDuration.from,dataDuration.to,
            total,bwt,female,pwc,mur,dataDuration.label)

        usageSummaryRequestHandler.onSuccess(usageSummaryStats)
    }

    var usageSummaryRequestHandler: UsageSummaryRequestHandler =
        object : UsageSummaryRequestHandler {
            override fun onSuccess(summary: UsageSummaryStats?) {
                userAlertClient.closeWaitDialog()
                if (summary != null) {
                    chartData = summary

                    if (!isChartInitialized)
                        initPieChart()
                    setPieChartData()
                }
            }

            override fun onError(message: String?) {
                userAlertClient.closeWaitDialog()
            }

        }

    var durationSelectionListener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i("_durationSelection", " - ")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                var duration = Nomenclature.getDuration(index)
//                userAlertClient.showWaitDialog("Loading data...")
                //FragmentIllegalState-Fix
                Log.e("_illegalState", "BwtStats  : " + lifecycle.currentState)
//                if (lifecycle.currentState == Lifecycle.State.RESUMED)
//                viewModel.countUsageForDays(duration, usageSummaryRequestHandler, lifecycle)
            }

        }


    private fun initPieChart() {

        binding.usageChart.setUsePercentValues(false)
        binding.usageChart.getDescription().setEnabled(false)
        binding.usageChart.setDrawHoleEnabled(true)
        binding.usageChart.setDrawEntryLabels(false)
        binding.usageChart.setDrawCenterText(true)
        binding.usageChart.setRotationEnabled(false)
        binding.usageChart.setHighlightPerTapEnabled(true)
        binding.usageChart.getLegend().setEnabled(false)

        binding.usageChart.setHoleRadius(58f)
        binding.usageChart.setTransparentCircleRadius(161f)
        binding.usageChart.setMaxAngle(180f) // HALF CHART
        binding.usageChart.setRotationAngle(180f)
        binding.usageChart.setCenterTextOffset(0f, -20f)
        binding.usageChart.setExtraOffsets(0f, 0f, 0f, -60f)

        // binding.usageChart.setCenterTextTypeface(tfLight)
        //binding.usageChart.setTransparentCircleAlpha(110)
        //binding.usageChart.setEntryLabelTypeface(tfRegular)
        binding.usageChart.setBackgroundColor(Color.WHITE)
        binding.usageChart.setHoleColor(Color.WHITE)
        binding.usageChart.setTransparentCircleColor(Color.WHITE)
        binding.usageChart.setEntryLabelColor(Color.WHITE)
        binding.usageChart.setEntryLabelTextSize(12f)


        isChartInitialized = true
    }

    @SuppressLint("ResourceType")
    private fun setPieChartData() {

        binding.usageChart.setCenterText(generateCenterSpannableText())

        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(chartData.mwc.toFloat()))
//        entries.add(PieEntry(chartData.fwc.toFloat()))
//        entries.add(PieEntry(chartData.pwc.toFloat()))
//        entries.add(PieEntry(chartData.mur.toFloat()))

        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor(getString(R.color.mwc)))
//        colors.add(Color.parseColor(getString(R.color.fwc)))
//        colors.add(Color.parseColor(getString(R.color.pwc)))
//        colors.add(Color.parseColor(getString(R.color.mur)))

        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.setSliceSpace(3f)
        dataSet.setSelectionShift(5f)
        dataSet.setColors(colors)

        val data = PieData(dataSet)
        data.setValueFormatter(formatter)
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        //data.setValueTypeface(tfLight)
        binding.usageChart.setData(data)

        // undo all highlights
        binding.usageChart.highlightValues(null)
        binding.usageChart.invalidate()

        //Other UI Fields
        binding.maleUsage.text = "" + chartData.mwc
//        binding.femaleUsage.text = "" + chartData.fwc
//        binding.pdUsage.text = "" + chartData.pwc
//        binding.murUsage.text = "" + chartData.mur
    }


    private fun generateCenterSpannableText(): SpannableString? {
        var totalStr = "" + chartData.total
        val s = SpannableString("$totalStr\nTotal Usage")

        s.setSpan(RelativeSizeSpan(1.7f), 0, totalStr.length, 0)
        //s.setSpan(StyleSpan(Typeface.NORMAL), 3, s.length - 4, 0)

        s.setSpan(ForegroundColorSpan(Color.GRAY), totalStr.length + 1, s.length, 0)
        s.setSpan(RelativeSizeSpan(.6f), totalStr.length + 1, s.length, 0)
        s.setSpan(StyleSpan(Typeface.ITALIC), totalStr.length + 1, s.length, 0)

        return s
    }

    var formatter: ValueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "" + value.toInt()
        }
    }



}