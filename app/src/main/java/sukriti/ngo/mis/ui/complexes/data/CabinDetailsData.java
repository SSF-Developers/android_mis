package sukriti.ngo.mis.ui.complexes.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sukriti.ngo.mis.dataModel.ThingDetails;
import sukriti.ngo.mis.repository.entity.Cabin;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.BwtCabin;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.FwcCabin;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.MurCabin;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.MwcCabin;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.PwcCabin;
import sukriti.ngo.mis.ui.complexes.lambda.CabinComposition_fetchdata.ComplexCompositionLambdaResult;

import static sukriti.ngo.mis.utils.Nomenclature.CABIN_TYPE_BWT;
import static sukriti.ngo.mis.utils.Nomenclature.CABIN_TYPE_URINAL;
import static sukriti.ngo.mis.utils.Nomenclature.CABIN_TYPE_WC;
import static sukriti.ngo.mis.utils.Nomenclature.USER_TYPE_BWT;
import static sukriti.ngo.mis.utils.Nomenclature.USER_TYPE_DISABLED;
import static sukriti.ngo.mis.utils.Nomenclature.USER_TYPE_FEMALE;
import static sukriti.ngo.mis.utils.Nomenclature.USER_TYPE_MALE;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_BWT_KLD;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_BWT_LVL;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_NO_OF_URINALS;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_NUM;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_SHORT_THING_NAME;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_SMART_LEVEL;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_USAGE_CHARGE;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_USER_TYPE;

public class CabinDetailsData {

    //CABIN ATTRIBUTES
    public String Arn;
    public String Id;
    public String ThingName = "";
    public String RegistrationDate;
    public String ShortThingName;

    public String CabinType; //ThingType
    public String CabinNumber = "";
    public String UserType = "";
    public String UsageChargeType = "";
    public String SmartnessLevel;

    public String UrinalCount = "";
    public String BwtCapacity = "";
    public String BwtLevel = "";
    public String Suffix = "";
    public String ConnectionStatus = "";
    public String DisconnectReason = "";

    public List<PropertyNameValueData> displayProperties;

    public static CabinDetailsData getCabinDetails(ThingDetails thingDetails) {

        CabinDetailsData cabinDetails = new CabinDetailsData();
        cabinDetails.displayProperties = new ArrayList<>();
        cabinDetails.ThingName = thingDetails.Name;
        cabinDetails.CabinType = thingDetails.ThingType;
        cabinDetails.ShortThingName = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_SHORT_THING_NAME);
        cabinDetails.SmartnessLevel = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_SMART_LEVEL);
        cabinDetails.displayProperties.add(new PropertyNameValueData("Smartness", cabinDetails.SmartnessLevel));
        if (cabinDetails.CabinType.compareToIgnoreCase(CABIN_TYPE_WC) == 0 || cabinDetails.CabinType.compareToIgnoreCase(CABIN_TYPE_URINAL) == 0) {
            cabinDetails.UserType = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_USER_TYPE);
            cabinDetails.UsageChargeType = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_USAGE_CHARGE);
            cabinDetails.Suffix = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_NUM);
            cabinDetails.displayProperties.add(new PropertyNameValueData("Usage Charge", cabinDetails.UsageChargeType));
        }
        if (cabinDetails.CabinType.compareToIgnoreCase(CABIN_TYPE_URINAL) == 0) {
            cabinDetails.UrinalCount = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_NO_OF_URINALS);
            cabinDetails.displayProperties.add(new PropertyNameValueData("Urinal Count", cabinDetails.UrinalCount));
        }
        if (cabinDetails.CabinType.compareToIgnoreCase(CABIN_TYPE_BWT) == 0) {
            cabinDetails.BwtCapacity = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_BWT_KLD);
            cabinDetails.BwtLevel = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_BWT_LVL);
            cabinDetails.displayProperties.add(new PropertyNameValueData("Capacity", cabinDetails.BwtCapacity));
            cabinDetails.displayProperties.add(new PropertyNameValueData("Bwt Level", cabinDetails.BwtLevel));
        }
        return cabinDetails;
    }


    public String getThingName(ComplexDetailsData complexDetails) {
        String ThingName = complexDetails.UUID;//UUID_ABB_ZZZ

        //User Type (A)
        ThingName += "_" + getA(UserType);
        //Cabin Type (BB)
        ThingName += getBB(CabinType);
        //Suffix (ZZZ)
        ThingName += "_" + Suffix;
        return ThingName;
    }

    private String getA(String userType) {
        if (userType.compareToIgnoreCase(USER_TYPE_MALE) == 0)
            return "M";
        if (userType.compareToIgnoreCase(USER_TYPE_FEMALE) == 0)
            return "F";
        if (userType.compareToIgnoreCase(USER_TYPE_DISABLED) == 0)
            return "P";
        if (userType.compareToIgnoreCase(USER_TYPE_BWT) == 0)
            return "B";
        return "U";
    }

    private String getBB(String cabinType) {
        if (cabinType.compareToIgnoreCase(CABIN_TYPE_WC) == 0)
            return "WC";
        else if (cabinType.compareToIgnoreCase(CABIN_TYPE_BWT) == 0)
            return "WT";
        else //if(cabinType.compareToIgnoreCase(CABIN_TYPE_URINAL)==0)
            return "UR";
        //Unreachable
    }

    public static Cabin toDbCabinData(CabinDetailsData cabinDetailsData) {

        Cabin dbCabin = new Cabin(1, cabinDetailsData.ThingName, cabinDetailsData.CabinType, cabinDetailsData.ShortThingName,
                cabinDetailsData.SmartnessLevel, cabinDetailsData.UserType, cabinDetailsData.UsageChargeType,
                cabinDetailsData.Suffix, cabinDetailsData.UrinalCount, cabinDetailsData.BwtCapacity, cabinDetailsData.BwtLevel,
                "" + Calendar.getInstance().getTimeInMillis());

        return dbCabin;
    }


    @NotNull
    public static CabinDetailsData getCabinDetailsLambda(@Nullable MurCabin detail) {
        CabinDetailsData cabinDetails = new CabinDetailsData();
        cabinDetails.displayProperties = new ArrayList<>();
        cabinDetails.ThingName = detail.thingName;
        cabinDetails.CabinType = detail.cabinType;
        cabinDetails.ShortThingName = detail.shortThingName;
        cabinDetails.ConnectionStatus = detail.connectionStatus;
        cabinDetails.DisconnectReason = detail.disconnectReason;
        cabinDetails.SmartnessLevel = detail.smartnessLevel;

        cabinDetails.displayProperties.add(new PropertyNameValueData("Smartness", cabinDetails.SmartnessLevel));
        if (cabinDetails.CabinType.compareToIgnoreCase(CABIN_TYPE_WC) == 0 || cabinDetails.CabinType.compareToIgnoreCase(CABIN_TYPE_URINAL) == 0) {
            cabinDetails.UserType = detail.userType;
            cabinDetails.UsageChargeType = detail.usageChargeType;
            cabinDetails.Suffix = detail.suffix;
            cabinDetails.displayProperties.add(new PropertyNameValueData("Usage Charge", cabinDetails.UsageChargeType));
        }
        if (cabinDetails.CabinType.compareToIgnoreCase(CABIN_TYPE_URINAL) == 0) {
            cabinDetails.UrinalCount = detail.urinalCount;
            cabinDetails.displayProperties.add(new PropertyNameValueData("Urinal Count", cabinDetails.UrinalCount));
        }
        return cabinDetails;
    }

    @NotNull
    public static CabinDetailsData getCabinDetailsLambda(@Nullable FwcCabin detail) {
        CabinDetailsData cabinDetails = new CabinDetailsData();
        cabinDetails.displayProperties = new ArrayList<>();
        cabinDetails.ThingName = detail.thingName;
        cabinDetails.CabinType = detail.cabinType;
        cabinDetails.ShortThingName = detail.shortThingName;
        cabinDetails.ConnectionStatus = detail.connectionStatus;
        cabinDetails.DisconnectReason = detail.disconnectReason;
        cabinDetails.SmartnessLevel = detail.smartnessLevel;
        cabinDetails.displayProperties.add(new PropertyNameValueData("Smartness", cabinDetails.SmartnessLevel));
        if (cabinDetails.CabinType.compareToIgnoreCase(CABIN_TYPE_WC) == 0 || cabinDetails.CabinType.compareToIgnoreCase(CABIN_TYPE_URINAL) == 0) {
            cabinDetails.UserType = detail.userType;
            cabinDetails.UsageChargeType = detail.usageChargeType;
            cabinDetails.Suffix = detail.suffix;
            cabinDetails.displayProperties.add(new PropertyNameValueData("Usage Charge", cabinDetails.UsageChargeType));
        }
        return cabinDetails;

    }

    @NotNull
    public static CabinDetailsData getCabinDetailsLambda(@Nullable MwcCabin detail) {
        CabinDetailsData cabinDetails = new CabinDetailsData();
        cabinDetails.displayProperties = new ArrayList<>();
        cabinDetails.ThingName = detail.thingName;
        cabinDetails.CabinType = detail.cabinType;
        cabinDetails.ShortThingName = detail.shortThingName;
        cabinDetails.ConnectionStatus = detail.connectionStatus;
        cabinDetails.DisconnectReason = detail.disconnectReason;
        cabinDetails.SmartnessLevel = detail.smartnessLevel;

        cabinDetails.displayProperties.add(new PropertyNameValueData("Smartness", cabinDetails.SmartnessLevel));
        if (cabinDetails.CabinType.compareToIgnoreCase(CABIN_TYPE_WC) == 0 || cabinDetails.CabinType.compareToIgnoreCase(CABIN_TYPE_URINAL) == 0) {
            cabinDetails.UserType = detail.userType;
            cabinDetails.UsageChargeType = detail.usageChargeType;
            cabinDetails.Suffix = detail.suffix;
            cabinDetails.displayProperties.add(new PropertyNameValueData("Usage Charge", cabinDetails.UsageChargeType));
        }
        return cabinDetails;

    }

    @NotNull
    public static CabinDetailsData getCabinDetailsLambda(@Nullable PwcCabin detail) {
        CabinDetailsData cabinDetails = new CabinDetailsData();
        cabinDetails.displayProperties = new ArrayList<>();
        cabinDetails.ThingName = detail.thingName;
        cabinDetails.CabinType = detail.cabinType;
        cabinDetails.ShortThingName = detail.shortThingName;
        cabinDetails.ConnectionStatus = detail.connectionStatus;
        cabinDetails.DisconnectReason = detail.disconnectReason;
        cabinDetails.SmartnessLevel = detail.smartnessLevel;
        cabinDetails.displayProperties.add(new PropertyNameValueData("Smartness", cabinDetails.SmartnessLevel));
        if (cabinDetails.CabinType.compareToIgnoreCase(CABIN_TYPE_WC) == 0 || cabinDetails.CabinType.compareToIgnoreCase(CABIN_TYPE_URINAL) == 0) {
            cabinDetails.UserType = detail.userType;
            cabinDetails.UsageChargeType = detail.usageChargeType;
            cabinDetails.Suffix = detail.suffix;
            cabinDetails.displayProperties.add(new PropertyNameValueData("Usage Charge", cabinDetails.UsageChargeType));
        }
        return cabinDetails;

    }

    @NotNull
    public static CabinDetailsData getCabinDetailsLambda(@Nullable BwtCabin detail) {
        CabinDetailsData cabinDetails = new CabinDetailsData();
        cabinDetails.displayProperties = new ArrayList<>();
        cabinDetails.ThingName = detail.thingName;
        cabinDetails.CabinType = detail.cabinType;
        cabinDetails.ShortThingName = detail.shortThingName;
        cabinDetails.ConnectionStatus = detail.connectionStatus;
        cabinDetails.DisconnectReason = detail.disconnectReason;
        cabinDetails.SmartnessLevel = detail.smartnessLevel;
        cabinDetails.displayProperties.add(new PropertyNameValueData("Smartness", cabinDetails.SmartnessLevel));
        if (cabinDetails.CabinType.compareToIgnoreCase(CABIN_TYPE_BWT) == 0) {
            cabinDetails.BwtCapacity = detail.bwtCapacity;
            cabinDetails.BwtLevel = detail.bwtLevel;
            cabinDetails.displayProperties.add(new PropertyNameValueData("Capacity", cabinDetails.BwtCapacity));
            cabinDetails.displayProperties.add(new PropertyNameValueData("Bwt Level", cabinDetails.BwtLevel));
        }
        return cabinDetails;

    }
}
