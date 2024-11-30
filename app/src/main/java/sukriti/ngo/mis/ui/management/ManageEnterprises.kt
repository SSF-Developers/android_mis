package sukriti.ngo.mis.ui.management

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_manage_enterprises.floatingActionBtnDelete
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.FragmentManageEnterprisesBinding
import sukriti.ngo.mis.interfaces.DialogActionHandler
import sukriti.ngo.mis.interfaces.DialogSingleActionHandler
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.administration.fragments.ManageTeam
import sukriti.ngo.mis.ui.management.adapters.EnterpriseAdapter
import sukriti.ngo.mis.ui.management.adapters.EnterpriseAdapter.EnterpriseItemClickListener
import sukriti.ngo.mis.ui.management.data.Enterprise
import sukriti.ngo.mis.ui.management.data.device.ContactInfo
import sukriti.ngo.mis.ui.management.lambda.CreateEnterprise.CreateEnterpriseLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.CreateEnterprise.CreateEnterpriseResponseHandler
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponseHandler
import sukriti.ngo.mis.ui.management.lambda.EnterpriseDetails.EnterpriseDetailsResponse
import sukriti.ngo.mis.ui.management.lambda.EnterpriseDetails.EnterpriseDetailsResponseHandler
import sukriti.ngo.mis.ui.management.lambda.ListDevices.ListDeviceLambdaResponse
import sukriti.ngo.mis.ui.management.lambda.ListDevices.ListDeviceResponseHandler
import sukriti.ngo.mis.ui.management.lambda.ListEnterprise.ListEnterpriseResponse
import sukriti.ngo.mis.ui.management.lambda.ListEnterprise.ListEnterpriseResponseHandler
import sukriti.ngo.mis.ui.reports.ReportsViewModel
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.UserAlertClient


class ManageEnterprises : Fragment() {

    private lateinit var binding : FragmentManageEnterprisesBinding
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var adapter: EnterpriseAdapter
    private var list: ArrayList<Enterprise> = ArrayList()
    private lateinit var lambdaClient: LambdaClient
    private lateinit var viewModel: ManagementViewModel
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var createEnterpriseCallback: createEnterpriseCall
    private lateinit var deleteEnterpriseCallback: deleteEnterpriseCall


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageEnterprisesBinding.inflate(layoutInflater)
        init()
        Log.i(Companion.TAG, "Manage Enterprise: onCreateView()")
        return binding.root
    }

    private fun init() {
        floatingActionButton = binding.floatingActionBtn
        userAlertClient = UserAlertClient(activity)
        lambdaClient = LambdaClient(activity?.applicationContext)
        viewModel = ViewModelProviders.of(requireActivity()).get(ManagementViewModel::class.java)

        if(viewModel.enterpriseList.size == 0) {
            getEnterpriseList()
        } else {
            setAdapter(viewModel.enterpriseList)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(Companion.TAG, "onViewCreated: ")
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
        Log.i(Companion.TAG, "onAttach")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: ")
        createEnterpriseCallback = object: createEnterpriseCall {
            override fun onSuccess(intent: Intent, url: String) {
                userAlertClient.closeWaitDialog()
                startActivity(intent)
            }

            override fun onError(message: String?) {
                userAlertClient.closeWaitDialog()
                userAlertClient.showDialogMessage("Error", message, false)
            }
        }

        val create: createEnterpriseCall

        floatingActionButton.setOnClickListener {
            Toast.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show()
            userAlertClient.showWaitDialog("Creating...")
            viewModel.createEnterprise(createEnterpriseCallback)

        }


        deleteEnterpriseCallback = object : deleteEnterpriseCall {
            override fun onSuccess(message: String) {
                userAlertClient.closeWaitDialog()
                binding.floatingActionBtnDelete.visibility = View.GONE
                binding.floatingActionBtn.visibility = View.VISIBLE
                val dialogActionHandler = DialogSingleActionHandler { getEnterpriseList() }
                userAlertClient.showDialogMessage("Delete", message, dialogActionHandler)
            }

            override fun onError(message: String) {
                userAlertClient.closeWaitDialog()
                userAlertClient.showDialogMessage("Delete", message, false)
            }

        }

        binding.floatingActionBtnDelete.setOnClickListener {
            // Call Delete lambda 1 by one

            viewModel.deleteEnterpriseList.forEach {
                Log.i("deleteEnterprise", "$it -> ${viewModel.enterpriseList[it].enterpriseDisplayName}")
                userAlertClient.showWaitDialog("Deleting enterprise")
                viewModel.deleteEnterprise(deleteEnterpriseCallback)
            }
        }

//        setAdapter(list)


    }

    override fun onResume() {
        super.onResume()
        Log.i(Companion.TAG, "onResume: ")
        binding.floatingActionBtn.visibility = View.VISIBLE
        binding.floatingActionBtnDelete.visibility = View.GONE
    }

    companion object {
        private var INSTANCE : ManageEnterprises? = null

        fun getInstance(): ManageEnterprises {
           return INSTANCE ?: ManageEnterprises()
        }

        private const val TAG = "ManageEnterprises"

    }

    private val itemClickListener: EnterpriseItemClickListener = object : EnterpriseItemClickListener {
            override fun onClick(enterprise: Enterprise?) {
                if (enterprise != null) {
                    viewModel.setSelectedEnterprise(enterprise)
                    userAlertClient.showWaitDialog("Getting info please wait...")
//                    viewModel.getDeviceList(deviceListCallback)
                    viewModel.getEnterpriseDetails(enterprise.name, enterpriseDetailsCallback)
//                    viewModel.getPoliciesList(policiesListCallback)

//                    Toast.makeText(context, "${enterprise.enterpriseDisplayName} selected", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(context, "Enterprise was null", Toast.LENGTH_SHORT).show()
                }
            }

/*
            override fun onLongClick(enterprise: Enterprise?) {

            }

            override fun showDelete(show: Boolean) {
                if(show) {
                    binding.floatingActionBtnDelete.visibility = View.VISIBLE
                    binding.floatingActionBtn.visibility = View.GONE
                } else {
                    binding.floatingActionBtnDelete.visibility = View.GONE
                    binding.floatingActionBtn.visibility = View.VISIBLE
                }
            }
*/
        }

    private fun setAdapter(list: ArrayList<Enterprise>) {
        adapter = EnterpriseAdapter(context, list, itemClickListener, viewModel)
        val layoutManager = LinearLayoutManager(context)
        binding.enterpriseRecyclerView.layoutManager = layoutManager
        binding.enterpriseRecyclerView.adapter = adapter


        binding.enterpriseListSize.text = list.size.toString()
    }

    private fun getEnterpriseList() {
        userAlertClient.showWaitDialog("Getting Enterprise list")

        val callback = object: ListEnterpriseResponseHandler {
            override fun onSuccess(response: ListEnterpriseResponse?) {
                userAlertClient.closeWaitDialog()
                setAdapter(viewModel.enterpriseList)
            }

            override fun onError(message: String?) {
                userAlertClient.closeWaitDialog()
                if(message.equals("No Enterprise Found")) {
                    binding.noEnterpriseFoundTv.visibility = View.VISIBLE
                    binding.enterpriseRecyclerView.visibility = View.GONE
                } else {
                    binding.noEnterpriseFoundTv.visibility = View.GONE
                    binding.enterpriseRecyclerView.visibility = View.VISIBLE
                    userAlertClient.showDialogMessage("Error", message, false)
                }
            }

        }

        viewModel.getEnterpriseList(callback)
    }

    val deviceListCallback = object: ListDeviceResponseHandler {
        override fun onSuccess(response: ListDeviceLambdaResponse) {
            viewModel.getPoliciesList(policiesListCallback)
        }

        override fun onError(message: String?) {
//            userAlertClient.closeWaitDialog()
//            userAlertClient.showDialogMessage("Error", message, false)
            viewModel.getPoliciesList(policiesListCallback)
        }

    }

    val policiesListCallback = object: ListPolicyLambdaResponseHandler {
        override fun onSuccess(response: ListPolicyLambdaResponse) {
            userAlertClient.closeWaitDialog()
//            viewModel.writePolicyToFile(response, context)
            navigationHandler.navigateTo(2) // Navigate To Manage Devices
        }

        override fun onError(message: String) {
            userAlertClient.closeWaitDialog()
//            userAlertClient.showDialogMessage("Error", message, false)

            navigationHandler.navigateTo(2)
        }
    }

    val enterpriseDetailsCallback = object: EnterpriseDetailsResponseHandler {
        override fun onSuccess(response: JsonObject) {
            val enterprise = Enterprise()
            val body = response.get("body").asJsonObject
            enterprise.name = body.get("name").asString
            enterprise.enterpriseDisplayName = body.get("enterpriseDisplayName").asString

            val contactInfo = ContactInfo()
            val contact = body.get("contactInfo").asJsonObject
//            contactInfo.contactEmail = contact.get("contactEmail").asString
            contactInfo.dataProtectionOfficerName = contact.get("dataProtectionOfficerName").asString
            contactInfo.dataProtectionOfficerEmail = contact.get("dataProtectionOfficerEmail").asString
            contactInfo.dataProtectionOfficerPhone = contact.get("dataProtectionOfficerPhone").asString
            contactInfo.euRepresentativeName = contact.get("euRepresentativeName").asString
            contactInfo.euRepresentativeEmail = contact.get("euRepresentativeEmail").asString
            contactInfo.euRepresentativePhone = contact.get("euRepresentativePhone").asString

            enterprise.contactInfo = contactInfo
            enterprise.state = body.get("state").asString
            try {
                enterprise.ttl = body.get("ttl").asLong
            } catch (exception: IllegalStateException) {
                Log.i("enterpriseDetails", "TTL is null")
            }
            viewModel.setSelectedEnterprise(enterprise)
            viewModel.getPoliciesList(policiesListCallback)

        }

        override fun onError(message: String) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error", "Something went wrong", false)
        }
    }
    interface createEnterpriseCall{

        fun onSuccess(intent: Intent, url: String)

        fun onError(message: String?)
    }

    interface deleteEnterpriseCall {
        fun onSuccess(message: String)

        fun onError(message: String)
    }

    override fun onPause() {
        super.onPause()
        userAlertClient.closeWaitDialog()
        Log.i(Companion.TAG, "onPause: ")
    }
    override fun onStop() {
        super.onStop()
        Log.i(Companion.TAG, "onStop: ")
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(Companion.TAG, "Manage Enterprise: onDestroyView")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.i(Companion.TAG, "onDestroy: ")
    }
}
