package sukriti.ngo.mis.ui.super_admin.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import sukriti.ngo.mis.R
import sukriti.ngo.mis.adapters.FaultyComplexAdapter
import sukriti.ngo.mis.adapters.LowWaterLevelComplexAdapter
import sukriti.ngo.mis.databinding.DashboardComplexHealthStatsBinding
import sukriti.ngo.mis.databinding.DashboardWaterLevelStatsBinding
import sukriti.ngo.mis.repository.data.ComplexHealthStats
import sukriti.ngo.mis.repository.entity.Health
import sukriti.ngo.mis.repository.utils.HLWHelper
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.LowWaterComplex
import sukriti.ngo.mis.ui.dashboard.interfaces.ComplexHealthRequestHandler
import sukriti.ngo.mis.ui.dashboard.interfaces.ComplexWaterLevelRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient

class WaterLevelStats : Fragment() {
    private lateinit var binding: DashboardWaterLevelStatsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var adapter: LowWaterLevelComplexAdapter

    companion object {
        private var INSTANCE: WaterLevelStats? = null

        fun getInstance(): WaterLevelStats {
            return INSTANCE
                ?: WaterLevelStats()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardWaterLevelStatsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel =
            ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)

//        viewModel.listComplexWaterLevelStatus(requestHandler)
        viewModel.lowWaterComplexes.observe(viewLifecycleOwner,lowWaterObserver)
    }


    var lowWaterObserver :Observer<ArrayList<LowWaterComplex>> = Observer {
        var complexes  = it as ArrayList<LowWaterComplex>
        var lowWaterLevelComplexCount = complexes.size
        if(lowWaterLevelComplexCount>0){
            binding.banner.root.visibility = View.GONE
            binding.complexGrid.visibility = View.VISIBLE

            val gridLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
            binding.complexGrid.layoutManager = gridLayoutManager
            adapter = LowWaterLevelComplexAdapter(context, complexes)
            binding.complexGrid.adapter = adapter

            binding.statusIcon.setBackgroundResource(R.drawable.vic_fault)
            binding.label.setTextColor(resources.getColor(R.color.alert,null))
            if(lowWaterLevelComplexCount == 1)
                binding.label.text = "1 unit with low water level detected"
            else
                binding.label.text = "$lowWaterLevelComplexCount units with low water level detected"
        }else{
            binding.banner.root.visibility = View.VISIBLE
            binding.complexGrid.visibility = View.GONE

            binding.banner.label.text = "All Complexes have sufficient water available."
            binding.statusIcon.setBackgroundResource(R.drawable.logo_smile)
//                binding.label.setTextColor(resources.getColor(R.color.primary,null))
            binding.label.text = "All units working"
        }


    }

//    var requestHandler: ComplexWaterLevelRequestHandler = object:
//        ComplexWaterLevelRequestHandler {
//        @RequiresApi(Build.VERSION_CODES.M)
//        override fun onSuccess(data: MutableList<Health>?) {
//            var lowWaterLevelComplexCount = HLWHelper.getLowWaterLevelComplexCount(data)
//            if(lowWaterLevelComplexCount>0){
//                binding.banner.root.visibility = View.GONE
//                binding.complexGrid.visibility = View.VISIBLE
//
//                val gridLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
//                binding.complexGrid.layoutManager = gridLayoutManager
//                adapter = LowWaterLevelComplexAdapter(context, data)
//                binding.complexGrid.adapter = adapter
//
//                binding.statusIcon.setBackgroundResource(R.drawable.vic_fault)
//                binding.label.setTextColor(resources.getColor(R.color.alert,null))
//                if(lowWaterLevelComplexCount == 1)
//                    binding.label.text = "1 unit with low water level detected"
//                else
//                    binding.label.text = "$lowWaterLevelComplexCount units with low water level detected"
//            }else{
//                binding.banner.root.visibility = View.VISIBLE
//                binding.complexGrid.visibility = View.GONE
//
//                binding.banner.label.text = "All Complexes have sufficient water available."
//                binding.statusIcon.setBackgroundResource(R.drawable.logo_smile)
////                binding.label.setTextColor(resources.getColor(R.color.primary,null))
//                binding.label.text = "All units working"
//            }
//
//        }
//
//        override fun onError(message: String?) {
//
//        }
//
//    }

}