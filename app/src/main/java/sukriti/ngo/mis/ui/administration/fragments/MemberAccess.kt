package sukriti.ngo.mis.ui.administration.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.databinding.MemberAccessBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import sukriti.ngo.mis.ui.administration.AdministrationViewModel.Companion.NAV_DEFINE_ACCESS
import sukriti.ngo.mis.ui.administration.adapter.define_access.StateAdapter
import sukriti.ngo.mis.ui.administration.adapter.read_access.StateAdapterRO
import sukriti.ngo.mis.ui.administration.data.TreeEdge
import sukriti.ngo.mis.ui.administration.interfaces.ProvisioningTreeRequestHandler
import sukriti.ngo.mis.ui.administration.interfaces.TreeInteractionListener
import sukriti.ngo.mis.ui.complexes.lambda.CabinDetailsLambdaClient
import sukriti.ngo.mis.ui.complexes.lambda.Complex_fetchAccessTree.AccessTreeLambdaRequest
import sukriti.ngo.mis.ui.login.data.UserProfile
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities.getUserAccessCount


class MemberAccess : Fragment() {
    private lateinit var viewModel: AdministrationViewModel
    private lateinit var binding: MemberAccessBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var accessTreeAdapter: StateAdapterRO
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var accessTreeLambda : CabinDetailsLambdaClient


    companion object {
        private var INSTANCE: MemberAccess? = null
        private const val TAG = "MemberAccess"
        fun getInstance(): MemberAccess {
            return INSTANCE
                ?: MemberAccess()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MemberAccessBinding.inflate(layoutInflater)
        Log.i(TAG, "MemberAccess: onCreateView")
        init()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationHandler) {
            navigationHandler = context
        } else {
            throw RuntimeException("$context must implement NavigationHandler")
        }
    }

    override fun onResume() {
        super.onResume()
    }


    private fun init() {
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(AdministrationViewModel::class.java)
        accessTreeLambda = CabinDetailsLambdaClient(context)
        binding.defineAccess.setOnClickListener{
            navigationHandler.navigateTo(NAV_DEFINE_ACCESS)
        }

        if (UserProfile.getRole(viewModel.getSelectedUser().cognitoUser.role) == UserProfile.Companion.UserRole.SuperAdmin) {
            binding.recyclerView.visibility = View.GONE
            binding.defineAccess.visibility = View.GONE
            binding.countStates.text = getString(R.string.all)
            binding.countDistricts.text = getString(R.string.all)
            binding.countCities.text = getString(R.string.all)
            binding.countComplexes.text = getString(R.string.all)
            binding.superAdminAccess.visibility = View.VISIBLE
        }
        else if (UserProfile.getRole(viewModel.getSelectedUser().cognitoUser.role) == UserProfile.Companion.UserRole.ClientSuperAdmin) {
            binding.defineAccess.visibility = View.GONE
            userAlertClient.showWaitDialog("Creating client provisioning tree...")

            Log.i("AccessMember", "init: client super admin selected user ${viewModel.getSelectedUser().cognitoUser.userName}" )
            viewModel.accessTreeClient.loadAccessTreeForUser(viewModel.getSelectedUser().cognitoUser.userName, clientSuperAdminAccessTreeRequestHandler)
        }
        else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.noData.visibility = View.GONE
            userAlertClient.showWaitDialog("Loading Access Tree")
            Log.i("AccessMember", "init: selected user ${viewModel.getSelectedUser().cognitoUser.userName}" )
            viewModel.accessTreeClient.loadAccessTreeForUser(viewModel.getSelectedUser().cognitoUser.userName, accessTreeHandler)

        }
    }

    private var clientSuperAdminAccessTreeRequestHandler: ProvisioningTreeRequestHandler = object : ProvisioningTreeRequestHandler{
        override fun onSuccess(country: Country?) {
            userAlertClient.closeWaitDialog()

            if(country!=null){
                binding.recyclerView.visibility = View.VISIBLE
                binding.noData.visibility = View.GONE
                accessTreeAdapter =
                    StateAdapterRO(
                        context,
                        country,
                        mTreeInteractionListener
                    )
                val accessCount = getUserAccessCount(country)
                val gridLayoutManager = GridLayoutManager(context, 1)
                binding.recyclerView.setLayoutManager(gridLayoutManager)
                binding.recyclerView.adapter = accessTreeAdapter
                binding.summaryContainer.visibility = View.VISIBLE
                binding.countStates.text = "" + accessCount.state
                binding.countDistricts.text = "" + accessCount.district
                binding.countCities.text = "" + accessCount.city
                binding.countComplexes.text = "" + accessCount.complex
            }
            else{
                userAlertClient.showDialogMessage("Access Tree Empty!","No Cabins provisioned for you as of now.",false)
                binding.recyclerView.visibility = View.GONE
                binding.noData.visibility = View.VISIBLE
            }
        }

        override fun onError(message: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert",message,false)
        }

    }

    var mTreeInteractionListener: TreeInteractionListener = object : TreeInteractionListener {
        override fun onSelectionChange(treeNodeType: Int, treeEdge: TreeEdge, selection: Int) {
            when (treeNodeType) {
//                AdministrationViewModel.TREE_NODE_STATE -> {
//                    country.states[treeEdge.stateIndex].recursive = selection
//                    selectedStates = updateSelectedCount(selection,binding.countStates,selectedStates)
//
//                    Log.i("TreeInteraction",""+country.states[0].recursive)
//                }
//                AdministrationViewModel.TREE_NODE_DISTRICT -> {
//                    country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].recursive = selection
//                    selectedDistricts = updateSelectedCount(selection,binding.countDistricts,selectedDistricts)
//                }
//                AdministrationViewModel.TREE_NODE_CITY -> {
//                    country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].cities[treeEdge.cityIndex].recursive = selection
//                    selectedCities = updateSelectedCount(selection,binding.countCities,selectedCities)
//                }
//                AdministrationViewModel.TREE_NODE_COMPLEX -> {
//                    country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].cities[treeEdge.cityIndex].complexes[treeEdge.complexIndex].isSelected = selection
//                    selectedComplexes = updateSelectedCount(selection,binding.countComplexes,selectedComplexes)
//                }
            }
        }

    }

    private fun updateSelectedCount(selection: Boolean, view: TextView, value: Int): Int {
        var localVal = value
        if (selection)
            localVal++;
        else
            localVal--;
        view.text = "" + localVal

        return localVal
    }

    private var accessTreeHandler: ProvisioningTreeRequestHandler = object : ProvisioningTreeRequestHandler {
        override fun onSuccess(country: Country?) {
            userAlertClient.closeWaitDialog()


            if(country == null) {
                Log.i("AccessMember", "onSuccess: country null")
            }
            else if(country.states == null) {
                Log.i("AccessMember", "onSuccess: states null")
            }

                accessTreeAdapter =
                StateAdapterRO(
                    context,
                    country,
                    mTreeInteractionListener
                )


            val accessCount = getUserAccessCount(viewModel.getSelectedUser().userAccess)
            val gridLayoutManager = GridLayoutManager(context, 1)
            binding.recyclerView.setLayoutManager(gridLayoutManager)
            binding.recyclerView.adapter = accessTreeAdapter
            binding.summaryContainer.visibility = View.VISIBLE
            binding.countStates.text = "" + accessCount.state
            binding.countDistricts.text = "" + accessCount.district
            binding.countCities.text = "" + accessCount.city
            binding.countComplexes.text = "" + accessCount.complex
        }

        override fun onError(message: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error", message, false)
        }

    }
}
