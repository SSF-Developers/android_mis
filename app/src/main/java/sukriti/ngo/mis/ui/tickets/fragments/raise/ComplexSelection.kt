package sukriti.ngo.mis.ui.tickets.fragments.raise

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import sukriti.ngo.mis.R
import sukriti.ngo.mis.adapters.accessTree.singleSelection.StateAdapterSS
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.databinding.ReportsSelectionTreeBinding
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import sukriti.ngo.mis.ui.administration.interfaces.TreeInteractionListener
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.ui.complexes.interfaces.ComplexDetailsRequestHandler
import sukriti.ngo.mis.ui.tickets.TicketsViewModel
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities

class ComplexSelection : BottomSheetDialogFragment() {

    private lateinit var viewModel: TicketsViewModel
    private lateinit var binding: ReportsSelectionTreeBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var country: Country
    private lateinit var accessTree: Country
    private lateinit var accessTreeAdapter: StateAdapterSS
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private var selectedStates = 0
    private var selectedDistricts = 0
    private var selectedCities = 0
    private var selectedComplexes = 0
    //private lateinit var accessTreeAdapter: CityComplexListAdapter

    companion object {
        private var INSTANCE: ComplexSelection? = null

        fun getInstance(): ComplexSelection {
            return INSTANCE
                ?: ComplexSelection()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ReportsSelectionTreeBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        Log.i("__setComplexRaiseTicket", "B: " + requireActivity().javaClass.name)

        viewModel = ViewModelProviders.of(requireActivity()).get(TicketsViewModel::class.java)
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)

        accessTree = Country()
        country = if (!sharedPrefsClient.getSelectionTreeStatus())
            sharedPrefsClient.getAccessTree()
        else
            sharedPrefsClient.getSelectionTree()

        userAlertClient.closeWaitDialog()
        accessTreeAdapter =
            StateAdapterSS(
                context,
                country,
                mTreeInteractionListener
            )

        val gridLayoutManager = GridLayoutManager(context, 1)
        binding.recyclerView.layoutManager = gridLayoutManager
        binding.recyclerView.adapter = accessTreeAdapter
        binding.summaryContainer.visibility = View.VISIBLE

        val accessCount = Utilities.getUserAccessCount(country)
        binding.states.name.text = getString(R.string.states)
        binding.states.value.text = "${accessCount.state}"
        binding.states.name.text = getString(R.string.districts)
        binding.states.value.text = "${accessCount.district}"
        binding.states.name.text = getString(R.string.cities)
        binding.states.value.text = "${accessCount.city}"
        binding.states.name.text = getString(R.string.complexes)
        binding.states.value.text = "${accessCount.complex}"
        binding.toolbar.title.text = getString(R.string.selection_tree)
        binding.toolbar.subTitle.text = getString(R.string.select_units_to_view_reports)

        binding.toolbar.closeCont.setOnClickListener(View.OnClickListener {
            dismiss()
        })

        binding.submit.setOnClickListener(View.OnClickListener {
            Log.i("_selectionStatus", "SelectionTree Submit()")
            sharedPrefsClient.saveSelectionTree(country)
            dismiss()
        })
    }


    var mTreeInteractionListener: TreeInteractionListener =
        TreeInteractionListener { treeNodeType, treeEdge, selection ->
            when (treeNodeType) {
                AdministrationViewModel.TREE_NODE_STATE -> {
                    country.states[treeEdge.stateIndex].recursive = selection
                    selectedStates =
                        updateSelectedCount(selection, binding.states.value, selectedStates)
                }

                AdministrationViewModel.TREE_NODE_DISTRICT -> {
                    country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].recursive =
                        selection
                    selectedDistricts =
                        updateSelectedCount(selection, binding.districts.value, selectedDistricts)
                }

                AdministrationViewModel.TREE_NODE_CITY -> {
                    country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].cities[treeEdge.cityIndex].recursive =
                        selection
                    selectedCities =
                        updateSelectedCount(selection, binding.cities.value, selectedCities)
                }

                AdministrationViewModel.TREE_NODE_COMPLEX -> {

                    val complexName = country.states[treeEdge.stateIndex]
                        .districts[treeEdge.districtIndex].cities[treeEdge.cityIndex]
                        .complexes[treeEdge.complexIndex].name
                    userAlertClient.showWaitDialog("Loading complex details...")
                    viewModel.getComplexDetails(complexName, complexDetailsRequestHandler)

                    country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].cities[treeEdge.cityIndex].complexes[treeEdge.complexIndex].isSelected =
                        selection
                    selectedComplexes =
                        updateSelectedCount(selection, binding.complexes.value, selectedComplexes)
                }
            }
        }

    private fun updateSelectedCount(selection: Int, view: TextView, value: Int): Int {
        var localVal = value
        if (selection == 1)
            localVal++
        else
            localVal--
        view.text = "$localVal"

        return localVal
    }

    private var complexDetailsRequestHandler: ComplexDetailsRequestHandler = object :
        ComplexDetailsRequestHandler {
        override fun onSuccess(complex: ComplexDetailsData?) {
            userAlertClient.closeWaitDialog()
            Log.i("__setComplexRaiseTicket", "00")
            if (complex != null) {
                viewModel.setComplexRaiseTicket(complex)
                dismiss()
            }
        }


        override fun onError(message: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert", message, false)
        }

    }
}
