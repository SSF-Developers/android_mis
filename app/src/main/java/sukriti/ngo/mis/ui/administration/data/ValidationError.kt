package sukriti.ngo.mis.ui.administration.data

data class ValidationError(
    var errorMessage: String = "")
{
    companion object {
        enum class FieldNames {
            UserName,
            Password,
            Role,
            Client,
            Organisation
        }
    }
}
