package sukriti.ngo.mis.dataModel

data class ValidationError(
    var index: Int = -1,
    var hasError: Boolean? = false,
    var message: String ="") {

}
