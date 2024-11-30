package sukriti.ngo.mis.ui.login.data

data class LoginResult(
    var status: Int = STATUS_NULL,
    var message: String? = "") {

    companion object {
        const val STATUS_NULL = 0
        const val STATUS_SUCCESS = 1
        const val STATUS_NEW_PASSWORD_REQUIRED = 2
        const val STATUS_AUTO_LOGIN = 3
        const val STATUS_FAILURE = -1
    }
}
