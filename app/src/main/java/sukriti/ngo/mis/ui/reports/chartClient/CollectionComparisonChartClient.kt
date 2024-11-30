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

class CollectionComparisonChartClient(ctx: Context, graph:BarChart, data: UsageReportData) {

    var barChart:BarChart = graph
    var chartData = data.collectionComparison
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
        barChart.setDrawBarShadow(false)
        barChart.setDrawGridBackground(false)
        barChart.getLegend().setEnabled(false)
        barChart.getAxisRight().setEnabled(false)

        val xAxis: XAxis = barChart.getXAxis()
        //xAxis.typeface = tfLight
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.valueFormatter = formatter

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

            set1 = barChart.getData().getDataSetByIndex(0) as BarDataSet
            set2 = barChart.getData().getDataSetByIndex(1) as BarDataSet
            set3 = barChart.getData().getDataSetByIndex(2) as BarDataSet
            set4 = barChart.getData().getDataSetByIndex(3) as BarDataSet
            set1.values = mwcValues
            set2.values = fwcValues
            set3.values = pwcValues
            set4.values = murValues

            barChart.getData().notifyDataChanged()
            barChart.notifyDataSetChanged()
        } else {
            Log.i("_setData", "1st init()")

            set1 = BarDataSet(mwcValues, "MWC")
            set1.color = Color.parseColor(context.getString(R.color.mwc));
            set2 = BarDataSet(fwcValues, "FWC")
            set2.color = Color.parseColor(context.getString(R.color.fwc));
            set3 = BarDataSet(pwcValues, "PWC")
            set3.color = Color.parseColor(context.getString(R.color.pwc));
            set4 = BarDataSet(murValues, "MUR")
            set4.color = Color.parseColor(context.getString(R.color.mur));

            set1.setDrawValues(false)
            set2.setDrawValues(false)
            set3.setDrawValues(false)
            set4.setDrawValues(false)

            val data = BarData(set1, set2, set3, set4)
            data.setValueFormatter(LargeValueFormatter())
            data.setBarWidth(barWidth)
            //data.setValueTypeface(tfLight)
            barChart.setData(data)

            var start = 0f
            barChart.groupBars(start, groupSpace, barSpace)
            barChart.getXAxis().setAxisMinimum(start)
            barChart.getXAxis().setAxisMaximum(
                start + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * chartData.male.size
            )

        }


        barChart.invalidate()
//        barChart.animateXY(Nomenclature.CHART_ANIM_DURATION, Nomenclature.CHART_ANIM_DURATION)
    }

    var formatter: ValueFormatter = object : ValueFormatter() {
        private val mFormat =
            SimpleDateFormat("dd MMM", Locale.ENGLISH)

        override fun getFormattedValue(value: Float): String {
            try{
                var index = value.toInt()
//                var date = DateConverter.toDbDate(chartData.male[index].date)
                var date = DateConverter.lambdaDate(chartData.male[index].date)
                Log.e("__formatter_00", "" + chartData.male[index].date)
                return mFormat.format(date)
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