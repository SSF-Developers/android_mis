package sukriti.ngo.mis.ui.administration.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.reports_home.loadAccessTree
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.databinding.ManageTeamBinding
import sukriti.ngo.mis.interfaces.NavigationHandler
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import sukriti.ngo.mis.ui.administration.AdministrationViewModel.Companion.NAV_CREATE_USER
import sukriti.ngo.mis.ui.administration.AdministrationViewModel.Companion.NAV_MEMBER_DETAILS
import sukriti.ngo.mis.ui.administration.AdministrationViewModel.Companion.NAV_GRANT_PERMISSION
import sukriti.ngo.mis.ui.administration.adapter.TeamAdapter
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData
import sukriti.ngo.mis.ui.administration.interfaces.DetailedTeamRequestHandler
import sukriti.ngo.mis.ui.administration.interfaces.ProvisioningTreeRequestHandler
import sukriti.ngo.mis.ui.reports.fragments.SelectionTree
import sukriti.ngo.mis.utils.AWSIotProvisioningClient
import sukriti.ngo.mis.utils.AccessTreeClient
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient


class ManageTeam : Fragment() {
    private lateinit var viewModel: AdministrationViewModel
    private lateinit var binding: ManageTeamBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var adapter: TeamAdapter
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var lsit : MutableList<MemberDetailsData>
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private  val TAG = "ManageTeam"

    companion object {
        private var INSTANCE: ManageTeam? = null

        fun getInstance(): ManageTeam {
            return INSTANCE
                ?: ManageTeam()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = ManageTeamBinding.inflate(layoutInflater)
        init()
        Log.i(TAG, "onCreateView: Manage Team")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated: Manage Team")
    }
    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: Manage Team")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(TAG, "onAttach: Manage Team")
        if (context is NavigationHandler) {
            navigationHandler = context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement NavigationHandler"
            )
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: Manage Team")
    }

    private fun init() {
        userAlertClient = UserAlertClient(activity)
        sharedPrefsClient = SharedPrefsClient(context)
        viewModel = ViewModelProviders.of(requireActivity()).get(AdministrationViewModel::class.java)

        binding.createUser.setOnClickListener {
            navigationHandler.navigateTo(NAV_CREATE_USER)
        }

        val userDetails = sharedPrefsClient.getUserDetails()
        Log.i("myUserDetails", Gson().toJson(userDetails))

        if(!userDetails.role.name.contains("Super Admin") && !userDetails.role.name.contains("SuperAdmin")) {
            binding.grantPermission.visibility = View.GONE
        } else {
            binding.grantPermission.setOnClickListener {
                navigationHandler.navigateTo(NAV_GRANT_PERMISSION)
            }
        }

        if(viewModel.getLoadTeamListDataFlag()){
            viewModel.setLoadTeamListDataFlag(false)
            userAlertClient.showWaitDialog("Getting team info...")
//            viewModel.getTeam(sharedPrefsClient.getUserDetails().user.userName,teamRequestHandler)
            viewModel.getTeamFromLambda(sharedPrefsClient.getUserDetails().user.userName,teamRequestHandler)
        }else{
            setAdapter()
        }
    }

    fun setAdapter(){
        adapter = TeamAdapter(context, lsit, mTeamItemClickListener)
        val gridLayoutManager = GridLayoutManager(context, 1)
        binding.teamRv.layoutManager = gridLayoutManager
        binding.teamRv.adapter = adapter
    //        binding.teamSize.text = ""+ (lsit?.size ?: "-")
        binding.teamSize.text = lsit.size.toString()

    }

    private var teamRequestHandler: DetailedTeamRequestHandler = object : DetailedTeamRequestHandler{
        override fun onSuccess(itemList: MutableList<MemberDetailsData>?) {
            userAlertClient.closeWaitDialog()

            if (itemList != null) {
                lsit = itemList
                setAdapter()
            }
        }

        override fun onError(message: String?) {
            userAlertClient.closeWaitDialog()
            userAlertClient.showDialogMessage("Error Alert!",message,false)
        }

    }


    private val mTeamItemClickListener : TeamAdapter.TeamItemClickListener = TeamAdapter.TeamItemClickListener { memberDetailsData ->
            if (memberDetailsData != null) {
                viewModel.setSelectedUser(memberDetailsData)
                navigationHandler.navigateTo(NAV_MEMBER_DETAILS)
            }
        }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause: Manage Team")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop: Manage Team")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "onDestroyView: Manage Team")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: Manage Team")
    }
}
