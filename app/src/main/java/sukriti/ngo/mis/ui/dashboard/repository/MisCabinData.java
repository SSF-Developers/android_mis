package sukriti.ngo.mis.ui.dashboard.repository;

import java.util.List;

import sukriti.ngo.mis.repository.entity.AqiLumen;
import sukriti.ngo.mis.repository.entity.BwtConfig;
import sukriti.ngo.mis.repository.entity.BwtHealth;
import sukriti.ngo.mis.repository.entity.BwtProfile;
import sukriti.ngo.mis.repository.entity.ClientRequest;
import sukriti.ngo.mis.repository.entity.CmsConfig;
import sukriti.ngo.mis.repository.entity.Health;
import sukriti.ngo.mis.repository.entity.OdsConfig;
import sukriti.ngo.mis.repository.entity.ResetProfile;
import sukriti.ngo.mis.repository.entity.Ticket;
import sukriti.ngo.mis.repository.entity.TicketProgress;
import sukriti.ngo.mis.repository.entity.UcemsConfig;
import sukriti.ngo.mis.repository.entity.UsageProfile;

public class MisCabinData {
    public int result;
    public String message;
    public List<Health> healthTableData;
    public List<BwtHealth> bwtHealthData;
    public List<AqiLumen> aqiLumenData;
    public List<UcemsConfig> ucemsConfigData;
    public List<OdsConfig> odsConfigData;
    public List<CmsConfig> cmsConfigData;
    public List<ClientRequest> clientRequestData;
    public List<BwtConfig> bwtConfigData;
    public List<UsageProfile> usageProfileData;
    public List<ResetProfile> resetProfileData;
    public List<BwtProfile> bwtProfileData;
    public List<Ticket> allTickets;
    public List<TicketProgress> ticketProgress;

//    public CabinDataLambdaResult(int result, String message, List<UcemsConfig> ucemsConfigData) {
//        this.result = result;
//        this.message = message;
//        this.healthTableData = healthTableData;
//        this.bwtHealthData = bwtHealthData;
//        this.aqiLumenData = aqiLumenData;
//        this.ucemsConfigData = ucemsConfigData;
//        this.odsConfigData = odsConfigData;
//        this.cmsConfigData = cmsConfigData;
//        this.clientRequestData = clientRequestData;
//        this.bwtConfigData = bwtConfigData;
//        this.usageProfileData = usageProfileData;
//        this.resetProfileData = resetProfileData;
//        this.bwtProfileData = bwtProfileData;
//    }

    public MisCabinData() {
    }

    public int getResult() {
        return result;
    }
    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public List<Health> getHealthTableDataData() {
        return healthTableData;
    }
    public void setHealthTableData(List<Health> healthTableData) {
        this.healthTableData = healthTableData;
    }

    public List<AqiLumen> getAqiLumenData() {
        return aqiLumenData;
    }
    public void setAqiLumenData(List<AqiLumen> aqiLumenData) {
        this.aqiLumenData = aqiLumenData;
    }

    public List<BwtHealth> getBwtHealthData() {
        return bwtHealthData;
    }
    public void setBwtHealthData(List<BwtHealth> bwtHealthData) {
        this.bwtHealthData = bwtHealthData;
    }

    public List<UcemsConfig> getUcemsConfigData() {
        return ucemsConfigData;
    }
    public void setUcemsConfigData(List<UcemsConfig> ucemsConfigData) {
        this.ucemsConfigData = ucemsConfigData;
    }

    public List<OdsConfig> getOdsConfigData() {
        return odsConfigData;
    }
    public void setOdsConfigData(List<OdsConfig> odsConfigData) {
        this.odsConfigData = odsConfigData;
    }

    public List<CmsConfig> getCmsConfigData() {
        return cmsConfigData;
    }
    public void setCmsConfigData(List<CmsConfig> cmsConfigData) {
        this.cmsConfigData = cmsConfigData;
    }

    public List<ClientRequest> getClientRequestData() {
        return clientRequestData;
    }
    public void setClientRequestData(List<ClientRequest> clientRequestData) {
        this.clientRequestData = clientRequestData;
    }

    public List<BwtConfig> getBwtConfigData() {
        return bwtConfigData;
    }
    public void setBwtConfigData(List<BwtConfig> bwtConfigData) {
        this.bwtConfigData = bwtConfigData;
    }

    public List<UsageProfile> getUsageProfileData() {
        return usageProfileData;
    }
    public void setUsageProfileData(List<UsageProfile> usageProfileData) {
        this.usageProfileData = usageProfileData;
    }

    public List<ResetProfile> getResetProfileData() {
        return resetProfileData;
    }
    public void setResetProfileData(List<ResetProfile> resetProfileData) {
        this.resetProfileData = resetProfileData;
    }

    public List<BwtProfile> getBwtProfileData() {
        return bwtProfileData;
    }
    public void setBwtProfileData(List<BwtProfile> bwtProfileData) {
        this.bwtProfileData = bwtProfileData;
    }

    public List<Health> getHealthTableData() {
        return healthTableData;
    }

    public List<Ticket> getAllTickets() {
        return allTickets;
    }

    public void setAllTickets(List<Ticket> allTickets) {
        this.allTickets = allTickets;
    }

    public List<TicketProgress> getTicketProgress() {
        return ticketProgress;
    }

    public void setTicketProgress(List<TicketProgress> ticketProgress) {
        this.ticketProgress = ticketProgress;
    }
}
