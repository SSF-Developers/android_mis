package sukriti.ngo.mis.ui.management.EnrollDevice.Fragments;

import static sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.RegisterComplex.validateInteger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.interfaces.DialogSingleActionHandler;
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Attributes;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ComplexDetails;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ErrorView;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.RegisterCabinPayload;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.RegisterCabinPayloadValue;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ThingDetails;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.RefreshListCallback;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListIotStateDistrictCityResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ManagementLambdaRequest;
import sukriti.ngo.mis.utils.LambdaClient;
import sukriti.ngo.mis.utils.SharedPrefsClient;
import sukriti.ngo.mis.utils.UserAlertClient;

public class RegisterCabin extends DialogFragment implements View.OnClickListener {

    private ThingDetails cabinDetails;
    private ComplexDetails complexDetails;
    private UserAlertClient userAlertClient;
    private RefreshListCallback refreshListCallback;
//    private AWSIotProvisioningClient provisioningClient;
//
//    private UnoSelect mUnoSelectFragment;
//    private UnoSelectWithCode mUnoSelectWithCode;

    private FloatingActionButton fabRegisterCabin;

    //CABIN ATTRIBUTES
    private LinearLayout cabinAttributesContainer, cabinCameraDetailsContainer;
    private TextView cabinName, cabinType, userType, usageChargeType, bwtLevel, suffix, suffixErr;
    private EditText cabinUrinalCount, bwtCapacity;
    private LinearLayout urinalCountContainer, bwtCapacityContainer, bwtLevelContainer;
    private LinearLayout userTypeContainer, usageChargeTypeContainer;
    //SELECT COMPLEX
    private TextView state, district, city, complexName;
    //COMPLEX ATTRIBUTES
    private LinearLayout complexDetailsContainer;
    private TextView complexUuid;
    private TextView smartness, commissioning;
    private TextView maleWc, femaleWc, pdWc;
    private TextView urinals, urinalCabins, bwt;
    //CLIENT DETAILS
    private LinearLayout clientDetailsContainer;
    private TextView address, lat, lon;
    private TextView clientName, billingGroup, date;
    private EditText cameraSerialNum;
    private EnrollDeviceViewModel viewModel;
    private LambdaClient lambdaClient;
    private SharedPrefsClient sharedPrefsClient;
    String userName;

    private final String CABIN_LIST = "cabinList";
    private final String USER_TYPE_LIST = "userTypeList";
    private final String USAGE_CHARGE = "usageCharge";
    private final String BWT_LEVEL = "bwtLevel";

    private final String TAG = "RegisterCabin";
    private ArrayList<ErrorView> errorList = new ArrayList<>();

    private String Suffix = "";
    //    private ArrayList<ThingGroupDetails> ComplexList;
//    private ArrayList<ThingDetails> CabinList = new ArrayList<>();
//    private ArrayList<ErrorView> errorList = new ArrayList<>();
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_register_cabin, container, false);
        fabRegisterCabin = contentView.findViewById(R.id.fabRegisterCabin);

        //CABIN ATTRIBUTES
        cabinAttributesContainer = contentView.findViewById(R.id.cabinAttributes);
        cabinName = contentView.findViewById(R.id.cabinName);
        cabinType = contentView.findViewById(R.id.cabinType);
        userType = contentView.findViewById(R.id.userType);
        usageChargeType = contentView.findViewById(R.id.usageChargeType);
        cabinUrinalCount = contentView.findViewById(R.id.urinalCount);
        bwtCapacity = contentView.findViewById(R.id.bwtCapacity);
        bwtLevel = contentView.findViewById(R.id.bwtLevel);
        urinalCountContainer = contentView.findViewById(R.id.urinalCountContainer);
        bwtCapacityContainer = contentView.findViewById(R.id.bwtCapacityContainer);
        bwtLevelContainer = contentView.findViewById(R.id.bwtLevelContainer);
        suffix = contentView.findViewById(R.id.suffix);
        suffixErr = contentView.findViewById(R.id.suffixErr);
        userTypeContainer = contentView.findViewById(R.id.userTypeContainer);
        usageChargeTypeContainer = contentView.findViewById(R.id.usageChargeTypeContainer);
        //SELECT COMPLEX
        state = contentView.findViewById(R.id.stateName);
        district = contentView.findViewById(R.id.districtName);
        city = contentView.findViewById(R.id.cityName);
        complexName = contentView.findViewById(R.id.complexName);
        //COMPLEX ATTRIBUTES
        complexDetailsContainer = contentView.findViewById(R.id.complexDetails);
        complexUuid = contentView.findViewById(R.id.complexUuid);
        commissioning = contentView.findViewById(R.id.commissioning);
        smartness = contentView.findViewById(R.id.smartness);
        maleWc = contentView.findViewById(R.id.maleWc);
        femaleWc = contentView.findViewById(R.id.femaleWc);
        pdWc = contentView.findViewById(R.id.pdWc);
        urinals = contentView.findViewById(R.id.urinals);
        urinalCabins = contentView.findViewById(R.id.urinalCabins);
        bwt = contentView.findViewById(R.id.bwt);
        //CLIENT DETAILS
        clientDetailsContainer = contentView.findViewById(R.id.clientDetails);
        cabinCameraDetailsContainer = contentView.findViewById(R.id.cabinCameraDetails);
        address = contentView.findViewById(R.id.address);
        lat = contentView.findViewById(R.id.lat);
        lon = contentView.findViewById(R.id.lon);
        clientName = contentView.findViewById(R.id.clientName);
        billingGroup = contentView.findViewById(R.id.billingGroup);
        date = contentView.findViewById(R.id.date);
        cameraSerialNum = contentView.findViewById(R.id.cameraSerialNumET);

        cabinType.setOnClickListener(this);
        userType.setOnClickListener(this);
        usageChargeType.setOnClickListener(this);
        bwtLevel.setOnClickListener(this);
        fabRegisterCabin.setOnClickListener(this);

        init();
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == userType.getId()) {
            /*ManagementLambdaRequest request = new ManagementLambdaRequest(
                    userName,
                    "list-iot-UserType",
                    complexDetails.getComplexName()

            );*/
            JsonObject request = new JsonObject();
            request.addProperty("userName", userName);
            request.addProperty("command", "list-iot-UserType");
            request.addProperty("value", complexDetails.getComplexName());
            request.addProperty("cabin_type", cabinType.getText().toString().toUpperCase());
            Log.i("userTypeListCallback", "Payload: " + request);
            userAlertClient.showWaitDialog("Getting Data...");
            lambdaClient.ExecuteIotCabinLambda(request, userTypeListCallback);
        }
        else if (id == cabinType.getId()) {
            ManagementLambdaRequest request = new ManagementLambdaRequest(
                    userName,
                    "list-iot-CabinType",
                    complexDetails.getComplexName()
            );

            userAlertClient.showWaitDialog("Getting Data...");
            lambdaClient.ExecuteIotCabinLambda(request, cabinListCallback);
        }
        else if (id == fabRegisterCabin.getId()) {
            if(validateCabinDetails()) {
                createCabin();
            }
        }
        else if (id == usageChargeType.getId()) {
            getUsageChargeTypeList();
        }
        else if (id == bwtLevel.getId()) {
            getBWTLevelList();
        }

    }

    private void getBWTLevelList() {
        String[] list = new String[4];
        list[0] = "BWT_G0";
        list[1] = "BWT_G1";
        list[2] = "BWT_G2";
        list[3] = "BWT_G3";
        showDialog(BWT_LEVEL, list);
    }

    private void getUsageChargeTypeList() {
        String[] list = new String[4];
        list[0] = "NONE";
        list[1] = "COIN";
        list[2] = "COIN_RF";
        list[3] = "RF";
        showDialog(USAGE_CHARGE, list);
    }

    private void createCabin() {
        userAlertClient.showWaitDialog("Registering Cabin...");
        RegisterCabinPayloadValue value = new RegisterCabinPayloadValue();
        String ThingName = getThingName();
        value.setName(ThingName);
        value.setDefaultClientId(ThingName);
        value.setBillingGroup(viewModel.getComplexDetails().getBillingGroup());
        value.setThingType(cabinType.getText().toString());
        value.setThingGroup(viewModel.getComplexDetails().getComplexName());

        ArrayList<Attributes> attributes = new ArrayList<>();
        attributes.add(new Attributes("COMPLEX", complexDetails.getComplexName()));
        attributes.add(new Attributes("ADDRESS", complexDetails.getAddress()));
        attributes.add(new Attributes("LATITUDE", complexDetails.getLatitude()));
        attributes.add(new Attributes("LONGITUDE", complexDetails.getLongitude()));
        attributes.add(new Attributes("DATE", complexDetails.getDate()));
        attributes.add(new Attributes("CLIENT", complexDetails.getClient()));
        attributes.add(new Attributes("SMART_LEVEL", complexDetails.getSmartness()));
        attributes.add(new Attributes("STATE", complexDetails.getStateName()));
        attributes.add(new Attributes("DISTRICT", complexDetails.getDistrictName()));
        attributes.add(new Attributes("CITY", complexDetails.getCityName()));
        attributes.add(new Attributes("STATE_CODE", complexDetails.getStateCode()));
        attributes.add(new Attributes("DISTRICT_CODE", complexDetails.getDistrictCode()));
        attributes.add(new Attributes("CITY_CODE", complexDetails.getCityCode()));
        attributes.add(new Attributes("SHORT_THING_NAME", ThingName.substring(ThingName.length()-7)));
        attributes.add(new Attributes("BILLING_GROUP", complexDetails.getBillingGroup()));
//        attributes.add(new Attributes("USER_TYPE", userType.getText().toString().toUpperCase()));
//        attributes.add(new Attributes("USAGE_CHARGE", usageChargeType.getText().toString().toUpperCase()));
        attributes.add(new Attributes("CAMERA_SERIAL_NUM", cameraSerialNum.getText().toString()));
//        attributes.add(new Attributes("CABIN_NUM", Suffix));

        if(cabinType.getText().toString().compareToIgnoreCase("WC") == 0 || cabinType.getText().toString().compareToIgnoreCase("URINAL") == 0) {

            attributes.add(new Attributes("USER_TYPE", userType.getText().toString()));
            attributes.add(new Attributes("USAGE_CHARGE", usageChargeType.getText().toString()));
            attributes.add(new Attributes("CABIN_NUM", Suffix));
        }
        if(cabinType.getText().toString().compareToIgnoreCase("URINAL") == 0) {
            attributes.add(new Attributes("NO_OF_URINALS", cabinUrinalCount.getText().toString()));
        }
        if(cabinType.getText().toString().compareToIgnoreCase("BWT") == 0) {
            attributes.add(new Attributes("BWT_KLD", bwtCapacity.getText().toString()));
            attributes.add(new Attributes("BWT_LVL", bwtLevel.getText().toString()));
        }


        value.setAttributes(attributes);
        RegisterCabinPayload payload = new RegisterCabinPayload();
        payload.setCommand("create-iot-thing");
        payload.setUserName(userName);
        payload.setValue(value);

        Log.i("registerCabin", "Final Complex -: " + new Gson().toJson(payload));

        lambdaClient.ExecuteIotCabinLambda(payload, new ListIotStateDistrictCityResponseHandler() {
            @Override
            public void onSuccess(@NonNull JsonObject response) {
                userAlertClient.closeWaitDialog();
                int statusCode = response.get("statusCode").getAsInt();
                if(statusCode == 200 ) {
                    String message = response.get("body").getAsString();
                    userAlertClient.showDialogMessage("Cabin created successfully", message, new DialogSingleActionHandler() {
                        @Override
                        public void onAction() {
                            refreshListCallback.refreshList();
                            dismiss();
                        }
                    });
                }
                else {
                    String message = response.get("body").getAsString();
                    userAlertClient.showDialogMessage("Error", message, false);
                }
            }

            @Override
            public void onError(@Nullable String message) {
                userAlertClient.closeWaitDialog();
            }
        });
    }

    private void init() {
        complexDetails = viewModel.getComplexDetails();
        cabinDetails = new ThingDetails();
        context = getContext();
        lambdaClient = new LambdaClient(context);
        userAlertClient = new UserAlertClient(getActivity());
        sharedPrefsClient = new SharedPrefsClient(context);
        userName = sharedPrefsClient.getUserDetails().getUser().getUserName();

        urinalCountContainer.setVisibility(View.GONE);
        bwtCapacityContainer.setVisibility(View.GONE);
        bwtLevelContainer.setVisibility(View.GONE);

        cabinAttributesContainer.setVisibility(View.VISIBLE);
        complexDetailsContainer.setVisibility(View.VISIBLE);
        clientDetailsContainer.setVisibility(View.VISIBLE);
        cabinCameraDetailsContainer.setVisibility(View.VISIBLE);

        cabinName.setText("Cabin Name");
        state.setText(complexDetails.getStateName());
        district.setText(complexDetails.getDistrictName());
        city.setText(complexDetails.getCityName());
        complexName.setText(complexDetails.getComplexName());
        complexUuid.setText(complexDetails.getUuid());
        commissioning.setText(complexDetails.getCommissioningStatus());
        smartness.setText(complexDetails.getSmartness());
        maleWc.setText(complexDetails.getWCCountMale());
        femaleWc.setText(complexDetails.getWCCountFemale());
        pdWc.setText(complexDetails.getWCCountPD());

        urinals.setText(complexDetails.getUrinals());
        urinalCabins.setText(complexDetails.getUrinalCabins());
        bwt.setText(complexDetails.getBwt());
        address.setText(complexDetails.getAddress());
        lat.setText(complexDetails.getLatitude());
        lon.setText(complexDetails.getLongitude());
        clientName.setText(complexDetails.getClient());
        billingGroup.setText(complexDetails.getBillingGroup());
        date.setText(complexDetails.getDate());

    }

    public EnrollDeviceViewModel getViewModel() {
        return viewModel;
    }

    public void setViewModel(EnrollDeviceViewModel viewModel) {
        this.viewModel = viewModel;
    }

    ListIotStateDistrictCityResponseHandler cabinListCallback = new ListIotStateDistrictCityResponseHandler() {
        @Override
        public void onSuccess(@NonNull JsonObject response) {
            userAlertClient.closeWaitDialog();
            int statusCode = response.get("statusCode").getAsInt();
            if (statusCode == 200) {
                JsonArray list = response.get("body").getAsJsonArray();
                String[] finalList = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    finalList[i] = list.get(i).getAsString().toUpperCase();
                }
                showDialog(CABIN_LIST, finalList);
            }
        }

        @Override
        public void onError(@Nullable String message) {

        }
    };

    ListIotStateDistrictCityResponseHandler userTypeListCallback = new ListIotStateDistrictCityResponseHandler() {
        @Override
        public void onSuccess(@NonNull JsonObject response) {
            Log.i("userTypeListCallback", response.toString());
            userAlertClient.closeWaitDialog();
            int statusCode = response.get("statusCode").getAsInt();
            if (statusCode == 200) {
                JsonArray list = response.get("body").getAsJsonArray();
                String[] finalList = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    finalList[i] = list.get(i).getAsString();
                }
                if(finalList.length == 0) {
                    userAlertClient.showDialogMessage("Error", "All the urinal cabins are filled. You can no-more create urinal cabins", false);
                } else {
                    showDialog(USER_TYPE_LIST, finalList);
                }
            }
        }

        @Override
        public void onError(@Nullable String message) {

        }
    };

    private void showDialog(String action, String[] items) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        if (Objects.equals(action, CABIN_LIST)) {
            dialog.setTitle("Select Cabin Type");
            Log.i("showDialog", "Cabin Type");
            dialog.setSingleChoiceItems(items, -1, (dialogInterface, i) -> {
                cabinType.setText(items[i]);
                if (items[i].equalsIgnoreCase("WC")) {
                    userTypeContainer.setVisibility(View.VISIBLE);
                    usageChargeTypeContainer.setVisibility(View.VISIBLE);
                    urinalCountContainer.setVisibility(View.GONE);
                    bwtCapacityContainer.setVisibility(View.GONE);
                    bwtLevelContainer.setVisibility(View.GONE);
                    userType.setText("");
                    usageChargeType.setText("");
                    bwtLevel.setText("");
                    bwtCapacity.setText("");
                    cabinUrinalCount.setText("");
                }
                else if (items[i].equalsIgnoreCase("BWT")) {
                    bwtCapacityContainer.setVisibility(View.VISIBLE);
                    bwtLevelContainer.setVisibility(View.VISIBLE);
                    usageChargeTypeContainer.setVisibility(View.GONE);
                    userTypeContainer.setVisibility(View.GONE);
                    urinalCountContainer.setVisibility(View.GONE);
                    userType.setText("B");
                    usageChargeType.setText("");
                    bwtLevel.setText("");
                    bwtCapacity.setText("");
                    cabinUrinalCount.setText("");
                }
                else if (items[i].equalsIgnoreCase("URINAL")) {
                    urinalCountContainer.setVisibility(View.VISIBLE);
                    usageChargeTypeContainer.setVisibility(View.VISIBLE);
                    userTypeContainer.setVisibility(View.VISIBLE);
                    bwtCapacityContainer.setVisibility(View.GONE);
                    bwtLevelContainer.setVisibility(View.GONE);
                    userType.setText("");
                    usageChargeType.setText("");
                    bwtLevel.setText("");
                    bwtCapacity.setText("");
                    cabinUrinalCount.setText("");
                }
                dialogInterface.dismiss();
            });
            dialog.show();
        }
        else if (Objects.equals(action, USER_TYPE_LIST)) {
            Log.i("showDialog", "showDialog: User Type List");
            dialog.setTitle("Select User Type");
            dialog.setSingleChoiceItems(items, -1, (dialogInterface, i) -> {
                userType.setText(items[i]);
                dialogInterface.dismiss();
            });
            dialog.show();
        }
        else if (Objects.equals(action, USAGE_CHARGE)) {
            Log.i("showDialog", "showDialog: Usage charge type");
            dialog.setTitle("Select Usage Charge Type");
            dialog.setSingleChoiceItems(items, -1, (dialogInterface, i) -> {
                usageChargeType.setText(items[i]);
                dialogInterface.dismiss();
            });
            dialog.show();
        }
        else if (Objects.equals(action, BWT_LEVEL)) {
            Log.i("showDialog", "showDialog: Bwt Level");
            dialog.setTitle("Select BWT Level");
            dialog.setSingleChoiceItems(items, -1, (dialogInterface, i) -> {
                bwtLevel.setText(items[i]);
                dialogInterface.dismiss();
            });
            dialog.show();
        }


    }

    private String getNewCabinSuffix() {
        ArrayList<String> BWT = new ArrayList<>();
        ArrayList<String> URI = new ArrayList<>();
        ArrayList<String> MWC = new ArrayList<>();
        ArrayList<String> FWC = new ArrayList<>();
        ArrayList<String> PWC = new ArrayList<>();

        for (int i = 0; i < viewModel.getThingDetailsList().size(); i++) {
            ThingDetails tempDetails = viewModel.getThingDetailsList().get(i);
            Log.i(TAG, "Details -: " + new Gson().toJson(tempDetails));
            if (tempDetails.ThingType.compareToIgnoreCase("BWT") == 0) {
                BWT.add(tempDetails.Name);
            }
            if (tempDetails.ThingType.compareToIgnoreCase("URINAL") == 0) {
                URI.add(tempDetails.Name);
            }
            if (tempDetails.ThingType.compareToIgnoreCase("WC") == 0) {
                String user = tempDetails.AttributesMap.get("USER_TYPE");
                Log.i(TAG, "USER_TYPE -: " + user);
                if(user != null) {
                    if (user.compareToIgnoreCase("MALE") == 0) {
                        MWC.add(tempDetails.Name);
                    }
                    if (user.compareToIgnoreCase("FEMALE") == 0) {
                        FWC.add(tempDetails.Name);
                    }
                    if (user.compareToIgnoreCase("PD") == 0) {
                        PWC.add(tempDetails.Name);
                    }
                }
            }
        }

        Log.i(TAG, "BWT -: " + new Gson().toJson(BWT));
        Log.i(TAG, "URI -: " + new Gson().toJson(URI));
        Log.i(TAG, "MWC -: " + new Gson().toJson(MWC));
        Log.i(TAG, "FWC -: " + new Gson().toJson(FWC));
        Log.i(TAG, "PWC -: " + new Gson().toJson(PWC));


        ArrayList<String> cabinIdList = new ArrayList<>();
        if (cabinType.getText().toString().compareToIgnoreCase("BWT") == 0) {
            Log.i(TAG, "cabinIdList -: Cabin Type is BWT");
            cabinIdList = BWT;
        }
        if (cabinType.getText().toString().compareToIgnoreCase("URINAL") == 0) {
            Log.i(TAG, "cabinIdList -: Cabin Type is BWT");
            cabinIdList = URI;
        }
        if (cabinType.getText().toString().compareToIgnoreCase("WC") == 0) {
            if (userType.getText().toString().compareToIgnoreCase("MALE") == 0) {
                Log.i(TAG, "cabinIdList -: Cabin Type is MWC");
                cabinIdList = MWC;
            }
            if (userType.getText().toString().compareToIgnoreCase("FEMALE") == 0) {
                Log.i(TAG, "cabinIdList -: Cabin Type is FWC");
                cabinIdList = FWC;
            }
            if (userType.getText().toString().compareToIgnoreCase("PD") == 0) {
                Log.i(TAG, "cabinIdList -: Cabin Type is User Type PD");
                cabinIdList = PWC;
            }
        }

        ArrayList<Integer> suffixList = new ArrayList<>();
        for (int i = 0; i < cabinIdList.size(); i++) {
            int existingSuffixes = Integer.parseInt(cabinIdList.get(i).split("_")[4]);
            suffixList.add(existingSuffixes);
        }
        if (suffixList.isEmpty()) {
            String newItemSuffix = getSuffixOptions().get(0);//getSuffixOptions().size()-1
            Log.e(TAG, "newItemSuffix: " + newItemSuffix);
            Suffix = newItemSuffix;
            return newItemSuffix;
        } else {
            Collections.sort(suffixList, numericComparator);
            int nextSuffix = suffixList.get(suffixList.size() - 1) + 1;
            Log.e(TAG, "nextSuffix: " + nextSuffix);
            String s =  formatted3DigitNumber(nextSuffix);
            Suffix = s;
            return s;
        }

    }

    private ArrayList<String> getSuffixOptions() {
        ArrayList<String> list = new ArrayList<>();
        if (cabinType.getText().toString().compareToIgnoreCase("URINAL") == 0) {
            list = getFormatted3DigitList(Integer.parseInt(viewModel.getComplexDetails().getUrinals()));
        }
        if (cabinType.getText().toString().compareToIgnoreCase("BWT") == 0) {
            list = getFormatted3DigitList(Integer.parseInt(viewModel.getComplexDetails().getBwt()));
        }
        if (cabinType.getText().toString().compareToIgnoreCase("WC") == 0) {
            if (userType.getText().toString().compareToIgnoreCase("MALE") == 0) {
                list = getFormatted3DigitList(Integer.parseInt(viewModel.getComplexDetails().getWCCountMale()));
            } else if (userType.getText().toString().compareToIgnoreCase("FEMALE") == 0) {
                list = getFormatted3DigitList(Integer.parseInt(viewModel.getComplexDetails().getWCCountFemale()));
            } else if (userType.getText().toString().compareToIgnoreCase("PD") == 0) {
                list = getFormatted3DigitList(Integer.parseInt(viewModel.getComplexDetails().getWCCountPD()));
            }
        }
        return list;
    }

    public static ArrayList<String> getFormatted3DigitList(int Max){
        ArrayList<String> list = new ArrayList<>();
        for(int i=1;i<=Max;i++) {
            list.add(formatted3DigitNumber(i));
        }
        return list;
    }

    public static String formatted3DigitNumber(int Count) {
        String strCount = "";
        if(Count<10)
            strCount = "00"+Count;
        else if(Count<100)
            strCount = "0"+Count;
        else
            strCount = ""+Count;
        return strCount;
    }

    public static Comparator<Integer> numericComparator = new  Comparator<Integer>()
    {
        public int compare(Integer a, Integer b)
        {
            //a on top of b is this returns -ve ==>
            return a-b;
        }
    };

    private String getA(String userType) {
        if(userType.compareToIgnoreCase("MALE")==0)
            return "M";
        if(userType.compareToIgnoreCase("FEMALE")==0)
            return "F";
        if(userType.compareToIgnoreCase("PD")==0)
            return "P";
        if(userType.compareToIgnoreCase("B")==0)
            return "B";
        return "U";
    }

    private String getBB(String cabinType) {
        if(cabinType.compareToIgnoreCase("WC")==0)
            return "WC";
        else if(cabinType.compareToIgnoreCase("BWT")==0)
            return "WT";
        else //if(cabinType.compareToIgnoreCase(CABIN_TYPE_URINAL)==0)
            return "UR";
        //Unreachable
    }

    public String getThingName() {
        String ThingName = viewModel.getComplexDetails().getUuid();//UUID_ABB_ZZZ

        //User Type (A)
        ThingName += "_"+getA(userType.getText().toString());
        //Cabin Type (BB)
        ThingName += getBB(cabinType.getText().toString());
        //Suffix (ZZZ)
        ThingName += "_"+getNewCabinSuffix();
        return ThingName;
    }

    private void validateField(boolean check, int ref, String Message) {
        if (check) {
            errorList.add(new ErrorView(Message, ref));
        } else {
            clearErrorMessage(ref);
        }
    }

    private void setErrorMessage(TextView errTextView, String message) {
        errTextView.setText(message);
    }

    private void clearErrorMessage(TextView errTextView) {
        errTextView.setText("");
    }

    private void clearErrorMessage(int viewRef) {
        ((TextView) RegisterCabin.this.getView().findViewById(viewRef)).setText("");
    }

    private boolean validateCabinDetails() {
        errorList = new ArrayList<>();
        validateField(cabinType.getText().toString().isEmpty(), R.id.cabinTypeErr, "Cabin Type not selected");

        if (cabinType.getText().toString().compareToIgnoreCase("WC") == 0 ||
                cabinType.getText().toString().compareToIgnoreCase("URINAL") == 0) {
            validateField(userType.getText().toString().isEmpty(), R.id.userTypeErr, "User Type not selected");

            validateField(usageChargeType.getText().toString().isEmpty(), R.id.usageChargeTypeErr, "Usage Charge Type not selected");
        }

        if (cabinType.getText().toString().compareToIgnoreCase("URINAL") == 0) {
            validateField(!validateInteger(cabinUrinalCount.getText().toString()), R.id.urinalCountErr, "Urinal Count Invalid");
        }
        if (cabinType.getText().toString().compareToIgnoreCase("BWT") == 0) {
            validateField(!validateInteger(bwtCapacity.getText().toString()), R.id.bwtCapacityErr, "BWT Capacity Invalid");
            validateField(bwtLevel.getText().toString().isEmpty(), R.id.bwtLevelErr, "BWT Level Invalid");
        }

        validateField(cameraSerialNum.getText().toString().isEmpty(), R.id.cameraSerialErr, "Camera Serial Number Invalid");

        for (int i = 0; i < errorList.size(); i++) {
            ((TextView) RegisterCabin.this.getView().findViewById(errorList.get(i).ViewRef)).setText(errorList.get(i).Message);
        }

        if (errorList.isEmpty()) {
                return true;
        } else {
            String Msg = errorList.size() == 1 ? errorList.size() + " error found. Please correct it to proceed"
                    : errorList.size() + " errors found. Please correct them to proceed";
            userAlertClient.showDialogMessage("Validation Error!", Msg, false);
            return false;
        }
    }

    public void setRefreshListCallback(RefreshListCallback refreshListCallback) {
        this.refreshListCallback = refreshListCallback;
    }
}
