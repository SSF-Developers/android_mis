package sukriti.ngo.mis.ui.reports.lambda.report_fetchDateWaiseUsageData;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

import java.util.ArrayList;

import sukriti.ngo.mis.ui.reports.data.Root;


public interface ReportLambdaHandler {
    @LambdaFunction
    ArrayList<Root> mis_report_fetchDateWaiseUsageData(ReportLambdaRequest request);
}
