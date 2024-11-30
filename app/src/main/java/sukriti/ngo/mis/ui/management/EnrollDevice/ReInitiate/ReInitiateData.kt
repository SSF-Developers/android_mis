package sukriti.ngo.mis.ui.management.EnrollDevice.ReInitiate

import com.google.gson.JsonObject
import sukriti.ngo.mis.ui.complexes.fragments.CabinDetails
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ComplexDetails
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Policy
import sukriti.ngo.mis.ui.management.data.device.ApplicationDetails

data class ReInitiateData(
    var policy: String = "",
    var selectedEnterprise: String = "",
    var serialNumber: String = "",
    var appDetails: ApplicationDetails = ApplicationDetails(), // To be changed to separate AppDetails
    var qrUrl: String = ""
)
