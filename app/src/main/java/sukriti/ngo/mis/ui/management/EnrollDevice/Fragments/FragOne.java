package sukriti.ngo.mis.ui.management.EnrollDevice.Fragments;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import sukriti.ngo.mis.databinding.FragmentOneBinding;
import sukriti.ngo.mis.databinding.HelperBinding;
import sukriti.ngo.mis.ui.management.EnrollDevice.Navigation.ViewPagerControl;
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ComplexDetails;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ThingDetails;
import sukriti.ngo.mis.ui.management.ManagementViewModel;
import sukriti.ngo.mis.ui.management.fargments.ComplexDetailsDialog;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListIotStateDistrictCityResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaRequest;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponse;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListPolicyLambdaResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ManagementLambdaRequest;
import sukriti.ngo.mis.utils.LambdaClient;
import sukriti.ngo.mis.utils.SharedPrefsClient;
import sukriti.ngo.mis.utils.UserAlertClient;

public class FragOne extends Fragment {
    private EnrollDeviceViewModel enrollViewModel;
    private ManagementViewModel managementViewModel;
    private LambdaClient lambdaClient;
    private static FragOne Instance;
    private HelperBinding binding;
    private UserAlertClient userAlertClient;
    private SharedPrefsClient sharedPrefsClient;
    private String userName;
    private final int[] cabinDetailsIndex = new int[1];
    private final String TAG = "Enrollment";
    private final String Debugging = "debugging";
    private final String showDiag = "showDialog";

    private ViewPagerControl interactionListener = null;

    private final int SELECT_STATE = 1;
    private final int SELECT_DISTRICT = 2;
    private final int SELECT_CITY = 3;
    private final int SELECT_COMPLEX = 4;

    public FragOne() {

    }

    public FragOne(EnrollDeviceViewModel enrollViewModel, ManagementViewModel managementViewModel, LambdaClient lambdaClient) {
        this.enrollViewModel = enrollViewModel;
        this.managementViewModel = managementViewModel;
        this.lambdaClient = lambdaClient;
    }

    public static FragOne getInstance(
            EnrollDeviceViewModel enrollViewModel,
            ManagementViewModel managementViewModel,
            LambdaClient lambdaClient
    ) {
        if (Instance == null) {
            Instance = new FragOne();
            Instance.setEnrollViewModel(enrollViewModel);
            Instance.setManagementViewModel(managementViewModel);
            Instance.setLambdaClient(lambdaClient);
        }

        return Instance;
    }

    public void setInteractionListener(ViewPagerControl listener) {
        this.interactionListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(Debugging, "onCreateView: FragOne");
        binding = HelperBinding.inflate(inflater);
        userAlertClient = new UserAlertClient(getActivity());
        sharedPrefsClient = new SharedPrefsClient(getContext());
        userName = sharedPrefsClient.getUserDetails().getUser().getUserName();

        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Debugging, "onCreate: FragOne");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: called for FragOne " + this);
        Log.i(Debugging, "onResume: called for FragOne " + this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(Debugging, "onViewCreated: FragOne");
        enrollViewModel.getIotStateListLiveData().observe(getViewLifecycleOwner(), iotStatesListObserver);

        enrollViewModel.getIotDistrictsListLiveData().observe(getViewLifecycleOwner(), iotDistrictsListObserver);

        enrollViewModel.getIotCitiesListLiveData().observe(getViewLifecycleOwner(), iotCitiesListObserver);

        enrollViewModel.getIotComplexesListLiveData().observe(getViewLifecycleOwner(), iotComplexesListObserver);

        enrollViewModel.getDataFetchCompleteLiveData().observe(getViewLifecycleOwner(), dataFetchCompleted);

        binding.selectStateTv.setOnClickListener(_view -> {
            // Call lambda from here
            // Handle callback from here
            Log.i(TAG, "onViewCreated: Fragment One: Select state button clicked");
            userAlertClient.showWaitDialog("Getting States...");
            enrollViewModel.fetchIotStatesList(new ListIotStateDistrictCityResponseHandler() {
                @Override
                public void onSuccess(@NonNull JsonObject response) {
                    // Handle success response
                    // We do not need to do anything here. The data changes will be tracked using live data
                }

                @Override
                public void onError(String message) {
                    userAlertClient.closeWaitDialog();
                    userAlertClient.showDialogMessage("Error", message, false);
                }
            });
        });

        binding.selectDistrictTv.setOnClickListener(_view -> {
            Log.i(TAG, "onViewCreated: Fragment One: Select district button clicked");

            if (!binding.selectStateTv.getText().toString().trim().isEmpty()) {

                userAlertClient.showWaitDialog("Getting Districts");

                enrollViewModel.fetchIotDistrictList(new ListIotStateDistrictCityResponseHandler() {
                    @Override
                    public void onSuccess(JsonObject response) {
                        // Handle success response
                    }

                    @Override
                    public void onError(String message) {
                        userAlertClient.closeWaitDialog();
                        userAlertClient.showDialogMessage("Error", message, false);
                    }
                });

            } else {
//                binding.selectStateError.setVisibility(View.VISIBLE);
                binding.selectStateLayout.setError("Select State");
            }
        });

        binding.selectCityTv.setOnClickListener(_view -> {
            Log.i(TAG, "onViewCreated: Fragment One: Select city button clicked");

            if (!binding.selectDistrictTv.getText().toString().trim().isEmpty()) {

                userAlertClient.showWaitDialog("Getting Cities");

                enrollViewModel.fetchIotCityList(new ListIotStateDistrictCityResponseHandler() {
                    @Override
                    public void onSuccess(JsonObject response) {
                        // Handle success response
                    }

                    @Override
                    public void onError(String message) {
                        userAlertClient.closeWaitDialog();
                        userAlertClient.showDialogMessage("Error", message, false);
                    }
                });

            } else {
//                binding.selectDistrictError.setVisibility(View.VISIBLE);
                binding.selectDistrictLayout.setError("Select District");
            }
        });

        binding.selectComplexTv.setOnClickListener(_view -> {
            Log.i(TAG, "onViewCreated: Fragment One: Select complex button clicked");

            if (!binding.selectCityTv.getText().toString().trim().isEmpty()) {
                userAlertClient.showWaitDialog("Getting Complexes");

                enrollViewModel.fetchIotComplexList(new ListIotStateDistrictCityResponseHandler() {
                    @Override
                    public void onSuccess(JsonObject response) {
                        // Handle success response
                    }

                    @Override
                    public void onError(String message) {
                        userAlertClient.closeWaitDialog();
                        userAlertClient.showDialogMessage("Error", message, false);
                    }
                });
            } else {
//                binding.selectCityError.setVisibility(View.VISIBLE);
                binding.selectCtiyLayout.setError("Select City");
            }
        });

        binding.creteNewComplexButton.setOnClickListener(_view -> {
            Log.i(TAG, "onViewCreated: Fragment One: create complex button clicked");

            userAlertClient.showWaitDialog("Wait...");
    /*val builder = AlertDialog.Builder(context, R.style.full_screen_dialog)
    val createComplexView = inflater.inflate(R.layout.create_new_complex, null)
    builder.setView(createComplexView)

    val dialog = builder.create()
    userAlertClient.closeWaitDialog()
    dialog.show()*/

            // setOnClickListenerForCreateNewComplex(createComplexView)

            RegisterComplex frag = new RegisterComplex();
            userAlertClient.closeWaitDialog();
            frag.show(getParentFragmentManager(), "RegisterComplex");
        });

        binding.next.setOnClickListener(listener -> {

            if (enrollViewModel.getStepOneCompleted()) {
                if (interactionListener != null) {

                    interactionListener.goToNextPage();

                } else {
                    Log.i("interactionListener", "it is null");
                }
            } else {
                userAlertClient.showDialogMessage("Complex not selected", "Please Select a complex", false);
            }

        });

    }

    public void setEnrollViewModel(EnrollDeviceViewModel enrollViewModel) {
        this.enrollViewModel = enrollViewModel;
    }

    public void setManagementViewModel(ManagementViewModel managementViewModel) {
        this.managementViewModel = managementViewModel;
    }

    public void setLambdaClient(LambdaClient lambdaClient) {
        this.lambdaClient = lambdaClient;
    }

    void fetchPolicies() {
        userAlertClient.closeWaitDialog();

        ListPolicyLambdaRequest request = new ListPolicyLambdaRequest(managementViewModel.getSelectedEnterprise().getName());

        ListPolicyLambdaResponseHandler callback = new ListPolicyLambdaResponseHandler() {

            @Override
            public void onSuccess(ListPolicyLambdaResponse response) {
                // userAlertClient.closeWaitDialog();
                if (response.getStatusCode() == 200) {
                    for (int i = 0; i < response.getBody().size(); i++) {
                        Log.i("Policies", "Policies: " + new Gson().toJson(response.getBody().get(i)));
                    }
                    enrollViewModel.setPolicyListItem(new ArrayList<>(response.getBody()));
                    setupAndShowComplexDetails(enrollViewModel.getComplexDetails());
                } else {
                    userAlertClient.closeWaitDialog();
                    Log.e("listPolicies", "onSuccess: error in policies lambda response");
                    setupAndShowComplexDetails(enrollViewModel.getComplexDetails());
                }
            }

            @Override
            public void onError(String message) {
                userAlertClient.closeWaitDialog();
                userAlertClient.showDialogMessage("Error", message, false);
            }
        };

        userAlertClient.showWaitDialog("Getting policies data");
        lambdaClient.ExecuteListPolicyLambda(request, callback);
    }

    void fetchThingDetails() {
        if (cabinDetailsIndex[0] < enrollViewModel.getThingsList().size()) {
            if (cabinDetailsIndex[0] == 0) {
                enrollViewModel.getThingDetailsList().clear();
            }
            ManagementLambdaRequest request = new ManagementLambdaRequest(
                    userName,
                    "describe-iot-thing",
                    enrollViewModel.getThingsList().get(cabinDetailsIndex[0])
            );
            lambdaClient.ExecuteIotCabinLambda(request, cabinDetailsHandler);
        }
    }

    private final ListIotStateDistrictCityResponseHandler cabinListHandler = new ListIotStateDistrictCityResponseHandler() {
        @Override
        public void onSuccess(JsonObject response) {
            enrollViewModel.getThingsList().clear();
            JsonObject body = response.get("body").getAsJsonObject();
            JsonArray thingsArray = body.get("things").getAsJsonArray();
            for (int i = 0; i < thingsArray.size(); i++) {
                String thing = thingsArray.get(i).getAsString();
                Log.i("thingsList", i + " -> " + thing);
                enrollViewModel.getThingsList().add(thing);
            }

            if (thingsArray.size() > 0) {
                fetchThingDetails();
            } else {
                userAlertClient.closeWaitDialog();
                fetchPolicies();
            }
        }

        @Override
        public void onError(String message) {
            userAlertClient.closeWaitDialog();
            userAlertClient.showDialogMessage("Error", message, false);
        }
    };

    private final ListIotStateDistrictCityResponseHandler cabinDetailsHandler = new ListIotStateDistrictCityResponseHandler() {
        @Override
        public void onSuccess(JsonObject response) {
            Log.i("CabinDetails", "Details -: " + new Gson().toJson(response));
            int statusCode = response.get("statusCode").getAsInt();
            if (statusCode == 200) {
                ThingDetails details = new ThingDetails();
                details.setAttributesMap(new HashMap<>());
                JsonObject body = response.get("body").getAsJsonObject();
                details.setDefaultClientId(body.get("defaultClientId").getAsString());
                Log.i("CabinDetails", "Default client ID " + details.getDefaultClientId());
                details.setName(body.get("thingName").getAsString());
                Log.i("CabinDetails", "Thing Name " + details.getName());
                details.setId(body.get("thingId").getAsString());
                details.setArn(body.get("thingArn").getAsString());
                details.setThingType(body.get("thingTypeName").getAsString());
                details.setVersion(body.get("version").getAsLong());

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode;
                try {
                    jsonNode = objectMapper.readTree(response.toString());
                    JsonNode attributeNode = jsonNode.get("body").get("attributes");
                    if (!attributeNode.isEmpty()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            attributeNode.fields().forEachRemaining(entry -> details.getAttributesMap().put(entry.getKey(), entry.getValue().asText()));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                enrollViewModel.getThingDetailsList().add(details);

                cabinDetailsIndex[0] = cabinDetailsIndex[0] + 1;

                if (cabinDetailsIndex[0] < enrollViewModel.getThingsList().size()) {
                    fetchThingDetails();
                } else {
                    fetchPolicies();
                }
            } else {
                cabinDetailsIndex[0] = cabinDetailsIndex[0] + 1;
                fetchThingDetails();
            }
        }

        @Override
        public void onError(String message) {
            // Handle error
        }
    };

    private void parseResponse(JsonObject response) {
        Log.d(TAG, "Fragment One: parseResponse() called with: response = " + response);
        ComplexDetails complexDetails = new ComplexDetails();

        for (Map.Entry<String, JsonElement> entry : response.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            Log.i("mapping", "Key " + key + " : Value " + value);
            String stringValue = value.getAsString();
            // .substring(1, value.getAsString().length() - 1);

            switch (key) {
                case "STATE_NAME":
                    complexDetails.setStateName(stringValue);
                    break;
                case "DISTRICT_NAME":
                    complexDetails.setDistrictName(stringValue);
                    break;
                case "CITY_NAME":
                    complexDetails.setCityName(stringValue);
                    break;
                case "STATE_CODE":
                    complexDetails.setStateCode(stringValue);
                    break;
                case "DISTRICT_CODE":
                    complexDetails.setDistrictCode(stringValue);
                    break;
                case "CITY_CODE":
                    complexDetails.setCityCode(stringValue);
                    break;
                case "LATT":
                    complexDetails.setLatitude(stringValue);
                    break;
                case "LONG":
                    complexDetails.setLongitude(stringValue);
                    break;
                case "CLNT":
                    complexDetails.setClient(stringValue);
                    break;
                case "BILL":
                    complexDetails.setBillingGroup(stringValue);
                    break;
                case "DATE":
                    complexDetails.setDate(stringValue);
                    break;
                case "DEVT":
                    complexDetails.setDeviceType(stringValue);
                    break;
                case "SLVL":
                    complexDetails.setSmartness(stringValue);
                    break;
                case "QMWC":
                    complexDetails.setWCCountMale(stringValue);
                    break;
                case "QFWC":
                    complexDetails.setWCCountFemale(stringValue);
                    break;
                case "QPWC":
                    complexDetails.setWCCountPD(stringValue);
                    break;
                case "QURI":
                    complexDetails.setUrinals(stringValue);
                    break;
                case "QURC":
                    complexDetails.setUrinalCabins(stringValue);
                    break;
                case "QBWT":
                    complexDetails.setBwt(stringValue);
                    break;
                case "COCO":
                    complexDetails.setCommissioningStatus(stringValue);
                    break;
                case "QSNV":
                    complexDetails.setNapkinVmCount(stringValue);
                    break;
                case "QSNI":
                    complexDetails.setNapkinIncineratorCount(stringValue);
                    break;
                case "MSNI":
                    complexDetails.setNapkinIncineratorManufacturer(stringValue);
                    break;
                case "MSNV":
                    complexDetails.setNapkinVmManufacturer(stringValue);
                    break;
                case "AR_K":
                    complexDetails.setKioskArea(stringValue);
                    break;
                case "CWTM":
                    complexDetails.setWaterAtmCapacity(stringValue);
                    break;
                case "ARSR":
                    complexDetails.setSupervisorRoomSize(stringValue);
                    break;
                case "MANU":
                    complexDetails.setManufacturer(stringValue);
                    break;
                case "CIVL":
                    complexDetails.setCivilPartner(stringValue);
                    break;
                case "TECH":
                    complexDetails.setTechProvider(stringValue);
                    break;
                case "ONMP":
                    complexDetails.setOMPartner(stringValue);
                    break;
                case "UUID":
                    complexDetails.setUuid(stringValue);
                    break;
                case "ROUTER_MOBILE":
                    complexDetails.setRouterMobile(stringValue);
                    break;
                case "ROUTER_IMEI":
                    complexDetails.setRouterImei(stringValue);
                    break;
                case "MODIFIED_BY":
                    complexDetails.setModifiedBy(stringValue);
                    break;
                case "THINGGROUPTYPE":
                    complexDetails.setThingGroupType(stringValue);
                    break;
                case "ADDR":
                    complexDetails.setAddress(stringValue);
                    break;
            }
        }

        complexDetails.setComplexName(enrollViewModel.getIotComplexesList()[enrollViewModel.getSelectedComplex()]);

        enrollViewModel.setComplexDetails(complexDetails);

        ManagementLambdaRequest request = new ManagementLambdaRequest(
                userName,
                "list-iot-thing",
                enrollViewModel.getComplexDetails().getComplexName()
        );
        lambdaClient.ExecuteIotCabinLambda(request, cabinListHandler);

        Log.i("dialog", new Gson().toJson(complexDetails));
        // setupAndShowComplexDetails(complexDetails);
    }

    private void showDialog(int action, String[] list) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        Log.i(showDiag, "showDialog: " + this + " action: " + action + " list = " + Arrays.toString(list));
        Log.d(TAG, "Fragment One: showDialog() called with: action = " + action + ", list = " + Arrays.toString(list));

        switch (action) {
            case SELECT_STATE:
                dialog.setTitle("Select State");
                dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {
                    binding.selectStateTv.setText(list[position]);

/*
                    binding.selectDistrictTv.setText("");
                    binding.selectCityTv.setText("");
                    binding.selectComplexTv.setText("");

                    binding.selectStateError.setVisibility(View.GONE);
                    binding.selectDistrictError.setVisibility(View.GONE);
                    binding.selectCityError.setVisibility(View.GONE);
*/

                    enrollViewModel.setSelectedState(position);
                    enrollViewModel.setSelectedDistrict(-1);
                    enrollViewModel.setSelectedCity(-1);
                    enrollViewModel.setSelectedComplex(-1);

                    enrollViewModel.getThingsList().clear();
                    enrollViewModel.getThingDetailsList().clear();
                    cabinDetailsIndex[0] = 0;
                    dialogInterface.dismiss();
                });
                dialog.show();
                break;

            case SELECT_DISTRICT:
                dialog.setTitle("Select District");
                if (!binding.selectStateTv.getText().toString().trim().isEmpty()) {
                    dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {
                        binding.selectDistrictTv.setText(list[position]);
/*                        binding.selectCity.setText("");
                        binding.selectComplex.setText("");

                        binding.selectDistrictError.setVisibility(View.GONE);
                        binding.selectCityError.setVisibility(View.GONE);*/

                        enrollViewModel.setSelectedDistrict(position);
                        enrollViewModel.setSelectedCity(-1);
                        enrollViewModel.setSelectedComplex(-1);
                        enrollViewModel.getThingsList().clear();
                        enrollViewModel.getThingDetailsList().clear();
                        cabinDetailsIndex[0] = 0;
                        dialogInterface.dismiss();
                    });
                    dialog.show();
                } else {
//                    binding.selectStateError.setVisibility(View.VISIBLE);
                    binding.selectStateLayout.setError("Select State");
                }
                break;

            case SELECT_CITY:
                dialog.setTitle("Select City");
                if (!binding.selectDistrictTv.getText().toString().trim().isEmpty()) {
                    dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {
                        binding.selectCityTv.setText(list[position]);
//                        binding.selectComplex.setText("");

//                        binding.selectCityError.setVisibility(View.GONE);

                        enrollViewModel.setSelectedCity(position);
                        enrollViewModel.getThingsList().clear();
                        enrollViewModel.getThingDetailsList().clear();
                        cabinDetailsIndex[0] = 0;

                        dialogInterface.dismiss();
                    });
                    dialog.show();
                } else {
//                    binding.selectDistrictError.setVisibility(View.VISIBLE);
                    binding.selectDistrictLayout.setError("Select District");
                }
                break;

            case SELECT_COMPLEX:
                dialog.setTitle("Select Complex");
                if (!binding.selectCityTv.getText().toString().trim().isEmpty()) {
                    dialog.setSingleChoiceItems(list, -1, (dialogInterface, position) -> {
                        binding.selectComplexTv.setText(list[position]);

//                        enrollViewModel.setSelectedComplex(position);
                        cabinDetailsIndex[0] = 0;

                        userAlertClient.showWaitDialog("Getting data. Please wait.");

/*                        ListIotStateDistrictCityResponseHandler callback = new ListIotStateDistrictCityResponseHandler() {
                            @Override
                            public void onSuccess(JsonObject response) {
                                Log.i("complexDetails", response.toString());

                                Gson gson = new Gson();
                                JsonObject jsonObject = gson.fromJson(response.get("body"), JsonObject.class);
                                Log.i("complexDetails", jsonObject.toString());

                                parseResponse(jsonObject);
                            }

                            @Override
                            public void onError(String message) {
                                if (message != null) {
                                    Log.i("complexDetails", message);
                                }
                            }
                        };

                        ManagementLambdaRequest request = new ManagementLambdaRequest(
                                userName,
                                "list-iot-complexDetail",
                                enrollViewModel.getIotComplexesList()[position]
                        );

                        lambdaClient.ExecuteManagementLambda(request, callback);*/
                        enrollViewModel.fetchComplexDetails(managementViewModel.getSelectedEnterprise().getName(), position);
                        dialogInterface.dismiss();
                    });
                    dialog.show();
                } else {
//                    binding.selectCityError.setVisibility(View.VISIBLE);
                    binding.selectCtiyLayout.setError("Select City");
                }
                break;

        }
    }


    private void setupAndShowComplexDetails(ComplexDetails complexDetails) {
        Log.d(TAG, "Fragment One: setupAndShowComplexDetails() called with: complexDetails = " + complexDetails);

        ComplexDetailsDialog frag = new ComplexDetailsDialog();
        frag.setRetrievedComplexDetails(enrollViewModel.getComplexDetails());
        frag.setManagementViewModel(managementViewModel);
        frag.setCancelable(false);
        userAlertClient.closeWaitDialog();
        frag.show(getParentFragmentManager(), "ComplexDetails");
    }


    private final Observer<String[]> iotStatesListObserver = strings -> {
        Log.d(TAG, "iotStatesListObserver: " + Arrays.toString(strings));
        Log.i(showDiag, "iot states observer called with : " + this + " and with data " + Arrays.toString(strings));
        if (strings != null) {
            userAlertClient.closeWaitDialog();
            Log.d(TAG, "calling show dialog for states");
            showDialog(SELECT_STATE, strings);
        }
    };

    private final Observer<String[]> iotDistrictsListObserver = strings -> {
        Log.d(TAG, "iotDistrictsListObserver: " + Arrays.toString(strings));
        if (strings != null) {
            userAlertClient.closeWaitDialog();
            Log.d(TAG, "calling show dialog for districts");
            showDialog(SELECT_DISTRICT, strings);
        }
    };

    private final Observer<String[]> iotCitiesListObserver = strings -> {
        Log.d(TAG, "iotCitiesListObserver: " + Arrays.toString(strings));
        if (strings != null) {
            userAlertClient.closeWaitDialog();
            Log.d(TAG, "calling show dialog for cities");
            showDialog(SELECT_CITY, strings);
        }
    };

    private final Observer<String[]> iotComplexesListObserver = strings -> {
        Log.d(TAG, "iotComplexesListObserver: " + Arrays.toString(strings));
        if (strings != null) {
            userAlertClient.closeWaitDialog();
            Log.d(TAG, "calling show dialog for complexes");
            showDialog(SELECT_COMPLEX, strings);
        }
    };

    private final Observer<Boolean> dataFetchCompleted = value -> {
        Log.i("dataFetch", "Data fetch live data value changed: " + value);

        if(value != null) {
            if (!value) {
                userAlertClient.closeWaitDialog();
                userAlertClient.showDialogMessage("Error", "Something went wrong, please close and try again.", false);
            } else {
                userAlertClient.closeWaitDialog();
                setupAndShowComplexDetails(enrollViewModel.getComplexDetails());
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        Log.i(Debugging, "onStart: FragOne");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(Debugging, "onPause: FragOne ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(Debugging, "onStop: FragOne ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(Debugging, "onDestroyView: FragOne ");
        enrollViewModel.getIotStateListLiveData().removeObserver(iotStatesListObserver);
        enrollViewModel.getIotDistrictsListLiveData().removeObserver(iotDistrictsListObserver);
        enrollViewModel.getIotCitiesListLiveData().removeObserver(iotCitiesListObserver);
        enrollViewModel.getIotComplexesListLiveData().removeObserver(iotComplexesListObserver);
        enrollViewModel.getDataFetchCompleteLiveData().removeObserver(dataFetchCompleted);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.i(Debugging, "onDestroy: FragOne ");
    }

    public boolean checkComplexSelected() {
        return !enrollViewModel.getComplexDetails().getComplexName().isEmpty();
    }
}
