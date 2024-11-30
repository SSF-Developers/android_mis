package sukriti.ngo.mis.ui.dashboard.fragments.banner

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.DashboardUsageCountStatsBinding
import sukriti.ngo.mis.databinding.FragmentFeedbackCountStatsBinding
import sukriti.ngo.mis.repository.data.*
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.DashboardChartData
import sukriti.ngo.mis.ui.dashboard.data.DataSummary
import sukriti.ngo.mis.ui.dashboard.interfaces.FeedbackComparisonRequestHandler
import sukriti.ngo.mis.ui.dashboard.interfaces.UsageComparisonRequestHandler
import sukriti.ngo.mis.ui.dashboard.interfaces.UsageSummaryRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient
import java.lang.IllegalStateException
import java.util.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FeedbackCountStats.newInstance] factory method to
 * create an instance of this fragment.
 */
class FeedbackCountStats : Fragment() {
    private lateinit var binding: FragmentFeedbackCountStatsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var chartData: FeedbackComparisonData
    private var isChartInitialized = false
    private var daysCount = 100

    companion object {
        private var INSTANCE: FeedbackCountStats? = null

        fun getInstance(): FeedbackCountStats {
            return INSTANCE
                ?: FeedbackCountStats()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedbackCountStatsBinding.inflate(inflater, container, false)
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

    var dataSummaryObserver : Observer<DataSummary> = Observer {
        var data = it as DataSummary
        var averageFeedback = data.feedback
        var duration = viewModel.getDataDuration()

        if (!averageFeedback.isNullOrEmpty())
        binding.label.text = "" + averageFeedback + " is average feedback recorded in " + duration.label

        viewModel.dashboardChartData.observe(viewLifecycleOwner, dashboardChartDataObserver)

    }



    var dashboardChartDataObserver: Observer<DashboardChartData> = Observer {
        var dashboardChartData = it as DashboardChartData
        var dataDuration = viewModel.getDataDuration()
        var feedback = dashboardChartData.feedback
     var total: List<DailyFeedbackCount> = emptyList()
        var mwc: List<DailyFeedbackCount> = emptyList()
        var fwc: List<DailyFeedbackCount> = emptyList()
        var pd: List<DailyFeedbackCount> = emptyList()
        var mur: List<DailyFeedbackCount> = emptyList()
        for (data in feedback) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            total += DailyFeedbackCount(data.date, 0, data.all.toFloat().toInt())
            mwc += DailyFeedbackCount(data.date, 0, data.mwc.toFloat().toInt())
            fwc += DailyFeedbackCount(data.date, 0.toInt(), data.fwc.toFloat().toInt())
            pd += DailyFeedbackCount(data.date, 0, data.pwc.toFloat().toInt())
            mur += DailyFeedbackCount(data.date, 0, data.mur.toFloat().toInt())
        }


        var feedbackComparisonData = FeedbackComparisonData(
            dataDuration.from, dataDuration.to,
            total, mwc, fwc, pd, mur, dataDuration.label
        )

    dataRequestHandler.onSuccess(feedbackComparisonData)
    }


    var dataRequestHandler: FeedbackComparisonRequestHandler =
    object : FeedbackComparisonRequestHandler {
        override fun onSuccess(stats: FeedbackComparisonData?) {
            userAlertClient.closeWaitDialog()
            if (stats != null) {
                chartData = stats

                if(!isChartInitialized)
                    initLineChart()
                setData()
            }
        }

        override fun onError(message: String?) {
            userAlertClient.closeWaitDialog()
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
        for ((index, item) in chartData._1starData.withIndex()) {
            dailyRecycledWaterValues.add(Entry(index.toFloat(), item.userCount.toFloat()))
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