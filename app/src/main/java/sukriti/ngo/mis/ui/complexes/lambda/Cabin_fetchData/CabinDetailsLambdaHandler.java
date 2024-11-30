package sukriti.ngo.mis.ui.complexes.lambda.Cabin_fetchData;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface CabinDetailsLambdaHandler {
    @LambdaFunction
    CabinDetailsLambdaResult mis_adminisatration_getCabinDetails(CabinDetailsLambdaRequest request);
}
