package sukriti.ngo.mis.repository.data

import sukriti.ngo.mis.repository.entity.UsageProfile
import java.util.*

data class ComplexWiseUsageData(
    var fromDate: Date,
    var toDate: Date,
    var usageProfile: List<UsageProfile>,
    var usageProfileMwc: List<UsageProfile>,
    var usageProfileFwc: List<UsageProfile>,
    var usageProfilePwc: List<UsageProfile>,
    var usageProfileMur: List<UsageProfile>,
    var durationLabel: String
) {
}
