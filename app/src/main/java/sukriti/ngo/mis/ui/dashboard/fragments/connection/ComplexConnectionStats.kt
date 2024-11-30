package sukriti.ngo.mis.ui.super_admin.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.adapters.ConnectionAdapter
import sukriti.ngo.mis.databinding.DashboardComplexConnectionStatsBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel.Companion.NAV_COMPLEXLIST_CONNECTION_STATS
import sukriti.ngo.mis.ui.dashboard.data.ConnectionStatus
import sukriti.ngo.mis.ui.dashboard.data.Table
import sukriti.ngo.mis.ui.dashboard.fragments.connection.ComplexConnectionActivity
import sukriti.ngo.mis.ui.dashboard.fragments.connection.ComplexList_ConnectionStats
import sukriti.ngo.mis.ui.reports.fragments.ReportsHome
import sukriti.ngo.mis.ui.tickets.TicketsViewModel
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient

class ComplexConnectionStats : Fragment(),ConnectionAdapter.ClickHandler {
    private lateinit var binding: DashboardComplexConnectionStatsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var adapter: ConnectionAdapter

    companion object {
        private var INSTANCE: ComplexConnectionStats? =
            null

        fun getInstance(): ComplexConnectionStats {
            return INSTANCE
                ?: ComplexConnectionStats()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardComplexConnectionStatsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel = ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)
        binding.label.setOnClickListener {
            (requireActivity() as NavigationHandler).navigateTo(NAV_COMPLEXLIST_CONNECTION_STATS)
//            navigateTo(NAV_COMPLEXLIST_CONNECTION_STATS)
//            navigate()
        }

//        viewModel.listFaultyComplexes(requestHandler)
        viewModel.connectionStatus.observe(viewLifecycleOwner, dis_ComplexObserver)
    }

    fun navigate() {
//        val frag = ComplexList_ConnectionStats.getInstance()
//        frag.show(childFragmentManager.beginTransaction(), "ComplexList")



//        startActivity(Intent(context,ComplexConnectionActivity::class.java))

    }

    @RequiresApi(Build.VERSION_CODES.M)
    var dis_ComplexObserver: Observer<ArrayList<ConnectionStatus>> = Observer {
        var data = it as ArrayList<ConnectionStatus>
//        var faultyComplex = viewModel.getComplexHealthStatusByFaultyComplex(faultyComplexes)

        Log.i("faulty", "faultyComplex: " + Gson().toJson(data))
        var disconnectedComplex = data.size //getTotalDisconnectedUnit(data)
        Log.i("faulty", "faultyComplexCount: " + disconnectedComplex)
        if (disconnectedComplex > 0) {

            binding.banner.root.visibility = View.GONE
            binding.complexGrid.visibility = View.VISIBLE

            val gridLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

            binding.complexGrid.layoutManager = gridLayoutManager
            adapter = ConnectionAdapter(requireContext(), data, true,this)
            binding.complexGrid.adapter = adapter

            binding.statusIcon.setBackgroundResource(R.drawable.vic_fault)
            binding.label.setTextColor(resources.getColor(R.color.alert, null))
            if (disconnectedComplex == 1)
                binding.label.text = "1 disconnected unit detected"
            else
                binding.label.text = "$disconnectedComplex disconnected Complex detected"
        } else {
            binding.banner.root.visibility = View.VISIBLE
            binding.complexGrid.visibility = View.GONE

            binding.banner.label.text = "All Complexes have all units Connected."
            binding.statusIcon.setBackgroundResource(R.drawable.logo_smile)
            binding.label.setTextColor(resources.getColor(R.color.primary, null))
            binding.label.text = "All units working"
        }
    }

    fun getTotalDisconnectedUnit(data: ArrayList<ConnectionStatus>): Int {
        var count = 0
        for (i in data) {
            count += i.count
        }
        return count
    }

    override fun onSelect(status: ConnectionStatus) {
        //
    }

//    override fun navigateTo(navigationAction: Int) {
//        when(navigationAction) {
//            NAV_COMPLEXLIST_CONNECTION_STATS -> {
//                navigationClient.loadFragment(
//                    ComplexList_ConnectionStats.getInstance(),
//                    binding.toolbar.mainToolbarTitle,
//                    "Reports",
//                    "reports",
//                    false
//                )
//                ComplexList_ConnectionStats.getInstance()
//                    .show(childFragmentManager.beginTransaction(), "UsageDetails")
//            }
//        }
//    }

}