package sukriti.ngo.mis.ui.super_admin.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import sukriti.ngo.mis.R
import sukriti.ngo.mis.adapters.QuickAccessAdapter
import sukriti.ngo.mis.databinding.DashboardQuickAccessBinding
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.interfaces.QuickAccessRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient

class QuickAccess : Fragment() {
    private lateinit var binding: DashboardQuickAccessBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var adapter: QuickAccessAdapter
    private lateinit var sharedPrefsClient: SharedPrefsClient

    companion object {
        private var INSTANCE: QuickAccess? = null

        fun getInstance(): QuickAccess {
            return INSTANCE
                ?: QuickAccess()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardQuickAccessBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.root.visibility = View.GONE // not in use  respect to web

        sharedPrefsClient = SharedPrefsClient(context)
        viewModel =
            ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)

        viewModel.listQuickAccessCabins(requestHandler)
    }

    var requestHandler: QuickAccessRequestHandler = object:
        QuickAccessRequestHandler {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onSuccess(data: MutableList<sukriti.ngo.mis.repository.entity.QuickAccess>?) {
            binding.banner.root.visibility = View.GONE
            binding.complexGrid.visibility = View.VISIBLE

            val gridLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            binding.complexGrid.layoutManager = gridLayoutManager
            adapter = QuickAccessAdapter(context, data,selectionHandler)
            binding.complexGrid.adapter = adapter

            binding.label.setTextColor(resources.getColor(R.color.primary,null))
            var count = data?.size
            if(count == 1)
                binding.label.text = "1 unit marked for quick access"
            else
                binding.label.text = "$count units marked for quick access"
        }

        @RequiresApi(Build.VERSION_CODES.M)
        override fun onError(message: String?) {
            binding.banner.root.visibility = View.VISIBLE
            binding.complexGrid.visibility = View.GONE
            binding.banner.label.text = "No units marked for quick access. ${Nomenclature.QuickAccessListLimit} empty quick access slots available."
//            binding.label.setTextColor(resources.getColor(R.color.primary,null))
            binding.label.text = "No Quick Access defined"
        }

    }

    var selectionHandler : QuickAccessAdapter.QuickAccessCabinSelectionHandler = object : QuickAccessAdapter.QuickAccessCabinSelectionHandler{
        override fun onCabinSelected(data: sukriti.ngo.mis.repository.entity.QuickAccess?) {
            sharedPrefsClient.saveQuickAccessSelection(data)
            viewModel.getDashboardNavigationHandler()
                .navigateTo(DashboardViewModel.NAV_QUICK_ACCESS_ACTIONS)
        }
    }
}