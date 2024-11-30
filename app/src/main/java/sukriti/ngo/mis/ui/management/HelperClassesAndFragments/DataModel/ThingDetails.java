package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel;

import java.util.ArrayList;
import java.util.Map;

public class ThingDetails {
    public boolean isSelected = false;
    public String Arn;
    public String Name;
    public String Id;
    public String DefaultClientId;
    public String ThingType;
    public String ThingGroup;
    public String BillingGroup;
    public Long Version;
    public ArrayList<Attributes> Attributes;
    public Map<String, String> AttributesMap;

    public void setAttributes(ArrayList<sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Attributes> attributes) {
        Attributes = attributes;
    }

    public void setAttributesMap(Map<String, String> attributesMap) {
        AttributesMap = attributesMap;
    }

    public ArrayList<sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Attributes> getAttributes() {
        return Attributes;
    }

    public Map<String, String> getAttributesMap() {
        return AttributesMap;
    }

    public String getArn() {
        return Arn;
    }

    public void setArn(String arn) {
        Arn = arn;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDefaultClientId() {
        return DefaultClientId;
    }

    public void setDefaultClientId(String defaultClientId) {
        DefaultClientId = defaultClientId;
    }

    public String getThingType() {
        return ThingType;
    }

    public void setThingType(String thingType) {
        ThingType = thingType;
    }

    public String getThingGroup() {
        return ThingGroup;
    }

    public void setThingGroup(String thingGroup) {
        ThingGroup = thingGroup;
    }

    public String getBillingGroup() {
        return BillingGroup;
    }

    public void setBillingGroup(String billingGroup) {
        BillingGroup = billingGroup;
    }

    public Long getVersion() {
        return Version;
    }

    public void setVersion(Long version) {
        Version = version;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
