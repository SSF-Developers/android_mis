package sukriti.ngo.mis.dataModel.dynamo_db;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "MIS_TEAM")
public class Team {
    private String admin;
    private String member;
    private String adminRole;
    private String memberRole;
    private String assignedBy;
    private String assignedOn;
    private String assignmentType;

    @DynamoDBHashKey(attributeName="admin")
    public String getAdmin() { return admin; }
    public void setAdmin(String admin) { this.admin = admin; }

    @DynamoDBAttribute(attributeName="member")
    public String getMember() {return member; }
    public void setMember(String member) { this.member = member; }

    @DynamoDBAttribute(attributeName="admin_role")
    public String getAdminRole() {return adminRole; }
    public void setAdminRole(String adminRole) { this.adminRole = adminRole; }

    @DynamoDBAttribute(attributeName="member_role")
    public String getMemberRole() {return memberRole; }
    public void setMemberRole(String memberRole) { this.memberRole = memberRole; }

    @DynamoDBAttribute(attributeName="assigned_by")
    public String getAssignedBy() {return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }

    @DynamoDBAttribute(attributeName="assigned_on")
    public String getAssignedOn() {return assignedOn; }
    public void setAssignedOn(String assignedOn) { this.assignedOn = assignedOn; }

    @DynamoDBAttribute(attributeName="assignment_type")
    public String getAssignmentType() {return assignmentType; }
    public void setAssignmentType(String assignmentType) { this.assignmentType = assignmentType; }

}
