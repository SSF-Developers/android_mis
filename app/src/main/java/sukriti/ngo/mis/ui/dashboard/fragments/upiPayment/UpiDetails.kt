package sukriti.ngo.mis.ui.dashboard.fragments.upiPayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.DashboardUsageDetailsBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COLLECTION_DETAILS_COMPARISON
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COLLECTION_DETAILS_SUMMARY
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COLLECTION_DETAILS_TIMELINE
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_UPI_COLLECTION_DETAILS_COMPARISON
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_UPI_COLLECTION_DETAILS_SUMMARY
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_UPI_COLLECTION_DETAILS_TIMELINE
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient

class UpiDetails : DialogFragment(),NavigationHandler {
    private lateinit var binding: DashboardUsageDetailsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient : NavigationClient
    private var selectedTabIndex = 1
    companion object {
        private var INSTANCE: UpiDetails? = null

        fun getInstance(): UpiDetails {
            return INSTANCE
                ?: UpiDetails()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardUsageDetailsBinding.inflate(inflater, container, false)
        binding.header.text = "UPI Collection Stats"
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
        viewModel = ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)

        navigateTo(NAV_UPI_COLLECTION_DETAILS_SUMMARY)
        binding.tabLayout.getTabAt(0)?.select()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!
                when(tab?.position){
                    0->{
                        navigateTo(NAV_UPI_COLLECTION_DETAILS_SUMMARY)
                    }

                    1->{
                        navigateTo(NAV_UPI_COLLECTION_DETAILS_COMPARISON)
                    }

                    2->{
                        navigateTo(NAV_UPI_COLLECTION_DETAILS_TIMELINE)
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
            NAV_UPI_COLLECTION_DETAILS_SUMMARY ->{
                navigationClient.loadFragment(UpiSummary.getInstance(),
                    R.id.container,"UPICollectionSummary",false)
            }
            NAV_UPI_COLLECTION_DETAILS_COMPARISON ->{
                navigationClient.loadFragment(UpiComparison.getInstance(),
                    R.id.container,"UPICollectionComparison",false)
            }
            NAV_UPI_COLLECTION_DETAILS_TIMELINE->{
                navigationClient.loadFragment(UpiTimeline.getInstance(),
                    R.id.container,"UPICollectionTimeline",false)
            }
        }
    }

}
