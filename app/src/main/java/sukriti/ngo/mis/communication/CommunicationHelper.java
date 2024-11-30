package sukriti.ngo.mis.communication;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sukriti.ngo.mis.repository.entity.Cabin;
import sukriti.ngo.mis.repository.entity.Complex;
import sukriti.ngo.mis.ui.complexes.data.MisCommand;
import sukriti.ngo.mis.ui.complexes.data.PayloadMetaData;
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData;
import sukriti.ngo.mis.ui.login.data.UserProfile;
import sukriti.ngo.mis.utils.Nomenclature;

import static sukriti.ngo.mis.utils.Nomenclature.getCabinType;

public class CommunicationHelper {



    public static String getTopicName(Nomenclature.PUB_TOPIC topicReference, Complex complex, Cabin cabin) {

        String stateCode = complex.getStateCode();
        String districtCode = complex.getDistrictCode();
        String cityCode = complex.getCityCode();
        String clientName = complex.getClient();
        String complexName = complex.getComplexName().length() <= 20 ? complex.getComplexName(): complex.getComplexName().substring(0, 20);
        String thingName = cabin.getThingName().substring(cabin.getThingName().length() - 7);

        String START = "TOILETS/"; //"TOILETS/"
        String topicPrefix = START +
                stateCode + "/" + districtCode + "/" + cityCode + "/" +
                clientName + "/" + complexName + "/" + thingName + "/";
        String topicNoThingNamePrefix = START +
                stateCode + "/" + districtCode + "/" + cityCode + "/" +
                clientName + "/" + complexName + "/";
        String topicPrefixClient = START + clientName + "/";
        HashMap<Nomenclature.PUB_TOPIC,String> topics = new HashMap<>();
        topics.put(Nomenclature.PUB_TOPIC.CMS_CONFIG,topicPrefix + "TOPIC_SSF_READ_CMS_CONFIG");
        topics.put(Nomenclature.PUB_TOPIC.UCEMS_CONFIG,topicPrefix + "TOPIC_SSF_READ_UCEMS_CONFIG");
        topics.put(Nomenclature.PUB_TOPIC.ODS_CONFIG,    topicPrefix + "TOPIC_SSF_READ_ODS_CONFIG");
        topics.put(Nomenclature.PUB_TOPIC.BWT_CONFIG,topicPrefix + "TOPIC_SSF_READ_BWT_CONFIG");
        topics.put(Nomenclature.PUB_TOPIC.DIAGNOSTICS,topicPrefix + "TOPIC_SSF_DIAG");
        topics.put(Nomenclature.PUB_TOPIC.BWT_DIAGNOSTICS,topicPrefix + "TOPIC_SSF_DIAG_BWT");
        topics.put(Nomenclature.PUB_TOPIC.COMMAND,topicPrefix + "TOPIC_SSF_COMMAND");
        topics.put(Nomenclature.PUB_TOPIC.CLIENT_TOPIC,topicPrefix + "TOPIC_SSF_READ_CLIENT_TOPIC");
//        topics.put(Nomenclature.PUB_TOPIC.CLIENT_TOPIC_GENERIC,topicPrefixClient + "TOPIC_SSF_READ_CLIENT_TOPIC");
//        topics.put(Nomenclature.PUB_TOPIC.CMS_CONFIG_GENERIC,topicPrefixClient + "TOPIC_SSF_READ_CMS_CONFIG");
//        topics.put(Nomenclature.PUB_TOPIC.UCEMS_CONFIG_GENERIC,topicPrefixClient + "TOPIC_SSF_READ_UCEMS_CONFIG");
//        topics.put(Nomenclature.PUB_TOPIC.ODS_CONFIG_GENERIC,topicPrefixClient + "TOPIC_SSF_READ_ODS_CONFIG");
//        topics.put(Nomenclature.PUB_TOPIC.BWT_CONFIG_GENERIC,topicPrefixClient + "TOPIC_SSF_READ_BWT_CONFIG");
        topics.put(Nomenclature.PUB_TOPIC.WATER,topicNoThingNamePrefix + "TOPIC_SSF_WATER");

        String topic = topics.get(topicReference);
        Log.i("__submit","topic: "+topic);
        return topic;
    }
    public static String getTopicName(Nomenclature.PUB_TOPIC topicReference,String clientName) {

        String START = "TEST/"; //"TOILETS/"
        String topicPrefixClient = START + clientName + "/";
        HashMap<Nomenclature.PUB_TOPIC,String> topics = new HashMap<>();
        topics.put(Nomenclature.PUB_TOPIC.CLIENT_TOPIC_GENERIC,topicPrefixClient + "TOPIC_SSF_READ_CLIENT_TOPIC");
        topics.put(Nomenclature.PUB_TOPIC.CMS_CONFIG_GENERIC,topicPrefixClient + "TOPIC_SSF_READ_CMS_CONFIG");
        topics.put(Nomenclature.PUB_TOPIC.UCEMS_CONFIG_GENERIC,topicPrefixClient + "TOPIC_SSF_READ_UCEMS_CONFIG");
        topics.put(Nomenclature.PUB_TOPIC.ODS_CONFIG_GENERIC,topicPrefixClient + "TOPIC_SSF_READ_ODS_CONFIG");
        topics.put(Nomenclature.PUB_TOPIC.BWT_CONFIG_GENERIC,topicPrefixClient + "TOPIC_SSF_READ_BWT_CONFIG");

        String topic = topics.get(topicReference);
        Log.i("__submit","topic: "+topic);
        return topic;
    }

    public static JSONObject getUcemsPayload(List<PropertyNameValueData> configData, PayloadMetaData metaData){
        HashMap<String,String> mapUiLabelToPayloadLabel = new HashMap<>();
        mapUiLabelToPayloadLabel.put("Entry Charge","Entrychargeamount");
        mapUiLabelToPayloadLabel.put("Payment Mode","Cabinpaymentmode");
        mapUiLabelToPayloadLabel.put("Air Dryer","Edis_airDryr");
        mapUiLabelToPayloadLabel.put("Choke","Edis_choke");
        mapUiLabelToPayloadLabel.put("CMS","Edis_cms");
        mapUiLabelToPayloadLabel.put("Fan","Edis_fan");
        mapUiLabelToPayloadLabel.put("Floor","Edis_floor");
        mapUiLabelToPayloadLabel.put("Flush","Edis_flush");
        mapUiLabelToPayloadLabel.put("Fresh Water","Edis_freshWtr");
        mapUiLabelToPayloadLabel.put("Recycled Water","Edis_recWtr");
        mapUiLabelToPayloadLabel.put("Light","Edis_light");
        mapUiLabelToPayloadLabel.put("Lock","Edis_lock");
        mapUiLabelToPayloadLabel.put("Ods","Edis_ods");
        mapUiLabelToPayloadLabel.put("Tap","Edis_tap");
        mapUiLabelToPayloadLabel.put("Exit Door Trigger Timer","Exitdoortriggertimer");
        mapUiLabelToPayloadLabel.put("Feedback Expiry Time","Feedbackexpirytime");
        mapUiLabelToPayloadLabel.put("Occ Wait Expiry Time","Occwaitexpirytime");
        mapUiLabelToPayloadLabel.put("Collect Expiry Time","Collexpirytime");

        JSONObject payload = new JSONObject();
        String payloadLabel,payloadValue;
        try {
            for(PropertyNameValueData item : configData){

                payloadLabel = mapUiLabelToPayloadLabel.get(item.Name);
                payloadValue = item.Value;
                Log.i("__ucemsPayload",""+item.Name +"/"+payloadLabel +": "+ item.Value+"/"+payloadValue);

                payload.put(mapUiLabelToPayloadLabel.get(item.Name),payloadValue);
            }
            payload.put("THING_NAME",metaData.getThingName());
            payload.put("cabin_type",metaData.getCabinType());
            payload.put("user_type",metaData.getUserType());
        }catch (JSONException e){
            e.printStackTrace();
            Log.e("__ucemsPayload","error: "+e.getMessage());
        }
        Log.i("__ucemsPayload",""+payload);
        return payload;
    }

    public static JSONObject getCmsPayload(List<PropertyNameValueData> configData, PayloadMetaData metaData){

        HashMap<String,String> mapUiLabelToPayloadLabel = new HashMap<>();
        mapUiLabelToPayloadLabel.put("Air Dryer Auto On Timer","Airdryerautoontimer");
        mapUiLabelToPayloadLabel.put("Air Dryer Duration Timer","Airdryerdurationtimer");
        mapUiLabelToPayloadLabel.put("Auto Air Dryer Enabled","Autoairdryerenabled");
        mapUiLabelToPayloadLabel.put("Auto Fan Enabled","Autofanenabled");
        mapUiLabelToPayloadLabel.put("Auto Floor Enabled","Autofloorenabled");
        mapUiLabelToPayloadLabel.put("Auto Full Flush Enabled","Autofullflushenabled");
        mapUiLabelToPayloadLabel.put("Auto Light Enabled","Autolightenabled");
        mapUiLabelToPayloadLabel.put("Auto Mini flush Enabled","Autominiflushenabled");
        mapUiLabelToPayloadLabel.put("Auto Pre Flush","Autopreflush");
        mapUiLabelToPayloadLabel.put("Axit After Away Timer","Exitafterawaytimer");
        mapUiLabelToPayloadLabel.put("Fan Auto Off Timer","Fanautoofftimer");
        mapUiLabelToPayloadLabel.put("Fan Auto On Timmer","Fanautoontimer");
        mapUiLabelToPayloadLabel.put("Floor Clean Count","Floorcleancount");
        mapUiLabelToPayloadLabel.put("Floor Clean Duration Timer","Floorcleandurationtimer");
        mapUiLabelToPayloadLabel.put("Full Flush Activation Timer","fullflushactivationtimer");
        mapUiLabelToPayloadLabel.put("Full Flush Duration Timer","fullflushdurationtimer");
        mapUiLabelToPayloadLabel.put("Light Auto Off Time","Lightautoofftime");
        mapUiLabelToPayloadLabel.put("Light Auto On Timer","Lightautoontimer");
        mapUiLabelToPayloadLabel.put("Mini Flush Activation Timer","Miniflushactivationtimer");
        mapUiLabelToPayloadLabel.put("Mini Flush Duration Timer","Miniflushdurationtimer");

        JSONObject payload = new JSONObject();
        String payloadLabel;
        try {
            for(PropertyNameValueData item : configData){

                payloadLabel = mapUiLabelToPayloadLabel.get(item.Name);
                Log.i("__cmsPayload",""+item.Name +": "+payloadLabel + item.Value);
                payload.put(mapUiLabelToPayloadLabel.get(item.Name),item.Value);
            }
            payload.put("THING_NAME",metaData.getThingName());
            payload.put("cabin_type",metaData.getCabinType());
            payload.put("user_type",metaData.getUserType());
        }catch (JSONException e){
            e.printStackTrace();
            Log.e("__cmsPayload","error: "+e.getMessage());
        }
        Log.i("__cmsPayload",""+payload);
        return payload;
    }

    public static JSONObject getOdsPayload(List<PropertyNameValueData> configData, PayloadMetaData metaData){

        HashMap<String,String> mapUiLabelToPayloadLabel = new HashMap<>();
        mapUiLabelToPayloadLabel.put("Ambient Floor Factor","Ambientfloorfactor");
        mapUiLabelToPayloadLabel.put("Ambient Seat Factor","Ambientseatfactor");
        mapUiLabelToPayloadLabel.put("Seat Floor Ratio","Seatfloorratio");
        mapUiLabelToPayloadLabel.put("Seat Threshold","Seatthreshold");

        JSONObject payload = new JSONObject();
        String payloadLabel;
        try {
            for(PropertyNameValueData item : configData){

                payloadLabel = mapUiLabelToPayloadLabel.get(item.Name);
                Log.i("__odsPayload",""+item.Name +": "+payloadLabel + item.Value);
                payload.put(mapUiLabelToPayloadLabel.get(item.Name),item.Value);
            }
            payload.put("THING_NAME",metaData.getThingName());
            payload.put("cabin_type",metaData.getCabinType());
            payload.put("user_type",metaData.getUserType());
        }catch (JSONException e){
            e.printStackTrace();
            Log.e("__odsPayload","error: "+e.getMessage());
        }
        Log.i("__odsPayload",""+payload);
        return payload;
    }

    public static JSONObject getCommandPayload(MisCommand command, PayloadMetaData metaData){
        JSONObject payload = new JSONObject();
        try {
            payload.put("commandName",command.getName());
            payload.put("Commandtype",command.getCode());
            payload.put("Commandaction",command.getAction());
            payload.put("Commandtime",command.getDuration());
            payload.put("command_override",command.getOverride());
            payload.put("THING_NAME",metaData.getThingName());
            payload.put("cabin_type",metaData.getCabinType());
            payload.put("user_type",metaData.getUserType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return payload;
    }

    public static JSONObject getCommandInfo(Cabin target, UserProfile userProfile){
        JSONObject payload = new JSONObject();
        try {
            payload.put("user",userProfile.getUser().getUserName());
            payload.put("client",userProfile.getOrganisation().getClient());

            payload.put("targetType","Cabin");
            payload.put("targetSubType",getCabinType(target.getShortThingName()));
            payload.put("targetName",target.getThingName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return payload;
    }

    public static JSONObject getConfigInfo(String configType, Cabin target, UserProfile userProfile){
        JSONObject payload = new JSONObject();
        try {
            payload.put("configType",configType);
            payload.put("user",userProfile.getUser().getUserName());
            payload.put("client",userProfile.getOrganisation().getClient());

            payload.put("targetType","Cabin");
            payload.put("targetSubType",target.getShortThingName());
            payload.put("targetName",target.getThingName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return payload;
    }

    public static JSONObject getGenericConfigInfo(String configType, String cabinType, String targetClient, UserProfile userProfile){
        JSONObject payload = new JSONObject();
        try {

            payload.put("configType",configType);
            payload.put("user",userProfile.getUser().getUserName());
            payload.put("client",userProfile.getOrganisation().getClient());

            payload.put("targetType","Client");
            payload.put("targetSubType",cabinType);
            payload.put("targetName",targetClient);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return payload;
    }
}
