package sukriti.ngo.mis.ui.administration.lambda;

import java.util.List;

import sukriti.ngo.mis.dataModel.dynamo_db.Country;
import sukriti.ngo.mis.dataModel.dynamo_db.PermissionTree;
import sukriti.ngo.mis.dataModel.dynamo_db.UserAccess;
import sukriti.ngo.mis.ui.administration.data.UserAccessKey;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.AccessTree;

public class DefineAccessLambdaRequest {
    String userName;
    String userRole;
    List<UserAccessKey> accessKeys;
    String policyDocument;
    String policyName;
//    Country accessTree;
    AccessTree accessTree;



    public DefineAccessLambdaRequest(String userName, String userRole,List<UserAccessKey> accessKeys,String policyName, String policyDocument) {
        this.policyName = policyName;
        this.policyDocument = policyDocument;
        this.userName = userName;
        this.userRole = userRole;
        this.accessKeys = accessKeys;
    }

    public DefineAccessLambdaRequest(String userName, String userRole,List<UserAccessKey> userAccessKeys, AccessTree accessTree) {
        this.userName = userName;
        this.userRole = userRole;
        this.accessKeys = userAccessKeys;
        this.accessTree = accessTree;
    }

    public DefineAccessLambdaRequest() {

    }

    public String getPolicyDocument() {
        return policyDocument;
    }

    public void setPolicyDocument(String policyDocument) {
        this.policyDocument = policyDocument;
    }

    public String getPolicyName() {
        return policyName;
    }
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public List<UserAccessKey> getUserAccessKeys() {
        return accessKeys;
    }

    public void setUserAccessKeys(List<UserAccessKey> userAccessKeys) {
        this.accessKeys = userAccessKeys;
    }

    public AccessTree getAccessTree() {
        return accessTree;
    }

    public void setAccessTree(AccessTree accessTree) {
        this.accessTree = accessTree;
    }
}
