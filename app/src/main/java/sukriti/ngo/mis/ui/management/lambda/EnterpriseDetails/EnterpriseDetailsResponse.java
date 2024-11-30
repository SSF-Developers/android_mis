package sukriti.ngo.mis.ui.management.lambda.EnterpriseDetails;

import sukriti.ngo.mis.ui.management.data.Body;

public class EnterpriseDetailsResponse {
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
