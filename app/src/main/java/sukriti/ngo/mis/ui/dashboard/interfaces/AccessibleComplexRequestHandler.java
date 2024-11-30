package sukriti.ngo.mis.ui.dashboard.interfaces;

import java.util.List;

import sukriti.ngo.mis.dataModel.dynamo_db.Complex;

public interface AccessibleComplexRequestHandler {
    public void onSuccess(List<Complex> data);
    public void onError(String message);
}
