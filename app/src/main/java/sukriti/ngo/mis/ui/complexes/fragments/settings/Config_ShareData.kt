package sukriti.ngo.mis.ui.complexes.fragments.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.databinding.CabinDetailsBinding
import sukriti.ngo.mis.databinding.CabinHealthBinding
import sukriti.ngo.mis.databinding.ComplexesHomeBinding
import sukriti.ngo.mis.repository.entity.Cabin
import sukriti.ngo.mis.repository.entity.Complex
import sukriti.ngo.mis.ui.administration.AdministrationViewModel
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData
import sukriti.ngo.mis.ui.administration.interfaces.ProvisioningTreeRequestHandler
import sukriti.ngo.mis.ui.administration.interfaces.TreeInteractionListener
import sukriti.ngo.mis.ui.complexes.CabinViewModel
import sukriti.ngo.mis.ui.complexes.ComplexesViewModel
import sukriti.ngo.mis.ui.complexes.adapters.CabinListAdapter
import sukriti.ngo.mis.ui.complexes.adapters.PropertiesListAdapter
import sukriti.ngo.mis.ui.complexes.adapters.PropertiesListAdapterLA
import sukriti.ngo.mis.ui.complexes.adapters.StateAdapterMC
import sukriti.ngo.mis.ui.complexes.data.CabinDetailsData
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData
import sukriti.ngo.mis.ui.complexes.interfaces.CabinComplexSelectionHandler
import sukriti.ngo.mis.ui.complexes.interfaces.ComplexDetailsRequestHandler
import sukriti.ngo.mis.ui.complexes.interfaces.PropertiesListRequestHandler
import sukriti.ngo.mis.ui.login.data.UserProfile.Companion.isClientSpecificRole
import sukriti.ngo.mis.ui.profile.ProfileViewModel
import sukriti.ngo.mis.ui.profile.interfaces.UserProfileRequestHandler
import sukriti.ngo.mis.utils.SharedPrefsClient
import sukriti.ngo.mis.utils.UserAlertClient
import sukriti.ngo.mis.utils.Utilities

class Config_ShareData : Fragment() {

    private lateinit var viewModel: CabinViewModel
    private lateinit var binding: CabinHealthBinding
    private lateinit var userAlertClient: UserAlertClient
    private lateinit var sharedPrefsClient: SharedPrefsClient
    private var _tag: String = "_CabinDetails"

    companion object {
        private var INSTANCE: Config_ShareData? = null

        fun getInstance(): Config_ShareData {
            return INSTANCE
                ?: Config_ShareData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = CabinHealthBinding.inflate(layoutInflater)
        init()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }

    private fun init() {
        Log.i(_tag, "init()")
        sharedPrefsClient = SharedPrefsClient(context)
        userAlertClient = UserAlertClient(activity)
        viewModel = ViewModelProviders.of(requireActivity()).get(CabinViewModel::class.java)


        viewModel.getCabinShareConfig(viewModel.getSelectedCabin().ThingName, requestHandler)
    }

    var requestHandler: PropertiesListRequestHandler = object : PropertiesListRequestHandler {
        override fun getData(data: MutableList<PropertyNameValueData>?, TimeStamp: String) {

            if (data?.size == 0) {
                binding.grid.visibility = View.GONE
                binding.noDataContainer.visibility = View.VISIBLE
                binding.noDataLabel.text = "No data found for the selected cabin"
            } else {
                binding.grid.visibility = View.VISIBLE
                binding.noDataContainer.visibility = View.GONE
                Log.i(_tag, "requestHandler: " + data?.size)
                val gridLayoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.grid.layoutManager = gridLayoutManager
                var mwcCabinListAdapter = PropertiesListAdapterLA(context, data)
                binding.grid.adapter = mwcCabinListAdapter
            }
        }
    }
}
