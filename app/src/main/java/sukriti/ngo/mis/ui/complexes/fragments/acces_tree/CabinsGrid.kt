package sukriti.ngo.mis.ui.complexes.fragments.acces_tree

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import sukriti.ngo.mis.communication.ConnectionStatusService
import sukriti.ngo.mis.databinding.CabinsGridBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.administration.interfaces.RequestHandler
import sukriti.ngo.mis.ui.complexes.CabinDetailsActivity
import sukriti.ngo.mis.ui.complexes.ComplexesViewModel
import sukriti.ngo.mis.ui.complexes.adapters.CabinListAdapter
import sukriti.ngo.mis.ui.complexes.data.CabinDetailsData
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.ui.complexes.data.ConnectionResponse
import sukriti.ngo.mis.ui.complexes.interfaces.CabinSelectionHandler
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.Nomenclature.*
import sukriti.ngo.mis.utils.UserAlertClient
import java.sql.Connection

class CabinsGrid : BottomSheetDialogFragment(), CabinSelectionHandler {

    private lateinit var viewModel: ComplexesViewModel
    private lateinit var binding: CabinsGridBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var details: ComplexDetailsData
    private lateinit var gridType: String
    private lateinit var mwcCabinListAdapter: CabinListAdapter


    fun setDetails(complexDetails: ComplexDetailsData, type: String) {
        details = complexDetails
        gridType = type
    }

    fun setNavigationHandler(handler: NavigationHandler) {
        navigationHandler = handler
    }

    companion object {
        private var INSTANCE: CabinsGrid? = null

        fun getInstance(): CabinsGrid {
            return INSTANCE
                ?: CabinsGrid()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CabinsGridBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProvider(requireActivity()).get(ComplexesViewModel::class.java)

        val gridLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        when (gridType) {
            CABIN_TYPE_MWC -> {
                binding.header.title.text = "Male WC Cabins"
                binding.header.subTitle.text = "" + details.mwcCabins.size + " cabins listed"
                binding.grid.layoutManager = gridLayoutManager
                mwcCabinListAdapter = CabinListAdapter(context, details.mwcCabins, this)
                binding.grid.adapter = mwcCabinListAdapter
            }
            CABIN_TYPE_FWC -> {
                binding.header.title.text = "Female WC Cabins"
                binding.header.subTitle.text = "" + details.fwcCabins.size + "cabins listed"
                binding.grid.layoutManager = gridLayoutManager
                mwcCabinListAdapter = CabinListAdapter(context, details.fwcCabins, this)
                binding.grid.adapter = mwcCabinListAdapter
            }
            CABIN_TYPE_PWC -> {
                binding.header.title.text = "Physically Disabled WC Cabins"
                binding.header.subTitle.text = "" + details.pwcCabins.size + "cabins listed"
                binding.grid.layoutManager = gridLayoutManager
                mwcCabinListAdapter = CabinListAdapter(context, details.pwcCabins, this)
                binding.grid.adapter = mwcCabinListAdapter
            }
            CABIN_TYPE_MUR -> {
                binding.header.title.text = "Male Urinal Cabins"
                binding.header.subTitle.text = "" + details.murCabins.size + "cabins listed"
                binding.grid.layoutManager = gridLayoutManager
                mwcCabinListAdapter = CabinListAdapter(context, details.murCabins, this)
                binding.grid.adapter = mwcCabinListAdapter
            }
            CABIN_TYPE_BWT -> {
                binding.header.title.text = "BWT Cabins"
                binding.header.subTitle.text = "" + details.bwtCabins.size + "cabins listed"
                binding.grid.layoutManager = gridLayoutManager
                mwcCabinListAdapter = CabinListAdapter(context, details.bwtCabins, this)
                binding.grid.adapter = mwcCabinListAdapter
            }
        }
        ConnectionStatusService.connectionResponse.observe(requireActivity(), connectionObserver)
    }

    private var connectionObserver: Observer<ConnectionResponse> = Observer {
        val response = it as ConnectionResponse
        Log.i("connection", "connectionObserver: "+ Gson().toJson(response))
        val cabinName = Nomenclature.getCabinType(
            response.clientId.substring(
                response.clientId.length - 7,
                response.clientId.length
            )
        )


        when (cabinName) {
            CABIN_TYPE_MWC -> {
                for ((i, mwc) in details.mwcCabins.withIndex()) {
                    if (mwc.ThingName == response.clientId) {
                        details.mwcCabins[i].ConnectionStatus = getConnectionStat(response.eventType,mwc.ConnectionStatus)
                        mwcCabinListAdapter.updateStatus(details.mwcCabins)
                    }
                }
            }
            CABIN_TYPE_FWC -> {
                for ((i, mwc) in details.fwcCabins.withIndex()) {
                    if (mwc.ThingName == response.clientId) {
                        details.fwcCabins[i].ConnectionStatus = getConnectionStat(response.eventType,mwc.ConnectionStatus)
                        mwcCabinListAdapter.updateStatus(details.fwcCabins)
                    }
                }
            }
            CABIN_TYPE_PWC -> {
                for ((i, mwc) in details.pwcCabins.withIndex()) {
                    if (mwc.ThingName == response.clientId) {
                        details.pwcCabins[i].ConnectionStatus = getConnectionStat(response.eventType,mwc.ConnectionStatus)
                        mwcCabinListAdapter.updateStatus(details.pwcCabins)
                    }
                }
            }
            CABIN_TYPE_MUR -> {
                for ((i, mwc) in details.murCabins.withIndex()) {
                    if (mwc.ThingName == response.clientId) {
                        details.murCabins[i].ConnectionStatus = getConnectionStat(response.eventType,mwc.ConnectionStatus)
                        mwcCabinListAdapter.updateStatus(details.murCabins)
                    }
                }
            }
            CABIN_TYPE_BWT -> {
                for ((i, mwc) in details.bwtCabins.withIndex()) {
                    if (mwc.ThingName == response.clientId) {
                        details.bwtCabins[i].ConnectionStatus = getConnectionStat(response.eventType,mwc.ConnectionStatus)
                        mwcCabinListAdapter.updateStatus(details.bwtCabins)
                    }
                }
            }
        }
    }

    private fun getConnectionStat(eventType :String, currentStatus : String): String {
        var connection = currentStatus
        if (eventType == "connected") {
            connection = "ONLINE"
        } else if (eventType =="disconnected"){
            connection = "OFFLINE"
        }
        return connection
    }
    override fun onCabinSelected(cabin: CabinDetailsData?) {
        if (cabin != null) {
            userAlertClient.showWaitDialog("Loading...")
            Log.i("_setSelection", details.ComplexName)
            viewModel.setSelection(details, cabin, setCabinSelectionHandler)
        }
    }

    private var setCabinSelectionHandler: RequestHandler = object : RequestHandler {
        override fun onSuccess() {
            userAlertClient.closeWaitDialog()
            startActivity(Intent(context, CabinDetailsActivity::class.java))
        }

        override fun onError(message: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert", message, false)
        }
    }
}