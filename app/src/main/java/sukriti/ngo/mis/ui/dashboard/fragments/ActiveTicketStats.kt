package sukriti.ngo.mis.ui.super_admin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.login.*
import sukriti.ngo.mis.adapters.ActiveTicketsAdapter
import sukriti.ngo.mis.dataModel._Result
import sukriti.ngo.mis.databinding.DashboardActiveTicketsBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.interfaces.RepositoryCallback
import sukriti.ngo.mis.repository.entity.Ticket
import sukriti.ngo.mis.ui.dashboard.DashboardViewModel
import sukriti.ngo.mis.ui.dashboard.data.ActiveTicket
import sukriti.ngo.mis.ui.tickets.TicketsViewModel
import sukriti.ngo.mis.utils.NavigationClient
import sukriti.ngo.mis.utils.Nomenclature
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient

class ActiveTicketStats : Fragment() {
    private lateinit var binding: DashboardActiveTicketsBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private lateinit var viewModel: DashboardViewModel
    private lateinit var navigationClient: NavigationClient
    private lateinit var adapter: ActiveTicketsAdapter

    companion object {
        private var INSTANCE: sukriti.ngo.mis.ui.super_admin.fragments.ActiveTicketStats? = null

        fun getInstance(): sukriti.ngo.mis.ui.super_admin.fragments.ActiveTicketStats {
            return INSTANCE
                ?: ActiveTicketStats()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashboardActiveTicketsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel =
            ViewModelProviders.of(requireActivity()).get(DashboardViewModel::class.java)
        userAlertClient = UserAlertClient(activity)
        navigationClient = NavigationClient(childFragmentManager)
        sharedPrefsClient = SharedPrefsClient(context)

//        viewModel.listActiveTickets(requestHandler)

        binding.raiseTicket.setOnClickListener(View.OnClickListener {
            (requireActivity() as NavigationHandler).navigateTo(TicketsViewModel.NAV_RAISE_NEW_TICKET)
        })
        viewModel.activeTickets.observe(viewLifecycleOwner,activeTicketsObserver)
    }

    var activeTicketsObserver :Observer<ArrayList<ActiveTicket>> = Observer {
        val activeTickets = it as ArrayList<ActiveTicket>
        Log.i("dashboardLambda", "activeTicketsObserver: "+Gson().toJson(activeTickets))
        val result: _Result<List<Ticket>> = _Result.Success(viewModel.getListOfTicket(activeTickets) )
        requestHandler.onComplete(result)

    }

    var requestHandler: RepositoryCallback<List<Ticket>> =
        object : RepositoryCallback<List<Ticket>> {
            override fun onComplete(result: _Result<List<Ticket>>?) {

                if (result is _Result.Success<List<Ticket>>) {
                    var activeTicketList = result.data as List<Ticket>
                    if (activeTicketList.isNotEmpty()) {

                        if(activeTicketList.size > Nomenclature.DASHBOARD_CHILD_ITEM_LIMIT){
                            activeTicketList = activeTicketList.subList(0,Nomenclature.DASHBOARD_CHILD_ITEM_LIMIT)
                        }

                        binding.banner.root.visibility = View.GONE
                        binding.complexGrid.visibility = View.VISIBLE

                        val gridLayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                        binding.complexGrid.layoutManager = gridLayoutManager
                        adapter = ActiveTicketsAdapter(context, activeTicketList,ticketListInteractionHandler)
                        binding.complexGrid.adapter = adapter
                    } else {
                        binding.banner.root.visibility = View.VISIBLE
                        binding.complexGrid.visibility = View.GONE
                        binding.banner.label.text = "No active tickets listed"

                    }
                }
            }
        }

    var ticketListInteractionHandler: ActiveTicketsAdapter.TicketListInteractionHandler =
        object : ActiveTicketsAdapter.TicketListInteractionHandler {
            override fun onTicketSelected(index: Int, ticketDetailsData: Ticket?) {
                sharedPrefsClient.setSelectedTicket(ticketDetailsData as Ticket)

                (requireActivity() as NavigationHandler).navigateTo(TicketsViewModel.NAV_TICKET_HOME)
            }
        }
}