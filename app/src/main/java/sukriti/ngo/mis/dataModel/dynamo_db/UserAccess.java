package sukriti.ngo.mis.dataModel.dynamo_db;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "MIS_ACCESS")
public class UserAccess {
    private String userId;
    private String userRole;
    private PermissionTree permissions;

    @DynamoDBHashKey(attributeName="user")
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @DynamoDBAttribute(attributeName="permissions")
    public PermissionTree getPermissions() {return permissions; }
    public void setPermissions(PermissionTree permissions) { this.permissions = permissions; }

    @DynamoDBAttribute(attributeName="userRole")
    public String getUserRole() {return userRole;}
    public void setUserRole(String userRole) {this.userRole = userRole;}

}
