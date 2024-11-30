package sukriti.ngo.mis.ui.complexes.interfaces;


import java.util.List;

import sukriti.ngo.mis.repository.entity.Health;
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData;

public interface PropertiesListRequestHandler {
    public void getData(List<PropertyNameValueData> data, String Timestamp);
}
