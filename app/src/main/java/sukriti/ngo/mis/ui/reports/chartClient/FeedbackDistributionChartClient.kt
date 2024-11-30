package sukriti.ngo.mis.ui.reports.chartClient

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.charts.BarChart
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
import sukriti.ngo.mis.repository.utils.DateConverter
import sukriti.ngo.mis.ui.reports.data.UsageReportData
import sukriti.ngo.mis.utils.Nomenclature
import java.text.SimpleDateFormat
import java.util.*

class FeedbackDistributionChartClient(ctx: Context, graph:BarChart, data: UsageReportData) {

    var barChart:BarChart = graph
    var chartData = data.feedbackComparison
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
        xAxis.isEnabled = false
        //xAxis.typeface = tfLight
//        xAxis.granularity = 1f
//        xAxis.setCenterAxisLabels(true)
//        xAxis.valueFormatter = formatter
        Log.i("_setData", "valueFormatter")


        val leftAxis: YAxis = barChart.getAxisLeft()
        //leftAxis.typeface = tfLight
        leftAxis.valueFormatter = LargeValueFormatter()
//        leftAxis.setDrawLabels(true)
//        leftAxis.setCenterAxisLabels(false)
        leftAxis.setDrawGridLines(false)
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

        var COLORS :IntArray = IntArray(4)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            COLORS = intArrayOf(
                context.getColor(R.color.mwc),
                context.getColor(R.color.fwc),
                context.getColor(R.color.pwc),
                context.getColor(R.color.mur)
            )
        }

        Log.i("reportLambda", "setData feedBack: "+Gson().toJson(chartData))
        Log.i("reportLambda", "mur feedBack: "+Gson().toJson(chartData.mur[0].feedback))
        Log.i("reportLambda", "mwc feedBack: "+Gson().toJson(chartData.mwc[0].feedback))
        Log.i("reportLambda", "fwc feedBack: "+Gson().toJson(chartData.fwc[0].feedback))
        Log.i("reportLambda", "pwc feedBack: "+Gson().toJson(chartData.pwc[0].feedback))

        val values = ArrayList<BarEntry>()
        values.add(BarEntry(1f, chartData.mwc[0].feedback.toFloat()))
        values.add(BarEntry(2f, chartData.fwc[0].feedback.toFloat()))
        values.add(BarEntry(3f, chartData.pwc[0].feedback.toFloat()))
        values.add(BarEntry(4f, chartData.mur[0].feedback.toFloat()))


//        rated1Values.add(BarEntry(0f,chartData.mwc[0].userCount.toFloat()))
//        rated1Values.add(BarEntry(1f,chartData.fwc[0].userCount.toFloat()))
//        rated1Values.add(BarEntry(2f,chartData.pwc[0].userCount.toFloat()))
//        rated1Values.add(BarEntry(3f,chartData.mur[0].userCount.toFloat()))
//
//        rated2Values.add(BarEntry(0f,chartData.mwc[1].userCount.toFloat()))
//        rated2Values.add(BarEntry(1f,chartData.fwc[1].userCount.toFloat()))
//        rated2Values.add(BarEntry(2f,chartData.pwc[1].userCount.toFloat()))
//        rated2Values.add(BarEntry(3f,chartData.mur[1].userCount.toFloat()))
//
//        rated3Values.add(BarEntry(0f,chartData.mwc[2].userCount.toFloat()))
//        rated3Values.add(BarEntry(1f,chartData.fwc[2].userCount.toFloat()))
//        rated3Values.add(BarEntry(2f,chartData.pwc[2].userCount.toFloat()))
//        rated3Values.add(BarEntry(3f,chartData.mur[2].userCount.toFloat()))
//
//        rated4Values.add(BarEntry(0f,chartData.mwc[3].userCount.toFloat()))
//        rated4Values.add(BarEntry(1f,chartData.fwc[3].userCount.toFloat()))
//        rated4Values.add(BarEntry(2f,chartData.pwc[3].userCount.toFloat()))
//        rated4Values.add(BarEntry(3f,chartData.mur[3].userCount.toFloat()))
//
//        rated5Values.add(BarEntry(0f,chartData.mwc[4].userCount.toFloat()))
//        rated5Values.add(BarEntry(1f,chartData.fwc[4].userCount.toFloat()))
//        rated5Values.add(BarEntry(2f,chartData.pwc[4].userCount.toFloat()))
//        rated5Values.add(BarEntry(3f,chartData.mur[4].userCount.toFloat()))
//
//        val set1: BarDataSet
//        val set2: BarDataSet
//        val set3: BarDataSet
//        val set4: BarDataSet
//        val set5: BarDataSet

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
//        if(false){
            Log.i("_setData", "reuse()")

            val set1 = barChart.getData().getDataSetByIndex(0) as BarDataSet
//            set2 = barChart.getData().getDataSetByIndex(1) as BarDataSet
//            set3 = barChart.getData().getDataSetByIndex(2) as BarDataSet
//            set4 = barChart.getData().getDataSetByIndex(3) as BarDataSet
//            set5 = barChart.getData().getDataSetByIndex(4) as BarDataSet
            set1.values = values
//            set2.values = rated2Values
//            set3.values = rated3Values
//            set4.values = rated4Values
//            set5.values = rated5Values
            barChart.getData().notifyDataChanged()
            barChart.notifyDataSetChanged()
        } else {
            Log.i("_setData", "1st init()")

            val set1 = BarDataSet(values, chartData.durationLabel)
//            set1.color = Color.parseColor(context.getString(R.color.rated_1));
//            set2 = BarDataSet(rated2Values, "Rated 2*")
//            set2.color = Color.parseColor(context.getString(R.color.rated_2));
//            set3 = BarDataSet(rated3Values, "Rated 3*")
//            set3.color = Color.parseColor(context.getString(R.color.rated_3));
//            set4 = BarDataSet(rated4Values, "Rated 4*")
//            set4.color = Color.parseColor(context.getString(R.color.rated_4));
//            set5 = BarDataSet(rated5Values, "Rated 5*")
//            set5.color = Color.parseColor(context.getString(R.color.rated_5));
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
            val _data =BarData(dataSets)
            _data.setValueFormatter(LargeValueFormatter())
            _data.setValueTextSize(10f)
            _data.setBarWidth(0.9f)
//            data.setBarWidth(barWidth)
            //data.setValueTypeface(tfLight)
            barChart.setData(_data)

//            var start = 0f
//            binding.chart.groupBars(start, groupSpace, barSpace)
//            binding.chart.getXAxis().setAxisMinimum(start)
//            binding.chart.getXAxis().setAxisMaximum(
//                start + binding.chart.getBarData().getGroupWidth(groupSpace, barSpace) * 4
//            )




//        //val data = BarData(set1, set2)
//            val data = BarData(set1, set2, set3, set4,set5)
//            //data.setValueFormatter(LargeValueFormatter())
//            data.setBarWidth(barWidth)
//            //data.setValueTypeface(tfLight)
//            barChart.setData(data)
//
//            var start = 0f
//            barChart.groupBars(start, groupSpace, barSpace)
//            barChart.getXAxis().setAxisMinimum(start)
//            barChart.getXAxis().setAxisMaximum(
//                start + barChart.getBarData().getGroupWidth(groupSpace, barSpace) * 4
//            )

        }


        barChart.invalidate()
        barChart.animateXY(Nomenclature.CHART_ANIM_DURATION, Nomenclature.CHART_ANIM_DURATION)
    }

    fun setFormatter(){
        barChart.xAxis.valueFormatter = formatter
        barChart.axisLeft.valueFormatter = leftAxisFormatter
    }

    var formatter: ValueFormatter = object : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            return "" + value.toInt()
//            when (value) {
//                0f -> {
//                    return "MWC"
//                }
//                1f -> {
//                    return "FWC"
//                }
//                2f -> {
//                    return "PWC"
//                }
//                3f -> {
//                    return "MUR"
//                }
//            }
//
//            return "" + value + "";
//        }
        }
    }

    var leftAxisFormatter: ValueFormatter = object : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
            Log.i("__leftAxisFormatter",""+value)
            return "" + value + "";
        }
    }
}