package sukriti.ngo.mis.ui.super_admin.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.DashboardBarchartFeedbackBinding
import sukriti.ngo.mis.repository.data.DailyFeedbackCount
import sukriti.ngo.mis.repository.data.FeedbackComparisonData
import sukriti.ngo.mis.repository.utils.DateConverter.lambdaDate
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.DashboardChartData
import sukriti.ngo.mis.ui.dashboard.interfaces.FeedbackComparisonRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.Nomenclature.CHART_ANIM_DURATION
import sukriti.ngo.mis.utils.UserAlertClient
import java.text.SimpleDateFormat
import java.util.*


class FeedbackComparison : Fragment() {
    private lateinit var binding: DashboardBarchartFeedbackBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var chartData: FeedbackComparisonData
    private var isChartInitialized = false

    companion object {
        private var INSTANCE: FeedbackComparison? = null

        fun getInstance(): FeedbackComparison {
            return INSTANCE
                ?: FeedbackComparison()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardBarchartFeedbackBinding.inflate(inflater, container, false)
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
        object :
            FeedbackComparisonRequestHandler {
            override fun onSuccess(stats: FeedbackComparisonData?) {
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
//            viewModel.countDailyUserCountForFeedbackForDays(duration,dataRequestHandler)
            }
        }


    private fun initLineChart() {
        Log.i("_setData", "initLineChart")

        //binding.chart.setDrawBorders(true);
        //binding.chart.setDrawBorders(true);
        binding.chart.getDescription().setEnabled(false)
        binding.chart.setPinchZoom(false)
        binding.chart.setDrawBarShadow(false)
        binding.chart.setDrawGridBackground(false)
        binding.chart.getLegend().setEnabled(false)
        binding.chart.getAxisRight().setEnabled(false)

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
        leftAxis.granularity = 1f
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

        isChartInitialized = true
    }

    @SuppressLint("ResourceType")
    private fun setData() {

        Log.i("_setData", "setData()")

        val groupSpace = 0.1f
        val barSpace = 0.03f // x4 DataSet
        val barWidth = 0.15f // x4 DataSet
        // (0.15 + 0.03) * 5 + 0.10 = 1.00 -> interval per "group"

//        val _1starData = arrayListOf<BarEntry>()
        val _2starData = arrayListOf<BarEntry>()
        val _3starData = arrayListOf<BarEntry>()
        val _4starData = arrayListOf<BarEntry>()
        val _5starData = arrayListOf<BarEntry>()

        val COLORS = intArrayOf(
            Color.parseColor(getString(R.color.mwc)),
            Color.parseColor(getString(R.color.fwc)),
            Color.parseColor(getString(R.color.pwc)),
            Color.parseColor(getString(R.color.mur))
        )


//        for ((index, item) in chartData._1starData.withIndex()) {
//            _1starData.add(BarEntry(index.toFloat(), item.userCount.toFloat()))
//        }
        for ((index, item) in chartData._2starData.withIndex()) {
            _2starData.add(BarEntry(index.toFloat(), item.userCount.toFloat()))
        }
        for ((index, item) in chartData._3starData.withIndex()) {
            _3starData.add(BarEntry(index.toFloat(), item.userCount.toFloat()))
        }
        for ((index, item) in chartData._4starData.withIndex()) {
            _4starData.add(BarEntry(index.toFloat(), item.userCount.toFloat()))
        }
        for ((index, item) in chartData._5starData.withIndex()) {
            _5starData.add(BarEntry(index.toFloat(), item.userCount.toFloat()))
        }


//        val set1: BarDataSet
        val set2: BarDataSet
        val set3: BarDataSet
        val set4: BarDataSet
        val set5: BarDataSet

        //if (binding.chart.getData() != null && binding.chart.getData().getDataSetCount() > 0) {
        if (false) {
            Log.i("_setData", "reuse()")

//            set1 = binding.chart.getData().getDataSetByIndex(0) as BarDataSet
            set2 = binding.chart.getData().getDataSetByIndex(1) as BarDataSet
            set3 = binding.chart.getData().getDataSetByIndex(2) as BarDataSet
            set4 = binding.chart.getData().getDataSetByIndex(3) as BarDataSet
            set5 = binding.chart.getData().getDataSetByIndex(4) as BarDataSet
//            set1.values = _1starData
            set2.values = _2starData
            set3.values = _3starData
            set4.values = _4starData
            set5.values = _5starData

            binding.chart.getData().notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        } else {
            Log.i("_setData", "1st init()")

//            set1 = BarDataSet(_1starData, "1 Star")
//            set1.color = Color.parseColor(getString(R.color.rated_1));
            set2 = BarDataSet(_2starData, "2 Star")
            set2.color = Color.parseColor(getString(R.color.mwc));
            set3 = BarDataSet(_3starData, "3 Star")
            set3.color = Color.parseColor(getString(R.color.fwc));
            set4 = BarDataSet(_4starData, "4 Star")
            set4.color = Color.parseColor(getString(R.color.pwc));
            set5 = BarDataSet(_5starData, "5 Star")
            set5.color = Color.parseColor(getString(R.color.mur));

//            set1.setDrawValues(false)
            set2.setDrawValues(false)
            set3.setDrawValues(false)
            set4.setDrawValues(false)
            set5.setDrawValues(false)

            val data = BarData(set2, set3, set4, set5)
            data.setValueFormatter(LargeValueFormatter())
            data.setBarWidth(barWidth)
            //data.setValueTypeface(tfLight)
            binding.chart.setData(data)

            var start = 0f
            binding.chart.groupBars(start, groupSpace, barSpace)
            binding.chart.getXAxis().setAxisMinimum(start)
            binding.chart.getXAxis().setAxisMaximum(
                start + binding.chart.getBarData()
                    .getGroupWidth(groupSpace, barSpace) * _2starData.size
            )

        }


        binding.chart.invalidate()
        binding.chart.animateXY(CHART_ANIM_DURATION, CHART_ANIM_DURATION)
    }

    var formatter: ValueFormatter = object : ValueFormatter() {
        private val mFormat =
            SimpleDateFormat("dd MMM", Locale.ENGLISH)

        override fun getFormattedValue(value: Float): String {
            return try {
                var index = value.toInt()
//                var date = toDbDate(chartData._1starData[index].date)
                var date = lambdaDate(chartData._2starData[index].date)
                Log.i("print", "getFormattedValue: date:" + chartData._2starData[index].date)
                Log.i("print", "getFormattedValue: $index date :$date")
                mFormat.format(date)
            } catch (e: ArrayIndexOutOfBoundsException) {
                Log.e("__formatter_01", "" + value)
                "  "
            } catch (e: IndexOutOfBoundsException) {
                Log.e("__formatter_02", "" + value)
                "  "
            }
        }
    }
}