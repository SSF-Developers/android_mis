package sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel;

import android.content.Context;

public class IotCabinDetails {
    public static final String CABIN_TYPE_BWT = "BWT";
    public static final String CABIN_TYPE_URINAL = "URINAL";
    public static final String CABIN_TYPE_WC = "WC";
    public static final String USER_TYPE_MALE = "MALE";
    public static final String USER_TYPE_FEMALE = "FEMALE";
    public static final String USER_TYPE_DISABLED = "PD";
    //    public static final String USER_TYPE_UNIVERSAL = "U";
    public static final String USER_TYPE_BWT = "B";
    public static final String USAGE_CHARGE_TYPE_NONE = "NONE";
    public static final String USAGE_CHARGE_TYPE_COIN = "COIN";
    public static final String USAGE_CHARGE_TYPE_COIN_RF = "COIN_RF";
    public static final String USAGE_CHARGE_TYPE_RF = "RF";
    public static final String BWT_LEVEL_G0 = "BWT_G0";
    public static final String BWT_LEVEL_G1 = "BWT_G1";
    public static final String BWT_LEVEL_G2 = "BWT_G2";
    public static final String BWT_LEVEL_G3 = "BWT_G3";
    public boolean isValidated = false;
    public Context context;


    //CABIN ATTRIBUTES
    public String ThingName = "";
    public String CabinName = "Cabin Name";
    public String CabinType = "";
    public String UserType = "";
    public String UsageChargeType = "";
    public String UrinalCount = "";
    public String BwtCapacity = "";
    public String BwtLevel= "";
    public String Suffix= "";

}
