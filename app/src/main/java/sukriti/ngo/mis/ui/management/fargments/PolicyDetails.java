package sukriti.ngo.mis.ui.management.fargments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.databinding.PoliciesItemLayoutBinding;
import sukriti.ngo.mis.interfaces.DialogSingleActionHandler;
import sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.AddNewApplicationDialog;
import sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.KioskCustomizationDialog;
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.KioskCustomization;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Policy;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.PolicyListItem;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.ClickCallback;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.DeleteAppCallback;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.EditAppClickCallback;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.RefreshListCallback;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.SubmitApplication;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.SubmitKioskCustomization;
import sukriti.ngo.mis.ui.management.ManagementViewModel;
import sukriti.ngo.mis.ui.management.adapters.ApplicationListAdapter;
import sukriti.ngo.mis.ui.management.data.device.Application;
import sukriti.ngo.mis.ui.management.lambda.CreateNewPolicy.CreatePolicyResponseHandler;
import sukriti.ngo.mis.utils.LambdaClient;
import sukriti.ngo.mis.utils.UserAlertClient;

public class PolicyDetails extends DialogFragment implements View.OnClickListener {

    private Policy policyDetails;
    private PoliciesItemLayoutBinding binding;
    ArrayList<Application> newApplication = new ArrayList<>();
    ArrayList<Application> list = new ArrayList<>();
    UserAlertClient userAlertClient;
    ApplicationListAdapter adapter;
    String kioskAppPackageName = "";
    int kioskAppPositionInList = -1;

/*
    SwitchCompat cameraEnabledContainer, addUserDisabledContainer, removeUserDisabledContainer, factoryResetDisabledContainer, mountPhysicalMediaDisabledContainer, safeBootDisabledContainer, uninstallAppDisabledContainer, bluetoothConfigDisabledContainer, vpnConfigDisabledContainer, networkResetDisabledContainer, smsDisabledContainer, modifyAccountsDisabledContainer, outgoingCallsDisabledContainer, kioskCustomizationSwitch;
*/

    /*
        FloatingActionButton policySelected;
        MaterialButton addNewAppButton;
        MaterialButton submitEditsButton;
        TextView kioskCustomizationHeading;
        LinearLayout kioskCustomizationContainer;
        Button editButton;
    */
    private EnrollDeviceViewModel viewModel;
    private ManagementViewModel managementViewModel;
    private SelectedCallback selectedCallback;
//    RecyclerView applicationsList;

    private KioskCustomization kioskCustomization;
    private RefreshListCallback refreshListCallback;
    LambdaClient lambdaClient;

    // Below two variables are used to check if the form is editable or selectable or both.
    // Selectable means user can only select the particular policy and not edit it.
    // Editable means user can only edit the policy and not select it. (Selection is used at the time of device enrollment.)
    private boolean isSelectable = false, isEditable = false;
    // The below variable is used to check if the user is currently editing the policy or not.
    private boolean isEditing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PoliciesItemLayoutBinding.inflate(inflater);
/*
        cameraEnabledContainer = view.findViewById(R.id.cameraEnabledValue);
        addUserDisabledContainer = view.findViewById(R.id.addUserDisabledValue);
        removeUserDisabledContainer = view.findViewById(R.id.removeUserDisabledValue);
        factoryResetDisabledContainer = view.findViewById(R.id.factoryResetDisabledValue);
        mountPhysicalMediaDisabledContainer = view.findViewById(R.id.mountPhysicalMediaDisabledValue);
        safeBootDisabledContainer = view.findViewById(R.id.safeBootDisabledValue);
        uninstallAppDisabledContainer = view.findViewById(R.id.uninstallAppsDisabledValue);
        bluetoothConfigDisabledContainer = view.findViewById(R.id.bluetoothConfigDisabledValue);
        vpnConfigDisabledContainer = view.findViewById(R.id.vpnConfigDisabledValue);
        networkResetDisabledContainer = view.findViewById(R.id.networkResetDisabledValue);
        smsDisabledContainer = view.findViewById(R.id.smsDisabledValue);
        modifyAccountsDisabledContainer = view.findViewById(R.id.modifyAccountsDisabledValue);
        outgoingCallsDisabledContainer = view.findViewById(R.id.outgoingCallsDisabledValue);
        kioskCustomizationSwitch = view.findViewById(R.id.kioskCustomizationEnabledValue);
        policySelected = view.findViewById(R.id.selectPolicy);
        applicationsList = view.findViewById(R.id.applicationsList);
        editButton = view.findViewById(R.id.editPolicyButton);
        addNewAppButton = view.findViewById(R.id.addNewApplication);
        submitEditsButton = view.findViewById(R.id.submitEditPolicy);
        kioskCustomizationContainer = view.findViewById(R.id.kioskCustomizationContainer);
        kioskCustomizationHeading = view.findViewById(R.id.kioskCustomizationHeadingTv);
*/


        binding.cameraEnabledValue.setClickable(false);
        binding.addUserDisabledValue.setClickable(false);
        binding.removeUserDisabledValue.setClickable(false);
        binding.factoryResetDisabledValue.setClickable(false);
        binding.mountPhysicalMediaDisabledValue.setClickable(false);
        binding.safeBootDisabledValue.setClickable(false);
        binding.uninstallAppsDisabledValue.setClickable(false);
        binding.bluetoothConfigDisabledValue.setClickable(false);
        binding.vpnConfigDisabledValue.setClickable(false);
        binding.networkResetDisabledValue.setClickable(false);
        binding.smsDisabledValue.setClickable(false);
        binding.modifyAccountsDisabledValue.setClickable(false);
        binding.outgoingCallsDisabledValue.setClickable(false);
//        binding.kioskCustomizationEnabledValue.setClickable(false);


        binding.selectPolicy.setOnClickListener(this);
        binding.editPolicyButton.setOnClickListener(this);
        binding.submitEditPolicy.setOnClickListener(this);
        binding.addNewApplication.setOnClickListener(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] policyNameArray = policyDetails.getName().split("/");
        String name = policyNameArray[policyNameArray.length - 1];
        Log.i("customName", name);
        if (name.contains("_KIOSK_LAUNCHER")) {
            name = name.subSequence(0, name.length() - 15).toString();
            Log.i("customName", "Contains _KIOSK_LAUNCHER");
        } else {
            Log.i("customName", "Not contains _KIOSK_LAUNCHER");
        }
        binding.policyNameEditText.setText(name);
        binding.cameraEnabledValue.setChecked(policyDetails.getCameraDisabled());
        binding.addUserDisabledValue.setChecked(policyDetails.getAddUserDisabled());
        binding.removeUserDisabledValue.setChecked(policyDetails.getRemoveUserDisabled());
        binding.factoryResetDisabledValue.setChecked(policyDetails.getFactoryResetDisabled());
        binding.mountPhysicalMediaDisabledValue.setChecked(policyDetails.getMountPhysicalMediaDisabled());
        binding.safeBootDisabledValue.setChecked(policyDetails.getSafeBootDisabled());
        binding.uninstallAppsDisabledValue.setChecked(policyDetails.getUninstallAppsDisabled());
        binding.bluetoothConfigDisabledValue.setChecked(policyDetails.getBluetoothConfigDisabled());
        binding.vpnConfigDisabledValue.setChecked(policyDetails.getVpnConfigDisabled());
        binding.networkResetDisabledValue.setChecked(policyDetails.getNetworkResetDisabled());
        binding.smsDisabledValue.setChecked(policyDetails.getSmsDisabled());
        binding.modifyAccountsDisabledValue.setChecked(policyDetails.getModifyAccountsDisabled());
        binding.outgoingCallsDisabledValue.setChecked(policyDetails.getOutgoingCallsDisabled());
        binding.powerButtonActionTv.setText(policyDetails.getKioskCustomization().getPowerButtonActions());
        binding.systemErrorWarningTv.setText(policyDetails.getKioskCustomization().getSystemErrorWarnings());
        binding.systemNavigationTv.setText(policyDetails.getKioskCustomization().getSystemNavigation());
        binding.statusBarTv.setText(policyDetails.getKioskCustomization().getStatusBar());
        binding.deviceSettingsLayoutTv.setText(policyDetails.getKioskCustomization().getDeviceSettings());


        binding.powerButtonActionLayout.setEnabled(false);
        binding.systemNavigationLayout.setEnabled(false);
        binding.systemErrorWarningLayout.setEnabled(false);
        binding.statusBarLayout.setEnabled(false);
        binding.deviceSettingsLayout.setEnabled(false);

//        binding.kioskCustomizationEnabledValue.setChecked(policyDetails.getKioskCustomLauncherEnabled());
        lambdaClient = new LambdaClient(getContext());
/*
        if (policyDetails.getKioskCustomLauncherEnabled()) {
            setKioskCustomizationView();
        }
*/
        if (policyDetails.getApplications() != null) {
            list = (ArrayList<Application>) policyDetails.getApplications();
            Log.i("policyDetailsApp", new Gson().toJson(policyDetails.getApplications()));
        } else {
            list = new ArrayList<>();
        }

/*        for (Application app :
                list) {
            if (app.getPackageName().equals(policyDetails.getKioskPackage())) {
                app.setInstallType("KIOSK");
            }
        }*/

        setApplications(list);

        userAlertClient = new UserAlertClient(getActivity());
        Log.i("policyDetailBoolean", "onViewCreated: Selectable: " + isSelectable);
        Log.i("policyDetailBoolean", "onViewCreated: Editable: " + isEditable);

        if (isSelectable) {
            binding.selectPolicy.setVisibility(View.VISIBLE);
            Log.i("policyDetailBoolean", "onViewCreated: Visibility set for select policy button: ");
        } else {
            binding.selectPolicy.setVisibility(View.GONE);
            Log.i("policyDetailBoolean", "onViewCreated: Visibility not set for select policy button: ");
        }
        if (isEditable) {
            binding.editPolicyButton.setVisibility(View.VISIBLE);
            Log.i("policyDetailBoolean", "onViewCreated: Visibility set for edit policy button: ");
        } else {
            binding.editPolicyButton.setVisibility(View.GONE);
            Log.i("policyDetailBoolean", "onViewCreated: Visibility not set for edit policy button: ");
        }

/*        binding.kioskCustomizationEnabledValue.setOnCheckedChangeListener((compoundButton, checked) -> {
            if(isEditable && checked) {
                KioskCustomizationDialog dialog = new KioskCustomizationDialog(getContext(), kioskCallback);
                dialog.setCancelable(false);
                dialog.show(getChildFragmentManager(), "Kiosk Customization Dialog");

            }
        });*/

        binding.addNewApplication.setOnClickListener(this);

        binding.editPolicyButton.setOnClickListener(this);

/*        binding.kioskCustomizationContainer.setOnClickListener(view1 -> {
            KioskCustomizationDialog dialog = new KioskCustomizationDialog(requireContext(), kioskCallback);
            dialog.setCancelable(false);
            dialog.show(getChildFragmentManager(), "Kiosk Customization Dialog");
        });*/

        binding.chooseKioskAppButton.setOnClickListener(v -> {
            showKioskAppSelection(kioskAppPositionInList);
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        String[] powerButtonAction = getResources().getStringArray(R.array.powerButtonAction);
        String[] systemErrorWarnings = getResources().getStringArray(R.array.SystemErrorWarnings);
        String[] systemNavigation = getResources().getStringArray(R.array.SystemNavigation);
        String[] statusBar = getResources().getStringArray(R.array.StatusBar);
        String[] deviceSettings = getResources().getStringArray(R.array.DeviceSettings);

        ArrayAdapter<String> powerButtonAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown, powerButtonAction);
        ArrayAdapter<String> systemErrorWarningsAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown, systemErrorWarnings);
        ArrayAdapter<String> systemNavigationAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown, systemNavigation);
        ArrayAdapter<String> statusBarAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown, statusBar);
        ArrayAdapter<String> deviceSettingsAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown, deviceSettings);

        binding.powerButtonActionTv.setAdapter(powerButtonAdapter);
        binding.systemErrorWarningTv.setAdapter(systemErrorWarningsAdapter);
        binding.systemNavigationTv.setAdapter(systemNavigationAdapter);
        binding.statusBarTv.setAdapter(statusBarAdapter);
        binding.deviceSettingsLayoutTv.setAdapter(deviceSettingsAdapter);
    }

/*
    private void setKioskCustomizationView() {
        binding.kioskCustomizationHeadingTv.setVisibility(View.VISIBLE);
        binding.kioskCustomizationContainer.setVisibility(View.VISIBLE);
        KioskCustomization customization = policyDetails.getKioskCustomization();
        binding.powerActionButtonValue.setText(customization.getPowerButtonActions());
        binding.statusBarValue.setText(customization.getStatusBar());
        binding.deviceSettingsValue.setText(customization.getDeviceSettings());
        binding.systemNavigationValue.setText(customization.getSystemNavigation());
        binding.systemErrorWarningValue.setText(customization.getSystemErrorWarnings());
        this.kioskCustomization = customization;
    }
*/

    private void setApplications(ArrayList<Application> arrayList) {
        adapter = new ApplicationListAdapter();
        Log.i("showDelDebug", "showDelete = " + isEditing);
        adapter.showDelete(isEditing);
        adapter.setList(arrayList);
        adapter.setClickCallback(applicationListItemClickCallback);
        adapter.setRadioButtonSelectionCallback(kioskAppSelectionCallback);
        adapter.setDeleteCallback(deleteAppCallback);
        adapter.setEditAppClickCallback(editAppClickCallback);
        binding.applicationsList.setAdapter(adapter);
        binding.applicationsList.setLayoutManager(new LinearLayoutManager(getContext()));

        if (isEditing) {
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeDelete);
            itemTouchHelper.attachToRecyclerView(binding.applicationsList);
        }

        binding.chooseKioskAppButton.setEnabled(!arrayList.isEmpty());
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        if (dialog != null) {
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }

    }

    public Policy getPolicyDetails() {
        return policyDetails;
    }

    public void setPolicyDetails(Policy policyDetails) {
        this.policyDetails = policyDetails;
    }

    public interface SelectedCallback {
        public void onSelected();
    }

    public void setViewModel(EnrollDeviceViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public SelectedCallback getSelectedCallback() {
        return selectedCallback;
    }

    public void setSelectedCallback(SelectedCallback selectedCallback) {
        this.selectedCallback = selectedCallback;
    }

    public boolean isSelectable() {
        return isSelectable;
    }

    public void setSelectable(boolean selectable) {
        isSelectable = selectable;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == binding.selectPolicy.getId()) {
            viewModel.setSelectedPolicy(policyDetails);
            String[] selectedPolicyNameArray = viewModel.getSelectedPolicy().getName().split("/");
            String name = selectedPolicyNameArray[selectedPolicyNameArray.length-1];
            name = name.substring(0, name.length()-15);

            for (int i = 0; i < viewModel.getPolicyListItem().size(); i++) {
                PolicyListItem currentPolicyItem = viewModel.getPolicyListItem().get(i);
                currentPolicyItem.setSelected(currentPolicyItem.getName().equals(name));
            }
            selectedCallback.onSelected();
            dismiss();
        }
        else if (id == binding.editPolicyButton.getId()) {
            isEditing = true;
            makeDialogEditable();
            newApplication.addAll(list);
            for (Application app : list) {
                Log.i("newApp", new Gson().toJson(app));
            }
            setApplications(newApplication);
        }
        else if (id == binding.submitEditPolicy.getId()) {
            if (verifyDetails()) {
                try {
                    createNewPolicyPayload();
                } catch (JSONException e) {
                    Log.e("Error", "onClick: Error while creating Edit Policy JSON");
                }
            }
        }
        else if (id == binding.addNewApplication.getId()) {
            showAddNewApplicationDialog();
        }
    }

    public void showAddNewApplicationDialog() {
        AddNewApplicationDialog dialog = new AddNewApplicationDialog(requireContext(), callback, lambdaClient, managementViewModel, null, -1 );
        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "Add New Application");
    }

    public void showEditAppDialog(Application app, int index) {
        AddNewApplicationDialog dialog = new AddNewApplicationDialog(requireContext(), callback, lambdaClient, managementViewModel, app, index);
        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "Add New Application");
    }

    private void makeDialogEditable() {
        Log.i("policyDetailsBoolean", "makeDialogEditable: ");
        binding.editPolicyButton.setVisibility(View.GONE);
        binding.cameraEnabledValue.setClickable(true);
        binding.addUserDisabledValue.setClickable(true);
        binding.removeUserDisabledValue.setClickable(true);
        binding.factoryResetDisabledValue.setClickable(true);
        binding.mountPhysicalMediaDisabledValue.setClickable(true);
        binding.safeBootDisabledValue.setClickable(true);
        binding.uninstallAppsDisabledValue.setClickable(true);
        binding.bluetoothConfigDisabledValue.setClickable(true);
        binding.vpnConfigDisabledValue.setClickable(true);
        binding.networkResetDisabledValue.setClickable(true);
        binding.smsDisabledValue.setClickable(true);
        binding.modifyAccountsDisabledValue.setClickable(true);
        binding.outgoingCallsDisabledValue.setClickable(true);
        binding.powerButtonActionLayout.setEnabled(true);
        binding.systemNavigationLayout.setEnabled(true);
        binding.systemErrorWarningLayout.setEnabled(true);
        binding.statusBarLayout.setEnabled(true);
        binding.deviceSettingsLayout.setEnabled(true);

//        binding.kioskCustomizationEnabledValue.setClickable(true);

        binding.kioskCustomizationHeadingTv.setVisibility(View.VISIBLE);
//        binding.kioskCustomizationContainer.setVisibility(View.VISIBLE);
        binding.chooseKioskAppButton.setVisibility(View.VISIBLE);
        binding.addNewApplication.setVisibility(View.VISIBLE);
        binding.submitEditPolicy.setVisibility(View.VISIBLE);

//        list.clear();
        setApplications(newApplication);

    }


/*
    private final SubmitKioskCustomization kioskCallback = kioskCustomization -> {
        this.kioskCustomization = kioskCustomization;
        Log.i("kioskDialog", "kioskCallback: " + new Gson().toJson(kioskCustomization));
        binding.powerActionButtonValue.setText(kioskCustomization.getPowerButtonActions());
        binding.systemErrorWarningValue.setText(kioskCustomization.getSystemErrorWarnings());
        binding.systemNavigationValue.setText(kioskCustomization.getSystemNavigation());
        binding.statusBarValue.setText(kioskCustomization.getStatusBar());
        binding.deviceSettingsValue.setText(kioskCustomization.getDeviceSettings());
    };
*/

    private final SubmitApplication callback = new SubmitApplication() {
        @Override
        public void submitApp(@NonNull Application app) {
            Log.i("newApp", new Gson().toJson(app));
            newApplication.add(app);
            setApplications(newApplication);
            binding.chooseKioskAppButton.setEnabled(!newApplication.isEmpty());
        }

        @Override
        public void editApp(@NonNull Application app, int index) {
            newApplication.remove(index);
            newApplication.add(index, app);
            setApplications(newApplication);
            if(index == kioskAppPositionInList) {
                kioskAppPackageName = app.getPackageName();
            }
        }
    };
    private void createNewPolicyPayload() throws JSONException {
        JsonObject policy = new JsonObject();

        String[] details = policyDetails.getName().split("/");
        String enterpriseName = details[0] + "/" + details[1];
        String policyName = details[2] + "/" + details[3];
        policyName = policyName.substring(0, policyName.length()-15);
        Log.i("updatePolicy", "createNewPolicyPayload: Enterprise Name: " + enterpriseName);
        Log.i("updatePolicy", "createNewPolicyPayload: Policy Name : " + policyName);
        policy.addProperty("enterprises_id", enterpriseName);
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
        fieldToPatch.addProperty("kioskPackage", kioskAppPackageName);
//        fieldToPatch.addProperty("kioskCustomLauncherEnabled", binding.kioskCustomizationEnabledValue.isChecked());


        JsonArray applicationsJsonArray = new JsonArray();
        for (int i = 0; i < newApplication.size(); i++) {
            JsonObject application = new JsonObject();
            Application app = newApplication.get(i);
            Log.i("createNewApp", new Gson().toJson(app));
            application.addProperty("packageName", app.getPackageName());
            application.addProperty("installType", app.getInstallType());
            application.addProperty("defaultPermissionPolicy", app.getDefaultPermissionPolicy());
            application.addProperty("autoUpdateMode", app.getAutoUpdateMode());
            application.addProperty("userControlSettings", app.getUserControlSettings());

            applicationsJsonArray.add(application);
        }

        fieldToPatch.add("applications", applicationsJsonArray);

/*
        if(binding.kioskCustomizationEnabledValue.isChecked()) {
            JsonObject kiosk = new JsonObject();
            kiosk.addProperty("powerButtonActions", kioskCustomization.getPowerButtonActions());
            kiosk.addProperty("systemErrorWarnings", kioskCustomization.getSystemErrorWarnings());
            kiosk.addProperty("systemNavigation", kioskCustomization.getSystemNavigation());
            kiosk.addProperty("statusBar", kioskCustomization.getStatusBar());
            kiosk.addProperty("deviceSettings", kioskCustomization.getDeviceSettings());
            fieldToPatch.add("kioskCustomization", kiosk);
        }
*/

        JsonObject kiosk = new JsonObject();
        kiosk.addProperty("powerButtonActions", binding.powerButtonActionTv.getText().toString());
        kiosk.addProperty("systemErrorWarnings", binding.systemErrorWarningTv.getText().toString());
        kiosk.addProperty("systemNavigation", binding.systemNavigationTv.getText().toString());
        kiosk.addProperty("statusBar", binding.statusBarTv.getText().toString());
        kiosk.addProperty("deviceSettings", binding.deviceSettingsLayoutTv.getText().toString());
        fieldToPatch.add("kioskCustomization", kiosk);

        policy.add("field_to_patch", fieldToPatch);
        Log.i("updatePolicy", "createPolicy: " + policy);

        // create payload and call update policy lambda

        userAlertClient.showWaitDialog("Updating Policy");
        lambdaClient.ExecuteCreatePolicyLambda(policy, createPolicyCallback);

    }

    private final CreatePolicyResponseHandler createPolicyCallback = new CreatePolicyResponseHandler() {
        @Override
        public void onSuccess(@NonNull JsonObject response) {
            userAlertClient.closeWaitDialog();
            Log.i("updatePolicy", "Lambda Response Success");
            try {
                int statusCode = response.get("statusCode").getAsInt();
                if (statusCode == 200) {
                    userAlertClient.showDialogMessage("Policy Updated", "Policy updated successfully", () -> {
                        dismiss();
                        if (refreshListCallback != null) {
                            refreshListCallback.refreshList();
                        }
                    });
                } else {
                    String message = response.get("body").getAsString();
                    userAlertClient.showDialogMessage("Error", message, false);
                }
            } catch (Exception exception) {
                Log.i("updatePolicy", "onSuccess: error while updating policy " + exception.getMessage());
            }
        }

        @Override
        public void onError(@NonNull String error) {
            userAlertClient.closeWaitDialog();
            userAlertClient.showDialogMessage("Error", error, false);
        }
    };

    private final ItemTouchHelper.SimpleCallback swipeDelete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            deleteAppCallback.deleteApp(viewHolder.getAdapterPosition());
            binding.chooseKioskAppButton.setEnabled(!newApplication.isEmpty());
            if (viewHolder.getAdapterPosition() == kioskAppPositionInList) {
                kioskAppPositionInList = -1;
                kioskAppPackageName = "";
            }
        }
    };

    private final DeleteAppCallback deleteAppCallback = new DeleteAppCallback() {
        @Override
        public void deleteApp(int position) {
            newApplication.remove(position);
            adapter.notifyItemRemoved(position);
            if (position == kioskAppPositionInList) {
                kioskAppPositionInList = -1;
                kioskAppPackageName = "";
            }
            binding.chooseKioskAppButton.setEnabled(!newApplication.isEmpty());
        }
    };

    private final ClickCallback applicationListItemClickCallback = new ClickCallback() {
        @Override
        public void onClick(int position) {
            binding.applicationsList.smoothScrollToPosition(position);
        }
    };


    public void setRefreshListCallback(RefreshListCallback refreshListCallback) {
        this.refreshListCallback = refreshListCallback;
    }

    public void setManagementViewModel(ManagementViewModel managementViewModel) {
        this.managementViewModel = managementViewModel;
    }

    private void showKioskAppSelection(int kioskAppPosition) {
        for (Application app : newApplication) {
            app.setShowRadioButton(true);
        }
        adapter.notifyDataSetChanged();
        Log.i("KioskSelection", "Kiosk app position: " + kioskAppPosition);
        if (kioskAppPosition >= 0 && kioskAppPosition < newApplication.size()) {
            newApplication.get(kioskAppPosition).setKioskSelected(true);
            adapter.notifyDataSetChanged();
        }
    }

    CreateNewPolicyDialog.RadioButtonSelection kioskAppSelectionCallback = new CreateNewPolicyDialog.RadioButtonSelection() {
        @Override
        public void onKioskAppSelected(int position) {
            kioskAppPackageName = newApplication.get(position).getPackageName();
            kioskAppPositionInList = position;
            newApplication.get(position).setKioskSelected(true);

            Log.i("KioskSelection", "Package Name: " + kioskAppPackageName);
            Log.i("KioskSelection", "Position: " + kioskAppPositionInList);


            for (int i = 0; i < newApplication.size(); i++) {
                Application app = newApplication.get(i);
                if (i != position) {
                    app.setKioskSelected(false);
                }
                app.setShowRadioButton(false);
                Log.i("KioskSelection", "App: " + app.getPackageName() + " -> " + app.isKioskSelected());
            }

            adapter.notifyDataSetChanged();
        }
    };

    private boolean verifyDetails() {
//        boolean isKioskCustomizationNotEmpty = true;
        boolean isApplicationListNotEmpty = true;
        boolean powerButton;
        boolean systemError;
        boolean systemNavigation;
        boolean statusBar;
        boolean deviceSettings;

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

/*
        if (kioskCustomization == null) {
            binding.kioskCustomizationErrorTv.setVisibility(View.VISIBLE);
            isKioskCustomizationNotEmpty = false;
        } else {
            binding.kioskCustomizationErrorTv.setVisibility(View.GONE);
        }
*/

        if (newApplication.isEmpty()) {
            binding.appListEmptyErrorTv.setVisibility(View.VISIBLE);
            isApplicationListNotEmpty = false;
        } else {
            binding.appListEmptyErrorTv.setVisibility(View.GONE);
        }

        return /*isKioskCustomizationNotEmpty &&*/ isApplicationListNotEmpty && powerButton && systemError && systemNavigation && statusBar && deviceSettings;
    }

    private boolean kioskAppSelected() {
        if (!newApplication.isEmpty()) {
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

    EditAppClickCallback editAppClickCallback = new EditAppClickCallback() {
        @Override
        public void edit(@NonNull Application app, int index) {
            showEditAppDialog(app, index);
        }
    };

    public interface RadioButtonSelection {
        void onKioskAppSelected(int position);
    }

}