package sukriti.ngo.mis.ui.administration.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.item_ticket_files.remove
import kotlinx.android.synthetic.main.ticket_raise_ticket.unit
import sukriti.ngo.mis.dataModel.dynamo_db.City
import sukriti.ngo.mis.dataModel.dynamo_db.Complex
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.dataModel.dynamo_db.District
import sukriti.ngo.mis.dataModel.dynamo_db.PermissionTree
import sukriti.ngo.mis.dataModel.dynamo_db.State
import sukriti.ngo.mis.databinding.DefineAccessBinding
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import sukriti.ngo.mis.ui.administration.adapter.define_access.StateAdapter
import sukriti.ngo.mis.ui.administration.interfaces.ProvisioningTreeRequestHandler
import sukriti.ngo.mis.ui.administration.interfaces.RequestHandler
import sukriti.ngo.mis.ui.administration.interfaces.TreeInteractionListener
import sukriti.ngo.mis.ui.administration.lambda.DefineAccessLambdaRequest
import sukriti.ngo.mis.ui.complexes.data.lambdaData.AccessTree
import sukriti.ngo.mis.ui.login.data.UserProfile.Companion.getRole
import sukriti.ngo.mis.ui.login.data.UserProfile.Companion.getRoleName
import sukriti.ngo.mis.ui.login.data.UserProfile.Companion.isClientSpecificRole
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities
import sukriti.ngo.mis.utils.Utilities.getIotPolicyDocument
import sukriti.ngo.mis.utils.Utilities.getIotPolicyName

class DefineAccess : Fragment() {
    private lateinit var viewModel: AdministrationViewModel
    private lateinit var binding: DefineAccessBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var country: Country
    private lateinit var finalTree: Country
    private lateinit var accessTreeAdapter: StateAdapter
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var accessTree: Country
    private var selectedStates = 0
    private var selectedDistricts = 0
    private var selectedCities = 0
    private var selectedComplexes = 0
    //private lateinit var accessTreeAdapter: CityComplexListAdapter

    companion object {
        private var INSTANCE: DefineAccess? = null

        fun getInstance(): DefineAccess {
            return INSTANCE
                ?: DefineAccess()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DefineAccessBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    private fun init() {
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel =
            ViewModelProviders.of(requireActivity()).get(AdministrationViewModel::class.java)

        finalTree = Country()
        binding.fab.setOnClickListener {

            val policyDocument: String
            val userRole = getRole(viewModel.getSelectedUser().cognitoUser.role)
            policyDocument = getIotPolicyDocument(country, userRole).toString()
            val policyName = getIotPolicyName(viewModel.getSelectedUser())
            /*
                        val request = DefineAccessLambdaRequest(
                            viewModel.getSelectedUser().cognitoUser.userName,
                            getRoleName(userRole),
                            Utilities.getUserAccessKeys(country),
                            policyName,
                            policyDocument)


             */


            Log.i("finalTree", "Final Tree $finalTree")
            userAlertClient.showWaitDialog("Updating user access...")

            val request = DefineAccessLambdaRequest(
                viewModel.getSelectedUser().cognitoUser.userName,
                getRoleName(userRole),
                Utilities.getUserAccessKeys(finalTree),
                convertDBTreeToLambdaTree(finalTree)
            )

            viewModel.updateUserAccessTree(
                request, viewModel.getSelectedUser(),
                finalTree, requestHandler
            )
        }

        binding.summaryContainer.visibility = View.GONE
        binding.fab.visibility = View.GONE
//        userAlertClient.showWaitDialog("Creating Provisioning Tree...")

        if (isClientSpecificRole(sharedPrefsClient.getUserDetails().role)) {
            /*            viewModel.getCompleteUserAccessTree(
                            sharedPrefsClient.getUserDetails().organisation.client,
                            sharedPrefsClient.getUserDetails().user.userName,
                            provisioningTreeRequestHandler
                        )*/

            userAlertClient.showWaitDialog("Loading Access Tree...")
            viewModel.accessTreeClient.loadAccessTreeForUser(
                sharedPrefsClient.getUserDetails().user.userName,
                provisioningTreeRequestHandler
            )
        } else {
            /*            viewModel.getCompleteUserAccessTree(
                            sharedPrefsClient.getUserDetails().user.userName,
                            provisioningTreeRequestHandler
                        )*/

            userAlertClient.showWaitDialog("Loading Access Tree...")
            viewModel.accessTreeClient.loadAccessTreeForUser(
                sharedPrefsClient.getUserDetails().user.userName,
                provisioningTreeRequestHandler
            )
        }
    }

    private var provisioningTreeRequestHandler: ProvisioningTreeRequestHandler =
        object : ProvisioningTreeRequestHandler {
            override fun onSuccess(mCountry: Country?) {
                userAlertClient.closeWaitDialog()

                if (mCountry != null) {
                    accessTree = Country()
                    country = mCountry
                    userAlertClient.closeWaitDialog()
                    accessTreeAdapter =
                        StateAdapter(
                            context,
                            country,
                            mTreeInteractionListener,
                            viewModel
                        )

                    val gridLayoutManager = GridLayoutManager(context, 1)
                    binding.recyclerView.layoutManager = gridLayoutManager
                    binding.recyclerView.adapter = accessTreeAdapter
                    binding.summaryContainer.visibility = View.VISIBLE
                    binding.fab.visibility = View.VISIBLE

                    val accessCount = Utilities.getUserAccessCount(country)
                    binding.countStates.text = "" + accessCount.state
                    binding.countDistricts.text = "" + accessCount.district
                    binding.countCities.text = "" + accessCount.city
                    binding.countComplexes.text = "" + accessCount.complex
                }
            }

            override fun onError(message: String?) {
                userAlertClient.closeWaitDialog()
                userAlertClient.showDialogMessage("Error Alert", message, true);
            }
        }


    var requestHandler: RequestHandler = object :
        RequestHandler {

        override fun onSuccess() {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage(
                "Access Updated",
                "User access updated successfully",
                true
            )
        }

        override fun onError(message: String?) {
            Log.i("updateAccessTree", message)
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!", message, false)
        }
    }

    var mTreeInteractionListener: TreeInteractionListener =
        TreeInteractionListener { treeNodeType, treeEdge, selection ->
            when (treeNodeType) {
                AdministrationViewModel.TREE_NODE_STATE -> {
                    country.states[treeEdge.stateIndex].recursive = selection
                    selectedStates =
                        updateSelectedCount(selection, binding.countStates, selectedStates)
                    Log.i("AccessDefine", "State")
                    Log.i("AccessDefine", "Selection %$selection")
                    val json = JsonObject()
                    json.addProperty("StateIndex", treeEdge.stateIndex)
                    Log.i("AccessDefine", json.toString())

                    updateState(treeEdge.stateIndex, selection)
                }

                AdministrationViewModel.TREE_NODE_DISTRICT -> {
                    country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].recursive =
                        selection
                    selectedDistricts =
                        updateSelectedCount(selection, binding.countDistricts, selectedDistricts)

                    Log.i("AccessDefine", "District")
                    Log.i("AccessDefine", "Selection %$selection")
                    val json = JsonObject()
                    json.addProperty("StateIndex", treeEdge.stateIndex)
                    json.addProperty("District", treeEdge.districtIndex)
                    Log.i("AccessDefine", json.toString())

                    updateDistrict(treeEdge.stateIndex, treeEdge.districtIndex, selection)
                }

                AdministrationViewModel.TREE_NODE_CITY -> {
                    country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].cities[treeEdge.cityIndex].recursive =
                        selection
                    selectedCities =
                        updateSelectedCount(selection, binding.countCities, selectedCities)
                    Log.i("AccessDefine", "City")
                    Log.i("AccessDefine", "Selection %$selection")
                    val json = JsonObject()
                    json.addProperty("StateIndex", treeEdge.stateIndex)
                    json.addProperty("District", treeEdge.districtIndex)
                    json.addProperty("CityIndex", treeEdge.cityIndex)
                    Log.i("AccessDefine", json.toString())
                    updateCity(
                        treeEdge.stateIndex,
                        treeEdge.districtIndex,
                        treeEdge.cityIndex,
                        selection
                    )
                }

                AdministrationViewModel.TREE_NODE_COMPLEX -> {
                    country.states[treeEdge.stateIndex].districts[treeEdge.districtIndex].cities[treeEdge.cityIndex].complexes[treeEdge.complexIndex].isSelected =
                        selection
                    selectedComplexes =
                        updateSelectedCount(selection, binding.countComplexes, selectedComplexes)
                    Log.i("AccessDefine", "Complex")
                    Log.i("AccessDefine", "Selection %$selection")
                    val json = JsonObject()
                    json.addProperty("StateIndex", treeEdge.stateIndex)
                    json.addProperty("District", treeEdge.districtIndex)
                    json.addProperty("CityIndex", treeEdge.cityIndex)
                    json.addProperty("ComplexIndex", treeEdge.complexIndex)
                    Log.i("AccessDefine", json.toString())

                    updateComplex(
                        treeEdge.stateIndex,
                        treeEdge.districtIndex,
                        treeEdge.cityIndex,
                        treeEdge.complexIndex,
                        selection
                    )
                }
            }
        }

    private fun updateSelectedCount(selection: Int, view: TextView, value: Int): Int {
        var localVal = value
        if (selection == 1)
            localVal++;
        else
            localVal--;
        view.text = "" + localVal

        return localVal
    }

    private fun updateComplex(
        stateIndex: Int,
        districtIndex: Int,
        cityIndex: Int,
        complexIndex: Int,
        selection: Int
    ) {
        val state = country.states[stateIndex]
        val district = country.states[stateIndex].districts[districtIndex]
        val city = country.states[stateIndex].districts[districtIndex].cities[cityIndex]
        val complex =
            country.states[stateIndex].districts[districtIndex].cities[cityIndex].complexes[complexIndex]

        if (selection == 1) {
            var newState = containsState(state.name, state.code)
            if (newState == null)
                newState = State(state.name, state.code, 0, ArrayList<District>())

            // check if state is present
            if (!finalTree.states.contains(newState)) {
                // State is not present Add the State
                finalTree.states.add(newState)
                Log.i("finalTree", "State Added")
            } else {
                Log.i("finalTree", "State already present")
            }

            // check if district is present
            val stateIdx = finalTree.states.indexOf(newState)

            var newDistrict = containsDistrict(stateIdx, district.name, district.code)
            if (newDistrict == null)
                newDistrict = District(district.name, district.code, 0, ArrayList<City>())

            Log.i("finalTree", "State ${state.name} is present at index $stateIdx")
            if (!finalTree.states[stateIdx].districts.contains(newDistrict)) {
                finalTree.states[stateIdx].districts.add(newDistrict)
                Log.i("finalTree", "District Added")
            } else {
                Log.i("finalTree", "District already present")
            }

            // check if city is present
            val districtIdx = finalTree.states[stateIdx].districts.indexOf(newDistrict)

            var newCity = containsCity(stateIdx, districtIdx, city.name, city.code)
            if (newCity == null)
                newCity = City(city.name, city.code, 0, ArrayList<Complex>())

            Log.i("finalTree", "District ${district.name} is present at $districtIdx")
            if (!finalTree.states[stateIdx].districts[districtIdx].cities.contains(newCity)) {
                finalTree.states[stateIdx].districts[districtIdx].cities.add(newCity)
                Log.i("finalTree", "City Added")
            } else {
                Log.i("finalTree", "City already present")
            }


            val cityIdx = finalTree.states[stateIdx].districts[districtIdx].cities.indexOf(newCity)
            Log.i("finalTree", "City ${city.name} is present at $cityIdx")

            // add complex
            finalTree.states[stateIdx].districts[districtIdx].cities[cityIdx].complexes.add(complex)
            val complexIdx =
                finalTree.states[stateIdx].districts[districtIdx].cities[cityIdx].complexes.indexOf(
                    complex
                )
            finalTree.states[stateIdx].districts[districtIdx].cities[cityIdx].complexes[complexIdx].isSelected =
                1
            Log.i("finalTree", "Complex Added")
            Log.i("finalTree", "Complex ${complex.name} is present at $complexIdx")

        } else {
            var stateIdx = -1

            for (i in 0 until finalTree.states.size) {
                if (finalTree.states[i].name.equals(state.name)) {
                    stateIdx = i
                    break
                }
            }

            var districtIdx = -1

            for (i in 0 until finalTree.states[stateIdx].districts.size) {
                if (finalTree.states[stateIdx].districts[i].name.equals(district.name)) {
                    districtIdx = i
                    break
                }
            }

            var cityIdx = -1

            for (i in 0 until finalTree.states[stateIdx].districts[districtIdx].cities.size) {
                if (finalTree.states[stateIdx].districts[districtIdx].cities[i].name.equals(city.name)) {
                    cityIdx = i
                    break
                }
            }

            var complexIdx = -1

            for (i in 0 until finalTree.states[stateIdx].districts[districtIdx].cities[cityIdx].complexes.size) {
                if (finalTree.states[stateIdx].districts[districtIdx].cities[cityIdx].complexes[i].name.equals(
                        complex.name
                    )
                ) {
                    complexIdx = i
                    break
                }
            }

            // Remove Complex
            Log.i("finalTree", "Complex to be removed ${complex.name} is present at $complexIdx")
            var complexListSize =
                finalTree.states[stateIdx].districts[districtIdx].cities[cityIndex].complexes.size
            Log.i("finalTree", "Complex list size before deleting $complexListSize")
            finalTree.states[stateIdx].districts[districtIdx].cities[cityIndex].complexes[complexIdx].isSelected =
                0
            finalTree.states[stateIdx].districts[districtIdx].cities[cityIndex].complexes.removeAt(
                complexIdx
            )
            Log.i("finalTree", "Complex Removed ")
            complexListSize =
                finalTree.states[stateIdx].districts[districtIdx].cities[cityIndex].complexes.size
            Log.i("finalTree", "Complex list size after deleting $complexListSize")


            // check if list of complexes in the current city is empty? if yes then remove the city
            var cityListSize = finalTree.states[stateIdx].districts[districtIdx].cities.size
            if (complexListSize == 0) {
                // Remove City
                Log.i("finalTree", "City to be removed ${city.name} is present at $cityIdx")
                Log.i("finalTree", "Number of cities before deleting $cityListSize")
                finalTree.states[stateIdx].districts[districtIdx].cities[cityIdx].recursive = 0
                finalTree.states[stateIdx].districts[districtIdx].cities.removeAt(cityIdx)
                Log.i("finalTree", "City removed")
                cityListSize = finalTree.states[stateIdx].districts[districtIdx].cities.size
                Log.i("finalTree", "Number of cities after deleting $cityListSize")
            }

            // check if number of cities in the current district is empty? if yes then remove the district
            var districtListSize = finalTree.states[stateIdx].districts.size
            if (cityListSize == 0) {
                // Remove District
                Log.i(
                    "finalTree",
                    "District to be removed ${district.name} is present at $districtIdx"
                )
                Log.i("finalTree", "Number of districts before deleting $districtListSize")
                finalTree.states[stateIdx].districts[districtIdx].recursive = 0
                finalTree.states[stateIdx].districts.removeAt(districtIdx)
                Log.i("finalTree", "District removed")
                districtListSize = finalTree.states[stateIdx].districts.size
                Log.i("finalTree", "Number of districts after deleting $districtListSize")
            }

            // check if the number of districts in the current state is empty? if yes then remove the state
            var stateListSize = finalTree.states.size
            if (districtListSize == 0) {
                // Remove State
                Log.i("finalTree", "State to be removed ${state.name} is present at $stateIdx")
                Log.i("finalTree", "Number of cities before deleting $stateListSize")
                finalTree.states[stateIdx].recursive = 0
                finalTree.states.removeAt(stateIdx)
                Log.i("finalTree", "State removed")
                stateListSize = finalTree.states.size
                Log.i("finalTree", "Number of cities after deleting $stateListSize")
            }
        }
    }

    private fun updateCity(stateIndex: Int, districtIndex: Int, cityIndex: Int, selection: Int) {
        val state = country.states[stateIndex]
        val district = country.states[stateIndex].districts[districtIndex]
        val city = country.states[stateIndex].districts[districtIndex].cities[cityIndex]

        if (selection == 1) {
            var newState = containsState(state.name, state.code)
            if (newState == null)
                newState = State(state.name, state.code, 0, ArrayList<District>())

            // check if state is present
            if (!finalTree.states.contains(newState)) {
                // State is not present Add the State
                finalTree.states.add(newState)
                Log.i("finalTree", "State Added")
            } else {
                Log.i("finalTree", "State already present")
            }

            // check if district is present
            val stateIdx = finalTree.states.indexOf(newState)

            var newDistrict = containsDistrict(stateIdx, district.name, district.code)
            if (newDistrict == null)
                newDistrict = District(district.name, district.code, 0, ArrayList<City>())

            Log.i("finalTree", "State ${state.name} is present at index $stateIdx")
            if (!finalTree.states[stateIdx].districts.contains(newDistrict)) {
                finalTree.states[stateIdx].districts.add(newDistrict)
                Log.i("finalTree", "District Added")
            } else {
                Log.i("finalTree", "District already present")
            }

            // check if city is present
            val districtIdx = finalTree.states[stateIdx].districts.indexOf(newDistrict)

            var newCity = containsCity(stateIdx, districtIdx, city.name, city.code)
            if (newCity == null)
                newCity = City(city.name, city.code, 0, ArrayList<Complex>())

            Log.i("finalTree", "District ${district.name} is present at $districtIdx")
            if (!finalTree.states[stateIdx].districts[districtIdx].cities.contains(newCity)) {
                finalTree.states[stateIdx].districts[districtIdx].cities.add(newCity)
                val cityIdx =
                    finalTree.states[stateIdx].districts[districtIdx].cities.indexOf(newCity)
                finalTree.states[stateIdx].districts[districtIdx].cities[cityIdx].recursive = 1
                Log.i("finalTree", "City Added")
            } else {
                Log.i("finalTree", "City already present")
                val cityIdx =
                    finalTree.states[stateIdx].districts[districtIdx].cities.indexOf(newCity)
                finalTree.states[stateIdx].districts[districtIdx].cities[cityIndex].complexes.removeAll(
                    finalTree.states[stateIdx].districts[districtIdx].cities[cityIndex].complexes
                )
                Log.i("finalTree", "All complexes removed")
                finalTree.states[stateIdx].districts[districtIdx].cities[cityIdx].recursive = 1
            }


        } else {
            var stateIdx = -1

            for (i in 0 until finalTree.states.size) {
                if (finalTree.states[i].name.equals(state.name)) {
                    stateIdx = i
                    break
                }
            }

            var districtIdx = -1

            for (i in 0 until finalTree.states[stateIdx].districts.size) {
                if (finalTree.states[stateIdx].districts[i].name.equals(district.name)) {
                    districtIdx = i
                    break
                }
            }

            var cityIdx = -1

            for (i in 0 until finalTree.states[stateIdx].districts[districtIdx].cities.size) {
                if (finalTree.states[stateIdx].districts[districtIdx].cities[i].name.equals(city.name)) {
                    cityIdx = i
                    break
                }
            }

            // remove city
            var cityListSize = finalTree.states[stateIdx].districts[districtIdx].cities.size
            Log.i("finalTree", "City to be removed ${city.name} is present at $cityIdx")
            Log.i("finalTree", "Number of cities before deleting $cityListSize")
            finalTree.states[stateIdx].districts[districtIdx].cities[cityIdx].recursive = 0
            finalTree.states[stateIdx].districts[districtIdx].cities.removeAt(cityIdx)
            Log.i("finalTree", "City removed")
            cityListSize = finalTree.states[stateIdx].districts[districtIdx].cities.size
            Log.i("finalTree", "Number of cities after deleting $cityListSize")

            // check if number of cities in the current district is empty? if yes then remove the district
            var districtListSize = finalTree.states[stateIdx].districts.size
            if (cityListSize == 0) {
                // Remove District
                Log.i(
                    "finalTree",
                    "District to be removed ${district.name} is present at $districtIdx"
                )
                Log.i("finalTree", "Number of districts before deleting $districtListSize")
                finalTree.states[stateIdx].districts[districtIdx].recursive = 0
                finalTree.states[stateIdx].districts.removeAt(districtIdx)
                Log.i("finalTree", "District removed")
                districtListSize = finalTree.states[stateIdx].districts.size
                Log.i("finalTree", "Number of districts after deleting $districtListSize")
            }

            // check if the number of districts in the current state is empty? if yes then remove the state
            var stateListSize = finalTree.states.size
            if (districtListSize == 0) {
                // Remove State
                Log.i("finalTree", "State to be removed ${state.name} is present at $stateIdx")
                Log.i("finalTree", "Number of cities before deleting $stateListSize")
                finalTree.states[stateIdx].recursive = 0
                finalTree.states.removeAt(stateIdx)
                Log.i("finalTree", "State removed")
                stateListSize = finalTree.states.size
                Log.i("finalTree", "Number of cities after deleting $stateListSize")
            }

        }
    }

    private fun updateDistrict(stateIndex: Int, districtIndex: Int, selection: Int) {
        val state = country.states[stateIndex]
        val district = country.states[stateIndex].districts[districtIndex]

        if (selection == 1) {
            var newState = containsState(state.name, state.code)
            if (newState == null)
                newState = State(state.name, state.code, 0, ArrayList<District>())

            // check if state is present
            if (!finalTree.states.contains(newState)) {
                // State is not present Add the State
                finalTree.states.add(newState)
                Log.i("finalTree", "State Added")
            } else {
                Log.i("finalTree", "State already present")
            }

            // check if district is present
            val stateIdx = finalTree.states.indexOf(newState)

            var newDistrict = containsDistrict(stateIdx, district.name, district.code)
            if (newDistrict == null)
                newDistrict = District(district.name, district.code, 0, ArrayList<City>())

            Log.i("finalTree", "State ${state.name} is present at index $stateIdx")
            if (!finalTree.states[stateIdx].districts.contains(newDistrict)) {
                finalTree.states[stateIdx].districts.add(newDistrict)
                val districtIdx = finalTree.states[stateIdx].districts.indexOf(newDistrict)
                finalTree.states[stateIdx].districts[districtIdx].recursive = 1
                Log.i("finalTree", "District Added")
            } else {
                Log.i("finalTree", "District already present")
                val districtIdx = finalTree.states[stateIdx].districts.indexOf(newDistrict)
                finalTree.states[stateIdx].districts[districtIdx].cities.removeAll(
                    finalTree.states[stateIdx].districts[districtIdx].cities
                )
                finalTree.states[stateIdx].districts[districtIdx].recursive = 1
            }
        } else {
            var stateIdx = -1

            for (i in 0 until finalTree.states.size) {
                if (finalTree.states[i].name.equals(state.name)) {
                    stateIdx = i
                    break
                }
            }

            var districtIdx = -1

            for (i in 0 until finalTree.states[stateIdx].districts.size) {
                if (finalTree.states[stateIdx].districts[i].name.equals(district.name)) {
                    districtIdx = i
                    break
                }
            }

            var districtListSize = finalTree.states[stateIdx].districts.size
            // Remove District
            Log.i("finalTree", "District to be removed ${district.name} is present at $districtIdx")
            Log.i("finalTree", "Number of districts before deleting $districtListSize")
            finalTree.states[stateIdx].districts[districtIdx].recursive = 0
            finalTree.states[stateIdx].districts.removeAt(districtIdx)
            Log.i("finalTree", "District removed")
            districtListSize = finalTree.states[stateIdx].districts.size
            Log.i("finalTree", "Number of districts after deleting $districtListSize")

            // check if the number of districts in the current state is empty? if yes then remove the state
            var stateListSize = finalTree.states.size
            if (districtListSize == 0) {
                // Remove State
                Log.i("finalTree", "State to be removed ${state.name} is present at $stateIdx")
                Log.i("finalTree", "Number of states before deleting $stateListSize")
                finalTree.states[stateIdx].recursive = 0
                finalTree.states.removeAt(stateIdx)
                Log.i("finalTree", "State removed")
                stateListSize = finalTree.states.size
                Log.i("finalTree", "Number of states after deleting $stateListSize")
            }
        }
    }

    private fun updateState(stateIndex: Int, selection: Int) {
        val state = country.states[stateIndex]

        if (selection == 1) {
            var newState = containsState(state.name, state.code)
            if (newState == null)
                newState = State(state.name, state.code, 0, ArrayList<District>())

            // check if state is present
            if (!finalTree.states.contains(newState)) {
                // State is not present Add the State
                finalTree.states.add(newState)
                val stateIdx = finalTree.states.indexOf(newState)
                finalTree.states[stateIdx].recursive = 1
                Log.i("finalTree", "State Added")
            } else {
                Log.i("finalTree", "State already present")
                val stateIdx = finalTree.states.indexOf(newState)
                finalTree.states[stateIdx].districts.removeAll(
                    finalTree.states[stateIdx].districts
                )
                finalTree.states[stateIdx].recursive = 1
            }
        } else {
            var stateIdx = -1

            for (i in 0 until finalTree.states.size) {
                if (finalTree.states[i].name.equals(state.name)) {
                    stateIdx = i
                    break
                }
            }
            var stateListSize = finalTree.states.size

            // Remove State
            Log.i("finalTree", "State to be removed ${state.name} is present at $stateIdx")
            Log.i("finalTree", "Number of cities before deleting $stateListSize")
            finalTree.states[stateIdx].recursive = 0
            finalTree.states.removeAt(stateIdx)
            Log.i("finalTree", "State removed")
            stateListSize = finalTree.states.size
            Log.i("finalTree", "Number of cities after deleting $stateListSize")
        }
    }

    private fun containsState(name: String, code: String): State? {
        Log.d("finalTree", "containsState() called with: name = $name, code = $code")
        var state: State? = null
        for (i in 0 until finalTree.states.size) {
            if (finalTree.states[i].name.equals(name) && finalTree.states[i].code.equals(code)) {
                state = finalTree.states[i]
                break;
            }
        }
        return state
    }

    private fun containsDistrict(stateIndex: Int, name: String, code: String): District? {
        Log.d(
            "finalTree",
            "containsDistrict() called with: stateIndex = $stateIndex, name = $name, code = $code"
        )
        var district: District? = null
        for (i in 0 until finalTree.states[stateIndex].districts.size) {
            if (finalTree.states[stateIndex].districts[i].name.equals(name) && finalTree.states[stateIndex].districts[i].code.equals(
                    code
                )
            ) {
                district = finalTree.states[stateIndex].districts[i]
            }
        }

        return district
    }


    private fun containsCity(
        stateIndex: Int,
        districtIndex: Int,
        name: String,
        code: String
    ): City? {
        Log.d(
            "finalTree",
            "containsCity() called with: stateIndex = $stateIndex, districtIndex = $districtIndex, name = $name, code = $code"
        )
        var city: City? = null
        for (i in 0 until finalTree.states[stateIndex].districts[districtIndex].cities.size) {
            if (finalTree.states[stateIndex].districts[districtIndex].cities[i].name.equals(name) &&
                finalTree.states[stateIndex].districts[districtIndex].cities[i].code.equals(code)
            ) {
                city = finalTree.states[stateIndex].districts[districtIndex].cities[i]
            }
        }

        return city
    }

    private fun containsComplex(
        stateIndex: Int,
        districtIndex: Int,
        cityIndex: Int,
        name: String,
        uuid: String
    ): Complex? {
        Log.d(
            "finalTree",
            "containsComplex() called with: stateIndex = $stateIndex, districtIndex = $districtIndex, cityIndex = $cityIndex, name = $name, uuid = $uuid"
        )
        var complex: Complex? = null
        for (i in 0 until finalTree.states[stateIndex].districts[districtIndex].cities[cityIndex].complexes.size) {
            if (finalTree.states[stateIndex].districts[districtIndex].cities[cityIndex].complexes[i].name.equals(
                    name
                ) &&
                finalTree.states[stateIndex].districts[districtIndex].cities[cityIndex].complexes[i].uuid.equals(
                    uuid
                )
            ) {
                complex =
                    finalTree.states[stateIndex].districts[districtIndex].cities[cityIndex].complexes[i]
            }
        }

        return complex
    }


    private fun convertDBTreeToLambdaTree(tree: Country): AccessTree {
        val accessTree = AccessTree()
        accessTree.country = sukriti.ngo.mis.ui.complexes.data.lambdaData.Country()
        accessTree.country.states = ArrayList()
        for(i in 0 until tree.states.size) {
            val lambdaState = sukriti.ngo.mis.ui.complexes.data.lambdaData.State()
            val dbState = tree.states[i]
            lambdaState.name = dbState.name
            lambdaState.code = dbState.code
            lambdaState.recursive = dbState.recursive

            for(j in 0 until dbState.districts.size) {
                val lambdaDistrict = sukriti.ngo.mis.ui.complexes.data.lambdaData.District()
                val dbDistrict = tree.states[i].districts[j]
                lambdaDistrict.name = dbDistrict.name
                lambdaDistrict.code = dbDistrict.code
                lambdaDistrict.recursive = dbDistrict.recursive

                for(k in 0 until dbDistrict.cities.size) {
                    val lambdaCity = sukriti.ngo.mis.ui.complexes.data.lambdaData.City()
                    val dbCity = tree.states[i].districts[j].cities[k]

                    lambdaCity.name = dbCity.name
                    lambdaCity.code = dbCity.code
                    lambdaCity.recursive = dbCity.recursive

                     for(l in 0 until dbCity.complexes.size) {
                         val lambdaComplex = sukriti.ngo.mis.ui.complexes.data.lambdaData.Complex()
                         val dbComplex = tree.states[i].districts[j].cities[k].complexes[l]

                         lambdaComplex.name = dbComplex.name
                         lambdaComplex.lon = dbComplex.lon
                         lambdaComplex.lat = dbComplex.lat
                         lambdaComplex.address = dbComplex.address
                         lambdaComplex.uuid = dbComplex.uuid
                         lambdaComplex.coco = dbComplex.coco
                         lambdaComplex.client = dbComplex.client
                         lambdaComplex.isSelected = dbComplex.isSelected == 1

                         if(lambdaCity.complexes == null)
                             lambdaCity.complexes = ArrayList()
                         lambdaCity.complexes.add(lambdaComplex)
                     }

                    if(lambdaDistrict.cities == null)
                        lambdaDistrict.cities = ArrayList()
                    lambdaDistrict.cities.add(lambdaCity)
                }
                if(lambdaState.districts == null) {
                    lambdaState.districts = ArrayList()
                }
                lambdaState.districts.add(lambdaDistrict)
            }
            accessTree.country.states.add(lambdaState)
        }

        Log.i("finalTree", "Access Tree")
        Log.i("finalTree", Gson().toJson(accessTree))

        return accessTree
    }

}
