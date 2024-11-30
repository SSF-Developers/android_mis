package sukriti.ngo.mis.ui.complexes.interfaces;


import sukriti.ngo.mis.repository.entity.Cabin;
import sukriti.ngo.mis.repository.entity.Complex;

public interface LatestAccessedCabinHandler {
    public void getLatestAccessedCabin(Cabin data);
}
