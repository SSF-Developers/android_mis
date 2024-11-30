package sukriti.ngo.mis.ui.management.lambda.EnrollDevice

import com.google.gson.JsonObject

class ManagementLambdaRequest(var userName: String, var command: String) {

    // For Creating new Billing Group
    var Name: String = ""
    var Description = ""
    var value: String? = ""


    // For adding new District from DDB to IoT
    var attribute: JsonObject = JsonObject()
    constructor(userName: String, command: String, value: String?): this(userName, command) {
        this.value = value
    }

    constructor(userName: String, command: String, Name: String, Description: String): this(userName, command, null) {
        this.Name = Name
        this.Description = Description
    }

/*
    constructor(userName: String, command: String, attribute: JsonObject): this(userName, command, null ) {
        this.attribute = attribute

    }
*/

    constructor(userName: String, command: String, value: JsonObject): this(userName, command, null ) {
    }


}