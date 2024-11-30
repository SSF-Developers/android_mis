package sukriti.ngo.mis.ui.complexes.interfaces;


import sukriti.ngo.mis.repository.entity.BwtHealth;
import sukriti.ngo.mis.repository.entity.Health;

public interface LatestCabinBwtHealthRequestHandler {
     void getData(BwtHealth data);
     void onError(String message);
}
