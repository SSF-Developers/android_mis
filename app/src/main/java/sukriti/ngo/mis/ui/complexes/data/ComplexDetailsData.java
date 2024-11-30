package sukriti.ngo.mis.ui.complexes.data;

import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sukriti.ngo.mis.dataModel.ThingDetails;
import sukriti.ngo.mis.repository.entity.Complex;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.BwtCabin;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.FwcCabin;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.MurCabin;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.MwcCabin;
import sukriti.ngo.mis.ui.complexes.data.lambdaData.PwcCabin;

import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_ADDRESS;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_BILLING_GROUP;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_CITY;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_CITY_CODE;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_CLIENT;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_COMPLEX;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_DATE;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_DISTRICT;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_DISTRICT_CODE;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_LATITUDE;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_LONGITUDE;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_SMART_LEVEL;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_STATE;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_STATE_CODE;

public class ComplexDetailsData implements Cloneable {
    public int totalCabins = 0;

    //LOCATION
    public String ComplexName = "";
    public String StateName = "";
    public String DistrictName = "";
    public String CityName = "";
    public String StateCode = "";
    public String DistrictCode = "";
    public String CityCode = "";
    public String Address = "";
    public String Lat = "";
    public String Lon = "";

    //CLIENT
    public String Client = "";
    public String BillingGroup = "";
    public String Date = "";

    //ATTRIBUTES
    public String DeviceType = "Toilet";
    public String CommissioningStatus = "";
    public String WcCountMale = "";
    public String WcCountFemale = "";
    public String WcCountPD = "";
    public String Urinals = "";
    public String UrinalCabins = "";
    public String BWT = "";
    public String NapkinVmCount = "";
    public String NapkinVmManufacturer = "";
    public String NapkinIncineratorCount = "";
    public String NapkinIncineratorManufacturer = "";
    public String KioskArea = "";
    public String WaterAtmCapacity = "";
    public String SupervisorRoomSize = "";
    public String UUID = "";

    //PARTNERS & PROVIDERS
    public String Manufacturer = "";
    public String TechProvider = "";
    public String CivilPartner = "";
    public String OmPartner = "";

    public List<CabinDetailsData> mwcCabins = new ArrayList<>();
    public List<CabinDetailsData> fwcCabins = new ArrayList<>();
    public List<CabinDetailsData> pwcCabins = new ArrayList<>();
    public List<CabinDetailsData> murCabins = new ArrayList<>();
    public List<CabinDetailsData> bwtCabins = new ArrayList<>();

    public  static ComplexDetailsData getComplexDetailsFromCabinAttributes(ThingDetails thingDetails){

        ComplexDetailsData complexDetails = new ComplexDetailsData();
        complexDetails.BillingGroup = thingDetails.BillingGroup;
        complexDetails.ComplexName = thingDetails.Name;
        complexDetails.ComplexName = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_COMPLEX);

        complexDetails.Address = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_ADDRESS);
        complexDetails.Lat = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_LATITUDE);
        complexDetails.Lon = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_LONGITUDE);
        complexDetails.Date = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_DATE);
        complexDetails.Client = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_CLIENT);
        complexDetails.StateName = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_STATE);
        complexDetails.DistrictName = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_DISTRICT);
        complexDetails.CityName = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_CITY);
        complexDetails.StateCode = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_STATE_CODE);
        complexDetails.DistrictCode = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_DISTRICT_CODE);
        complexDetails.CityCode = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_CITY_CODE);
        complexDetails.BillingGroup = thingDetails.AttributesMap.get(_ATTRIBUTE_CABIN_BILLING_GROUP);
        return complexDetails;
    }


    public  static Complex toDbComplexData(ComplexDetailsData complexDetailsData){

        Complex dbComplex = new Complex(1,complexDetailsData.ComplexName,complexDetailsData.Address,complexDetailsData.Lat,complexDetailsData.Lon,complexDetailsData.Date,
                complexDetailsData.Client,complexDetailsData.StateName,complexDetailsData.DistrictName,complexDetailsData.CityName,complexDetailsData.StateCode,complexDetailsData.DistrictCode,
                complexDetailsData.CityCode,complexDetailsData.BillingGroup, ""+Calendar.getInstance().getTimeInMillis());
        return dbComplex;
    }


    public static ComplexDetailsData getComplexDetailsFromCabinAttributes_lambda(ComplexDetailsData data,MurCabin cabin) {
            ComplexDetailsData complexDetails = data;
            complexDetails.BillingGroup = cabin.billingGroup;
            complexDetails.ComplexName = cabin.complex;

            complexDetails.Address = cabin.address;
            complexDetails.Lat = cabin.latitude;
            complexDetails.Lon = cabin.longitude;
            complexDetails.Date = cabin.date;
            complexDetails.Client = cabin.client;
            complexDetails.StateName = cabin.state;
            complexDetails.DistrictName = cabin.district;
            complexDetails.CityName = cabin.city;
            complexDetails.StateCode = cabin.stateCode;
            complexDetails.DistrictCode = cabin.districtCode;
            complexDetails.CityCode = cabin.cityCode;
            complexDetails.BillingGroup = cabin.billingGroup;
            return complexDetails;
    }

    public static ComplexDetailsData getComplexDetailsFromCabinAttributes_lambda(ComplexDetailsData data,MwcCabin cabin) {
            ComplexDetailsData complexDetails = data;
            complexDetails.BillingGroup = cabin.billingGroup;
            complexDetails.ComplexName = cabin.complex;

            complexDetails.Address = cabin.address;
            complexDetails.Lat = cabin.latitude;
            complexDetails.Lon = cabin.longitude;
            complexDetails.Date = cabin.date;
            complexDetails.Client = cabin.client;
            complexDetails.StateName = cabin.state;
            complexDetails.DistrictName = cabin.district;
            complexDetails.CityName = cabin.city;
            complexDetails.StateCode = cabin.stateCode;
            complexDetails.DistrictCode = cabin.districtCode;
            complexDetails.CityCode = cabin.cityCode;
            complexDetails.BillingGroup = cabin.billingGroup;
            return complexDetails;
    }

    public static ComplexDetailsData getComplexDetailsFromCabinAttributes_lambda(ComplexDetailsData data,FwcCabin cabin) {
            ComplexDetailsData complexDetails = data;
            complexDetails.BillingGroup = cabin.billingGroup;
            complexDetails.ComplexName = cabin.complex;

            complexDetails.Address = cabin.address;
            complexDetails.Lat = cabin.latitude;
            complexDetails.Lon = cabin.longitude;
            complexDetails.Date = cabin.date;
            complexDetails.Client = cabin.client;
            complexDetails.StateName = cabin.state;
            complexDetails.DistrictName = cabin.district;
            complexDetails.CityName = cabin.city;
            complexDetails.StateCode = cabin.stateCode;
            complexDetails.DistrictCode = cabin.districtCode;
            complexDetails.CityCode = cabin.cityCode;
            complexDetails.BillingGroup = cabin.billingGroup;
            return complexDetails;
    }

    public static ComplexDetailsData getComplexDetailsFromCabinAttributes_lambda(ComplexDetailsData data,PwcCabin cabin) {
            ComplexDetailsData complexDetails = data;
            complexDetails.BillingGroup = cabin.billingGroup;
            complexDetails.ComplexName = cabin.complex;

            complexDetails.Address = cabin.address;
            complexDetails.Lat = cabin.latitude;
            complexDetails.Lon = cabin.longitude;
            complexDetails.Date = cabin.date;
            complexDetails.Client = cabin.client;
            complexDetails.StateName = cabin.state;
            complexDetails.DistrictName = cabin.district;
            complexDetails.CityName = cabin.city;
            complexDetails.StateCode = cabin.stateCode;
            complexDetails.DistrictCode = cabin.districtCode;
            complexDetails.CityCode = cabin.cityCode;
            complexDetails.BillingGroup = cabin.billingGroup;
            return complexDetails;
    }

    public static ComplexDetailsData getComplexDetailsFromCabinAttributes_lambda(ComplexDetailsData data,BwtCabin cabin) {
            ComplexDetailsData complexDetails = data;
            complexDetails.BillingGroup = cabin.billingGroup;
            complexDetails.ComplexName = cabin.complex;

            complexDetails.Address = cabin.address;
            complexDetails.Lat = cabin.latitude;
            complexDetails.Lon = cabin.longitude;
            complexDetails.Date = cabin.date;
            complexDetails.Client = cabin.client;
            complexDetails.StateName = cabin.state;
            complexDetails.DistrictName = cabin.district;
            complexDetails.CityName = cabin.city;
            complexDetails.StateCode = cabin.stateCode;
            complexDetails.DistrictCode = cabin.districtCode;
            complexDetails.CityCode = cabin.cityCode;
            complexDetails.BillingGroup = cabin.billingGroup;
            return complexDetails;
    }
}
