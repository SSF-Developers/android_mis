package sukriti.ngo.mis.ui.management.fargments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.FragmentEnterpriseHomeBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import sukriti.ngo.mis.ui.administration.fragments.MemberAccess
import sukriti.ngo.mis.ui.administration.fragments.MemberDetails
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel
import sukriti.ngo.mis.ui.management.ManageDevices
import sukriti.ngo.mis.ui.management.ManagementViewModel
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient

class EnterpriseHome : Fragment() {

    private lateinit var binding: FragmentEnterpriseHomeBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var navigationClient : NavigationClient
    private lateinit var enrollDeviceViewModel: EnrollDeviceViewModel
    private lateinit var managementViewModel: ManagementViewModel
    private var enterpriseDetails: Fragment? = null
    private var devices: Fragment? = null
    private var policies: Fragment? = null
    private var complexCurd: Fragment? = null
    private var selectedTabIndex = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentEnterpriseHomeBinding.inflate(inflater)
        init()
        return binding.root
    }

    private fun init() {
        userAlertClient = UserAlertClient(activity)
        enrollDeviceViewModel = ViewModelProviders.of(requireActivity()).get(EnrollDeviceViewModel::class.java)
        managementViewModel = ViewModelProviders.of(requireActivity()).get(ManagementViewModel::class.java)
        navigationClient = NavigationClient(childFragmentManager)

//        if(memberDetails == null) memberDetails = MemberDetails.getInstance()
//        navigationClient.loadFragment(memberDetails!!,"Member Details",true)
        if(enterpriseDetails == null) enterpriseDetails = EnterpriseDetails.getInstance(managementViewModel)
        navigationClient.loadFragment(enterpriseDetails!!, "Enterprise Details", true)
        binding.tabLayout.getTabAt(0)?.select()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!

                when(tab.position){
                    0->{
                        if(enterpriseDetails == null) enterpriseDetails = EnterpriseDetails.getInstance(managementViewModel)
                        navigationClient.loadFragment(enterpriseDetails!!, "Enterprise Details", true)
                    }

                    1->{
                        if(devices == null) devices = ManageDevices.getInstance()
                        navigationClient.loadFragment(devices!!, "Manage Devices", true)

                    }

                    2 -> {
                        if(policies == null) policies = ManagePolicies.getInstance(managementViewModel, enrollDeviceViewModel)
                        navigationClient.loadFragment(policies!!, "Manage Policies", true)
                    }

                    3 -> {
                        if(complexCurd == null) complexCurd = ComplexCRUD.getInstance()
                        navigationClient.loadFragment(complexCurd!!, "Complex CRUD", true)
                    }
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    companion object {
        private const val TAG = "EnterpriseHome"
        private var INSTANCE :EnterpriseHome? = null

        fun getInstance(): EnterpriseHome {
            return INSTANCE ?: EnterpriseHome()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(TAG, "onAttach: Member Details")
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
        managementViewModel.devicesList.clear()
//        managementViewModel.policyList.clear()
        managementViewModel.deleteEnterpriseList.clear()

    }

}