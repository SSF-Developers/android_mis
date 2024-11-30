package sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import sukriti.ngo.mis.ui.profile.fragments.ProfileDataLambdaRequest

interface DeleteScheduledReportUserLambda {

    @LambdaFunction
    fun mis_adminisatration_actions(request: ProfileDataLambdaRequest?): DeleteScheduleReportUserLambdaResult?

}