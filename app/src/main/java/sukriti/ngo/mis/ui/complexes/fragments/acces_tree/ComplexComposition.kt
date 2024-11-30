package sukriti.ngo.mis.ui.complexes.fragments.acces_tree

import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.login.*
import sukriti.ngo.mis.R
import sukriti.ngo.mis.communication.ConnectionStatusService
import sukriti.ngo.mis.databinding.ComplexDetailsBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.complexes.ComplexesViewModel
import sukriti.ngo.mis.ui.complexes.ComplexesViewModel.Companion.NAV_TAB_BWT
import sukriti.ngo.mis.ui.complexes.ComplexesViewModel.Companion.NAV_TAB_FWC
import sukriti.ngo.mis.ui.complexes.ComplexesViewModel.Companion.NAV_TAB_MUR
import sukriti.ngo.mis.ui.complexes.ComplexesViewModel.Companion.NAV_TAB_MWC
import sukriti.ngo.mis.ui.complexes.ComplexesViewModel.Companion.NAV_TAB_PWC
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.ui.complexes.data.ConnectionResponse
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature.*
import sukriti.ngo.mis.utils.UserAlertClient

class ComplexComposition : BottomSheetDialogFragment(), NavigationHandler {

    private lateinit var viewModel: ComplexesViewModel
    private lateinit var binding: ComplexDetailsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var details: ComplexDetailsData
    private lateinit var navigationClient: NavigationClient
    private var selectedTabIndex = 1

    fun setDetails(complexDetails: ComplexDetailsData) {
        details = complexDetails
    }

    fun setNavigationHandler(handler: NavigationHandler) {
        navigationHandler = handler
    }

    companion object {
        private var INSTANCE: ComplexComposition? = null

        fun getInstance(): ComplexComposition {
            return INSTANCE ?: ComplexComposition()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ComplexDetailsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        context?.stopService(Intent(context,ConnectionStatusService::class.java))
    }

    private fun init() {
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProvider(requireActivity()).get(ComplexesViewModel::class.java)
        navigationClient = NavigationClient(childFragmentManager)

        //testing for iot connection
        context?.startService(Intent(context, ConnectionStatusService::class.java))


        binding.toolbar.title.text = details.ComplexName
        binding.toolbar.subTitle.text =
            details.StateCode + " - " + details.DistrictName + " - " + details.CityName
        binding.name.label.text = "Complex Name"
        binding.name.value.text = details.ComplexName
        binding.address.label.text = "Address"
        binding.address.value.text = details.Address
        binding.date.label.text = "Activation Date"
        binding.date.value.text = details.Date
        binding.client.label.text = "Client Name"
        binding.client.value.text = details.Client
        binding.cabinCount.label.text = "Cabin Count"
        binding.cabinCount.value.text = "" + details.totalCabins

        binding.toolbar.closeCont.setOnClickListener(View.OnClickListener {
            dismiss()
        })

        navigateTo(NAV_TAB_MWC)
        binding.tabLayout.getTabAt(0)?.select()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!
                when (tab?.position) {
                    0 -> {
                        navigateTo(NAV_TAB_MWC)
                    }

                    1 -> {
                        navigateTo(NAV_TAB_FWC)
                    }

                    2 -> {
                        navigateTo(NAV_TAB_PWC)
                    }
                    3 -> {
                        navigateTo(NAV_TAB_MUR)
                    }
                    4 -> {
                        navigateTo(NAV_TAB_BWT)
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })

    }

    override fun navigateTo(navigationAction: Int) {
        val cabinGrid: CabinsGrid

        when (navigationAction) {
            NAV_TAB_MWC -> {
                Log.i("_ComplexesVM", "navigateTo: " + Gson().toJson(details))
                cabinGrid = CabinsGrid.getInstance()
                cabinGrid.setDetails(details, CABIN_TYPE_MWC)
                navigationClient.loadFragment(
                    cabinGrid,
                    R.id.container, "cabinGridMwc", false
                )
            }
            NAV_TAB_FWC -> {
                cabinGrid =
                    CabinsGrid.getInstance()
                cabinGrid.setDetails(details, CABIN_TYPE_FWC)
                navigationClient.loadFragment(
                    cabinGrid,
                    R.id.container, "cabinGridFwc", false
                )
            }
            NAV_TAB_PWC -> {
                cabinGrid =
                    CabinsGrid.getInstance()
                cabinGrid.setDetails(details, CABIN_TYPE_PWC)
                navigationClient.loadFragment(
                    cabinGrid,
                    R.id.container, "cabinGridPwc", false
                )
            }
            NAV_TAB_MUR -> {
                cabinGrid =
                    CabinsGrid.getInstance()
                cabinGrid.setDetails(details, CABIN_TYPE_MUR)
                navigationClient.loadFragment(
                    cabinGrid,
                    R.id.container, "cabinGridMur", false
                )
            }
            NAV_TAB_BWT -> {
                cabinGrid =
                    CabinsGrid.getInstance()
                cabinGrid.setDetails(details, CABIN_TYPE_BWT)
                navigationClient.loadFragment(
                    cabinGrid,
                    R.id.container, "cabinGridBwt", false
                )
            }
        }
    }

}