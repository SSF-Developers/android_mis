package sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface DownloadReportLambdaHandler {

    @LambdaFunction
    DownloadReportLambdaResult mis_report_api(DownloadReportRequest request);
}
