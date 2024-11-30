package sukriti.ngo.mis.ui.administration.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import sukriti.ngo.mis.R
import sukriti.ngo.mis.communication.SimpleHandler
import sukriti.ngo.mis.databinding.MemberDetailsBinding
import sukriti.ngo.mis.repository.utils.DateConverter.toDateString
import sukriti.ngo.mis.repository.utils.DateConverter.toDateString_FromAwsDateFormat
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import sukriti.ngo.mis.ui.administration.interfaces.RequestHandler
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities.*


class MemberDetails : Fragment() {
    private lateinit var viewModel: AdministrationViewModel
    private lateinit var binding: MemberDetailsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationClient: NavigationClient

    companion object {
        private var INSTANCE: MemberDetails? = null

        fun getInstance(): MemberDetails {
            return INSTANCE
                ?: MemberDetails()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MemberDetailsBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    private fun init() {
        navigationClient = NavigationClient(childFragmentManager)
        userAlertClient = UserAlertClient(activity)
        viewModel =
            ViewModelProviders.of(requireActivity()).get(AdministrationViewModel::class.java)
        setActions()

        binding.userName.text = viewModel.getSelectedUser().cognitoUser.userName
        binding.accountStatus.text = viewModel.getSelectedUser().cognitoUser.accountStatus
        binding.role.text = viewModel.getSelectedUser().cognitoUser.role
        binding.client.text = viewModel.getSelectedUser().cognitoUser.client


        binding.accessDefined.text = getString(
            R.string.memberAccessDefineText,
            viewModel.getSelectedUser().team.assignedBy,
            getDiplayAssigmentType(viewModel.getSelectedUser().team.assignmentType)
        )


        binding.accessDefinedDate.text = toDateString(viewModel.getSelectedUser().team.assignedOn.toLong())
        binding.created.text = toDateString_FromAwsDateFormat(viewModel.getSelectedUser().cognitoUser.created)

        if (viewModel.getSelectedUser().cognitoUser.accountStatus.compareTo("CONFIRMED") == 0) {
            binding.confirmedUserDetails.visibility = View.VISIBLE
            binding.name.text = viewModel.getSelectedUser().cognitoUser.name
            binding.gender.text = viewModel.getSelectedUser().cognitoUser.gender
            binding.address.text = viewModel.getSelectedUser().cognitoUser.address
            binding.email.text = viewModel.getSelectedUser().cognitoUser.email
            binding.phoneNumber.text = viewModel.getSelectedUser().cognitoUser.phoneNumber
        } else {
            binding.confirmedUserDetails.visibility = View.GONE
        }

        binding.disableUser.setOnClickListener{
            userAlertClient.showWaitDialog("Working...")
            viewModel.disableUser(
                viewModel.getSelectedUser().cognitoUser.userName,
                disableUserRequestHandler
            )
        }

        binding.enableUser.setOnClickListener{
            userAlertClient.showWaitDialog("Working...")
            viewModel.enableUser(viewModel.getSelectedUser().cognitoUser.userName, enableUserRequestHandler)
        }

        binding.deleteUser.setOnClickListener {
            showConfirmDeleteAction()
        }
    }

    private fun setActions() {
        if (viewModel.getSelectedUser().cognitoUser.isEnabled) {
            binding.disableUser.visibility = View.VISIBLE
            binding.enableUser.visibility = View.GONE
            binding.deleteUser.visibility = View.GONE
            binding.userStatusSpace.visibility = View.GONE
            binding.userStatusContainer.visibility = View.GONE
            binding.userStatus.text = ""
        } else {
            binding.disableUser.visibility = View.GONE
            binding.enableUser.visibility = View.VISIBLE
            binding.deleteUser.visibility = View.VISIBLE
            binding.userStatusSpace.visibility = View.VISIBLE
            binding.userStatusContainer.visibility = View.VISIBLE
            binding.userStatus.text = getString(R.string.disabled)
        }
    }

    private fun showConfirmDeleteAction() {
        val confirmActionCallback: SimpleHandler = object : SimpleHandler {
            override fun onSuccess() {
                userAlertClient.showWaitDialog("Deleting user...")
                viewModel.deleteUserFromDynamoDb(
                    viewModel.getSelectedUser().cognitoUser.userName,
                    deleteUserRequestHandler
                )
            }

            override fun onError(errorMessage: String?) {

            }

        }
        userAlertClient.showConfirmActionDialog(
            childFragmentManager,
            "Confirm Delete Action",
            "To confirm delete user action, type 'DELETE' in the field below to permanently delete the user",
            "DELETE",
            confirmActionCallback
        )
    }

    private var disableUserRequestHandler: RequestHandler = object : RequestHandler {
        override fun onSuccess() {
            userAlertClient.closeWaitDialog()
            viewModel.getSelectedUser().cognitoUser.isEnabled = false
            setActions()
        }

        override fun onError(message: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!", message, false)
        }

    }

    private var enableUserRequestHandler: RequestHandler = object : RequestHandler {
        override fun onSuccess() {
            userAlertClient.closeWaitDialog()
            viewModel.getSelectedUser().cognitoUser.isEnabled = true
            setActions()
        }

        override fun onError(message: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!", message, false)
        }

    }

    var deleteUserRequestHandler: RequestHandler = object : RequestHandler {
        override fun onSuccess() {
            viewModel.deleteUser(
                viewModel.getSelectedUser().cognitoUser.userName,
                deleteUserFromCognitoRequestHandler
            )
        }

        override fun onError(message: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!", message, false)
        }

    }

    var deleteUserFromCognitoRequestHandler: RequestHandler = object : RequestHandler {
        override fun onSuccess() {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("User Deleted!", "", true)
            viewModel.setLoadTeamListDataFlag(true)
        }

        override fun onError(message: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!", message, false)
        }
    }
}
