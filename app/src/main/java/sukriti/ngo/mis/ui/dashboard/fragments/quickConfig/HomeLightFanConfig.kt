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
import sukriti.ngo.mis.databinding.ConfigLightFanBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_FEEDBACK_DETAILS_SUMMARY
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_QUICK_CONFIG_LIGHT_AND_FAN_FAN
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_QUICK_CONFIG_LIGHT_AND_FAN_LIGHT
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient

class HomeLightFanConfig : DialogFragment(),NavigationHandler {
    private lateinit var binding: ConfigLightFanBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient : NavigationClient
    private var selectedTabIndex = 1
    companion object {
        private var INSTANCE: HomeLightFanConfig? = null

        fun getInstance(): HomeLightFanConfig {
            return INSTANCE
                ?: HomeLightFanConfig()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ConfigLightFanBinding.inflate(inflater, container, false)
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

        navigateTo(NAV_QUICK_CONFIG_LIGHT_AND_FAN_LIGHT)
        binding.tabLayout.getTabAt(0)?.select()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!
                when(tab?.position){
                    0->{
                        navigateTo(NAV_QUICK_CONFIG_LIGHT_AND_FAN_LIGHT)
                    }

                    1->{
                        navigateTo(NAV_QUICK_CONFIG_LIGHT_AND_FAN_FAN)
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
            NAV_QUICK_CONFIG_LIGHT_AND_FAN_LIGHT ->{
                navigationClient.loadFragment(LightConfig.getInstance(),
                    R.id.container,"configLight",false)
            }
            NAV_QUICK_CONFIG_LIGHT_AND_FAN_FAN ->{
                navigationClient.loadFragment(FanConfig.getInstance(),
                    R.id.container,"configFan",false)
            }
        }
    }

}
