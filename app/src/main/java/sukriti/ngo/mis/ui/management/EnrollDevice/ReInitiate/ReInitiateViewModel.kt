package sukriti.ngo.mis.ui.management.EnrollDevice.ReInitiate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.PolicyListItem
import sukriti.ngo.mis.utils.LambdaClient

class ReInitiateViewModel: ViewModel() {

    val reInitiateData = MutableLiveData<ReInitiateData>().apply { value = ReInitiateData() }
    var policyList = mutableListOf<PolicyListItem>()

    fun reset() {
        reInitiateData.value = ReInitiateData()
    }
}