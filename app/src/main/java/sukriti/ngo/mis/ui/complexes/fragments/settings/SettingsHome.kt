package sukriti.ngo.mis.ui.complexes.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.SettingsHomeBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.repository.entity.Cabin
import sukriti.ngo.mis.repository.entity.Complex
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.SETTINGS_NAV_BWT
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.SETTINGS_NAV_CMS
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.SETTINGS_NAV_ODS
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.SETTINGS_NAV_SHARED_DATA
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.SETTINGS_NAV_UCEMS
import sukriti.ngo.mis.utils.*

class SettingsHome : Fragment(), NavigationHandler {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: SettingsHomeBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var navigationClient : NavigationClient
    private lateinit var complexDetails: Complex
    private lateinit var cabinDetails: Cabin

    private var selectedTabIndex = 1
    private var _tag: String = "_CabinDetails"

    companion object {
        private var INSTANCE: SettingsHome? = null

        fun getInstance(): SettingsHome {
            return INSTANCE
                ?: SettingsHome()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingsHomeBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    private fun init() {
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)
        viewModel = ViewModelProviders.of(requireActivity()).get(CabinViewModel::class.java)

        navigateTo(SETTINGS_NAV_UCEMS)
        binding.tabLayout.getTabAt(0)?.select()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!
                when(tab?.position){
                    0->{
                        navigateTo(SETTINGS_NAV_UCEMS)
                    }

                    1->{
                        navigateTo(SETTINGS_NAV_CMS)
                    }

                    2->{
                        navigateTo(SETTINGS_NAV_ODS)
                    }
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun navigateTo(navigationAction: Int) {
        when (navigationAction) {
            SETTINGS_NAV_UCEMS-> {
                navigationClient.loadFragment(
                    Config_UCEMS.getInstance(),
                    R.id.container, "ucems", false
                )
            }
            SETTINGS_NAV_CMS-> {
                navigationClient.loadFragment(
                    Config_CMS.getInstance(),
                    R.id.container, "cms", false
                )
            }
            SETTINGS_NAV_ODS-> {
                navigationClient.loadFragment(
                    Config_ODS.getInstance(),
                    R.id.container, "ods", false
                )
            }
        }
    }
}
