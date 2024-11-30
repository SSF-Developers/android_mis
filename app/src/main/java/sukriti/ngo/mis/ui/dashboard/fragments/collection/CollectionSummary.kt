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
import sukriti.ngo.mis.databinding.DashboardBarchartCabinsBinding
import sukriti.ngo.mis.repository.data.ChargeCollectionStats
import sukriti.ngo.mis.repository.data.UsageSummaryStats
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.PieChartData
import sukriti.ngo.mis.ui.dashboard.interfaces.ChargeCollectionSummaryRequestHandler
import sukriti.ngo.mis.ui.dashboard.interfaces.UsageSummaryRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.Nomenclature.CHART_ANIM_DURATION
import sukriti.ngo.mis.utils.UserAlertClient
import java.util.*

class CollectionSummary : Fragment() {
    private lateinit var binding: DashboardBarchartCabinsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var chartData: ChargeCollectionStats
    private var isChartInitialized = false

    companion object {
        private var INSTANCE: CollectionSummary? = null

        fun getInstance(): CollectionSummary {
            return INSTANCE
                ?: CollectionSummary()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardBarchartCabinsBinding.inflate(inflater, container, false)
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
        viewModel.pieChartData.observe(viewLifecycleOwner,pieChartObserver)
    }


    var  pieChartObserver :Observer<PieChartData> = Observer {
        var pieChartData = it as PieChartData
        var dataDuration = viewModel.getDataDuration()
        var pieChartUsages = pieChartData.usage
        var male =0
        var female = 0
        var pwc = 0
        var mur = 0
        var total = 0

        for(data in pieChartUsages) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            when (data.name) {
                "MWC" -> male += data.value
                "FWC" -> female += data.value
                "PWC" -> pwc += data.value
                "MUR" -> mur += data.value
            }
        }

        total = male+female+pwc+mur
        Log.i("dashboardLambda", "data: male :$male \nfemale :$female\npwc :$pwc\nmur :$mur\ntotal :$total")

        var usageSummaryStats =ChargeCollectionStats(dataDuration.from,dataDuration.to,
            total.toFloat(),male.toFloat(),female.toFloat(),pwc.toFloat(),mur.toFloat(),dataDuration.label)

        requestHandler.onSuccess(usageSummaryStats)
    }

    var requestHandler: ChargeCollectionSummaryRequestHandler =
        object : ChargeCollectionSummaryRequestHandler {
            override fun onSuccess(summary: ChargeCollectionStats?) {
                userAlertClient.closeWaitDialog()
                if (summary != null) {
                    chartData = summary

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
            var duration = Nomenclature.getDuration(index)
//            userAlertClient.showWaitDialog("Loading data...")
//            viewModel.getTotalUsageChargeCollectionForDays(duration,requestHandler)
        }

    }

    private fun initLineChart() {
        Log.i("_setData", "initLineChart" )

        //binding.chart.setDrawBorders(true);
        binding.chart.setPinchZoom(false)
        binding.chart.setDrawBarShadow(false)
        binding.chart.setDrawGridBackground(false)
        binding.chart.getDescription().setEnabled(false)
        binding.chart.getLegend().setEnabled(false)
        binding.chart.getAxisRight().setEnabled(false)

        val xAxis: XAxis = binding.chart.getXAxis()
        xAxis.isEnabled = false
        //xAxis.typeface = tfLight
        //xAxis.granularity = 1f
        //xAxis.setCenterAxisLabels(true)
        //xAxis.valueFormatter = formatter


        val leftAxis: YAxis = binding.chart.getAxisLeft()
        //leftAxis.typeface = tfLight
        leftAxis.valueFormatter = LargeValueFormatter()
        leftAxis.setDrawGridLines(false)
        leftAxis.spaceTop = 5f
        leftAxis.axisMinimum = 0f

        isChartInitialized = true
    }

    @SuppressLint("ResourceType")
    private fun setData() {
        val COLORS = intArrayOf(
            Color.parseColor(getString(R.color.mwc)),
            Color.parseColor(getString(R.color.fwc)),
            Color.parseColor(getString(R.color.pwc)),
            Color.parseColor(getString(R.color.mur))
        )

        val values = ArrayList<BarEntry>()
        values.add(BarEntry(1f, chartData.male.toFloat()))
        values.add(BarEntry(2f, chartData.female.toFloat()))
        values.add(BarEntry(3f, chartData.pd.toFloat()))
        values.add(BarEntry(4f, chartData.mur.toFloat()))

        if (binding.chart.getData() != null &&
            binding.chart.getData().getDataSetCount() > 0
        ) {
            val set = binding.chart.getData().getDataSetByIndex(0) as BarDataSet
            set.values = values
            binding.chart.getData().notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        } else {
            val set = BarDataSet(values, chartData.durationLabel)
            set.setDrawIcons(false)
            set.setColors(*COLORS)
            set.setDrawValues(false)

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set)

            val data = BarData(dataSets)
            data.setValueTextSize(10f)
            data.barWidth = 0.9f
            binding.chart.data = data
        }

        binding.chart.invalidate()
        binding.chart.animateXY(CHART_ANIM_DURATION, CHART_ANIM_DURATION)
    }

    var formatter: ValueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "" + value.toInt()
        }
    }
}