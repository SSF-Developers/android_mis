package sukriti.ngo.mis.ui.complexes.lambda.Cabin_fetchData;

import java.util.ArrayList;

import sukriti.ngo.mis.ui.complexes.data.AqiLumen;
import sukriti.ngo.mis.ui.complexes.data.CmsConfig;
import sukriti.ngo.mis.ui.complexes.data.Health;
import sukriti.ngo.mis.ui.complexes.data.OdsConfig;
import sukriti.ngo.mis.ui.complexes.data.ResetProfile;
import sukriti.ngo.mis.ui.complexes.data.Root;
import sukriti.ngo.mis.ui.complexes.data.UcemsConfig;
import sukriti.ngo.mis.ui.complexes.data.UpiPaymentList;
import sukriti.ngo.mis.ui.complexes.data.UsageAndFeedback;
import sukriti.ngo.mis.ui.complexes.data.UsageProfile;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.LiveStatusResult;

public class CabinDetailsLambdaResult {
    public int status;
    public AqiLumen aqiLumen;
    public Health health;
    public UcemsConfig ucemsConfig;
    public CmsConfig cmsConfig;
    public OdsConfig odsConfig;
    public ArrayList<UsageProfile> usageProfile;
    public ArrayList<ResetProfile> resetProfile;
    public ArrayList<UpiPaymentList> upiPaymentList;
    public LiveStatusResult liveStatusResult;
//    public UsageAndFeedback usageAndFeedback;


    public CabinDetailsLambdaResult(int status, AqiLumen aqiLumen, Health health, UcemsConfig ucemsConfig, CmsConfig cmsConfig, OdsConfig odsConfig, ArrayList<UsageProfile> usageProfile, ArrayList<ResetProfile> resetProfile, ArrayList<UpiPaymentList> upiPaymentList, LiveStatusResult liveStatusResult) {
        this.status = status;
        this.aqiLumen = aqiLumen;
        this.health = health;
        this.ucemsConfig = ucemsConfig;
        this.cmsConfig = cmsConfig;
        this.odsConfig = odsConfig;
        this.usageProfile = usageProfile;
        this.resetProfile = resetProfile;
        this.upiPaymentList = upiPaymentList;
        this.liveStatusResult = liveStatusResult;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public AqiLumen getAqiLumen() {
        return aqiLumen;
    }

    public void setAqiLumen(AqiLumen aqiLumen) {
        this.aqiLumen = aqiLumen;
    }

    public Health getHealth() {
        return health;
    }

    public void setHealth(Health health) {
        this.health = health;
    }

    public UcemsConfig getUcemsConfig() {
        return ucemsConfig;
    }

    public void setUcemsConfig(UcemsConfig ucemsConfig) {
        this.ucemsConfig = ucemsConfig;
    }

    public CmsConfig getCmsConfig() {
        return cmsConfig;
    }

    public void setCmsConfig(CmsConfig cmsConfig) {
        this.cmsConfig = cmsConfig;
    }

    public OdsConfig getOdsConfig() {
        return odsConfig;
    }

    public void setOdsConfig(OdsConfig odsConfig) {
        this.odsConfig = odsConfig;
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

    public ArrayList<UpiPaymentList> getUpiPaymentList() {
        return upiPaymentList;
    }

    public void setUpiPaymentList(ArrayList<UpiPaymentList> upiPaymentList) {
        this.upiPaymentList = upiPaymentList;
    }

    public LiveStatusResult getLiveStatusResult() {
        return liveStatusResult;
    }

    public void setLiveStatusResult(LiveStatusResult liveStatusResult) {
        this.liveStatusResult = liveStatusResult;
    }
}

