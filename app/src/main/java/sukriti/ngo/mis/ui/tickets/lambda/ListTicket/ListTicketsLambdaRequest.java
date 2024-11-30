package sukriti.ngo.mis.ui.tickets.lambda.ListTicket;

import sukriti.ngo.mis.ui.login.data.UserProfile;

public class ListTicketsLambdaRequest {
    String userId;
    String userRole;

    public ListTicketsLambdaRequest() {

    }

    public ListTicketsLambdaRequest(String userId, String userRole) {
        this.userId = userId;
        this.userRole = userRole;
    }

    public ListTicketsLambdaRequest(UserProfile userProfile) {
        this.userId = userProfile.getUser().getUserName();
        this.userRole = UserProfile.Companion.getRoleName(userProfile.getRole());
//        this.userId = "dev_000000";
//        this.userRole = "Super Admin";
    }


    public String getUserId(){return userId;}

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
