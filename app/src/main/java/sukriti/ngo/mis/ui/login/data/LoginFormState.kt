package sukriti.ngo.mis.ui.login.data

data class LoginFormState(
    var userName: String = "",
    var password: String = "",
    var usernameError: String = "",
    var passwordError: String = "",
    var isPasswordValid: Boolean = false,
    var isUserNameValid: Boolean = false,
    var isValidated: Boolean = false
)
