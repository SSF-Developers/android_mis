package sukriti.ngo.mis.interfaces;

import sukriti.ngo.mis.dataModel.ValidationResult;
import sukriti.ngo.mis.repository.data.LatestTimestampData;

public interface LatestTimestampRequestHandler {
    public void onResult(LatestTimestampData data);
}
