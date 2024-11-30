package sukriti.ngo.mis.ui.dashboard.repository.lambda.aggregateData;

import java.util.List;

public class CabinDataLambdaRequest {
    String clientCode;
    List<String> complexes;
    String timeStamp;
    String userName;
    String requestTimeStamp;

    public CabinDataLambdaRequest(String clientCode, List<String> complexes, String timeStamp) {
        this.clientCode = clientCode;
        this.complexes = complexes;
        this.timeStamp = timeStamp;
    }

    public CabinDataLambdaRequest() {

    }

    public String getClientCode() {
        return clientCode;
    }
    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public List<String> getComplexes() {
        return complexes;
    }
    public void setComplexes(List<String> complexes) {
        this.complexes = complexes;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRequestTimeStamp() {
        return requestTimeStamp;
    }
    public void setRequestTimeStamp(String requestTimeStamp) {
        this.requestTimeStamp = requestTimeStamp;
    }
}
