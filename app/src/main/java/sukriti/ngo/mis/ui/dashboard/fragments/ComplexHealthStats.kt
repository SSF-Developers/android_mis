package sukriti.ngo.mis.ui.super_admin.fragments

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
import kotlinx.android.synthetic.main.login.*
import sukriti.ngo.mis.R
import sukriti.ngo.mis.adapters.FaultyComplexAdapter
import sukriti.ngo.mis.databinding.DashboardComplexHealthStatsBinding
import sukriti.ngo.mis.repository.data.ComplexHealthStats
import sukriti.ngo.mis.repository.utils.HLWHelper.getFaultyComplexCount
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.FaultyComplex
import sukriti.ngo.mis.ui.dashboard.interfaces.ComplexHealthRequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient

class ComplexHealthStats : Fragment() {
    private lateinit var binding: DashboardComplexHealthStatsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var adapter: FaultyComplexAdapter

    companion object {
        private var INSTANCE: sukriti.ngo.mis.ui.super_admin.fragments.ComplexHealthStats? = null

        fun getInstance(): sukriti.ngo.mis.ui.super_admin.fragments.ComplexHealthStats {
            return INSTANCE
                ?: ComplexHealthStats()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardComplexHealthStatsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel = ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)

//        viewModel.listFaultyComplexes(requestHandler)
        viewModel.faultyComplexes.observe(viewLifecycleOwner,faultyComplexObserver)
    }

    private var faultyComplexObserver :Observer<ArrayList<FaultyComplex>> = Observer {
        val faultyComplexes = it as ArrayList<FaultyComplex>
        val faultyComplex = viewModel.getComplexHealthStatusByFaultyComplex(faultyComplexes)
        Log.i("faulty","faultyComplex: " + Gson().toJson(faultyComplex))
        requestHandler.onSuccess(faultyComplex)
    }

    var requestHandler: ComplexHealthRequestHandler = object:
        ComplexHealthRequestHandler {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onSuccess(data: MutableList<ComplexHealthStats>?) {

            val faultyComplexCount = getFaultyComplexCount(data)
            Log.i("faulty", "faultyComplexCount: $faultyComplexCount")
            Log.i("faulty", "faultyComplexCount: $faultyComplexCount")
            if(faultyComplexCount>0){

                binding.banner.root.visibility = View.GONE
                binding.complexGrid.visibility = View.VISIBLE

                val gridLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)

                binding.complexGrid.layoutManager = gridLayoutManager
                adapter = FaultyComplexAdapter(context, data)
                binding.complexGrid.adapter = adapter

                binding.statusIcon.setBackgroundResource(R.drawable.vic_fault)
                binding.label.setTextColor(resources.getColor(R.color.alert,null))
                if(faultyComplexCount == 1)
                    binding.label.text = "1 Faulty unit detected"
                else
                    binding.label.text = "$faultyComplexCount Faulty units detected"
            }else{
                binding.banner.root.visibility = View.VISIBLE
                binding.complexGrid.visibility = View.GONE

                binding.banner.label.text = "All Complexes have all units functional."
                binding.statusIcon.setBackgroundResource(R.drawable.logo_smile)
                binding.label.setTextColor(resources.getColor(R.color.primary,null))
                binding.label.text = "All units working"
            }
        }

        override fun onError(message: String?) {

        }

    }

}