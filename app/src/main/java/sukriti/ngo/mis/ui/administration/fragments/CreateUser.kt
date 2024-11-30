package sukriti.ngo.mis.ui.administration.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel.CognitoUser
import sukriti.ngo.mis.dataModel.CreateUserRequest
import sukriti.ngo.mis.dataModel.ThingGroupDetails
import sukriti.ngo.mis.dataModel.ValidationResult
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.databinding.CreateUserBinding
import sukriti.ngo.mis.interfaces.AuthorisationHandler
import sukriti.ngo.mis.interfaces.ClientListHandler
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import sukriti.ngo.mis.ui.administration.AdministrationViewModel.Companion.NAV_DEFINE_ACCESS
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData
import sukriti.ngo.mis.ui.administration.data.ValidationError
import sukriti.ngo.mis.ui.administration.interfaces.FormSubmitHandler
import sukriti.ngo.mis.ui.administration.interfaces.RequestHandler
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.ClientListLambdaResult
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.GetClientListResultHandler
import sukriti.ngo.mis.ui.administration.lambda.DefineAccessLambdaRequest
import sukriti.ngo.mis.ui.login.data.UserProfile
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities
import sukriti.ngo.mis.utils.Utilities.getNameList
import java.util.ArrayList


class CreateUser : Fragment() {
    private lateinit var viewModel: AdministrationViewModel
    private lateinit var binding: CreateUserBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var sharedPrefsClient: SharedPrefsClient

    companion object {
        private var INSTANCE: CreateUser? = null

        fun getInstance(): CreateUser {
            Log.i("CreateUser", "getInstance()")
            return INSTANCE ?: CreateUser()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("CreateUser", "onAttach()")
        if (context is NavigationHandler) {
            navigationHandler = context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement NavigationHandler"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("CreateUser", "onCreateView()")
        binding = CreateUserBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Log.i("CreateUser", "onResume()")
    }

    private fun init() {
        Log.i("CreateUser", "init()")
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(AdministrationViewModel::class.java)
        Log.i("CreateUser", "viewModel initialized properly()")

        val items = viewModel.getCreateRoleList()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_role_selection, items)
        (binding.roleAct as? AutoCompleteTextView)?.setAdapter(adapter)
        (binding.roleAct as? AutoCompleteTextView)?.onItemClickListener = OnItemClickListener { parent, arg1, pos, id ->
                Log.i("CreateUser", items[pos])
                Utilities.hideKeypad(context, binding.client.editText)
            Log.i("CreateUser", "Keypad hidden")
            if (sharedPrefsClient.getUserDetails().role == UserProfile.Companion.UserRole.ClientAdmin ||
                    sharedPrefsClient.getUserDetails().role == UserProfile.Companion.UserRole.ClientSuperAdmin
                ) {
                Log.i("CreateUser", "Client Role = ${sharedPrefsClient.getUserDetails().role}")
                binding.client.editText?.setText(sharedPrefsClient.getUserDetails().organisation.client)
//                    binding.organisation.editText?.setText(sharedPrefsClient.getUserDetails().organisation.name)
                    binding.client.isEnabled = false
            }
            else if (items[pos].compareTo(UserProfile.getRoleName(UserProfile.Companion.UserRole.SuperAdmin)) == 0 ||
                    items[pos].compareTo(UserProfile.getRoleName(UserProfile.Companion.UserRole.VendorAdmin)) == 0 ||
                    items[pos].compareTo(UserProfile.getRoleName(UserProfile.Companion.UserRole.VendorManager)) == 0
                ) {
                Log.i("CreateUser", "items positioni = ${items[pos]}")
                binding.client.editText?.setText("SSF")
//                    binding.organisation.editText?.setText("Sukriti Social Foundation")
                    binding.client.isEnabled = false
                }
            else {
                    if (binding.client.editText?.text.toString().compareTo("SSF") == 0) {
                        binding.client.editText?.setText("")
//                        binding.organisation.editText?.setText("")
                        binding.client.isEnabled = true
                    }
                }

                binding.container1.visibility = View.VISIBLE
                binding.container2.visibility = View.VISIBLE
        }

        binding.login.setOnClickListener(View.OnClickListener {
            Utilities.hideKeypad(context, binding.client.editText)
            binding.userName.error = null
            binding.password.error = null
            binding.role.error = null
            binding.client.error = null
//            binding.organisation.error = null

            val userName = binding.userName.editText?.text.toString()
            val password = binding.password.editText?.text.toString()
            val role = binding.role.editText?.text.toString()
            val client = binding.client.editText?.text.toString()
//            rahul karn
            val org =  "STATE"     //binding.organisation.editText?.text.toString()

            val request = CreateUserRequest(userName, password, org, client, role)

            val handler: FormSubmitHandler = object : FormSubmitHandler {
                override fun onSuccess() {
                    val memberDetails = MemberDetailsData()
                    memberDetails.cognitoUser = CognitoUser(userName)
                    memberDetails.cognitoUser.role = binding.role.editText?.text.toString()
                    viewModel.setSelectedUser(memberDetails)
                    userAlertClient.closeWaitDialog()
                    viewModel.setLoadTeamListDataFlag(true)

                    if (UserProfile.getRole(request.Role) == UserProfile.Companion.UserRole.SuperAdmin) {
                        val clientSuperAdminDefineUserAccessRequestHandler: RequestHandler =
                            object : RequestHandler {
                                override fun onSuccess() {
                                    userAlertClient.closeWaitDialog()
                                    userAlertClient.showDialogMessage(
                                        "User Created",
                                        "Client Super Admin created successfully.",
                                        true
                                    )
                                }

                                override fun onError(message: String?) {
                                    userAlertClient.closeWaitDialog()
                                    userAlertClient.showDialogMessage(
                                        "Error Alert!",
                                        message,
                                        false
                                    )
                                }

                            }

                        val country = Country()
                        country.name = "INDIA";
                        country.recursive = 1
                        val jo = Utilities.getIotPolicyDocument(country,UserProfile.Companion.UserRole.SuperAdmin).toString()
                        val policyName = Utilities.getIotPolicyName(viewModel.getSelectedUser())
                        val request = DefineAccessLambdaRequest(
                            viewModel.getSelectedUser().cognitoUser.userName,
                            viewModel.getSelectedUser().cognitoUser.role,
                            Utilities.getUserAccessKeys(country),
                            policyName,
                            jo)
                        userAlertClient.showWaitDialog("Updating user access...")
                        viewModel.updateUserAccessTree(
                            request, viewModel.getSelectedUser(),
                            country, clientSuperAdminDefineUserAccessRequestHandler
                        )
                    }
                    else if (UserProfile.getRole(request.Role) == UserProfile.Companion.UserRole.ClientSuperAdmin) {
                        val clientSuperAdminDefineUserAccessRequestHandler: RequestHandler =
                            object : RequestHandler {
                                override fun onSuccess() {
                                    userAlertClient.closeWaitDialog()
                                    userAlertClient.showDialogMessage(
                                        "User Created",
                                        "Client Super Admin created successfully.",
                                        true
                                    )
                                }

                                override fun onError(message: String?) {
                                    userAlertClient.closeWaitDialog()
                                    userAlertClient.showDialogMessage(
                                        "Error Alert!",
                                        message,
                                        false
                                    )
                                }

                            }

                        val country = Country()
                        country.name = "INDIA";
                        country.recursive = 1
                        val jo = Utilities.getIotPolicyDocument(country,UserProfile.Companion.UserRole.ClientSuperAdmin).toString()
                        val policyName = Utilities.getIotPolicyName(viewModel.getSelectedUser())
                        val request = DefineAccessLambdaRequest(viewModel.getSelectedUser().cognitoUser.userName,
                            viewModel.getSelectedUser().cognitoUser.role,
                            Utilities.getUserAccessKeys(country),
                            policyName,
                            jo)
                        userAlertClient.showWaitDialog("Updating user access...")
                        viewModel.updateUserAccessTree(
                            request, viewModel.getSelectedUser(),
                            country, clientSuperAdminDefineUserAccessRequestHandler
                        )
                    }
                    else {
                        viewModel
                        userAlertClient.showDialogMessage(
                            "User Created",
                            "Would you like to define user access now?",
                            NAV_DEFINE_ACCESS,
                            navigationHandler
                        )
                    }
                }

                override fun onValidationError(errorList: HashMap<ValidationError.Companion.FieldNames, ValidationError>?) {
                    userAlertClient.closeWaitDialog()
                    if (errorList != null) {
                        for ((key, value) in errorList) {
                            if (key == ValidationError.Companion.FieldNames.UserName)
                                binding.userName.error = value.errorMessage

                            if (key == ValidationError.Companion.FieldNames.Password)
                                binding.password.error = value.errorMessage

                            if (key == ValidationError.Companion.FieldNames.Role)
                                binding.role.error = value.errorMessage

                            if (key == ValidationError.Companion.FieldNames.Client)
                                binding.client.error = value.errorMessage

//                            if (key == ValidationError.Companion.FieldNames.Organisation)
//                                binding.organisation.error = value.errorMessage
                        }
                    }
                }

                override fun onServerError(message: String?) {
                    userAlertClient.closeWaitDialog()
                    userAlertClient.showDialogMessage("Error Alert", message, false)
                }

            }

            userAlertClient.showWaitDialog("Creating...")
            viewModel.createUser(request, handler)
        })

        userAlertClient.showWaitDialog("Initializing client...")
        Log.i("CreateUser", "calling initProvisioningClient()")
        viewModel.initProvisioningClient(provisioningClientAuthHandler)
        Log.i("CreateUser", "init() completed")
    }

    private var provisioningClientAuthHandler: AuthorisationHandler =
        object : AuthorisationHandler {
            override fun onResult(Result: ValidationResult?) {
                userAlertClient.closeWaitDialog();
                Log.i("CreateUser", "calling getClientListHandler ${Result.toString()}")
                viewModel.getClientList(getClientListResultHandler)
            }
        }

    private var getClientListHandler: ClientListHandler = object : ClientListHandler {
        override fun onResult(list: ArrayList<ThingGroupDetails>?) {
            userAlertClient.closeWaitDialog()
            Log.i("CreateUser", "getClientListHandler()")

            val items = getNameList(list)
            val adapter = ArrayAdapter(requireContext(), R.layout.item_role_selection, items)
            (binding.clientAct as? AutoCompleteTextView)?.setAdapter(adapter)
            (binding.clientAct as? AutoCompleteTextView)?.onItemClickListener =
                OnItemClickListener { parent, arg1, pos, id ->
                    if (list != null) {
                        Utilities.hideKeypad(context, binding.client.editText)
//                        binding.organisation.editText?.setText(list[pos].AttributesMap["STATE"])
                    }
                }
        }

        override fun onError(err: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert", err, false)
        }
    }

    private var getClientListResultHandler: GetClientListResultHandler = object: GetClientListResultHandler {
        override fun onSuccess(result: ClientListLambdaResult?) {
            userAlertClient.closeWaitDialog()
            Log.i("CreateUser", "getClientListHandler()")
            val tempList = result?.list
            val items = getNameList(tempList)
            val adapter = ArrayAdapter(requireContext(), R.layout.item_role_selection, items)
            (binding.clientAct as? AutoCompleteTextView)?.setAdapter(adapter)
            (binding.clientAct as? AutoCompleteTextView)?.onItemClickListener =
                OnItemClickListener { parent, arg1, pos, id ->
                    if (tempList != null) {
                        Utilities.hideKeypad(context, binding.client.editText)
//                        binding.organisation.editText?.setText(list[pos].AttributesMap["STATE"])
                    }
                }
        }

        override fun onError(errorMessage: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert", errorMessage, false)
        }

    }
}
