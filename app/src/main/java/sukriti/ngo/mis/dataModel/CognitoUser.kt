package sukriti.ngo.mis.dataModel

import com.amazonaws.services.cognitoidentityprovider.model.AdminGetUserResult
import com.amazonaws.services.cognitoidentityprovider.model.AttributeType

data class CognitoUser(
    var userName: String,
    var accountStatus: String,
    var isEnabled : Boolean,
    var role: String,
    var client: String,
    var organisation: String,
    var lastModified: String,
    var created: String,
    var name: String,
    var gender: String,
    var address: String,
    var phoneNumber: String,
    var email: String) {

    constructor(_userName: String, _accountStatus:String,_isEnabled:Boolean,_role:String,_client: String,_organisation:String,_lastModified:String,_created:String) :
            this(_userName,_accountStatus,_isEnabled,
        _role,_client,_organisation,_lastModified,_created,"","","","","") {

    }

    constructor(_userName: String) : this(_userName,"",true,"","","","","","","","","","") {

    }
}
