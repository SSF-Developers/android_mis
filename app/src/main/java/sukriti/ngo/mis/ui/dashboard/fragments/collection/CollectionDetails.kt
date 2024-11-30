package sukriti.ngo.mis.ui.super_admin.fragments

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
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_USAGE_DETAILS_COMPARISON
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_USAGE_DETAILS_SUMMARY
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_USAGE_DETAILS_TIMELINE
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient

class CollectionDetails : DialogFragment(),NavigationHandler {
    private lateinit var binding: DashboardUsageDetailsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient : NavigationClient
    private var selectedTabIndex = 1

    companion object {
        private var INSTANCE: CollectionDetails? = null

        fun getInstance(): CollectionDetails {
            return INSTANCE
                ?: CollectionDetails()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardUsageDetailsBinding.inflate(inflater, container, false)
        binding.header.text = "Collection Stats"
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

        navigateTo(NAV_COLLECTION_DETAILS_SUMMARY)
        binding.tabLayout.getTabAt(0)?.select()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!
                when(tab?.position){
                    0->{
                        navigateTo(NAV_COLLECTION_DETAILS_SUMMARY)
                    }

                    1->{
                        navigateTo(NAV_COLLECTION_DETAILS_COMPARISON)
                    }

                    2->{
                        navigateTo(NAV_COLLECTION_DETAILS_TIMELINE)
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
            NAV_COLLECTION_DETAILS_SUMMARY ->{
                navigationClient.loadFragment(CollectionSummary.getInstance(),
                    R.id.container,"CollectionSummary",false)
            }
            NAV_COLLECTION_DETAILS_COMPARISON ->{
                navigationClient.loadFragment(CollectionComparison.getInstance(),
                    R.id.container,"CollectionComparison",false)
            }
            NAV_COLLECTION_DETAILS_TIMELINE->{
                navigationClient.loadFragment(CollectionTimeline.getInstance(),
                    R.id.container,"CollectionTimeline",false)
            }
        }
    }

}
