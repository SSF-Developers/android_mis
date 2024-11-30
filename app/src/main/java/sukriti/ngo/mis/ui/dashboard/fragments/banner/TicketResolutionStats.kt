package sukriti.ngo.mis.ui.dashboard.fragments.banner

import android.annotation.SuppressLint
import android.app.usage.UsageStats
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.dashboard_usage_stats.*
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.databinding.*
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.repository.data.DailyTicketEventSummary
import sukriti.ngo.mis.repository.data.DailyWaterRecycledStats
import sukriti.ngo.mis.repository.data.UsageComparisonStats
import sukriti.ngo.mis.repository.data.UsageSummaryStats
import sukriti.ngo.mis.repository.entity.Ticket
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.interfaces.RecycledWaterRequestHandler
import sukriti.ngo.mis.ui.dashboard.interfaces.UsageComparisonRequestHandler
import sukriti.ngo.mis.ui.dashboard.interfaces.UsageSummaryRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.UserAlertClient
import java.lang.IllegalStateException
import java.util.ArrayList

class TicketResolutionStats : Fragment() {
    private lateinit var binding: DashboardTicketResolutionStatsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var chartData: List<DailyTicketEventSummary>
    private var isChartInitialized = false
    private var daysCount = 100

    companion object {
        private var INSTANCE: TicketResolutionStats? = null

        fun getInstance(): TicketResolutionStats {
            return INSTANCE
                ?: TicketResolutionStats()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardTicketResolutionStatsBinding.inflate(inflater, container, false)
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
        viewModel.getDailyTicketEventSummary(daysCount, dataRequestHandler)

        //TODO :RK :Opmize using DB querries
        viewModel.listAllTickets(listTicketsRequestHandler)
    }

    var dataRequestHandler: RepositoryCallback<List<DailyTicketEventSummary>> =
        object : RepositoryCallback<List<DailyTicketEventSummary>>  {
            override fun onComplete(result: _Result<List<DailyTicketEventSummary>>?) {
                if(result is _Result.Success){
                    chartData = result.data as List<DailyTicketEventSummary>

                    if (!isChartInitialized)
                        initLineChart()
                    setData()
                }else{
                    var err = result as _Result.Error

                }
            }
        }

    var countAllTicket= 0
    private var listTicketsRequestHandler: RepositoryCallback<List<Ticket>> =
        object : RepositoryCallback<List<Ticket>> {
            override fun onComplete(result: _Result<List<Ticket>>?) {

                if(result is _Result.Success<List<Ticket>>){
                    var data = result.data  as List<Ticket>
                    countAllTicket=data.size
                    viewModel.listAllResolvedTicket(listResolvedTicketsRequestHandler)

                } else {
                    var err = result as _Result.Error<Ticket>
                }
            }
        }

    private var listResolvedTicketsRequestHandler: RepositoryCallback<List<Ticket>> =
        object : RepositoryCallback<List<Ticket>> {
            override fun onComplete(result: _Result<List<Ticket>>?) {

                if(result is _Result.Success<List<Ticket>>){
                    var data = result.data  as List<Ticket>
                    ("" +countAllTicket + " ticket are raised and "+data.size +" ticket are resolved ").also { binding.label.text = it }

                } else {
                    var err = result as _Result.Error<Ticket>
                }
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
        for ((index, item) in chartData.withIndex()) {
            dailyRecycledWaterValues.add(Entry(index.toFloat(), item.activeTicketCount.toFloat()))
        }

        //ContextCrash introduced requireContext()
        //Try-Catch for IllegalState: Fragment not attached to a context.
        try {
            val set1 = createLineDataSet(
                dailyRecycledWaterValues,
                "Total Usage",
                ContextCompat.getColor(requireContext(), R.color.total)
            )
            val data = LineData(set1)

            data.setValueTextColor(Color.WHITE)
            data.setValueTextSize(9f)
            binding.chart.data = data

            binding.chart.invalidate()
            binding.chart.animateX(800)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Log.e("ContextCrash","handled for UsageCountStats 01")
        }
    }

    @SuppressLint("ResourceType")
    fun createLineDataSet(values: ArrayList<Entry>, label: String, lineColor: Int): LineDataSet {
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