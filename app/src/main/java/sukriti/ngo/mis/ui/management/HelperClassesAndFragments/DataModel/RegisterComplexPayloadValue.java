package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel;

import java.util.ArrayList;

public class RegisterComplexPayloadValue {
    String Name, Description, Parent;
    ArrayList<Attributes> Attributes;


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getParent() {
        return Parent;
    }

    public void setParent(String parent) {
        Parent = parent;
    }

    public ArrayList<Attributes> getAttributes() {
        return Attributes;
    }

    public void setAttributes(ArrayList<Attributes> attributes) {
        this.Attributes = attributes;
    }
}
