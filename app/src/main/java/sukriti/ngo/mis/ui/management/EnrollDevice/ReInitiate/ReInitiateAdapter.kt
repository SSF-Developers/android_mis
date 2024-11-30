package sukriti.ngo.mis.ui.management.EnrollDevice.ReInitiate

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import sukriti.ngo.mis.ui.management.ManagementViewModel

class ReInitiateAdapter(
    fragment: Fragment,
    val viewModel: ReInitiateViewModel
): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ReInitiatePolicyScreen(viewModel)
            1 -> ReInitiateApplicationDetailsScreen(viewModel)
            else -> ReInitiateQrScreen(viewModel)
        }
    }
}