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
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.DashboardBarchartCabinsBinding
import sukriti.ngo.mis.repository.data.DailyChargeCollection
import sukriti.ngo.mis.repository.data.DailyChargeCollectionData
import sukriti.ngo.mis.repository.data.DailyUsageCount
import sukriti.ngo.mis.repository.data.UsageComparisonStats
import sukriti.ngo.mis.repository.utils.DateConverter.lambdaDate
import sukriti.ngo.mis.repository.utils.DateConverter.toDbDate
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.DashboardChartData
import sukriti.ngo.mis.ui.dashboard.interfaces.ChargeCollectionComparisonRequestHandler
import sukriti.ngo.mis.ui.dashboard.interfaces.UsageComparisonRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.Nomenclature.CHART_ANIM_DURATION
import sukriti.ngo.mis.utils.UserAlertClient
import java.text.SimpleDateFormat
import java.util.*

class CollectionComparison : Fragment() {
    private lateinit var binding: DashboardBarchartCabinsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient : NavigationClient
    private lateinit var chartData: DailyChargeCollectionData
    private var isChartInitialized = false

    companion object {
        private var INSTANCE: CollectionComparison? = null

        fun getInstance(): CollectionComparison {
            return INSTANCE
                ?: CollectionComparison()
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
        viewModel.dashboardChartData.observe(viewLifecycleOwner, dashboardChartDataObserver)
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
        for (data in collections) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            total += DailyChargeCollection(data.date, data.all.toFloat())
            male += DailyChargeCollection(data.date, data.mwc.toFloat())
            female += DailyChargeCollection(data.date, data.fwc.toFloat())
            pd += DailyChargeCollection(data.date, data.pwc.toFloat())
            mur += DailyChargeCollection(data.date, data.mur.toFloat())
        }


        var usagesComparisonStats = DailyChargeCollectionData(
            dataDuration.from, dataDuration.to,
            total, male, female, pd, mur, dataDuration.label
        )

        requestHandler.onSuccess(usagesComparisonStats)
    }


    var requestHandler: ChargeCollectionComparisonRequestHandler =
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
            var duration = Nomenclature.getDuration(index)
//            userAlertClient.showWaitDialog("Loading data...")
//            viewModel.getDailyUsageChargeCollectionComparisonForDays(duration,requestHandler)
        }
    }


    private fun initLineChart(){
        Log.i("_setData", "initLineChart" )

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
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

        isChartInitialized = true
    }

    @SuppressLint("ResourceType")
    private fun setData() {

        Log.i("_setData", "setData()")

        val groupSpace = 0.08f
        val barSpace = 0.03f
        val barWidth = 0.2f

        val mwcValues = ArrayList<BarEntry>()
        val fwcValues = ArrayList<BarEntry>()
        val pwcValues = ArrayList<BarEntry>()
        val murValues = ArrayList<BarEntry>()
        for((index, item) in chartData.male.withIndex()){
            mwcValues.add(BarEntry(index.toFloat(), item.amount.toFloat()))
        }
        for((index, item) in chartData.female.withIndex()){
            fwcValues.add(BarEntry(index.toFloat(), item.amount.toFloat()))
        }
        for((index, item) in chartData.pd.withIndex()){
            pwcValues.add(BarEntry(index.toFloat(), item.amount.toFloat()))
        }
        for((index, item) in chartData.mur.withIndex()){
            murValues.add(BarEntry(index.toFloat(), item.amount.toFloat()))
        }

        val set1: BarDataSet
        val set2: BarDataSet
        val set3: BarDataSet
        val set4: BarDataSet

        //if (binding.chart.getData() != null && binding.chart.getData().getDataSetCount() > 0) {
        if(false){
            Log.i("_setData", "reuse()")

            set1 = binding.chart.getData().getDataSetByIndex(0) as BarDataSet
            set2 = binding.chart.getData().getDataSetByIndex(1) as BarDataSet
            set3 = binding.chart.getData().getDataSetByIndex(2) as BarDataSet
            set4 = binding.chart.getData().getDataSetByIndex(3) as BarDataSet
            set1.values = mwcValues
            set2.values = fwcValues
            set3.values = pwcValues
            set4.values = murValues

            binding.chart.getData().notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        } else {
            Log.i("_setData", "1st init()")

            set1 = BarDataSet(mwcValues, "MWC")
            set1.color = Color.parseColor(getString(R.color.mwc));
            set2 = BarDataSet(fwcValues, "FWC")
            set2.color = Color.parseColor(getString(R.color.fwc));
            set3 = BarDataSet(pwcValues, "PWC")
            set3.color = Color.parseColor(getString(R.color.pwc));
            set4 = BarDataSet(murValues, "MUR")
            set4.color = Color.parseColor(getString(R.color.mur));

            set1.setDrawValues(false)
            set2.setDrawValues(false)
            set3.setDrawValues(false)
            set4.setDrawValues(false)

            val data = BarData(set1, set2, set3, set4)
            data.setValueFormatter(LargeValueFormatter())
            data.setBarWidth(barWidth)
            //data.setValueTypeface(tfLight)
            binding.chart.setData(data)

            var start = 0f
            binding.chart.groupBars(start, groupSpace, barSpace)
            binding.chart.getXAxis().setAxisMinimum(start)
            binding.chart.getXAxis().setAxisMaximum(
                start + binding.chart.getBarData().getGroupWidth(groupSpace, barSpace) * chartData.male.size
            )
            binding.chart.z

        }


        binding.chart.invalidate()
        binding.chart.animateXY(CHART_ANIM_DURATION, CHART_ANIM_DURATION)
    }

   var formatter: ValueFormatter = object : ValueFormatter() {
        private val mFormat =
            SimpleDateFormat("dd MMM", Locale.ENGLISH)

        override fun getFormattedValue(value: Float): String {
            try{
                var index = value.toInt()
//                var date = toDbDate(chartData.male[index].date)
                var date = lambdaDate(chartData.male[index].date)
                return mFormat.format(date)
            }catch (e: ArrayIndexOutOfBoundsException ){
                Log.e("__formatter_01",""+value)
                return " xx "
            }catch (e: IndexOutOfBoundsException){
                Log.e("__formatter_02",""+value)
                return " xx "
            }
        }
    }
}