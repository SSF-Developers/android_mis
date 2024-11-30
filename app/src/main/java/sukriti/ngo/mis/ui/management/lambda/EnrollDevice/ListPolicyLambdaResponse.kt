package sukriti.ngo.mis.ui.management.lambda.EnrollDevice

import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Policy
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.PolicyListItem

class ListPolicyLambdaResponse {

    var statusCode: Int = 0
//    var body: List<Policy> = mutableListOf()
    var body: List<PolicyListItem> = mutableListOf()
}