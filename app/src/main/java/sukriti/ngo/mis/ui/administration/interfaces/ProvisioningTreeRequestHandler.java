package sukriti.ngo.mis.ui.administration.interfaces;

import sukriti.ngo.mis.dataModel.dynamo_db.Country;

public interface ProvisioningTreeRequestHandler {
    public void onSuccess(Country country);
    public void onError(String message);
}
