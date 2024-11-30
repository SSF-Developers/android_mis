package sukriti.ngo.mis.ui.management.fargments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import sukriti.ngo.mis.communication.SimpleHandler
import sukriti.ngo.mis.databinding.FragmentManagePoliciesBinding
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Policy
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.PolicyListItem
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.PolicyDeleteCallback
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.RefreshListCallback
import sukriti.ngo.mis.ui.management.ManageDevices
import sukriti.ngo.mis.ui.management.ManagementViewModel
import sukriti.ngo.mis.ui.management.adapters.PolicyListAdapter
import sukriti.ngo.mis.ui.management.lambda.DeletePolicy.DeletePolicyResponseHandler
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaRequest
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponseHandler
import sukriti.ngo.mis.ui.management.lambda.GetPolicyDataLambda.GetPolicyLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.GetPolicyDataLambda.GetPolicyLambdaResponseHandler
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.UserAlertClient

class ManagePolicies : Fragment() {

    private lateinit var binding: FragmentManagePoliciesBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var lambdaClient: LambdaClient

    private lateinit var managementViewModel: ManagementViewModel
    private lateinit var enrollDeviceViewModel: EnrollDeviceViewModel

    val adapter = PolicyListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManagePoliciesBinding.inflate(inflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    companion object {
        private var INSTANCE : ManagePolicies? = null

        fun getInstance(manage: ManagementViewModel, enroll: EnrollDeviceViewModel): ManagePolicies {
            if(INSTANCE == null) {
                INSTANCE = ManagePolicies()
                INSTANCE!!.managementViewModel = manage
                INSTANCE!!.enrollDeviceViewModel = enroll
            }
            return INSTANCE as ManagePolicies
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userAlertClient = UserAlertClient(activity)
        lambdaClient = LambdaClient(context)

        binding.createNewPolicyButton.setOnClickListener {
            val createDialog = CreateNewPolicyDialog()
            createDialog.setManagementViewModel(managementViewModel)
            createDialog.setEnrollDeviceViewModel(enrollDeviceViewModel)
            createDialog.show(childFragmentManager, "Create New Policy")
        }
//        fetchPolicies()
        setAdapter()
        enrollDeviceViewModel.policyListLiveData.observe(viewLifecycleOwner ,refreshPolicyListObserver)

    }

/*    private fun fetchPolicies() {
        val request = ListPolicyLambdaRequest(managementViewModel.getSelectedEnterprise().name)
        val callback: ListPolicyLambdaResponseHandler = object : ListPolicyLambdaResponseHandler {
            override fun onSuccess(response: ListPolicyLambdaResponse) {
                 userAlertClient.closeWaitDialog();
                if (response.statusCode == 200) {
                    for (i in response.body.indices) {
                        Log.i("Policies", "Policies: " + Gson().toJson(response.body[i]))
                    }
                    managementViewModel.policyList = ArrayList(response.body)
                    setAdapter()
//                    setupAndShowComplexDetails(enrollViewModel.complexDetails)
                } else {
                    userAlertClient.closeWaitDialog()
                    Log.e("listPolicies", "onSuccess: error in policies lambda response")
//                    setupAndShowComplexDetails(enrollViewModel.complexDetails)
                }
            }

            override fun onError(message: String) {
                userAlertClient.closeWaitDialog()
                userAlertClient.showDialogMessage("Error", message, false)
            }
        }
        userAlertClient.showWaitDialog("Getting policies data")
        lambdaClient.ExecuteListPolicyLambda(request, callback)
    }*/

    fun setAdapter() {
/*

        if(managementViewModel.policyList.isEmpty()) {
            Log.i("listPolicy", "Size: ${managementViewModel.policyList.size}" )
            Log.i("listPolicy", "Policy list emtpy" )
            binding.noPoliciesFoundTv.visibility = View.VISIBLE
        }
        else {
            binding.noPoliciesFoundTv.visibility = View.GONE
            adapter.policiesList = managementViewModel.policyList
            Log.i("listPolicy", "Size: ${managementViewModel.policyList.size}" )
            Log.i("listPolicy", "Policy list not empty" )
            Log.i(
                "listPolicy",
                "setAdapter: Adapter Policy List: ${Gson().toJson(adapter.policiesList)}"
            )
            Log.i(
                "listPolicy",
                "setAdapter: View Model Policy List: ${Gson().toJson(managementViewModel.policyList)}"
            )
            val clickListener = object : PolicyListAdapter.PolicyListItemClickListener {
                override fun onClick(policy: Policy) {
                    val policyDetailsDialog = PolicyDetails()
                    policyDetailsDialog.policyDetails = policy
                    policyDetailsDialog.isEditable = true
                    policyDetailsDialog.setViewModel(enrollDeviceViewModel)
                    policyDetailsDialog.setManagementViewModel(managementViewModel)
                    policyDetailsDialog.selectedCallback = selectedCallback
                    policyDetailsDialog.setRefreshListCallback(refreshListCallback)
                    policyDetailsDialog.show(parentFragmentManager, "CabinDetails")
                }

            }
            adapter.clickListener = clickListener
            adapter.selectedCallback = selectedCallback
            binding.policyListRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.policyListRecyclerView.adapter = adapter

        }
*/

        if(managementViewModel.policyListItems.isEmpty()) {
            Log.i("listPolicy", "Size: ${managementViewModel.policyListItems.size}" )
            Log.i("listPolicy", "Policy list empty" )
            binding.noPoliciesFoundTv.visibility = View.VISIBLE
        }
        else {
            binding.noPoliciesFoundTv.visibility = View.GONE
            adapter.policiesList = managementViewModel.policyListItems
            adapter.showDelete = true
            adapter.policyDeleteCallback = policyDeleteCallback
            Log.i("listPolicy", "Size: ${managementViewModel.policyListItems.size}" )
            Log.i("listPolicy", "Policy list not empty" )
            Log.i(
                "listPolicy",
                "setAdapter: Adapter Policy List: ${Gson().toJson(adapter.policiesList)}"
            )
            Log.i(
                "listPolicy",
                "setAdapter: View Model Policy List: ${Gson().toJson(managementViewModel.policyListItems)}"
            )
            val clickListener = object : PolicyListAdapter.PolicyListItemClickListener {
                override fun onClick(policy: PolicyListItem) {
                    userAlertClient.showWaitDialog("Loading policy")

                    val request = JsonObject()
                    request.addProperty("enterprises_id", managementViewModel.getSelectedEnterprise().name)
                    request.addProperty("policy_name", "policies/${policy.name}")

                    Log.i("getPolicy", "Request $request")
                    lambdaClient.ExecuteGetPolicyLambda(request, getPolicyDataCallback)
/*
                    val policyDetailsDialog = PolicyDetails()
                    policyDetailsDialog.policyDetails = policy
                    policyDetailsDialog.isEditable = true
                    policyDetailsDialog.setViewModel(enrollDeviceViewModel)
                    policyDetailsDialog.setManagementViewModel(managementViewModel)
                    policyDetailsDialog.selectedCallback = selectedCallback
                    policyDetailsDialog.setRefreshListCallback(refreshListCallback)
                    policyDetailsDialog.show(parentFragmentManager, "CabinDetails")
*/
                }

            }
            adapter.clickListener = clickListener
            adapter.selectedCallback = selectedCallback
            binding.policyListRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.policyListRecyclerView.adapter = adapter

        }

    }

    private val getPolicyDataCallback = object : GetPolicyLambdaResponseHandler {
        override fun onSuccess(response: GetPolicyLambdaResponse) {
            userAlertClient.closeWaitDialog()
            // Prepare and show policy details dialog
            val policyDetailsDialog = PolicyDetails()
            policyDetailsDialog.policyDetails = response.body
            policyDetailsDialog.isEditable = true
            policyDetailsDialog.setViewModel(enrollDeviceViewModel)
            policyDetailsDialog.setManagementViewModel(managementViewModel)
            policyDetailsDialog.selectedCallback = selectedCallback
            policyDetailsDialog.setRefreshListCallback(refreshListCallback)
            policyDetailsDialog.show(parentFragmentManager, "CabinDetails")

        }

        override fun onError(message: String) {
            userAlertClient.closeWaitDialog()

            userAlertClient.showDialogMessage("Error", "Something went wrong. Please try again later", false)
        }

    }

    private val selectedCallback = PolicyDetails.SelectedCallback {
        adapter.policiesList = managementViewModel.policyListItems
        adapter.notifyDataSetChanged()
    }

    private val refreshListCallback = object :  RefreshListCallback {
        override fun refreshList() {
            // do the work of refreshing policy details here.
//            enrollDeviceViewModel.refreshPolicies()
            userAlertClient.showWaitDialog("Getting Policies")
            managementViewModel.getPoliciesList(object: ListPolicyLambdaResponseHandler {
                override fun onSuccess(response: ListPolicyLambdaResponse) {
                    userAlertClient.closeWaitDialog()
                    adapter.policiesList = managementViewModel.policyListItems
                    adapter.notifyItemRangeChanged(0, adapter.policiesList.size-1)
                }

                override fun onError(message: String) {
                    userAlertClient.closeWaitDialog()
                }
            })
        }
    }

    private val refreshPolicyListObserver = Observer<MutableList<PolicyListItem>> {
//        adapter.policiesList = enrollDeviceViewModel.policies
//        adapter.notifyDataSetChanged()
    }
    private val policyDeleteCallback = object: PolicyDeleteCallback {
        override fun onDeleteRequest(policy: PolicyListItem, position: Int) {
            userAlertClient.showConfirmActionDialog(
                childFragmentManager,
                "Delete Policy?",
                "Do you want to delete ${policy.name} policy?, Type 'DELETE' to confirm.",
                "DELETE",
                object: SimpleHandler {
                    override fun onSuccess() {
                        // call policy delete lambda
                        userAlertClient.showWaitDialog("Deleting policy")
                        val request = JsonObject()
                        request.addProperty("policyName", "${managementViewModel.getSelectedEnterprise().name}/policies/${policy.name}")
                        lambdaClient.ExecuteDeletePolicyLambda(request, object: DeletePolicyResponseHandler {
                            override fun onSuccess(response: JsonObject) {
                                userAlertClient.closeWaitDialog()
                                val statusCode = response.get("statusCode").asInt
                                if(statusCode == 200) {
                                    val message = response.get("body").asString
                                    userAlertClient.showDialogMessage("Delete successful", message, false)
                                    managementViewModel.policyListItems.removeAt(position)
                                    adapter.notifyItemRemoved(position)
                                }
                                else {
                                    val message = response.get("body").asString
                                    userAlertClient.showDialogMessage("Deletion failed", message, false)
                                }
                            }

                            override fun onError(message: String?) {
                                userAlertClient.closeWaitDialog()
                                userAlertClient.showDialogMessage("Error", message, false)
                            }
                        })
                    }

                    override fun onError(message: String?) {
                        // do not do anything
                        // As user didn't type correct action word, don't do anything
                    }
                }
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        enrollDeviceViewModel.policyListLiveData.removeObserver(refreshPolicyListObserver)
    }
}