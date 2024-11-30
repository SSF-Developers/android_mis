package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel;

import java.util.ArrayList;

public class RegisterCabinPayload {
    String userName, command;
    RegisterCabinPayloadValue value;

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

    public RegisterCabinPayloadValue getValue() {
        return value;
    }

    public void setValue(RegisterCabinPayloadValue value) {
        this.value = value;
    }
}
