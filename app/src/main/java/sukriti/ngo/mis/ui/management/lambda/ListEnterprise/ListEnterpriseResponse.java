package sukriti.ngo.mis.ui.management.lambda.ListEnterprise;

import java.util.List;

import sukriti.ngo.mis.ui.management.data.Body;
import sukriti.ngo.mis.ui.management.data.Enterprise;

public class ListEnterpriseResponse {
    private int statusCode;
    private Body body;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }


}
