package sukriti.ngo.mis.ui.dashboard.fragments.connection

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.FragmentDetailConnectionDialogBinding
import sukriti.ngo.mis.ui.complexes.adapters.accessTreeComplexSelection.AdapterConnectionDetail
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.ConnectionStatus
import sukriti.ngo.mis.ui.dashboard.data.Table

class DetailConnectionDialog : DialogFragment() {
    private lateinit var viewModel: DashboardViewModel
    private lateinit var binding: FragmentDetailConnectionDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailConnectionDialogBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    companion object {
    }

    fun init() {
        viewModel = ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        viewModel.detailConnection.observe(viewLifecycleOwner, detailConnectionObserver)
    }

    private var detailConnectionObserver: Observer<ConnectionStatus> = Observer {
        binding.title.text = it.complex.name
        setAdapter(it.table as ArrayList<Table>)
    }

    fun setAdapter(data: ArrayList<Table>) {
//        val data = viewModel.toMutableUpiList(upiProfiles)
        if (data?.size == 0) {
//            binding.gridContainer.visibility = View.GONE
            binding.grid.visibility = View.GONE
            binding.noDataContainer.visibility = View.VISIBLE
            binding.noDataLabel.text = "No Upi recorded for selected duration."
        } else {
//            binding.gridContainer.visibility = View.VISIBLE
            binding.grid.visibility = View.VISIBLE
            binding.noDataContainer.visibility = View.GONE

            Log.i("_detailFragment", "requestHandler: " + data?.size)
            setTitles()
            binding.grid.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            var adapter = AdapterConnectionDetail(data)
            binding.grid.adapter = adapter

        }

    }

    fun setTitles() {
        val ids = intArrayOf(
            R.id.title_01,
            R.id.title_02,
            R.id.title_03,
            R.id.title_04,
            R.id.title_05
        )
        val titles = arrayListOf<String>(
            "Cabin Type",
            "Connection Status",
            "Disconnection Reason",
            "Date",
            "Time"
        )
        val fields = arrayOfNulls<TextView>(ids.size)

        for (i in ids.indices) {
            fields[i] = binding.root.findViewById(ids[i])
            fields[i]?.text = titles[i]
        }
    }

}