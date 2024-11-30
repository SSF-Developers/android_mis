package sukriti.ngo.mis.ui.complexes.lambda.CabinComposition_fetchdata;

import com.amazonaws.mobileconnectors.lambdainvoker.LambdaFunction;

public interface ComplexCompositionLambdaHandler {
    @LambdaFunction
    ComplexCompositionLambdaResult mis_adminisatration_getComplexComposition(ComplexCompositionLambdaRequest request);
}
