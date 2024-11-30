package sukriti.ngo.mis.ui.complexes.lambda.Complex_fetchAccessTree;

import java.util.ArrayList;

import sukriti.ngo.mis.ui.complexes.data.AqiLumen;
import sukriti.ngo.mis.ui.complexes.data.CmsConfig;
import sukriti.ngo.mis.ui.complexes.data.Health;
import sukriti.ngo.mis.ui.complexes.data.OdsConfig;
import sukriti.ngo.mis.ui.complexes.data.ResetProfile;
import sukriti.ngo.mis.ui.complexes.data.UcemsConfig;
import sukriti.ngo.mis.ui.complexes.data.UpiPaymentList;
import sukriti.ngo.mis.ui.complexes.data.UsageProfile;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.AccessTree;

public class AccessTreeLambdaResult {
    public int status;
    public AccessTree accessTree;

    public AccessTreeLambdaResult(int status, AccessTree accessTree) {
        this.status = status;
        this.accessTree = accessTree;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public AccessTree getAccessTree() {
        return accessTree;
    }

    public void setAccessTree(AccessTree accessTree) {
        this.accessTree = accessTree;
    }
}

