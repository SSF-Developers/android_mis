package sukriti.ngo.mis.ui.complexes.interfaces;


import java.util.List;

import sukriti.ngo.mis.ui.complexes.data.DisplayBwtProfile;
import sukriti.ngo.mis.ui.complexes.data.DisplayUsageProfile;

public interface BwtProfileRequestHandler {
    public void getData(List<DisplayBwtProfile> data);
}
