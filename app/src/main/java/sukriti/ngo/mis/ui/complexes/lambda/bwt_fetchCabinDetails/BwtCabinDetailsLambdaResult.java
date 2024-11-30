package sukriti.ngo.mis.ui.complexes.lambda.bwt_fetchCabinDetails;

import java.util.ArrayList;

import sukriti.ngo.mis.ui.complexes.bwtData.BwtConfig;
import sukriti.ngo.mis.ui.complexes.bwtData.Health;
import sukriti.ngo.mis.ui.complexes.data.ResetProfile;
import sukriti.ngo.mis.ui.complexes.bwtData.TurbidityAndWaterRecycled;
import sukriti.ngo.mis.ui.complexes.bwtData.UsageProfile;
import sukriti.ngo.mis.ui.complexes.data.AqiLumen;
import sukriti.ngo.mis.ui.complexes.data.CmsConfig;
import sukriti.ngo.mis.ui.complexes.data.OdsConfig;
import sukriti.ngo.mis.ui.complexes.data.UcemsConfig;
import sukriti.ngo.mis.ui.complexes.data.UpiPaymentList;

public class BwtCabinDetailsLambdaResult {
    public int status;
    public Health health;
    public BwtConfig bwtConfig;
    public ArrayList<UsageProfile> usageProfile;
    public ArrayList<ResetProfile> resetProfile;
    public TurbidityAndWaterRecycled turbidityAndWaterRecycled;

    public BwtCabinDetailsLambdaResult(int status, Health health, BwtConfig bwtConfig, ArrayList<UsageProfile> usageProfile, ArrayList<ResetProfile> resetProfile, TurbidityAndWaterRecycled turbidityAndWaterRecycled) {
        this.status = status;
        this.health = health;
        this.bwtConfig = bwtConfig;
        this.usageProfile = usageProfile;
        this.resetProfile = resetProfile;
        this.turbidityAndWaterRecycled = turbidityAndWaterRecycled;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Health getHealth() {
        return health;
    }

    public void setHealth(Health health) {
        this.health = health;
    }

    public BwtConfig getBwtConfig() {
        return bwtConfig;
    }

    public void setBwtConfig(BwtConfig bwtConfig) {
        this.bwtConfig = bwtConfig;
    }

    public ArrayList<UsageProfile> getUsageProfile() {
        return usageProfile;
    }

    public void setUsageProfile(ArrayList<UsageProfile> usageProfile) {
        this.usageProfile = usageProfile;
    }

    public ArrayList<ResetProfile> getResetProfile() {
        return resetProfile;
    }

    public void setResetProfile(ArrayList<ResetProfile> resetProfile) {
        this.resetProfile = resetProfile;
    }

    public TurbidityAndWaterRecycled getTurbidityAndWaterRecycled() {
        return turbidityAndWaterRecycled;
    }

    public void setTurbidityAndWaterRecycled(TurbidityAndWaterRecycled turbidityAndWaterRecycled) {
        this.turbidityAndWaterRecycled = turbidityAndWaterRecycled;
    }
}

