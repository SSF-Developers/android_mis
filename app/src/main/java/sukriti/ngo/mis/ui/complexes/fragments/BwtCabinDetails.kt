package sukriti.ngo.mis.ui.complexes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.CabinDetailsBinding
import sukriti.ngo.mis.databinding.CabinDetailsBwtBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.repository.entity.BwtConfig
import sukriti.ngo.mis.repository.entity.Cabin
import sukriti.ngo.mis.repository.entity.Complex
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.NAV_COMMANDS
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.NAV_HEALTH
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.NAV_PROFILE
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.NAV_SETTINGS
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.NAV_STATUS
import sukriti.ngo.mis.ui.complexes.fragments.profile.ProfileHome
import sukriti.ngo.mis.ui.complexes.fragments.profile.Profile_Bwt
import sukriti.ngo.mis.ui.complexes.fragments.settings.Config_BWT
import sukriti.ngo.mis.ui.complexes.fragments.settings.SettingsHome
import sukriti.ngo.mis.utils.*

class BwtCabinDetails : Fragment(), NavigationHandler {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinDetailsBwtBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var navigationClient : NavigationClient
    private lateinit var complexDetails: Complex
    private lateinit var cabinDetails: Cabin

    private var selectedTabIndex = 1
    private var _tag: String = "_CabinDetails"

    companion object {
        private var INSTANCE: BwtCabinDetails? = null

        fun getInstance(): BwtCabinDetails {
            return INSTANCE
                ?: BwtCabinDetails()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CabinDetailsBwtBinding.inflate(layoutInflater)
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

        navigateTo(NAV_HEALTH)
        binding.tabLayout.getTabAt(0)?.select()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!
                when(tab?.position){
                    0->{
                        navigateTo(NAV_HEALTH)
                    }
                    1->{
                        navigateTo(NAV_SETTINGS)
                    }
                    2->{
                        navigateTo(NAV_PROFILE)
                    }
                    3->{
                        navigateTo(NAV_COMMANDS)
                    }
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun navigateTo(navigationAction: Int) {
        when (navigationAction) {
            
            NAV_HEALTH-> {
                navigationClient.loadFragment(
                    CabinBwtHealth.getInstance(),
                    R.id.container, "cabinHealth", false
                )
            }
            NAV_SETTINGS-> {
                navigationClient.loadFragment(
                    Config_BWT.getInstance(),
                    R.id.container, "Config_BWT", false
                )
            }

            NAV_PROFILE -> {
                navigationClient.loadFragment(
                    ProfileHome.getInstance(),

                    R.id.container, "ProfileHome", false
                 )
            }
            NAV_COMMANDS -> {
                navigationClient.loadFragment(
                    CabinCommands.getInstance(),
                    R.id.container, "CabinCommands", false
                )
            }

        }
    }
}
