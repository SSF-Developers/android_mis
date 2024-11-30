package sukriti.ngo.mis.ui.complexes.lambda.CabinComposition_fetchdata;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.ComplexComposition;

public class ComplexCompositionLambdaResult {
    public int status;
    public ComplexComposition complexComposition;

    public ComplexCompositionLambdaResult(int status, ComplexComposition complexComposition) {
        this.status = status;
        this.complexComposition = complexComposition;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ComplexComposition getComplexComposition() {
        return complexComposition;
    }

    public void setComplexComposition(ComplexComposition complexComposition) {
        this.complexComposition = complexComposition;
    }
}

