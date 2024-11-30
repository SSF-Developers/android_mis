package sukriti.ngo.mis.ui.dashboard.fragments.banner

import android.annotation.SuppressLint
import android.app.usage.UsageStats
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dashboard_usage_stats.*
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.*
import sukriti.ngo.mis.repository.data.DailyUsageCount
import sukriti.ngo.mis.repository.data.DailyWaterRecycledStats
import sukriti.ngo.mis.repository.data.UsageComparisonStats
import sukriti.ngo.mis.repository.data.UsageSummaryStats
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.DashboardChartData
import sukriti.ngo.mis.ui.dashboard.data.DataSummary
import sukriti.ngo.mis.ui.dashboard.interfaces.RecycledWaterRequestHandler
import sukriti.ngo.mis.ui.dashboard.interfaces.UsageComparisonRequestHandler
import sukriti.ngo.mis.ui.dashboard.interfaces.UsageSummaryRequestHandler
import sukriti.ngo.mis.ui.super_admin.fragments.UsageSummary
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.UserAlertClient
import java.lang.IllegalStateException
import java.util.ArrayList

class UsageCountStats : Fragment() {
    private lateinit var binding: DashboardUsageCountStatsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var chartData: UsageComparisonStats
    private var isChartInitialized = false
    private var daysCount = 100

    companion object {
        private var INSTANCE: UsageCountStats? = null

        fun getInstance(): UsageCountStats {
            return INSTANCE
                ?: UsageCountStats()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardUsageCountStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init() {
        viewModel = ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(activity, lifecycle)
        navigationClient = NavigationClient(childFragmentManager)
        //todo handle direct through lambda
//        viewModel.countUsageForDays(daysCount, dataSummaryRequestHandler,lifecycle)
        viewModel.dataSummary.observe(viewLifecycleOwner,dataSummaryObserver)

    }

    var dataSummaryObserver :Observer<DataSummary> = Observer {
        var data = it as DataSummary
        var usageCount = data.usage
        var duration = viewModel.getDataDuration()
        val usageSummary =UsageSummaryStats(duration.from,duration.to,usageCount,
            0,0,0,0,duration.label)
        dataSummaryRequestHandler.onSuccess(usageSummary)
    }

    var dataSummaryRequestHandler: UsageSummaryRequestHandler =
        object : UsageSummaryRequestHandler {
            override fun onSuccess(summary: UsageSummaryStats?) {
                if (summary != null) {
                    binding.label.text =
                        "" + summary.total + " usages recorded in " + summary.durationLabel
//                    viewModel.getUsageComparisonForDays(daysCount, dataRequestHandler)
                    viewModel.dashboardChartData.observe(viewLifecycleOwner, dashboardChartDataObserver)
                }
            }

            override fun onError(message: String?) {

            }
        }



    var dashboardChartDataObserver: Observer<DashboardChartData> = Observer {
        var dashboardChartData = it as DashboardChartData
        var dataDuration = viewModel.getDataDuration()
        var usages = dashboardChartData.usage
        var total: List<DailyUsageCount> = emptyList()
        var male: List<DailyUsageCount> = emptyList()
        var female: List<DailyUsageCount> = emptyList()
        var pd: List<DailyUsageCount> = emptyList()
        var mur: List<DailyUsageCount> = emptyList()
        for (data in usages) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            total += DailyUsageCount(data.date, data.all)
            male += DailyUsageCount(data.date, data.mwc)
            female += DailyUsageCount(data.date, data.fwc)
            pd += DailyUsageCount(data.date, data.pwc)
            mur += DailyUsageCount(data.date, data.mur)
        }


        var usagesComparisonStats = UsageComparisonStats(
            dataDuration.from, dataDuration.to,
            total, male, female, pd, mur, dataDuration.label
        )

        dataRequestHandler.onSuccess(usagesComparisonStats)
    }

    var dataRequestHandler: UsageComparisonRequestHandler =
        object : UsageComparisonRequestHandler {
            override fun onSuccess(summary: UsageComparisonStats?) {
                if (summary != null) {
                    chartData = summary

                    if (!isChartInitialized)
                        initLineChart()
                    setData()
                }
            }

            override fun onError(message: String?) {

            }
        }
    private fun initLineChart() {
        binding.chart.getDescription().setEnabled(false)
        binding.chart.setPinchZoom(false)
        binding.chart.setDrawGridBackground(false)
        binding.chart.getLegend().setEnabled(false)
        binding.chart.setTouchEnabled(false)
        binding.chart.setDragEnabled(false)
        binding.chart.setScaleEnabled(false)
        // binding.chart.setBackgroundColor(color)
        // chart.setDrawHorizontalGrid(false);
        // chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);
        // set custom chart offsets (automatic offset calculation is hereby disabled)
        binding.chart.setViewPortOffsets(10f, 0f, 10f, 0f)

        binding.chart.axisLeft.isEnabled = false
        binding.chart.axisLeft.spaceTop = 40f
        binding.chart.axisLeft.spaceBottom = 40f
        binding.chart.axisRight.isEnabled = false
        binding.chart.xAxis.isEnabled = false

        //isChartInitialized = true
    }

    @SuppressLint("ResourceType")
    private fun setData() {
        Log.i("_setData", "1st init()")

        val dailyRecycledWaterValues = arrayListOf<Entry>()
        for ((index, item) in chartData.total.withIndex()) {
            dailyRecycledWaterValues.add(Entry(index.toFloat(), item.count.toFloat()))
        }

        //ContextCrash introduced requireContext()
        //Try-Catch for IllegalState: Fragment not attached to a context.
        try {
            val set1 = createLineDataSet(
                dailyRecycledWaterValues,
                "Total Usage",
                ContextCompat.getColor(requireContext(), R.color.total)
            )
            val data = LineData(set1)

            data.setValueTextColor(Color.WHITE)
            data.setValueTextSize(9f)
            binding.chart.data = data

            binding.chart.invalidate()
            binding.chart.animateX(800)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Log.e("ContextCrash","handled for UsageCountStats 01")
        }

    }

    @SuppressLint("ResourceType")
    fun createLineDataSet(values: ArrayList<Entry>, label: String, lineColor: Int): LineDataSet {
        val set = LineDataSet(values, label)
        set.setDrawCircles(false)
        set.setDrawValues(false)
        set.setDrawCircleHole(false)

        set.axisDependency = YAxis.AxisDependency.RIGHT
        set.color = lineColor
        set.valueTextColor = ColorTemplate.getHoloBlue()
        set.lineWidth = 3.0f
        set.fillAlpha = 65
        set.fillColor = lineColor
        set.highLightColor = Color.parseColor(getString(R.color.primary))

        return set
    }
}