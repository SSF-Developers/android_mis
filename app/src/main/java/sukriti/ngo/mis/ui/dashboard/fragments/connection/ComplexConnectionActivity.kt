package sukriti.ngo.mis.ui.dashboard.fragments.connection

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.adapters.ConnectionAdapter
import sukriti.ngo.mis.databinding.ActivityComplexConnectionBinding
import sukriti.ngo.mis.databinding.HomeActivityBinding
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.ConnectionStatus
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient

class ComplexConnectionActivity : AppCompatActivity() {

    private lateinit var binding :ActivityComplexConnectionBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var adapter: ConnectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_complex_connection)
        binding = ActivityComplexConnectionBinding.inflate(layoutInflater)
        init()
    }


    fun init() {
        viewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(this)
//        navigationClient = NavigationClient(childFragmentManager)
//        viewModel.connectionStatus.observe(this, dis_ComplexObserver)
    }


//    @RequiresApi(Build.VERSION_CODES.M)
//    var dis_ComplexObserver: Observer<ArrayList<ConnectionStatus>> = Observer {
//        var data = it as ArrayList<ConnectionStatus>
////        var faultyComplex = viewModel.getComplexHealthStatusByFaultyComplex(faultyComplexes)
//
//        Log.i("faulty", "faultyComplex: " + Gson().toJson(data))
//        var disconnectedComplex = data.size //getTotalDisconnectedUnit(data)
//        Log.i("faulty", "faultyComplexCount: " + disconnectedComplex)
//        if (disconnectedComplex > 0) {
//            binding.banner.root.visibility = View.GONE
//            binding.complexGrid.visibility = View.VISIBLE
//
////        val gridLayoutManager = LinearLayoutManager(context, LinearLayoutManager, false)
//            val gridLayoutManager = GridLayoutManager(this, 2)
//            binding.complexGrid.layoutManager = gridLayoutManager
//            adapter = ConnectionAdapter(this, data, false)
//            binding.complexGrid.adapter = adapter
//
//            binding.statusIcon.setBackgroundResource(R.drawable.vic_fault)
//            binding.label.setTextColor(resources.getColor(R.color.alert, null))
//            if (disconnectedComplex == 1)
//                binding.label.text = "1 disconnected unit detected"
//            else
//                binding.label.text = "$disconnectedComplex disconnected Complex detected"
//        } else {
//            binding.banner.root.visibility = View.VISIBLE
//            binding.complexGrid.visibility = View.GONE
//
//            binding.banner.label.text = "All Complexes have all units Connected."
//            binding.statusIcon.setBackgroundResource(R.drawable.logo_smile)
//            binding.label.setTextColor(resources.getColor(R.color.primary, null))
//            binding.label.text = "All units working"
//        }
//    }

}