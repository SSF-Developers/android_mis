package sukriti.ngo.mis.ui.tickets.fragments.viewList

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.databinding.TicketsMyTicketsBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.repository.entity.Ticket
import sukriti.ngo.mis.ui.tickets.TicketsViewModel
import sukriti.ngo.mis.ui.tickets.TicketsViewModel.Companion.NAV_RAISE_NEW_TICKET
import sukriti.ngo.mis.ui.tickets.adapters.TicketListAdapter
import sukriti.ngo.mis.ui.tickets.data.TicketDetailsData
import sukriti.ngo.mis.ui.tickets.data.TicketListData
import sukriti.ngo.mis.utils.*


class TicketListNormal : Fragment() {

    private lateinit var mThis: Fragment
    private lateinit var viewModel: TicketsViewModel
    private lateinit var binding: TicketsMyTicketsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var mNavigationHandler: NavigationHandler
    private var userTouchFlagStatusSelection = false


    companion object {
        private var listType = -1
        private var INSTANCE: TicketListNormal? = null

        fun getInstance(listType: Int): TicketListNormal {
            INSTANCE = TicketListNormal()
            Companion.listType = listType
//            if (INSTANCE == null) {
//                Log.i("__loadData", "usageReport:GraphicalReport()")
//                INSTANCE = TicketListNormal()
//                this.listType = listType
//            }
            return INSTANCE as TicketListNormal
        }
    }

    fun setNavigationHandler(handler: NavigationHandler) {
        mNavigationHandler = handler
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TicketsMyTicketsBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.statusSelection.setSelection(0)
        mThis = this as Fragment

    }

    private fun init() {
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireParentFragment()).get(TicketsViewModel::class.java)

        val items = Nomenclature.getTicketStatusList(listType)
        val adapter = ArrayAdapter(requireContext(), R.layout.item_duration_selection, items)
        binding.statusSelection.setOnTouchListener(statusSelectionTouchListener)
        binding.statusSelection.adapter = adapter
        binding.statusSelection.onItemSelectedListener = durationSelectionListener
        binding.statusSelection.setSelection(
            Nomenclature.getIndex(
                viewModel.getTicketStatusSelection(),
                items
            )
        )
        binding.statusSelectionContainer.setOnClickListener(View.OnClickListener {
            binding.statusSelection.performClick()
        })

        binding.raiseTicket.setOnClickListener(View.OnClickListener {
            mNavigationHandler.navigateTo(NAV_RAISE_NEW_TICKET)
        })

        setNoData("No tickets listed")
        userAlertClient.showWaitDialog("Fetching tickets...")
        viewModel.getTicketListData(listType,Handler(requireContext().mainLooper),listTicketsRequestHandler )

        if(listType == TicketsViewModel.NAV_RAISED_TICKETS){
            binding.raiseTicket.visibility = View.VISIBLE
        }
    }

    fun setNoData(message: String) {
        binding.grid.visibility = View.GONE
        binding.noDataContainer.noDataContainer.visibility = View.VISIBLE
        binding.noDataContainer.noDataLabel.text = message
    }



    private var listTicketsRequestHandler: RepositoryCallback<List<Ticket>> =
        RepositoryCallback { result ->
            userAlertClient.closeWaitDialog()

            if(result is _Result.Success<List<Ticket>>){
                val data = result.data  as List<Ticket>

                if(data.isNotEmpty()){
                    val gridLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    binding.grid.layoutManager = gridLayoutManager
                    val mTicketListAdapter = TicketListAdapter(data, ticketListInteractionHandler)
                    binding.grid.adapter = mTicketListAdapter

                    binding.grid.visibility = View.VISIBLE
                    binding.noDataContainer.noDataContainer.visibility = View.GONE
                }else{
                    userAlertClient.showDialogMessage("Error Alert","No tickets listed",false)
                    setNoData("No tickets listed")
                }

            } else {
                val err = result as _Result.Error<TicketListData>
                userAlertClient.showDialogMessage("Error Alert",err.message,false)
            }
        }



    var ticketListInteractionHandler: TicketListAdapter.TicketListInteractionHandler =
        object : TicketListAdapter.TicketListInteractionHandler {
            override fun onTicketSelected(index: Int, ticketDetailsData: Ticket?) {
                viewModel.selectedTicketData = ticketDetailsData as Ticket
                mNavigationHandler.navigateTo(TicketsViewModel.NAV_TICKET_HOME)
            }

        }

    var statusSelectionTouchListener: View.OnTouchListener = object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            userTouchFlagStatusSelection = true
            return false
        }
    }

    var durationSelectionListener: OnItemSelectedListener =
        object : OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i("_durationSelection", " - ")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, index: Int, p3: Long) {
                if (userTouchFlagStatusSelection) {
                    var selection = Nomenclature.getTicketStatusList()[index]
                    Log.i("_selection", "selection: $selection")
                    viewModel.setTicketStatusSelection(selection)
                    //loadData()
                    viewModel.getTicketListData(listType,Handler(requireContext().mainLooper),listTicketsRequestHandler)
                    userTouchFlagStatusSelection = false
                }
            }
        }

}
