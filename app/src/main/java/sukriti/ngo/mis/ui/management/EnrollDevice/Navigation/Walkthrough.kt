package sukriti.ngo.mis.ui.management.EnrollDevice.Navigation

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import sukriti.ngo.mis.R
import sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.FragOne
import sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.FragmentFive
import sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.FragmentFour
import sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.FragmentThree
import sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.FragmentTwo
import sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.QRScreen
import sukriti.ngo.mis.ui.management.EnrollDevice.Interface.RefreshDeviceList
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel
import sukriti.ngo.mis.ui.management.ManagementViewModel
import sukriti.ngo.mis.utils.LambdaClient

class Walkthrough(
    context: Context,
    private val cfm: FragmentManager,
    private val cycleLife: Lifecycle,
    val viewModel: EnrollDeviceViewModel,
    val managementViewModel: ManagementViewModel,
    val lambdaClient: LambdaClient,
    val refreshDeviceList: RefreshDeviceList
) : DialogFragment(), OnClickListener, ViewPagerControl {

    private lateinit var viewPager: ViewPager2
    var jumpToScreen = 0;
    //    private lateinit var next: Button
//    private lateinit var prev: Button
    private lateinit var cancel: ImageView
    companion object {
        private const val TAG = "walkthrough"
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.walkthrough, container, false)
        Log.d(
            TAG,
            "Walkthrough: onCreateView() called with: inflater = $inflater, container = $container, savedInstanceState = $savedInstanceState"
        )
        viewPager = view.findViewById(R.id.walkthrough_pager)
//        viewPager.isUserInputEnabled = false
        viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT

//        next = view.findViewById(R.id.walkthrough_next)
//        prev = view.findViewById(R.id.walkthrough_prev)
        cancel = view.findViewById(R.id.cancelDialog)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(
            TAG,
            "onViewCreated() called"
        )

        val one = FragOne(viewModel, managementViewModel, lambdaClient)
        one.setInteractionListener(this@Walkthrough)
        val two = FragmentTwo(viewModel, lambdaClient)
        two.interactionListener = this@Walkthrough
        val three = FragmentThree(viewModel, managementViewModel, lambdaClient)
        three.interactionListener = this@Walkthrough
        val four = FragmentFour(lambdaClient, viewModel, managementViewModel)
        four.interactionListener = this@Walkthrough
        val five = FragmentFive(managementViewModel, lambdaClient, viewModel)
        five.interactionListener = this@Walkthrough
        val six = QRScreen(viewModel)
        six.interactionListener = this@Walkthrough

        val fragmentList = arrayListOf(
            one,
            two,
            three,
            four,
            five,
            six
        )

        var adapter : WalkthroughAdapter? = WalkthroughAdapter(cfm, fragmentList, cycleLife)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false
        viewPager.offscreenPageLimit = 6

        viewPager.currentItem = jumpToScreen

        cancel.setOnClickListener {
            Log.d(TAG, "Walkthrough: cancel button clicked")
            viewModel.resetViewModelState()
            one.setInteractionListener(null)
            two.interactionListener = null
            three.interactionListener = null
            four.interactionListener = null
            five.interactionListener = null
            six.interactionListener = null

            destroyFragments(one)

            destroyFragments(two)
            destroyFragments(three)
            destroyFragments(four)
            destroyFragments(five)
            destroyFragments(six)
            adapter = null;
            refreshDeviceList.refreshList()
            dismiss()

        }

    }

    override fun onClick(view: View?) {

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Walkthrough: onResume() called")
        val viewGrp = dialog?.window?.attributes
        viewGrp?.width = LayoutParams.MATCH_PARENT
        viewGrp?.height = LayoutParams.MATCH_PARENT

        dialog?.window?.attributes = viewGrp
    }

    override fun goToNextPage() {
        Log.d(TAG, "Walkthrough: goToNextPage() called")

        when (viewPager.currentItem) {
            0 -> {
                viewPager.currentItem = 1
//                prev.visibility = View.VISIBLE
            }

            1 -> viewPager.currentItem = 2

            2 -> {
                viewPager.currentItem = 3
            }

            3 -> {
                viewPager.currentItem = 4
//                next.visibility = View.GONE
            }
            4 -> {
                viewPager.currentItem = 5
            }
        }
    }

    override fun goToPrevPage() {
        Log.d(TAG, "Walkthrough: goToPrevPage() called")
        when (viewPager.currentItem) {
            1 -> {
                viewPager.currentItem = 0
//                prev.visibility = View.GONE
            }

            2 -> viewPager.currentItem = 1
            3 -> {
                viewPager.currentItem = 2
            }
            4 -> {
                viewPager.currentItem = 3
//                next.visibility = View.VISIBLE
            }

            5 -> {
                viewPager.currentItem = 4
            }
        }
    }

    private fun destroyFragments(fragment: Fragment) {
        fragment.onPause()
        fragment.onStop()
        fragment.onDestroyView()
        fragment.onDestroy()
    }

    interface CheckStepCompleted {
        fun stepNotCompleted()
    }

}
