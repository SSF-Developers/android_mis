package sukriti.ngo.mis.ui.complexes.interfaces;


import sukriti.ngo.mis.repository.entity.Cabin;
import sukriti.ngo.mis.repository.entity.Complex;

public interface CabinComplexSelectionHandler {
    public void onSelection(Complex complex, Cabin cabin);
}
