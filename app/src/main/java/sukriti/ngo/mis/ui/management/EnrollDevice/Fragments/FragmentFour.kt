package sukriti.ngo.mis.ui.management.EnrollDevice.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.JsonObject
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.FragmentFourBinding
import sukriti.ngo.mis.ui.management.EnrollDevice.Lambda.DDBSaveLambda.DynamoDbDataWriterResponseCallback
import sukriti.ngo.mis.ui.management.EnrollDevice.Navigation.ViewPagerControl
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.PolicyListItem
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.RefreshListCallback
import sukriti.ngo.mis.ui.management.ManagementViewModel
import sukriti.ngo.mis.ui.management.adapters.PolicyListAdapter
import sukriti.ngo.mis.ui.management.fargments.CreateNewPolicyDialog
import sukriti.ngo.mis.ui.management.fargments.PolicyDetails
import sukriti.ngo.mis.ui.management.lambda.GetPolicyDataLambda.GetPolicyLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.GetPolicyDataLambda.GetPolicyLambdaResponseHandler
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.UserAlertClient


class FragmentFour(
    val lambdaClient: LambdaClient,
    val viewModel: EnrollDeviceViewModel,
    val managementViewModel: ManagementViewModel
) : Fragment() {

    private lateinit var binding: FragmentFourBinding;
    private val Debugging = "debugging"
    var interactionListener: ViewPagerControl? = null
    private lateinit var userAlertClient: UserAlertClient

    companion object {
        private const val TAG = "debugging"
    }
    val adapter = PolicyListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: FragmentFour")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView: FragmentFour")
        binding = FragmentFourBinding.inflate(inflater)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.walkthrough_pager)
        setAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated: FragmentFour")
        userAlertClient = UserAlertClient(activity)

        binding.next.setOnClickListener {

            if(viewModel.stepFourCompleted) {
                interactionListener?.goToNextPage()
            } else {
                if(viewModel.selectedPolicy.name != null && viewModel.selectedPolicy.name != "" ) {
                    saveDataInDynamoDb()
                } else {
                    userAlertClient.showDialogMessage("Policy not selected", "Please select a policy", false)
                }
            }
        }

        binding.back.setOnClickListener {
            interactionListener?.goToPrevPage()
        }

        binding.createPolicy.setOnClickListener {
            val dialog = CreateNewPolicyDialog()
            dialog.managementViewModel = managementViewModel
            dialog.enrollDeviceViewModel = viewModel
            dialog.show(childFragmentManager, "Create New Policy")

        }

        viewModel.policyListLiveData.observe(viewLifecycleOwner ,refreshPolicyListObserver)

    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: FragmentFour")
    }

    fun setAdapter() {
        adapter.policiesList = viewModel.policyListItem
        adapter.showDelete = false
        Log.i(
            "listPolicy",
            "setAdapter: Adapter Policy List: ${Gson().toJson(adapter.policiesList)}"
        )
        Log.i(
            "listPolicy",
            "setAdapter: View Model Policy List: ${Gson().toJson(viewModel.policies)}"
        )
        val clickListener = object : PolicyListAdapter.PolicyListItemClickListener {
            override fun onClick(policy: PolicyListItem) {
/*
                val policyDetailsDialog = PolicyDetails()
                policyDetailsDialog.policyDetails = policy
                policyDetailsDialog.setViewModel(viewModel)
                policyDetailsDialog.isSelectable = true
                policyDetailsDialog.isEditable = true
                policyDetailsDialog.selectedCallback = selectedCallback
                policyDetailsDialog.setManagementViewModel(managementViewModel)
                policyDetailsDialog.setRefreshListCallback(refreshListCallback)
                policyDetailsDialog.show(parentFragmentManager, "CabinDetails")
*/
                userAlertClient.showWaitDialog("Loading policy")

                val request = JsonObject()
                request.addProperty("enterprises_id", managementViewModel.getSelectedEnterprise().name)
                request.addProperty("policy_name", "policies/${policy.name}")

                Log.i("getPolicy", "Request $request")
                lambdaClient.ExecuteGetPolicyLambda(request, getPolicyDataCallback)


            }

        }
        adapter.clickListener = clickListener
        adapter.selectedCallback = selectedCallback
        binding.policyListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.policyListRecyclerView.adapter = adapter
    }

    private val getPolicyDataCallback = object : GetPolicyLambdaResponseHandler {
        override fun onSuccess(response: GetPolicyLambdaResponse) {
            userAlertClient.closeWaitDialog()
            // Prepare and show policy details dialog
            val policyDetailsDialog = PolicyDetails()
            policyDetailsDialog.policyDetails = response.body
            policyDetailsDialog.setViewModel(viewModel)
            policyDetailsDialog.isSelectable = true
            policyDetailsDialog.isEditable = true
            policyDetailsDialog.selectedCallback = selectedCallback
            policyDetailsDialog.setManagementViewModel(managementViewModel)
            policyDetailsDialog.setRefreshListCallback(refreshListCallback)
            policyDetailsDialog.show(parentFragmentManager, "CabinDetails")

        }

        override fun onError(message: String) {
            userAlertClient.closeWaitDialog()

            userAlertClient.showDialogMessage("Error", "Something went wrong. Please try again later", false)
        }

    }


    private fun saveDataInDynamoDb() {
        val payload = JsonObject()

        val policy = JsonObject()
//        policy.addProperty("policy_name", "SukritiPolicy");
        val policyName = viewModel.selectedPolicy.name.split("/")[3]
        policyName.replace("_KIOSK", "")
        policyName.replace("_KIOSK_LAUNCHER", "")

        policy.addProperty("policy_name", policyName);

        payload.add("policy_details", policy)
        payload.addProperty("serial_number", viewModel.deviceSerialNumber)
        payload.addProperty("command", "update-data")
        payload.addProperty("details_type", "policy_details")
//        payload.add("policy", getPolicyJson(viewModel.policies[1]))

        Log.i("ddbPayload", Gson().toJson(payload))
        userAlertClient.showWaitDialog("Processing...")
        try {
            lambdaClient.ExecuteDynamoDbDataWriter(
                payload,
                object : DynamoDbDataWriterResponseCallback {
                    override fun onSuccess(response: JsonObject) {
                        userAlertClient.closeWaitDialog()
                        val statusCode = response.get("statusCode").asInt
                        if (statusCode == 200) {
                            val message = response.get("body").asString
                            userAlertClient.showDialogMessage(
                                "Success",
                                message
                            ) {
                                viewModel.policyDdbUpdated = true
                                viewModel.stepFourCompleted = true
                                interactionListener?.goToNextPage()
                            }

                        } else {
                            val message = response.get("body").asString
                            userAlertClient.showDialogMessage(
                                "Error, Please retry", message
                            ) {
                                // Do nothing and don't let the user move forward.
                            }
                        }
                    }

                    override fun onError(message: String) {
                        userAlertClient.closeWaitDialog()
                        userAlertClient.showDialogMessage("Error", message, false)
                    }

                })
        } catch (exception: Exception) {
            userAlertClient.closeWaitDialog()
            Log.i("Exception", exception.message)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: FragmentFour")
        setAdapter()
    }
    override fun onPause() {
        super.onPause()
        Log.i(Debugging, "onPause: FragmentFour ")
    }

    override fun onStop() {
        super.onStop()
        Log.i(Debugging, "onStop: FragmentFour ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(Debugging, "onDestroyView: FragmentFour ")
        viewModel.policyListLiveData.removeObserver(refreshPolicyListObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(Debugging, "onDestroy: FragmentFour ")
    }

    private val selectedCallback = PolicyDetails.SelectedCallback {
        adapter.policiesList = viewModel.policyListItem
        adapter.notifyDataSetChanged()
    }

    private val refreshListCallback = object: RefreshListCallback {
        override fun refreshList() {
            // do the work of refreshing the policy list as user as updated the policy
            Log.i("RefreshPolicy", "asking view model to refresh po")
            viewModel.refreshPolicies()
        }
    }

    private val refreshPolicyListObserver = Observer<MutableList<PolicyListItem>> {
        adapter.policiesList = viewModel.policyListItem
        adapter.notifyDataSetChanged()
    }

}