package sukriti.ngo.mis.ui.profile.fragments

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
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.databinding.MemberAccessBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.administration.adapter.define_access.StateAdapter
import sukriti.ngo.mis.ui.administration.adapter.read_access.StateAdapterRO
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData
import sukriti.ngo.mis.ui.administration.data.TreeEdge
import sukriti.ngo.mis.ui.administration.fragments.MemberDetails
import sukriti.ngo.mis.ui.administration.interfaces.ProvisioningTreeRequestHandler
import sukriti.ngo.mis.ui.administration.interfaces.TreeInteractionListener
import sukriti.ngo.mis.ui.login.data.UserProfile
import sukriti.ngo.mis.ui.profile.ProfileViewModel
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities.getUserAccessCount
import sukriti.ngo.mis.utils.Utilities.getUserAccessCountRO


class ProfileUserAccess : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: MemberAccessBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var accessTreeAdapter: StateAdapterRO
    private lateinit var navigationHandler: NavigationHandler
    private var selectedStates = 0
    private var selectedDistricts = 0
    private var selectedCities = 0
    private var selectedComplexes = 0

    companion object {
        private var INSTANCE: ProfileUserAccess? = null

        fun getInstance(): ProfileUserAccess {
            return INSTANCE
                ?: ProfileUserAccess()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MemberAccessBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NavigationHandler) {
            navigationHandler = context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement NavigationHandler"
            )
        }
    }

    override fun onResume() {
        super.onResume()
    }


    private fun init() {
        userAlertClient = UserAlertClient(activity)
        viewModel =
            ViewModelProviders.of(requireActivity()).get(ProfileViewModel::class.java)

        userAlertClient.showWaitDialog("Fetching user access tree")
        viewModel.loadAccessTreeForUser(viewModel.getProfileData().userName, object: ProvisioningTreeRequestHandler {
            override fun onSuccess(country: Country?) {
                userAlertClient.closeWaitDialog()
                val memberDetails = MemberDetailsData()
                memberDetails.userAccess = country
                viewModel.setUserDetails(memberDetails)

                binding.defineAccess.visibility = View.GONE
                if(viewModel.getUserDetails().userAccess == null){
                    binding.recyclerView.visibility = View.GONE
                    binding.noData.visibility = View.VISIBLE
                }
                else if(UserProfile.getRole(viewModel.getProfileData().userRole)==UserProfile.Companion.UserRole.SuperAdmin){
                    Log.d("profileDebug", "Super Admin")
                    binding.recyclerView.visibility = View.GONE
                    binding.defineAccess.visibility = View.GONE
                    binding.countStates.text = "ALL"
                    binding.countDistricts.text = "ALL"
                    binding.countCities.text = "ALL"
                    binding.countComplexes.text = "ALL"
                    binding.superAdminAccess.visibility = View.VISIBLE
                }
                else if(UserProfile.getRole(viewModel.getProfileData().userRole)==UserProfile.Companion.UserRole.ClientSuperAdmin){
                    binding.recyclerView.visibility = View.GONE
                    binding.defineAccess.visibility = View.GONE
                    binding.countStates.text = "ALL"
                    binding.countDistricts.text = "ALL"
                    binding.countCities.text = "ALL"
                    binding.countComplexes.text = "ALL"
                    binding.superAdminAccess.visibility = View.VISIBLE
                }
                else{
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.noData.visibility = View.GONE
                    accessTreeAdapter =
                        StateAdapterRO(
                            context,
                            viewModel.getUserDetails().userAccess,
                            mTreeInteractionListener
                        )
//            rahul karn
//            var accessCount = getUserAccessCount(viewModel.getUserDetails().userAccess)
                    var accessCount = getUserAccessCountRO(viewModel.getUserDetails().userAccess)
//            ********
                    val gridLayoutManager = GridLayoutManager(context, 1)
                    binding.recyclerView.setLayoutManager(gridLayoutManager)
                    binding.recyclerView.adapter = accessTreeAdapter
                    binding.summaryContainer.visibility = View.VISIBLE
                    binding.countStates.text = ""+accessCount.state
                    binding.countDistricts.text = ""+accessCount.district
                    binding.countCities.text = ""+accessCount.city
                    binding.countComplexes.text = ""+accessCount.complex
                }

            }

            override fun onError(message: String?) {
                userAlertClient.closeWaitDialog()
            }
        })

    }


    var mTreeInteractionListener : TreeInteractionListener = object : TreeInteractionListener {
        override fun onSelectionChange(treeNodeType: Int, treeEdge: TreeEdge, selection: Int) {
            when(treeNodeType){
            }
        }

    }

    private fun updateSelectedCount(selection: Boolean, view: TextView, value: Int) : Int{
        var localVal = value
        if(selection)
            localVal++;
        else
            localVal--;
        view.text = ""+localVal

        return localVal
    }
}
