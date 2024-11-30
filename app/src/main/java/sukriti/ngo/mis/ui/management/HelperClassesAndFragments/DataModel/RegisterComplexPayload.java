package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel;

public class RegisterComplexPayload {
    String userName, command;
    RegisterComplexPayloadValue value;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public RegisterComplexPayloadValue getValue() {
        return value;
    }

    public void setValue(RegisterComplexPayloadValue value) {
        this.value = value;
    }
}
