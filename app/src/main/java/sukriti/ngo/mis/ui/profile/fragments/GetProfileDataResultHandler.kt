package sukriti.ngo.mis.ui.profile.fragments

import sukriti.ngo.mis.ui.administration.lambda.CreateUser.ClientListLambdaResult

interface GetProfileDataResultHandler {

    fun onSuccess(result: UserProfileDataLambdaResult?)

    fun onError(errorMessage: String?)
}