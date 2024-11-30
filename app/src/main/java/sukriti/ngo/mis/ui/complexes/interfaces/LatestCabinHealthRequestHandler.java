package sukriti.ngo.mis.ui.complexes.interfaces;


import sukriti.ngo.mis.repository.entity.Complex;
import sukriti.ngo.mis.repository.entity.Health;

public interface LatestCabinHealthRequestHandler {
     void getData(Health data);
     void onError(String message);
}
