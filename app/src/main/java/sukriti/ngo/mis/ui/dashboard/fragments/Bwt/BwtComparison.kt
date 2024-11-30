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
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.DashboardBarchartCabinsBinding
import sukriti.ngo.mis.databinding.FragmentBwtComparisionBinding
import sukriti.ngo.mis.repository.data.DailyUsageCount
import sukriti.ngo.mis.repository.data.UsageComparisonStats
import sukriti.ngo.mis.repository.utils.DateConverter
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.BwtdashboardChartData
import sukriti.ngo.mis.ui.dashboard.data.DashboardChartData
import sukriti.ngo.mis.ui.dashboard.interfaces.UsageComparisonRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.UserAlertClient
import java.text.SimpleDateFormat
import java.util.*


class BwtComparison : Fragment() {

    private lateinit var binding: FragmentBwtComparisionBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var chartData: UsageComparisonStats
    private var isChartInitialized = false

    companion object {
        private var INSTANCE: BwtComparison? = null

        fun getInstance(): BwtComparison {
            return INSTANCE
                ?: BwtComparison()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBwtComparisionBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel =
            ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)

        val items = Nomenclature.getDurationLabels()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_duration_selection, items)
        binding.timelineHolder.timeInterval.adapter = adapter
        binding.timelineHolder.timeInterval.onItemSelectedListener = durationSelectionListener
        binding.timelineHolder.timeInterval.setSelection(Nomenclature.DURATION_DEFAULT_SELECTION)
        binding.timelineHolder.timeIntervalContainer.setOnClickListener(View.OnClickListener {
            binding.timelineHolder.timeInterval.performClick()
        })
        viewModel.bwtdashboardChartData.observe(viewLifecycleOwner, bwtDashboardChartDataObserver)
    }

    var bwtDashboardChartDataObserver: Observer<BwtdashboardChartData> = Observer {
        var dashboardChartData = it as BwtdashboardChartData
        var dataDuration = viewModel.getDataDuration()
        var usages = dashboardChartData.waterRecycled
        var total: List<DailyUsageCount> = emptyList()
        var male: List<DailyUsageCount> = emptyList()
        var female: List<DailyUsageCount> = emptyList()
        var pd: List<DailyUsageCount> = emptyList()
        var mur: List<DailyUsageCount> = emptyList()

        for (data in usages) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            total += DailyUsageCount(data.date, data.all)
            male += DailyUsageCount(data.date, data.bwt)
//            female += DailyUsageCount(data.date, data.fwc)
//            pd += DailyUsageCount(data.date, data.pwc)
//            mur += DailyUsageCount(data.date, data.mur)
        }


        var usagesComparisonStats = UsageComparisonStats(
            dataDuration.from, dataDuration.to,
            total, male, female, pd, mur, dataDuration.label
        )

        usageComparisonRequestHandler.onSuccess(usagesComparisonStats)
    }

    var usageComparisonRequestHandler: UsageComparisonRequestHandler =
        object : UsageComparisonRequestHandler {
            override fun onSuccess(stats: UsageComparisonStats?) {
                userAlertClient.closeWaitDialog()
                if (stats != null) {
                    chartData = stats

                    if (!isChartInitialized)
                        initLineChart()
                    setData()
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
//            userAlertClient.showWaitDialog("Loading data...")
//            viewModel.getUsageComparisonForDays(duration,usageComparisonRequestHandler)
            }
        }


    private fun initLineChart() {
        Log.i("_setData", "initLineChart")

        //binding.chart.setDrawBorders(true);
        //binding.chart.setDrawBorders(true);
        binding.chart.description.isEnabled = false
        binding.chart.setPinchZoom(false)
        binding.chart.setDrawBarShadow(false)
        binding.chart.setDrawGridBackground(false)
        binding.chart.legend.isEnabled = false
        binding.chart.axisRight.isEnabled = false

        val xAxis: XAxis = binding.chart.xAxis
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

        isChartInitialized = true
    }

    @SuppressLint("ResourceType")
    private fun setData() {

        Log.i("_setData", "setData()")

        val groupSpace = 0.08f
        val barSpace = 0.03f
        val barWidth = 0.2f

        val COLORS = intArrayOf(
            Color.parseColor(getString(R.color.mwc))
//            Color.parseColor(getString(R.color.fwc)),
//            Color.parseColor(getString(R.color.pwc)),
//            Color.parseColor(getString(R.color.mur))
        )
        val mwcValues = ArrayList<BarEntry>()
//        val fwcValues = ArrayList<BarEntry>()
//        val pwcValues = ArrayList<BarEntry>()
//        val murValues = ArrayList<BarEntry>()
        for ((index, item) in chartData.male.withIndex()) {
            mwcValues.add(BarEntry(index.toFloat(), item.count.toFloat()))
        }
//        for ((index, item) in chartData.female.withIndex()) {
//            fwcValues.add(BarEntry(index.toFloat(), item.count.toFloat()))
//        }
//        for ((index, item) in chartData.pd.withIndex()) {
//            pwcValues.add(BarEntry(index.toFloat(), item.count.toFloat()))
//        }
//        for ((index, item) in chartData.mur.withIndex()) {
//            murValues.add(BarEntry(index.toFloat(), item.count.toFloat()))
//        }

        val set1: BarDataSet
        val set2: BarDataSet
        val set3: BarDataSet
        val set4: BarDataSet

//        if (binding.chart.data != null && binding.chart.data.dataSetCount > 0) {
        if (false){
            Log.i("_setData", "user comparison reuse()")

            set1 = binding.chart.data.getDataSetByIndex(0) as BarDataSet
//            set2 = binding.chart.data.getDataSetByIndex(1) as BarDataSet
//            set3 = binding.chart.data.getDataSetByIndex(2) as BarDataSet
//            set4 = binding.chart.data.getDataSetByIndex(3) as BarDataSet
            set1.values = mwcValues
//            set2.values = fwcValues
//            set3.values = pwcValues
//            set4.values = murValues

            binding.chart.data.notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        } else {
            Log.i("_setData", "user comparison 1st init()")
            set1 = BarDataSet(mwcValues, "MWC")
            set1.color = Color.parseColor(getString(R.color.mwc));
//            set2 = BarDataSet(fwcValues, "FWC")
//            set2.color = Color.parseColor(getString(R.color.fwc));
//            set3 = BarDataSet(pwcValues, "PWC")
//            set3.color = Color.parseColor(getString(R.color.pwc));
//            set4 = BarDataSet(murValues, "MUR")
//            set4.color = Color.parseColor(getString(R.color.mur));

            set1.setDrawValues(false)
//            set2.setDrawValues(false)
//            set3.setDrawValues(false)
//            set4.setDrawValues(false)


            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.barWidth = 0.9f
            binding.chart.data = data
        }


        binding.chart.invalidate()
        binding.chart.animateXY(Nomenclature.CHART_ANIM_DURATION, Nomenclature.CHART_ANIM_DURATION)
    }

    var formatter: ValueFormatter = object : ValueFormatter() {
        private val mFormat =
            SimpleDateFormat("dd MMM", Locale.ENGLISH)

        override fun getFormattedValue(value: Float): String {
            return try {
                var index = value.toInt()
//                var date = toDbDate(chartData.male[index].date)
                var date = DateConverter.lambdaDate(chartData.male[index].date)
                mFormat.format(date)
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.e("__formatter_01", "" + value)
                " xx "
            } catch (e: IndexOutOfBoundsException) {
                Log.e("__formatter_02", "" + value)
                " xx "
            }
        }
    }
}