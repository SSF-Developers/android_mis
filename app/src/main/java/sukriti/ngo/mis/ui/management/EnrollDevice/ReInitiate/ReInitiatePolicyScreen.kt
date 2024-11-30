package sukriti.ngo.mis.ui.management.EnrollDevice.ReInitiate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sukriti.ngo.mis.databinding.ReInitiatePolicyScreenBinding

class ReInitiatePolicyScreen(
    val viewModel: ReInitiateViewModel
) : Fragment() {

    lateinit var binding: ReInitiatePolicyScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ReInitiatePolicyScreenBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



    companion object {

    }
}