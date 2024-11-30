package sukriti.ngo.mis.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import sukriti.ngo.mis.dataModel.DbSyncStatus
import sukriti.ngo.mis.dataModel.dynamo_db.Country
import sukriti.ngo.mis.repository.data.LatestTimestampData
import sukriti.ngo.mis.repository.entity.QuickAccess
import sukriti.ngo.mis.repository.entity.Ticket
import sukriti.ngo.mis.ui.dashboard.data.UiResult
import sukriti.ngo.mis.ui.login.data.UserProfile
import java.util.*

class SharedPrefsClient(context: Context?) {

    private lateinit var mEditor: Editor
    private lateinit var mPrefs: SharedPreferences
    private val PREFS_NAME = "sukriti-mis"

    init {
        if (context != null) {
            mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            mEditor = mPrefs.edit()
        }

    }

    fun clearAllData() {
        mEditor.clear()
        mEditor.apply()
    }

    fun saveUserDetails(userProfile: UserProfile) {
        userProfile.organisation.client
        mEditor.putString("userProfile.role", UserProfile.getRoleName(userProfile.role))
        mEditor.putString("organisation.name", userProfile.organisation.name)
        mEditor.putString("organisation.client", userProfile.organisation.client)
        mEditor.putString("user.userName", userProfile.user.userName)
        mEditor.putString("user.name", userProfile.user.name)
        mEditor.putString("user.gender", userProfile.user.gender)
        mEditor.putString("user.address", userProfile.user.address)
        mEditor.putString("communication.email", userProfile.communication.email)
        mEditor.putString("communication.phoneNumber", userProfile.communication.phoneNumber)
        mEditor.apply()
    }

    fun getUserDetails(): UserProfile {
        val userProfile = UserProfile()
        userProfile.role = UserProfile.getRole(mPrefs.getString("userProfile.role", "") ?: "")
        userProfile.organisation.name = mPrefs.getString("organisation.name", "") ?: ""
        userProfile.organisation.client = mPrefs.getString("organisation.client", "") ?: ""
        userProfile.user.userName = mPrefs.getString("user.userName", "") ?: ""
        userProfile.user.name = mPrefs.getString("user.name", "") ?: ""
        userProfile.user.gender = mPrefs.getString("user.gender", "") ?: ""
        userProfile.user.address = mPrefs.getString("user.address", "") ?: ""
        userProfile.communication.email = mPrefs.getString("communication.email", "") ?: ""
        userProfile.communication.phoneNumber = mPrefs.getString("communication.phoneNumber", "") ?: ""
        return userProfile
    }

    fun saveLastSynTimestamp(timestamp: Long) {
        mEditor.putLong("lastSynTimestamp",timestamp)
        mEditor.apply()
    }

    fun getLastSynTimestamp():Long {
        return mPrefs.getLong("lastSynTimestamp", 0)
    }

    fun saveDbSyncStatus(dbSyncStatus: DbSyncStatus) {
        mEditor.putLong(
            "lastTimestamp.healthTimestamp",
            dbSyncStatus.lastTimestamp.healthTimestamp
        )
        mEditor.putLong(
            "lastTimestamp.BwtHealthTimestamp",
            dbSyncStatus.lastTimestamp.BwtHealthTimestamp
        )
        mEditor.putLong(
            "lastTimestamp.AqiLumenTimestamp",
            dbSyncStatus.lastTimestamp.AqiLumenTimestamp
        )
        mEditor.putLong(
            "lastTimestamp.UcemsConfigTimestamp",
            dbSyncStatus.lastTimestamp.UcemsConfigTimestamp
        )
        mEditor.putLong(
            "lastTimestamp.OdsConfigTimestamp",
            dbSyncStatus.lastTimestamp.OdsConfigTimestamp
        )
        mEditor.putLong(
            "lastTimestamp.CmsConfigTimestamp",
            dbSyncStatus.lastTimestamp.CmsConfigTimestamp
        )
        mEditor.putLong(
            "lastTimestamp.ClientRequestTimestamp",
            dbSyncStatus.lastTimestamp.ClientRequestTimestamp
        )
        mEditor.putLong(
            "lastTimestamp.BwtConfigTimestamp",
            dbSyncStatus.lastTimestamp.BwtConfigTimestamp
        )
        mEditor.putLong(
            "lastTimestamp.UsageProfileTimestamp",
            dbSyncStatus.lastTimestamp.UsageProfileTimestamp
        )
        mEditor.putLong(
            "lastTimestamp.ResetProfileTimestamp",
            dbSyncStatus.lastTimestamp.ResetProfileTimestamp
        )
        mEditor.putLong(
            "lastTimestamp.BwtProfileTimestamp",
            dbSyncStatus.lastTimestamp.BwtProfileTimestamp
        )
        mEditor.putBoolean("dbSyncStatus.isNewLogin", dbSyncStatus.isNewLogin)
        mEditor.apply()
    }

    fun clearDbSyncStatus() {
        mEditor.putLong(
            "lastTimestamp.healthTimestamp",
            0
        )
        mEditor.putLong(
            "lastTimestamp.BwtHealthTimestamp",
            0
        )
        mEditor.putLong(
            "lastTimestamp.AqiLumenTimestamp",
            0
        )
        mEditor.putLong(
            "lastTimestamp.UcemsConfigTimestamp",
            0
        )
        mEditor.putLong(
            "lastTimestamp.OdsConfigTimestamp",
            0
        )
        mEditor.putLong(
            "lastTimestamp.CmsConfigTimestamp",
           0
        )
        mEditor.putLong(
            "lastTimestamp.ClientRequestTimestamp",
            0
        )
        mEditor.putLong(
            "lastTimestamp.BwtConfigTimestamp",
            0
        )
        mEditor.putLong(
            "lastTimestamp.UsageProfileTimestamp",
            0
        )
        mEditor.putLong(
            "lastTimestamp.ResetProfileTimestamp",
            0
        )
        mEditor.putLong(
            "lastTimestamp.BwtProfileTimestamp",
            0
        )
        mEditor.apply()
    }

    fun getDbSyncStatus(): DbSyncStatus {
        val timestampData = LatestTimestampData(
            mPrefs.getLong("lastTimestamp.healthTimestamp", 0L),
            mPrefs.getLong("lastTimestamp.BwtHealthTimestamp", 0L),
            mPrefs.getLong("lastTimestamp.AqiLumenTimestamp", 0L),
            mPrefs.getLong("lastTimestamp.UcemsConfigTimestamp", 0L),
            mPrefs.getLong("lastTimestamp.OdsConfigTimestamp", 0L),
            mPrefs.getLong("lastTimestamp.CmsConfigTimestamp", 0L),
            mPrefs.getLong("lastTimestamp.ClientRequestTimestamp", 0L),
            mPrefs.getLong("lastTimestamp.BwtConfigTimestamp", 0L),
            mPrefs.getLong("lastTimestamp.UsageProfileTimestamp", 0L),
            mPrefs.getLong("lastTimestamp.ResetProfileTimestamp", 0L),
            mPrefs.getLong("lastTimestamp.BwtProfileTimestamp", 0L)
        )


        val isNewLogin = mPrefs.getBoolean("dbSyncStatus.isNewLogin", true)
        return DbSyncStatus(timestampData, isNewLogin)
    }

    fun saveAccessibleComplexList(complexList: List<String>) {
        val ja = Gson().toJson(complexList)
        mEditor.putString("accessibleComplexList", ja.toString())
        mEditor.apply()
    }

    fun getAccessibleComplexList(): List<String> {
        val accessibleComplexList = mPrefs.getString("accessibleComplexList", "[]")
        val typeToken = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(accessibleComplexList, typeToken)
    }

    fun saveQuickAccessSelection(data: QuickAccess?) {
        mEditor.putString("quickAccessSelection",Gson().toJson(data))
        mEditor.apply()

    }

    fun getQuickAccessSelection(): QuickAccess {
        val quickAccess: QuickAccess
        val quickAccessStr = mPrefs.getString("quickAccessSelection","{}")
        quickAccess = Gson().fromJson<QuickAccess>(quickAccessStr, QuickAccess::class.java)
        return quickAccess
    }



    fun saveSelectionTree(mCountry: Country?) {
        mEditor.putString("selectionTree",Gson().toJson(mCountry))
        mEditor.apply()
        saveSelectionTreeStatus(true)
        Log.i("_selectionStatus","SP-Client saveSelectionTree()")
    }

    fun getSelectionTree(): Country {
        val accessTreeStr = mPrefs.getString("selectionTree", "{}")
        return Gson().fromJson(accessTreeStr, Country::class.java)
    }
    fun saveSelectionTreeStatus(status: Boolean) {
        mEditor.putBoolean("selectionTreeStatus",status)
        mEditor.apply()
    }

    fun getSelectionTreeStatus():Boolean {
        return mPrefs.getBoolean("selectionTreeStatus", false)
    }

    //Cached Client List
    fun getClientList(): ArrayList<String> {
        val clientListStr = mPrefs.getString("clientList", "[]")
        return Gson().fromJson<ArrayList<String>>(clientListStr, ArrayList::class.java)
    }

    fun getClientListTimestamp():Long {
        return mPrefs.getLong("clientListTimestamp", 0)
    }

    fun saveClientList(clientList: ArrayList<String>) {
        mEditor.putString("clientList",Gson().toJson(clientList))
        mEditor.putLong("clientListTimestamp",Calendar.getInstance().timeInMillis)
        mEditor.apply()
    }

    //Cached Access Tree
    fun getAccessTree(): Country {
        val accessTreeStr = mPrefs.getString("accessTree", "{}")
        return Gson().fromJson(accessTreeStr, Country::class.java)
    }

    fun getAccessTreeTimestamp():Long {
        return mPrefs.getLong("accessTreeTimestamp", 0)
    }

    fun saveAccessTree(mCountry: Country?) {
        mEditor.putString("accessTree",Gson().toJson(mCountry))
        mEditor.putLong("accessTreeTimestamp",Calendar.getInstance().timeInMillis)
        mEditor.apply()
//        saveAccessTreeStatus(true)
    }

    fun saveAccessTreeTimeStamp(timeInMilliSec : Long){
        mEditor.putLong("accessTreeTimestamp",0)
    }

    fun clearAccessTreeTimeStamp(){
        mEditor.putLong("accessTreeTimestamp",0)
        mEditor.apply()
    }

    fun setSelectedTicket(selectedTicketData: Ticket) {
        mEditor.putString("selectedTicket",Gson().toJson(selectedTicketData))
        mEditor.apply()
    }

    fun getSelectedTicket(): Ticket {
        val selectedTicketStr = mPrefs.getString("selectedTicket", "{}")
        return Gson().fromJson(selectedTicketStr, Ticket::class.java)
    }

    fun setSelectedTicketNewDataFlag(flag: Boolean) {
        mEditor.putBoolean("selectedTicketNewDataFlag",flag)
        mEditor.apply()
    }

    fun getSelectedTicketNewDataFlag():Boolean {
       return mPrefs.getBoolean("selectedTicketNewDataFlag",true)
    }

//    fun saveAccessTreeStatus(status: Boolean) {
//        mEditor.putBoolean("accessTreeStatus",status)
//        mEditor.apply()
//    }
//
//    fun getAccessTreeStatus():Boolean {
//        return mPrefs.getBoolean("accessTreeStatus", false)
//    }


    fun saveUiResult(data: UiResult?) {
        Log.i("saveUiResult", "saveUiResult: "+Gson().toJson(data))
        mEditor.putString("uiResult",Gson().toJson(data))
        mEditor.apply()

    }

    fun getUiResult(): UiResult {
        val uiResult: UiResult
        val uiResultAccessStr = mPrefs.getString("uiResult","")
        uiResult = Gson().fromJson<UiResult>(uiResultAccessStr, UiResult::class.java)
        Log.i("saveUiResult", "getUiResult: "+Gson().toJson(uiResult))
        return uiResult
    }
}