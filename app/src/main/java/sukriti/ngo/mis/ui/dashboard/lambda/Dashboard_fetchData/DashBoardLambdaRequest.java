package sukriti.ngo.mis.ui.dashboard.lambda.Dashboard_fetchData;

import sukriti.ngo.mis.ui.dashboard.data.DashBoardRequestData;
import sukriti.ngo.mis.ui.tickets.data.TicketDetailsData;

public class DashBoardLambdaRequest {

    public String complex;
    public String duration;
    public String userName;

    public DashBoardLambdaRequest( String complex, String duration, String userName) {
        this.complex =complex;
        this.duration =duration;
        this.userName =userName;
    }

    public DashBoardLambdaRequest() {

    }

    public String getComplex() {
        return complex;
    }

    public void setComplex(String complex) {
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



}
