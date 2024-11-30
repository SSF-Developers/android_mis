package sukriti.ngo.mis.interfaces;


import java.util.ArrayList;

import sukriti.ngo.mis.dataModel.ThingGroupDetails;

public interface GetThingGroupChildrenHandler {
     void onResult(String Parent, ArrayList<ThingGroupDetails> List, int Type);
     void onError(String message);
}
