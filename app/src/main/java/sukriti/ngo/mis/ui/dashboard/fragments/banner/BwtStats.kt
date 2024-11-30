package sukriti.ngo.mis.ui.dashboard.fragments.banner

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.DashboardBwtBinding
import sukriti.ngo.mis.repository.data.DailyWaterRecycledStats
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.interfaces.RecycledWaterRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.UserAlertClient
import java.util.*

class BwtStats : Fragment() {
    private lateinit var binding: DashboardBwtBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var chartData: DailyWaterRecycledStats
    private var isChartInitialized = false

    companion object {
        private var INSTANCE: BwtStats? = null

        fun getInstance(): BwtStats {
            return INSTANCE
                ?: BwtStats()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardBwtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun init() {
        viewModel = ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)

        userAlertClient.showWaitDialog("Loading data...")
        viewModel.listRecycledWaterStatsForDays(100,dataRequestHandler)
    }

    var dataRequestHandler: RecycledWaterRequestHandler =
        object : RecycledWaterRequestHandler {
            override fun onSuccess(summary: DailyWaterRecycledStats?) {
                userAlertClient.closeWaitDialog()
                if (summary != null) {
                    binding.label.text = ""+summary.getTotal(summary)+" Lts of water saved in " + summary.durationLabel
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

    private fun initLineChart() {
        binding.chart.getDescription().setEnabled(false)
        binding.chart.setPinchZoom(false)
        binding.chart.setDrawGridBackground(false)
        binding.chart.getLegend().setEnabled(false)
        binding.chart.setTouchEnabled(false)
        binding.chart.setDragEnabled(false)
        binding.chart.setScaleEnabled(false)
        // binding.chart.setBackgroundColor(color)
        // chart.setDrawHorizontalGrid(false);
        // chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);
        // set custom chart offsets (automatic offset calculation is hereby disabled)
        binding.chart.setViewPortOffsets(10f, 0f, 10f, 0f)

        binding.chart.axisLeft.isEnabled = false
        binding.chart.axisLeft.spaceTop = 40f
        binding.chart.axisLeft.spaceBottom = 40f
        binding.chart.axisRight.isEnabled = false
        binding.chart.xAxis.isEnabled = false

        //isChartInitialized = true
    }

    @SuppressLint("ResourceType")
    private fun setData() {
        Log.i("_setData", "1st init()")

        val dailyRecycledWaterValues = arrayListOf<Entry>()
//        for((index, item) in chartData.data.withIndex()){
//            dailyRecycledWaterValues.add(Entry(index.toFloat(), item.quantity.toFloat()))
//        }

        for (index in 0..50) {
            val y = Math.random() * 10
            //dailyRecycledWaterValues.add(Entry(index.toFloat(), item.quantity.toFloat()))
            dailyRecycledWaterValues.add(Entry(index.toFloat(), y.toFloat()))
        }

        val set1 = createLineDataSet(dailyRecycledWaterValues,"Total Usage", Color.parseColor(getString(R.color.total)))
        val data = LineData(set1)

        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)
        binding.chart.data = data

        binding.chart.invalidate()
        binding.chart.animateX(800)
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