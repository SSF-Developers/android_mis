package sukriti.ngo.mis.ui.complexes.lambda.Cabin_fetchData;

public class CabinDetailsLambdaRequest {

    public String cabinThingName;

    public CabinDetailsLambdaRequest(String cabinThingName) {
        this.cabinThingName = cabinThingName;
    }

    public String getCabinThingName() {
        return cabinThingName;
    }

    public void setCabinThingName(String cabinThingName) {
        this.cabinThingName = cabinThingName;
    }
}
