package sukriti.ngo.mis.ui.administration.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import sukriti.ngo.mis.databinding.MemberDetailsHomeBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import sukriti.ngo.mis.utils.*

class MemberDetailsHome : Fragment() {

    private lateinit var viewModel: AdministrationViewModel
    private lateinit var binding: MemberDetailsHomeBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var navigationClient : NavigationClient
    private var memberDetails: Fragment? = null
    private var memberAccess: Fragment? = null
    private var selectedTabIndex = 1
    private val TAG = "MemberDetailsHome"

    companion object {
        private var INSTANCE: MemberDetailsHome? = null

        fun getInstance(): MemberDetailsHome {
            return INSTANCE
                ?: MemberDetailsHome()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: Member Detail ")
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = MemberDetailsHomeBinding.inflate(layoutInflater)
        init()
        Log.i(TAG, "onCreateView: Member Details Home")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated: Member Details")
    }



    private fun init() {
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(AdministrationViewModel::class.java)
        navigationClient = NavigationClient(childFragmentManager)
        if(memberDetails == null) memberDetails = MemberDetails.getInstance()
        navigationClient.loadFragment(memberDetails!!,"Member Details",true)
        binding.tabLayout.getTabAt(0)?.select()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabIndex = tab?.position!!

                when(tab.position){
                    0->{
                        if(memberDetails == null) memberDetails = MemberDetails.getInstance()
                        navigationClient.loadFragment(memberDetails!!,"Member Details",true)
                    }

                    1->{
                        if(memberAccess == null) memberAccess = MemberAccess.getInstance()
                        navigationClient.loadFragment(memberAccess!!,"Member Access",true)
                    }
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: Member Details")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause: Member Details")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop: Member Details")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "onDestroyView: Member Details")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: Member Details")
    }

}
