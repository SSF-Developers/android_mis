package sukriti.ngo.mis.ui.management.fargments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.databinding.CreateNewPolicyBinding;
import sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.AddNewApplicationDialog;
import sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.KioskCustomizationDialog;
import sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.WifiDialog;
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.DeviceConnectivityManagement;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.KioskCustomization;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.WifiSsid;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.WifiSsidPolicy;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.ClickCallback;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.DeleteAppCallback;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.EditAppClickCallback;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.SubmitApplication;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.SubmitKioskCustomization;
import sukriti.ngo.mis.ui.management.ManagementViewModel;
import sukriti.ngo.mis.ui.management.adapters.ApplicationListAdapter;
import sukriti.ngo.mis.ui.management.adapters.WifiListAdapter;
import sukriti.ngo.mis.ui.management.data.device.Application;
import sukriti.ngo.mis.ui.management.lambda.CreateNewPolicy.CreatePolicyResponseHandler;
import sukriti.ngo.mis.utils.LambdaClient;
import sukriti.ngo.mis.utils.UserAlertClient;

public class CreateNewPolicyDialog extends DialogFragment {
    private CreateNewPolicyBinding binding;

    EnrollDeviceViewModel enrollDeviceViewModel;
    ManagementViewModel managementViewModel;
    ArrayList<Application> applicationArrayList = new ArrayList<>();
    ArrayList<String> wifiSsidArrayList = new ArrayList<>();
    KioskCustomization kioskCustomization;
    LambdaClient lambdaClient;
    UserAlertClient userAlertClient;
    ApplicationListAdapter adapter;
    WifiListAdapter wifiListAdapter;
    String kioskAppPackageName = "";
    int kioskAppPositionInList = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CreateNewPolicyBinding.inflate(inflater);
        lambdaClient = new LambdaClient(getContext());
        userAlertClient = new UserAlertClient(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addNewApplication.setOnClickListener(newApp -> {
            showAddAppDialog();
        });

        binding.createNewPolicyButton.setOnClickListener(click -> {
            try {
                if (verifyDetails() && kioskAppSelected()) {
                    createPolicy();
                }
            } catch (JSONException exception) {
                Log.e("createNewPolicy", "onViewCreated: Error: " + exception.getMessage());
            }
        });

        setDefaultValue();
/*
        binding.kioskCustomizationEnabledContainer.setOnClickListener(kiosk -> {
            KioskCustomizationDialog dialog = new KioskCustomizationDialog(getContext(), kioskCallback);
            dialog.setCancelable(false);
            dialog.show(getChildFragmentManager(), "Kiosk Customization Dialog");
        });
*/


/*        binding.kioskCustomizationEnabledValue.setOnCheckedChangeListener((compoundButton, checked) -> {
            if(checked) {
                KioskCustomizationDialog dialog = new KioskCustomizationDialog(getContext(), kioskCallback);
                dialog.setCancelable(false);
                dialog.show(getChildFragmentManager(), "Kiosk Customization Dialog");
//                    binding.kioskCustomizationHeadingTv.setVisibility(View.VISIBLE);
//                    binding.kioskCustomizationContainer.setVisibility(View.VISIBLE);
            }
            else {
                binding.kioskCustomizationHeadingTv.setVisibility(View.GONE);
                binding.kioskCustomizationContainer.setVisibility(View.GONE);
            }
        });*/

/*
        binding.kioskCustomizationContainer.setOnClickListener(view1 -> {
            KioskCustomizationDialog dialog = new KioskCustomizationDialog(requireContext(), kioskCallback);
            dialog.setCancelable(false);
            dialog.show(getChildFragmentManager(), "Kiosk Customization Dialog");
        });
*/

        binding.chooseKioskAppButton.setOnClickListener(v -> {
            showKioskAppSelection(kioskAppPositionInList);
        });

        binding.addNewWifi.setOnClickListener(v -> {
            showAddWifiDialog();
        });

        setWifiSsidListAdapter();
    }

    private void setDefaultValue() {
        binding.cameraEnabledValue.setChecked(false);
        binding.addUserDisabledValue.setChecked(true);
        binding.removeUserDisabledValue.setChecked(true);
        binding.factoryResetDisabledValue.setChecked(true);
        binding.mountPhysicalMediaDisabledValue.setChecked(true);
        binding.safeBootDisabledValue.setChecked(true);
        binding.uninstallAppsDisabledValue.setChecked(true);
        binding.bluetoothConfigDisabledValue.setChecked(false);
        binding.vpnConfigDisabledValue.setChecked(true);
        binding.networkResetDisabledValue.setChecked(true);
        binding.smsDisabledValue.setChecked(true);
        binding.modifyAccountsDisabledValue.setChecked(true);
        binding.outgoingCallsDisabledValue.setChecked(true);


        String[] powerButtonAction = getResources().getStringArray(R.array.powerButtonAction);
        String[] systemErrorWarnings = getResources().getStringArray(R.array.SystemErrorWarnings);
        String[] systemNavigation = getResources().getStringArray(R.array.SystemNavigation);
        String[] statusBar = getResources().getStringArray(R.array.StatusBar);
        String[] deviceSettings = getResources().getStringArray(R.array.DeviceSettings);
        String[] wifiPolicyType = getResources().getStringArray(R.array.wifiSsidPolicyType);

        binding.powerButtonActionTv.setText(powerButtonAction[1]);
        binding.systemErrorWarningTv.setText(systemErrorWarnings[1]);
        binding.systemNavigationTv.setText(systemNavigation[1]);
        binding.statusBarTv.setText(statusBar[1]);
        binding.deviceSettingsLayoutTv.setText(deviceSettings[1]);
        binding.wifiPolicyTypeTv.setText(wifiPolicyType[2]);

    }

    private void showAddAppDialog() {
        AddNewApplicationDialog dialog = new AddNewApplicationDialog(requireContext(), callback, lambdaClient, managementViewModel, null, -1);
        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "Add New Application");
    }
    private void showAddWifiDialog() {
        WifiDialog dialog = new WifiDialog(submitWifi);
        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "Add New Wifi");
    }

    private void showEditAppDialog(Application app, int index) {
        AddNewApplicationDialog dialog = new AddNewApplicationDialog(requireContext(), callback, lambdaClient, managementViewModel, app, index);
        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "Add New Application");
    }

    private boolean verifyDetails() {
        boolean isPolicyNameNotEmpty = true;
        boolean isKioskCustomizationNotEmpty = true;
        boolean isApplicationListNotEmpty = true;

        boolean powerButton;
        boolean systemError;
        boolean systemNavigation;
        boolean statusBar;
        boolean deviceSettings;
        boolean isWifiPolicyType;
        boolean isWifiListEmpty = false;
        boolean shouldConsiderWifiEmptyList = false;

        if (binding.powerButtonActionTv.getText().toString().isEmpty()) {
            binding.powerButtonActionLayout.setErrorEnabled(true);
            binding.powerButtonActionLayout.setError("Field required");
            powerButton = false;
        } else {
            binding.powerButtonActionLayout.setErrorEnabled(false);
            powerButton = true;
        }

        if (binding.systemErrorWarningTv.getText().toString().isEmpty()) {
            binding.systemErrorWarningLayout.setErrorEnabled(true);
            binding.systemErrorWarningLayout.setError("Field required");
            systemError = false;
        } else {
            binding.systemErrorWarningLayout.setErrorEnabled(false);
            systemError = true;
        }

        if (binding.systemNavigationTv.getText().toString().isEmpty()) {
            binding.systemNavigationLayout.setErrorEnabled(true);
            binding.systemNavigationLayout.setError("Field required");
            systemNavigation = false;
        } else {
            binding.systemNavigationLayout.setErrorEnabled(false);
            systemNavigation = true;
        }

        if (binding.statusBarTv.getText().toString().isEmpty()) {
            binding.statusBarLayout.setErrorEnabled(true);
            binding.statusBarLayout.setError("Field required");
            statusBar = false;
        } else {
            binding.statusBarLayout.setErrorEnabled(false);
            statusBar = true;
        }

        if (binding.deviceSettingsLayoutTv.getText().toString().isEmpty()) {
            binding.deviceSettingsLayout.setErrorEnabled(true);
            binding.deviceSettingsLayout.setError("Field required");
            deviceSettings = false;
        } else {
            binding.deviceSettingsLayout.setErrorEnabled(false);
            deviceSettings = true;
        }

        if (binding.policyNameEditText.getText().toString().isEmpty()) {
            binding.policyNameLayout.setError("Policy Name required");
            isPolicyNameNotEmpty = false;
        } else {
            binding.policyNameLayout.setErrorEnabled(false);
        }

        if(binding.wifiPolicyTypeTv.getText().toString().isEmpty()) {
            binding.wifiPolicyTypeLayout.setErrorEnabled(false);
            binding.wifiPolicyTypeLayout.setError("Wifi Policy Type required");
            isWifiPolicyType = false;
        }
        else {
            binding.wifiPolicyTypeLayout.setErrorEnabled(false);
            isWifiPolicyType = true;

            if(binding.wifiPolicyTypeTv.getText().toString().equals("WIFI_SSID_ALLOWLIST")) {
                shouldConsiderWifiEmptyList = true;
                if(wifiSsidArrayList.isEmpty()) {
                    binding.wifiError.setVisibility(View.VISIBLE);
                    isWifiListEmpty = true;
                }
                else {
                    binding.wifiError.setVisibility(View.GONE);
                }
            }
        }



/*
        if(kioskCustomization == null) {
            binding.kioskCustomizationErrorTv.setVisibility(View.VISIBLE);
            isKioskCustomizationNotEmpty = false;
        } else {
            binding.kioskCustomizationErrorTv.setVisibility(View.GONE);
        }
*/

        if(applicationArrayList.isEmpty()) {
            binding.appListEmptyErrorTv.setVisibility(View.VISIBLE);
            isApplicationListNotEmpty = false;
        } else {
            binding.appListEmptyErrorTv.setVisibility(View.GONE);
        }

        if(shouldConsiderWifiEmptyList) {
            /*&& isKioskCustomizationNotEmpty*/
            return isPolicyNameNotEmpty && isApplicationListNotEmpty && powerButton && systemError && systemNavigation && statusBar && deviceSettings && !isWifiListEmpty;
        }
        else {
            return isPolicyNameNotEmpty /*&& isKioskCustomizationNotEmpty*/ && isApplicationListNotEmpty && powerButton && systemError && systemNavigation && statusBar && deviceSettings && isWifiPolicyType;
        }
    }

    private boolean kioskAppSelected() {
        if (!applicationArrayList.isEmpty()) {
            if (kioskAppPositionInList == -1) {
                binding.kioskAppErrorTv.setVisibility(View.VISIBLE);
                return false;
            } else {
                binding.kioskAppErrorTv.setVisibility(View.GONE);
                return true;
            }
        } else {
            binding.kioskAppErrorTv.setVisibility(View.GONE);
            return true;
        }
    }

/*
    private final SubmitKioskCustomization kioskCallback = kioskCustomization -> {
        this.kioskCustomization = kioskCustomization;
        binding.powerActionButtonValue.setText(kioskCustomization.getPowerButtonActions());
        binding.systemErrorWarningValue.setText(kioskCustomization.getSystemErrorWarnings());
        binding.systemNavigationValue.setText(kioskCustomization.getSystemNavigation());
        binding.statusBarValue.setText(kioskCustomization.getStatusBar());
        binding.deviceSettingsValue.setText(kioskCustomization.getDeviceSettings());
        binding.kioskCustomizationHeadingTv.setVisibility(View.VISIBLE);
        binding.kioskCustomizationContainer.setVisibility(View.VISIBLE);

    };
*/

    @Override
    public void onStart() {
        super.onStart();
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setLayout(width, height);
    }

    @Override
    public void onResume() {
        super.onResume();
        String[] powerButtonAction = getResources().getStringArray(R.array.powerButtonAction);
        String[] systemErrorWarnings = getResources().getStringArray(R.array.SystemErrorWarnings);
        String[] systemNavigation = getResources().getStringArray(R.array.SystemNavigation);
        String[] statusBar = getResources().getStringArray(R.array.StatusBar);
        String[] deviceSettings = getResources().getStringArray(R.array.DeviceSettings);
        String[] wifiPolicyType = getResources().getStringArray(R.array.wifiSsidPolicyType);

        ArrayAdapter<String> powerButtonAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown, powerButtonAction);
        ArrayAdapter<String> systemErrorWarningsAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown, systemErrorWarnings);
        ArrayAdapter<String> systemNavigationAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown, systemNavigation);
        ArrayAdapter<String> statusBarAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown, statusBar);
        ArrayAdapter<String> deviceSettingsAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown, deviceSettings);
        ArrayAdapter<String> wifiPolicyTypeAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown, wifiPolicyType);

        binding.powerButtonActionTv.setAdapter(powerButtonAdapter);
        binding.systemErrorWarningTv.setAdapter(systemErrorWarningsAdapter);
        binding.systemNavigationTv.setAdapter(systemNavigationAdapter);
        binding.statusBarTv.setAdapter(statusBarAdapter);
        binding.deviceSettingsLayoutTv.setAdapter(deviceSettingsAdapter);
        binding.wifiPolicyTypeTv.setAdapter(wifiPolicyTypeAdapter);

    }

    private final SubmitApplication callback = new SubmitApplication() {
        @Override
        public void submitApp(@NonNull Application app) {
            applicationArrayList.add(app);
            setApplicationAdapter();
            binding.chooseKioskAppButton.setEnabled(!applicationArrayList.isEmpty());

        }

        @Override
        public void editApp(@NonNull Application app, int index) {
            applicationArrayList.remove(index);
            applicationArrayList.add(index, app);
            if(index == kioskAppPositionInList) {
                kioskAppPackageName = app.getPackageName();
            }
            setApplicationAdapter();
        }
    };

    private final EditAppClickCallback editApp = new EditAppClickCallback() {
        @Override
        public void edit(@NonNull Application app, int index) {

        }
    };

    public void setWifiSsidListAdapter() {
        wifiListAdapter = new WifiListAdapter(wifiSsidArrayList);
        binding.wifiSsidListRecyclerView.setAdapter(wifiListAdapter);
        binding.wifiSsidListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void setApplicationAdapter() {
        adapter = new ApplicationListAdapter();
        adapter.setList(applicationArrayList);
        adapter.setClickCallback(applicationListItemClickCallback);
        adapter.setDeleteCallback(deleteAppCallback);
        adapter.setRadioButtonSelectionCallback(kioskAppSelectionCallback);
        adapter.setEditAppClickCallback(editAppClickCallback);
        binding.applicationsListRecyclerView.setAdapter(adapter);
        binding.applicationsListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeDelete);
        itemTouchHelper.attachToRecyclerView(binding.applicationsListRecyclerView);
    }

    private final DeleteAppCallback deleteAppCallback = new DeleteAppCallback() {
        @Override
        public void deleteApp(int position) {
            applicationArrayList.remove(position);
            Log.i("KioskSelection", "Delete position: " + position);
            if (position == kioskAppPositionInList) {
                kioskAppPositionInList = -1;
                kioskAppPackageName = "";
            }
            adapter.notifyItemRemoved(position);
            binding.chooseKioskAppButton.setEnabled(!applicationArrayList.isEmpty());
        }
    };

    public EnrollDeviceViewModel getEnrollDeviceViewModel() {
        return enrollDeviceViewModel;
    }

    public void setEnrollDeviceViewModel(EnrollDeviceViewModel enrollDeviceViewModel) {
        this.enrollDeviceViewModel = enrollDeviceViewModel;
    }

    public ManagementViewModel getManagementViewModel() {
        return managementViewModel;
    }

    public void setManagementViewModel(ManagementViewModel managementViewModel) {
        this.managementViewModel = managementViewModel;
    }

    public void createPolicy() throws JSONException {
        JsonObject policy = new JsonObject();
        policy.addProperty("enterprises_id", managementViewModel.getSelectedEnterprise().getName());
        String policyName = "policies/" + binding.policyNameEditText.getText().toString();
        policy.addProperty("policy_name", policyName);

        JsonObject fieldToPatch = new JsonObject();

        fieldToPatch.addProperty("cameraDisabled", binding.cameraEnabledValue.isChecked());
        fieldToPatch.addProperty("addUserDisabled", binding.addUserDisabledValue.isChecked());
        fieldToPatch.addProperty("removeUserDisabled", binding.removeUserDisabledValue.isChecked());
        fieldToPatch.addProperty("factoryResetDisabled", binding.factoryResetDisabledValue.isChecked());
        fieldToPatch.addProperty("mountPhysicalMediaDisabled", binding.mountPhysicalMediaDisabledValue.isChecked());
        fieldToPatch.addProperty("safeBootDisabled", binding.safeBootDisabledValue.isChecked());
        fieldToPatch.addProperty("uninstallAppsDisabled", binding.uninstallAppsDisabledValue.isChecked());
        fieldToPatch.addProperty("bluetoothConfigDisabled", binding.bluetoothConfigDisabledValue.isChecked());
        fieldToPatch.addProperty("vpnConfigDisabled", binding.vpnConfigDisabledValue.isChecked());
        fieldToPatch.addProperty("networkResetDisabled", binding.networkResetDisabledValue.isChecked());
        fieldToPatch.addProperty("smsDisabled", binding.smsDisabledValue.isChecked());
        fieldToPatch.addProperty("modifyAccountsDisabled", binding.modifyAccountsDisabledValue.isChecked());
        fieldToPatch.addProperty("outgoingCallsDisabled", binding.outgoingCallsDisabledValue.isChecked());
//        fieldToPatch.addProperty("kioskCustomLauncherEnabled", binding.kioskCustomizationEnabledValue.isChecked());
        fieldToPatch.addProperty("kioskPackage", kioskAppPackageName);

        JsonArray applicationsJsonArray = new JsonArray();
        for (int i = 0; i < applicationArrayList.size(); i++) {
            JsonObject application = new JsonObject();
            Application app = applicationArrayList.get(i);
            Log.i("createNewApp", new Gson().toJson(app));
            application.addProperty("packageName", app.getPackageName());
            application.addProperty("installType", app.getInstallType());
            application.addProperty("defaultPermissionPolicy", app.getDefaultPermissionPolicy());
            application.addProperty("autoUpdateMode", app.getAutoUpdateMode());
            application.addProperty("userControlSettings", app.getUserControlSettings());

            applicationsJsonArray.add(application);
        }

        fieldToPatch.add("applications", applicationsJsonArray);

        JsonObject kiosk = new JsonObject();
        kiosk.addProperty("powerButtonActions", binding.powerButtonActionTv.getText().toString());
        kiosk.addProperty("systemErrorWarnings", binding.systemErrorWarningTv.getText().toString());
        kiosk.addProperty("systemNavigation", binding.systemNavigationTv.getText().toString());
        kiosk.addProperty("statusBar", binding.statusBarTv.getText().toString());
        kiosk.addProperty("deviceSettings", binding.deviceSettingsLayoutTv.getText().toString());
        fieldToPatch.add("kioskCustomization", kiosk);

/*
        WifiSsidPolicy ssidPolicy = new WifiSsidPolicy();
        ssidPolicy.setWifiSsidPolicyType(binding.wifiPolicyTypeTv.getText().toString());
//        ArrayList<WifiSsid> list = new ArrayList<>();
        WifiSsid[] list = new WifiSsid[wifiSsidArrayList.size()];
        for(int i = 0; i < wifiSsidArrayList.size(); i++) {
            String name = wifiSsidArrayList.get(i);
            WifiSsid ssid = new WifiSsid();
            ssid.setWifiSsid(name);
            list[i] = ssid;
        }

        ssidPolicy.setWifiSsids(list);

        DeviceConnectivityManagement deviceConnectivityManagement = new DeviceConnectivityManagement();
        deviceConnectivityManagement.setWifiSsidPolicy(ssidPolicy);
*/


        JsonArray wifiSSidArray = new JsonArray();
        for(String s: wifiSsidArrayList) {
            JsonObject obj = new JsonObject();
            obj.addProperty("wifiSsid", s);
            wifiSSidArray.add(obj);
        }

        JsonObject wifiSsidPolicy = new JsonObject();
        wifiSsidPolicy.addProperty("wifiSsidPolicyType", binding.wifiPolicyTypeTv.getText().toString());
        wifiSsidPolicy.add("wifiSsids", wifiSSidArray);

        JsonObject deviceConnectivityManagement = new JsonObject();
        deviceConnectivityManagement.add("wifiSsidPolicy", wifiSsidPolicy);

        fieldToPatch.add("deviceConnectivityManagement", deviceConnectivityManagement);
        policy.add("field_to_patch", fieldToPatch);
        Log.i("createNewPolicy", "createPolicy: " + policy);

//        userAlertClient.showWaitDialog("Creating Policy");
//        lambdaClient.ExecuteCreatePolicyLambda(policy, createPolicyCallback);

    }

    private final CreatePolicyResponseHandler createPolicyCallback = new CreatePolicyResponseHandler() {
        @Override
        public void onSuccess(@NonNull JsonObject response) {
            userAlertClient.closeWaitDialog();
            Log.i("updatePolicy", "Lambda Response Success");
            try {
                int statusCode = response.get("statusCode").getAsInt();
                if (statusCode == 200) {
                    userAlertClient.showDialogMessage("Policy Created", "Policy created successfully", false);
                } else {
                    String message = response.get("body").getAsString();
                    userAlertClient.showDialogMessage("Error", message, false);
                }
            } catch (Exception exception) {
                Log.i("updatePolicy", "onSuccess: error while creating policy " + exception.getMessage());
            }
        }

        @Override
        public void onError(@NonNull String error) {
            userAlertClient.closeWaitDialog();
            userAlertClient.showDialogMessage("Error", error, false);
        }
    };

    private final ClickCallback applicationListItemClickCallback = new ClickCallback() {
        @Override
        public void onClick(int position) {
            binding.applicationsListRecyclerView.smoothScrollToPosition(position);
        }
    };


    private ItemTouchHelper.SimpleCallback swipeDelete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            deleteAppCallback.deleteApp(viewHolder.getAdapterPosition());
            Log.i("KioskSelection", "Delete position: " + viewHolder.getAdapterPosition());
            if (viewHolder.getAdapterPosition() == kioskAppPositionInList) {
                kioskAppPositionInList = -1;
                kioskAppPackageName = "";
            }
            binding.chooseKioskAppButton.setEnabled(!applicationArrayList.isEmpty());
            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
        }
    };

    private void showKioskAppSelection(int kioskAppPosition) {
        for (Application app : applicationArrayList) {
            app.setShowRadioButton(true);
        }
        adapter.notifyDataSetChanged();
        Log.i("KioskSelection", "Kiosk app position: " + kioskAppPosition);
        if (kioskAppPosition >= 0 && kioskAppPosition < applicationArrayList.size()) {
            applicationArrayList.get(kioskAppPosition).setKioskSelected(true);
            adapter.notifyDataSetChanged();
        }
    }

    CreateNewPolicyDialog.RadioButtonSelection kioskAppSelectionCallback = new CreateNewPolicyDialog.RadioButtonSelection() {
        @Override
        public void onKioskAppSelected(int position) {
            kioskAppPackageName = applicationArrayList.get(position).getPackageName();
            kioskAppPositionInList = position;
            applicationArrayList.get(position).setKioskSelected(true);

            Log.i("KioskSelection", "Package Name: " + kioskAppPackageName);
            Log.i("KioskSelection", "Position: " + kioskAppPositionInList);


            for (int i = 0; i < applicationArrayList.size(); i++) {
                Application app = applicationArrayList.get(i);
                if (i != position) {
                    app.setKioskSelected(false);
                }
                app.setShowRadioButton(false);
                Log.i("KioskSelection", "App: " + app.getPackageName() + " -> " + app.isKioskSelected());
            }

            adapter.notifyDataSetChanged();
        }
    };

    EditAppClickCallback editAppClickCallback = (app, index) -> showEditAppDialog(app, index);

    WifiDialog.SubmitWifi submitWifi = wifiName -> {
//        wifiSsidArrayList.add(wifiName);
        wifiListAdapter.addItem(wifiName);
    };

    public interface RadioButtonSelection {
        void onKioskAppSelected(int position);
    }

}
