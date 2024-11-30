package sukriti.ngo.mis.ui.complexes.interfaces;


import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData;

public interface ComplexDetailsRequestHandler {
    public void onSuccess(ComplexDetailsData complex);
    public void onError(String message);
}
