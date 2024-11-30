package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel;

public class UpdateComplexPayload {
    String userName, command;
    UpdateComplexPayloadValue value;

    public UpdateComplexPayload() {

    }

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

    public UpdateComplexPayloadValue getValue() {
        return value;
    }

    public void setValue(UpdateComplexPayloadValue value) {
        this.value = value;
    }
}
