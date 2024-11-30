package sukriti.ngo.mis.ui.complexes.interfaces;


import sukriti.ngo.mis.repository.entity.AqiLumen;
import sukriti.ngo.mis.repository.entity.Health;

public interface LatestCabinAqiLumenRequestHandler {
    void getData(AqiLumen data);
    void onError(String message);
}
