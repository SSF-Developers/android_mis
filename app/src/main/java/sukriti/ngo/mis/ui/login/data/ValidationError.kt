package sukriti.ngo.mis.ui.login.data

data class ValidationError(
    var errorMessage: String = "")
{
    companion object {
        enum class FieldNames {
            Name,
            Gender,
            Password,
            RepeatPassword,
            Address,
            PhoneNumber,
            Email
        }
    }
}
