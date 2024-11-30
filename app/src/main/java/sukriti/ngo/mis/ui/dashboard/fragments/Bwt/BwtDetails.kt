package sukriti.ngo.mis.ui.dashboard.fragments.Bwt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.DashboardUsageDetailsBinding
import sukriti.ngo.mis.databinding.FragmentBwtDetailsBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_BWT_DETAILS_SUMMARY
import sukriti.ngo.mis.ui.super_admin.fragments.UsageComparison
import sukriti.ngo.mis.ui.super_admin.fragments.UsageDetails
import sukriti.ngo.mis.ui.super_admin.fragments.UsageSummary
import sukriti.ngo.mis.ui.super_admin.fragments.UsageTimeline
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient

class BwtDetails : DialogFragment(), NavigationHandler {
    private lateinit var binding: FragmentBwtDetailsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient : NavigationClient
    private var selectedTabIndex = 1
    companion object {
        private var INSTANCE: BwtDetails? = null

        fun getInstance(): BwtDetails {
            return INSTANCE
                ?: BwtDetails()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBwtDetailsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }

    private fun init() {
        viewModel =
            ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)

        navigateTo(NAV_BWT_DETAILS_SUMMARY)
        binding.tabLayout.getTabAt(0)?.select()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!
                when(tab?.position){
                    0->{
                        navigateTo(NAV_BWT_DETAILS_SUMMARY)
                    }

                    1->{
                        navigateTo(DashboardViewModel.NAV_BWT_DETAILS_COMPARISON)
                    }

                    2->{
                        navigateTo(DashboardViewModel.NAV_BWT_DETAILS_TIMELINE)
                    }
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })

        binding.back.setOnClickListener(View.OnClickListener {
            dismiss()
        })
    }

    override fun navigateTo(navigationAction: Int) {
        when(navigationAction){
            NAV_BWT_DETAILS_SUMMARY ->{
                navigationClient.loadFragment(
                    BwtSummary.getInstance(),
                    R.id.container,"bwtSummary",false)
            }
            DashboardViewModel.NAV_BWT_DETAILS_COMPARISON ->{
                navigationClient.loadFragment(
                    BwtComparison.getInstance(),
                    R.id.container,"bwtComparison",false)
            }
            DashboardViewModel.NAV_BWT_DETAILS_TIMELINE ->{
                navigationClient.loadFragment(
                    BwtTimeline.getInstance(),
                    R.id.container,"bwtTimeline",false)
            }
        }
    }

}
