package sukriti.ngo.mis.ui.dashboard.fragments.banner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import sukriti.ngo.mis.databinding.DashboardScrollingCardBinding
import sukriti.ngo.mis.databinding.DashboardWaterLevelStatsBinding
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.UiResult
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient
import java.util.*

class ScrollingCard : Fragment() {
    private lateinit var binding: DashboardScrollingCardBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private var index = -1
    private var flipTimer = Timer()
    private var fragmentList = arrayListOf<Fragment>()

    companion object {
        private var INSTANCE: ScrollingCard? = null

        fun getInstance(): ScrollingCard {
            return INSTANCE
                ?: ScrollingCard()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardScrollingCardBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        startFlipTimer()
    }

    override fun onPause() {
        super.onPause()
        stopFlipTimer()
    }

    private fun init() {
        viewModel = ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)
        fragmentList.add(UsageCountStats.getInstance())
        fragmentList.add(FeedbackCountStats.getInstance())
        fragmentList.add(BwtStats.getInstance())

        viewModel.uiResult.observe(viewLifecycleOwner, uiResultObserver)
    }

    var uiResultObserver: Observer<UiResult> = Observer {
        var uiResult = it as UiResult

        var viewUsageCountStats = uiResult.data.total_usage
        if (viewUsageCountStats == "true") {
            if (fragmentList.contains(UsageCountStats.getInstance())) {
            } else {
                fragmentList.add(UsageCountStats.getInstance())
            }
        } else if (viewUsageCountStats == "false") {

            if (fragmentList.contains(UsageCountStats.getInstance())) {
                fragmentList.remove(UsageCountStats.getInstance())
            }
        }

        var viewWaterStats = uiResult.data.water_saved
        if (viewWaterStats == "true") {
            if (fragmentList.contains(BwtStats.getInstance())) {
            } else {
                fragmentList.add(BwtStats.getInstance())
            }
        } else if (viewWaterStats == "false") {

            if (fragmentList.contains(BwtStats.getInstance())) {
                fragmentList.remove(BwtStats.getInstance())
            }
        }
        var viewFeedBackStats = uiResult.data.average_feedback
        if (viewFeedBackStats == "true") {
            if (fragmentList.contains(FeedbackCountStats.getInstance())) {
            } else {
                fragmentList.add(FeedbackCountStats.getInstance())
            }
        } else if (viewFeedBackStats == "false") {
            if (fragmentList.contains(FeedbackCountStats.getInstance())) {
                fragmentList.remove(FeedbackCountStats.getInstance())
            }
        }
    }


    private fun startFlipTimer() {
        if (flipTimer != null) {
            flipTimer.cancel()
        }

        flipTimer = Timer()
        index = -1

        flipTimer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                index++
                if (index == fragmentList.size)
                    index = 0

                navigationClient.flipLoadFragment(fragmentList[index], "" + index)
            }
        }, 100, 5000)
    }

    private fun stopFlipTimer() {
        flipTimer.cancel()
    }
}