package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel;

import java.util.ArrayList;

public class RegisterCabinPayloadValue {
    String BillingGroup = "";
    String DefaultClientId = "";
    String Name = "";
    String ThingGroup = "";
    String ThingType = "";


    ArrayList<Attributes> Attributes;

    public ArrayList<sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Attributes> getAttributes() {
        return Attributes;
    }

    public void setAttributes(ArrayList<sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Attributes> attributes) {
        Attributes = attributes;
    }

    public String getBillingGroup() {
        return BillingGroup;
    }

    public void setBillingGroup(String billingGroup) {
        BillingGroup = billingGroup;
    }

    public String getDefaultClientId() {
        return DefaultClientId;
    }

    public void setDefaultClientId(String defaultClientId) {
        DefaultClientId = defaultClientId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getThingGroup() {
        return ThingGroup;
    }

    public void setThingGroup(String thingGroup) {
        ThingGroup = thingGroup;
    }

    public String getThingType() {
        return ThingType;
    }

    public void setThingType(String thingType) {
        ThingType = thingType;
    }
}
