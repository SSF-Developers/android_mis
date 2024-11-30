package sukriti.ngo.mis.ui.management.lambda.EnrollDevice;

import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Attribute;

public class RequestManagementLambda {
    String userName;
    String command;
    String stateCode;
    String districtCode;
//    String Name;
    String value;
    Attribute attribute;


    public RequestManagementLambda(String userName, String command, Attribute attribute) {
        this.userName = userName;
        this.command = command;
        this.attribute = attribute;
    }

    public RequestManagementLambda(String userName, String command, String stateCode, String districtCode) {
        this.userName = userName;
        this.command = command;
        this.stateCode = stateCode;
        this.districtCode = districtCode;
    }

    public RequestManagementLambda(String userName, String command, String value) {
        this.userName = userName;
        this.command = command;
        this.value = value;
    }
}
