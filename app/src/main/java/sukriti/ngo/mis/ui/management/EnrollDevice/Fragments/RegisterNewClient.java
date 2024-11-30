package sukriti.ngo.mis.ui.management.EnrollDevice.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.bouncycastle.cert.ocsp.Req;

import java.util.ArrayList;
import java.util.Collections;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Attribute;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Attributes;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ComplexDetails;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.RegisterComplexPayload;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.RegisterComplexPayloadValue;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.NameCodeModel;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListIotStateDistrictCityResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ManagementLambdaRequest;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.RequestManagementLambda;
import sukriti.ngo.mis.utils.LambdaClient;
import sukriti.ngo.mis.utils.SharedPrefsClient;
import sukriti.ngo.mis.utils.UserAlertClient;

public class RegisterNewClient extends DialogFragment implements View.OnClickListener {
    private UserAlertClient userAlertClient;

    private TextView label;
//    , clientNameErr;
    private RelativeLayout close;
    private ImageButton verifyName;
    private FloatingActionButton fab;
    private EditText clientName, description;
    private ImageButton iconVerifyName;
    private CheckBox[] infoShareCheckboxes;
    //    private ComplexDetails complexDetails;
    private TextView state, district, city;
//    private TextView stateNameError, districtNameError, cityNameError;

    private final String TAG = "RegisterNewClient";
    private boolean isValidName = false;
    private String ClientName = "";

    private int[] checkBoxIds = {R.id.lightHealth, R.id.fanHealth, R.id.flushHealth, R.id.floorCleanHealth, R.id.averageFeedback,
            R.id.totalUsage, R.id.waterLevel, R.id.aqi_NH3, R.id.aqi_CO, R.id.aqi_CH4, R.id.luminosity, R.id.deviceTheft, R.id.lat, R.id.lon, R.id.totalRecycledWater};

    private String Title;
    ComplexDetails complexDetails;

    private ArrayList<NameCodeModel> ddbStatesList;
    private ArrayList<NameCodeModel> ddbDistrictList;
    private ArrayList<NameCodeModel> ddbCityList;

    private int selectedState = -1, selectedDistrict = -1, selectedCity = -1;

    private final int SELECT_STATE_DDB = 0;
    private final int SELECT_DISTRICT_DDB = 1;
    private final int SELECT_CITY_DDB = 2;
    private SharedPrefsClient sharedPrefsClient;
    private LambdaClient lambdaClient;
    String userName;


    public RegisterNewClient() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFragmentStyle);
        if (getArguments() != null) {
            userAlertClient = new UserAlertClient(getActivity());
            Title = "Add New Client";
        }
        sharedPrefsClient = new SharedPrefsClient(getContext());
        lambdaClient = new LambdaClient(getContext());
        userName = sharedPrefsClient.getUserDetails().getUser().getUserName();
        userAlertClient = new UserAlertClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popup_register_new_client, container, false);

        label = v.findViewById(R.id.popupLabel);
        close = v.findViewById(R.id.closeContainer);
        verifyName = v.findViewById(R.id.iconVerifyName);
        fab = v.findViewById(R.id.fab);
        clientName = v.findViewById(R.id.clientName);
//        clientNameErr = v.findViewById(R.id.clientNameErr);
        iconVerifyName = v.findViewById(R.id.iconVerifyName);
        description = v.findViewById(R.id.description);
        state = v.findViewById(R.id.stateName);
        district = v.findViewById(R.id.districtName);
        city = v.findViewById(R.id.cityName);
//        stateNameError = v.findViewById(R.id.stateNameErr);
//        districtNameError = v.findViewById(R.id.districtNameErr);
//        cityNameError = v.findViewById(R.id.cityNameErr);
        initInfoShareCheckboxes(v);

        clientName.addTextChangedListener(TextChangeWatcher);
        verifyName.setOnClickListener(this);
        close.setOnClickListener(this);
        fab.setOnClickListener(this);
        state.setOnClickListener(this);
        district.setOnClickListener(this);
        city.setOnClickListener(this);

        label.setText(Title);
        return v;
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.closeContainer) {
            dismiss();
        }
        else if (id == R.id.iconVerifyName) {
            validateName();
        }
        else if (id == R.id.fab) {
            if (isValidated())
                initiateAddThingGroup();
        }
        else if (id == R.id.stateName) {
            userAlertClient.showWaitDialog("Getting Data...");
            ManagementLambdaRequest request = new ManagementLambdaRequest(
                    userName,
                    "list-ddb-state"
            );

            lambdaClient.ExecuteManagementLambda(request, dDbStatesListCallback);
        }
        else if (id == R.id.districtName) {
            userAlertClient.showWaitDialog("Getting Data...");
            ManagementLambdaRequest request = new ManagementLambdaRequest(
                    userName,
                    "list-ddb-district",
                    ddbStatesList.get(selectedState).getCODE()
            );

            lambdaClient.ExecuteManagementLambda(request, dDbDistrictListCallback);
        }
        else if (id == R.id.cityName) {
            userAlertClient.showWaitDialog("Getting Data...");
            RequestManagementLambda request = new RequestManagementLambda(
                    userName,
                    "list-ddb-city",
                    ddbStatesList.get(selectedState).getCODE(),
                    ddbDistrictList.get(selectedDistrict).getCODE()
            );

            lambdaClient.ExecuteManagementLambda(request, dDbCityListCallback);

        }

    }

    private void validateName() {
        // Fetch List of all clients

        ManagementLambdaRequest request = new ManagementLambdaRequest(
                userName,
                "list-iot-clientName"
        );

        userAlertClient.showWaitDialog("Validating name...");
        lambdaClient.ExecuteManagementLambda(request, new ListIotStateDistrictCityResponseHandler() {
            @Override
            public void onSuccess(@NonNull JsonObject response) {
                userAlertClient.closeWaitDialog();
                Log.i("listClients", response.toString());
                int statusCode = response.get("statusCode").getAsInt();
                if(statusCode == 200) {
                    if(response.toString().contains(clientName.getText().toString())) {
                        iconVerifyName.setBackgroundResource(R.drawable.ic_check_availability);
//                        setErrorMessage(clientNameErr, "Name already taken. Choose another name");
                    }
                    else {
                        isValidName = true;
                        iconVerifyName.setBackgroundResource(R.drawable.checked);
//                        clearErrorMessage(clientNameErr);
                    }
                }
                else {
                    String message = response.get("body").getAsString();
                    userAlertClient.showDialogMessage("Error", message, false);
                }
            }

            @Override
            public void onError(@Nullable String message) {
                userAlertClient.closeWaitDialog();
                userAlertClient.showDialogMessage("Error", message, false);
            }
        });
    }

    public void initiateAddThingGroup() {
        RegisterComplexPayload payload = new RegisterComplexPayload();
        payload.setUserName(userName);
        payload.setCommand("add-iot-clientgroupName");
        RegisterComplexPayloadValue value = new RegisterComplexPayloadValue();
        value.setName(clientName.getText().toString());
        value.setDescription(description.getText().toString());
        value.setParent("ClientGroup");

        ArrayList<Attributes> attributes = new ArrayList<>();

        if(state.getText().equals("ALL")) {
            attributes.add(new Attributes("STATE", "ALL"));
        } else {
            attributes.add(new Attributes("STATE", capsAllReplaceSpace(ddbStatesList.get(selectedState).getNAME())));
        }

        if(district.getText().equals("ALL")) {
            attributes.add(new Attributes("DISTRICT", "ALL"));
        } else {
            attributes.add(new Attributes("DISTRICT", ddbDistrictList.get(selectedDistrict).getNAME()));
        }


        if(city.getText().equals("ALL")) {
            attributes.add(new Attributes("CITY", "ALL"));
        } else {
            attributes.add(new Attributes("CITY", ddbCityList.get(selectedCity).getNAME()));
        }

        attributes.add(new Attributes("HEALTH_LIGHT", String.valueOf(infoShareCheckboxes[0].isChecked())));
        attributes.add(new Attributes("HEALTH_FAN", String.valueOf(infoShareCheckboxes[1].isChecked())));
        attributes.add(new Attributes("HEALTH_FLUSH", String.valueOf(infoShareCheckboxes[2].isChecked())));
        attributes.add(new Attributes("HEALTH_FLOOR_CLEAN", String.valueOf(infoShareCheckboxes[3].isChecked())));
        attributes.add(new Attributes("AVERAGE_FEEDBACK", String.valueOf(infoShareCheckboxes[4].isChecked())));
        attributes.add(new Attributes("TOTAL_USAGE", String.valueOf(infoShareCheckboxes[5].isChecked())));
        attributes.add(new Attributes("WATER_LEVEL", String.valueOf(infoShareCheckboxes[6].isChecked())));
        attributes.add(new Attributes("AQI_NH3", String.valueOf(infoShareCheckboxes[7].isChecked())));
        attributes.add(new Attributes("AQI_CO", String.valueOf(infoShareCheckboxes[8].isChecked())));
        attributes.add(new Attributes("AQI_CO", String.valueOf(infoShareCheckboxes[9].isChecked())));
        attributes.add(new Attributes("LUMINOSITY", String.valueOf(infoShareCheckboxes[10].isChecked())));
        attributes.add(new Attributes("DEVICE_THEFT", String.valueOf(infoShareCheckboxes[11].isChecked())));
        attributes.add(new Attributes("LATITUDE", String.valueOf(infoShareCheckboxes[12].isChecked())));
        attributes.add(new Attributes("LONGITUDE", String.valueOf(infoShareCheckboxes[13].isChecked())));
        attributes.add(new Attributes("TOTAL_WATER_RECYCLED", String.valueOf(infoShareCheckboxes[14].isChecked())));

        value.setAttributes(attributes);
        payload.setValue(value);

        userAlertClient.showWaitDialog("Creating New Client");
        lambdaClient.ExecuteManagementLambda(payload, new ListIotStateDistrictCityResponseHandler() {
            @Override
            public void onSuccess(@NonNull JsonObject response) {
                userAlertClient.closeWaitDialog();
                int statusCode = response.get("statusCode").getAsInt();
                Log.i("registerClient", response.toString());
            }

            @Override
            public void onError(@Nullable String message) {
                userAlertClient.closeWaitDialog();
                userAlertClient.showDialogMessage("Error", message, false);

            }
        });

    }


    private boolean isValidated() {
        String errMsg = "";
        ClientName = clientName.getText().toString();
        if (ClientName.isEmpty()) {
            errMsg = "Enter a Client Name";
            userAlertClient.showDialogMessage("Error Alert!", errMsg, false);
            return false;
        }
        else if (ClientName.contains(" ")) {
            errMsg = "Name cannot have Spaces. Please provide a name without any space.";
            userAlertClient.showDialogMessage("Error Alert!", errMsg, false);
            return false;
        }
        else if (!isValidName) {
            errMsg = "Please verify Client Name to proceed";
            userAlertClient.showDialogMessage("Error Alert!", errMsg, false);
            return false;
        }
        else if (state.getText().toString().isEmpty()) {
            errMsg = "Please Select State for the Client Group.";
            userAlertClient.showDialogMessage("Error Alert!", errMsg, false);
            return false;
        }
        else if (district.getText().toString().isEmpty()) {
            errMsg = "Please Select District for the Client Group.";
            userAlertClient.showDialogMessage("Error Alert!", errMsg, false);
            return false;
        }
        else if (city.getText().toString().isEmpty()) {
            errMsg = "Please Select City for the Client Group.";
            userAlertClient.showDialogMessage("Error Alert!", errMsg, false);
            return false;
        }
        return true;
    }

    private void initInfoShareCheckboxes(View v) {
        infoShareCheckboxes = new CheckBox[checkBoxIds.length];
        for (int i = 0; i < checkBoxIds.length; i++) {
            infoShareCheckboxes[i] = v.findViewById(checkBoxIds[i]);
        }
    }

    private void setErrorMessage(TextView errTextView, String message) {
        errTextView.setText(message);
    }

    private void clearErrorMessage(TextView errTextView) {
        errTextView.setText("");
    }

    private final TextWatcher TextChangeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (isValidName) {
                isValidName = false;
//                clearErrorMessage(clientNameErr);
                iconVerifyName.setBackgroundResource(R.drawable.ic_check_availability);
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            isValidName = false;
//            clearErrorMessage(clientNameErr);
            iconVerifyName.setBackgroundResource(R.drawable.ic_check_availability);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    ListIotStateDistrictCityResponseHandler dDbStatesListCallback = new ListIotStateDistrictCityResponseHandler() {
        @Override
        public void onSuccess(@NonNull JsonObject response) {
            userAlertClient.closeWaitDialog();
            Log.i("ddbStatesList", response.toString());

            int statusCode = response.get("statusCode").getAsInt();

            if (statusCode == 200) {
                JsonArray jsonArray = response.getAsJsonArray("body");
                if (jsonArray.size() > 0) {
                    String[] list = new String[jsonArray.size() + 1];
                    list[0] = "ALL";
                    ArrayList<NameCodeModel> statesList = new ArrayList<>();
                    statesList.add(new NameCodeModel("ALL", "ALL"));
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject obj = jsonArray.get(i).getAsJsonObject();
                        NameCodeModel tempObj = new NameCodeModel();
                        tempObj.setNAME(obj.get("Name").getAsString());
                        tempObj.setCODE(obj.get("Code").getAsString());
                        statesList.add(tempObj);
                        list[i + 1] = obj.get("Name").getAsString();
                    }
                    ddbStatesList = statesList;
                    showDialog(SELECT_STATE_DDB, list);
                } else {
                    userAlertClient.showDialogMessage("No Data Found", "No Records Available", false);
                }
            }
            else {
                String message = response.get("body").getAsString();
                    userAlertClient.showDialogMessage(
                            "Error",
                            message,
                            false
                    );

            }

        }

        @Override
        public void onError(@Nullable String message) {
            userAlertClient.closeWaitDialog();
            userAlertClient.showDialogMessage("Error", message, false);
        }
    };

    ListIotStateDistrictCityResponseHandler dDbDistrictListCallback = new ListIotStateDistrictCityResponseHandler() {
        @Override
        public void onSuccess(@NonNull JsonObject response) {
            userAlertClient.closeWaitDialog();
            Log.i("ddbDistrictList", response.toString());

            int statusCode = response.get("statusCode").getAsInt();

            if (statusCode == 200) {
                JsonArray jsonArray = response.getAsJsonArray("body");
                if (jsonArray.size() > 0) {
                    String[] list = new String[jsonArray.size() + 1];
                    ArrayList<NameCodeModel> districtList = new ArrayList<>();
                    districtList.add(new NameCodeModel("ALL", "ALL"));
                    list[0] = "ALL";
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject obj = jsonArray.get(i).getAsJsonObject();
                        NameCodeModel tempObj = new NameCodeModel();
                        tempObj.setNAME(obj.get("Name").getAsString());
                        tempObj.setCODE(obj.get("Code").getAsString());
                        districtList.add(tempObj);
                        list[i + 1] = obj.get("Name").getAsString();
                    }
                    ddbDistrictList = districtList;
                    showDialog(SELECT_DISTRICT_DDB, list);
                } else {
                    userAlertClient.showDialogMessage("No Data Found", "No Records Available", false);
                }
            } else {
                String message = response.get("body").getAsString();
                userAlertClient.showDialogMessage(
                        "Error",
                        message,
                        false
                );

            }

        }

        @Override
        public void onError(@Nullable String message) {
            userAlertClient.closeWaitDialog();
            userAlertClient.showDialogMessage("Error", message, false);
        }
    };

    ListIotStateDistrictCityResponseHandler dDbCityListCallback = new ListIotStateDistrictCityResponseHandler() {
        @Override
        public void onSuccess(@NonNull JsonObject response) {
            userAlertClient.closeWaitDialog();
            Log.i("ddbDCityList", response.toString());

            int statusCode = response.get("statusCode").getAsInt();

            if (statusCode == 200) {
                JsonArray jsonArray = response.getAsJsonArray("body");
                if (jsonArray.size() > 0) {
                    String[] list = new String[jsonArray.size() + 1];
                    ArrayList<NameCodeModel> cityList = new ArrayList<>();
                    list[0] = "ALL";
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject obj = jsonArray.get(i).getAsJsonObject();
                        NameCodeModel tempObj = new NameCodeModel();
                        tempObj.setNAME(obj.get("Name").getAsString());
                        tempObj.setCODE(obj.get("Code").getAsString());
                        cityList.add(tempObj);
                        list[i + 1] = obj.get("Name").getAsString();
                    }
                    ddbCityList = cityList;
                    showDialog(SELECT_CITY_DDB, list);
                } else {
                    userAlertClient.showDialogMessage("No Data Found", "No Records Available", false);
                }
            } else {
                String message = response.get("body").getAsString();
                userAlertClient.showDialogMessage(
                        "Error",
                        message,
                        false
                );
            }
        }

        @Override
        public void onError(@Nullable String message) {
            userAlertClient.closeWaitDialog();
            userAlertClient.showDialogMessage("Error", message, false);
        }
    };

    private void showDialog(int action, String[] list) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        if (action == SELECT_STATE_DDB) {
            dialog.setTitle("Select State");
            dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {

                state.setText(list[position]);
                if (position == 0) {
                    district.setText("ALL");
                    city.setText("ALL");

                    selectedState = 0;
                    selectedDistrict = 0;
                    selectedCity = 0;
                } else {
                    district.setText("");
                    city.setText("");

                    selectedState = position;
                    selectedDistrict = -1;
                    selectedCity = -1;
                }
//                stateNameError.setVisibility(View.GONE);
//                districtNameError.setVisibility(View.GONE);
//                cityNameError.setVisibility(View.GONE);
                dialogInterface.dismiss();
            });

            dialog.show();
        } else if (action == SELECT_DISTRICT_DDB) {
            dialog.setTitle("Select District");
            dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {

                district.setText(list[position]);
                if (position == 0) {
                    city.setText("ALL");
                    selectedDistrict = 0;
                    selectedCity = 0;
                } else {
                    city.setText("");
                    selectedDistrict = position;
                    selectedCity = -1;
                }

//                districtNameError.setVisibility(View.GONE);
//                cityNameError.setVisibility(View.GONE);
                dialogInterface.dismiss();
            });
            dialog.show();
        } else if (action == SELECT_CITY_DDB) {
            dialog.setTitle("Select City");
            dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {

                city.setText(list[position]);
                selectedCity = position;
//                cityNameError.setVisibility(View.GONE);
                dialogInterface.dismiss();
            });
            dialog.show();
        }

    }

    public static  String capsAllReplaceSpace(String capString){
        capString = capString.toUpperCase();
        capString = capString.replace(" ","_");
        return capString;
    }


}
