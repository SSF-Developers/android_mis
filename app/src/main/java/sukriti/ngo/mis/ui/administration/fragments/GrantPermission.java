package sukriti.ngo.mis.ui.administration.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.security.spec.RSAOtherPrimeInfo;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.databinding.FragmentGrantPermissionBinding;
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.ClientList;
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.ClientListLambdaResult;
import sukriti.ngo.mis.ui.administration.lambda.CreateUser.GetClientListResultHandler;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.ClientPermissionData;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.ClientPermissionLambdaRequest;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.ClientPermissionLambdaResult;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.GetPermissionDataResultHandler;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.SubmitPermissionData;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.SubmitPermissionResult;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.SubmitPermissionResultHandler;
import sukriti.ngo.mis.ui.administration.lambda.GrantPermission.SubmitPermissionsRequest;
import sukriti.ngo.mis.utils.LambdaClient;
import sukriti.ngo.mis.utils.UserAlertClient;

public class GrantPermission extends Fragment {

    private FragmentGrantPermissionBinding binding;
    private LambdaClient lambdaClient;
    private UserAlertClient userAlertClient;

    public GrantPermission() {
        // Required empty public constructor
    }

    public static GrantPermission newInstance() {
        GrantPermission fragment = new GrantPermission();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentGrantPermissionBinding.inflate(inflater, container, false);
        lambdaClient = new LambdaClient(getContext());
        userAlertClient = new UserAlertClient(getActivity());
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.items, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        binding.clientSelectionSpinner.setAdapter(adapter);
        getClientList("SSF");

        return binding.root;
    }
    @Override
    public void onResume() {
        super.onResume();
        setClickListener();
    }

    private void setClickListener() {
        binding.clientSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("myPermissionData", "Item Selected " + adapterView.getItemAtPosition(i));
                userAlertClient.showWaitDialog("Loading permissions");
                loadClientDetails(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.gpSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("myPermissionData", "Button Clicked");
                userAlertClient.showWaitDialog("Submitting Data");
                SubmitPermissionResultHandler callback = new SubmitPermissionResultHandler() {
                    @Override
                    public void onSuccess(@NonNull SubmitPermissionResult result) {
                        userAlertClient.closeWaitDialog();
                        if(result.getResult().equals("1") && result.getMessage().equals("success")) {
                            userAlertClient.showDialogMessage("Success", "Data Submitted Successfully", false);
                        } else {
                            userAlertClient.showDialogMessage("Error", "Something went wrong", false);
                        }
                    }

                    @Override
                    public void onError(@NonNull String message) {
                        userAlertClient.showDialogMessage("Error", message, false);
                    }
                };

                SubmitPermissionData data = getUiData();
                data.setClientName(binding.clientSelectionSpinner.getSelectedItem().toString());
                SubmitPermissionsRequest request = new SubmitPermissionsRequest(data);
                lambdaClient.ExecuteSubmitPermissionsLambda(request, callback);

            }
        });

    }

    public void getClientList(String clientName) {
        userAlertClient.showWaitDialog("Getting Client List");
        GetClientListResultHandler resultHandler = new GetClientListResultHandler() {
            @Override
            public void onSuccess(ClientListLambdaResult result) {
                userAlertClient.closeWaitDialog();
                Log.i("clientList", "Lambda result success");
                List<ClientList> list = result.getList();
                ArrayList<String> items = new ArrayList<>();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.color_spinner_layout, items);
                adapter.clear();
                for(ClientList client: list) {
                    adapter.add(client.getName());
                }
                adapter.setDropDownViewResource(R.layout.spinner_dropdown);
                binding.clientSelectionSpinner.setAdapter(adapter);
                userAlertClient.showWaitDialog("Fetching permissions");
                loadClientDetails(clientName);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("clientList", errorMessage);
                userAlertClient.showDialogMessage("Error!", "Something went wrong", true);
            }
        };

        Log.i("clientList", "Executing lambda");
        lambdaClient.ExecuteGetClientListLambda(resultHandler);

    }

    public void loadClientDetails(String clientName) {

        GetPermissionDataResultHandler handler = new GetPermissionDataResultHandler() {
            @Override
            public void onSuccess(@NonNull ClientPermissionLambdaResult result) {
                userAlertClient.closeWaitDialog();
                Log.i("myPermissionData", "Grant Permission onSuccess()");
                ClientPermissionData data = result.getData();
                setUiData(data);
            }

            @Override
            public void onError(@NonNull String message) {
                Log.i("myPermissionData", "Grant Permission onError()");
            }
        };

        ClientPermissionLambdaRequest request = new ClientPermissionLambdaRequest(clientName);
        Log.i("myPermissionData", "calling lambda with clientName " + clientName);
        lambdaClient.ExecuteGetPermissionDataLambda(request, handler);
    }

    public void setUiData(ClientPermissionData data) {
        binding.gpDashboard.totalIndicatorLayout.switchTotalUsage.setChecked(data.getTotal_usage());
        binding.gpDashboard.totalIndicatorLayout.switchAverageFeedback.setChecked(data.getAverage_feedback());
        binding.gpDashboard.totalIndicatorLayout.switchWaterSaved.setChecked(data.getWater_saved());

        binding.gpDashboard.collectionLayout.switchCollectionStats.setChecked(data.getCollection_stats());
        binding.gpDashboard.collectionLayout.switchUsageQuickConfig.setChecked(data.getUsage_charge());
        binding.gpDashboard.collectionLayout.switchUsageProfile.setChecked(data.getUsage_charge_profile());

        binding.gpDashboard.bwtReWaterStatLayout.switchReWaterStat.setChecked(data.getBwt_stats());

        binding.gpComplex.cabinStatusLayout.switchCarbonMonoxide.setChecked(data.getCarbon_monooxide());
        binding.gpComplex.cabinStatusLayout.switchMethane.setChecked(data.getMethane());
        binding.gpComplex.cabinStatusLayout.switchAmmonia.setChecked(data.getAmmonia());
        binding.gpComplex.cabinStatusLayout.switchLuminous.setChecked(data.getLuminous());

        binding.gpComplex.cabinHealthLayout.switchAirDryerHealth.setChecked(data.getAir_dryer_health());
        binding.gpComplex.cabinHealthLayout.switchChoke.setChecked(data.getChoke_health());
        binding.gpComplex.cabinHealthLayout.switchTap.setChecked(data.getTap_health());

        binding.gpComplex.usageProfileLayout.switchAirDryerUsageProfile.setChecked(data.getAir_dryer_profile());
        binding.gpComplex.usageProfileLayout.switchRfid.setChecked(data.getRfid_profile());

        binding.gpBwt.systemHealthLayout.switchAlp.setChecked(data.getAlp());
        binding.gpBwt.systemHealthLayout.switchMp1Valve.setChecked(data.getMp1_valve());
        binding.gpBwt.systemHealthLayout.switchMp2Valve.setChecked(data.getMp2_valve());
        binding.gpBwt.systemHealthLayout.switchMp3Valve.setChecked(data.getMp3_valve());
        binding.gpBwt.systemHealthLayout.switchMp4Valve.setChecked(data.getMp4_valve());

        binding.gpBwt.usageLayout.switchTurbidityValue.setChecked(data.getTurbidity_value());


    }

    public SubmitPermissionData getUiData() {
        SubmitPermissionData data = new SubmitPermissionData();
        data.setTotal_usage(binding.gpDashboard.totalIndicatorLayout.switchTotalUsage.isChecked());
        data.setAverage_feedback(binding.gpDashboard.totalIndicatorLayout.switchAverageFeedback.isChecked());
        data.setWater_saved(binding.gpDashboard.totalIndicatorLayout.switchWaterSaved.isChecked());

        data.setCollection_stats(binding.gpDashboard.collectionLayout.switchCollectionStats.isChecked());
        data.setUsage_charge(binding.gpDashboard.collectionLayout.switchUsageQuickConfig.isChecked());
        data.setUsage_charge_profile(binding.gpDashboard.collectionLayout.switchUsageProfile.isChecked());

        data.setBwt_stats(binding.gpDashboard.bwtReWaterStatLayout.switchReWaterStat.isChecked());

        data.setCarbon_monooxide(binding.gpComplex.cabinStatusLayout.switchCarbonMonoxide.isChecked());
        data.setMethane(binding.gpComplex.cabinStatusLayout.switchMethane.isChecked());
        data.setAmmonia(binding.gpComplex.cabinStatusLayout.switchAmmonia.isChecked());
        data.setLuminous(binding.gpComplex.cabinStatusLayout.switchLuminous.isChecked());

        data.setAir_dryer_health(binding.gpComplex.cabinHealthLayout.switchAirDryerHealth.isChecked());
        data.setChoke_health(binding.gpComplex.cabinHealthLayout.switchChoke.isChecked());
        data.setTap_health(binding.gpComplex.cabinHealthLayout.switchTap.isChecked());

        data.setAir_dryer_profile(binding.gpComplex.usageProfileLayout.switchAirDryerUsageProfile.isChecked());
        data.setRfid_profile(binding.gpComplex.usageProfileLayout.switchRfid.isChecked());

        data.setAlp(binding.gpBwt.systemHealthLayout.switchAlp.isChecked());
        data.setMp1_valve(binding.gpBwt.systemHealthLayout.switchMp1Valve.isChecked());
        data.setMp2_valve(binding.gpBwt.systemHealthLayout.switchMp2Valve.isChecked());
        data.setMp3_valve(binding.gpBwt.systemHealthLayout.switchMp3Valve.isChecked());
        data.setMp4_valve(binding.gpBwt.systemHealthLayout.switchMp4Valve.isChecked());

        data.setTurbidity_value(binding.gpBwt.usageLayout.switchTurbidityValue.isChecked());

        return data;
    }

}