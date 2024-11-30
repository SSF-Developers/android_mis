package sukriti.ngo.mis.ui.reports.lambda.report_fetchDateWaiseUsageData;

import java.util.ArrayList;

public class ReportLambdaRequest {

    public ArrayList<String> getComplex() {
        return complex;
    }

    public void setComplex(ArrayList<String> complex) {
        this.complex = complex;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<String> complex;
    public String duration;
    public String userName;

    public ReportLambdaRequest(ArrayList<String> complex, String duration, String userName) {
        this.complex = complex;
        this.duration = duration;
        this.userName = userName;
    }

    public ReportLambdaRequest() {

    }


}
