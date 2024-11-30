package sukriti.ngo.mis.ui.management.fargments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.communication.SimpleHandler;
import sukriti.ngo.mis.interfaces.DialogSingleActionHandler;
import sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.RegisterComplex;
import sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.RegisterNewClient;
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Attribute;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Attributes;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ComplexDetails;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ErrorView;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.UpdateComplexPayload;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.UpdateComplexPayloadValue;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.RegisterNewBillingGroup;
import sukriti.ngo.mis.ui.management.ManagementViewModel;
import sukriti.ngo.mis.ui.management.adapters.ClientLogoAdapter;
import sukriti.ngo.mis.ui.management.lambda.DeleteComplex.DeleteComplexResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListIotStateDistrictCityResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ManagementLambdaRequest;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.RequestManagementLambda;
import sukriti.ngo.mis.ui.management.lambda.FetchClientLogo.FetchClientLogoResponseHandler;
import sukriti.ngo.mis.utils.ConfirmAction;
import sukriti.ngo.mis.utils.LambdaClient;
import sukriti.ngo.mis.utils.SharedPrefsClient;
import sukriti.ngo.mis.utils.UserAlertClient;
import sukriti.ngo.mis.utils.Utilities;

public class ComplexDetailsDialog extends DialogFragment implements View.OnClickListener {


    AutoCompleteTextView state, district, city;
    TextInputEditText  complex;
    AutoCompleteTextView clientName, billingGroup, date, commissioningStatus,deviceType, smartness;
    TextView clientLogoError;
    TextInputEditText address, latitude, longitude, maleWc, femaleWc, pdWc, urinal, urinalCabin, bwt, napkinVmCount, napkinVmManufacturer;
    TextInputEditText napkinIncineratorCount, napkinIncineratorManufacturer, kioskArea, waterAtmCapacity, supervisorRoomSize;

    TextInputLayout stateLayout, districtLayout, cityLayout, billingGroupLayout, clientLayout;
    TextInputEditText manufacturer, techProvider, civilPartner, omPartner;
    TextInputEditText routerImeiET, routerMobileET;
    ImageView cancelDialog;
    EnrollDeviceViewModel viewModel;
    ManagementViewModel managementViewModel;
    FloatingActionButton update, delete;
    LambdaClient lambdaClient;
    UserAlertClient userAlertClient;
    SharedPrefsClient sharedPrefsClient;
    ComplexDetails complexDetails;
    ComplexDetails retrievedComplexDetails;
    private RecyclerView clientLogoView;
    ArrayList<String> listOfClientLogo = new ArrayList<>();;
    private ClientLogoAdapter clientLogoAdapter;

    private final int SELECT_CLIENT = 3;
    private final int SELECT_BILLING_GROUP = 7;
    private ArrayList<ErrorView> errorList;
    public ComplexDetailsDialog() {
    }

   /* public ComplexDetailsDialog(EnrollDeviceViewModel viewModel) {
            this.viewModel = viewModel;
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.update_complex_details, container, false);


        state = view.findViewById(R.id.state);
        district = view.findViewById(R.id.district);
        city = view.findViewById(R.id.city);
        complex = view.findViewById(R.id.complexName);

        address = view.findViewById(R.id.address);
        latitude = view.findViewById(R.id.latitude);
        longitude = view.findViewById(R.id.longitude);

        commissioningStatus = view.findViewById(R.id.commissioning);
        deviceType = view.findViewById(R.id.deviceType);
        smartness = view.findViewById(R.id.smartness);
        maleWc = view.findViewById(R.id.maleWc);
        femaleWc = view.findViewById(R.id.femaleWc);
        pdWc = view.findViewById(R.id.pdWc);
        urinal = view.findViewById(R.id.urinals);
        urinalCabin = view.findViewById(R.id.urinalCabins);
        bwt = view.findViewById(R.id.bwt);
        napkinVmCount = view.findViewById(R.id.napkinVmCount);
        napkinVmManufacturer = view.findViewById(R.id.napkinVmManufacturer);
        napkinIncineratorCount = view.findViewById(R.id.napkinIncineratorCount);
        napkinIncineratorManufacturer = view.findViewById(R.id.napkinIncineratorManufacturer);
        kioskArea = view.findViewById(R.id.kioskArea);
        waterAtmCapacity = view.findViewById(R.id.waterAtmCapacity);
        supervisorRoomSize = view.findViewById(R.id.supervisorRoomSize);
        manufacturer = view.findViewById(R.id.manufacturer);
        techProvider = view.findViewById(R.id.techProvider);
        civilPartner = view.findViewById(R.id.civilPartner);
        omPartner = view.findViewById(R.id.omPartner);
        routerImeiET = view.findViewById(R.id.routerImeiET);
        routerMobileET = view.findViewById(R.id.routerMobileET);
        clientName = view.findViewById(R.id.clientName);
        clientLogoView = view.findViewById(R.id.logoRecyclerView);
        clientLogoError = view.findViewById(R.id.logoError);
        billingGroup = view.findViewById(R.id.billingGroup);
        date = view.findViewById(R.id.date);
        cancelDialog = view.findViewById(R.id.cancelDialog);
        update = view.findViewById(R.id.updateComplex);
        delete = view.findViewById(R.id.deleteComplex);

        billingGroupLayout = view.findViewById(R.id.billingGroupLayout);
        clientLayout = view.findViewById(R.id.clientNameLayout);
        stateLayout = view.findViewById(R.id.stateLayout);
        districtLayout = view.findViewById(R.id.districtLayout);
        cityLayout = view.findViewById(R.id.cityLayout);

        cancelDialog.setOnClickListener(this);
//        fetchClientLogo();
        lambdaClient = new LambdaClient(getContext());
        userAlertClient = new UserAlertClient(getActivity());
        sharedPrefsClient = new SharedPrefsClient(getContext());

        setupUi(retrievedComplexDetails);
        return view;
    }

    public void setupUi(ComplexDetails details) {
        this.complexDetails = details;
        String TAG = "dialog";
        state.setText(details.getStateName());
        Log.i(TAG, "State " + details.getStateName());
        district.setText(details.getDistrictName());
        Log.i(TAG, "District " + details.getDistrictName());
        city.setText(details.getCityName());
        Log.i(TAG, "City " + details.getCityName());
        complex.setText(details.getComplexName());
        Log.i(TAG, "Complex " + details.getComplexName());

        address.setText(details.getAddress());
        latitude.setText(details.getLatitude());
        longitude.setText(details.getLongitude());

        clientName.setText(details.getClient());
        billingGroup.setText(details.getBillingGroup());
        date.setText(details.getDate());

        commissioningStatus.setText(details.getCommissioningStatus());
        deviceType.setText(details.getDeviceType());
        smartness.setText(details.getSmartness());

        maleWc.setText(details.getWCCountMale());
        femaleWc.setText(details.getWCCountFemale());
        pdWc.setText(details.getWCCountPD());

        urinal.setText(details.getUrinals());
        urinalCabin.setText(details.getUrinalCabins());
        bwt.setText(details.getBwt());

        napkinVmCount.setText(details.getNapkinVmCount());
        napkinVmManufacturer.setText(details.getNapkinVmManufacturer());

        napkinIncineratorCount.setText(details.getNapkinIncineratorCount());
        napkinIncineratorManufacturer.setText(details.getNapkinIncineratorManufacturer());

        kioskArea.setText(details.getKioskArea());
        waterAtmCapacity.setText(details.getWaterAtmCapacity());
        supervisorRoomSize.setText(details.getSupervisorRoomSize());

        manufacturer.setText(details.getManufacturer());
        civilPartner.setText(details.getCivilPartner());
        omPartner.setText(details.getOMPartner());
        techProvider.setText(details.getTechProvider());

        routerImeiET.setText(details.getRouterImei());
        routerMobileET.setText(details.getRouterMobile());

        commissioningStatus.setOnClickListener(this);
        smartness.setOnClickListener(this);
        update.setOnClickListener(this);
        delete.setOnClickListener(this);
        date.setOnClickListener(this);
        billingGroup.setOnClickListener(this);

        billingGroupLayout.setEndIconOnClickListener( view -> registerNewBillingGroup());
        clientLayout.setEndIconOnClickListener(view -> registerNewClient());
        clientName.setOnClickListener(this);

        fetchClientLogo();
    }


    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == commissioningStatus.getId()) {
            Log.i("commissioningClick", "onClick: commissioning status clicked");
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            String[] choices = {"True", "False"};
            builder.setSingleChoiceItems(choices, -1, (dialogInterface, position) -> {
                commissioningStatus.setText(choices[position]);
                dialogInterface.dismiss();
            });

            builder.setTitle("Commissioning Status");

            builder.show();

            Log.i("commissioningClick", "onClick: dialog shown");
        }
        else if (id == smartness.getId()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            String[] choices = {"None", "Basic", "Premium", "Extra-Premium"};

            builder.setSingleChoiceItems(choices, -1, ((dialogInterface, i) -> {
                smartness.setText(choices[i]);
                dialogInterface.dismiss();
            }));

            builder.show();
        }
        else if (id == update.getId()) {
            if(verifyDetails()) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                Fragment prev =getParentFragmentManager().findFragmentByTag("ConfirmComplexUpdateAction");
                if(prev != null) {
                    transaction.remove(prev);
                }

                ConfirmAction confirmActionFragment = ConfirmAction.newInstance();
                confirmActionFragment.setUp(
                        "Update Action",
                        "To confirm update, type 'UPDATE' in the field below",
                        "UPDATE"
                );

                confirmActionFragment.show(transaction, "ConfirmComplexUpdateAction");
                confirmActionFragment.setListener(new SimpleHandler() {
                    @Override
                    public void onSuccess() {
                        // Call update lambda here
                        updateComplex();
                    }

                    @Override
                    public void onError(String ErrorMsg) {
                        confirmActionFragment.dismiss();
                    }
                });
            }
        }
        else if (id == delete.getId()) {
            String userName = sharedPrefsClient.getUserDetails().getUser().getUserName();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Fragment prev =getFragmentManager().findFragmentByTag("ConfirmComplexDeleteAction");
            if(prev != null) {
                transaction.remove(prev);
            }

            ConfirmAction confirmActionFragment = ConfirmAction.newInstance();
            confirmActionFragment.setUp(
                    "Delete Action",
                    "To confirm delete, type 'DELETE' in the field below",
                    "DELETE"
            );

            confirmActionFragment.show(transaction, "ConfirmComplexDeleteAction");
            confirmActionFragment.setListener(new SimpleHandler() {
                @Override
                public void onSuccess() {
                    // Call delete lambda here
                    confirmActionFragment.dismiss();
                    userAlertClient.showWaitDialog("Deleting Complex: " + complex.getText().toString());


                    JsonObject request = new JsonObject();
                    request.addProperty("userName", userName);
                    request.addProperty("command", "delete-iot-complex");
                    request.addProperty("complex", complex.getText().toString());
                    request.addProperty("enterpriseId", managementViewModel.getSelectedEnterprise().getName());

/*
                    RequestManagementLambda request = new RequestManagementLambda(
                            userName,
                            "delete-iot-complex",
                            complex.getText().toString(),
                            managementViewModel.getSelectedEnterprise().getEnterpriseDisplayName()

                    );
*/

                    lambdaClient.ExecuteDeleteComplexLambda(request, new DeleteComplexResponseHandler() {
                        @Override
                        public void onSuccess(@NonNull JsonObject response) {
                            userAlertClient.closeWaitDialog();
                            int statusCode = response.get("statusCode").getAsInt();
                            String message = response.get("body").getAsString();
                            if(statusCode == 200) {
                                userAlertClient.showDialogMessage("Delete Successful", message, ComplexDetailsDialog.this::dismiss);
                            } else {
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

                @Override
                public void onError(String ErrorMsg) {
                    confirmActionFragment.dismiss();
                }
            });

        }
        else if (id == date.getId()) {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(
                    getActivity(),
                    (view1, year, monthOfYear, dayOfMonth) -> {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        date.setText(new SimpleDateFormat("dd/MM/yyyy").format(newDate.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        }
        else if (id == clientName.getId()) {
            userAlertClient.showWaitDialog("Getting clients");
            ManagementLambdaRequest request = new ManagementLambdaRequest(
                    sharedPrefsClient.getUserDetails().getUser().getUserName(),
                    "list-iot-clientName"
            );

            fetchListOfClientName(request, clientListCallback, "list-iot-clientName");
        }
        else if (id == billingGroup.getId()) {
            userAlertClient.showWaitDialog("Getting Billing Groups");

            ManagementLambdaRequest request = new ManagementLambdaRequest(sharedPrefsClient.getUserDetails().getUser().getUserName(), "list-billing-groups");
            lambdaClient.ExecuteManagementLambda(request, listBillingGroups);

        }
        else if (id == cancelDialog.getId()) {
            dismiss();
        }
    }

    public void registerNewClient() {
        RegisterNewClient registerNewClient = new RegisterNewClient();
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        Fragment prev = getParentFragmentManager().findFragmentByTag("RegisterNewClient");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        registerNewClient.show(ft, "RegisterNewClient");
    }

    public void registerNewBillingGroup() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        Fragment prev = getParentFragmentManager().findFragmentByTag("RegisterNewBillingGroup");
        if (prev != null) {
            transaction.remove(prev);
        }

        RegisterNewBillingGroup newBillingGroup1 = new RegisterNewBillingGroup();
        newBillingGroup1.show(transaction, "RegisterNewBillingGroup");
    }

    private void updateComplex() {
        ArrayList<Attributes> attributes = new ArrayList<>();
        String userName = sharedPrefsClient.getUserDetails().getUser().getUserName();
        attributes.add(new Attributes("STATE_NAME", complexDetails.getStateName()));
        attributes.add(new Attributes("DISTRICT_NAME", complexDetails.getDistrictName()));
        attributes.add(new Attributes("CITY_NAME", complexDetails.getCityName()));
        attributes.add(new Attributes("STATE_CODE", complexDetails.getStateCode()));
        attributes.add(new Attributes("DISTRICT_CODE", complexDetails.getDistrictCode()));
        attributes.add(new Attributes("CITY_CODE", complexDetails.getCityCode()));
        attributes.add(new Attributes("DEVT", deviceType.getText().toString() ));
        attributes.add(new Attributes("ADDR", address.getText().toString().trim() ));
        attributes.add(new Attributes("LATT", complexDetails.getLatitude() ));
        attributes.add(new Attributes("LONG", complexDetails.getLongitude() ));
        attributes.add(new Attributes("CLNT", complexDetails.getClient().trim() ));
        attributes.add(new Attributes("BILL", complexDetails.getBillingGroup().trim() ));
        attributes.add(new Attributes("DATE", complexDetails.getDate() ));
        attributes.add(new Attributes("SLVL", smartness.getText().toString() ));
        attributes.add(new Attributes("QMWC", maleWc.getText().toString() ));
        attributes.add(new Attributes("QFWC", femaleWc.getText().toString() ));
        attributes.add(new Attributes("QPWC", pdWc.getText().toString() ));
        attributes.add(new Attributes("QURI", urinal.getText().toString() ));
        attributes.add(new Attributes("QURC", urinalCabin.getText().toString() ));
        attributes.add(new Attributes("QBWT", bwt.getText().toString() ));
        attributes.add(new Attributes("COCO", complexDetails.getCommissioningStatus() ));
        attributes.add(new Attributes("QSNV", napkinVmCount.getText().toString() ));
        attributes.add(new Attributes("MSNV", napkinVmManufacturer.getText().toString().trim() ));
        attributes.add(new Attributes("QSNI", napkinIncineratorCount.getText().toString() ));
        attributes.add(new Attributes("MSNI", napkinIncineratorManufacturer.getText().toString().trim() ));
        attributes.add(new Attributes("AR_K", kioskArea.getText().toString().trim() ));
        attributes.add(new Attributes("CWTM", waterAtmCapacity.getText().toString() ));
        attributes.add(new Attributes("ARSR", supervisorRoomSize.getText().toString() ));
        attributes.add(new Attributes("MANU", manufacturer.getText().toString().trim()));
        attributes.add(new Attributes("TECH", techProvider.getText().toString().trim()));
        attributes.add(new Attributes("CIVL", civilPartner.getText().toString().trim()));
        attributes.add(new Attributes("ONMP", omPartner.getText().toString().trim()));
        attributes.add(new Attributes("UUID", complexDetails.getUuid()));
        attributes.add(new Attributes("MODIFIED_BY", userName));
        attributes.add(new Attributes("THINGGROUPTYPE", "COMPLEX"));
        attributes.add(new Attributes("ROUTER_IMEI",  routerImeiET.getText().toString().trim()));
        attributes.add(new Attributes("ROUTER_MOBILE",  routerMobileET.getText().toString().trim()));

        UpdateComplexPayloadValue value = new UpdateComplexPayloadValue();
        value.setAttributes(attributes);
        value.setName(complexDetails.getComplexName());

        UpdateComplexPayload payload = new UpdateComplexPayload();

        payload.setCommand("update-iot-complex");
        payload.setUserName(userName);
        payload.setValue(value);

        userAlertClient.showWaitDialog("Updating complex...");

        Log.i("UpdateComplex", "Final Payload -: " + new Gson().toJson(payload));
        lambdaClient.ExecuteManagementLambda(payload, new ListIotStateDistrictCityResponseHandler() {
            @Override
            public void onSuccess(@NonNull JsonObject response) {
                userAlertClient.closeWaitDialog();
                int statusCode = response.get("statusCode").getAsInt();
                if(statusCode == 200) {
                    String message = response.get("body").getAsString();
                    userAlertClient.showDialogMessage("Update Successful", message, false);
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

    ListIotStateDistrictCityResponseHandler clientListCallback = new ListIotStateDistrictCityResponseHandler() {
        @Override
        public void onSuccess(@NonNull JsonObject response) {
            userAlertClient.closeWaitDialog();
            Log.i("listClientName", response.toString());
            JsonArray jsonArray = response.getAsJsonArray("body");
            String[] array = new String[jsonArray.size()];

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject obj = jsonArray.get(i).getAsJsonObject();
                array[i] = obj.get("Name").getAsString();
            }

            showDialog(SELECT_CLIENT, array);
        }

        @Override
        public void onError(@Nullable String message) {
            userAlertClient.closeWaitDialog();
            userAlertClient.showDialogMessage("Error", message, false);
        }
    };

    ListIotStateDistrictCityResponseHandler listBillingGroups = new ListIotStateDistrictCityResponseHandler() {
        @Override
        public void onSuccess(@NonNull JsonObject response) {
            userAlertClient.closeWaitDialog();
            int statusCode = response.get("statusCode").getAsInt();
            Log.i("listBillingGroups", String.valueOf(statusCode));
            if (statusCode == 403) {
                userAlertClient.showDialogMessage(
                        "Access Denied",
                        "You do not have permission",
                        false
                );
            } else if (statusCode == 200) {
                JsonArray jsonArray = response.getAsJsonArray("body");

                if (jsonArray.size() > 0) {
                    String[] list = new String[jsonArray.size()];
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject obj = jsonArray.get(i).getAsJsonObject();
                        list[i] = obj.get("Name").getAsString();
                    }

                    showDialog(SELECT_BILLING_GROUP, list);

                }
            }

        }

        @Override
        public void onError(@Nullable String message) {
            userAlertClient.closeWaitDialog();
            userAlertClient.showDialogMessage("Error", message, false);
        }
    };

    FetchClientLogoResponseHandler fetchClientLogoCallback = new FetchClientLogoResponseHandler() {
        @Override
        public void onSuccess(@NonNull JsonObject response) {
            userAlertClient.closeWaitDialog();
            int statusCode = response.get("statusCode").getAsInt();
            if(statusCode == 200) {
                JsonArray logoList = response.getAsJsonArray("body");
                for (int i = 0; i < logoList.size(); i++) {
                    listOfClientLogo.add(logoList.get(i).getAsString());
                }
//                setupUi(retrievedComplexDetails);
                setLogoAdapter();
            }
            else {
                userAlertClient.showDialogMessage("Error", "", false);
            }
        }

        @Override
        public void onError(@NonNull String error) {
            userAlertClient.closeWaitDialog();
            userAlertClient.showDialogMessage("Error", error, false);
        }
    };


    private void showDialog(int action, String[] list) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        if (action == SELECT_CLIENT) {
            dialog.setTitle("Select Client");
            dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {
                clientName.setText(list[position]);

                fetchClientLogo();
                dialogInterface.dismiss();
            });

            dialog.show();
        }
        else if (action == SELECT_BILLING_GROUP) {
            dialog.setTitle("Select Billing Group");
            dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {

                billingGroup.setText(list[position]);
                dialogInterface.dismiss();
            });
            dialog.show();
        }

    }

    private boolean verifyDetails() {
        errorList = new ArrayList<>();
//        validateField(!complexDetails.isValidName(), R.id.complexNameErr, "Complex Name not validated. Please enter a name and validate.");

//        validateField(complexDetails.getStateName().isEmpty(), R.id.stateNameErr, "State not selected. Please select 'State' for the complex.");

//        validateField(complexDetails.getDistrictName().isEmpty(), R.id.districtNameErr, "District not selected. Please select 'District' for the complex.");

//        validateField(complexDetails.getCityName().isEmpty(), R.id.cityNameErr, "City not selected. Please select 'City' for the complex.");

        validateField(address.getText().toString().trim().isEmpty(), R.id.addressLayout, "Address not provided. Please provide 'Address' for the complex.");

        complexDetails.setClient(clientName.getText().toString());
        validateField(complexDetails.getClient().isEmpty(), R.id.clientNameLayout, "Client not selected. Please select 'Client' for the complex.");

        complexDetails.setBillingGroup(billingGroup.getText().toString());
        validateField(complexDetails.getBillingGroup().isEmpty(), R.id.billingGroupLayout, "Billing Group not selected. Please select 'Billing Group' for the complex.");

        complexDetails.setDate(date.getText().toString());
        validateField(complexDetails.getDate().isEmpty(), R.id.dateLayout, "Date not selected. Please select 'Date' for the complex.");

        complexDetails.setCommissioningStatus(commissioningStatus.getText().toString());
        validateField(complexDetails.getCommissioningStatus().isEmpty(), R.id.commissioningStatusLayout, "Commissioning Status not selected. Please select 'Commissioning Status' for the complex.");

        complexDetails.setSmartness(smartness.getText().toString());
        validateField(complexDetails.getSmartness().isEmpty(), R.id.smartnessLevelLayout, "Smartness Level not selected. Please select 'Smartness Level' for the complex.");

        complexDetails.setKioskArea( kioskArea.getText().toString());
        validateField(kioskArea.getText().toString().trim().isEmpty(), R.id.areaOfKioskLayout, "KIOSK Area Invalid. Please provide 'Area Of KIOSK' for the complex.");

        complexDetails.setWaterAtmCapacity(waterAtmCapacity.getText().toString());
        validateField(complexDetails.getWaterAtmCapacity().isEmpty(), R.id.waterAtmCapacityLayout, "Water Atm Capacity Area Invalid. Please provide 'Water Atm Capacity' for the complex.");

        complexDetails.setSupervisorRoomSize(supervisorRoomSize.getText().toString().trim());
        validateField(!validateInteger(complexDetails.getSupervisorRoomSize()), R.id.supervisorRoomSizeLayout, "Supervisor Room Size Invalid. Please provide valid 'Supervisor Room Size' for the complex.");

        complexDetails.setManufacturer(manufacturer.getText().toString().trim());
        validateField(complexDetails.getManufacturer().isEmpty(), R.id.manufacturerLayout, "Manufacturer Invalid. Please provide a valid 'Manufacturer' for the complex.");

        complexDetails.setTechProvider(techProvider.getText().toString().trim());
        validateField(complexDetails.getTechProvider().isEmpty(), R.id.techProviderLayout, "Tech Provider Invalid. Please provide a valid 'Tech Provider' for the complex.");

        complexDetails.setCivilPartner(civilPartner.getText().toString().trim());
        validateField(complexDetails.getCivilPartner().isEmpty(), R.id.civilPartnerLayout, "Civil Partner Invalid. Please provide a valid 'Civil Partner' for the complex.");

        complexDetails.setOMPartner(omPartner.getText().toString().trim());
        validateField(complexDetails.getOMPartner().isEmpty(), R.id.oAndMPartnersLayout, "O And M Partner Invalid. Please provide a valid 'O And M Partner' for the complex.");

        complexDetails.setWCCountMale(maleWc.getText().toString());
        validateField(!validateInteger(complexDetails.getWCCountMale()), R.id.maleWcLayout, "WC Count Invalid. Please provide a valid 'Number of WC' for the complex.");

        complexDetails.setWCCountFemale(femaleWc.getText().toString());
        validateField(!validateInteger(complexDetails.getWCCountFemale()), R.id.femaleWcLayout, "WC Count Invalid. Please provide a valid 'Number of WC' for the complex.");

        complexDetails.setWCCountPD(pdWc.getText().toString());
        validateField(!validateInteger(complexDetails.getWCCountPD()), R.id.pWcLayout, "WC Count Invalid. Please provide a valid 'Number of WC' for the complex.");

        complexDetails.setUrinals(urinal.getText().toString());
        validateField(!validateInteger(complexDetails.getUrinals()), R.id.urinalLayout, "Urinals Count Invalid. Please provide a valid 'Number of Urinals' for the complex.");

        complexDetails.setUrinalCabins(urinalCabin.getText().toString());
        validateField(!validateInteger(complexDetails.getUrinalCabins()), R.id.urinalCabinsLayout, "Urinal Cabins Count Invalid. Please provide a valid 'Number of Urinal Cabins' for the complex.");

        complexDetails.setBwt(bwt.getText().toString());
        validateField(!validateInteger(bwt.getText().toString()), R.id.bwtLayout, "BWT Count Invalid. Please provide a valid 'Number of BWTs' for the complex.");

        complexDetails.setNapkinVmCount(napkinVmCount.getText().toString());
        validateField(!validateInteger(complexDetails.getNapkinVmCount()), R.id.napkinVendingMachineLayout, "Napkin Vm Count not selected. Please select 'Number of Napkin Vending Machines' for the complex.");

        complexDetails.setNapkinVmManufacturer(napkinVmManufacturer.getText().toString().trim());
        validateField(complexDetails.getNapkinVmManufacturer().isEmpty(), R.id.napkinVMManufacturerLayout, "Napkin Vending Machine Manufacturer Invalid. Please provide 'Vending Machine Manufacturer' for the complex.");

        complexDetails.setNapkinIncineratorCount(napkinIncineratorCount.getText().toString());
        validateField(!validateInteger(complexDetails.getNapkinIncineratorCount()), R.id.napkinIncineratorLayout, "Napkin Incinerator Count not selected. Please select 'Number of Napkin Incinerators' for the complex.");

        complexDetails.setNapkinIncineratorManufacturer(napkinIncineratorManufacturer.getText().toString().trim());
        validateField(complexDetails.getNapkinIncineratorManufacturer().isEmpty(), R.id.napkinIncineratorManufacturerLayout, "Napkin Incinerator Manufacturer Invalid. Please provide 'Napkin Incinerator Manufacturer' for the complex.");

        complexDetails.setRouterImei(routerImeiET.getText().toString().trim());
        validateField(complexDetails.getRouterImei().isEmpty(), R.id.routerImeiLayout, "Router IMEI Invalid. Please provide Router IMEI");

        complexDetails.setRouterMobile(routerMobileET.getText().toString().trim());
        validateField(complexDetails.getRouterMobile().isEmpty(), R.id.routerMobileLayout, "Router Mobile Invalid. Please provide Router Mobile Number");


        for (int i = 0; i < errorList.size(); i++) {
            ((TextInputLayout) ComplexDetailsDialog.this.getView().findViewById(errorList.get(i).getViewRef())).setError(errorList.get(i).getMessage());
            ((TextInputLayout) ComplexDetailsDialog.this.getView().findViewById(errorList.get(i).getViewRef())).setErrorEnabled(true);

            Log.i("registerComplex", errorList.get(i).getMessage());
        }

        if(clientLogoAdapter.getSelectedImagePosition() == -1) {
            clientLogoError.setVisibility(View.VISIBLE);
        }
        else {
            clientLogoError.setVisibility(View.GONE);
        }

        if (errorList.isEmpty() && clientLogoAdapter.getSelectedImagePosition() != -1)
            return true;
        else {
            String Msg = errorList.size() == 1 ? errorList.size() + " error found. Please correct it to proceed"
                    : errorList.size() + " errors found. Please correct them to proceed";
            userAlertClient.showDialogMessage("Validation Error!", Msg, false);
            return false;
        }
    }

    private void validateField(boolean check, int ref, String Message) {
        if (check) {
            errorList.add(new ErrorView(Message, ref));
        }
        else {
            clearErrorMessage(ref);
        }
    }

    private void clearErrorMessage(int viewRef) {
        ((TextInputLayout) ComplexDetailsDialog.this.getView().findViewById(viewRef)).setErrorEnabled(false);
    }

    public static boolean validateInteger(String input) {
        return input.matches("-?\\d+");
    }

    public ComplexDetails getRetrievedComplexDetails() {
        return retrievedComplexDetails;
    }

    public void setRetrievedComplexDetails(ComplexDetails retrievedComplexDetails) {
        this.retrievedComplexDetails = retrievedComplexDetails;
    }

    public void fetchListOfClientName(ManagementLambdaRequest request,ListIotStateDistrictCityResponseHandler clientCallback,String command) {
        lambdaClient.ExecuteManagementLambda(request, clientCallback);
    }

    public void setManagementViewModel(ManagementViewModel managementViewModel) {
        this.managementViewModel = managementViewModel;
    }

    private void fetchClientLogo() {
        JsonObject request = new JsonObject();
        request.addProperty("client", retrievedComplexDetails.getClient());
        userAlertClient.showWaitDialog("Fetching Client Logos");
        lambdaClient.ExecuteFetchClientLogoLambda(request, fetchClientLogoCallback);
    }
    private void setLogoAdapter() {
//        ArrayList<String> list = new ArrayList<>();
//        list.add("https://download.logo.wine/logo/Indian_Oil_Corporation/Indian_Oil_Corporation-Logo.wine.png");
//        list.add("https://download.logo.wine/logo/Bharat_Petroleum/Bharat_Petroleum-Logo.wine.png");
//        list.add("https://gis.smartcitymoradabad.org/img/mscllogo.jpg");
//        list.add("https://download.logo.wine/logo/Oil_and_Natural_Gas_Corporation/Oil_and_Natural_Gas_Corporation-Logo.wine.png");
//        list.add("https://download.logo.wine/logo/Abu_Dhabi_National_Oil_Company/Abu_Dhabi_National_Oil_Company-Logo.wine.png");
//        list.add("https://download.logo.wine/logo/Indian_Oil_Corporation/Indian_Oil_Corporation-Logo.wine.png");
//        list.add("https://download.logo.wine/logo/Bharat_Petroleum/Bharat_Petroleum-Logo.wine.png");
//        list.add("https://gis.smartcitymoradabad.org/img/mscllogo.jpg");
//        list.add("https://download.logo.wine/logo/Oil_and_Natural_Gas_Corporation/Oil_and_Natural_Gas_Corporation-Logo.wine.png");
//        list.add("https://download.logo.wine/logo/Abu_Dhabi_National_Oil_Company/Abu_Dhabi_National_Oil_Company-Logo.wine.png");
//        list.add("https://download.logo.wine/logo/Indian_Oil_Corporation/Indian_Oil_Corporation-Logo.wine.png");
//        list.add("https://download.logo.wine/logo/Bharat_Petroleum/Bharat_Petroleum-Logo.wine.png");
//        list.add("https://gis.smartcitymoradabad.org/img/mscllogo.jpg");
//        list.add("https://download.logo.wine/logo/Oil_and_Natural_Gas_Corporation/Oil_and_Natural_Gas_Corporation-Logo.wine.png");
//        list.add("https://download.logo.wine/logo/Abu_Dhabi_National_Oil_Company/Abu_Dhabi_National_Oil_Company-Logo.wine.png");

        clientLogoView.setVisibility(View.VISIBLE);
        clientLogoAdapter = new ClientLogoAdapter(listOfClientLogo);
        clientLogoView.setAdapter(clientLogoAdapter);
        if(Utilities.isTabletDevice(getContext()))
            clientLogoView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        else
            clientLogoView.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }
}
