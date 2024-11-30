package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces

import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Policy
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.PolicyListItem

interface PolicyDeleteCallback {

    fun onDeleteRequest(policy: PolicyListItem, position: Int)
}