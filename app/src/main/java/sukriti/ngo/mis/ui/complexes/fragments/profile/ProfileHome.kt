package sukriti.ngo.mis.ui.complexes.fragments.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.login.*
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.CabinProfileHomeBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.repository.entity.Cabin
import sukriti.ngo.mis.repository.entity.Complex
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.PROFILE_NAV_BWT_USAGE
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.PROFILE_NAV_RESET
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.PROFILE_NAV_UPI_COLLECTION
import sukriti.ngo.mis.ui.complexes.CabinViewModel.Companion.PROFILE_NAV_USAGE
import sukriti.ngo.mis.utils.*

class ProfileHome : Fragment(), NavigationHandler {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinProfileHomeBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var navigationClient: NavigationClient
    private lateinit var complexDetails: Complex
    private lateinit var cabinDetails: Cabin

    private var selectedTabIndex = 1
    private var _tag: String = "_CabinDetails"

    companion object {
        private var INSTANCE: ProfileHome? = null

        fun getInstance(): ProfileHome {
            return INSTANCE
                ?: ProfileHome()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CabinProfileHomeBinding.inflate(layoutInflater)
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
        navToUsageProfile()

        if (sharedPrefsClient.getUiResult().data.collection_stats == "false"
            || Nomenclature.getCabinType(Nomenclature.CABIN_TYPE_BWT, viewModel.forCabin.substring(20, 23))) {
            binding.tabLayout.getTabAt(2)?.view?.visibility = View.GONE
        } else {
            binding.tabLayout.getTabAt(2)?.view?.visibility = View.VISIBLE
        }

        binding.tabLayout.getTabAt(0)?.select()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!
                when (tab?.position) {
                    0 -> {
                        navToUsageProfile()
                    }

                    1 -> {
                        navigateTo(PROFILE_NAV_RESET)
                    }
                    2 -> {
                        navigateTo(PROFILE_NAV_UPI_COLLECTION)
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    fun navToUsageProfile(){
        if (Nomenclature.getCabinType(Nomenclature.CABIN_TYPE_BWT, viewModel.forCabin.substring(20, 23)))
            navigateTo(PROFILE_NAV_BWT_USAGE)
        else
            navigateTo(PROFILE_NAV_USAGE)
    }
    override fun navigateTo(navigationAction: Int) {
        when (navigationAction) {
            PROFILE_NAV_USAGE -> {
                navigationClient.loadFragment(
                    Profile_Usage.getInstance(),
                    R.id.container, "profile_usage", false
                )
            }

            PROFILE_NAV_UPI_COLLECTION -> {
                navigationClient.loadFragment(
                    Profile_Upi_Collection.getInstance(),
                    R.id.container, "profile_upi_collection", false
                )
            }

            PROFILE_NAV_RESET -> {
                navigationClient.loadFragment(
                    Profile_Reset.getInstance(),
                    R.id.container, "profile_reset", false
                )
            }

            PROFILE_NAV_BWT_USAGE -> {
                navigationClient.loadFragment(
                    Profile_Bwt.getInstance(),
                    R.id.container, "Profile_Bwt", false
                )
            }
        }
    }
}
