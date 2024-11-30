package sukriti.ngo.mis.ui.complexes.interfaces;


import sukriti.ngo.mis.repository.entity.Complex;
import sukriti.ngo.mis.ui.complexes.data.CabinDetailsData;

public interface LatestAccessedComplexHandler {
    public void getLatestAccessedComplex(Complex data);
}
