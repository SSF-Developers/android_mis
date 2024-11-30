package sukriti.ngo.mis.repository;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import sukriti.ngo.mis.repository.data.DailyAverageFeedback;
import sukriti.ngo.mis.repository.data.DailyChargeCollection;
import sukriti.ngo.mis.repository.data.DailyFeedbackCount;
import sukriti.ngo.mis.repository.data.DailyTicketEventCount;
import sukriti.ngo.mis.repository.data.DailyUsageCount;
import sukriti.ngo.mis.repository.data.DailyWaterRecycled;
import sukriti.ngo.mis.repository.data.FeedbackSummary;
import sukriti.ngo.mis.repository.data.FeedbackWiseUserCount;
import sukriti.ngo.mis.repository.entity.AqiLumen;
import sukriti.ngo.mis.repository.entity.BwtConfig;
import sukriti.ngo.mis.repository.entity.BwtHealth;
import sukriti.ngo.mis.repository.entity.BwtProfile;
import sukriti.ngo.mis.repository.entity.Cabin;
import sukriti.ngo.mis.repository.entity.ClientRequest;
import sukriti.ngo.mis.repository.entity.CmsConfig;
import sukriti.ngo.mis.repository.entity.Complex;
import sukriti.ngo.mis.repository.entity.DailyFeedback;
import sukriti.ngo.mis.repository.entity.DailyUsage;
import sukriti.ngo.mis.repository.entity.DailyUsageCharge;
import sukriti.ngo.mis.repository.entity.Health;
import sukriti.ngo.mis.repository.entity.OdsConfig;
import sukriti.ngo.mis.repository.entity.QuickAccess;
import sukriti.ngo.mis.repository.entity.ResetProfile;
import sukriti.ngo.mis.repository.entity.Ticket;
import sukriti.ngo.mis.repository.entity.TicketProgress;
import sukriti.ngo.mis.repository.entity.UcemsConfig;
import sukriti.ngo.mis.repository.entity.UsageProfile;
import sukriti.ngo.mis.ui.complexes.data.ComplexDetailsData;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface DataAccess {

    //Health.class, BwtHealth.class, AqiLumen.class, UcemsConfig.class,
    //        OdsConfig.class, CmsConfig.class, ClientRequest.class, BwtConfig.class,UsageProfile.class,
    //        ResetProfile.class, BwtProfile.clas

    @Insert(onConflict = REPLACE)
    void insertItem(Ticket item);

    @Insert(onConflict = REPLACE)
    void insertItem(TicketProgress item);

    @Insert(onConflict = IGNORE)
    void insertItem(Health item);

    @Insert(onConflict = IGNORE)
    void insertItem(BwtHealth item);

    @Insert(onConflict = IGNORE)
    void insertItem(AqiLumen item);

    @Insert(onConflict = IGNORE)
    void insertItem(UcemsConfig item);

    @Insert(onConflict = IGNORE)
    void insertItem(OdsConfig item);

    @Insert(onConflict = IGNORE)
    void insertItem(CmsConfig item);

    @Insert(onConflict = IGNORE)
    void insertItem(ClientRequest item);

    @Insert(onConflict = IGNORE)
    void insertItem(BwtConfig item);

    @Insert(onConflict = IGNORE)
    long insertItem(UsageProfile item);

    @Insert(onConflict = IGNORE)
    void insertItem(ResetProfile item);

    @Insert(onConflict = IGNORE)
    void insertItem(BwtProfile item);

    @Insert(onConflict = IGNORE)
    void insertItem(DailyUsage item);

    @Insert(onConflict = IGNORE)
    void insertItem(DailyFeedback item);

    @Insert(onConflict = IGNORE)
    void insertItem(DailyUsageCharge item);

    @Insert(onConflict = REPLACE)
    void XinsertItem(Complex item);

    @Insert(onConflict = IGNORE)
    void insertItem(Cabin item);

    //Delete Item
    @Query("DELETE FROM Complex where ComplexName = :complexName")
    void deleteComplex(String complexName);

    @Query("DELETE FROM Cabin")
    void truncateCabin();

    // Truncate
    @Query("DELETE FROM Health")
    void truncateHealth();

    @Query("DELETE FROM BwtHealth")
    void truncateBwtHealth();

    @Query("DELETE FROM AqiLumen")
    void truncateAqiLumen();

    @Query("DELETE FROM UcemsConfig")
    void truncateUcemsConfig();

    @Query("DELETE FROM OdsConfig")
    void truncateOdsConfig();

    @Query("DELETE FROM CmsConfig")
    void truncateCmsConfig();

    @Query("DELETE FROM ClientRequest")
    void truncateClientRequest();

    @Query("DELETE FROM BwtConfig")
    void truncateBwtConfig();

    @Query("DELETE FROM UsageProfile")
    void truncateUsageProfile();

    @Query("DELETE FROM ResetProfile")
    void truncateResetProfile();

    @Query("DELETE FROM BwtProfile")
    void truncateBwtProfile();

    @Query("DELETE FROM DailyUsage")
    void truncateDailyUsage();

    @Query("DELETE FROM DailyFeedback")
    void truncateDailyFeedback();

    @Query("DELETE FROM DailyUsageCharge")
    void truncateDailyUsageCharge();

    @Query("DELETE FROM QuickAccess")
    void truncateQuickAccess();

    //LatestTimestamp
    @Query("SELECT MAX(DEVICE_TIMESTAMP) FROM Health")
    String getLatestTimestampForHealth();

    @Query("SELECT MAX(DEVICE_TIMESTAMP) FROM BwtHealth")
    String getLatestTimestampForBwtHealth();

    @Query("SELECT MAX(DEVICE_TIMESTAMP)  FROM AqiLumen")
    String getLatestTimestampForAqiLumen();

    @Query("SELECT MAX(DEVICE_TIMESTAMP) FROM UcemsConfig")
    String getLatestTimestampForUcemsConfig();

    @Query("SELECT MAX(DEVICE_TIMESTAMP) FROM OdsConfig")
    String getLatestTimestampForOdsConfig();

    @Query("SELECT MAX(DEVICE_TIMESTAMP)  FROM CmsConfig")
    String getLatestTimestampForCmsConfig();

    @Query("SELECT MAX(DEVICE_TIMESTAMP) FROM ClientRequest")
    String getLatestTimestampForClientRequest();

    @Query("SELECT MAX(DEVICE_TIMESTAMP) FROM BwtConfig")
    String getLatestTimestampForBwtConfig();

    @Query("SELECT MAX(DEVICE_TIMESTAMP) FROM UsageProfile")
    String getLatestTimestampForUsageProfile();

    @Query("SELECT MAX(DEVICE_TIMESTAMP) FROM ResetProfile")
    String getLatestTimestampForResetProfile();

    @Query("SELECT MAX(DEVICE_TIMESTAMP) FROM BwtProfile")
    String getLatestTimestampForBwtProfile();

    //Dashboard-Usage
    @Query("SELECT COUNT(*) FROM DailyUsage WHERE date  BETWEEN :fromDate AND :toDate ")
    int countUsage(String fromDate, String toDate);

    @Query("SELECT COUNT(*) FROM DailyUsage WHERE date  BETWEEN :fromDate AND :toDate AND type = :type")
    int countUsage(String type,String fromDate, String toDate);

    @Query("SELECT date,COUNT(*) as count FROM DailyUsage WHERE date BETWEEN :fromDate AND :toDate AND type = :type GROUP BY date")
    List<DailyUsageCount> countDailyUsage(String type, String fromDate, String toDate);

    @Query("SELECT date,COUNT(*) as count FROM DailyUsage WHERE date BETWEEN :fromDate AND :toDate GROUP BY date")
    List<DailyUsageCount> countDailyUsage(String fromDate, String toDate);

    @Query("SELECT date,COUNT(*) as count FROM DailyUsage GROUP BY date")
    List<DailyUsageCount> countDailyUsage();

    @Query("SELECT count(DISTINCT cabin) FROM DailyUsage WHERE date  BETWEEN :fromDate AND :toDate AND type = :type")
    int fetchCabinTypeCount(String type, String fromDate, String toDate);

    //int mwcCount = dataAccess.countRawUsage("%MWC%");
    @Query("SELECT COUNT(*) FROM UsageProfile WHERE SHORT_THING_NAME LIKE :type")
    int countRawUsage(String type);

    //Dashboard-Feedback
    @Query("SELECT Sum(feedback) as totalFeedback,COUNT(*) as userCount, AVG(feedback) as averageFeedback " +
            "FROM DailyFeedback WHERE date BETWEEN :fromDate AND :toDate")
    FeedbackSummary countTotalFeedback(String fromDate, String toDate);

    @Query("SELECT Sum(feedback) as totalFeedback,COUNT(*) as userCount, AVG(feedback) as averageFeedback  FROM DailyFeedback WHERE date BETWEEN :fromDate AND :toDate AND type = :type")
    FeedbackSummary countTotalFeedback(String type, String fromDate, String toDate);

    @Query("SELECT feedback,COUNT(*) as userCount FROM DailyFeedback WHERE date BETWEEN :fromDate AND :toDate AND type = :type GROUP BY feedback")
    List<FeedbackWiseUserCount> countUsersForFeedback(String type, String fromDate, String toDate);

    @Query("SELECT date, feedback as totalFeedback, COUNT(*) as userCount FROM DailyFeedback WHERE date BETWEEN :fromDate AND :toDate AND feedback = :feedback GROUP BY feedback,date")
    List<DailyFeedbackCount> countDailyUserCountForFeedback(String fromDate, String toDate, Integer feedback);

    @Query("SELECT date,Sum(feedback) as total, AVG(feedback) as average, COUNT(*) as userCount FROM DailyFeedback WHERE date BETWEEN :fromDate AND :toDate GROUP BY date")
    List<DailyAverageFeedback> countDailyFeedback(String fromDate, String toDate);

    @Query("SELECT date,Sum(feedback) as total, AVG(feedback) as average, COUNT(*) as userCount FROM DailyFeedback WHERE date BETWEEN :fromDate AND :toDate AND type = :type GROUP BY date")
    List<DailyAverageFeedback> countDailyFeedback(String type, String fromDate, String toDate);

    //Dashboard - HealthLockWater
    @Query("SELECT DISTINCT COMPLEX FROM Health")
    List<String> getAllComplexesForHealthTable();

    @Query("SELECT DISTINCT THING_NAME FROM Health")
    List<String> getAllCabinsForHealthTable();

    @Query("SELECT DISTINCT THING_NAME FROM AqiLumen")
    List<String> getAllCabinsForAqiLumenTable();

    @Query("SELECT *  FROM Health WHERE THING_NAME = :thingName ORDER BY SERVER_TIMESTAMP DESC LIMIT 1")
    Health getCurrentHealthStatus(String thingName);

    @Query("SELECT DISTINCT THING_NAME FROM BwtHealth")
    List<String> getAllCabinsForBwtHealthTable();

    @Query("SELECT *  FROM BwtHealth WHERE THING_NAME = :thingName ORDER BY SERVER_TIMESTAMP DESC LIMIT 1")
    BwtHealth getCurrentBwtHealthStatus(String thingName);

    @Query("SELECT Count(*)  FROM BWTPROFILE WHERE COMPLEX = :complexName")
    Integer getBwtProfileCount(String complexName);

    @Query("SELECT Count(*)  FROM BWTPROFILE")
    Integer getBwtProfileCount();

    @Query("SELECT *  FROM Health WHERE COMPLEX = :complexName ORDER BY SERVER_TIMESTAMP DESC LIMIT 1")
    Health getCurrentWaterStatus(String complexName);

    //Dashboard - UsageChargeCollection
    @Query("SELECT SUM(amount) FROM DailyUsageCharge WHERE date BETWEEN :fromDate AND :toDate")
    Float countTotalCollection(String fromDate, String toDate);

    @Query("SELECT SUM(amount) FROM DailyUsageCharge WHERE date BETWEEN :fromDate AND :toDate AND type = :type")
    Float countTotalCollection(String type, String fromDate, String toDate);

    @Query("SELECT date,SUM(amount) as amount FROM DailyUsageCharge WHERE date BETWEEN :fromDate AND :toDate GROUP BY date")
    List<DailyChargeCollection> countDailyCollection(String fromDate, String toDate);

    @Query("SELECT date,SUM(amount) as amount FROM DailyUsageCharge WHERE date BETWEEN :fromDate AND :toDate AND type = :type GROUP BY date")
    List<DailyChargeCollection> countDailyCollection(String type, String fromDate, String toDate);

    //Dashboard - Banner
    @Query("SELECT date,SUM(waterRecycled) as quantity FROM BwtProfile WHERE date BETWEEN :fromDate AND :toDate GROUP BY date")
    List<DailyWaterRecycled> countDailyWaterRecycled(String fromDate, String toDate);

    //Complex Access
    @Query("SELECT *  FROM Complex ORDER BY lastAccessTimestamp ASC LIMIT 1")
    Complex getComplexWithOldestAccessTimestamp();

    @Query("SELECT Count(*)  FROM Complex")
    Integer getComplexAccessCount();

    @Query("SELECT *  FROM Complex ORDER BY lastAccessTimestamp DESC")
    List<Complex>  listComplexAccessLog();

    @Query("SELECT *  FROM Complex ORDER BY lastAccessTimestamp DESC LIMIT 1")
    Complex  listLatestAccessedComplex();

    @Query("SELECT *  FROM Complex ORDER BY lastAccessTimestamp DESC")
    List<Complex>  listAllAccessedComplex();

    @Query("SELECT Count(*)  FROM Cabin")
    Integer getCabinAccessCount();

    @Query("SELECT *  FROM Cabin ORDER BY lastAccessTimestamp DESC LIMIT 1")
    Cabin  listLatestAccessedCabin();

    //Cabin Details
    @Query("SELECT * FROM Health WHERE THING_NAME = :thingName ORDER BY DEVICE_TIMESTAMP DESC LIMIT 1")
    Health getCabinHealth(String thingName);

    @Query("SELECT * FROM BwtHealth WHERE THING_NAME = :thingName ORDER BY DEVICE_TIMESTAMP DESC LIMIT 1")
    BwtHealth getCabinBwtHealth(String thingName);

    @Query("SELECT * FROM AQILUMEN WHERE THING_NAME = :thingName ORDER BY DEVICE_TIMESTAMP DESC LIMIT 1")
    AqiLumen getCabinAqiLumen(String thingName);

    @Query("SELECT * FROM UcemsConfig WHERE THING_NAME = :thingName ORDER BY DEVICE_TIMESTAMP DESC LIMIT 1")
    UcemsConfig getCabinUcemsConfig(String thingName);

    @Query("SELECT * FROM UcemsConfig")
    List<UcemsConfig> getCabinUcemsConfig();


    @Query("SELECT * FROM OdsConfig WHERE THING_NAME = :thingName ORDER BY DEVICE_TIMESTAMP DESC LIMIT 1")
    OdsConfig getCabinOdsConfig(String thingName);

    @Query("SELECT * FROM CmsConfig WHERE THING_NAME = :thingName ORDER BY DEVICE_TIMESTAMP DESC LIMIT 1")
    CmsConfig getCabinCmsConfig(String thingName);

    @Query("SELECT * FROM BwtConfig WHERE THING_NAME = :thingName ORDER BY DEVICE_TIMESTAMP DESC LIMIT 1")
    BwtConfig getCabinBwtConfig(String thingName);

    //CLIENT REQUEST
    @Query("SELECT * FROM ClientRequest WHERE THING_NAME = :thingName ORDER BY DEVICE_TIMESTAMP DESC LIMIT 1")
    ClientRequest getCabinClientRequest(String thingName);

    @Query("SELECT * FROM ClientRequest")
    List<ClientRequest> getCabinClientRequest();

    @Query("SELECT Count(*) FROM ClientRequest")
    Integer getCabinClientRequestCount();

    //CABIN PROFILE
    @Query("SELECT * FROM UsageProfile WHERE THING_NAME = :thingName AND SERVER_TIMESTAMP BETWEEN :fromTimeStamp AND :toTimeStamp ORDER BY SERVER_TIMESTAMP DESC")
    List<UsageProfile> getProfileUsage(String thingName, String fromTimeStamp, String toTimeStamp);

    @Query("SELECT * FROM BWTPROFILE WHERE THING_NAME = :thingName AND SERVER_TIMESTAMP BETWEEN :fromTimeStamp AND :toTimeStamp ORDER BY SERVER_TIMESTAMP DESC")
    List<BwtProfile> getProfileBwt(String thingName, String fromTimeStamp, String toTimeStamp);

    @Query("SELECT * FROM BWTPROFILE ORDER BY SERVER_TIMESTAMP DESC")
    List<BwtProfile> getProfileBwt();

    @Query("SELECT * FROM RESETPROFILE WHERE THING_NAME = :thingName AND DEVICE_TIMESTAMP BETWEEN :fromTimeStamp AND :toTimeStamp ORDER BY DEVICE_TIMESTAMP DESC")
    List<ResetProfile> getProfileReset(String thingName, String fromTimeStamp, String toTimeStamp);

    @Query("SELECT * FROM RESETPROFILE ORDER BY DEVICE_TIMESTAMP DESC")
    List<ResetProfile> getProfileReset();

    //Quick Access
    @Insert(onConflict = IGNORE)
    void insertItem(QuickAccess item);

    @Query("DELETE FROM QuickAccess where ThingName = :thingName")
    void deleteQuickAccess(String thingName);

    @Query("SELECT * FROM QuickAccess ORDER BY markedForQuickAccessTimestamp DESC")
    List<QuickAccess> getQuickAccessList();

    //Reports
    @Query("SELECT * FROM UsageProfile WHERE (STATE IN(:states) OR DISTRICT IN (:districts) OR CITY IN (:cities) OR COMPLEX IN (:complexes) )" +
            "AND SERVER_TIMESTAMP BETWEEN :fromTimeStamp AND :toTimeStamp ORDER BY SERVER_TIMESTAMP DESC")
    List<UsageProfile> getProfileReport(List<String> states,List<String> districts,List<String> cities,List<String> complexes, String fromTimeStamp, String toTimeStamp);

    @Query("SELECT * FROM UsageProfile WHERE (COMPLEX = :complex )" +
            "AND SERVER_TIMESTAMP BETWEEN :fromTimeStamp AND :toTimeStamp ORDER BY SERVER_TIMESTAMP DESC")
    List<UsageProfile> getProfileReport(String complex, String fromTimeStamp, String toTimeStamp);

    @Query("SELECT * FROM ResetProfile WHERE (STATE IN(:states) OR DISTRICT IN (:districts) OR CITY IN (:cities) OR COMPLEX IN (:complexes) )" +
            "AND DEVICE_TIMESTAMP BETWEEN :fromTimeStamp AND :toTimeStamp ORDER BY DEVICE_TIMESTAMP DESC")
    List<ResetProfile> getResetReport(List<String> states,List<String> districts,List<String> cities,List<String> complexes, String fromTimeStamp, String toTimeStamp);

    @Query("SELECT * FROM BwtProfile WHERE (STATE IN(:states) OR DISTRICT IN (:districts) OR CITY IN (:cities) OR COMPLEX IN (:complexes) )" +
            "AND DEVICE_TIMESTAMP BETWEEN :fromTimeStamp AND :toTimeStamp ORDER BY DEVICE_TIMESTAMP DESC")
    List<BwtProfile> getBwtReport(List<String> states,List<String> districts,List<String> cities,List<String> complexes, String fromTimeStamp, String toTimeStamp);

    //Complex-Wise Report
    @Query("SELECT COUNT(*) FROM DailyUsage WHERE complex = :complex AND date  BETWEEN :fromDate AND :toDate ")
    int countUsageForComplex(String complex, String fromDate, String toDate);

    @Query("SELECT COUNT(*) FROM DailyUsage WHERE complex = :complex AND date  BETWEEN :fromDate AND :toDate AND type = :type")
    int countUsageForComplex(String complex, String type,String fromDate, String toDate);

    @Query("SELECT date,COUNT(*) as count FROM DailyUsage WHERE complex = :Complex AND date BETWEEN :fromDate AND :toDate GROUP BY date")
    List<DailyUsageCount> countDailyUsageForComplex(String Complex, String fromDate, String toDate);

    @Query("SELECT date,COUNT(*) as count FROM DailyUsage WHERE complex = :Complex AND date BETWEEN :fromDate AND :toDate AND type = :type GROUP BY date")
    List<DailyUsageCount> countDailyUsageForComplex(String Complex,String type, String fromDate, String toDate);

    @Query("SELECT Sum(feedback) as totalFeedback,COUNT(*) as userCount, AVG(feedback) as averageFeedback FROM DailyFeedback WHERE complex = :complex AND date BETWEEN :fromDate AND :toDate")
    FeedbackSummary countTotalFeedbackForComplex(String complex,String fromDate, String toDate);

    @Query("SELECT Sum(feedback) as totalFeedback,COUNT(*) as userCount, AVG(feedback) as averageFeedback  FROM DailyFeedback WHERE complex = :complex AND date BETWEEN :fromDate AND :toDate AND type = :type")
    FeedbackSummary countTotalFeedbackForComplex(String complex,String type, String fromDate, String toDate);

    @Query("SELECT feedback,COUNT(*) as userCount FROM DailyFeedback WHERE complex = :complex AND date BETWEEN :fromDate AND :toDate AND type = :type GROUP BY feedback")
    List<FeedbackWiseUserCount> countUsersForFeedbackForComplex(String complex,String type, String fromDate, String toDate);

    @Query("SELECT date, feedback as totalFeedback, COUNT(*) as userCount FROM DailyFeedback WHERE complex = :complex AND date BETWEEN :fromDate AND :toDate AND feedback = :feedback GROUP BY feedback,date")
    List<DailyFeedbackCount> countDailyUserCountForFeedbackForComplex(String complex,String fromDate, String toDate, Integer feedback);

    @Query("SELECT date,Sum(feedback) as total, AVG(feedback) as average, COUNT(*) as userCount FROM DailyFeedback WHERE complex = :complex AND date BETWEEN :fromDate AND :toDate GROUP BY date")
    List<DailyAverageFeedback> countDailyFeedbackForComplex(String complex,String fromDate, String toDate);

    @Query("SELECT date,Sum(feedback) as total, AVG(feedback) as average, COUNT(*) as userCount FROM DailyFeedback WHERE complex = :complex AND date BETWEEN :fromDate AND :toDate AND type = :type GROUP BY date")
    List<DailyAverageFeedback> countDailyFeedbackForComplex(String complex,String type, String fromDate, String toDate);

    @Query("SELECT SUM(amount) FROM DailyUsageCharge WHERE complex = :complex AND date BETWEEN :fromDate AND :toDate")
    Float countTotalCollectionForComplex(String complex,String fromDate, String toDate);

    @Query("SELECT SUM(amount) FROM DailyUsageCharge WHERE complex = :complex AND date BETWEEN :fromDate AND :toDate AND type = :type")
    Float countTotalCollectionForComplex(String complex,String type, String fromDate, String toDate);

    @Query("SELECT date,SUM(amount) as amount FROM DailyUsageCharge WHERE complex = :complex AND date BETWEEN :fromDate AND :toDate GROUP BY date")
    List<DailyChargeCollection> countDailyCollectionForComplex(String complex,String fromDate, String toDate);

    @Query("SELECT date,SUM(amount) as amount FROM DailyUsageCharge WHERE complex = :complex AND date BETWEEN :fromDate AND :toDate AND type = :type GROUP BY date")
    List<DailyChargeCollection> countDailyCollectionForComplex(String complex,String type, String fromDate, String toDate);


    //Graphical-Reports-Summary
    @Query("SELECT * FROM UsageProfile WHERE COMPLEX IN(:complexes) " +
            "AND SERVER_TIMESTAMP BETWEEN :fromTimeStamp AND :toTimeStamp ORDER BY SERVER_TIMESTAMP DESC")
    List<UsageProfile> getProfileReport(List<String> complexes, String fromTimeStamp, String toTimeStamp);

    @Query("SELECT COUNT(*) FROM DailyUsage WHERE complex IN(:complexes) AND date  BETWEEN :fromDate AND :toDate ")
    int countUsageForComplex(List<String> complexes, String fromDate, String toDate);

    @Query("SELECT COUNT(*) FROM DailyUsage WHERE complex IN(:complexes) AND date  BETWEEN :fromDate AND :toDate AND type = :type")
    int countUsageForComplex(List<String> complexes, String type,String fromDate, String toDate);

    @Query("SELECT date,COUNT(*) as count FROM DailyUsage WHERE complex IN(:complexes) AND date BETWEEN :fromDate AND :toDate GROUP BY date")
    List<DailyUsageCount> countDailyUsageForComplex(List<String> complexes, String fromDate, String toDate);

    @Query("SELECT date,COUNT(*) as count FROM DailyUsage WHERE complex IN(:complexes) AND date BETWEEN :fromDate AND :toDate AND type = :type GROUP BY date")
    List<DailyUsageCount> countDailyUsageForComplex(List<String> complexes,String type, String fromDate, String toDate);

    @Query("SELECT Sum(feedback) as totalFeedback,COUNT(*) as userCount, AVG(feedback) as averageFeedback FROM DailyFeedback WHERE complex IN(:complexes) AND date BETWEEN :fromDate AND :toDate")
    FeedbackSummary countTotalFeedbackForComplex(List<String> complexes,String fromDate, String toDate);

    @Query("SELECT Sum(feedback) as totalFeedback,COUNT(*) as userCount, AVG(feedback) as averageFeedback  FROM DailyFeedback WHERE complex IN(:complexes) AND date BETWEEN :fromDate AND :toDate AND type = :type")
    FeedbackSummary countTotalFeedbackForComplex(List<String> complexes,String type, String fromDate, String toDate);

    @Query("SELECT feedback,COUNT(*) as userCount FROM DailyFeedback WHERE complex IN(:complexes) AND date BETWEEN :fromDate AND :toDate AND type = :type GROUP BY feedback")
    List<FeedbackWiseUserCount> countUsersForFeedbackForComplex(List<String> complexes,String type, String fromDate, String toDate);

    @Query("SELECT date, feedback as totalFeedback, COUNT(*) as userCount FROM DailyFeedback WHERE complex IN(:complexes) AND date BETWEEN :fromDate AND :toDate AND feedback = :feedback GROUP BY feedback,date")
    List<DailyFeedbackCount> countDailyUserCountForFeedbackForComplex(List<String> complexes,String fromDate, String toDate, Integer feedback);

    @Query("SELECT date,Sum(feedback) as total, AVG(feedback) as average, COUNT(*) as userCount FROM DailyFeedback WHERE complex IN(:complexes) AND date BETWEEN :fromDate AND :toDate GROUP BY date")
    List<DailyAverageFeedback> countDailyFeedbackForComplex(List<String> complexes,String fromDate, String toDate);

    @Query("SELECT date,Sum(feedback) as total, AVG(feedback) as average, COUNT(*) as userCount FROM DailyFeedback WHERE complex IN(:complexes) AND date BETWEEN :fromDate AND :toDate AND type = :type GROUP BY date")
    List<DailyAverageFeedback> countDailyFeedbackForComplex(List<String> complexes,String type, String fromDate, String toDate);

    @Query("SELECT SUM(amount) FROM DailyUsageCharge WHERE complex IN(:complexes) AND date BETWEEN :fromDate AND :toDate")
    Float countTotalCollectionForComplex(List<String> complexes,String fromDate, String toDate);

    @Query("SELECT SUM(amount) FROM DailyUsageCharge WHERE complex IN(:complexes) AND date BETWEEN :fromDate AND :toDate AND type = :type")
    Float countTotalCollectionForComplex(List<String> complexes,String type, String fromDate, String toDate);

    @Query("SELECT date,SUM(amount) as amount FROM DailyUsageCharge WHERE complex IN(:complexes) AND date BETWEEN :fromDate AND :toDate GROUP BY date")
    List<DailyChargeCollection> countDailyCollectionForComplex(List<String> complexes,String fromDate, String toDate);

    @Query("SELECT date,SUM(amount) as amount FROM DailyUsageCharge WHERE complex IN(:complexes) AND date BETWEEN :fromDate AND :toDate AND type = :type GROUP BY date")
    List<DailyChargeCollection> countDailyCollectionForComplex(List<String> complexes,String type, String fromDate, String toDate);

    //TICKETS
    @Query("UPDATE Ticket SET isQueuedForUser = :isQueued WHERE ticket_id =:ticketId")
    void updateTicketQueuedStatus(Boolean isQueued, String ticketId);

    @Query("UPDATE Ticket SET isUnQueued = :isUnQueued WHERE ticket_id =:ticketId")
    void updateTicketUnQueuedStatus(Boolean isUnQueued, String ticketId);

    @Query("UPDATE Ticket SET isQueuedForUser = 0")
    void setAllTicketsNotQueued();

    @Query("SELECT * FROM Ticket ORDER BY timestamp DESC")
    List<Ticket> getAllTickets();

    @Query("SELECT * FROM Ticket WHERE ticket_status = :status ORDER BY timestamp DESC")
    List<Ticket> getAllTicketsWithStatus(String status);

    @Query("SELECT * FROM Ticket WHERE ticket_status != 'Closed' ORDER BY timestamp DESC")
    List<Ticket> getAllActiveTickets();

//    rahul karn
    @Query("SELECT * FROM Ticket WHERE ticket_status =:ticketStatus  ORDER BY timestamp DESC")
    List<Ticket> getAllActiveTickets(String ticketStatus);

    @Query("SELECT ticket_id FROM Ticket WHERE ticket_status != 'Closed' AND complex_name =:complex ORDER BY timestamp DESC")
    List<String> getAllActiveTicketsByComplex(String complex);

    @Query("SELECT * FROM Ticket WHERE isUnQueued = 1 ORDER BY timestamp DESC")
    List<Ticket> getUnQueuedTickets();

    @Query("SELECT * FROM Ticket WHERE isQueuedForUser = 1 ORDER BY timestamp DESC")
    List<Ticket> getQueuedTickets();

    @Query("SELECT * FROM Ticket WHERE creator_id = :userId AND ticket_status != 'Closed' ORDER BY timestamp DESC")
    List<Ticket> getRaisedAndNotClosedTickets(String userId);

//    rahul karn
    @Query("SELECT * FROM Ticket WHERE creator_id = :userId AND ticket_status =:ticketStatus ORDER BY timestamp DESC")
    List<Ticket> getRaisedAndNotClosedTickets(String userId,String ticketStatus);

    @Query("SELECT * FROM Ticket WHERE ticket_status = 'Closed' ORDER BY timestamp DESC")
    List<Ticket> getClosedTickets();

    @Query("SELECT * FROM Ticket WHERE lead_id = :userId AND ticket_status != 'Closed' ORDER BY timestamp DESC")
    List<Ticket> getAssignedTickets(String userId);

    //    rahul karn
    @Query("SELECT * FROM Ticket WHERE lead_id = :userId AND ticket_status =:ticketStatus ORDER BY timestamp DESC")
    List<Ticket> getAssignedTickets(String userId,String ticketStatus);

    @Query("SELECT * FROM Ticket WHERE assigned_by = :userId AND ticket_status != 'Closed' ORDER BY timestamp DESC")
    List<Ticket> getTeamAssignedTickets(String userId);

//    rahul karn
    @Query("SELECT * FROM Ticket WHERE assigned_by = :userId AND ticket_status = :ticketStatus  ORDER BY timestamp DESC")
    List<Ticket> getTeamAssignedTickets(String userId,String ticketStatus);

    @Query("SELECT date, COUNT(*) as eventCount FROM TicketProgress WHERE event = :event AND date BETWEEN :fromDate AND :toDate GROUP BY date")
    List<DailyTicketEventCount> getDailyTicketEventCount(String event, String fromDate, String toDate);

    @Query("SELECT date, COUNT(*) as eventCount FROM TicketProgress WHERE event = :event AND ticket_id IN(:ticket_id) AND date BETWEEN :fromDate AND :toDate GROUP BY date")
    List<DailyTicketEventCount> getDailyTicketEventCount(String event, String fromDate, String toDate,List<String> ticket_id);
}
