package sukriti.ngo.mis.ui.reports.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.databinding.ReportsHomeBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.repository.entity.Cabin
import sukriti.ngo.mis.repository.entity.Complex
import sukriti.ngo.mis.ui.administration.interfaces.ProvisioningTreeRequestHandler
import sukriti.ngo.mis.ui.reports.ReportsViewModel
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.NAV_COMMAND_LOG
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.NAV_CONFIG_LOG
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.NAV_DATA_REPORT
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.NAV_GRAPHICAL_REPORT
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.NAV_DOWNLOAD_REPORT
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.NAV_RAW_DATA
import sukriti.ngo.mis.ui.reports.ReportsViewModel.Companion.NAV_SELECTION_TREE
import sukriti.ngo.mis.utils.*

class ReportsHome : Fragment(), NavigationHandler {

    private lateinit var viewModel: ReportsViewModel
    private lateinit var binding: ReportsHomeBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var navigationClient: NavigationClient

    private var selectedTabIndex = 1

    companion object {
        private var INSTANCE: ReportsHome? = null

        fun getInstance(): ReportsHome {
            return INSTANCE
                ?: ReportsHome()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ReportsHomeBinding.inflate(layoutInflater)
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
        viewModel = ViewModelProviders.of(this).get(ReportsViewModel::class.java)
        //sharedPrefsClient.saveAccessTreeStatus(false)
        sharedPrefsClient.saveSelectionTreeStatus(false)
        Log.i("_selectionStatus","ReportsHome set false default")

        navigateTo(NAV_GRAPHICAL_REPORT)
        binding.tabLayout.getTabAt(0)?.select()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!
                when (tab?.position) {
                    0 -> {
                        navigateTo(NAV_GRAPHICAL_REPORT)
                    }

                    1 -> {
                        navigateTo(NAV_DOWNLOAD_REPORT)
                    }
//                    1 -> {
//                        navigateTo(NAV_DATA_REPORT)
//                    }
//                    2 -> {
//                        navigateTo(NAV_RAW_DATA)
//                    }
//
//                    3 -> {
//                        navigateTo(NAV_CONFIG_LOG)
//                    }
//                    4 -> {
//                        navigateTo(NAV_COMMAND_LOG)
//                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })


        userAlertClient.showWaitDialog("Loading selection tree...")
        viewModel.loadAccessTreeForUser(
            sharedPrefsClient.getUserDetails().user.userName,
            provisioningTreeRequestHandler
        )

        binding.loadAccessTree.setOnClickListener{
            navigateTo(NAV_SELECTION_TREE)
        }
    }

    private var provisioningTreeRequestHandler: ProvisioningTreeRequestHandler =
        object : ProvisioningTreeRequestHandler {
            override fun onSuccess(mCountry: Country?) {
                if (mCountry != null) {
                    userAlertClient.closeWaitDialog()
                    SelectionTree.getInstance().show(childFragmentManager, "selectionTree")
                } else {
                    userAlertClient.closeWaitDialog()
                }
            }

            override fun onError(message: String?) {
                userAlertClient.closeWaitDialog()
                userAlertClient.showDialogMessage("Error Alert", message, true);
            }
        }

    override fun navigateTo(navigationAction: Int) {
        when (navigationAction) {

            NAV_SELECTION_TREE -> {
                SelectionTree.getInstance().show(childFragmentManager, "selectionTree")
            }

            NAV_GRAPHICAL_REPORT -> {
                val fragment = GraphicalReport.getInstance()
                fragment.setNavigationHandler(this)
                navigationClient.loadFragment(
                    fragment,
                    R.id.container, "usageReport", false
                )
            }

            NAV_DOWNLOAD_REPORT -> {
                val fragment = DownloadReport.getInstance();
                fragment.setNavigationHandler(this)
                navigationClient.loadFragment(fragment, R.id.container, "downloadReport", false)
            }

//            NAV_DATA_REPORT -> {
//                var fragment = DateDataReport.getInstance()
//                fragment.setNavigationHandler(this)
//                navigationClient.loadFragment(
//                    fragment,
//                    R.id.container, "usageReport", false
//                )
//            }
//
//            NAV_RAW_DATA -> {
//                var fragment = RawData.getInstance()
//                fragment.setNavigationHandler(this)
//                navigationClient.loadFragment(
//                    fragment,
//                    R.id.container, "reportReset", false
//                )
//            }
//            NAV_CONFIG_LOG-> {
//                navigationClient.loadFragment(
//                    ProfileHome.getInstance(),
//                    R.id.container, "ProfileHome", false
//                )
//            }
//            NAV_COMMAND_LOG-> {
//                navigationClient.loadFragment(
//                    CabinCommands.getInstance(),
//                    R.id.container, "CabinCommands", false
//                )
//            }
        }
    }
}
