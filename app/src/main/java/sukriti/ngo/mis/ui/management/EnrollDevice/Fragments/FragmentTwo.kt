package sukriti.ngo.mis.ui.management.EnrollDevice.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.FragmentTwoBinding
import sukriti.ngo.mis.ui.management.EnrollDevice.Navigation.ViewPagerControl
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ThingDetails
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.RefreshListCallback
import sukriti.ngo.mis.ui.management.adapters.CabinListAdapter
import sukriti.ngo.mis.ui.management.fargments.CabinDetailsDialog
import sukriti.ngo.mis.ui.management.fargments.CabinDetailsDialog.ClickCallback
import sukriti.ngo.mis.utils.LambdaClient
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient


class FragmentTwo(var viewModel: EnrollDeviceViewModel, var lambdaClient: LambdaClient) :
    Fragment() {

    private lateinit var binding: FragmentTwoBinding
    lateinit var sharedPrefsClient: SharedPrefsClient
    lateinit var userName: String
    lateinit var userAlertClient: UserAlertClient
    val adapter = CabinListAdapter()
    val thingsList = mutableListOf<String>()
    private val Debugging = "debugging"
    private var isExpanded = false

    private val fromBottomFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.from_bottom_fab)
    }

    private val toBottomFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.to_bottom_fab)
    }

    private val rotateClockWiseFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.rotate_clock_wise)
    }

    private val rotateAntiClockWiseFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.rotate_anit_clock_wise)
    }

    var interactionListener: ViewPagerControl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(Debugging, "onCreate: FragmentTwo")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        Log.i(Debugging, "onCreateView: FragmentTwo")
        binding = FragmentTwoBinding.inflate(inflater)

        binding.cabinListRecyclerView.layoutManager = LinearLayoutManager(context)

        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        userName = sharedPrefsClient.getUserDetails().user.userName

        binding.registerNewCabin.setOnClickListener {
            if (viewModel.complexDetails.complexName.isNotEmpty()) {
                val registerCabinFragment = RegisterCabin()
                registerCabinFragment.viewModel = viewModel
                registerCabinFragment.setRefreshListCallback(refreshCabinListCallback)
                registerCabinFragment.show(parentFragmentManager, "RegisterNewCabin")
            } else {
                userAlertClient.showDialogMessage(
                    "Select Complex",
                    "Please select complex in the previous step",
                    false
                )
            }
        }

        viewModel.cabinDetailsListLiveData.observe(viewLifecycleOwner, cabinDetailsListObserver)

/*
        binding.optionsButton.setOnClickListener {
            if (isExpanded) {
                shrinkFab()
            } else {
                expandFab()
            }
        }
*/

        binding.next.setOnClickListener {

            if (interactionListener != null) {
                if (viewModel.stepTwoCompleted) {

                    interactionListener?.goToNextPage()

                } else {
                    userAlertClient.showDialogMessage(
                        "Cabin not selected",
                        "Please select a cabin",
                        false
                    )
                }

            }

        }

        binding.back.setOnClickListener {
            interactionListener?.goToPrevPage()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(Debugging, "onViewCreated: FragmentTwo")
    }
/*
    private fun shrinkFab() {

//        binding.optionsButton.startAnimation(rotateAntiClockWiseFabAnim)
        binding.next.startAnimation(toBottomFabAnim)
        binding.registerNewCabin.startAnimation(toBottomFabAnim)
        binding.nextTV.startAnimation(toBottomFabAnim)
        binding.registerNewCabinTv.startAnimation(toBottomFabAnim)
        isExpanded = !isExpanded
    }
*/

/*
    private fun expandFab() {


//        binding.optionsButton.startAnimation(rotateClockWiseFabAnim)
        binding.next.startAnimation(fromBottomFabAnim)
        binding.registerNewCabin.startAnimation(fromBottomFabAnim)
        binding.nextTV.startAnimation(fromBottomFabAnim)
        binding.registerNewCabinTv.startAnimation(fromBottomFabAnim)


        isExpanded = !isExpanded
    }
*/

    override fun onStart() {
        super.onStart()
        Log.i(Debugging, " onStart(): Fragment Two")
        setAdapter()
    }

    fun setAdapter() {
        adapter.complexDetails = viewModel.complexDetails
        Log.i("FragTwo", "Complex Details ${Gson().toJson(viewModel.complexDetails)}")
        adapter.cabinList = viewModel.thingsList
        Log.i("FragTwo", "Cabin List ${Gson().toJson(viewModel.thingsList)}")
        adapter.cabinDetails = viewModel.thingDetailsList
        Log.i("FragTwo", "Cabin List details ${Gson().toJson(viewModel.thingDetailsList)}")
        adapter.clickListener = cabinListItemClickList
        adapter.clickCallback = clickCallback

        binding.cabinListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.cabinListRecyclerView.adapter = adapter


    }

    private val cabinListItemClickList = object : CabinListAdapter.CabinListItemClickListener {
        override fun onClick(details: ThingDetails) {
            prepareAndShowDetailsForm(details)
        }
    }

    private val clickCallback = ClickCallback {
        adapter.cabinDetails = viewModel.thingDetailsList
        Log.i("ClickCallback", "Data list updated")
        adapter.notifyDataSetChanged()
        Log.i("ClickCallback", "Data set change notified")
    }

    private val refreshCabinListCallback = object : RefreshListCallback {
        override fun refreshList() {
            viewModel.refreshCabinList()
        }
    }

    private var cabinDetailsListObserver = object : Observer<MutableList<ThingDetails>> {
        override fun onChanged(t: MutableList<ThingDetails>?) {
            adapter.cabinDetails = viewModel.thingDetailsList
            adapter.notifyDataSetChanged()
        }
    }

    private fun prepareAndShowDetailsForm(thingDetails: ThingDetails) {
        val cabinDetailsDialog = CabinDetailsDialog(viewModel.complexDetails, thingDetails)
        cabinDetailsDialog.clickCallback = clickCallback
        cabinDetailsDialog.setvViewmodel(viewModel)
        cabinDetailsDialog.show(parentFragmentManager, "CabinDetails")
    }

    override fun onResume() {
        super.onResume()
        Log.i(Debugging, "Fragment Two onResume()")
        setAdapter()
    }

    override fun onPause() {
        super.onPause()
        Log.i(Debugging, "onPause: FragmentTwo ")
    }

    override fun onStop() {
        super.onStop()
        Log.i(Debugging, "onStop: FragmentTwo ")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(Debugging, "onDestroyView: FragmentTwo ")
        viewModel.cabinDetailsListLiveData.removeObserver(cabinDetailsListObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(Debugging, "onDestroy: FragmentTwo ")
    }


}