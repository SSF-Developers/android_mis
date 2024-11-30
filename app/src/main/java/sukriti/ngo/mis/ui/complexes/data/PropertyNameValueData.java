package sukriti.ngo.mis.ui.complexes.data;

import java.util.ArrayList;
import java.util.List;

import sukriti.ngo.mis.dataModel.ThingDetails;

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
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_STATE;
import static sukriti.ngo.mis.utils.Nomenclature._ATTRIBUTE_CABIN_STATE_CODE;

public class PropertyNameValueData {
    public String Name = "";
    public String Value = "";

    public PropertyNameValueData(String Name, String Value){
        this.Name = Name;
        this.Value = Value;
    }
}
