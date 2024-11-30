package sukriti.ngo.mis.ui.login.data

import android.util.Log

data class UserProfile(
    var organisation: OrganisationDetails = OrganisationDetails(),
    var user: UserDetails = UserDetails(),
    var communication: CommunicationDetails = CommunicationDetails(),
    var role: UserRole = UserRole.Undefined)

{

    var accountStatus = ""
    var createdOn = ""
    companion object {
        enum class UserRole {
            SuperAdmin,
            VendorAdmin,
            ClientSuperAdmin,
            ClientAdmin,
            ClientManager,
            VendorManager,
            Undefined
        }

        fun getRoleName(role: UserRole): String{
            return when(role){
                UserRole.Undefined -> "Undefined"
                UserRole.SuperAdmin -> "Super Admin"
                UserRole.VendorAdmin -> "Vendor Admin"
                UserRole.ClientSuperAdmin -> "Client Super Admin"
                UserRole.ClientAdmin -> "Client Admin"
                UserRole.ClientManager -> "Client Manager"
                UserRole.VendorManager -> "Vendor Manager"
            }
        }

        fun getRole(roleName: String): UserRole{
            Log.i("getRole","$roleName")
            return when(roleName){
                "Undefined" -> UserRole.Undefined
                "Super Admin" -> UserRole.SuperAdmin
                "Vendor Admin" -> UserRole.VendorAdmin
                "Client Super Admin" -> UserRole.ClientSuperAdmin
                "Client Admin" -> UserRole.ClientAdmin
                "Client Manager" -> UserRole.ClientManager
                "Vendor Manager" -> UserRole.VendorManager

                else -> UserRole.Undefined
            }
        }

        fun isClientSpecificRole(role:UserRole):Boolean{
            return when(role){
                UserRole.ClientSuperAdmin -> true
                UserRole.ClientAdmin -> true
                UserRole.ClientManager -> true

                else -> false
            }
        }

        fun hasWriteAccess(role:UserRole):Boolean{
            return when(role){
                UserRole.ClientSuperAdmin -> true
                UserRole.ClientAdmin -> true
                UserRole.VendorAdmin -> true
                UserRole.SuperAdmin -> true

                else -> false
            }
        }
    }


    data class OrganisationDetails(
        var name: String = "",
        var client: String = "")

    data class UserDetails(
        var userName: String = "",
        var name: String = "",
        var gender: String = "",
        var password: String = "",
        var repeatPassword: String = "",
        var address: String = ""
    )


    data class CommunicationDetails(
        var email: String = "",
        var phoneNumber: String = "",
        var isPhoneNumberVerified: Boolean = false,
        var isEmailVerified: Boolean = false
    )
}
