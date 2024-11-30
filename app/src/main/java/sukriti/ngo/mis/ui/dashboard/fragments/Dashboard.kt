package sukriti.ngo.mis.ui.super_admin.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dashboard.*
import kotlinx.android.synthetic.main.item_faulty_cabin.*
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.dataModel.dynamo_db.Complex
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.databinding.DashboardBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import sukriti.ngo.mis.ui.administration.interfaces.ProvisioningTreeRequestHandler
import sukriti.ngo.mis.ui.administration.interfaces.RequestHandler
import sukriti.ngo.mis.ui.administration.interfaces.TreeInteractionListener
import sukriti.ngo.mis.ui.complexes.adapters.accessTreeComplexSelection.StateAdapterCS
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.ui.complexes.fragments.acces_tree.ComplexComposition
import sukriti.ngo.mis.ui.complexes.interfaces.ComplexDetailsRequestHandler
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_BWT_DETAILS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_BWT_STATS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COLLECTION_DETAILS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COLLECTION_STATS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COMPLEX_ACTIVE_TICKETS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COMPLEX_CONNECTION_STATS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COMPLEX_HEALTH_STATS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_DATA_SCREEN
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_FEEDBACK_DETAILS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_FEEDBACK_STATS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_FLIPPING_CARD
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_LOADING_SCREEN
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_QUICK_ACCESS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_QUICK_ACCESS_ACTIONS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_QUICK_CONFIG
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_QUICK_CONFIG_DATA_REQUEST
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_QUICK_CONFIG_FLOOR_CLEAN
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_QUICK_CONFIG_FLUSH
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_QUICK_CONFIG_LIGHT_FAN
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_QUICK_CONFIG_USAGE_CHARGE
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_UPI_COLLECTION_DETAILS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_UPI_COLLECTION_STATS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_USAGE_DETAILS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_USAGE_STATS
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_WATER_LEVEL_STATS
import sukriti.ngo.mis.ui.dashboard.HomeActivity
import sukriti.ngo.mis.ui.dashboard.data.UiResult
import sukriti.ngo.mis.ui.dashboard.fragments.Bwt.BwtDetails
import sukriti.ngo.mis.ui.dashboard.fragments.Bwt.BwtStat
import sukriti.ngo.mis.ui.dashboard.fragments.banner.ScrollingCard
import sukriti.ngo.mis.ui.dashboard.fragments.quickAccess.QuickAccessCommands
import sukriti.ngo.mis.ui.dashboard.fragments.quickConfig.HomeDataRequestConfig
import sukriti.ngo.mis.ui.dashboard.fragments.quickConfig.HomeUsageChargeConfig
import sukriti.ngo.mis.ui.dashboard.fragments.upiPayment.UpiDetails
import sukriti.ngo.mis.ui.dashboard.fragments.upiPayment.UpiStats
import sukriti.ngo.mis.ui.dashboard.interfaces.AccessibleComplexRequestHandler
import sukriti.ngo.mis.ui.login.data.UserProfile
import sukriti.ngo.mis.ui.profile.ProfileViewModel
import sukriti.ngo.mis.utils.*
import sukriti.ngo.mis.utils.Utilities.getNames
import java.util.*

class Dashboard : Fragment(), NavigationHandler {
    private lateinit var binding: DashboardBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var administrationViewModel: AdministrationViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var sharedPrefsClient: SharedPrefsClient

    companion object {
        private var INSTANCE: Dashboard? = null

        fun getInstance(): Dashboard {
            return INSTANCE
                ?: Dashboard()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val items = Nomenclature.getDurationLabels()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_duration_selection, items)
        binding = DashboardBinding.inflate(inflater, container, false)
        binding.timeInterval.adapter = adapter
        binding.timeInterval.onItemSelectedListener = durationSelectionListener
        binding.timeInterval.setSelection(Nomenclature.DURATION_DEFAULT_SELECTION)
        binding.timeIntervalContainer.setOnClickListener(View.OnClickListener {
            binding.timeInterval.performClick()
        })
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.setBottomNavEnable(false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun init() {
        viewModel = ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        administrationViewModel = ViewModelProviders.of(requireActivity()).get(AdministrationViewModel::class.java)
        profileViewModel = ViewModelProviders.of(requireActivity()).get(ProfileViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)
        viewModel.setDashboardNavigationHandler(this)
        sharedPrefsClient = SharedPrefsClient(context)
        viewModel.setBottomNavEnable(false)

        if (sharedPrefsClient.getDbSyncStatus().isNewLogin) {
            //userAlertClient.showWaitScreen("Loading cabin stats...")
//            navigateTo(NAV_LOADING_SCREEN)
//            viewModel.truncateAllTables(truncateDbRequestHandler)
            Log.i("dashBoard", "init: New Login")
            sharedPrefsClient.clearAccessTreeTimeStamp()
            profileViewModel.loadAccessTreeForUser(sharedPrefsClient.getUserDetails().user.userName, provisioningTreeRequestHandler)
        }
        navigateTo(NAV_LOADING_SCREEN)
        loadCards()

        viewModel.syncStatus.observe(viewLifecycleOwner, Observer {
            val syncStatus = it ?: return@Observer
            if (!syncStatus) {
                //Sync Completed
                loadCards();
            }
        })
        viewModel.uiResult.observe(viewLifecycleOwner, uiResultObserver)
    }

    private var uiResultObserver: Observer<UiResult> = Observer {
        val uiResult = it as UiResult

        Log.i("gg", "UiResult DashBoard: "+ Gson().toJson(uiResult))

        val viewFeedBackStats = uiResult.data.average_feedback
        if (viewFeedBackStats == "true") {
            feedbackStats.visibility = View.VISIBLE
        }
        else if (viewFeedBackStats == "false") {
            feedbackStats.visibility = View.GONE
        }

        val viewCollectionStats = uiResult.data.collection_stats
        if (viewCollectionStats == "true") {
            collectionStats.visibility = View.VISIBLE
            upiCollectionStats.visibility = View.VISIBLE
        } else if (viewCollectionStats == "false") {
            collectionStats.visibility = View.GONE
            upiCollectionStats.visibility = View.GONE
        }
        val viewBwt = uiResult.data.bwt_stats

        if(viewBwt == "true"){
            bwtCollectionStats.visibility = View.VISIBLE
        }
        else{
            bwtCollectionStats.visibility = View.GONE
        }

        viewModel.setBottomNavEnable(true)
    }

    //action on select duration
    var durationSelectionListener: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i("_durationSelection", " - ")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                val duration = Nomenclature.getDuration(index)
//                userAlertClient.showWaitDialog("Loading data...")
                //FragmentIllegalState-Fix
                navigateTo(NAV_LOADING_SCREEN)
                Log.e("_illegalState", "UsageStats  : " + lifecycle.currentState)
//                if (lifecycle.currentState == Lifecycle.State.RESUMED)
                viewModel.getDataWithDuration(duration, resultFromServer)
            }

        }

    private var resultFromServer: RepositoryCallback<String> = RepositoryCallback { result ->
            userAlertClient.closeWaitDialog()
            if (result is _Result.Success<String>) {
                navigateTo(NAV_DATA_SCREEN)
            }
            else {
                navigateTo(NAV_DATA_SCREEN)
                val err = result as _Result.Error<String>
                userAlertClient.showDialogMessage("Error Alert", err.message, false)
            }
        }

    var truncateDbRequestHandler: RequestHandler = object : RequestHandler {
        override fun onSuccess() {
            viewModel.getAccessibleComplexList(
                administrationViewModel,
                accessibleComplexRequestHandler
            )
        }

        override fun onError(message: String?) {
            userAlertClient.closeWaitScreen()
            viewModel.getAccessibleComplexList(
                administrationViewModel,
                accessibleComplexRequestHandler
            )
        }
    }

    var accessibleComplexRequestHandler: AccessibleComplexRequestHandler = object : AccessibleComplexRequestHandler {
            override fun onSuccess(data: MutableList<Complex>?) {
                sharedPrefsClient.saveAccessibleComplexList(getNames(data))

                if (UserProfile.isClientSpecificRole(sharedPrefsClient.getUserDetails().role))
                    viewModel.getMisUserData(userDataRequestHandler)
                else
                    viewModel.getMisVendorData(userDataRequestHandler)
            }

            override fun onError(message: String?) {
                userAlertClient.closeWaitScreen()
                userAlertClient.showDialogMessage("Error Alert", message, true)
            }

        }

    var userDataRequestHandler: RequestHandler = object : RequestHandler {
        override fun onSuccess() {
            Log.e("_illegalState", "UsageStats  : " + lifecycle.currentState)
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                //userAlertClient.closeWaitScreen()
                navigateTo(NAV_DATA_SCREEN)
                loadCards()
                (activity as HomeActivity).updateSyncTimeStamp()
            }

        }

        override fun onError(message: String?) {
            Log.i("__ExecuteLambda", "dashboard: onError: ")
            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                //userAlertClient.closeWaitScreen()
                userAlertClient.showDialogMessage("Error Alert!", message, false)
                navigateTo(NAV_DATA_SCREEN)
                loadCards()
            }
        }
    }

    //load data and navigate to fragments
    fun loadCards() {
        navigateTo(NAV_FLIPPING_CARD)
        navigateTo(NAV_USAGE_STATS)
        Log.i("_illegalState", "2" + lifecycle.currentState);
        navigateTo(NAV_FEEDBACK_STATS)
        navigateTo(NAV_COLLECTION_STATS)
        navigateTo(NAV_UPI_COLLECTION_STATS)
        navigateTo(NAV_BWT_STATS)
        navigateTo(NAV_COMPLEX_ACTIVE_TICKETS)
        navigateTo(NAV_COMPLEX_HEALTH_STATS)
        navigateTo(NAV_COMPLEX_CONNECTION_STATS)
        navigateTo(NAV_WATER_LEVEL_STATS)
        navigateTo(NAV_QUICK_ACCESS)
        navigateTo(NAV_QUICK_CONFIG)
    }

    override fun navigateTo(navigationAction: Int) {
        when (navigationAction) {

            NAV_LOADING_SCREEN -> {
                binding.loadingScreen.bringToFront()
                binding.loadingScreen.visibility = View.VISIBLE
                LoadingFragment.getInstance().setMessage("Loading cabin stats...")
                navigationClient.loadFragment(
                    LoadingFragment.getInstance(),
                    R.id.loadingScreen, "loadingScreen", false
                )
            }

            NAV_DATA_SCREEN -> {
                binding.loadingScreen.visibility = View.GONE
            }

            NAV_FLIPPING_CARD -> {
                navigationClient.loadFragment(
                    ScrollingCard.getInstance(),
                    R.id.flippingCard, "flippingCard", false
                )
            }

            NAV_USAGE_STATS -> {
                navigationClient.loadFragment(
                    UsageStats.getInstance(),
                    R.id.usageStats, "usageStats", false
                )
            }

            NAV_USAGE_DETAILS -> {
                UsageDetails.getInstance()
                    .show(childFragmentManager.beginTransaction(), "UsageDetails")
            }

            NAV_FEEDBACK_STATS -> {
                navigationClient.loadFragment(
                    FeedbackStats.getInstance(),
                    R.id.feedbackStats, "feedbackStats", false
                )
            }

            NAV_FEEDBACK_DETAILS -> {
                FeedbackDetails.getInstance()
                    .show(childFragmentManager.beginTransaction(), "FeedbackDetails")
            }

            NAV_COLLECTION_STATS -> {
                navigationClient.loadFragment(
                    CollectionStats.getInstance(),
                    R.id.collectionStats, "collectionStats", false
                )
            }

            NAV_UPI_COLLECTION_STATS -> {
                navigationClient.loadFragment(
                    UpiStats.getInstance(),
                    R.id.upiCollectionStats, "upiCollectionStats", false
                )
            }

            NAV_BWT_STATS -> {
                navigationClient.loadFragment(
                    BwtStat.getInstance(),
                    R.id.bwtCollectionStats, "bwtStats", false
                )
            }

            NAV_COLLECTION_DETAILS -> {
                CollectionDetails.getInstance()
                    .show(childFragmentManager.beginTransaction(), "CollectionDetails")
            }

            NAV_UPI_COLLECTION_DETAILS -> {
                UpiDetails.getInstance().show(childFragmentManager.beginTransaction(), "UpiDetails")
            }

             NAV_BWT_DETAILS -> {
                BwtDetails.getInstance().show(childFragmentManager.beginTransaction(), "BwtDetails")
            }

            NAV_COMPLEX_HEALTH_STATS -> {
                navigationClient.loadFragment(
                    ComplexHealthStats.getInstance(),
                    R.id.healthStatus, "ComplexHealthStats", false
                )
            }

            NAV_COMPLEX_CONNECTION_STATS -> {
                navigationClient.loadFragment(
                    ComplexConnectionStats.getInstance(),
                    R.id.connectionStatus, "ComplexHealthStats", false
                )
            }

            NAV_COMPLEX_ACTIVE_TICKETS -> {
                navigationClient.loadFragment(
                    ActiveTicketStats.getInstance(),
                    R.id.activeTickets, "ActiveTicketStats", false
                )
            }

            NAV_WATER_LEVEL_STATS -> {
                navigationClient.loadFragment(
                    WaterLevelStats.getInstance(),
                    R.id.waterLevelStatus, "WaterLevelStats", false
                )
            }

            NAV_QUICK_ACCESS -> {
                navigationClient.loadFragment(
                    QuickAccess.getInstance(),
                    R.id.quickAccess, "QuickAccess", false
                )
            }

            NAV_QUICK_ACCESS_ACTIONS -> {
                QuickAccessCommands.getInstance()
                    .show(childFragmentManager.beginTransaction(), "QuickAccessCommands")
            }

            NAV_QUICK_CONFIG -> {
                navigationClient.loadFragment(
                    QuickConfig.getInstance(),
                    R.id.quickConfig, "QuickConfig", false
                )
            }

            NAV_QUICK_CONFIG_DATA_REQUEST -> {
                HomeDataRequestConfig.getInstance()
                    .show(childFragmentManager.beginTransaction(), "HomeDataRequestConfig")
            }

            NAV_QUICK_CONFIG_LIGHT_FAN -> {
                HomeLightFanConfig.getInstance()
                    .show(childFragmentManager.beginTransaction(), "HomeLightFanConfig")
            }

            NAV_QUICK_CONFIG_FLOOR_CLEAN -> {
                HomeFloorConfig.getInstance()
                    .show(childFragmentManager.beginTransaction(), "HomeFloorConfig")
            }

            NAV_QUICK_CONFIG_FLUSH -> {
                HomeFlushConfig.getInstance()
                    .show(childFragmentManager.beginTransaction(), "HomeFlushConfig")
            }

            NAV_QUICK_CONFIG_USAGE_CHARGE -> {
                HomeUsageChargeConfig.getInstance()
                    .show(childFragmentManager.beginTransaction(), "HomeUsageChargeConfig")
            }
        }
    }

    //complex tree
    private var provisioningTreeRequestHandler: ProvisioningTreeRequestHandler = object : ProvisioningTreeRequestHandler {
            override fun onSuccess(mCountry: Country?) {
                if (mCountry != null) {
                    Log.i("dashboard", "onSuccess: fetched new Access tree from ")
                }
            }

            override fun onError(message: String?) {
                Log.e("dashboard", "onError: "+message )
            }
        }

}