package sukriti.ngo.mis.ui.complexes.lambda.bwt_fetchCabinDetails;

public class BwtCabinDetailsLambdaRequest {

    public String cabinThingName;

    public BwtCabinDetailsLambdaRequest(String cabinThingName) {
        this.cabinThingName = cabinThingName;
    }

    public String getCabinThingName() {
        return cabinThingName;
    }

    public void setCabinThingName(String cabinThingName) {
        this.cabinThingName = cabinThingName;
    }
}
