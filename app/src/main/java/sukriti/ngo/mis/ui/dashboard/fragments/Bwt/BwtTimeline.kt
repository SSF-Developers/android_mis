package sukriti.ngo.mis.ui.dashboard.fragments.Bwt

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.DashboardTimelineBinding
import sukriti.ngo.mis.databinding.FragmentBwtTimelineBinding
import sukriti.ngo.mis.repository.data.DailyUsageCount
import sukriti.ngo.mis.repository.data.UsageComparisonStats
import sukriti.ngo.mis.repository.utils.DateConverter
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.BwtdashboardChartData
import sukriti.ngo.mis.ui.dashboard.data.DashboardChartData
import sukriti.ngo.mis.ui.dashboard.interfaces.UsageComparisonRequestHandler
import sukriti.ngo.mis.ui.super_admin.fragments.UsageTimeline
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.UserAlertClient
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BwtTimeline : Fragment() {
    private lateinit var binding: FragmentBwtTimelineBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var chartData: UsageComparisonStats
    private var isChartInitialized = false

    companion object {
        private var INSTANCE: BwtTimeline? = null

        fun getInstance(): BwtTimeline {
            return INSTANCE
                ?: BwtTimeline()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBwtTimelineBinding.inflate(inflater, container, false)
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

        viewModel.bwtdashboardChartData.observe(viewLifecycleOwner,bwtDashboardChartDataObserver)
    }

    var bwtDashboardChartDataObserver : Observer<BwtdashboardChartData> = Observer {
        var dashboardChartData = it as BwtdashboardChartData
        var dataDuration = viewModel.getDataDuration()
        var waterRecycled = dashboardChartData.waterRecycled
        var total: List<DailyUsageCount> = emptyList()
        var waterRecycledcount: List<DailyUsageCount> = emptyList()
        var female: List<DailyUsageCount> = emptyList()
        var pd: List<DailyUsageCount> = emptyList()
        var mur: List<DailyUsageCount> = emptyList()
        for(data in waterRecycled) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            total += DailyUsageCount(data.date,data.all)
            waterRecycledcount += DailyUsageCount(data.date,data.bwt)
//            female += DailyUsageCount(data.date,data.fwc)
//            pd += DailyUsageCount(data.date,data.pwc)
//            mur += DailyUsageCount(data.date,data.mur)
        }

        Log.i("dashboardLambda", "data: waterRecycledcount :$waterRecycledcount \nfemale :$female\npwc :$pd\nmur :$mur\ntotal :$total")

        var usagesComparisonStats = UsageComparisonStats(dataDuration.from,dataDuration.to,
            total,waterRecycledcount,female,pd,mur,dataDuration.label)

        dataRequestHandler.onSuccess(usagesComparisonStats)
    }



    var dataRequestHandler: UsageComparisonRequestHandler =
        object : UsageComparisonRequestHandler {
            override fun onSuccess(stats: UsageComparisonStats?) {
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
//            viewModel.getUsageTimelineForDays(duration,dataRequestHandler)
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
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)
        binding.chart.getAxisRight().setEnabled(false)

        isChartInitialized = true
    }

    @SuppressLint("ResourceType")
    private fun setData() {

        Log.i("_setData", "1st init()")

        val totalUsageValues = arrayListOf<Entry>()
        val mwcUsageValues = arrayListOf<Entry>()
//        val fwcUsageValues = arrayListOf<Entry>()
//        val pwcUsageValues = arrayListOf<Entry>()
//        val murUsageValues = arrayListOf<Entry>()
        for((index, item) in chartData.total.withIndex()){
            totalUsageValues.add(Entry(index.toFloat(), item.count.toFloat()))
        }
        for((index, item) in chartData.male.withIndex()){
            mwcUsageValues.add(Entry(index.toFloat(), item.count.toFloat()))
        }
//        for((index, item) in chartData.female.withIndex()){
//            fwcUsageValues.add(Entry(index.toFloat(), item.count.toFloat()))
//        }
//        for((index, item) in chartData.pd.withIndex()){
//            pwcUsageValues.add(Entry(index.toFloat(), item.count.toFloat()))
//        }
//        for((index, item) in chartData.mur.withIndex()){
//            murUsageValues.add(Entry(index.toFloat(), item.count.toFloat()))
//        }

        val set1 = createLineDataSet(totalUsageValues,"Total Water Recycled", Color.parseColor(getString(R.color.total)))
        val set2 = createLineDataSet(mwcUsageValues,"Water Recycled", Color.parseColor(getString(R.color.mwc)))
//        val set3 = createLineDataSet(fwcUsageValues,"FWC Usage", Color.parseColor(getString(R.color.fwc)))
//        val set4 = createLineDataSet(pwcUsageValues,"PWC Usage", Color.parseColor(getString(R.color.pwc)))
//        val set5 = createLineDataSet(murUsageValues,"MUR Usage", Color.parseColor(getString(R.color.mur)))


        val data = LineData(set1,set2)
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
            try{
                var index = value.toInt()
//                var date = DateConverter.toDbDate(reverseDate(chartData.male[index].date))
                var date = DateConverter.lambdaDate(chartData.male[index].date)
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