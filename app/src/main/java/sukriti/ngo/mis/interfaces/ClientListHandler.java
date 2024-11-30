package sukriti.ngo.mis.interfaces;

import java.util.ArrayList;

import sukriti.ngo.mis.dataModel.ThingGroupDetails;
import sukriti.ngo.mis.dataModel.ValidationResult;

public interface ClientListHandler {
    public void onResult(ArrayList<ThingGroupDetails> list);
    public void onError(String err);
}
