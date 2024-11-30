package sukriti.ngo.mis.ui.management.EnrollDevice.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Attribute;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Attributes;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ComplexDetails;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ErrorView;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.RegisterComplexPayload;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.RegisterComplexPayloadValue;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.NameCodeModel;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.RegisterNewBillingGroup;
import sukriti.ngo.mis.ui.management.adapters.ClientLogoAdapter;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListIotStateDistrictCityResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ManagementLambdaRequest;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.RequestManagementLambda;
import sukriti.ngo.mis.ui.management.lambda.FetchClientLogo.FetchClientLogoResponseHandler;
import sukriti.ngo.mis.utils.LambdaClient;
import sukriti.ngo.mis.utils.SharedPrefsClient;
import sukriti.ngo.mis.utils.UserAlertClient;
import sukriti.ngo.mis.utils.Utilities;

public class RegisterComplex extends DialogFragment implements View.OnClickListener {

    private TextInputLayout stateLayout, districtLayout, cityLayout, addressLayout, latitudeLayout, longitudeLayout, complexNameLayout;
    private AutoCompleteTextView stateName, districtName, cityName;
    private TextInputEditText address, latitude, longitude, complexName;
    private RecyclerView clientLogoView;
    private TextView clientLogoError;

    private TextInputLayout clientNameLayout, billingGroupLayout, dateLayout;
    private AutoCompleteTextView clientName, billingGroup, date;

    private AutoCompleteTextView commissioning, deviceType, smartness ;
    private TextInputEditText maleWc, femaleWc, pdWc, urinals, urinalCabins, bwt, napkinVmCount, napkinVmManufacturer, napkinIncineratorCount, napkinIncineratorManufacturer, kioskArea, waterAtmCapacity, supervisorRoomSize;
    private TextInputEditText omPartner, civilPartner, techProvider, manufacturer;
    private FloatingActionButton fab;

    private TextInputEditText routerImeiET, routerMobileET;
    private ClientLogoAdapter clientLogoAdapter;
    private LambdaClient lambdaClient;
    private SharedPrefsClient sharedPrefsClient;
    private UserAlertClient userAlertClient;

    private ArrayList<NameCodeModel> statesList = new ArrayList<>();
    private ArrayList<NameCodeModel> districtList = new ArrayList<>();
    private ArrayList<NameCodeModel> cityList = new ArrayList<>();

    private ArrayList<NameCodeModel> ddbStatesList = new ArrayList<>();
    private ArrayList<NameCodeModel> ddbDistrictList = new ArrayList<>();
    private ArrayList<NameCodeModel> ddbCityList = new ArrayList<>();
    private ArrayList<ErrorView> errorList;
    ArrayList<String> complexNameList;
    ArrayList<String> listOfClientLogo;
    String UUID;


    private EnrollDeviceViewModel viewModel;
    private ComplexDetails complexDetails;


    private final int SELECT_STATE = 0;
    private final int SELECT_DISTRICT = 1;
    private final int SELECT_CITY = 2;
    private final int SELECT_CLIENT = 3;
    private final int SELECT_STATE_DDB = 4;
    private final int SELECT_DISTRICT_DDB = 5;
    private final int SELECT_CITY_DDB = 6;
    private final int SELECT_BILLING_GROUP = 7;
    int[] index = {0};
    int[] complexNum = {0};

    private int selectedState = -1;
    private int selectedDistrict = -1;
    private int selectedCity = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_new_complex, container, false);

        stateName = view.findViewById(R.id.state);

        stateLayout = view.findViewById(R.id.stateLayout);
        districtName = view.findViewById(R.id.district);
        districtLayout = view.findViewById(R.id.districtLayout);
        cityName = view.findViewById(R.id.city);
        cityLayout = view.findViewById(R.id.cityLayout);

        address = view.findViewById(R.id.address);
        addressLayout = view.findViewById(R.id.addressLayout);

        complexNameLayout = view.findViewById(R.id.complexNameLayout);

        clientNameLayout = view.findViewById(R.id.clientNameLayout);
        billingGroupLayout = view.findViewById(R.id.billingGroupLayout);
        dateLayout = view.findViewById(R.id.dateLayout);
        clientName = view.findViewById(R.id.clientName);
        clientLogoView = view.findViewById(R.id.logoRecyclerView);
        clientLogoError = view.findViewById(R.id.logoError);
        billingGroup = view.findViewById(R.id.billingGroup);
        date = view.findViewById(R.id.date);

        commissioning = view.findViewById(R.id.commissioning);
        deviceType = view.findViewById(R.id.deviceType);
        smartness = view.findViewById(R.id.smartness);

        maleWc = view.findViewById(R.id.maleWc);
        femaleWc = view.findViewById(R.id.femaleWc);
        pdWc = view.findViewById(R.id.pdWc);
        urinals = view.findViewById(R.id.urinals);
        urinalCabins = view.findViewById(R.id.urinalCabins);
        bwt = view.findViewById(R.id.bwt);
        napkinVmCount = view.findViewById(R.id.napkinVmCount);
        napkinVmManufacturer = view.findViewById(R.id.napkinVmManufacturer);
        napkinIncineratorCount = view.findViewById(R.id.napkinIncineratorCount);
        napkinIncineratorManufacturer = view.findViewById(R.id.napkinIncineratorManufacturer);
        kioskArea = view.findViewById(R.id.kioskArea);
        waterAtmCapacity = view.findViewById(R.id.waterAtmCapacity);
        supervisorRoomSize = view.findViewById(R.id.supervisorRoomSize);

        routerImeiET = view.findViewById(R.id.routerImeiET);
        routerMobileET = view.findViewById(R.id.routerMobileET);

        complexName = view.findViewById(R.id.complexName);

        manufacturer = view.findViewById(R.id.manufacturer);
        civilPartner = view.findViewById(R.id.civilPartner);
        omPartner = view.findViewById(R.id.omPartner);
        techProvider = view.findViewById(R.id.techProvider);

        complexDetails = new ComplexDetails();
        setTextWatcher(complexName);

        fab = view.findViewById(R.id.fabRegisterComplex);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setOnClickListener();
        viewModel = ViewModelProviders.of(getActivity()).get(EnrollDeviceViewModel.class);
    }

    private void setOnClickListener() {
        stateName.setOnClickListener( view -> fetchState());
        districtName.setOnClickListener(view -> fetchDistricts());
        cityName.setOnClickListener(view -> fetchCities());

        stateLayout.setEndIconOnClickListener(view -> newState());
        districtLayout.setEndIconOnClickListener(view -> newDistrict());
        cityLayout.setEndIconOnClickListener(view -> newCity());
        complexNameLayout.setEndIconOnClickListener(view -> verifyComplexName());


        clientName.setOnClickListener(this);
        billingGroup.setOnClickListener(this);
        clientNameLayout.setEndIconOnClickListener(view -> registerNewClient());
        billingGroupLayout.setEndIconOnClickListener( view -> registerNewBillingGroup());
        commissioning.setOnClickListener(this);
        date.setOnClickListener(this);

        deviceType.setOnClickListener(this);
        smartness.setOnClickListener(this);
//        complexNameLayout.setEndIconOnClickListener(this);
        fab.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;

            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            if(dialog.getWindow() != null)
                dialog.getWindow().setLayout(width, height);
        }

        lambdaClient = new LambdaClient(getContext());
        sharedPrefsClient = new SharedPrefsClient(getContext());
        userAlertClient = new UserAlertClient(getActivity());

//        tempSetDetails();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == fab.getId()) {
            if (verifyDetails()) {
                createComplexUUID();
            }
        }
        else if (id == clientName.getId()) {
            userAlertClient.showWaitDialog("Getting clients");
            ManagementLambdaRequest request = new ManagementLambdaRequest(
                    sharedPrefsClient.getUserDetails().getUser().getUserName(),
                    "list-iot-clientName"
            );

            viewModel.fetchListOf(request, clientListCallback, "list-iot-clientName");
        }
        else if (id == commissioning.getId()) {
            String[] choices = {"True", "False"};
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

            dialog.setSingleChoiceItems(choices, -1, (dialogInterface, i) -> {
                commissioning.setText(choices[i]);
                dialogInterface.dismiss();
            });

            dialog.show();
        }
        else if (id == smartness.getId()) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            String[] choices = {"None", "Basic", "Premium", "Extra-Premium"};
            dialog.setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    smartness.setText(choices[i]);
                    dialogInterface.dismiss();
                }
            });

            dialog.show();
        }
        else if (id == billingGroup.getId()) {
            userAlertClient.showWaitDialog("Getting Billing Groups");

            ManagementLambdaRequest request = new ManagementLambdaRequest(sharedPrefsClient.getUserDetails().getUser().getUserName(), "list-billing-groups");
            lambdaClient.ExecuteManagementLambda(request, listBillingGroups);
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
        /*else if (id == newClient.getId()) {
            RegisterNewClient registerNewClient = new RegisterNewClient();
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            Fragment prev = getParentFragmentManager().findFragmentByTag("RegisterNewClient");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            registerNewClient.show(ft, "RegisterNewClient");
        }*/
/*
        else if (id == newBillingGroup.getId()) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            Fragment prev = getParentFragmentManager().findFragmentByTag("RegisterNewBillingGroup");
            if (prev != null) {
                transaction.remove(prev);
            }

            RegisterNewBillingGroup newBillingGroup1 = new RegisterNewBillingGroup();
            newBillingGroup1.show(transaction, "RegisterNewBillingGroup");

        }
*/


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

    public void fetchState() {
        userAlertClient.showWaitDialog("Getting States");
        String userName = sharedPrefsClient.getUserDetails().getUser().getUserName();
        ManagementLambdaRequest request = new ManagementLambdaRequest(userName, "list-iot-state");

        viewModel.fetchListOf(request, statesListCallback, "list-iot-state");
    }

    public void fetchDistricts() {
        if (!stateName.getText().toString().isEmpty()) {
            stateLayout.setErrorEnabled(false);
            stateLayout.setHelperTextEnabled(false);
            userAlertClient.showWaitDialog("Getting Districts");
            ManagementLambdaRequest request = new ManagementLambdaRequest(
                    sharedPrefsClient.getUserDetails().getUser().getUserName(),
                    "list-iot-district",
                    statesList.get(selectedState).getCODE()
            );

            viewModel.fetchListOf(request, districtListCallback, "list-iot-district");
        } else {
            stateLayout.setErrorEnabled(true);
            stateLayout.setError(getString(R.string.selectState));
        }
    }

    public void fetchCities() {
        if (!districtName.getText().toString().isEmpty()) {
            districtLayout.setErrorEnabled(false);
            districtLayout.setHelperTextEnabled(false);
            userAlertClient.showWaitDialog("Getting City");
            ManagementLambdaRequest request = new ManagementLambdaRequest(
                    sharedPrefsClient.getUserDetails().getUser().getUserName(),
                    "list-iot-city",
                    districtList.get(selectedDistrict).getCODE()
            );

            viewModel.fetchListOf(request, cityListCallback, "list-iot-city");
        } else {
            districtLayout.setErrorEnabled(true);
            districtLayout.setError(getString(R.string.selectDistrict));
        }

    }

    private void createNewComplex() {
        ArrayList<Attributes> attributes = new ArrayList<>();

        attributes.add(new Attributes("STATE_NAME", statesList.get(selectedState).getNAME()));
        attributes.add(new Attributes("DISTRICT_NAME", districtList.get(selectedDistrict).getNAME()));
        attributes.add(new Attributes("CITY_NAME", cityList.get(selectedCity).getNAME()));
        attributes.add(new Attributes("STATE_CODE", statesList.get(selectedState).getCODE()));
        attributes.add(new Attributes("DISTRICT_CODE", districtList.get(selectedDistrict).getCODE()));
        attributes.add(new Attributes("CITY_CODE", cityList.get(selectedCity).getCODE()));
        attributes.add(new Attributes("DEVT", complexDetails.getDeviceType() ));
        attributes.add(new Attributes("ADDR", complexDetails.getAddress() ));
        attributes.add(new Attributes("LATT", complexDetails.getLatitude() ));
        attributes.add(new Attributes("LONG", complexDetails.getLongitude() ));
        attributes.add(new Attributes("CLNT", complexDetails.getClient() ));
        attributes.add(new Attributes("BILL", complexDetails.getBillingGroup() ));
        attributes.add(new Attributes("DATE", complexDetails.getDate() ));
        attributes.add(new Attributes("SLVL", complexDetails.getSmartness() ));
        attributes.add(new Attributes("QMWC", complexDetails.getWCCountMale() ));
        attributes.add(new Attributes("QFWC", complexDetails.getWCCountFemale() ));
        attributes.add(new Attributes("QPWC", complexDetails.getWCCountPD() ));
        attributes.add(new Attributes("QURI", complexDetails.getUrinals() ));
        attributes.add(new Attributes("QURC", complexDetails.getUrinalCabins() ));
        attributes.add(new Attributes("QBWT", complexDetails.getBwt() ));
        attributes.add(new Attributes("COCO", complexDetails.getCommissioningStatus() ));
        attributes.add(new Attributes("QSNV", complexDetails.getNapkinVmCount() ));
        attributes.add(new Attributes("MSNV", complexDetails.getNapkinVmManufacturer() ));
        attributes.add(new Attributes("QSNI", complexDetails.getNapkinIncineratorCount() ));
        attributes.add(new Attributes("MSNI", complexDetails.getNapkinIncineratorManufacturer() ));
        attributes.add(new Attributes("AR_K", complexDetails.getKioskArea() ));
        attributes.add(new Attributes("CWTM", complexDetails.getWaterAtmCapacity() ));
        attributes.add(new Attributes("ARSR", complexDetails.getSupervisorRoomSize() ));
        attributes.add(new Attributes("MANU", complexDetails.getManufacturer()));
        attributes.add(new Attributes("TECH", complexDetails.getTechProvider()));
        attributes.add(new Attributes("CIVL", complexDetails.getCivilPartner()));
        attributes.add(new Attributes("ONMP", complexDetails.getOMPartner()));
        attributes.add(new Attributes("UUID", complexDetails.getUuid()));
        attributes.add(new Attributes("MODIFIED_BY", sharedPrefsClient.getUserDetails().getUser().getUserName()));
        attributes.add(new Attributes("THINGGROUPTYPE", "COMPLEX"));
        attributes.add(new Attributes("ROUTER_IMEI",  complexDetails.getRouterImei()));
        attributes.add(new Attributes("ROUTER_MOBILE",  complexDetails.getRouterMobile()));
        if(clientLogoAdapter.getSelectedImagePosition() != -1) {
            attributes.add(new Attributes("CLIENT_LOGO",  listOfClientLogo.get(clientLogoAdapter.getSelectedImagePosition())));
        }
        else {
            attributes.add(new Attributes("CLIENT_LOGO",  ""));
        }

/*
        JsonObject finalObject = new JsonObject();
        finalObject.addProperty("userName", sharedPrefsClient.getUserDetails().getUser().getUserName());
        finalObject.addProperty("command", "add-iot-complex");
        finalObject.addProperty("Description", "");
        finalObject.addProperty("Name", complexDetails.getComplexName());
        finalObject.addProperty("Parent", cityList.get(selectedCity).getCODE());
*/

        RegisterComplexPayload finalPay = new RegisterComplexPayload();
        finalPay.setUserName(sharedPrefsClient.getUserDetails().getUser().getUserName());
        finalPay.setCommand("add-iot-complex");

        RegisterComplexPayloadValue value = new RegisterComplexPayloadValue();
        value.setAttributes(attributes);
        value.setDescription("");
        value.setParent(cityList.get(selectedCity).getCODE());
        value.setName(complexDetails.getComplexName());

        finalPay.setValue(value);
//        RequestManagementLambda request = new RequestManagementLambda(finalPay);

        ListIotStateDistrictCityResponseHandler callback = new ListIotStateDistrictCityResponseHandler() {
            @Override
            public void onSuccess(@NonNull JsonObject response) {
                userAlertClient.closeWaitDialog();
                Log.i("registerComplex", response.toString());
                int statusCode = response.get("statusCode").getAsInt();
                if(statusCode == 200) {
                    userAlertClient.showDialogMessage("Success", "Complex registered successfully", false);
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
        };

        userAlertClient.showWaitDialog("Registering Complex");
        Log.i("registerComplex", "Payload: " + new Gson().toJson(finalPay));
        lambdaClient.ExecuteManagementLambda(finalPay, callback);

//        JsonArray attributesArray = new JsonArray();

    }

    private boolean verifyDetails() {
        errorList = new ArrayList<>();
        validateField(!complexDetails.isValidName(), complexNameLayout.getId(), "Complex Name not validated. Please enter a name and validate.");

        validateField(complexDetails.getStateName().isEmpty(), R.id.stateLayout, "State not selected. Please select 'State' for the complex.");
        validateField(complexDetails.getDistrictName().isEmpty(), R.id.districtLayout, "District not selected. Please select 'District' for the complex.");
        validateField(complexDetails.getCityName().isEmpty(), R.id.cityLayout, "City not selected. Please select 'City' for the complex.");

        if(!stateName.getText().toString().isEmpty()) {
            complexDetails.setStateName(statesList.get(selectedState).getNAME());
            complexDetails.setStateName(statesList.get(selectedState).getCODE());
        }

        if(!districtName.getText().toString().isEmpty()) {
            complexDetails.setDistrictName(districtList.get(selectedDistrict).getNAME());
            complexDetails.setDistrictCode(districtList.get(selectedDistrict).getCODE());
        }

        if(!cityName.getText().toString().isEmpty()) {
            complexDetails.setCityName(cityList.get(selectedCity).getNAME());
            complexDetails.setCityCode(cityList.get(selectedCity).getCODE());
        }

        complexDetails.setAddress( address.getText().toString());
        validateField(complexDetails.getAddress().isEmpty(), R.id.addressLayout, "Address not provided. Please provide 'Address' for the complex.");

        complexDetails.setClient(clientName.getText().toString());
        validateField(complexDetails.getClient().isEmpty(), R.id.clientNameLayout, "Client not selected. Please select 'Client' for the complex.");
        complexDetails.setBillingGroup(billingGroup.getText().toString());
        validateField(complexDetails.getBillingGroup().isEmpty(), R.id.billingGroupLayout, "Client not selected. Please select 'Billing Group' for the complex.");
        complexDetails.setDate(date.getText().toString());
        validateField(complexDetails.getDate().isEmpty(), R.id.dateLayout, "Date not selected. Please select 'Date' for the complex.");
        complexDetails.setCommissioningStatus(commissioning.getText().toString());
        validateField(complexDetails.getCommissioningStatus().isEmpty(), R.id.commissioningStatusLayout, "Commissioning Status not selected. Please select 'Commissioning Status' for the complex.");
        complexDetails.setSmartness(smartness.getText().toString());
        validateField(complexDetails.getSmartness().isEmpty(), R.id.smartnessLevelLayout, "Smartness Level not selected. Please select 'Smartness Level' for the complex.");

        complexDetails.setKioskArea( kioskArea.getText().toString());
        validateField(complexDetails.getKioskArea().isEmpty(), R.id.areaOfKioskLayout, "KIOSK Area Invalid. Please provide 'Area Of KIOSK' for the complex.");

        complexDetails.setWaterAtmCapacity(waterAtmCapacity.getText().toString());
        validateField(complexDetails.getWaterAtmCapacity().isEmpty(), R.id.waterAtmCapacityLayout, "Water Atm Capacity Area Invalid. Please provide 'Water Atm Capacity' for the complex.");

        complexDetails.setSupervisorRoomSize(supervisorRoomSize.getText().toString());
        validateField(!validateInteger(complexDetails.getSupervisorRoomSize()), R.id.supervisorRoomSizeLayout, "Supervisor Room Size Invalid. Please provide valid 'Supervisor Room Size' for the complex.");

        complexDetails.setManufacturer(manufacturer.getText().toString().trim());
        validateField(complexDetails.getManufacturer().isEmpty(), R.id.manufacturerLayout, "Manufacturer Invalid. Please provide a valid 'Manufacturer' for the complex.");

        complexDetails.setTechProvider(techProvider.getText().toString());
        validateField(complexDetails.getTechProvider().isEmpty(), R.id.techProviderLayout, "Tech Provider Invalid. Please provide a valid 'Tech Provider' for the complex.");

        complexDetails.setCivilPartner(civilPartner.getText().toString());
        validateField(complexDetails.getCivilPartner().isEmpty(), R.id.civilPartnerLayout, "Civil Partner Invalid. Please provide a valid 'Civil Partner' for the complex.");

        complexDetails.setOMPartner(omPartner.getText().toString());
        validateField(complexDetails.getOMPartner().isEmpty(), R.id.oAndMPartnersLayout, "O And M Partner Invalid. Please provide a valid 'O And M Partner' for the complex.");

        complexDetails.setWCCountMale(maleWc.getText().toString());
        validateField(!validateInteger(complexDetails.getWCCountMale()), R.id.maleWcLayout, "WC Count Invalid. Please provide a valid 'Number of WC' for the complex.");

        complexDetails.setWCCountFemale(femaleWc.getText().toString());
        validateField(!validateInteger(complexDetails.getWCCountFemale()), R.id.femaleWcLayout, "WC Count Invalid. Please provide a valid 'Number of WC' for the complex.");

        complexDetails.setWCCountPD(pdWc.getText().toString());
        validateField(!validateInteger(complexDetails.getWCCountPD()), R.id.pWcLayout, "WC Count Invalid. Please provide a valid 'Number of WC' for the complex.");

        complexDetails.setUrinals(urinals.getText().toString());
        validateField(!validateInteger(complexDetails.getUrinals()), R.id.urinalLayout, "Urinals Count Invalid. Please provide a valid 'Number of Urinals' for the complex.");

        complexDetails.setUrinalCabins(urinalCabins.getText().toString());
        validateField(!validateInteger(complexDetails.getUrinalCabins()), R.id.urinalCabinsLayout, "Urinal Cabins Count Invalid. Please provide a valid 'Number of Urinal Cabins' for the complex.");

        complexDetails.setBwt(bwt.getText().toString());
        validateField(!validateInteger(complexDetails.getBwt()), R.id.bwtLayout, "BWT Count Invalid. Please provide a valid 'Number of BWTs' for the complex.");

        complexDetails.setNapkinVmCount(napkinVmCount.getText().toString());
        validateField(!validateInteger(complexDetails.getNapkinVmCount()), R.id.napkinVendingMachineLayout, "Napkin Vm Count not selected. Please select 'Number of Napkin Vending Machines' for the complex.");

        complexDetails.setNapkinVmManufacturer(napkinVmManufacturer.getText().toString());
        validateField(complexDetails.getNapkinVmManufacturer().isEmpty(), R.id.napkinVMManufacturerLayout, "Napkin Vending Machine Manufacturer Invalid. Please provide 'Vending Machine Manufacturer' for the complex.");

        complexDetails.setNapkinIncineratorCount(napkinIncineratorCount.getText().toString());
        validateField(!validateInteger(complexDetails.getNapkinIncineratorCount()), R.id.napkinIncineratorLayout, "Napkin Incinerator Count not selected. Please select 'Number of Napkin Incinerators' for the complex.");

        complexDetails.setNapkinIncineratorManufacturer(napkinIncineratorManufacturer.getText().toString());
        validateField(complexDetails.getNapkinIncineratorManufacturer().isEmpty(), R.id.napkinIncineratorManufacturerLayout, "Napkin Incinerator Manufacturer Invalid. Please provide 'Napkin Incinerator Manufacturer' for the complex.");

        complexDetails.setRouterImei(routerImeiET.getText().toString());
        validateField(complexDetails.getRouterImei().isEmpty(), R.id.routerImeiLayout, "Router IMEI Invalid. Please provide Router IMEI");

        complexDetails.setRouterMobile(routerMobileET.getText().toString());
        validateField(complexDetails.getRouterMobile().isEmpty(), R.id.routerMobileLayout, "Router Mobile Invalid. Please provide Router Mobile Number");

        if(listOfClientLogo != null && listOfClientLogo.isEmpty()) {
            clientLogoError.setVisibility(View.GONE);
        } else {
            if(clientLogoAdapter != null) {
                if (clientLogoAdapter.getSelectedImagePosition() == -1) {
                    clientLogoError.setVisibility(View.VISIBLE);
                } else {
                    clientLogoError.setVisibility(View.GONE);
                }
            }
        }

        for (int i = 0; i < errorList.size(); i++) {
            ((TextInputLayout) RegisterComplex.this.getView().findViewById(errorList.get(i).getViewRef())).setError(errorList.get(i).getMessage());
            ((TextInputLayout) RegisterComplex.this.getView().findViewById(errorList.get(i).getViewRef())).setErrorEnabled(true);

            Log.i("registerComplex", errorList.get(i).getMessage());
        }

        if (errorList.isEmpty() && (listOfClientLogo.isEmpty() || clientLogoAdapter.getSelectedImagePosition() != -1))
            return true;
        else {
            String Msg = errorList.size() == 1 ? errorList.size() + " error found. Please correct it to proceed"
                    : errorList.size() + " errors found. Please correct them to proceed";
            userAlertClient.showDialogMessage("Validation Error!", Msg, false);
            return false;
        }
    }


    ListIotStateDistrictCityResponseHandler statesListCallback = new ListIotStateDistrictCityResponseHandler() {
        @Override
        public void onSuccess(@NonNull JsonObject response) {
            userAlertClient.closeWaitDialog();
            int statusCode = response.get("statusCode").getAsInt();
            Log.i("registerComplex", String.valueOf(statusCode));
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
                    ArrayList<NameCodeModel> tempStateList = new ArrayList<>();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject obj = jsonArray.get(i).getAsJsonObject();
                        NameCodeModel tempObj = new NameCodeModel();
                        tempObj.setNAME(obj.get("NAME").getAsString());
                        tempObj.setCODE(obj.get("CODE").getAsString());
                        tempStateList.add(tempObj);
                        list[i] = obj.get("NAME").getAsString();
                    }

                    statesList = tempStateList;
                    showDialog(SELECT_STATE, list);

                }
            }
        }

        @Override
        public void onError(@Nullable String message) {
            userAlertClient.closeWaitDialog();
            userAlertClient.showDialogMessage("Error", message, false);
        }
    };
    ListIotStateDistrictCityResponseHandler districtListCallback = new ListIotStateDistrictCityResponseHandler() {
        @Override
        public void onSuccess(@NonNull JsonObject response) {
            userAlertClient.closeWaitDialog();

            int statusCode = response.get("statusCode").getAsInt();
            if (statusCode == 403) {
                userAlertClient.showDialogMessage(
                        "Access Denied",
                        "You do not have permission",
                        false
                );
            }
            else if (statusCode == 200) {
                JsonArray jsonArray = response.getAsJsonArray("body");

                if (jsonArray.size() > 0) {
                    String[] list = new String[jsonArray.size()];
                    ArrayList<NameCodeModel> tempList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject obj = jsonArray.get(i).getAsJsonObject();
                        NameCodeModel tempObj = new NameCodeModel();
                        tempObj.setNAME(obj.get("NAME").getAsString());
                        tempObj.setCODE(obj.get("CODE").getAsString());
                        tempList.add(tempObj);
                        list[i] = obj.get("NAME").getAsString();
                    }

                    districtList = tempList;
                    showDialog(SELECT_DISTRICT, list);
                } else {
                    userAlertClient.showDialogMessage("Data not available", "No Districts Found", false);
                }
            }
        }

        @Override
        public void onError(@Nullable String message) {
            userAlertClient.closeWaitDialog();
            userAlertClient.showDialogMessage("Error", message, false);
        }
    };
    ListIotStateDistrictCityResponseHandler cityListCallback = new ListIotStateDistrictCityResponseHandler() {
        @Override
        public void onSuccess(@NonNull JsonObject response) {
            userAlertClient.closeWaitDialog();

            int statusCode = response.get("statusCode").getAsInt();
            if (statusCode == 403) {
                userAlertClient.showDialogMessage(
                        "Access Denied",
                        "You do not have permission",
                        false
                );
            }
            else if (statusCode == 200) {
                JsonArray jsonArray = response.getAsJsonArray("body");

                if (jsonArray.size() > 0) {
                    String[] list = new String[jsonArray.size()];
                    ArrayList<NameCodeModel> tempList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject obj = jsonArray.get(i).getAsJsonObject();
                        NameCodeModel tempObj = new NameCodeModel();
                        tempObj.setNAME(obj.get("NAME").getAsString());
                        tempObj.setCODE(obj.get("CODE").getAsString());
                        tempList.add(tempObj);
                        list[i] = obj.get("NAME").getAsString();
                    }

                    cityList = tempList;
                    showDialog(SELECT_CITY, list);
                } else {
                    userAlertClient.showDialogMessage("Data not found", "No Record Available", false);
                }
            }
        }

        @Override
        public void onError(@Nullable String message) {
            userAlertClient.closeWaitDialog();
            userAlertClient.showDialogMessage("Error", message, false);
        }
    };
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

    ListIotStateDistrictCityResponseHandler dDbStatesListCallback = new ListIotStateDistrictCityResponseHandler() {
        @Override
        public void onSuccess(@NonNull JsonObject response) {
            userAlertClient.closeWaitDialog();
            Log.i("ddbStatesList", response.toString());

            int statusCode = response.get("statusCode").getAsInt();

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
                    ArrayList<NameCodeModel> statesList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject obj = jsonArray.get(i).getAsJsonObject();
                        NameCodeModel tempObj = new NameCodeModel();
                        tempObj.setNAME(obj.get("Name").getAsString());
                        tempObj.setCODE(obj.get("Code").getAsString());
                        statesList.add(tempObj);
                        list[i] = obj.get("Name").getAsString();
                    }
                    ddbStatesList = statesList;
                    showDialog(SELECT_STATE_DDB, list);
                } else {
                    userAlertClient.showDialogMessage("No Data Found", "No Records Available", false);
                }
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
                    ArrayList<NameCodeModel> districtList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject obj = jsonArray.get(i).getAsJsonObject();
                        NameCodeModel tempObj = new NameCodeModel();
                        tempObj.setNAME(obj.get("Name").getAsString());
                        tempObj.setCODE(obj.get("Code").getAsString());
                        districtList.add(tempObj);
                        list[i] = obj.get("Name").getAsString();
                    }
                    ddbDistrictList = districtList;
                    showDialog(SELECT_DISTRICT_DDB, list);
                } else {
                    userAlertClient.showDialogMessage("No Data Found", "No Records Available", false);
                }
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
                    ArrayList<NameCodeModel> cityList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.size(); i++) {
                        JsonObject obj = jsonArray.get(i).getAsJsonObject();
                        NameCodeModel tempObj = new NameCodeModel();
                        tempObj.setNAME(obj.get("Name").getAsString());
                        tempObj.setCODE(obj.get("Code").getAsString());
                        cityList.add(tempObj);
                        list[i] = obj.get("Name").getAsString();
                    }
                    ddbCityList = cityList;
                    showDialog(SELECT_CITY_DDB, list);
                } else {
                    userAlertClient.showDialogMessage("No Data Found", "No Records Available", false);
                }
            }

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
                listOfClientLogo = new ArrayList<>();
                for (int i = 0; i < logoList.size(); i++) {
                    listOfClientLogo.add(logoList.get(i).getAsString());
                }
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

        if (action == SELECT_STATE) {
            dialog.setTitle("Select State");
            dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {
                stateName.setText(list[position]);
                districtName.setText("");
                cityName.setText("");

                stateLayout.setHelperTextEnabled(false);
                districtLayout.setHelperTextEnabled(false);
                cityLayout.setHelperTextEnabled(false);

                selectedState = position;
                selectedDistrict = -1;
                selectedCity = -1;

                dialogInterface.dismiss();
            });

            dialog.show();
        }
        else if (action == SELECT_DISTRICT) {
            dialog.setTitle("Select District");
            if (!stateName.getText().toString().isEmpty()) {
                dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {
                    districtName.setText(list[position]);
                    cityName.setText("");

                    districtLayout.setHelperTextEnabled(false);
                    cityLayout.setHelperTextEnabled(false);

                    selectedDistrict = position;
                    selectedCity = -1;
                    dialogInterface.dismiss();
                });

                dialog.show();
            } else {
                stateLayout.setHelperTextEnabled(true);
            }
        }
        else if (action == SELECT_CITY) {
            dialog.setTitle("Select City");
            if (!districtName.getText().toString().isEmpty()) {
                dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {
                    cityName.setText(list[position]);
                    cityLayout.setHelperTextEnabled(false);
                    selectedCity = position;
                    dialogInterface.dismiss();
                });

                dialog.show();
            } else {
                districtName.setVisibility(View.VISIBLE);
            }
        }
        else if (action == SELECT_CLIENT) {
            dialog.setTitle("Select Client");
            dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {
                clientName.setText(list[position]);
//                setLogoAdapter();
                fetchClientLogo();
                dialogInterface.dismiss();
            });

            dialog.show();
        }
        else if (action == SELECT_STATE_DDB) {
            dialog.setTitle("Select State");
            dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {

                Attribute attribute = new Attribute(ddbStatesList.get(position).getCODE(), capsAllReplaceSpace(ddbStatesList.get(position).getNAME()));


                RequestManagementLambda request = new RequestManagementLambda(
                        sharedPrefsClient.getUserDetails().getUser().getUserName(),
                        "add-iot-state",
                        attribute
                );
                Log.i("add-iot-state", "showDialog: Request " + new Gson().toJson(request));

                ListIotStateDistrictCityResponseHandler callback = new ListIotStateDistrictCityResponseHandler() {
                    @Override
                    public void onSuccess(@NonNull JsonObject response) {
                        userAlertClient.closeWaitDialog();
                        int statusCode = response.get("statusCode").getAsInt();
                        String message = response.get("body").getAsString();

                        if (statusCode == 200) {
                            userAlertClient.showDialogMessage("Success", message, false);
                        } else {
                            userAlertClient.showDialogMessage("Error", message, false);
                        }
                    }

                    @Override
                    public void onError(@Nullable String message) {
                        userAlertClient.closeWaitDialog();
                        userAlertClient.showDialogMessage("Error", message, false);
                    }
                };

                userAlertClient.showWaitDialog("Adding State");
                lambdaClient.ExecuteManagementLambda(request, callback);

                dialogInterface.dismiss();
            });

            dialog.show();
        }
        else if (action == SELECT_DISTRICT_DDB) {
            dialog.setTitle("Select District");
            dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {

                Attribute attribute = new Attribute(ddbDistrictList.get(position).getCODE(), capsAllReplaceSpace(ddbDistrictList.get(position).getNAME()), statesList.get(selectedState).getCODE());

                RequestManagementLambda request = new RequestManagementLambda(
                        sharedPrefsClient.getUserDetails().getUser().getUserName(),
                        "add-iot-district",
                        attribute
                );

                Log.i("add-iot-district", "showDialog: Request " + new Gson().toJson(request));

                ListIotStateDistrictCityResponseHandler callback = new ListIotStateDistrictCityResponseHandler() {
                    @Override
                    public void onSuccess(@NonNull JsonObject response) {
                        userAlertClient.closeWaitDialog();
                        int statusCode = response.get("statusCode").getAsInt();
                        String message = response.get("body").getAsString();

                        if (statusCode == 200) {
                            userAlertClient.showDialogMessage("Success", message, false);
                        } else {
                            userAlertClient.showDialogMessage("Error", message, false);
                        }
                    }

                    @Override
                    public void onError(@Nullable String message) {
                        userAlertClient.closeWaitDialog();
                        userAlertClient.showDialogMessage("Error", message, false);
                    }
                };

                userAlertClient.showWaitDialog("Adding District");

                lambdaClient.ExecuteManagementLambda(request, callback);

                dialogInterface.dismiss();
            });
            dialog.show();
        }
        else if (action == SELECT_CITY_DDB) {
            dialog.setTitle("Select City");
            dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {


                Attribute attribute = new Attribute(ddbCityList.get(position).getCODE(), capsAllReplaceSpace(ddbCityList.get(position).getNAME()), districtList.get(selectedDistrict).getCODE());

                RequestManagementLambda request = new RequestManagementLambda(
                        sharedPrefsClient.getUserDetails().getUser().getUserName(),
                        "add-iot-city",
                        attribute
                );
                Log.i("add-iot-city", "showDialog: Request " + new Gson().toJson(request));

                ListIotStateDistrictCityResponseHandler callback = new ListIotStateDistrictCityResponseHandler() {
                    @Override
                    public void onSuccess(@NonNull JsonObject response) {
                        userAlertClient.closeWaitDialog();
                        int statusCode = response.get("statusCode").getAsInt();
                        String message = response.get("body").getAsString();

                        if (statusCode == 200) {
                            userAlertClient.showDialogMessage("Success", message, false);
                        } else {
                            userAlertClient.showDialogMessage("Error", message, false);
                        }
                    }

                    @Override
                    public void onError(@Nullable String message) {
                        userAlertClient.closeWaitDialog();
                        userAlertClient.showDialogMessage("Error", message, false);
                    }
                };

                userAlertClient.showWaitDialog("Adding City");

                lambdaClient.ExecuteManagementLambda(request, callback);


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

    private void fetchClientLogo() {
        JsonObject request = new JsonObject();
        request.addProperty("client", clientName.getText().toString());
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


    private void setTextWatcher(EditText editText) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                complexDetails.setValidName(false);
                complexDetails.setComplexName("");
                complexNameLayout.setHelperTextEnabled(false);
                complexNameLayout.setEndIconDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_availability, null));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        editText.addTextChangedListener(textWatcher);

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
        ((TextInputLayout) RegisterComplex.this.getView().findViewById(viewRef)).setErrorEnabled(false);
    }

    public static boolean validateInteger(String input) {
        if(input.matches("-?\\d+"))
            return true;
        else
            return false;
    }

    public  void createComplexUUID() {
        // First of all we have to get the list of complexes in the city
        String userName = sharedPrefsClient.getUserDetails().getUser().getUserName();
        ManagementLambdaRequest request = new ManagementLambdaRequest(userName, "list-iot-complex", cityList.get(selectedCity).getCODE());

        ListIotStateDistrictCityResponseHandler callback = new ListIotStateDistrictCityResponseHandler() {
            @Override
            public void onSuccess(@NonNull JsonObject response) {
                Log.i("listIotComplex", response.toString());
                int statusCode = response.get("statusCode").getAsInt();
                if(statusCode == 200) {
                    JsonArray complexNameArray = response.getAsJsonArray("body");
                    complexNameList = new ArrayList<>();
                    if(complexNameArray.size() > 0) {
                        for (int i = 0; i < complexNameArray.size(); i++) {
                            JsonObject tempObject = complexNameArray.get(i).getAsJsonObject();
                            complexNameList.add(tempObject.get("Name").getAsString());
                        }

                        ManagementLambdaRequest request1 = new ManagementLambdaRequest(userName, "list-iot-complexDetail", complexNameList.get(0));
                        lambdaClient.ExecuteManagementLambda(request1, handler);

                    }
                }
                else {
                    // Todo
                }
            }

            @Override
            public void onError(@Nullable String message) {
                userAlertClient.closeWaitDialog();
                if(message != null)
                    Log.i("listIotComplex", message);
            }
        };

        userAlertClient.showWaitDialog("Creating Complex UUID");
        lambdaClient.ExecuteManagementLambda(request, callback);

    }

    public void tempSetDetails() {
        address.setText("Test");

        maleWc.setText("2");
        femaleWc.setText("2");
        pdWc.setText("2");
        urinals.setText("2");
        urinalCabins.setText("2");
        bwt.setText("2");

        napkinIncineratorCount.setText("2");
        napkinIncineratorManufacturer.setText("N/A");
        napkinVmCount.setText("2");
        napkinVmManufacturer.setText("N/A");

        techProvider.setText("Sukriti");
        omPartner.setText("Sukriti");
        manufacturer.setText("Sukriti");
        civilPartner.setText("Sukriti");

        waterAtmCapacity.setText("0");
        supervisorRoomSize.setText("0");

        routerImeiET.setText("123456");
        routerMobileET.setText("123456");

        kioskArea.setText("N/A");

    }

    ListIotStateDistrictCityResponseHandler handler = new ListIotStateDistrictCityResponseHandler() {
        @Override
        public void onSuccess(@NonNull JsonObject response) {
            String userName = sharedPrefsClient.getUserDetails().getUser().getUserName();
            Log.i("complexDetailsLambda", response.toString());
            if(response.toString().contains(date.getText().toString())) {
                complexNum[0] = complexNum[0] + 1;
            }
            index[0] = index[0]+1;
            if(index[0] < complexNameList.size()) {
                ManagementLambdaRequest request2 = new ManagementLambdaRequest(userName, "list-iot-complexDetail", complexNameList.get(index[0]));
                lambdaClient.ExecuteManagementLambda(request2, handler);
            }
            else {
                Log.i("complexDetailsLambda", "Complex Number: " + complexNum[0]);
                userAlertClient.closeWaitDialog();
                complexDetails.setUuid(setUUID(complexNum[0]));
                createNewComplex();
            }
        }

        @Override
        public void onError(@Nullable String message) {
            userAlertClient.closeWaitDialog();
            if(message != null)
                Log.i("complexDetailsLambda", message);
        }
    };


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


    public String setUUID(int Count) {
        String strCount = formatted3DigitNumber(Count);
        UUID = complexDetails.getCityCode();
        UUID += "_" + complexDetails.getDate().replace("/", "");
        UUID += "_" + strCount;
        return UUID;
    }

    public  String capsAllReplaceSpace(String capString){
        capString = capString.toUpperCase();
        capString = capString.replace(" ","_");
        return capString;
    }

    public void newState() {
        userAlertClient.showWaitDialog("Getting states list");
        ManagementLambdaRequest request = new ManagementLambdaRequest(
                sharedPrefsClient.getUserDetails().getUser().getUserName(),
                "list-ddb-state"
        );

        viewModel.fetchListOf(request, dDbStatesListCallback, "list-ddb-state");
    }

    public void newDistrict() {
        if (!stateName.getText().toString().isEmpty()) {
            userAlertClient.showWaitDialog("Getting District list");
            ManagementLambdaRequest request = new ManagementLambdaRequest(
                    sharedPrefsClient.getUserDetails().getUser().getUserName(),
                    "list-ddb-district",
                    statesList.get(selectedState).getCODE()
            );

            viewModel.fetchListOf(request, dDbDistrictListCallback, "list-ddb-district");
        } else {
            stateLayout.setHelperTextEnabled(true);
            stateLayout.setHelperText(getString(R.string.selectState));
        }

    }

    public void newCity() {
        if (!districtName.getText().toString().isEmpty()) {
            userAlertClient.showWaitDialog("Getting Cities list");
/*                ManagementLambdaRequest request = new ManagementLambdaRequest(
                        sharedPrefsClient.getUserDetails().getUser().getUserName(),
                        "list-ddb-city",
                        districtList.get(selectedDistrict).getCODE()
                );*/

            RequestManagementLambda request = new RequestManagementLambda(
                    sharedPrefsClient.getUserDetails().getUser().getUserName(),
                    "list-ddb-city",
                    statesList.get(selectedState).getCODE(),
                    districtList.get(selectedDistrict).getCODE()
            );

            viewModel.fetchListOf(request, dDbCityListCallback, "list-ddb-city");
        }
        else {
            districtLayout.setHelperText(getString(R.string.selectDistrict));
            districtLayout.setHelperTextEnabled(true);
        }

    }

    public void verifyComplexName() {
        // Write the logic to verify if complex name is already taken or not?
        userAlertClient.showWaitDialog("Verifying complex name");
        complexNameLayout.setHelperTextEnabled(false);
        complexNameLayout.setEndIconDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_availability, null));
        String cpxName = complexName.getText().toString();
        ManagementLambdaRequest request = new ManagementLambdaRequest(
                sharedPrefsClient.getUserDetails().getUser().getUserName(),
                "List-iot-all-complex"
        );


        ListIotStateDistrictCityResponseHandler callback = new ListIotStateDistrictCityResponseHandler() {
            @Override
            public void onSuccess(@NonNull JsonObject response) {
                userAlertClient.closeWaitDialog();
                Log.i("listAllComplex", response.toString());
                int statusCode = response.get("statusCode").getAsInt();
                if(statusCode == 200) {
                    JsonObject object = response.getAsJsonObject("body");
                    JsonArray complexList = object.getAsJsonArray("complexList");

                    for(int i = 0; i < complexList.size(); i++) {
                        JsonObject complex = complexList.get(i).getAsJsonObject();
                        Log.i("listAllComplex", complex.toString());
                        String name = complex.get("name").getAsString();

                        if(cpxName.equals(name)) {
                            complexNameLayout.setHelperText(getString(R.string.complexNameTaken));
                            complexNameLayout.setHelperTextEnabled(true);
                            return;
                        }
                    }

                    complexNameLayout.setEndIconDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.checked, null));
                    complexDetails.setComplexName(complexName.getText().toString());
                    complexDetails.setValidName(true);
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
        };

        userAlertClient.showWaitDialog("Verifying complex name");
        lambdaClient.ExecuteManagementLambda(request, callback);

    }

}

