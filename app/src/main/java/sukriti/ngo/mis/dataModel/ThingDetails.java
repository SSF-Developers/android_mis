package sukriti.ngo.mis.dataModel;

import java.util.ArrayList;
import java.util.Map;

public class ThingDetails {
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
}
