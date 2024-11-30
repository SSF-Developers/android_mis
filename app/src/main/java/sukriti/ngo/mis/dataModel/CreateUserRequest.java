package sukriti.ngo.mis.dataModel;

import sukriti.ngo.mis.ui.login.data.UserProfile;

public class CreateUserRequest {
    public String UserName, Password, OrganizationName, ClientName,Role;
    public CreateUserRequest(String UserName, String Password, String OrganizationName, String ClientName, String Role)
    {
        this.UserName = UserName;
        this.Password = Password;
        this.OrganizationName = OrganizationName;
        this.ClientName = ClientName;
        this.Role = Role;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getOrganizationName() {
        return OrganizationName;
    }

    public void setOrganizationName(String organizationName) {
        OrganizationName = organizationName;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }
}
