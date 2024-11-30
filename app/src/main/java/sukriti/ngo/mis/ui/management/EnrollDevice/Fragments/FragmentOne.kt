package sukriti.ngo.mis.ui.management.EnrollDevice.Fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.JsonObject
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.FragmentOneBinding
import sukriti.ngo.mis.ui.management.EnrollDevice.Navigation.ViewPagerControl
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Constants.SELECT_ACTION_COMMISSIONING
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Constants.SELECT_ACTION_SMARTNESS
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ComplexDetails
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ThingDetails
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.NameCodeModel
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.UnoSelect
import sukriti.ngo.mis.ui.management.ManagementViewModel
import sukriti.ngo.mis.ui.management.fargments.ComplexDetailsDialog
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListIotStateDistrictCityResponseHandler
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaRequest
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponseHandler
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ManagementLambdaRequest
import sukriti.ngo.mis.utils.AWSIotProvisioningClient
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient


class FragmentOne(
    val viewModel: EnrollDeviceViewModel,
    val managementViewModel: ManagementViewModel,
    val lambdaClient: LambdaClient
) : Fragment() {

    private lateinit var mUnoSelectFragment: UnoSelect
    private lateinit var smartness: TextView
    private lateinit var newBillingGroup: Button
    private lateinit var provisioningClient: AWSIotProvisioningClient
    private lateinit var binding: FragmentOneBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPref: SharedPrefsClient
    lateinit var userName: String
    private var statesList = mutableListOf<NameCodeModel>()
    private var districtList = mutableListOf<NameCodeModel>()
    private var cityList = mutableListOf<NameCodeModel>()
    private var complexList = emptyArray<String>()
    var cabinDetailsIndex = arrayOf(0)
    private var interactionListener: ViewPagerControl? = null
    var TAG = "Enrollment"
    var Debugging = "debugging"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun setInteractionListener(listener: ViewPagerControl) {
        interactionListener = listener
    }
    companion object {
        const val SELECT_STATE = 1
        const val SELECT_DISTRICT = 2
        const val SELECT_CITY = 3
        const val SELECT_COMPLEX = 4
        const val DISMISS_DIALOG = 5

        lateinit var INSTANCE: FragmentOne
        fun getInstance(
            viewModel: EnrollDeviceViewModel,
            managementViewModel: ManagementViewModel,
            lambdaClient: LambdaClient
        ): FragmentOne {
            if (INSTANCE == null) {
                INSTANCE = FragmentOne(viewModel, managementViewModel, lambdaClient)
            }

            return INSTANCE
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = FragmentOneBinding.inflate(layoutInflater)
        Log.d(
            TAG,
            "Fragment One: onCreateView() called with: inflater"
        )
        userAlertClient = UserAlertClient(activity)
        sharedPref = SharedPrefsClient(context)
        userName = sharedPref.getUserDetails().user.userName
        val viewPager = activity?.findViewById<ViewPager2>(R.id.walkthrough_pager)
        if (viewPager == null) {
            Log.i("FragTest", "onCreateView: walkthrough pager is null")
            if (activity != null) {
                Log.i("FragTest", "onCreateView: ")
            } else {
                Log.i("FragTest", "onCreateView: activity is null")
            }
        } else {
            Log.i("FragTest", "onCreateView: current item ${viewPager.currentItem}")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(
            TAG,
            "Fragment One: onViewCreated() called"
        )

        viewModel.iotStateListLiveData.observe(viewLifecycleOwner, iotStatesListObserver)
        viewModel.iotDistrictsListLiveData.observe(viewLifecycleOwner, iotDistrictsListObserver)
        viewModel.iotCitiesListLiveData.observe(viewLifecycleOwner, iotCitiesListObserver)
        viewModel.iotComplexesListLiveData.observe(viewLifecycleOwner, iotComplexesListObserver)


        binding.selectState.setOnClickListener {

            // Call lambda from here
            // Handle callback from here

            userAlertClient.showWaitDialog("Getting States")
            val callback = object : ListIotStateDistrictCityResponseHandler {
                override fun onSuccess(response: JsonObject) {
                    userAlertClient.closeWaitDialog()

                    val statusCode = response.get("statusCode").asInt
                    if (statusCode == 403) {
                        userAlertClient.showDialogMessage(
                            "Access Denied",
                            "You do not have permission",
                            false
                        )
                    } else if (statusCode == 200) {
                        val jsonArray = response.getAsJsonArray("body")
                        if (jsonArray.size() > 0) {

                            var list = emptyArray<String>()
                            val tempStateList = mutableListOf<NameCodeModel>()
                            for (i in 0 until jsonArray.size()) {
                                val obj = jsonArray.get(i).asJsonObject
                                val tempObj = NameCodeModel()
                                tempObj.NAME = obj.get("NAME").asString
                                tempObj.CODE = obj.get("CODE").asString
                                tempStateList.add(tempObj)
                                list += obj.get("NAME").asString
                            }
                            statesList = tempStateList
                            showDialog(SELECT_STATE, list)
                        } else {
                            userAlertClient.showDialogMessage(
                                "Data not available",
                                "No States are available",
                                false
                            )
                        }
                    }
                }

                override fun onError(message: String?) {
                    userAlertClient.closeWaitDialog()
                    Log.i("manage_res", "onError: ")
                }

            }

            /*val request = ManagementLambdaRequest(sharedPref.getUserDetails().user.userName, "list-iot-state")
            viewModel.fetchListOf(request, callback, "list-iot-state")*/

            viewModel.fetchIotStatesList(object : ListIotStateDistrictCityResponseHandler {
                override fun onSuccess(response: JsonObject) {

                }

                override fun onError(message: String?) {
                    userAlertClient.closeWaitDialog()
                    userAlertClient.showDialogMessage("Error", message, false)
                }

            })
        }

        binding.selectDistrict.setOnClickListener {
            if (binding.selectState.text.isNotBlank()) {

                userAlertClient.showWaitDialog("Getting Districts")

                val callback = object : ListIotStateDistrictCityResponseHandler {
                    override fun onSuccess(response: JsonObject) {


                    }

                    override fun onError(message: String?) {
                        userAlertClient.closeWaitDialog()
                        userAlertClient.showDialogMessage("Error", message, false)
                    }

                }
                viewModel.fetchIotDistrictList(callback)
            } else {
                binding.selectStateError.visibility = View.VISIBLE
            }
        }

        binding.selectCity.setOnClickListener {
            if (binding.selectDistrict.text.isNotBlank()) {

                userAlertClient.showWaitDialog("Getting Cities")

                val callback = object : ListIotStateDistrictCityResponseHandler {
                    override fun onSuccess(response: JsonObject) {

                    }

                    override fun onError(message: String?) {
                        userAlertClient.closeWaitDialog()
                        userAlertClient.showDialogMessage("Error", message, false)
                    }

                }

                viewModel.fetchIotCityList(callback)

            } else {
                binding.selectDistrictError.visibility = View.VISIBLE
            }
        }

        binding.selectComplex.setOnClickListener {
            if (binding.selectCity.text.isNotBlank()) {
                userAlertClient.showWaitDialog("Getting Complexes")
                val callback = object : ListIotStateDistrictCityResponseHandler {
                    override fun onSuccess(response: JsonObject) {

                    }

                    override fun onError(message: String?) {
                        userAlertClient.closeWaitDialog()
                        userAlertClient.showDialogMessage("Error", message, false)
                    }

                }

                viewModel.fetchIotComplexList(callback)
            } else {
                binding.selectCityError.visibility = View.VISIBLE
            }
        }

        binding.creteNewComplexButton.setOnClickListener {
            Log.i(TAG, "onViewCreated: Fragment One: create complex button clicked")

            userAlertClient.showWaitDialog("Wait...")
            /*            val builder = AlertDialog.Builder(context, R.style.full_screen_dialog)
                        val createComplexView = inflater.inflate(R.layout.create_new_complex, null)
                        builder.setView(createComplexView)

                        val dialog = builder.create()
                        userAlertClient.closeWaitDialog()
                        dialog.show()*/

//            setOnClickListenerForCreateNewComplex(createComplexView)

            val frag = RegisterComplex()
            userAlertClient.closeWaitDialog()
            frag.show(parentFragmentManager, "RegisterComplex")
        }

        binding.next.setOnClickListener {
            if(checkComplexSelected()) {
                // Write logic to move to next screen
            } else {
                userAlertClient.showDialogMessage("Complex not selected", "Please select a complex", false)
            }
        }


    }

//    private fun setOnClickListenerForCreateNewComplex(createComplexView: View) {
//        smartness = createComplexView.findViewById(R.id.smartness)
//        newBillingGroup = createComplexView.findViewById(R.id.newBillingGroup)
//        provisioningClient = AWSIotProvisioningClient(context)
//
//        smartness.setOnClickListener {
//            showSimpleSelectionDialog(
//                SELECT_ACTION_SMARTNESS,
//                viewModel.getSmartnessList()!!, "Select Smartness"
//            )
//
//        }
//
//        newBillingGroup.setOnClickListener {
///*
//            val ft = requireFragmentManager().beginTransaction()
//            val prev = requireFragmentManager().findFragmentByTag("RegisterNewBillingGroup")
//            if (prev != null) {
//                ft.remove(prev)
//            }
//            ft.addToBackStack(null)
//            val registerNewBillingGroupFragment: RegisterNewBillingGroup =
//                RegisterNewBillingGroup.newInstance(ADD_ACTION_BILLING_GROUP)
//            registerNewBillingGroupFragment.show(ft, "RegisterNewBillingGroup")
//*/
//
//        }
//
//    }

    private fun showSimpleSelectionDialog(action: Int, list: ArrayList<String>, selection: String) {
        Log.i(TAG, "Fragment One: showSimpleSelectionDialog:")
        val ft = requireFragmentManager().beginTransaction()
        val prev = requireFragmentManager().findFragmentByTag("UnoSelect")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        mUnoSelectFragment = UnoSelect.newInstance(action, selection)
        mUnoSelectFragment.setUp(list) { selection1, action1 ->
            if (action1 == SELECT_ACTION_SMARTNESS) {
                mUnoSelectFragment.dismiss()
                smartness.text = selection1.toString()
                Log.i("selection", "showSimpleSelectionDialog: Selection = $selection1")

            } else if (action1 == SELECT_ACTION_COMMISSIONING) {
                mUnoSelectFragment.dismiss()
                Log.i("selection", "showSimpleSelectionDialog: Selection = $selection1")
            }
        }
        mUnoSelectFragment.show(ft, "UnoSelect")
    }

    private fun showDialog(action: Int, list: Array<String>) {
        val dialog = AlertDialog.Builder(context)
        Log.d(TAG, "Fragment One: showDialog() called with: action = $action, list = $list")
        when (action) {
            SELECT_STATE -> {
                dialog.setTitle("Select State")
                dialog.setSingleChoiceItems(list, -1) { dialogInterface, position ->
                    binding.selectState.text = list[position]

                    binding.selectDistrict.text = ""
                    binding.selectCity.text = ""
                    binding.selectComplex.text = ""

                    binding.selectStateError.visibility = View.GONE
                    binding.selectDistrictError.visibility = View.GONE
                    binding.selectCityError.visibility = View.GONE

                    viewModel.selectedState = position
                    viewModel.selectedDistrict = -1
                    viewModel.selectedCity = -1
                    viewModel.selectedComplex = -1

                    viewModel.thingsList.clear()
                    viewModel.thingDetailsList.clear()
                    cabinDetailsIndex[0] = 0
                    dialogInterface.dismiss()
                }

                dialog.show()
            }

            SELECT_DISTRICT -> {
                dialog.setTitle("Select District")
                if (binding.selectState.text.isNotBlank()) {
                    dialog.setSingleChoiceItems(list, -1) { dialogInterface, position ->
                        binding.selectDistrict.text = list[position]
                        binding.selectCity.text = ""
                        binding.selectComplex.text = ""

                        binding.selectDistrictError.visibility = View.GONE
                        binding.selectCityError.visibility = View.GONE

                        viewModel.selectedDistrict = position
                        viewModel.selectedCity = -1
                        viewModel.selectedComplex = -1
                        viewModel.thingsList.clear()
                        viewModel.thingDetailsList.clear()
                        cabinDetailsIndex[0] = 0
                        dialogInterface.dismiss()
                    }

                    dialog.show()
                } else {
                    binding.selectStateError.visibility = View.VISIBLE
                }

            }

            SELECT_CITY -> {
                dialog.setTitle("Select City")
                if (binding.selectDistrict.text.isNotBlank()) {
                    dialog.setSingleChoiceItems(list, -1) { dialogInterface, position ->
                        binding.selectCity.text = list[position]

                        binding.selectComplex.text = ""

                        binding.selectCityError.visibility = View.GONE

                        viewModel.selectedCity = position
                        viewModel.thingsList.clear()
                        viewModel.thingDetailsList.clear()
                        cabinDetailsIndex[0] = 0

                        dialogInterface.dismiss()
                    }

                    dialog.show()
                } else {
                    binding.selectDistrictError.visibility = View.VISIBLE
                }
            }

            SELECT_COMPLEX -> {
                dialog.setTitle("Select Complex")
                if (binding.selectCity.text.isNotBlank()) {
                    dialog.setSingleChoiceItems(list, -1) { dialogInterface, position ->
                        binding.selectComplex.text = list[position]

                        binding.selectCityError.visibility = View.GONE
                        viewModel.selectedComplex = position
                        cabinDetailsIndex[0] = 0

                        userAlertClient.showWaitDialog("Loading complex details")

                        val callback = object : ListIotStateDistrictCityResponseHandler {
                            override fun onSuccess(response: JsonObject) {
                                Log.i("complexDetails", response.toString())

                                val gson = Gson()
                                val jsonObject =
                                    gson.fromJson(response.get("body"), JsonObject::class.java)
                                Log.i("complexDetails", jsonObject.toString())

                                parseResponse(jsonObject)
                            }

                            override fun onError(message: String?) {
                                if (message != null) {
                                    Log.i("complexDetails", message)
                                }
                            }

                        }

                        val request = ManagementLambdaRequest(
                            userName,
                            "list-iot-complexDetail",
                            viewModel.iotComplexesList[position]
                        )

                        lambdaClient.ExecuteManagementLambda(request, callback)

                        dialogInterface.dismiss()
                    }

                    dialog.show()
                } else {
                    binding.selectCityError.visibility = View.VISIBLE
                }
            }

            DISMISS_DIALOG -> {

            }
        }

    }

    private fun parseResponse(response: JsonObject) {
        Log.d(TAG, "Fragment One: parseResponse() called with: response = $response")
        val complexDetails = ComplexDetails()

        for (key in response.keySet()) {
            when (key) {
                "STATE_NAME" -> complexDetails.stateName =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "DISTRICT_NAME" -> complexDetails.districtName =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "CITY_NAME" -> complexDetails.cityName =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "STATE_CODE" -> complexDetails.stateCode =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "DISTRICT_CODE" -> complexDetails.districtCode =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "CITY_CODE" -> complexDetails.cityCode =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "LATT" -> complexDetails.latitude =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "LONG" -> complexDetails.longitude =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "CLNT" -> complexDetails.client =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "BILL" -> complexDetails.billingGroup =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "DATE" -> complexDetails.date =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "DEVT" -> complexDetails.deviceType =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "SLVL" -> complexDetails.smartness =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "QMWC" -> complexDetails.wCCountMale =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "QFWC" -> complexDetails.wCCountFemale =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "QPWC" -> complexDetails.wCCountPD =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "QURI" -> complexDetails.urinals =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "QURC" -> complexDetails.urinalCabins =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "QBWT" -> complexDetails.bwt =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "COCO" -> complexDetails.commissioningStatus =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "QSNV" -> complexDetails.napkinVmCount =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "QSNI" -> complexDetails.napkinIncineratorCount =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "MSNI" -> complexDetails.napkinIncineratorManufacturer =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "MSNV" -> complexDetails.napkinVmManufacturer =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "AR_K" -> complexDetails.kioskArea =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "CWTM" -> complexDetails.waterAtmCapacity =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "ARSR" -> complexDetails.supervisorRoomSize =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "MANU" -> complexDetails.manufacturer =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "CIVL" -> complexDetails.civilPartner =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "TECH" -> complexDetails.techProvider =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "ONMP" -> complexDetails.oMPartner =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "UUID" -> complexDetails.uuid =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "ROUTER_MOBILE" -> complexDetails.routerMobile =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "ROUTER_IMEI" -> complexDetails.routerImei =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "MODIFIED_BY" -> complexDetails.modifiedBy =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "THINGGROUPTYPE" -> complexDetails.thingGroupType =
                    response[key].toString().substring(1, response[key].toString().length - 1)

                "ADDR" -> complexDetails.address =
                    response[key].toString().substring(1, response[key].toString().length - 1)

            }
        }

        complexDetails.complexName = viewModel.iotComplexesList[viewModel.selectedComplex]

        viewModel.complexDetails = complexDetails

        val request = ManagementLambdaRequest(
            userName,
            "list-iot-thing",
            viewModel.complexDetails.complexName
        )
        lambdaClient.ExecuteIotCabinLambda(request, cabinListHandler)

        Log.i("dialog", Gson().toJson(complexDetails))
//        setupAndShowComplexDetails(complexDetails)

    }

    private fun setupAndShowComplexDetails(complexDetails: ComplexDetails) {

        Log.d(
            TAG,
            "Fragment One: setupAndShowComplexDetails() called with: complexDetails = $complexDetails"
        )
        /*
                val builder = AlertDialog.Builder(context, R.style.full_screen_dialog)
                val createComplexView = layoutInflater.inflate(R.layout.create_new_complex, null)
                builder.setView(createComplexView)
                createComplexView.findViewById<TextView>(R.id.smartness).text = complexDetails.smartness
                createComplexView.findViewById<TextView>(R.id.stateName).text = complexDetails.stateName
                createComplexView.findViewById<TextView>(R.id.districtName).text = complexDetails.districtName
                createComplexView.findViewById<TextView>(R.id.cityName).text = complexDetails.cityName
        */

        val frag = ComplexDetailsDialog()
        frag.isCancelable = false
        userAlertClient.closeWaitDialog()
        frag.show(parentFragmentManager, "ComplexDetails")
    }

    private val cabinListHandler = object : ListIotStateDistrictCityResponseHandler {
        override fun onSuccess(response: JsonObject) {
            viewModel.thingsList.clear()
            val body = response.get("body").asJsonObject
            val thingsArray = body.get("things").asJsonArray
            for (i in 0 until thingsArray.size()) {
                val thing = thingsArray[i].asString
                Log.i("thingsList", "$i -> $thing")
                viewModel.thingsList.add(thing)
            }


            /*if(viewModel.thingsList.size > 0) {
                val request = ManagementLambdaRequest(
                    userName,
                    "describe-iot-thing",
                    viewModel.thingsList[0]
                )
                lambdaClient.ExecuteManagementLambda(request, cabinDetailsHandler)
            }*/

            if (thingsArray.size() > 0) {
                fetchThingDetails()
            } else {
                // closing dialog with text "Getting complex details"
                userAlertClient.closeWaitDialog()
                fetchPolicies()
//                setupAndShowComplexDetails(viewModel.complexDetails)
            }

        }

        override fun onError(message: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error", message, false)
        }

    }

    private val cabinDetailsHandler = object : ListIotStateDistrictCityResponseHandler {
        override fun onSuccess(response: JsonObject) {
            Log.i("CabinDetails", "Details -: ${Gson().toJson(response)}");
            val statusCode = response.get("statusCode").asInt
            if (statusCode == 200) {
                val details = ThingDetails()
                details.AttributesMap = HashMap<String, String>()
                val body = response.get("body").asJsonObject
//                details.DefaultClientId = body.get("defaultClientId").asString
                details.DefaultClientId = body.get("defaultClientId").asString
                Log.i("CabinDetails", "Default client ID ${details.DefaultClientId}")
                details.Name = body.get("thingName").asString
                Log.i("CabinDetails", "Thing Name ${details.Name}")
                details.Id = body.get("thingId").asString
                details.Arn = body.get("thingArn").asString
                details.ThingType = body.get("thingTypeName").asString

                val objectMapper = ObjectMapper()
                val jsonNode: JsonNode = objectMapper.readTree(response.toString())
                val attributeNode = jsonNode["body"]["attributes"]

                if (!attributeNode.isEmpty) {
                    attributeNode.fields().forEach {
                        details.AttributesMap[it.key] = it.value.asText()
                    }
                }

                Log.i("CabinDetails", "Cabin Details ${Gson().toJson(details)}")
                viewModel.thingDetailsList.add(details)

                cabinDetailsIndex[0] = cabinDetailsIndex[0] + 1

                if (cabinDetailsIndex[0] < viewModel.thingsList.size) {
                    fetchThingDetails()
                } else {
//                    setupAndShowComplexDetails(viewModel.complexDetails)
                    fetchPolicies()
                }

            } else {
                cabinDetailsIndex[0] = cabinDetailsIndex[0] + 1
                fetchThingDetails()
            }
        }

        override fun onError(message: String?) {

        }

    }

    fun fetchThingDetails() {
        if (cabinDetailsIndex[0] < viewModel.thingsList.size) {
            if (cabinDetailsIndex[0] == 0) {
                viewModel.thingDetailsList.clear()
            }
            val request = ManagementLambdaRequest(
                userName,
                "describe-iot-thing",
                viewModel.thingsList[cabinDetailsIndex[0]]
            )
            lambdaClient.ExecuteIotCabinLambda(request, cabinDetailsHandler)
        }
    }

    fun fetchPolicies() {
        userAlertClient.closeWaitDialog()

        val request = ListPolicyLambdaRequest(managementViewModel.getSelectedEnterprise().name)

        val callback = object : ListPolicyLambdaResponseHandler {

           override fun onSuccess(response: ListPolicyLambdaResponse) {
//                userAlertClient.closeWaitDialog()
                if (response.statusCode == 200) {
//                    viewModel.policies = response.body.toMutableList()
                    setupAndShowComplexDetails(viewModel.complexDetails)
                } else {
                    userAlertClient.closeWaitDialog()
                    Log.e("listPolicies", "onSuccess: error in policies lambda response")
                    setupAndShowComplexDetails(viewModel.complexDetails)
                }
            }



            override fun onError(message: String) {
                userAlertClient.closeWaitDialog()
                userAlertClient.showDialogMessage("Error", message, false)
            }

        }

        userAlertClient.showWaitDialog("Getting policies data")
        lambdaClient.ExecuteListPolicyLambda(request, callback)
    }


    private val iotStatesListObserver = Observer<Array<String>> {
        Log.d(TAG, "iotStatesListObserver: $it")
        if (it != null) {
            userAlertClient.closeWaitDialog()
            Log.d(TAG, "calling show dialog for states")
            showDialog(SELECT_STATE, it)
        }
    }

    private val iotDistrictsListObserver = Observer<Array<String>> {
        Log.d(TAG, "iotDistrictsListObserver: $it")
        if (it != null) {
            userAlertClient.closeWaitDialog()
            Log.d(TAG, "calling show dialog for districts")
            showDialog(SELECT_DISTRICT, it)
        }
    }

    private val iotCitiesListObserver = Observer<Array<String>> {
        Log.d(TAG, "iotCitiesListObserver: $it")
        if (it != null) {
            userAlertClient.closeWaitDialog()
            Log.d(TAG, "calling show dialog for cities")
            showDialog(SELECT_CITY, it)
        }
    }

    private val iotComplexesListObserver = Observer<Array<String>> {
        Log.d(TAG, "iotComplexesListObserver: $it")
        if (it != null) {
            userAlertClient.closeWaitDialog()
            Log.d(TAG, "calling show dialog for complexes")
            showDialog(SELECT_COMPLEX, it)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i(Debugging, "onPause: FragOne ")
    }

    override fun onStop() {
        super.onStop()
        Log.i(Debugging, "onStop: FragOne ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(Debugging, "onDestroyView: FragOne ")
        viewModel.iotStateListLiveData.removeObserver(iotStatesListObserver)
        viewModel.iotDistrictsListLiveData.removeObserver(iotDistrictsListObserver)
        viewModel.iotCitiesListLiveData.removeObserver(iotCitiesListObserver)
        viewModel.iotComplexesListLiveData.removeObserver(iotComplexesListObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(Debugging, "onDestroy: FragOne ")
    }

    fun checkComplexSelected(): Boolean {
        return viewModel.complexDetails.complexName.isBlank()
    }


}