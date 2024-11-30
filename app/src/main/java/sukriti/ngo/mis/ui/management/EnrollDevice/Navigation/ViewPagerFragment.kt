package sukriti.ngo.mis.ui.management.EnrollDevice.Navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sukriti.ngo.mis.R
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel


class ViewPagerFragment(viewModel: EnrollDeviceViewModel) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val view =  inflater.inflate(R.layout.fragment_view_pager, container, false)

/*        val fragmentList = arrayListOf(
            FragmentOne(),
            FragmentTwo(),
            FragmentThree(),
            FragmentFour()
        )

        val adapter = ViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)

        view.viewPager.adapter = adapter*/

        return view
    }

}