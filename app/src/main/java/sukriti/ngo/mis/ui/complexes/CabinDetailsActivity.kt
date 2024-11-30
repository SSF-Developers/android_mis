package sukriti.ngo.mis.ui.complexes

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import kotlinx.android.synthetic.main.login.*
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.databinding.CabinDetailsActivityBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.repository.entity.Cabin
import sukriti.ngo.mis.repository.entity.Complex
import sukriti.ngo.mis.repository.entity.QuickAccess
import sukriti.ngo.mis.ui.administration.interfaces.RequestHandler
import sukriti.ngo.mis.ui.complexes.ComplexesViewModel.Companion.NAV_CABIN_HOME
import sukriti.ngo.mis.ui.complexes.ComplexesViewModel.Companion.NAV_CABIN_HOME_BWT
import sukriti.ngo.mis.ui.complexes.fragments.BwtCabinDetails
import sukriti.ngo.mis.ui.complexes.fragments.CabinDetails
import sukriti.ngo.mis.ui.complexes.interfaces.CabinComplexSelectionHandler
import sukriti.ngo.mis.ui.dashboard.interfaces.CheckQuickAccessRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.Nomenclature.getCabinType
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient

class CabinDetailsActivity : NavigationHandler, AppCompatActivity() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinDetailsActivityBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationClient: NavigationClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private var hasQuickAccess = false
    private lateinit var quickAccess: QuickAccess

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }


    private fun init() {
        sharedPrefsClient = SharedPrefsClient(applicationContext)
        viewModel = ViewModelProviders.of(this).get(CabinViewModel::class.java)
        binding = CabinDetailsActivityBinding.inflate(layoutInflater)
        navigationClient = NavigationClient(supportFragmentManager)
        setContentView(binding.root)
        userAlertClient = UserAlertClient(this)

        //Action bar and Menu Drawer
        setSupportActionBar(binding.toolbar.mainToolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar!!.setDisplayShowHomeEnabled(true)
//        binding.toolbar.mainToolbar.setNavigationOnClickListener {
//            finish()
//        }
        viewModel.getSelectedCabinAndComplex(selectionRequestHandler)



        binding.toolbar.quickAccessCont.setOnClickListener(View.OnClickListener {
            userAlertClient.showWaitDialog("Loading...")
            if (hasQuickAccess)
                viewModel.removeQuickAccess(quickAccess.ThingName, removeQuickAccessRequestHandler)
            else
                viewModel.addQuickAccessItem(quickAccess, addQuickAccessRequestHandler)

        })
    }

    //on selection on cabin from complex composition
    private var selectionRequestHandler: CabinComplexSelectionHandler =
        CabinComplexSelectionHandler { complex, cabin ->
            Log.i("_ComplexesVM", "onSelection complex: " + Gson().toJson(complex))
            Log.i("_ComplexesVM", "onSelection cabin: " + Gson().toJson(cabin))
            if (cabin != null) {
                viewModel.forCabin = cabin.ThingName
                userAlertClient.showWaitDialog("Loading Data...")
                //                viewModel.getCabinData(resultFromServer)
            }

            //            if (getCabinType(Nomenclature.CABIN_TYPE_BWT, cabin?.ShortThingName)) {
            if (getCabinType(Nomenclature.CABIN_TYPE_BWT, cabin?.ShortThingName?.substring(0,2))) {
                viewModel.getBwtCabinData(resultFromServer)
                navigateTo(NAV_CABIN_HOME_BWT)
            } else {
                viewModel.getCabinData(resultFromServer)
                navigateTo(NAV_CABIN_HOME)
            }

            Log.i("_setSelection", complex?.ComplexName)
            binding.toolbar.title.text = "" + complex?.ComplexName
            binding.toolbar.subTitle.text = cabin?.ShortThingName

            if (complex != null && cabin != null) {
                quickAccess = QuickAccess(complex, cabin, "-1")
                viewModel.hasQuickAccess(quickAccess.ThingName, hasQuickAccessRequestHandler)
            }
        }

    private var resultFromServer: RepositoryCallback<String> =
        RepositoryCallback { result ->
            if(result is _Result.Success<String>){
                Log.i("clientDetails", "result success")
                userAlertClient.closeWaitDialog()
            }else{
                val err = result as _Result.Error<String>
                userAlertClient.showDialogMessage("Error Alert",err.message,false)
            }
        }
    private var hasQuickAccessRequestHandler: CheckQuickAccessRequestHandler =
        CheckQuickAccessRequestHandler { result ->
            hasQuickAccess = result
            if (hasQuickAccess)
                binding.toolbar.quickAccess.setImageResource(R.drawable.ic_quick_access_active)
            else
                binding.toolbar.quickAccess.setImageResource(R.drawable.ic_quick_access)
        }

    var addQuickAccessRequestHandler: RequestHandler = object : RequestHandler {
        override fun onSuccess() {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage(
                "Quick Access",
                "Cabin added to quick access list successfully.",
                false
            )
            hasQuickAccess = true
            binding.toolbar.quickAccess.setImageResource(R.drawable.ic_quick_access_active)
        }

        override fun onError(message: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!", message, false)
        }

    }

    private var removeQuickAccessRequestHandler: RequestHandler = object : RequestHandler {
        override fun onSuccess() {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage(
                "Quick Access",
                "Cabin removed from quick access list successfully.",
                false
            )
            hasQuickAccess = false
            binding.toolbar.quickAccess.setImageResource(R.drawable.ic_quick_access)
        }

        override fun onError(message: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!", message, false)
        }

    }

    override fun navigateTo(navigationAction: Int) {

        when (navigationAction) {
            NAV_CABIN_HOME -> {
                val cabinDetails: CabinDetails = CabinDetails.getInstance()
                navigationClient.loadFragment(
                    cabinDetails,
                    "cabinDetails",
                    false
                )
            }

            NAV_CABIN_HOME_BWT -> {
                val bwtCabinDetails = BwtCabinDetails.getInstance()
                navigationClient.loadFragment(
                    bwtCabinDetails,
                    "bwtCabinDetails",
                    false
                )
            }
        }
    }
}
