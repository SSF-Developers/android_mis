package sukriti.ngo.mis.ui.reports.chartClient

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import sukriti.ngo.mis.R
import sukriti.ngo.mis.repository.utils.DateConverter
import sukriti.ngo.mis.ui.reports.data.UsageReportData
import sukriti.ngo.mis.utils.Nomenclature
import java.text.SimpleDateFormat
import java.util.*

class TicketResolutionTimelineChartClient(ctx: Context, graph:LineChart, data: UsageReportData) {

    var barChart:LineChart = graph
    var chartData = data.ticketResolutionTimeline
    var context = ctx
    private var isChartInitialized = false

    init {
        if(!isChartInitialized)
            initLineChart()
        setData()
    }

    private fun initLineChart() {
        Log.i("_setData", "initLineChart")

        //binding.chart.setDrawBorders(true);
        //binding.chart.setDrawBorders(true);
        barChart.getDescription().setEnabled(false)
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(false)
        barChart.getLegend().setEnabled(false)
        barChart.getAxisRight().setEnabled(false)

        val xAxis: XAxis = barChart.getXAxis()
        //xAxis.typeface = tfLight
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.valueFormatter = formatter
        Log.i("_setData", "valueFormatter")


        val leftAxis: YAxis = barChart.getAxisLeft()
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

        Log.i("_setData", "1st init()")

        val totalUsageValues = arrayListOf<Entry>()
        val mwcUsageValues = arrayListOf<Entry>()
        val fwcUsageValues = arrayListOf<Entry>()
        val pwcUsageValues = arrayListOf<Entry>()
        val murUsageValues = arrayListOf<Entry>()
        for((index, item) in chartData.total.withIndex()){
            totalUsageValues.add(Entry(index.toFloat(), item.activeTicketCount.toFloat()))
        }
        for((index, item) in chartData.male.withIndex()){
            mwcUsageValues.add(Entry(index.toFloat(), item.activeTicketCount.toFloat()))
        }
        for((index, item) in chartData.female.withIndex()){
            fwcUsageValues.add(Entry(index.toFloat(), item.activeTicketCount.toFloat()))
        }
        for((index, item) in chartData.pd.withIndex()){
            pwcUsageValues.add(Entry(index.toFloat(), item.activeTicketCount.toFloat()))
        }
        for((index, item) in chartData.mur.withIndex()){
            murUsageValues.add(Entry(index.toFloat(), item.activeTicketCount.toFloat()))
        }

        val set1 = createLineDataSet(totalUsageValues,"Total Usage",Color.parseColor(context.getString(R.color.total)))
        val set2 = createLineDataSet(mwcUsageValues,"MWC Usage",Color.parseColor(context.getString(R.color.mwc)))
        val set3 = createLineDataSet(fwcUsageValues,"FWC Usage",Color.parseColor(context.getString(R.color.fwc)))
        val set4 = createLineDataSet(pwcUsageValues,"PWC Usage",Color.parseColor(context.getString(R.color.pwc)))
        val set5 = createLineDataSet(murUsageValues,"MUR Usage",Color.parseColor(context.getString(R.color.mur)))


        val data = LineData(set1,set2,set3,set4,set5)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)
        barChart.data = data

        barChart.invalidate()

//        barChart.animateX(Nomenclature.CHART_ANIM_DURATION)
    }

    var formatter: ValueFormatter = object : ValueFormatter() {
        private val mFormat =
            SimpleDateFormat("dd MMM", Locale.ENGLISH)

        override fun getFormattedValue(value: Float): String {
            try{
                var index = value.toInt()
                var date = DateConverter.toDbDate(chartData.male[index].date)
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
        set.highLightColor = Color.parseColor(context.getString(R.color.primary))

        return set
    }

    fun setFormatter(){
        barChart.xAxis.valueFormatter = formatter
    }
}