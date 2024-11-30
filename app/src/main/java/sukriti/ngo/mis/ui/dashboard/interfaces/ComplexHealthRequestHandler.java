package sukriti.ngo.mis.ui.dashboard.interfaces;

import java.util.List;

import sukriti.ngo.mis.repository.data.ComplexHealthStats;
import sukriti.ngo.mis.repository.entity.Health;

public interface ComplexHealthRequestHandler {
    public void onSuccess(List<ComplexHealthStats> data);
    public void onError(String message);
}
