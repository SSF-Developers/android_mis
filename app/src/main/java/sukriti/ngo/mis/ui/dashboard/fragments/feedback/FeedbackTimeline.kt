package sukriti.ngo.mis.ui.super_admin.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.DashboardTimelineBinding
import sukriti.ngo.mis.repository.data.DailyAverageFeedback
import sukriti.ngo.mis.repository.data.DailyUsageCount
import sukriti.ngo.mis.repository.data.FeedbackTimelineData
import sukriti.ngo.mis.repository.data.UsageComparisonStats
import sukriti.ngo.mis.repository.utils.DateConverter
import sukriti.ngo.mis.ui.administration.interfaces.RequestHandler
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.DashboardChartData
import sukriti.ngo.mis.ui.dashboard.data.Feedback
import sukriti.ngo.mis.ui.dashboard.interfaces.FeedbackTimelineRequestHandler
import sukriti.ngo.mis.ui.dashboard.interfaces.UsageComparisonRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.UserAlertClient
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class FeedbackTimeline : Fragment() {
    private lateinit var binding: DashboardTimelineBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var chartData: FeedbackTimelineData
    private var isChartInitialized = false

    companion object {
        private var INSTANCE: FeedbackTimeline? = null

        fun getInstance(): FeedbackTimeline {
            return INSTANCE
                ?: FeedbackTimeline()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardTimelineBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel =
            ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)

        val items = Nomenclature.getTimelineDurationLabels()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_duration_selection, items)
        binding.timelineHolder.timeInterval.adapter = adapter
        binding.timelineHolder.timeInterval.onItemSelectedListener = durationSelectionListener
        binding.timelineHolder.timeInterval.setSelection(Nomenclature.TIMELINE_DURATION_DEFAULT_SELECTION)
        binding.timelineHolder.timeIntervalContainer.setOnClickListener(View.OnClickListener {
            binding.timelineHolder.timeInterval.performClick()
        })
        viewModel.dashboardChartData.observe(viewLifecycleOwner,dashboardChartDataObserver)
    }

//    var dashboardChartDataObserver : androidx.lifecycle.Observer<DashboardChartData> = Observer {
        var dashboardChartDataObserver :Observer<DashboardChartData> = Observer {
        var dashboardChartData = it as DashboardChartData
        var dataDuration = viewModel.getDataDuration()
        var feedback = dashboardChartData.feedback
        var total: List<DailyAverageFeedback> = emptyList()
        var male: List<DailyAverageFeedback> = emptyList()
        var female: List<DailyAverageFeedback> = emptyList()
        var pd: List<DailyAverageFeedback> = emptyList()
        var mur: List<DailyAverageFeedback> = emptyList()
        for(data in feedback) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            total += DailyAverageFeedback(data.date,0f,data.all.toFloat(),0)
            male += DailyAverageFeedback(data.date,0f,data.mwc.toFloat(),0)
            female += DailyAverageFeedback(data.date,0f,data.fwc.toFloat(),0)
            pd += DailyAverageFeedback(data.date,0f,data.pwc.toFloat(),0)
            mur += DailyAverageFeedback(data.date,0f,data.mur.toFloat(),0)
        }

        Log.i("dashboardLambda", "data: male :$male \nfemale :$female\npwc :$pd\nmur :$mur\ntotal :$total")

        var feedbackTimelineData = FeedbackTimelineData(dataDuration.from,dataDuration.to,
            total,male,female,pd,mur,dataDuration.label)

        dataRequestHandler.onSuccess(feedbackTimelineData)
    }

    var dataRequestHandler: FeedbackTimelineRequestHandler =
        object : FeedbackTimelineRequestHandler {
            override fun onSuccess(stats: FeedbackTimelineData?) {
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

    var durationSelectionListener: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener{
        override fun onNothingSelected(p0: AdapterView<*>?) {
            Log.i("_durationSelection", " - ")
        }

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
            var duration = Nomenclature.getTimelineDuration(index)
//            userAlertClient.showWaitDialog("Loading data...")
//            viewModel.getFeedbackTimelineForDays(duration,dataRequestHandler)
        }
    }

    private fun initLineChart() {
        binding.chart.getDescription().setEnabled(false)
        binding.chart.setPinchZoom(false)
        binding.chart.setDrawGridBackground(false)
        binding.chart.getLegend().setEnabled(false)


        val xAxis: XAxis = binding.chart.getXAxis()
        //xAxis.typeface = tfLight
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.valueFormatter = formatter

        val leftAxis: YAxis = binding.chart.getAxisLeft()
        //leftAxis.typeface = tfLight
        leftAxis.valueFormatter = LargeValueFormatter()
        leftAxis.setDrawLabels(true)
        leftAxis.setCenterAxisLabels(false)
        leftAxis.setDrawGridLines(false)
        leftAxis.spaceTop = 5f
        leftAxis.granularity = 1.0f
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)
        binding.chart.getAxisRight().setEnabled(false)

        isChartInitialized = true
    }

    @SuppressLint("ResourceType")
    private fun setData() {

        val averageFeedbackValues = arrayListOf<Entry>()
        val mwcFeedbackValues = arrayListOf<Entry>()
        val fwcFeedbackValues = arrayListOf<Entry>()
        val pwcFeedbackValues = arrayListOf<Entry>()
        val murFeedbackValues = arrayListOf<Entry>()

        for((index, item) in chartData.averageData.withIndex()){
            averageFeedbackValues.add(Entry(index.toFloat(), item.average))
            Log.i("_averageFeedbackValues", ""+item.date + ": "+ item.average)
        }

        for((index, item) in chartData.mwcData.withIndex()){
            mwcFeedbackValues.add(Entry(index.toFloat(), item.average))
        }
        for((index, item) in chartData.fwcData.withIndex()){
            fwcFeedbackValues.add(Entry(index.toFloat(), item.average))
        }
        for((index, item) in chartData.pwcData.withIndex()){
            pwcFeedbackValues.add(Entry(index.toFloat(), item.average))
        }
        for((index, item) in chartData.murData.withIndex()){
            murFeedbackValues.add(Entry(index.toFloat(), item.average))
        }

        val set1 = createLineDataSet(averageFeedbackValues,"Average Feedback",Color.parseColor(getString(R.color.total)))
        val set2 = createLineDataSet(mwcFeedbackValues,"MWC Feedback",Color.parseColor(getString(R.color.mwc)))
        val set3 = createLineDataSet(fwcFeedbackValues,"FWC Feedback",Color.parseColor(getString(R.color.fwc)))
        val set4 = createLineDataSet(pwcFeedbackValues,"PWC Feedback",Color.parseColor(getString(R.color.pwc)))
        val set5 = createLineDataSet(murFeedbackValues,"MUR Feedback",Color.parseColor(getString(R.color.mur)))


        val data = LineData(set1,set2,set3,set4,set5)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)
        binding.chart.data = data

        binding.chart.invalidate()
        binding.chart.animateX(Nomenclature.CHART_ANIM_DURATION)
    }

    var formatter: ValueFormatter = object : ValueFormatter() {
        private val mFormat =
            SimpleDateFormat("dd MMM", Locale.ENGLISH)

        override fun getFormattedValue(value: Float): String {
            //return "$value"

            try{
                var index = value.toInt()
//                var date = DateConverter.toDbDate(chartData.averageData[index].date)
                var date = DateConverter.lambdaDate(chartData.averageData[index].date)
                var dateStr = mFormat.format(date)
                return dateStr
            }catch (e: ArrayIndexOutOfBoundsException ){
                Log.e("__formatter_01",""+value)
                return " xx "
            }catch (e: IndexOutOfBoundsException){
                Log.e("__formatter_02",""+value)
                return " xx "
            }
        }
    }

    @SuppressLint("ResourceType")
    fun createLineDataSet(values:ArrayList<Entry>, label: String, lineColor:Int): LineDataSet {
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