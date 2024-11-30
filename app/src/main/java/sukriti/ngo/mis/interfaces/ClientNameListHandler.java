package sukriti.ngo.mis.interfaces;

import java.util.ArrayList;

import sukriti.ngo.mis.dataModel.ThingGroupDetails;

public interface ClientNameListHandler {
    public void onResult(ArrayList<String> list);
    public void onError(String err);
}
