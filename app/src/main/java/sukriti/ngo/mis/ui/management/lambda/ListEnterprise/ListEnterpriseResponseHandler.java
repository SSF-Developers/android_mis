package sukriti.ngo.mis.ui.management.lambda.ListEnterprise;

public interface ListEnterpriseResponseHandler {

    void onSuccess(ListEnterpriseResponse response);

    void onError(String message);

}
