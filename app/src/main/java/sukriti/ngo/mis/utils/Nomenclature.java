package sukriti.ngo.mis.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sukriti.ngo.mis.dataModel.DataDuration;
import sukriti.ngo.mis.dataModel.QuickConfigItem;
import sukriti.ngo.mis.repository.entity.AqiLumen;
import sukriti.ngo.mis.repository.entity.CmsConfig;
import sukriti.ngo.mis.repository.entity.OdsConfig;
import sukriti.ngo.mis.repository.entity.Ticket;
import sukriti.ngo.mis.repository.entity.UcemsConfig;
import sukriti.ngo.mis.ui.complexes.data.MisCommand;
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData;
import sukriti.ngo.mis.ui.login.data.UserProfile;
import sukriti.ngo.mis.ui.tickets.data.TicketAction;

public class Nomenclature {

    //Dashboard-Card-Max-Limuit
    public static final int DASHBOARD_CHILD_ITEM_LIMIT = 10;

    //Tickets
    public static final int TICKET_FILE_LIST_SIZE = 4;
    public static final String TICKET_FOLDER_RAISE = "Raise";

    public static final int TICKET_ROLE_VOID = 0;
    public static final int TICKET_ROLE_SUPER_ADMIN = 1;
    public static final int TICKET_ROLE_VENDOR_ADMIN = 2;
    public static final int TICKET_ROLE_CREATOR = 3;
    public static final int TICKET_ROLE_LEAD = 4;

    public static final String SELECT_ALL = "All";
    public static final String TICKET_STATUS_RAISED = "Raised";
    public static final String TICKET_STATUS_QUEUED = "Queued";
    public static final String TICKET_STATUS_UNQUEUED = "Un-Queued";
    public static final String TICKET_STATUS_ASSIGNED = "Assigned";
    public static final String TICKET_STATUS_OPEN = "Open";
    public static final String TICKET_STATUS_RESOLVED = "Resolved";
    public static final String TICKET_STATUS_CLOSED = "Closed";

    public static final int TICKET_ACTION_VOID = 0;
    public static final int TICKET_ACTION_ACCEPT_ASSIGNMENT = 1;
    public static final int TICKET_ACTION_ASSIGN_TO = 2;
    public static final int TICKET_ACTION_TICKET_PROGRESS = 3;
    public static final int TICKET_ACTION_MARK_RESOLVED = 4;
    public static final int TICKET_ACTION_MARK_CLOSED = 5;
    public static final int TICKET_ACTION_RE_OPEN = 6;

    public static final int TASK_ACTION_VOID = 0;
    public static final int TASK_ACTION_CREATE = 11;
    public static final int TASK_ACTION_CLOSE = 12;
    public static final int TASK_ACTION_PROGRESS = 13;

    public static final String TICKET_EVENT_RAISED = "Raised";
    public static final String TICKET_EVENT_QUEUED = "Queued";
    public static final String TICKET_EVENT_UNQUEUED = "Un-Queued";
    public static final String TICKET_EVENT_ASSIGNED = "Assigned";
    public static final String TICKET_EVENT_OPENED = "Opened";
    public static final String TICKET_EVENT_PROGRESS = "Progress";
    public static final String TICKET_EVENT_RESOLVED = "Resolved";
    public static final String TICKET_EVENT_CLOSED = "Closed";
    public static final String TICKET_EVENT_REOPENED = "Re-Opened";


    //CACHED DATA
    public static final int LIFETIME_CLIENT_LIST = 15 * 60 * 1000;
    public static final int LIFETIME_ACCESS_TREE = 24 * 60 * 60 * 1000;//15-Mins: 15*60*1000

//    24 Hr in milli
    public static final int TWENTY_FOUR_HOURS_IN_MILLI = 24 * 60 * 60 * 1000;//15-Mins: 15*60*1000
    public static final int HOURS_IN_MILLI = 60 * 60 * 1000;//15-Mins: 15*60*1000

    //IoT Publish
    public static final int PUB_TYPE_CONFIG = 1;
    public static final int PUB_TYPE_COMMAND = 2;
    public static final int PUB_TARGET_CABIN = 1;
    public static final int PUB_TARGET_CLIENT = 2;

    public static final int COMPLEX_ACCESS_LOG_SIZE = 10;
    //Thing : CABIN
    public static final String _ATTRIBUTE_CABIN_COMPLEX = "COMPLEX";
    public static final String _ATTRIBUTE_CABIN_ADDRESS = "ADDRESS";
    public static final String _ATTRIBUTE_CABIN_LATITUDE = "LATITUDE";
    public static final String _ATTRIBUTE_CABIN_LONGITUDE = "LONGITUDE";
    public static final String _ATTRIBUTE_CABIN_DATE = "DATE";
    public static final String _ATTRIBUTE_CABIN_CLIENT = "CLIENT";
    public static final String _ATTRIBUTE_CABIN_SMART_LEVEL = "SMART_LEVEL";
    public static final String _ATTRIBUTE_CABIN_CITY = "CITY";
    public static final String _ATTRIBUTE_CABIN_DISTRICT = "DISTRICT";
    public static final String _ATTRIBUTE_CABIN_STATE = "STATE";
    public static final String _ATTRIBUTE_CABIN_CITY_CODE = "CITY_CODE";
    public static final String _ATTRIBUTE_CABIN_DISTRICT_CODE = "DISTRICT_CODE";
    public static final String _ATTRIBUTE_CABIN_STATE_CODE = "STATE_CODE";
    public static final String _ATTRIBUTE_CABIN_SHORT_THING_NAME = "SHORT_THING_NAME";
    public static final String _ATTRIBUTE_CABIN_BILLING_GROUP = "BILLING_GROUP";
    //FOR THING TYPE - WC : URINAL
    public static final String _ATTRIBUTE_CABIN_USER_TYPE = "USER_TYPE";
    public static final String _ATTRIBUTE_CABIN_USAGE_CHARGE = "USAGE_CHARGE";
    public static final String _ATTRIBUTE_CABIN_NUM = "CABIN_NUM";
    //THING TYPE URINAL
    public static final String _ATTRIBUTE_CABIN_NO_OF_URINALS = "NO_OF_URINALS";
    //FOR THING TYPE BWT
    public static final String _ATTRIBUTE_CABIN_BWT_KLD = "BWT_KLD";
    public static final String _ATTRIBUTE_CABIN_BWT_LVL = "BWT_LVL";
    //CABIN_TYPE - AWS IoT
    //public static final String CABIN_TYPE_BWT = "BWT";
    public static final String CABIN_TYPE_URINAL = "URINAL";
    public static final String CABIN_TYPE_WC = "WC";
    public static final String USER_TYPE_MALE = "MALE";
    //USER_TYPE
    public static final String USER_TYPE_FEMALE = "FEMALE";
    public static final String USER_TYPE_DISABLED = "PD";
    public static final String USER_TYPE_UNIVERSAL = "U";
    public static final String USER_TYPE_BWT = "B";
    //USAGE_CHARGE_TYPE
    public static final String USAGE_CHARGE_TYPE_NONE = "NONE";
    public static final String USAGE_CHARGE_TYPE_COIN = "COIN";
    public static final String USAGE_CHARGE_TYPE_COIN_RF = "COIN_RF";
    public static final String USAGE_CHARGE_TYPE_RF = "RF";
    //BWT_LEVEL
    public static final String BWT_LEVEL_G0 = "BWT_G0";
    public static final String BWT_LEVEL_G1 = "BWT_G1";
    public static final String BWT_LEVEL_G2 = "BWT_G2";
    public static final String BWT_LEVEL_G3 = "BWT_G3";
    //CABIN_TYPE - Charts
    public static final String CABIN_TYPE_NULL = "-";
    public static final String CABIN_TYPE_MWC = "MWC";
    public static final String CABIN_TYPE_FWC = "FWC";
    public static final String CABIN_TYPE_PWC = "PWC";
/*
    public static final String CABIN_TYPE_MWC = "WC";
    public static final String CABIN_TYPE_FWC = "WC";
    public static final String CABIN_TYPE_PWC = "WC";
*/

    public static final String CABIN_TYPE_MUR = "MUR";
    public static final String CABIN_TYPE_BWT = "BWT";

    //Default Valuses
    public static final String DEFAULT_CABIN_HEALTH = "Unit working fine";

    public static final int CHART_ANIM_DURATION = 400;
    public static final int DURATION_DEFAULT_SELECTION = 3;
    public static final int TIMELINE_DURATION_DEFAULT_SELECTION = 0;
    public static int QuickAccessListLimit = 10;

    public static List<String> getDurationLabels() {
        List<String> list = new ArrayList<>();
        list.add("Today");
        list.add("Yesterday");
        list.add("Last 2 days");
        list.add("Last 7 days");
        list.add("Last 15 days");
        list.add("Last 30 days");
        list.add("Last 90 days");
        list.add("Last 180 days");
        list.add("Last 360 days");
        return list;
    }

    public static List<String> getLimitedDurationLabels() {
        List<String> list = new ArrayList<>();
        list.add("Today");
        list.add("Yesterday");
        list.add("Last 2 days");
        list.add("Last 7 days");
        list.add("Last 15 days");
        return list;
    }

    public static int getIndex(String selection, List<String> list) {
        int index = -1;
        int i = 0;
        for (String item : list) {
            if (item.compareToIgnoreCase(selection) == 0)
                index = i;
            i++;
        }
        return index;
    }

    public static int getDuration(int index) {
        int duration = 0;
        switch (index) {
            case 0: {
                duration = 0;
                break;
            }
            case 1: {
                duration = 1;
                break;
            }
            case 2: {
                duration = 2;
                break;
            }
            case 3: {
                duration = 7;
                break;
            }
            case 4: {
                duration = 15;
                break;
            }
            case 5: {
                duration = 30;
                break;
            }
            case 6: {
                duration = 90;
                break;
            }
            case 7: {
                duration = 180;
                break;
            }
            case 8: {
                duration = 360;
            }
        }
        Log.i("_durationSelection", "index: " + index +
                " duration:" + duration);
        return duration;
    }

    public static List<String> getTimelineDurationLabels() {
        List<String> list = new ArrayList<>();
        list.add("Last 7 days");
        list.add("Last 15 days");
        list.add("Last 30 days");
        list.add("Last 90 days");
        list.add("Last 180 days");
        list.add("Last 360 days");
        return list;
    }

    public static int getTimelineDuration(int index) {
        int duration = 0;
        switch (index) {
            case 0: {
                duration = 7;
                break;
            }
            case 1: {
                duration = 15;
                break;
            }
            case 2: {
                duration = 30;
                break;
            }
            case 3: {
                duration = 90;
                break;
            }
            case 4: {
                duration = 180;
                break;
            }
            case 5: {
                duration = 360;
            }
        }
        Log.i("_durationSelection", "index: " + index +
                " duration:" + duration);
        return duration;
    }


    public static DataDuration getDataDuration(int duration) {
        String durationLabel = "Last " + duration + " Days";
        Calendar calendar;
        Date to;
        Date from;

        if (duration == 0) {
            durationLabel = "Today";
            calendar = Calendar.getInstance();
            to = new Date(calendar.getTimeInMillis());
            Log.i("_misTest", "getDataDuration: "+to);
//            rahul karn
//            from = to;
            from = new Date(calendar.getTimeInMillis()- (Calendar.HOUR_OF_DAY+1)*HOURS_IN_MILLI);// TWENTY_FOUR_HOURS_IN_MILLI);

//            *********
        } else if (duration == 1) {
            durationLabel = "Yesterday";
            calendar = Calendar.getInstance();
//            calendar.add(Calendar.DAY_OF_MONTH, -1 * duration);

            Log.i("_misTest", "getDataDuration: "+calendar);
//            rahul karn
//            from =new Date(calendar.getTimeInMillis());
            from = new Date((calendar.getTimeInMillis()- (Calendar.HOUR_OF_DAY+1)*HOURS_IN_MILLI) -TWENTY_FOUR_HOURS_IN_MILLI);
//            to = from;
            to = new Date(calendar.getTimeInMillis()- (Calendar.HOUR_OF_DAY+1)*HOURS_IN_MILLI);
//            ************
        } else {
            calendar = Calendar.getInstance();
            to = new Date(calendar.getTimeInMillis());
            calendar.add(Calendar.DAY_OF_MONTH, -1 * duration);
            from = new Date(calendar.getTimeInMillis());
        }

        Log.i("_getDataDuration", durationLabel + ": from: " + from + ", to: " + to + "");
        return new DataDuration(from, to, durationLabel);
    }

    public static String getCabinType(String shortThingName) {
        if (shortThingName.toLowerCase().contains(CABIN_TYPE_MWC.toLowerCase()))
            return CABIN_TYPE_MWC;
        if (shortThingName.toLowerCase().contains(CABIN_TYPE_FWC.toLowerCase()))
            return CABIN_TYPE_FWC;
        if (shortThingName.toLowerCase().contains(CABIN_TYPE_PWC.toLowerCase()))
            return CABIN_TYPE_PWC;
        if (shortThingName.toLowerCase().contains(CABIN_TYPE_MUR.toLowerCase()))
            return CABIN_TYPE_MUR;
        if (shortThingName.toLowerCase().contains(CABIN_TYPE_BWT.toLowerCase()))
            return CABIN_TYPE_BWT;
        return CABIN_TYPE_NULL;
    }

    public static String getUserType(String shortThingName) {
        if (shortThingName.toLowerCase().contains(CABIN_TYPE_MWC.toLowerCase()))
            return USER_TYPE_MALE;
        if (shortThingName.toLowerCase().contains(CABIN_TYPE_FWC.toLowerCase()))
            return USER_TYPE_FEMALE;
        if (shortThingName.toLowerCase().contains(CABIN_TYPE_PWC.toLowerCase()))
            return USER_TYPE_DISABLED;
        if (shortThingName.toLowerCase().contains(CABIN_TYPE_MUR.toLowerCase()))
            return USER_TYPE_MALE;
        if (shortThingName.toLowerCase().contains(CABIN_TYPE_BWT.toLowerCase()))
            return USER_TYPE_BWT;
        return CABIN_TYPE_NULL;
    }

    public static boolean getCabinType(String shortThingName, String CabinType) {
        Log.i("__Bwt", "CabinType: "+CabinType.toLowerCase());
        Log.i("__Bwt", "shortThingName: "+shortThingName.toLowerCase());
        if (shortThingName.toLowerCase().contains(CabinType.toLowerCase())) {
            Log.i("__Bwt", "getCabinType: Bwt");
            return true;
        }
        Log.i("__Bwt", "getCabinType: no");
        return false;
    }

    public static List<String> getUcemsConfigOptions() {
        List<String> list = new ArrayList<>();
        list.add("Non Critical");
        list.add("Critical");
        return list;
    }

    public static List<String> getCmsConfigOptions() {
        List<String> list = new ArrayList<>();
        list.add("Disabled");
        list.add("Enabled");
        return list;
    }

    public static List<String> getPaymentModesOptions() {
        List<String> list = new ArrayList<>();
        list.add("None");
        list.add("Coin");
        list.add("RFID");
        list.add("Coin and RF");
        return list;
    }

    public static List<String> getActionOptions() {
        List<String> list = new ArrayList<>();
        list.add("Switch Off");
        list.add("Switch On");
        return list;
    }

    public static List<String> getOverrideOptions() {
        List<String> list = new ArrayList<>();
        list.add("Override Command");
        list.add("Do Not Override");
        return list;
    }

    //Topics subscribed by IoT App
    public enum PUB_TOPIC {
        CMS_CONFIG,
        CMS_CONFIG_GENERIC,
        UCEMS_CONFIG,
        UCEMS_CONFIG_GENERIC,
        ODS_CONFIG,
        ODS_CONFIG_GENERIC,
        BWT_CONFIG,
        BWT_CONFIG_GENERIC,
        DIAGNOSTICS,
        BWT_DIAGNOSTICS,
        COMMAND,
        CLIENT_TOPIC,
        CLIENT_TOPIC_GENERIC,
        WATER
    }

    public static ArrayList<MisCommand> getCommandList() {
        ArrayList<MisCommand> commands = new ArrayList<>();
        commands.add(new MisCommand("Light", "CMS Config Cabin Light", "1", "2", "10", "1"));
        commands.add(new MisCommand("Fan", "Cabin Light", "2", "2", "10", "1"));
        commands.add(new MisCommand("Flush", "Cabin Light", "3", "2", "10", "1"));
        commands.add(new MisCommand("Floor Clean", "Cabin Light", "4", "2", "10", "1"));
        commands.add(new MisCommand("Air Dryer", "Cabin Light", "5", "2", "10", "1"));
        commands.add(new MisCommand("CMS Reset", "Cabin Light", "6", "-1", "-1", "1"));
        commands.add(new MisCommand("CMS Clear Fault", "Cabin Light", "7", "-1", "-1", "1"));

        commands.add(new MisCommand("Door Lock", "Cabin Light", "8", "2", "10", "1"));
        commands.add(new MisCommand("Play Exit Audio", "Cabin Light", "9", "2", "10", "1"));
        commands.add(new MisCommand("Play Cabin Audio", "Cabin Light", "10", "2", "10", "1"));
        commands.add(new MisCommand("UCEMS Reset", "Cabin Light", "11", "-1", "-1", "1"));
        commands.add(new MisCommand("UCEMS Clear Fault", "Cabin Light", "12", "-1", "-1", "1"));

        commands.add(new MisCommand("ODS Reset", "Cabin Light", "13", "-1", "-1", "1"));
        commands.add(new MisCommand("ODS Stat Reset", "Cabin Light", "14", "-1", "-1", "1"));

        return commands;
    }
    public static ArrayList<MisCommand> getBWTCommandList() {
        ArrayList<MisCommand> commands = new ArrayList<>();
        commands.add(new MisCommand("Pump", "Bwt Pump", "1", "2", "10", "1"));
        commands.add(new MisCommand("Blower", "Bwt Blower", "2", "2", "10", "1"));
        commands.add(new MisCommand("Clear Fault", "Clear Fault", "3", "2", "10", "1"));
        commands.add(new MisCommand("Reset", "Bwt Reset", "4", "2", "10", "1"));

        return commands;
    }

    public static ArrayList<MisCommand> getQuickAccessCommandList() {
        ArrayList<MisCommand> commands = new ArrayList<>();
        commands.add(new MisCommand("Light", "Turn On cabin light for 10 seconds", "1", "2", "10", "1"));
        commands.add(new MisCommand("Flush", "Initiate cabin flush for 10 Seconds", "3", "2", "10", "1"));
        commands.add(new MisCommand("Floor Clean", "Perform floor clean operation for 10 seconds", "4", "2", "10", "1"));
        commands.add(new MisCommand("Door Lock", "Un-Lock cabin door", "8", "2", "10", "1"));
        return commands;
    }

    public static ArrayList<QuickConfigItem> getQuickConfigItemList() {
        ArrayList<QuickConfigItem> commands = new ArrayList<>();
        commands.add(new QuickConfigItem("Usage Charge Config", "Configure usage charge and payment mode settings for all units in one go.",
                QuickConfigItem.Type.USAGE_CHARGE_CONFIG));
        commands.add(new QuickConfigItem("Flush Config", "Configure full-flush, mini-flush and pre-flush settings for all units in one go.",
                QuickConfigItem.Type.FLUSH_CONFIG));
        commands.add(new QuickConfigItem("Floor Clean Config", "Configure floor-clean-count and floor-clean-time settings for all units in one go.",
                QuickConfigItem.Type.FLOOR_CLEAN_CONFIG));
        commands.add(new QuickConfigItem("Light & Fan Config", "Configure Light and Fan settings for all units in one go.",
                QuickConfigItem.Type.LIGHT_FAN_CONFIG));
        commands.add(new QuickConfigItem("Data Request Config", "Configure Data Request settings for all units in one go.",
                QuickConfigItem.Type.DATA_REQUEST_CONFIG));
        return commands;
    }

    public static List<String> getReportActions() {
        List<String> list = new ArrayList<>();
        list.add("Choose");
        list.add("Download");
        list.add("Email");
        return list;
    }

    public static List<String> getGraphReportActions() {
        List<String> list = new ArrayList<>();
        list.add("Choose");
        list.add("Download Graphs");
        list.add("Download Data");
        list.add("Email Graphs");
        list.add("Email Data");
        return list;
    }

    public static List<String> getTicketStatusList() {
        List<String> list = new ArrayList<>();
        list.add("All");
        list.add(TICKET_STATUS_QUEUED);
        list.add(TICKET_STATUS_UNQUEUED);
        list.add(TICKET_STATUS_ASSIGNED);
        list.add(TICKET_STATUS_OPEN);
        list.add(TICKET_STATUS_RESOLVED);
        list.add(TICKET_STATUS_CLOSED);
        return list;
    }
    public static List<String> getTicketStatusList(int listType) {
        List<String> list = new ArrayList<>();
        switch (listType) {
            case 10:
//            NAV_RAISED_TICKETS
                list.add(SELECT_ALL);
                list.add(TICKET_STATUS_QUEUED);
                list.add(TICKET_STATUS_UNQUEUED);
                break;
            case 22:
            case 11:
//            NAV_QUEUED_TICKETS
//            NAV_ALL_QUEUED_TICKETS
//            list.add(SELECT_ALL);
                list.add(TICKET_STATUS_QUEUED);
                break;
            case 21:
            case 12:
//            NAV_UN_QUEUED_TICKETS
//            NAV_ALL_UN_QUEUED_TICKETS
//            list.add(SELECT_ALL);
                list.add(TICKET_STATUS_UNQUEUED);
                break;
            case 14:
            case 26:
//         NAV_ALL_CLOSED_TICKET
//         NAV_CLOSED_TICKET
//            list.add(SELECT_ALL);
                list.add(TICKET_STATUS_CLOSED);
                break;
            case 24:
//         NAV_ALL_OPEN_TICKETS
//            list.add(SELECT_ALL);
                list.add(TICKET_STATUS_OPEN);
                break;
            case 25:
//            NAV_ALL_RESOLVED_TICKETS
//            list.add(SELECT_ALL);
                list.add(TICKET_STATUS_RESOLVED);
                break;
            default:
//            NAV_ALL_ASSIGNED_TICKETS==23
//            NAV_ASSIGNED_TICKETS==13
//            NAV_TEAM_ASSIGNED_TICKET==15
//            NAV_ALL_ACTIVE_TICKET==17
                list.add(SELECT_ALL);
                list.add(TICKET_STATUS_QUEUED);
                list.add(TICKET_STATUS_UNQUEUED);
                list.add(TICKET_STATUS_ASSIGNED);
                list.add(TICKET_STATUS_OPEN);
                list.add(TICKET_STATUS_RESOLVED);
                list.add(TICKET_STATUS_CLOSED);
                break;
        }
        return list;
    }

    public static List<String> getTicketCriticalLevelDisplayList() {
        List<String> list = new ArrayList<>();
        list.add("Normal: A non-critical malfunction");
        list.add("Urgent: A critical malfunction");
        list.add("Possible Fault: Possible Malfunction");
        return list;
    }

    public static List<String> getTicketCriticalLevelCodeList() {
        List<String> list = new ArrayList<>();
        list.add("Normal");
        list.add("Urgent");
        list.add("Possible Fault");
        return list;
    }

    public static String getEventDescription(String event) {
        Map<String, String> list = new HashMap<>();
        list.put(TICKET_EVENT_RAISED, "Ticket was raised with malfunction details.");
        list.put(TICKET_EVENT_ASSIGNED, "A Lead was assigned to the ticket");
        list.put(TICKET_EVENT_OPENED, "Ticket marked open. First activity recorded from the team.");
        list.put(TICKET_EVENT_PROGRESS, "Progress was submitted on the ticket.");
        list.put(TICKET_EVENT_RESOLVED, "The issue was reported to be resolved by the team");
        list.put(TICKET_EVENT_CLOSED, "The resolution was accepted and this ticket has been closed");
        list.put(TICKET_EVENT_REOPENED, "The ticket was re-opened, the malfunction surfaced again");
        String description = list.get(event);
        if (description == null)
            description = "";

        return description;
    }

    public static String getActionDescription(int action) {
        Map<Integer, String> list = new HashMap<>();
        list.put(TICKET_ACTION_ASSIGN_TO, "To assign the ticket to the selected member, type 'assign lead' in confirm action slot. You can also provide comments to update the creator.");
//        rahul karn
//        list.put(TICKET_ACTION_ACCEPT_ASSIGNMENT, "To accept 'Laed' role for this ticket, type 'accept lead' in confirm action slot. You can also provide comments to update the creator.");
        list.put(TICKET_ACTION_ACCEPT_ASSIGNMENT, "To accept 'Lead' role for this ticket, type 'accept lead' in confirm action slot. You can also provide comments to update the creator.");
        list.put(TICKET_ACTION_TICKET_PROGRESS, "Provide ticket progress comments to update the creator.");
        list.put(TICKET_ACTION_MARK_RESOLVED, "To mark the ticket 'Resolved', type 'mark resolved' in confirm action slot. You can also provide comments to update the creator.");
        list.put(TICKET_ACTION_MARK_CLOSED, "To mark the ticket 'Closed', type 'close ticket' in confirm action slot. You can also provide comments to update the creator.");
        list.put(TICKET_ACTION_RE_OPEN, "To Re-Open this ticket, type 'reopen ticket' in confirm action slot. You can also provide comments to update the creator.");

        String description = list.get(action);
        if (description == null)
            description = "";

        return description;
    }

    public static List<String> getTicketActionsNames(Ticket ticketDetail, UserProfile user) {
        List<String> actionNames = new ArrayList<>();
        Log.i("_misTest", "getTicketActionsNames: "+user);
        List<TicketAction> actions = getTicketActions(ticketDetail, user);
        Log.i("_misTest", "getTicketActionsNames: "+actions.size());
        for (TicketAction action : actions) {
            actionNames.add(action.getActionName());
            Log.i("_misTest", "getTicketActionsNames: "+actionNames);
        }
        return actionNames;
    }

    public static List<TicketAction> getTicketActions(Ticket ticketDetail, UserProfile user) {

        final TicketAction acceptAssignment = new TicketAction(
                TICKET_ACTION_ACCEPT_ASSIGNMENT, "Accept Ticket", "");
        final TicketAction assignTo = new TicketAction(
                TICKET_ACTION_ASSIGN_TO, "Assign Ticket", "");
        final TicketAction submitProgress = new TicketAction(
                TICKET_ACTION_TICKET_PROGRESS, "Submit Progress", "");
        final TicketAction markResolved = new TicketAction(
                TICKET_ACTION_MARK_RESOLVED, "Mark Resolved", "");
        final TicketAction markClosed = new TicketAction(
                TICKET_ACTION_MARK_CLOSED, "Mark Closed", "");
        final TicketAction reopenTicket = new TicketAction(
                TICKET_ACTION_RE_OPEN, "Re-Open Ticket", "");

        //Status : UnQueued; User = Admin => TICKET_ACTION_MANUAL_ASSIGNMENT
        //Status : Queued; User = [Lead,Admin] => TICKET_ACTION_ACCEPT_ASSIGNMENT
        //Status : Queued; User = Admin => TICKET_ACTION_ASSIGN_TO
        //Status : Assigned | Open; User = [Lead,Admin] => TICKET_ACTION_MARK_RESOLVED, TICKET_ACTION_TICKET_PROGRESS
        //Status : Resolved; User = [Creator,Admin] => TICKET_ACTION_MARK_CLOSED, TICKET_ACTION_RE_OPEN

        //Status : UnQueued; User = [SA] => TICKET_ACTION_ASSIGN_TO | TICKET_ACTION_TICKET_PROGRESS | TICKET_ACTION_ACCEPT_ASSIGNMENT
        //Status : Queued; User = [SA] => TICKET_ACTION_ASSIGN_TO | TICKET_ACTION_TICKET_PROGRESS | TICKET_ACTION_ACCEPT_ASSIGNMENT
        //Status : Queued; User = [VA] => TICKET_ACTION_ASSIGN_TO | TICKET_ACTION_ACCEPT_ASSIGNMENT
        //Status : Assigned; User = [Lead] => TICKET_ACTION_MARK_RESOLVED, TICKET_ACTION_TICKET_PROGRESS
        //Status : Assigned; User = [Lead+] => TICKET_ACTION_MARK_RESOLVED, TICKET_ACTION_TICKET_PROGRESS
        //Status : Open; User = [Lead] => TICKET_ACTION_MARK_RESOLVED, TICKET_ACTION_TICKET_PROGRESS
        //Status : Open; User = [Lead+] => TICKET_ACTION_MARK_RESOLVED, TICKET_ACTION_TICKET_PROGRESS
        //Status : Resolved; User = [Creator] => TICKET_ACTION_MARK_CLOSED
        //Status : Resolved; User = [Creator+] => TICKET_ACTION_MARK_CLOSED
        //Status : Closed; User = [Creator] => TICKET_ACTION_RE_OPEN
        //Status : Closed; User = [Creator+] => TICKET_ACTION_RE_OPEN

        List<TicketAction> list = new ArrayList<>();
        String ticketStatus = ticketDetail.getTicket_status();
        int userRoleForTicket = getUserRoleForTicket(ticketDetail, user);
        Log.i("_misTest", "getTicketActions: "+userRoleForTicket);
        Log.i("_misTest", "getTicketActions isunqd: "+ticketDetail.isUnQueued());
        Log.i("_misTest", "getTicketActions isqd: "+ticketDetail.isQueuedForUser());

        //UnQueued
        if (ticketStatus.compareToIgnoreCase(TICKET_STATUS_UNQUEUED) == 0
                && (userRoleForTicket == TICKET_ROLE_SUPER_ADMIN)) {
            list.add(assignTo);
            list.add(submitProgress);
            list.add(acceptAssignment);
        }

        //Queued
        if (ticketStatus.compareToIgnoreCase(TICKET_STATUS_QUEUED) == 0
                && (userRoleForTicket == TICKET_ROLE_SUPER_ADMIN)) {
            list.add(assignTo);
            list.add(submitProgress);
            list.add(acceptAssignment);
        }

        if (ticketStatus.compareToIgnoreCase(TICKET_STATUS_QUEUED) == 0
                && (userRoleForTicket == TICKET_ROLE_VENDOR_ADMIN)) {
            list.add(assignTo);
            list.add(acceptAssignment);
        }

        //Assigned
        if (ticketStatus.compareToIgnoreCase(TICKET_STATUS_ASSIGNED) == 0
                && (userRoleForTicket == TICKET_ROLE_LEAD)) {
            list.add(markResolved);
            list.add(submitProgress);
        }

        if (list.isEmpty() && ticketStatus.compareToIgnoreCase(TICKET_STATUS_ASSIGNED) == 0
                && (isSeniorToLead(ticketDetail,user))) {
            list.add(markResolved);
            list.add(submitProgress);
        }
//        **************

        //Open
        if (ticketStatus.compareToIgnoreCase(TICKET_STATUS_OPEN) == 0
                && (userRoleForTicket == TICKET_ROLE_LEAD)) {
            list.add(markResolved);
            list.add(submitProgress);
        }

        if (list.isEmpty() && ticketStatus.compareToIgnoreCase(TICKET_STATUS_OPEN) == 0
                && (isSeniorToLead(ticketDetail,user))) {
            list.add(markResolved);
            list.add(submitProgress);
        }

        //Resolved
        if (ticketStatus.compareToIgnoreCase(TICKET_STATUS_RESOLVED) == 0
                && (userRoleForTicket == TICKET_ROLE_CREATOR)) {
            list.add(markClosed);
        }

        if (list.isEmpty() && ticketStatus.compareToIgnoreCase(TICKET_STATUS_RESOLVED) == 0
                && (isSeniorToCreator(ticketDetail,user))) {
            list.add(markClosed);
        }

        // rahul karn
        if (list.isEmpty() && ticketStatus.compareToIgnoreCase(TICKET_STATUS_RESOLVED) == 0
                && (userRoleForTicket == TICKET_ROLE_SUPER_ADMIN)) {
            list.add(markClosed);
        }

        if (list.isEmpty() && ticketStatus.compareToIgnoreCase(TICKET_STATUS_RESOLVED) == 0
                && (userRoleForTicket == TICKET_ROLE_VENDOR_ADMIN)) {
            list.add(markClosed);
        }
//***********
        //Closed
        if (ticketStatus.compareToIgnoreCase(TICKET_STATUS_CLOSED) == 0
                && (userRoleForTicket == TICKET_ROLE_CREATOR)) {
            list.add(reopenTicket);
        }

        if (list.isEmpty() && ticketStatus.compareToIgnoreCase(TICKET_STATUS_RESOLVED) == 0
                && (isSeniorToCreator(ticketDetail,user))) {
            list.add(reopenTicket);
        }
        // rahul karn
        if (list.isEmpty() && ticketStatus.compareToIgnoreCase(TICKET_STATUS_CLOSED) == 0
                && (userRoleForTicket == TICKET_ROLE_SUPER_ADMIN)) {
            list.add(markClosed);
        }

        if (list.isEmpty() && ticketStatus.compareToIgnoreCase(TICKET_STATUS_CLOSED) == 0
                && (userRoleForTicket == TICKET_ROLE_VENDOR_ADMIN)) {
            list.add(markClosed);
        }
//***********
        if (list.size() > 0)
            list.add(0, new TicketAction(TICKET_ACTION_VOID, "Select Action", ""));
        return list;
    }

    public static List<TicketAction> getTaskActions(Ticket ticketDetail, UserProfile user) {

        //Status : Assigned | Open; User = [Lead,Admin] => TASK_ACTION_CREATE, TASK_ACTION_CLOSE, TASK_ACTION_PROGRESS
        //Status : Assigned | Open; User = Operator =>  TASK_ACTION_PROGRESS

        List<TicketAction> list = new ArrayList<>();
        String ticketStatus = ticketDetail.getTicket_status();
        int userRoleForTicket = getUserRoleForTicket(ticketDetail, user);


        if ((ticketStatus.compareToIgnoreCase(TICKET_STATUS_ASSIGNED) == 0 || ticketStatus.compareToIgnoreCase(TICKET_STATUS_OPEN) == 0)
                && (userRoleForTicket == TICKET_ROLE_SUPER_ADMIN || userRoleForTicket == TICKET_ROLE_LEAD)) {
            list.add(new TicketAction(TASK_ACTION_CREATE, "Create Task", ""));
            list.add(new TicketAction(TASK_ACTION_CLOSE, "Close Task", ""));
            list.add(new TicketAction(TASK_ACTION_PROGRESS, "Submit Task Progress", ""));
        }

        if ((ticketStatus.compareToIgnoreCase(TICKET_STATUS_ASSIGNED) == 0 || ticketStatus.compareToIgnoreCase(TICKET_STATUS_OPEN) == 0)
                && (userRoleForTicket == TICKET_ROLE_SUPER_ADMIN)) {
            list.add(new TicketAction(TASK_ACTION_PROGRESS, "Submit Task Progress", ""));
        }

        if (list.size() > 0)
            list.add(0, new TicketAction(TASK_ACTION_VOID, "Select Action", ""));
        return list;
    }


    public static int getUserRoleForTicket(Ticket ticketDetail, UserProfile user) {
        if (ticketDetail.getCreator_id().compareToIgnoreCase(user.getUser().getUserName()) == 0) {
            if (user.getRole() == UserProfile.Companion.getRole("Super Admin"))
                return TICKET_ROLE_SUPER_ADMIN;
            else if (user.getRole() == UserProfile.Companion.getRole("Vendor Admin"))
                return TICKET_ROLE_VENDOR_ADMIN;
            else
            return TICKET_ROLE_CREATOR;
        }else if (ticketDetail.getLead_id().compareToIgnoreCase(user.getUser().getUserName()) == 0)
            return TICKET_ROLE_LEAD;
        else if (user.getRole() == UserProfile.Companion.getRole("Super Admin"))
            return TICKET_ROLE_SUPER_ADMIN;
        else if (user.getRole() == UserProfile.Companion.getRole("Vendor Admin"))
            return TICKET_ROLE_VENDOR_ADMIN;
        else
            return TICKET_ROLE_VOID;
    }

    public static boolean isSeniorToCreator(Ticket ticketDetail, UserProfile user) {
        if(UserProfile.Companion.isClientSpecificRole((UserProfile.Companion.getRole(ticketDetail.getCreator_role())))){
            int ticketCreatorSeniority = getClientSideSeniority(ticketDetail.getCreator_role());
            int userSeniority = getClientSideSeniority(UserProfile.Companion.getRoleName(user.getRole()));
            return userSeniority > ticketCreatorSeniority;
        }else {
            int ticketCreatorSeniority = getVendorSideSeniority(ticketDetail.getCreator_role());
            int userSeniority = getVendorSideSeniority(UserProfile.Companion.getRoleName(user.getRole()));
            return userSeniority > ticketCreatorSeniority;
        }

    }

    public static boolean isSeniorToLead(Ticket ticketDetail, UserProfile user) {
        int ticketLeadSeniority = getVendorSideSeniority(ticketDetail.getLead_role());
        int userSeniority = getVendorSideSeniority(UserProfile.Companion.getRoleName(user.getRole()));
        return userSeniority > ticketLeadSeniority;
    }

    public static int getVendorSideSeniority(String role) {
        if (role.compareToIgnoreCase("Super Admin") == 0)
            return 3;
        if (role.compareToIgnoreCase("Vendor Admin") == 0)
            return 2;
        if (role.compareToIgnoreCase("Vendor Manager") == 0)
            return 1;
        else
            return 0;
    }

    public static int getClientSideSeniority(String role) {
        if (role.compareToIgnoreCase("Super Admin") == 0)
            return 4;
        if (role.compareToIgnoreCase("Client Super Admin") == 0)
            return 3;
        if (role.compareToIgnoreCase("Client Admin") == 0)
            return 2;
        if (role.compareToIgnoreCase("Client Manager") == 0)
            return 1;
        else
            return 0;
    }

    //Default Values
    public static List<PropertyNameValueData> getDefaultHealth() {
        ArrayList<PropertyNameValueData> properties = new ArrayList<>();
        properties.add(new PropertyNameValueData("Air Dryer",DEFAULT_CABIN_HEALTH));
        properties.add(new PropertyNameValueData("Choke",DEFAULT_CABIN_HEALTH));
        properties.add(new PropertyNameValueData("Fan",DEFAULT_CABIN_HEALTH));
        properties.add(new PropertyNameValueData("Floor Clean",DEFAULT_CABIN_HEALTH));
        properties.add(new PropertyNameValueData("Flush",DEFAULT_CABIN_HEALTH));
        properties.add(new PropertyNameValueData("Light",DEFAULT_CABIN_HEALTH));
        properties.add(new PropertyNameValueData("Lock",DEFAULT_CABIN_HEALTH));
        properties.add(new PropertyNameValueData("ODS",DEFAULT_CABIN_HEALTH));
        properties.add(new PropertyNameValueData("Tap",DEFAULT_CABIN_HEALTH));
        return properties;
    }

    public static AqiLumen getDefaultAqiLumen() {
        AqiLumen mAqiLumen = new AqiLumen(0,"","","","","","0","0","0","0","","","","","","","","","","","");
        return mAqiLumen;
    }

    public static List<PropertyNameValueData> getDefaultUcemsConfig(){
        UcemsConfig mUcemsConfig = new UcemsConfig(0,"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","");
        List<PropertyNameValueData> items =  mUcemsConfig.getPropertiesList();
        //Log.i("_DefaultUcemsConfig",""+new Gson().toJson(items));
        return items;
    }

    public static List<PropertyNameValueData> getDefaultCmsConfig(){
        CmsConfig mCmsConfig = new CmsConfig(0,"","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","","");
        List<PropertyNameValueData> items =  mCmsConfig.getPropertiesList();
        return items;
    }

    public static List<PropertyNameValueData> getDefaultOdsConfig(){
        OdsConfig mOdsConfig = new OdsConfig(0,"","","","","","","","","","","","","","","","");
        List<PropertyNameValueData> items =  mOdsConfig.getPropertiesList();
        return items;
    }
}
