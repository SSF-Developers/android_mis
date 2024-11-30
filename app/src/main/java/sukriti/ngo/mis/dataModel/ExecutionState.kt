package sukriti.ngo.mis.dataModel

data class ExecutionState(
    var status: Int? = STATUS_NULL,
    var message: String? = "") {

    companion object {
        const val STATUS_NULL = 0
        const val STATUS_PROCESSING = 1
        const val STATUS_SUCCESS = 2
        const val STATUS_FAILURE= -1
    }
}
