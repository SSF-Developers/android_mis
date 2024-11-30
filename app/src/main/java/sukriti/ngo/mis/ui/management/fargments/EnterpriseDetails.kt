package sukriti.ngo.mis.ui.management.fargments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.JsonObject
import sukriti.ngo.mis.communication.SimpleHandler
import sukriti.ngo.mis.databinding.FragmentEnterpriseDetailsBinding
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.SubmitCallback
import sukriti.ngo.mis.ui.management.ManagementViewModel
import sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise.DeleteEnterpriseLambdaRequest
import sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise.DeleteEnterpriseLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.DeleteEnterprise.DeleteEnterpriseResponseHandler
import sukriti.ngo.mis.ui.management.lambda.PatchEnterprise.PatchEnterpriseLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.UndoEnterpriseDelete.UndoEnterpriseDeleteLambdaRequest
import sukriti.ngo.mis.ui.management.lambda.UndoEnterpriseDelete.UndoEnterpriseDeleteResponse
import sukriti.ngo.mis.ui.management.lambda.UndoEnterpriseDelete.UndoEnterpriseDeleteResponseHandler
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities

class EnterpriseDetails(
    val managementViewModel: ManagementViewModel
) : Fragment() {

    lateinit var binding : FragmentEnterpriseDetailsBinding
    lateinit var userAlertClient: UserAlertClient
    lateinit var lambdaClient: LambdaClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEnterpriseDetailsBinding.inflate(inflater)
        userAlertClient = UserAlertClient(activity)
        lambdaClient = LambdaClient(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()

        binding.updateEnterpriseButton.setOnClickListener {
            val updateDialog = UpdateEnterpriseDialog(updateEnterpriseCallback)
            updateDialog.isCancelable = false
            updateDialog.show(childFragmentManager, "Update Enterprise")
        }

        binding.deleteEnterpriseButton.setOnClickListener {
//            userAlertClient.showConfirmActionDialog(
//                childFragmentManager,
//                "Delete Enterprise ${managementViewModel.getSelectedEnterprise().enterpriseDisplayName}",
//                "Do you want to delete this enterprise? Type DELETE to confirm.",
//                "DELETE",
//                deleteEnterpriseCallback
//            )

            val fragment = EnterpriseDelete(managementViewModel)
            fragment.isCancelable = true
            fragment.show(childFragmentManager, "Delete Enterprise")
        }

        binding.undoDeleteEnterpriseButton.setOnClickListener {
            userAlertClient.showConfirmActionDialog(
                childFragmentManager,
                "Undo Delete Enterprise ${managementViewModel.getSelectedEnterprise().enterpriseDisplayName}",
                "Do you want to undo delete? Type UNDO to confirm.",
                "UNDO",
                undoDeleteEnterpriseCallback
            )
        }

        if(managementViewModel.getSelectedEnterprise().ttl != null) {
            Utilities.convertEpochTStoDate(managementViewModel.getSelectedEnterprise().ttl!!)
        }

    }

    private val deleteEnterpriseCallback  = object: SimpleHandler {
        override fun onSuccess() {
            // call delete lambda
            userAlertClient.showWaitDialog("Deleting Enterprise")
            val deleteRequest = DeleteEnterpriseLambdaRequest()
            deleteRequest.name = managementViewModel.getSelectedEnterprise().name
            deleteRequest.soft_delete = "true"
            lambdaClient.ExecuteDeleteEnterpriseLambda(deleteRequest, object: DeleteEnterpriseResponseHandler {
                override fun onSuccess(response: DeleteEnterpriseLambdaResponse) {
                    userAlertClient.closeWaitDialog()
                    if(response.statusCode == 200) {
                        userAlertClient.showDialogMessage("Delete successful", "Enterprise deleted successfully", false)
                    }
                    else {
                        userAlertClient.showDialogMessage("Delete failed", "Enterprise deletion failed", false)
                    }
                }

                override fun onError(message: String?) {
                    userAlertClient.closeWaitDialog()
                    userAlertClient.showDialogMessage("Error", message, false)
                }
            })
        }

        override fun onError(message: String?) {
        }
    }

    private val undoDeleteEnterpriseCallback = object : SimpleHandler {
        override fun onSuccess() {
            userAlertClient.showWaitDialog("Performing undo action.")
            val undoDeleteRequest = UndoEnterpriseDeleteLambdaRequest()
            undoDeleteRequest.name = managementViewModel.getSelectedEnterprise().name
            undoDeleteRequest.soft_delete = "false"
            lambdaClient.ExecuteUndoEnterpriseDeleteLambda(undoDeleteRequest, object: UndoEnterpriseDeleteResponseHandler {
                override fun onSuccess(response: UndoEnterpriseDeleteResponse) {
                    userAlertClient.closeWaitDialog()
                    if(response.statusCode == 200) {
                        userAlertClient.showDialogMessage("Undo Delete successful", "Enterprise restored successfully", false)
                        binding.deleteTime.visibility = View.GONE
                        binding.undoDeleteEnterpriseButton.visibility = View.GONE
                        binding.deleteEnterpriseButton.visibility = View.VISIBLE

//                        managementViewModel.getSelectedEnterprise().state = "active"
//                        managementViewModel.getSelectedEnterprise().ttl = null
                    }
                }

                override fun onError(message: String) {
                    userAlertClient.closeWaitDialog()
                    userAlertClient.showDialogMessage("Undo Delete failed", "Enterprise restoration failed", false)
                }
            })
        }

        override fun onError(errorMessage: String?) {
        }
    }

    private val updateEnterpriseCallback = object: SubmitCallback {
        override fun onSubmit(json: JsonObject) {
            val payload = JsonObject()
            payload.addProperty("command", "patch_enterprise")
            payload.addProperty("enterpriseId", managementViewModel.getSelectedEnterprise().name)
            payload.add("updateBody", json)

            Log.i("updateEnterprise", "Final Payload: $payload" )
            userAlertClient.showWaitDialog("Patching enterprise...")
            managementViewModel.patchEnterprise(payload, patchDeviceCallback)
        }
    }

    private val patchDeviceCallback = object: PatchEnterpriseLambdaResponse {
        override fun onSuccess(response: JsonObject) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Update successful", "Enterprise updated successfully", false)
        }

        override fun onError(message: String) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Update Failed", message, false)
        }
    }

    companion object {
        private var INSTANCE: EnterpriseDetails? = null
        fun getInstance(viewModel: ManagementViewModel) : EnterpriseDetails {
            return INSTANCE ?: EnterpriseDetails(viewModel)
        }
    }


    private fun setupUi() {
        val enterprise = managementViewModel.getSelectedEnterprise()
        binding.enterpriseNameValue.text = enterprise.name
        binding.displayNameValue.text = enterprise.enterpriseDisplayName

        binding.dataProtectionOfficerNameValue.text = enterprise.contactInfo.dataProtectionOfficerName
        binding.dataProtectionOfficerEmailValue.text = enterprise.contactInfo.dataProtectionOfficerEmail
        binding.dataProtectionOfficerPhoneValue.text = enterprise.contactInfo.dataProtectionOfficerPhone

        binding.euRepresentativeNameValue.text = enterprise.contactInfo.euRepresentativeName
        binding.euRepresentativeEmailValue.text = enterprise.contactInfo.euRepresentativeEmail
        binding.euRepresentativePhoneValue.text = enterprise.contactInfo.euRepresentativePhone

        if(enterprise.state == "inactive" && enterprise.ttl != null) {
            binding.deleteEnterpriseButton.visibility = View.GONE
            binding.undoDeleteEnterpriseButton.visibility = View.VISIBLE
            binding.deleteTime.visibility = View.VISIBLE
            val time = Utilities.convertEpochTStoDate(enterprise.ttl!!)
            val uiMessage = "Enterprise will be deleted on: $time"
            binding.deleteTime.text = uiMessage
        }
        else {
            binding.deleteTime.visibility = View.GONE
            binding.deleteEnterpriseButton.visibility = View.VISIBLE
            binding.undoDeleteEnterpriseButton.visibility = View.GONE
        }
    }
}