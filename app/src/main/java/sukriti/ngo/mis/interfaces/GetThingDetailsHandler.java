package sukriti.ngo.mis.interfaces;


import sukriti.ngo.mis.dataModel.ThingDetails;

public interface GetThingDetailsHandler {
     void onResult(ThingDetails thingDetails);
     void onError(String message);
}
