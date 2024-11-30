package sukriti.ngo.mis.ui.profile.fragments

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction
import sukriti.ngo.mis.ui.reports.lambda.DownloadReportLambda.DeleteScheduleReportUserLambdaResult

interface GetProfileDataLambdaHandler {
    @LambdaFunction
    fun mis_adminisatration_actions(request: ProfileDataLambdaRequest?): UserProfileDataLambdaResult?

}