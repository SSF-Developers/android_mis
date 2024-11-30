package sukriti.ngo.mis.ui.complexes.lambda.bwt_fetchCabinDetails;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface BwtCabinDetailsLambdaHandler {
    @LambdaFunction
    BwtCabinDetailsLambdaResult mis_administration_BWT_getCabinDetails(BwtCabinDetailsLambdaRequest request);
}
