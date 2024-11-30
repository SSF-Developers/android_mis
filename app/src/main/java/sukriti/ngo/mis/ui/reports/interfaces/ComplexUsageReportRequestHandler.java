package sukriti.ngo.mis.ui.reports.interfaces;


import java.util.List;

import sukriti.ngo.mis.ui.complexes.data.DisplayUsageProfile;
import sukriti.ngo.mis.ui.reports.data.UsageReportData;

public interface ComplexUsageReportRequestHandler {
    public void getData(List<UsageReportData> data);
}
