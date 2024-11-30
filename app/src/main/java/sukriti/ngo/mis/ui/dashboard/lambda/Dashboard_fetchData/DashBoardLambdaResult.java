package sukriti.ngo.mis.ui.dashboard.lambda.Dashboard_fetchData;

import java.util.ArrayList;

import sukriti.ngo.mis.ui.dashboard.data.ActiveTicket;
import sukriti.ngo.mis.ui.dashboard.data.BwtdashboardChartData;
import sukriti.ngo.mis.ui.dashboard.data.BwtdataSummary;
import sukriti.ngo.mis.ui.dashboard.data.BwtpieChartData;
import sukriti.ngo.mis.ui.dashboard.data.ConnectionStatus;
import sukriti.ngo.mis.ui.dashboard.data.DashboardChartData;
import sukriti.ngo.mis.ui.dashboard.data.DataSummary;
import sukriti.ngo.mis.ui.dashboard.data.FaultyComplex;
import sukriti.ngo.mis.ui.dashboard.data.LowWaterComplex;
import sukriti.ngo.mis.ui.dashboard.data.PieChartData;
import sukriti.ngo.mis.ui.dashboard.data.UiResult;

public class DashBoardLambdaResult {

    public DashboardChartData dashboardChartData;
    public PieChartData pieChartData;
    public DataSummary dataSummary;
    public ArrayList<FaultyComplex> faultyComplexes;
    public ArrayList<ConnectionStatus> connectionStatus;
    public ArrayList<LowWaterComplex> lowWaterComplexes;
    public ArrayList<ActiveTicket> activeTickets;
    public UiResult uiResult;
    public BwtdashboardChartData bwtdashboardChartData;
    public BwtpieChartData bwtpieChartData;
    public BwtdataSummary bwtdataSummary;

    public DashBoardLambdaResult(DashboardChartData dashboardChartData, PieChartData pieChartData, DataSummary dataSummary, ArrayList<FaultyComplex> faultyComplexes, ArrayList<ConnectionStatus> connectionStatus, ArrayList<LowWaterComplex> lowWaterComplexes, ArrayList<ActiveTicket> activeTickets, UiResult uiResult, BwtdashboardChartData bwtdashboardChartData, BwtpieChartData bwtpieChartData, BwtdataSummary bwtdataSummary) {
        this.dashboardChartData = dashboardChartData;
        this.pieChartData = pieChartData;
        this.dataSummary = dataSummary;
        this.faultyComplexes = faultyComplexes;
        this.connectionStatus = connectionStatus;
        this.lowWaterComplexes = lowWaterComplexes;
        this.activeTickets = activeTickets;
        this.uiResult = uiResult;
        this.bwtdashboardChartData = bwtdashboardChartData;
        this.bwtpieChartData = bwtpieChartData;
        this.bwtdataSummary = bwtdataSummary;
    }


    public DashboardChartData getDashboardChartData() {
        return dashboardChartData;
    }

    public void setDashboardChartData(DashboardChartData dashboardChartData) {
        this.dashboardChartData = dashboardChartData;
    }

    public PieChartData getPieChartData() {
        return pieChartData;
    }

    public void setPieChartData(PieChartData pieChartData) {
        this.pieChartData = pieChartData;
    }

    public DataSummary getDataSummary() {
        return dataSummary;
    }

    public void setDataSummary(DataSummary dataSummary) {
        this.dataSummary = dataSummary;
    }

    public ArrayList<FaultyComplex> getFaultyComplexes() {
        return faultyComplexes;
    }

    public void setFaultyComplexes(ArrayList<FaultyComplex> faultyComplexes) {
        this.faultyComplexes = faultyComplexes;
    }

    public ArrayList<ConnectionStatus> getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(ArrayList<ConnectionStatus> connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public ArrayList<LowWaterComplex> getLowWaterComplexes() {
        return lowWaterComplexes;
    }

    public void setLowWaterComplexes(ArrayList<LowWaterComplex> lowWaterComplexes) {
        this.lowWaterComplexes = lowWaterComplexes;
    }

    public ArrayList<ActiveTicket> getActiveTickets() {
        return activeTickets;
    }

    public void setActiveTickets(ArrayList<ActiveTicket> activeTickets) {
        this.activeTickets = activeTickets;
    }

    public UiResult getUiResult() {
        return uiResult;
    }

    public void setUiResult(UiResult uiResult) {
        this.uiResult = uiResult;
    }

    public BwtdashboardChartData getBwtdashboardChartData() {
        return bwtdashboardChartData;
    }

    public void setBwtdashboardChartData(BwtdashboardChartData bwtdashboardChartData) {
        this.bwtdashboardChartData = bwtdashboardChartData;
    }

    public BwtpieChartData getBwtpieChartData() {
        return bwtpieChartData;
    }

    public void setBwtpieChartData(BwtpieChartData bwtpieChartData) {
        this.bwtpieChartData = bwtpieChartData;
    }

    public BwtdataSummary getBwtdataSummary() {
        return bwtdataSummary;
    }

    public void setBwtdataSummary(BwtdataSummary bwtdataSummary) {
        this.bwtdataSummary = bwtdataSummary;
    }
}