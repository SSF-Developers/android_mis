package sukriti.ngo.mis.ui.reports.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel.dynamo_db.City
import sukriti.ngo.mis.dataModel.dynamo_db.Complex
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.dataModel.dynamo_db.District
import sukriti.ngo.mis.dataModel.dynamo_db.State
import sukriti.ngo.mis.databinding.ReportsSelectionTreeBinding
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import sukriti.ngo.mis.ui.administration.interfaces.TreeInteractionListener
import sukriti.ngo.mis.ui.reports.PDF_ReportGeneration.SelectionStructure.StateStructure
import sukriti.ngo.mis.ui.reports.ReportsViewModel
import sukriti.ngo.mis.ui.reports.adapters.StateAdapterST
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities

class SelectionTree : BottomSheetDialogFragment() {

    private lateinit var viewModel: ReportsViewModel
    private lateinit var binding: ReportsSelectionTreeBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var country: Country
    private lateinit var accessTree: Country
    private lateinit var accessTreeAdapter: StateAdapterST
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private var selectedStates = 0
    private var selectedDistricts = 0
    private var selectedCities = 0
    private var selectedComplexes = 0

    //private lateinit var accessTreeAdapter: CityComplexListAdapter

    companion object {
        private var INSTANCE: SelectionTree? = null

        fun getInstance(): SelectionTree {
            return INSTANCE
                ?: SelectionTree()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ReportsSelectionTreeBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    private fun init() {
        viewModel = ViewModelProviders.of(requireParentFragment()).get(ReportsViewModel::class.java)
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)

        accessTree = Country()
        country = if(!sharedPrefsClient.getSelectionTreeStatus())
            sharedPrefsClient.getAccessTree()
        else
            sharedPrefsClient.getSelectionTree()

        userAlertClient.closeWaitDialog()
        accessTreeAdapter =
            StateAdapterST(
                context,
                country,
                mTreeInteractionListener
            )

        val gridLayoutManager = GridLayoutManager(context, 1)
        binding.recyclerView.layoutManager = gridLayoutManager
        binding.recyclerView.adapter = accessTreeAdapter
        binding.summaryContainer.visibility = View.VISIBLE

        //   aug QA : todo rahul karn
        val accessCount = Utilities.getUserAccessCount(country)
        binding.states.name.text = getString(R.string.states)
        binding.states.value.text = "${accessCount.state}"
        binding.districts.name.text = getString(R.string.districts)
        binding.districts.value.text = "${accessCount.district}"
        binding.cities.name.text = getString(R.string.cities)
        binding.cities.value.text = "${accessCount.city}"
        binding.complexes.name.text = getString(R.string.complexes)
        binding.complexes.value.text = "${accessCount.complex}"
//        binding.states.name.text = "Districts"
//        binding.states.value.text = "" + accessCount.district
//        binding.states.name.text = "Cities"
//        binding.states.value.text = "" + accessCount.city
//        binding.states.name.text = "Complexes"
//        binding.states.value.text = "" + accessCount.complex
        binding.toolbar.title.text = getString(R.string.selection_tree)
        binding.toolbar.subTitle.text = getString(R.string.select_units_to_view_reports)

        binding.toolbar.closeCont.setOnClickListener{
            dismiss()
        }

        binding.submit.setOnClickListener {
            Log.i("_selectionStatus", "SelectionTree Submit()")
//            if (selectedComplexes > 0) {
            if (selectedStates > 0 || selectedDistricts > 0 || selectedCities > 0 || selectedComplexes > 0) {
                sharedPrefsClient.saveSelectionTree(country)
                viewModel.setSelectionTree(country)
                Log.i("selectionTree", "States size ${country.states.size}")
                Log.i("selectionTree", "Country: ${Gson().toJson(country)}")
                dismiss()
            } else {
                Log.i("myAccessTree", "No units selected")
            }
        }
    }

    var mTreeInteractionListener: TreeInteractionListener = TreeInteractionListener { treeNodeType, treeEdge, selection ->
            when (treeNodeType) {
                AdministrationViewModel.TREE_NODE_STATE -> {
                    country.states[treeEdge.stateIndex].recursive = selection
                    stateStruct(selection,country.states[treeEdge.stateIndex],null,null,null)
                    Log.i("myAccessTree", "Tree Node Type TREE_NODE_STATE binding states value ${binding.states.value}")
                    selectedStates = updateSelectedCount(selection, binding.states.value, selectedStates)
                    Log.i("myAccessTree", "Tree Node Type TREE_NODE_STATE binding selectionState  $selectionState")

                }

                AdministrationViewModel.TREE_NODE_DISTRICT -> {
                    country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].recursive =
                        selection
                    stateStruct(selection,country.states[treeEdge.stateIndex],
                        country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex],null,null)

                    Log.i("myAccessTree", "Tree Node Type TREE_NODE_DISTRICT binding district value ${binding.districts.value}" )
                    selectedDistricts = updateSelectedCount(selection, binding.districts.value, selectedDistricts)
                    Log.i("myAccessTree", "Tree Node Type TREE_NODE_DISTRICT selectedDistrict $selectedDistricts" )
                }

                AdministrationViewModel.TREE_NODE_CITY -> {
                    country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].cities[treeEdge.cityIndex].recursive =
                        selection
                    stateStruct(selection,country.states[treeEdge.stateIndex],country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex],
                        country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].cities[treeEdge.cityIndex],null)

                    Log.i("myAccessTree", "Tree Node Type TREE_NODE_CITY binding city value ${binding.cities.value}")
                    selectedCities = updateSelectedCount(selection, binding.cities.value, selectedCities)
                    Log.i("myAccessTree", "Tree Node Type TREE_NODE_CITY selectedComplex $selectedCities" )
                }

                AdministrationViewModel.TREE_NODE_COMPLEX -> {
                    country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].cities[treeEdge.cityIndex].complexes[treeEdge.complexIndex].isSelected =
                        selection
                    stateStruct(selection,country.states[treeEdge.stateIndex],country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex],
                        country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].cities[treeEdge.cityIndex],
                        country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].cities[treeEdge.cityIndex].complexes[treeEdge.complexIndex])

                    Log.i("myAccessTree", "Tree Node Type TREE_NODE_COMPLEX binding complex value ${binding.complexes.value}" )
                    selectedComplexes = updateSelectedCount(selection, binding.complexes.value, selectedComplexes)
                    Log.i("myAccessTree", "Tree Node Type TREE_NODE_COMPLEX selectedComplex $selectedComplexes" )
                }
            }
        }

    private fun updateSelectedCount(selection: Int, view: TextView, value: Int): Int {
        Log.i("myAccessTree", "updateSelectedCount selection $selection")
        Log.i("myAccessTree", "updateSelectedCount value $value")
        var localVal = value
        if (selection == 1)
            localVal++
        else
            localVal--
        view.text = localVal.toString()

        return localVal
    }

    private val selectionState:ArrayList<StateStructure> = ArrayList()
    private fun stateStruct(selection: Int, state:State?, district:District?, city: City?, complex: Complex?){

//        val stateStruct = StateStructure()
//        val districtStruct =DistrictStructure()
//        val citiStruct =CitiesStructure()
//        val complexesStruct = ComplexesStructure()
//
//        var found =false
//        var selected =false
//
//
//        if (state!=null && district == null && city == null && complex == null){
//                    State@ for (states in selectionState) {
//                        if (state.name == states.name) {
//                            if(selection == 1){
//                                found = true
//                                break@State
//                            }
//                            else{
//                                selectionState.remove(states)
//                            }
//                        } else {
//                            found = false
//                        }
//                    }
//                    if (!found) {
//                        if (selection ==1) {
//                            stateStruct.name = state.name
//                            selectionState.add(stateStruct)
//                        }
//                    }
//                }
//        else if (state!=null && district != null && city == null && complex == null){
//                    State@ for (states in selectionState) {
//                        if (state.name == states.name) {
//                            District@ for (dist in states.districts) {
//                                if (district.name == dist.name) {
//                                    if (selection ==1) {
//                                        found = true
//                                        break@District
//                                    }else{
//                                        states.districts.remove(dist)
//                                        if(states.districts.size==0) selectionState.remove(states)
//                                    }
//                                } else {
//                                    found = false
//                                }
//                            }
//                            if (!found) {
//                                if (selection==1) {
//                                    districtStruct.name = district.name
//                                    states.addDistrict(districtStruct)
//                                }
//                            }
//                            found = true
//                            break@State
//                        } else {
//                            found = false
//                        }
//                    }
//                    if (!found) {
//                        if (selection==1) {
//                            stateStruct.name = state.name
//                            districtStruct.name = district.name
//                            stateStruct.addDistrict(districtStruct)
//                            selectionState.add(stateStruct)
//                        }
//                    }
//                }
//        else if (state!=null && district != null && city != null && complex == null){
//                    State@ for (states in selectionState) {
//                        if (state.name == states.name) {
//                            District@ for (dist in states.districts) {
//                                if (district.name == dist.name) {
//                                    Citi@ for (citi in dist.cities) {
//                                        if (city.name == citi.name) {
//                                            if (selection==1) {
//                                                found = true
//                                                break@Citi
//                                            }else{
//                                                dist.cities.remove(citi)
//                                                if (dist.cities.size==0) states.districts.remove(dist)
//                                                if (states.districts.size==0)selectionState.remove(states)
//                                            }
//                                        } else {
//                                            found = false
//                                        }
//                                    }
//                                    if (!found) {
//                                        if(selection==1) {
//                                            citiStruct.name = city.name
//                                            dist.addCiti(citiStruct)
//                                        }
//                                    }
//                                    found = true
//                                    break@District
//                                } else {
//                                    found = false
//                                }
//                            }
//                            if (!found) {
//                                if(selection==1) {
//                                    districtStruct.name = district.name
//                                    citiStruct.name = city.name
//                                    districtStruct.addCiti(citiStruct)
//                                    states.addDistrict(districtStruct)
//                                }
//                            }
//                            found = true
//                            break@State
//                        } else {
//                            found = false
//                        }
//                    }
//                    if (!found) {
//                        if (selection==1) {
//                            stateStruct.name = state.name
//                            districtStruct.name = district.name
//                            citiStruct.name = city.name
//                            districtStruct.addCiti(citiStruct)
//                            stateStruct.addDistrict(districtStruct)
//                            selectionState.add(stateStruct)
//                        }
//                    }
//                }
//        else if (state != null && district != null && city != null && complex != null) {
//                    State@ for (states in selectionState) {
//                        if (state.name == states.name) {
//                            District@ for (dist in states.districts) {
//                                if (district.name == dist.name) {
//                                    Citi@ for (citi in dist.cities) {
//                                        if (city.name == citi.name) {
//                                            Complex@ for (complexes in citi.complexes) {
//                                                if (complex.name ==complexes.name){
//                                                    if (selection ==1) {
//                                                        found = true
//                                                        break@Complex
//                                                    }
//                                                    else{
//                                                        citi.complexes.remove(complexes)
//                                                        if (citi.complexes.size==0)dist.cities.remove(citi)
//                                                        if (dist.cities.size==0)  states.districts.remove(dist)
//                                                        if (states.districts.size==0)  selectionState.remove(states)
//                                                    }
//                                                }else{
//                                                    found=false
//                                                }
//                                            }
//                                            if (!found) {
//                                                if (selection==1) {
//                                                    complexesStruct.name = complex.name
//                                                    citi.addComplex(complexesStruct)
//                                                }
//                                            }
//                                            found = true
//                                            break@Citi
//                                        } else {
//                                            found = false
//                                        }
//                                    }
//                                    if (!found) {
//                                        if (selection==1) {
//                                            citiStruct.name = city.name
//                                            complexesStruct.name = complex.name
//                                            citiStruct.addComplex(complexesStruct)
//                                            dist.addCiti(citiStruct)
//                                        }
//                                    }
//                                    found = true
//                                    break@District
//                                } else {
//                                    found = false
//                                }
//                            }
//                            if (!found) {
//                                if (selection==1) {
//                                    districtStruct.name = district.name
//                                    citiStruct.name = city.name
//                                    complexesStruct.name = complex.name
//                                    citiStruct.addComplex(complexesStruct)
//                                    districtStruct.addCiti(citiStruct)
//                                    states.addDistrict(districtStruct)
//                                }
//                            }
//                            found = true
//                            break@State
//                        } else {
//                            found = false
//                        }
//                    }
//                    if (!found) {
//                        if (selection==1)
//                        stateStruct.name =state.name
//                        districtStruct.name =district.name
//                        citiStruct.name =city.name
//                        complexesStruct.name =complex.name
//                        citiStruct.addComplex(complexesStruct)
//                        districtStruct.addCiti(citiStruct)
//                        stateStruct.addDistrict(districtStruct)
//                        selectionState.add(stateStruct)
//                    }
//                }
//
//       viewModel.selectionState =selectionState
    }
}

