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
import sukriti.ngo.mis.databinding.DashboardBarchartFeedbackBinding
import sukriti.ngo.mis.repository.data.FeedbackSummaryData
import sukriti.ngo.mis.repository.data.FeedbackWiseUserCount
import sukriti.ngo.mis.repository.data.UsageSummaryStats
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.PieChartData
import sukriti.ngo.mis.ui.dashboard.interfaces.FeedbackSummaryRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.Nomenclature.CHART_ANIM_DURATION
import sukriti.ngo.mis.utils.UserAlertClient
import java.util.*

class FeedbackSummary : Fragment() {
    private lateinit var binding: DashboardBarchartFeedbackBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var chartData: FeedbackSummaryData
    private var isChartInitialized = false

    companion object {
        private var INSTANCE: FeedbackSummary? = null

        fun getInstance(): FeedbackSummary {
            return INSTANCE
                ?: FeedbackSummary()
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
        viewModel.pieChartData.observe(viewLifecycleOwner, pieChartObserver)
    }


    var pieChartObserver: Observer<PieChartData> = Observer {
        var pieChartData = it as PieChartData
        var dataDuration = viewModel.getDataDuration()
        var pieChartFeedBack = pieChartData.feedback
        var male = 0.0
        var female = 0.0
        var pwc = 0.0
        var mur = 0.0
        var total = 0.0

        var lMale: List<FeedbackWiseUserCount> = emptyList()
        var lFemale: List<FeedbackWiseUserCount> = emptyList()
        var lPwc: List<FeedbackWiseUserCount> = emptyList()
        var lMur: List<FeedbackWiseUserCount> = emptyList()
        var lTotal: List<FeedbackWiseUserCount> = emptyList()

        for (data in pieChartFeedBack) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            when (data.name) {
                "MWC" -> male += data.value
                "FWC" -> female += data.value
                "PWC" -> pwc += data.value
                "MUR" -> mur += data.value
            }
        }

//        total = (male+female+pwc+mur)/pieChartFeedBack.size

        lMale += FeedbackWiseUserCount(male.toInt(), 0)
        lFemale += FeedbackWiseUserCount(female.toInt(), 0)
        lPwc += FeedbackWiseUserCount(pwc.toInt(), 0)
        lMur += FeedbackWiseUserCount(mur.toInt(), 0)
//    lTotal +=FeedbackWiseUserCount(total.toInt(),0)
        Log.i(
            "dashboardLambda",
            "data: male :$male \nfemale :$female\npwc :$pwc\nmur :$mur\ntotal :$total"
        )

        var feedbackSummaryData = FeedbackSummaryData(
            dataDuration.from, dataDuration.to,
            lMale, lFemale, lPwc, lMur, dataDuration.label
        )

        feedbackSummaryRequestHandler.onSuccess(feedbackSummaryData)
    }


    var feedbackSummaryRequestHandler: FeedbackSummaryRequestHandler =
        object : FeedbackSummaryRequestHandler {
            override fun onSuccess(stats: FeedbackSummaryData?) {
                userAlertClient.closeWaitDialog()
                if (stats != null) {
                    chartData = stats

                    if (!isChartInitialized)
                        initLineChart()
                    setData()
                    //setLineChartData(4,15f)
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
                userAlertClient.showWaitDialog("Loading data...")
                viewModel.countUsersForFeedbackForDays(duration, feedbackSummaryRequestHandler)
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
        xAxis.isEnabled =false
        //xAxis.typeface = tfLight
//        xAxis.granularity = 1f
//        xAxis.setCenterAxisLabels(true)
//        xAxis.valueFormatter = formatter

        val leftAxis: YAxis = binding.chart.getAxisLeft()
        //leftAxis.typeface = tfLight
        leftAxis.valueFormatter = LargeValueFormatter()
//        leftAxis.setDrawLabels(true)
//        leftAxis.setDrawLabels(false)
//        leftAxis.setCenterAxisLabels(false)
        leftAxis.setDrawGridLines(false)
//        leftAxis.granularity = 1f
        leftAxis.spaceTop = 5f
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

//        val rated1Values = ArrayList<BarEntry>()
//        val rated2Values = ArrayList<BarEntry>()
//        val rated3Values = ArrayList<BarEntry>()
//        val rated4Values = ArrayList<BarEntry>()
//        val rated5Values = ArrayList<BarEntry>()

        val COLORS = intArrayOf(
            Color.parseColor(getString(R.color.mwc)),
            Color.parseColor(getString(R.color.fwc)),
            Color.parseColor(getString(R.color.pwc)),
            Color.parseColor(getString(R.color.mur))
        )

        val values = ArrayList<BarEntry>()
        values.add(BarEntry(1f, chartData.mwc[0].feedback.toFloat()))
        values.add(BarEntry(2f, chartData.fwc[0].feedback.toFloat()))
        values.add(BarEntry(3f, chartData.pwc[0].feedback.toFloat()))
        values.add(BarEntry(4f, chartData.mur[0].feedback.toFloat()))

//        rated1Values.add(BarEntry(0f,chartData.mwc[0].userCount.toFloat()))
//        rated1Values.add(BarEntry(1f,chartData.fwc[0].userCount.toFloat()))
//        rated1Values.add(BarEntry(2f,chartData.pwc[0].userCount.toFloat()))
//        rated1Values.add(BarEntry(3f,chartData.mur[0].userCount.toFloat()))

//        rated2Values.add(BarEntry(0f,chartData.mwc[1].userCount.toFloat()))
//        rated2Values.add(BarEntry(1f,chartData.fwc[1].userCount.toFloat()))
//        rated2Values.add(BarEntry(2f,chartData.pwc[1].userCount.toFloat()))
//        rated2Values.add(BarEntry(3f,chartData.mur[1].userCount.toFloat()))

//        rated3Values.add(BarEntry(0f,chartData.mwc[2].userCount.toFloat()))
//        rated3Values.add(BarEntry(1f,chartData.fwc[2].userCount.toFloat()))
//        rated3Values.add(BarEntry(2f,chartData.pwc[2].userCount.toFloat()))
//        rated3Values.add(BarEntry(3f,chartData.mur[2].userCount.toFloat()))

//        rated4Values.add(BarEntry(0f,chartData.mwc[3].userCount.toFloat()))
//        rated4Values.add(BarEntry(1f,chartData.fwc[3].userCount.toFloat()))
//        rated4Values.add(BarEntry(2f,chartData.pwc[3].userCount.toFloat()))
//        rated4Values.add(BarEntry(3f,chartData.mur[3].userCount.toFloat()))

//        rated5Values.add(BarEntry(0f,chartData.mwc[4].userCount.toFloat()))
//        rated5Values.add(BarEntry(1f,chartData.fwc[4].userCount.toFloat()))
//        rated5Values.add(BarEntry(2f,chartData.pwc[4].userCount.toFloat()))
//        rated5Values.add(BarEntry(3f,chartData.mur[4].userCount.toFloat()))

//        val set1: BarDataSet
//        val set2: BarDataSet
//        val set3: BarDataSet
//        val set4: BarDataSet
//        val set5: BarDataSet

        if (binding.chart.getData() != null && binding.chart.getData().getDataSetCount() > 0) {
            Log.i("_setData", "reuse()")
            val set1: BarDataSet = binding.chart.getData().getDataSetByIndex(0) as BarDataSet
//            set2 = binding.chart.getData().getDataSetByIndex(1) as BarDataSet
//            set3 = binding.chart.getData().getDataSetByIndex(2) as BarDataSet
//            set4 = binding.chart.getData().getDataSetByIndex(3) as BarDataSet
//            set5 = binding.chart.getData().getDataSetByIndex(4) as BarDataSet
            set1.values = values
//            set2.values = rated2Values
//            set3.values = rated3Values
//            set4.values = rated4Values
//            set5.values = rated5Values
            binding.chart.getData().notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        } else {
            Log.i("_setData", "1st init()")

            val set1 = BarDataSet(values, chartData.durationLabel)
//            set1.color = Color.parseColor(getString(R.color.rated_1));
//            set2 = BarDataSet(rated2Values, "Rated 2*")
//            set2.color = Color.parseColor(getString(R.color.rated_2));
//            set3 = BarDataSet(rated3Values, "Rated 3*")
//            set3.color = Color.parseColor(getString(R.color.rated_3));
//            set4 = BarDataSet(rated4Values, "Rated 4*")
//            set4.color = Color.parseColor(getString(R.color.rated_4));
//               set5 = BarDataSet(rated5Values, "Rated 5*")
//            set5.color = Color.parseColor(getString(R.color.rated_5));
            set1.setDrawIcons(false)
            set1.setColors(*COLORS)
            set1.setDrawValues(true)
//            set2.setDrawValues(false)
//            set3.setDrawValues(false)
//            set4.setDrawValues(false)
//            set5.setDrawValues(false)


            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)

            //val data = BarData(set1, set2)
            val data = BarData(dataSets)
            data.setValueFormatter(LargeValueFormatter())
            data.setValueTextSize(10f)
            data.setBarWidth(0.9f)
//            data.setBarWidth(barWidth)
            //data.setValueTypeface(tfLight)
            binding.chart.setData(data)

//            var start = 0f
//            binding.chart.groupBars(start, groupSpace, barSpace)
//            binding.chart.getXAxis().setAxisMinimum(start)
//            binding.chart.getXAxis().setAxisMaximum(
//                start + binding.chart.getBarData().getGroupWidth(groupSpace, barSpace) * 4
//            )

        }


        binding.chart.invalidate()
        binding.chart.animateXY(CHART_ANIM_DURATION, CHART_ANIM_DURATION)
    }

    fun getY(rating: Int, index: Int): Float {
        var list: List<FeedbackWiseUserCount> = arrayListOf()

        when (index) {
            0 -> {
                list = chartData.mwc
            }
            0 -> {
                list = chartData.fwc
            }
            0 -> {
                list = chartData.pwc
            }
            0 -> {
                list = chartData.mur
            }
        }

        var userCountForRating = -1f
        for (item in list) {
            if (item.feedback == rating)
                userCountForRating = item.userCount.toFloat()
        }

        return userCountForRating
    }

    var formatter: ValueFormatter = object : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return "" + value.toInt()
//            when(value){
//                0f->{
//                    return "MWC"
//                }
//                1f->{
//                    return "FWC"
//                }
//                2f->{
//                    return "PWC"
//                }
//                3f->{
//                    return "MUR"
//                }
        }

//            return ""+value+"";
    }

}