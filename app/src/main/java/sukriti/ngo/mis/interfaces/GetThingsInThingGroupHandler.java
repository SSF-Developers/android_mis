package sukriti.ngo.mis.interfaces;


import java.util.ArrayList;

import sukriti.ngo.mis.dataModel.ThingDetails;

public interface GetThingsInThingGroupHandler {
     void onResult(ArrayList<ThingDetails> List);
     void onError(String message);
}
