package sukriti.ngo.mis.ui.super_admin.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import sukriti.ngo.mis.R
import sukriti.ngo.mis.adapters.QuickAccessAdapter
import sukriti.ngo.mis.adapters.QuickConfigAdapter
import sukriti.ngo.mis.dataModel.QuickConfigItem
import sukriti.ngo.mis.databinding.DashboardQuickAccessBinding
import sukriti.ngo.mis.databinding.DashboardQuickConfigBinding
import sukriti.ngo.mis.interfaces.ClientNameListHandler
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.UiResult
import sukriti.ngo.mis.ui.dashboard.interfaces.QuickAccessRequestHandler
import sukriti.ngo.mis.ui.login.data.UserProfile
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import java.util.ArrayList

class QuickConfig : Fragment() {
    private lateinit var binding: DashboardQuickConfigBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var adapter: QuickConfigAdapter
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var quickConfigSelection: QuickConfigItem

    companion object {
        private var INSTANCE: QuickConfig? = null

        fun getInstance(): QuickConfig {
            return INSTANCE
                ?: QuickConfig()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardQuickConfigBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        sharedPrefsClient = SharedPrefsClient(context)
        viewModel =
            ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)


        val gridLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.grid.layoutManager = gridLayoutManager
        adapter = QuickConfigAdapter(context, selectionHandler)
        binding.grid.adapter = adapter
        viewModel.uiResult.observe(viewLifecycleOwner, uiResultObserver)
    }

    private var uiResultObserver: Observer<UiResult> = Observer {
        val uiResult = it as UiResult
        val usagecharge = uiResult.data.usage_charge
        val adapterList = adapter.quickConfigList
        when (usagecharge) {
            "true" -> {
                if (!adapterList.contains(usageConfig())) adapter.add(usageConfig(), 0)
            }
            "false" -> {
                if (adapterList.contains(usageConfig())) adapter.add(usageConfig(), 0)
            }
        }
    }

    private fun usageConfig(): QuickConfigItem {
        return QuickConfigItem(
            "Usage Charge Config",
            "Configure usage charge and payment mode settings for all units in one go.",
            QuickConfigItem.Type.USAGE_CHARGE_CONFIG
        )
    }

    private var selectionHandler: QuickConfigAdapter.QuickConfigSelectionHandler =
        QuickConfigAdapter.QuickConfigSelectionHandler { data ->
            if (data != null) {
                quickConfigSelection = data
            }

            if (!UserProfile.isClientSpecificRole(sharedPrefsClient.getUserDetails().role)) {
                userAlertClient.showWaitDialog("Loading client list...")
                viewModel.getClientList(clientListRequestHandler)

//                handleNavigation()
            } else {
                handleNavigation()
            }
        }

    private var clientListRequestHandler: ClientNameListHandler = object : ClientNameListHandler {
        override fun onResult(list: ArrayList<String>?) {
            userAlertClient.closeWaitDialog()
            handleNavigation()
        }

        override fun onError(err: String?) {
            userAlertClient.closeWaitDialog()
        }
    }

    private fun handleNavigation() {
//        userAlertClient.closeWaitDialog()
        when (quickConfigSelection?.type) {
            QuickConfigItem.Type.USAGE_CHARGE_CONFIG -> {
                viewModel.getDashboardNavigationHandler()
                    .navigateTo(DashboardViewModel.NAV_QUICK_CONFIG_USAGE_CHARGE)
            }
            QuickConfigItem.Type.FLUSH_CONFIG -> {
                viewModel.getDashboardNavigationHandler()
                    .navigateTo(DashboardViewModel.NAV_QUICK_CONFIG_FLUSH)
            }
            QuickConfigItem.Type.LIGHT_FAN_CONFIG -> {
                viewModel.getDashboardNavigationHandler()
                    .navigateTo(DashboardViewModel.NAV_QUICK_CONFIG_LIGHT_FAN)
            }
            QuickConfigItem.Type.FLOOR_CLEAN_CONFIG -> {
                viewModel.getDashboardNavigationHandler()
                    .navigateTo(DashboardViewModel.NAV_QUICK_CONFIG_FLOOR_CLEAN)
            }
            QuickConfigItem.Type.DATA_REQUEST_CONFIG -> {
                viewModel.getDashboardNavigationHandler()
                    .navigateTo(DashboardViewModel.NAV_QUICK_CONFIG_DATA_REQUEST)
            }
        }
    }
}