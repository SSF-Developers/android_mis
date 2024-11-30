package sukriti.ngo.mis.ui.tickets.fragments.raise

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.android.synthetic.main.login.*
import sukriti.ngo.mis.AWSConfig
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.databinding.TicketRaiseTicketBinding
import sukriti.ngo.mis.interfaces.DialogActionHandler
import sukriti.ngo.mis.interfaces.DialogSingleActionHandler
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.ui.administration.interfaces.ProvisioningTreeRequestHandler
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.ui.tickets.TicketsViewModel
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.ACTION_PICK_TICKET_FILES
import sukriti.ngo.mis.ui.tickets.adapters.TicketFilesAdapter
import sukriti.ngo.mis.ui.tickets.data.TicketDetailsData
import sukriti.ngo.mis.ui.tickets.data.TicketFileItem
import sukriti.ngo.mis.ui.tickets.interfaces.SubmitTicketHandler
import sukriti.ngo.mis.ui.tickets.lambda.CreateTicket.CreateTicketLambdaRequest
import sukriti.ngo.mis.utils.*
import sukriti.ngo.mis.utils.Nomenclature.TICKET_FOLDER_RAISE


class RaiseTicket : Fragment() {
    private lateinit var viewModel: TicketsViewModel
    private lateinit var binding: TicketRaiseTicketBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var navigationClient: NavigationClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var submitTicketHandler: SubmitTicketHandler
    private final var REQUEST_PERMISSION = 1

    companion object {
        private var INSTANCE: RaiseTicket? = null

        fun getInstance(): RaiseTicket {
            if (INSTANCE == null) {
                Log.i("__loadData", "usageReport:GraphicalReport()")
                INSTANCE =
                    RaiseTicket()
            }
            return INSTANCE as RaiseTicket
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
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
        Log.i("__profile", "profile");
        binding = TicketRaiseTicketBinding.inflate(layoutInflater)
        init()
        return binding.root
    }


    private fun init() {
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(TicketsViewModel::class.java)
        navigationClient = NavigationClient(childFragmentManager)

        val items = Nomenclature.getTicketCriticalLevelDisplayList()
        val adapter = ArrayAdapter(requireContext(), R.layout.item_role_selection, items)
        (binding.criticalityAct as? AutoCompleteTextView)?.setAdapter(adapter)
        (binding.criticalityAct as? AutoCompleteTextView)?.onItemClickListener =
            AdapterView.OnItemClickListener { parent, arg1, pos, id ->
                Log.i("tag", items[pos].toString())
                Utilities.hideKeypad(context, binding.description.editText)
            }

        binding.selectUnit.setOnClickListener{
            userAlertClient.showWaitDialog("Loading selection tree...")
            viewModel.loadAccessTreeForUser(
                sharedPrefsClient.getUserDetails().user.userName,
                provisioningTreeRequestHandler
            )
        }

        binding.uploadFile.setOnClickListener(View.OnClickListener {

            val mimeTypes = arrayOf("image/*", "video/*")
            val pickIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            pickIntent.type = "image/*,video/*"
            pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(pickIntent, ACTION_PICK_TICKET_FILES)
        })

        viewModel.selectedComplex.observe(viewLifecycleOwner, Observer {
            val selectedComplex = it ?: return@Observer
            binding.selectionDetailsContainer.visibility = View.VISIBLE
            binding.unit.editText?.setText("${selectedComplex.ComplexName}")
            binding.city.editText?.setText("${selectedComplex.CityName}")
            binding.district.editText?.setText("${selectedComplex.StateCode}: ${selectedComplex.DistrictName}")

            setError(binding.unit, false)
            setError(binding.city, false)
            setError(binding.district, false)
        })

        loadData()
    }

    fun loadData() {
        val accessTreeAdapter =
            TicketFilesAdapter(
                removeHandler,
                viewModel.getFileList()
            )
        binding.ticketFiles.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.ticketFiles.adapter = accessTreeAdapter
    }

    fun createTicket() {
        if (validateForm()) {
            val title = binding.title.editText?.text.toString()
            val description = binding.description.editText?.text.toString()
            val criticality = Nomenclature.getTicketCriticalLevelCodeList()[Nomenclature.getIndex(
                binding.criticality.editText?.text.toString(),
                Nomenclature.getTicketCriticalLevelDisplayList()
            )]

            val ticketDetails = TicketDetailsData(
                viewModel.selectedComplex.value as ComplexDetailsData,
                sharedPrefsClient.getUserDetails(),
                title,
                description,
                criticality
            )
            Log.i("_validateForm", Gson().toJson(ticketDetails))
            userAlertClient.showWaitDialog("Creating ticket...")
            viewModel.createTicket(
                CreateTicketLambdaRequest(
                    ticketDetails
                ), createTicketCallback)
        }
    }

    private fun validateForm(): Boolean {
        var isValidated = true;
        if (binding.unit.editText?.text.toString().isEmpty()) {
            isValidated = false
            binding.selectionDetailsContainer.visibility = View.VISIBLE
            setError(binding.unit, true)
            setError(binding.city, true)
            setError(binding.district, true)

        } else {
            setError(binding.unit, false)
            setError(binding.city, false)
            setError(binding.district, false)
        }
        if (binding.title.editText?.text.toString().isEmpty()) {
            isValidated = false
            setError(binding.title, true)
        } else {
            setError(binding.title, false)
        }

        if (binding.criticality.editText?.text.toString().isEmpty()) {
            isValidated = false
            setError(binding.criticality, true)
        } else {
            setError(binding.criticality, false)
        }

        return isValidated
    }

    private fun uploadTicketFiles() {
        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val credentialsProvider = CognitoCachingCredentialsProvider(
                context,
                AWSConfig.identityPoolID,
                AWSConfig.cognitoRegion
            )
            viewModel.uploadTicketFiles(credentialsProvider, uploadTicketFilesCallback,TICKET_FOLDER_RAISE)
        } else {
            userAlertClient.closeWaitDialog()
            requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun setError(textInput: TextInputLayout, flag: Boolean) {
        if (flag) {
            textInput.error = "*Required"
        } else {
            textInput.isErrorEnabled = false
        }
    }

    private var provisioningTreeRequestHandler: ProvisioningTreeRequestHandler =
        object : ProvisioningTreeRequestHandler {
            override fun onSuccess(mCountry: Country?) {
                if (mCountry != null) {
                    userAlertClient.closeWaitDialog()
                    ComplexSelection.getInstance()
                        .show(childFragmentManager, "selectionTree")
                } else {
                    userAlertClient.closeWaitDialog()
                }
            }

            override fun onError(message: String?) {
                userAlertClient.closeWaitDialog()
                Log.i("_misTest", "onError: prov handler raised ticket :"+message)
                userAlertClient.showDialogMessage("Error Alert", message, true);
            }
        }

    private var removeHandler: TicketFilesAdapter.RemoveHandler =
        TicketFilesAdapter.RemoveHandler { index ->
            viewModel.removeFile(index)
            loadData()
        }

    private var uploadTicketFilesCallback: RepositoryCallback<String> =
        RepositoryCallback<String> {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Ticket Submitted","Ticket successfully submitted, your reference id is: "+viewModel.newTicketId,true)
        }

    private var createTicketCallback: RepositoryCallback<String> =
        RepositoryCallback<String> { result ->
            if(result is _Result.Success<String>){
                Log.i("_createTicketCallback",result.data)
                viewModel.newTicketId = result.data
                if (viewModel.getFileListCount() > 0) {
                    uploadTicketFiles()
                }else{
                    userAlertClient.closeWaitDialog()
                    userAlertClient.showDialogMessage("Ticket Submitted","Ticket successfully submitted, your reference id is: "+viewModel.newTicketId,true)
                }
            }else{
                var error = result as _Result.Error<String>
                userAlertClient.closeWaitDialog()
                Log.i("_misTest", "onComplete: createTicketcallbak errcode :" + error.errorCode+"  err mssg : "+error.message +"  err excp:"+error.exception)
                userAlertClient.showDialogMessage("Error Alert",error.message,false)
            }
        }

    private fun hasPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                requireContext(),
                permission
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        return true
    }

    private fun requestPermissions(permission: String) {
        val permissions = arrayOf(permission)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(permission)) {
                var mDialogActionHandler: DialogActionHandler = object : DialogActionHandler {
                    override fun onPositiveAction() {
                        requestPermissions(permissions, REQUEST_PERMISSION);
                    }

                    override fun onNegativeAction() {
                    }

                }

                userAlertClient.showDialogMessage(
                    "Permission Request",
                    "To upload images with this ticket, please grant permissions to access images on this device.",
                    mDialogActionHandler
                )
            } else {
                requestPermissions(permissions, REQUEST_PERMISSION);
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                userAlertClient.showWaitDialog("Uploading ticket files...")
                uploadTicketFiles()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ACTION_PICK_TICKET_FILES -> {
                val uri: Uri? = data?.data
                if (uri != null) {
                    var fileType = TicketFileItem.Companion.FileType.Empty
                    MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(requireContext().contentResolver.getType(uri))
                        .toString()
                    val type = requireContext().contentResolver.getType(uri).toString()
                    Log.i("_FileType", "type: $type")

                    if (type.contains("image")) {
                        fileType = TicketFileItem.Companion.FileType.Photo
                    } else if (type.contains("video")) {
                        fileType = TicketFileItem.Companion.FileType.Video
                    }


                    var result = viewModel.addFile(
                        TicketFileItem(
                            fileType,
                            uri,
                            ""
                        )
                    )

                    if (result is _Result.Success) {
                        loadData()
                    } else {
                        result = result as _Result.Error
                        userAlertClient.showDialogMessage("Error Alert!", result.message, false)
                    }
                }
            }
        }
    }

    var mDialogActionHandler : DialogSingleActionHandler = DialogSingleActionHandler { requireActivity().finish() }
}
