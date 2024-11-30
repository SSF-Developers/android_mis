package sukriti.ngo.mis.ui.dashboard.interfaces;

import java.util.List;

import sukriti.ngo.mis.repository.entity.Health;

public interface ComplexWaterLevelRequestHandler {
    public void onSuccess(List<Health> data);
    public void onError(String message);
}
