package sukriti.ngo.mis.dataModel

import sukriti.ngo.mis.repository.data.LatestTimestampData

data class DbSyncStatus(
    var lastTimestamp: LatestTimestampData, var isNewLogin: Boolean = true) {
}
