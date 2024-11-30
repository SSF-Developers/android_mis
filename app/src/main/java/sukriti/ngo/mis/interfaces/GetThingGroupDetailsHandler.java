package sukriti.ngo.mis.interfaces;

import com.amazonaws.services.iot.model.ThingGroupMetadata;

import java.util.Map;

public interface GetThingGroupDetailsHandler {
     void onResult(String GroupName, String Description, Map<String, String> Attributes, ThingGroupMetadata metadata);
     void onError(String message);
}
