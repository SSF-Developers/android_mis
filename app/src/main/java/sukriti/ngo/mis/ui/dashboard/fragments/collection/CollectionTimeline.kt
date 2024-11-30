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
import sukriti.ngo.mis.repository.data.DailyChargeCollection
import sukriti.ngo.mis.repository.data.DailyChargeCollectionData
import sukriti.ngo.mis.repository.data.DailyUsageCount
import sukriti.ngo.mis.repository.data.UsageComparisonStats
import sukriti.ngo.mis.repository.utils.DateConverter
import sukriti.ngo.mis.repository.utils.DateConverter.*
import sukriti.ngo.mis.ui.administration.interfaces.RequestHandler
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.DashboardChartData
import sukriti.ngo.mis.ui.dashboard.interfaces.ChargeCollectionComparisonRequestHandler
import sukriti.ngo.mis.ui.dashboard.interfaces.UsageComparisonRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.UserAlertClient
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class CollectionTimeline : Fragment() {
    private lateinit var binding: DashboardTimelineBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var chartData: DailyChargeCollectionData
    private var isChartInitialized = false

    companion object {
        private var INSTANCE: CollectionTimeline? = null

        fun getInstance(): CollectionTimeline {
            return INSTANCE
                ?: CollectionTimeline()
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

    var dashboardChartDataObserver :Observer<DashboardChartData> = Observer {
    var dashboardChartData = it as DashboardChartData
        var dataDuration = viewModel.getDataDuration()
        var collections = dashboardChartData.collection
        var total: List<DailyChargeCollection> = emptyList()
        var male: List<DailyChargeCollection> = emptyList()
        var female: List<DailyChargeCollection> = emptyList()
        var pd: List<DailyChargeCollection> = emptyList()
        var mur: List<DailyChargeCollection> = emptyList()
        for(data in collections) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            total += DailyChargeCollection(data.date,data.all.toFloat())
            male += DailyChargeCollection(data.date,data.mwc.toFloat())
            female += DailyChargeCollection(data.date,data.fwc.toFloat())
            pd += DailyChargeCollection(data.date,data.pwc.toFloat())
            mur += DailyChargeCollection(data.date,data.mur.toFloat())
        }

        Log.i("dashboardLambda", "data: male :$male \nfemale :$female\npwc :$pd\nmur :$mur\ntotal :$total")

        var collectionsComparisonStats = DailyChargeCollectionData(dataDuration.from,dataDuration.to,
            total,male,female,pd,mur,dataDuration.label)

        dataRequestHandler.onSuccess(collectionsComparisonStats)
    }


    var dataRequestHandler: ChargeCollectionComparisonRequestHandler =
        object : ChargeCollectionComparisonRequestHandler {

            override fun onSuccess(stats: DailyChargeCollectionData?) {
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
//            viewModel.getUsageChargeCollectionTimelineForDays(duration,dataRequestHandler)
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
        val fwcUsageValues = arrayListOf<Entry>()
        val pwcUsageValues = arrayListOf<Entry>()
        val murUsageValues = arrayListOf<Entry>()
        for((index, item) in chartData.total.withIndex()){
            totalUsageValues.add(Entry(index.toFloat(), item.amount.toFloat()))
        }
        for((index, item) in chartData.male.withIndex()){
            mwcUsageValues.add(Entry(index.toFloat(), item.amount.toFloat()))
        }
        for((index, item) in chartData.female.withIndex()){
            fwcUsageValues.add(Entry(index.toFloat(), item.amount.toFloat()))
        }
        for((index, item) in chartData.pd.withIndex()){
            pwcUsageValues.add(Entry(index.toFloat(), item.amount.toFloat()))
        }
        for((index, item) in chartData.mur.withIndex()){
            murUsageValues.add(Entry(index.toFloat(), item.amount.toFloat()))
        }

        val set1 = createLineDataSet(totalUsageValues,"Total Usage",Color.parseColor(getString(R.color.total)))
        val set2 = createLineDataSet(mwcUsageValues,"MWC Usage",Color.parseColor(getString(R.color.mwc)))
        val set3 = createLineDataSet(fwcUsageValues,"FWC Usage",Color.parseColor(getString(R.color.fwc)))
        val set4 = createLineDataSet(pwcUsageValues,"PWC Usage",Color.parseColor(getString(R.color.pwc)))
        val set5 = createLineDataSet(murUsageValues,"MUR Usage",Color.parseColor(getString(R.color.mur)))


        val data = LineData(set1,set2,set3,set4,set5)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)
        binding.chart.data = data

        binding.chart.invalidate()
        binding.chart.animateX(Nomenclature.CHART_ANIM_DURATION)
    }

    var formatter: ValueFormatter = object : ValueFormatter() {
        private val mFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)

        override fun getFormattedValue(value: Float): String {
            try{
                var index = value.toInt()
//                var date = toDbDate(chartData.male[index].date)
                var date = lambdaDate(chartData.male[index].date)
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