package sukriti.ngo.mis.ui.super_admin.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dashboard.*
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.DashboardFeedbackStatsBinding
import sukriti.ngo.mis.repository.data.FeedbackStatsData
import sukriti.ngo.mis.repository.data.FeedbackSummary
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.PieChartData
import sukriti.ngo.mis.ui.dashboard.interfaces.FeedbackStatsRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.Nomenclature.getDurationLabels
import sukriti.ngo.mis.utils.UserAlertClient

class FeedbackStats : Fragment() {
    private lateinit var binding: DashboardFeedbackStatsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var chartData: FeedbackStatsData
    private var isChartInitialized = false

    companion object {
        private var INSTANCE: FeedbackStats? = null

        fun getInstance(): FeedbackStats {
            return INSTANCE
                ?: FeedbackStats()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardFeedbackStatsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel =
            ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)

        val items = getDurationLabels()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_duration_selection, items)
        binding.timeInterval.adapter = adapter
        binding.timeInterval.onItemSelectedListener = durationSelectionListener
        binding.timeInterval.setSelection(Nomenclature.DURATION_DEFAULT_SELECTION)
        binding.timeIntervalContainer.setOnClickListener(View.OnClickListener {
            binding.timeInterval.performClick()
        })

        binding.details.setOnClickListener(View.OnClickListener {
            viewModel.getDashboardNavigationHandler()
                .navigateTo(DashboardViewModel.NAV_FEEDBACK_DETAILS)
        })

        viewModel.pieChartData.observe(viewLifecycleOwner, pieChartObserver)
    }

    var pieChartObserver: Observer<PieChartData> = Observer {
        var pieChartData = it as PieChartData
        var dataDuration = viewModel.getDataDuration()
        var pieChartFeedback = pieChartData.feedback
        var male = 0f
        var female = 0f
        var pwc = 0f
        var mur = 0f
        var total = 0f

        for (data in pieChartFeedback) {
            Log.i("dashboardLambda", "data: " + Gson().toJson(data))
            when (data.name) {
                "MWC" -> male += data.value.toFloat()
                "FWC" -> female += data.value.toFloat()
                "PWC" -> pwc += data.value.toFloat()
                "MUR" -> mur += data.value.toFloat()
            }
        }
        total = (male + female + pwc + mur)/pieChartFeedback.size


        Log.i(
            "dashboardLambda",
            "data: male :$male \nfemale :$female\npwc :$pwc\nmur :$mur\ntotal :$total"
        )

        var feedBackStatsData = FeedbackStatsData(
            dataDuration.from, dataDuration.to,
            FeedbackSummary(total, 0, 0),
            FeedbackSummary(male, 0, 0),
            FeedbackSummary(female, 0, 0),
            FeedbackSummary(pwc, 0, 0),
            FeedbackSummary(mur, 0, 0),
            dataDuration.label
        )

        feedbackSummaryRequestHandler.onSuccess(feedBackStatsData)
    }

    var feedbackSummaryRequestHandler: FeedbackStatsRequestHandler =
        object :
            FeedbackStatsRequestHandler {
            override fun onSuccess(summary: FeedbackStatsData?) {
                userAlertClient.closeWaitDialog()
                if (summary != null) {
                    chartData = summary

                    if (!isChartInitialized)
                        initPieChart()
                    setPieChartData()
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
//            viewModel.countFeedbackSummaryForDays(duration,feedbackSummaryRequestHandler)
            }

        }


    private fun initPieChart() {

        binding.usageChart.setUsePercentValues(false)
        binding.usageChart.getDescription().setEnabled(false)
        binding.usageChart.setDrawHoleEnabled(true)
        binding.usageChart.setDrawEntryLabels(false)
        binding.usageChart.setDrawCenterText(true)
        binding.usageChart.setRotationEnabled(false)
        binding.usageChart.setHighlightPerTapEnabled(true)
        binding.usageChart.getLegend().setEnabled(false)

        binding.usageChart.setHoleRadius(58f)
        binding.usageChart.setTransparentCircleRadius(161f)
        binding.usageChart.setMaxAngle(180f) // HALF CHART
        binding.usageChart.setRotationAngle(180f)
        binding.usageChart.setCenterTextOffset(0f, -20f)
        binding.usageChart.setExtraOffsets(0f, 0f, 0f, -60f)

        // binding.usageChart.setCenterTextTypeface(tfLight)
        //binding.usageChart.setTransparentCircleAlpha(110)
        //binding.usageChart.setEntryLabelTypeface(tfRegular)
        binding.usageChart.setBackgroundColor(Color.WHITE)
        binding.usageChart.setHoleColor(Color.WHITE)
        binding.usageChart.setTransparentCircleColor(Color.WHITE)
        binding.usageChart.setEntryLabelColor(Color.WHITE)
        binding.usageChart.setEntryLabelTextSize(12f)


        isChartInitialized = true
    }

    @SuppressLint("ResourceType")
    private fun setPieChartData() {

        binding.usageChart.setCenterText(generateCenterSpannableText())

        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(chartData.mwc.averageFeedback))
        entries.add(PieEntry(chartData.fwc.averageFeedback))
        entries.add(PieEntry(chartData.pwc.averageFeedback))
        entries.add(PieEntry(chartData.mur.averageFeedback))

        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor(getString(R.color.mwc)))
        colors.add(Color.parseColor(getString(R.color.fwc)))
        colors.add(Color.parseColor(getString(R.color.pwc)))
        colors.add(Color.parseColor(getString(R.color.mur)))

        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.setSliceSpace(3f)
        dataSet.setSelectionShift(5f)
        dataSet.setColors(colors)

        val data = PieData(dataSet)
        data.setValueFormatter(formatter)
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        //data.setValueTypeface(tfLight)
        binding.usageChart.setData(data)

        // undo all highlights
        binding.usageChart.highlightValues(null)
        binding.usageChart.invalidate()

        //Other UI Fields
        binding.maleUsage.text =
            "" + chartData.mwc.averageFeedback //+" ("+chartData.mwc.userCount+" users)"
        binding.femaleUsage.text =
            "" + chartData.fwc.averageFeedback //+" ("+chartData.fwc.userCount+" users)"
        binding.pdUsage.text =
            "" + chartData.pwc.averageFeedback //+" ("+chartData.pwc.userCount+" users)"
        binding.murUsage.text =
            "" + chartData.mur.averageFeedback //+" ("+chartData.mur.userCount+" users)"
    }


    private fun generateCenterSpannableText(): SpannableString? {
        var totalStr = "" + chartData.total.averageFeedback
        if (totalStr.length > 4)
            totalStr = totalStr.substring(0, 4)
        val s = SpannableString("$totalStr\nAverage Feedback")

        s.setSpan(RelativeSizeSpan(1.7f), 0, totalStr.length, 0)
        //s.setSpan(StyleSpan(Typeface.NORMAL), 3, s.length - 4, 0)

        s.setSpan(ForegroundColorSpan(Color.GRAY), totalStr.length + 1, s.length, 0)
        s.setSpan(RelativeSizeSpan(.6f), totalStr.length + 1, s.length, 0)
        s.setSpan(StyleSpan(Typeface.ITALIC), totalStr.length + 1, s.length, 0)

        return s
    }

    var formatter: ValueFormatter = object : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return "" + value.toInt()
        }
    }
}