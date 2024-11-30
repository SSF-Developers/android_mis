package sukriti.ngo.mis.ui.complexes.fragments.acces_tree

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.login.*
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.databinding.ComplexesHomeBinding
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData
import sukriti.ngo.mis.ui.administration.interfaces.ProvisioningTreeRequestHandler
import sukriti.ngo.mis.ui.administration.interfaces.TreeInteractionListener
import sukriti.ngo.mis.ui.complexes.ComplexesViewModel
import sukriti.ngo.mis.ui.complexes.adapters.StateAdapterMC
import sukriti.ngo.mis.ui.complexes.adapters.accessTreeComplexSelection.StateAdapterCS
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.ui.complexes.interfaces.ComplexDetailsRequestHandler
import sukriti.ngo.mis.ui.login.data.UserProfile.Companion.isClientSpecificRole
import sukriti.ngo.mis.ui.profile.ProfileViewModel
import sukriti.ngo.mis.ui.profile.interfaces.UserProfileRequestHandler
import sukriti.ngo.mis.ui.reports.adapters.StateAdapterST
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities
import sukriti.ngo.mis.utils.Utilities.getTrimmedDisplayAccessTree

class ComplexesAccessTree : Fragment() {
    private lateinit var viewModel: ComplexesViewModel
    private lateinit var administrationViewModel: AdministrationViewModel
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: ComplexesHomeBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var country: Country
    private lateinit var accessTree: Country
    private lateinit var accessTreeAdapter: StateAdapterCS
    private lateinit var sharedPrefsClient: SharedPrefsClient
    //private lateinit var accessTreeAdapter: CityComplexListAdapter

    companion object {
        private var INSTANCE: ComplexesAccessTree? = null

        fun getInstance(): ComplexesAccessTree {
            return INSTANCE
                ?: ComplexesAccessTree()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ComplexesHomeBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }


    private fun init() {
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel =
            ViewModelProviders.of(requireActivity()).get(ComplexesViewModel::class.java)
        administrationViewModel =
            ViewModelProviders.of(requireActivity()).get(AdministrationViewModel::class.java)
        profileViewModel =
            ViewModelProviders.of(requireActivity()).get(ProfileViewModel::class.java)


        binding.summaryContainer.visibility = View.GONE
        userAlertClient.showWaitDialog("Loading complex list...")
        profileViewModel.loadAccessTreeForUser(sharedPrefsClient.getUserDetails().user.userName, provisioningTreeRequestHandler)
//        profileViewModel.fetchUserDetails(
//            sharedPrefsClient.getUserDetails().user.userName,
//            userProfileRequestHandler
//        )
    }

    var userProfileRequestHandler: UserProfileRequestHandler = object : UserProfileRequestHandler {
        override fun onSuccess(userProfile: MemberDetailsData?) {

            if (isClientSpecificRole(sharedPrefsClient.getUserDetails().role)) {
                if (userProfile != null) {
                    administrationViewModel.setSelectedUser(userProfile)
                    administrationViewModel.getCompleteUserAccessTree(
                        sharedPrefsClient.getUserDetails().organisation.client,
                        sharedPrefsClient.getUserDetails().user.userName,
                        provisioningTreeRequestHandler
                    )
                }
            } else {
                if (userProfile != null) {
                    administrationViewModel.setSelectedUser(userProfile)
                    administrationViewModel.getCompleteUserAccessTree(
                        sharedPrefsClient.getUserDetails().user.userName,
                        provisioningTreeRequestHandler
                    )
                }
            }

        }

        override fun onError(error: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!", error, true)
        }

    }

    private var provisioningTreeRequestHandler: ProvisioningTreeRequestHandler =
        object : ProvisioningTreeRequestHandler {
            override fun onSuccess(mCountry: Country?) {
                if (mCountry != null) {
                    accessTree = Country()
                    country = getTrimmedDisplayAccessTree(mCountry)
                    userAlertClient.closeWaitDialog()

                    accessTreeAdapter =
                        StateAdapterCS(
                            context,
                            country,
                            mTreeInteractionListener
                        )

                    val gridLayoutManager = GridLayoutManager(context, 1)
                    binding.recyclerView.layoutManager = gridLayoutManager
                    binding.recyclerView.adapter = accessTreeAdapter
                    binding.summaryContainer.visibility = View.VISIBLE

                    val accessCount = Utilities.getUserAccessCount(country)
                    binding.countComplexes.text = "" + accessCount.complex
                }else{
                    userAlertClient.closeWaitDialog()
                }
            }

            override fun onError(message: String?) {
                userAlertClient.closeWaitDialog()
                userAlertClient.showDialogMessage("Error Alert", message, true);
            }
        }

    var mTreeInteractionListener: TreeInteractionListener =
        TreeInteractionListener { treeNodeType, treeEdge, selection ->
            when (treeNodeType) {
                AdministrationViewModel.TREE_NODE_STATE -> {
                    country.states[treeEdge.stateIndex].recursive = selection
                }
                AdministrationViewModel.TREE_NODE_DISTRICT -> {
                    country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].recursive =
                        selection
                }
                AdministrationViewModel.TREE_NODE_CITY -> {
                    country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].cities[treeEdge.cityIndex].recursive =
                        selection
                }
                AdministrationViewModel.TREE_NODE_COMPLEX -> {
                    val complexName = country.states[treeEdge.stateIndex]
                        .districts[treeEdge.districtIndex].cities[treeEdge.cityIndex]
                        .complexes[treeEdge.complexIndex].name
                    Log.i("myComplexName", complexName)
                    userAlertClient.showWaitDialog("Loading complex details...")
                    viewModel.getComplexDetails(complexName,complexDetailsRequestHandler)
                }
            }
        }

    private var complexDetailsRequestHandler : ComplexDetailsRequestHandler = object : ComplexDetailsRequestHandler{
        override fun onSuccess(complex: ComplexDetailsData?) {
            userAlertClient.closeWaitDialog()

            Log.i("_ComplexesVM", "onSuccess: "+Gson().toJson(complex))
            if (complex != null) {

                val complexDetailsSheet = ComplexComposition.getInstance()
                complexDetailsSheet.setDetails(complex)
                Log.i("_ComplexesVM", "onSuccess: "+Gson().toJson(complex))
                //complexDetailsSheet.setNavigationHandler(this@ComplexesHome)
                complexDetailsSheet.show(childFragmentManager,"complexDetails")
            }
            else{
                Log.i("_ComplexesVM", "onSuccess: null : "+Gson().toJson(complex))
            }
        }


        override fun onError(message: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert",message,false)
        }

    }
}
