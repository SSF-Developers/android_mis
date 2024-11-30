package sukriti.ngo.mis.ui.complexes.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.element_report_complex_usage.*
import sukriti.ngo.mis.R
import sukriti.ngo.mis.communication.ConnectionStatusService
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.databinding.CabinDetailsBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.repository.entity.Cabin
import sukriti.ngo.mis.repository.entity.Complex
import sukriti.ngo.mis.repository.utils.DateConverter
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.NAV_COMMANDS
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.NAV_HEALTH
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.NAV_PROFILE
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.NAV_SETTINGS
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.NAV_STATUS
import sukriti.ngo.mis.ui.complexes.data.ConnectionResponse
import sukriti.ngo.mis.ui.complexes.fragments.profile.ProfileHome
import sukriti.ngo.mis.ui.complexes.fragments.settings.SettingsHome
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.utils.*

class CabinDetails : Fragment(), NavigationHandler {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinDetailsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var navigationClient: NavigationClient
    private lateinit var complexDetails: Complex
    private lateinit var cabinDetails: Cabin

    private var selectedTabIndex = 1
    private var _tag: String = "_CabinDetails"

    companion object {
        private var INSTANCE: CabinDetails? = null

        fun getInstance(): CabinDetails {
            return INSTANCE
                ?: CabinDetails()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CabinDetailsBinding.inflate(layoutInflater)
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
//        userAlertClient.showWaitDialog("Loading Cabin Details....")
        navigateTo(NAV_STATUS)
        binding.tabLayout.getTabAt(0)?.select()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!
                when (tab?.position) {
                    0 -> {
                        navigateTo(NAV_STATUS)
                    }

                    1 -> {
                        navigateTo(NAV_HEALTH)
                    }

                    2 -> {
                        navigateTo(NAV_SETTINGS)
                    }
                    3 -> {
                        navigateTo(NAV_PROFILE)
                    }
                    4 -> {
                        navigateTo(NAV_COMMANDS)
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
//        viewModel.getCabinData(resultFromServer)
        ConnectionStatusService.connectionResponse.observe(requireActivity(),connectionObserver)
    }

    var connectionObserver :Observer<ConnectionResponse> = Observer {
        val response =it as ConnectionResponse
       if( viewModel.forCabin == response.clientId){
           if (response.eventType=="connected") {
               binding.leftHead.text = "Last Sync"
               binding.leftResult.text = DateConverter.toDateString(response.timestamp) + ", " +DateConverter.toDbTime(response.timestamp)
               binding.rightHead.text = "IP Address"
               binding.rightResult.text = response.ipAddress
           }else if (response.eventType=="disconnected"){
               binding.leftHead.text = "Last Sync"
               binding.leftResult.text = DateConverter.toDateString(response.timestamp) + ", " +DateConverter.toDbTime(response.timestamp)
               binding.rightHead.text = "Disconnect reason"
               binding.rightResult.text = response.disconnectReason
           }
       }
    }

    private var resultFromServer: RepositoryCallback<String> =
        RepositoryCallback { result ->
            if (result is _Result.Success<String>) {
            } else {
                var err = result as _Result.Error<String>
                userAlertClient.showDialogMessage("Error Alert", err.message, false)
            }
        }

    override fun navigateTo(navigationAction: Int) {
        when (navigationAction) {
            NAV_STATUS -> {
                navigationClient.loadFragment(
                    CabinAqiLumen.getInstance(),
                    R.id.container, "aqiLumen", false
                )
            }
            NAV_HEALTH -> {
                navigationClient.loadFragment(
                    CabinHealth.getInstance(),
                    R.id.container, "cabinHealth", false
                )
            }
            NAV_SETTINGS -> {
                navigationClient.loadFragment(
                    SettingsHome.getInstance(),
                    R.id.container, "SettingsHome", false
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
