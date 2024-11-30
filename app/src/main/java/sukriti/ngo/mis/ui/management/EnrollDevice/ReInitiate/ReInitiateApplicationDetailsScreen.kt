package sukriti.ngo.mis.ui.management.EnrollDevice.ReInitiate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import sukriti.ngo.mis.R
import sukriti.ngo.mis.databinding.ReInitiateApplicationDetailsScreenBinding

// TODO: Rename parameter arguments, choose names that match

class ReInitiateApplicationDetailsScreen(
    val viewModel: ReInitiateViewModel
) : Fragment() {

    lateinit var binding: ReInitiateApplicationDetailsScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = ReInitiateApplicationDetailsScreenBinding.inflate(inflater)
        return binding.root
    }

    companion object {

    }
}