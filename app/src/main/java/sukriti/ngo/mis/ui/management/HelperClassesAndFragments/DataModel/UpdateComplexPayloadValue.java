package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel;

import java.util.ArrayList;

public class UpdateComplexPayloadValue {
    ArrayList<Attributes> Attributes;
    String Name;

    public UpdateComplexPayloadValue() {

    }

    public ArrayList<Attributes> getAttributes() {
        return Attributes;
    }

    public void setAttributes(ArrayList<Attributes> attributes) {
        this.Attributes = attributes;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
