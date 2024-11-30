package sukriti.ngo.mis.ui.management.HelperClassesAndFragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.BillingGroupDetails;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.CreateBillingGroupHandler;
import sukriti.ngo.mis.R;

import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListIotStateDistrictCityLambdaResponse;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ListIotStateDistrictCityResponseHandler;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.ManagementLambdaRequest;
import sukriti.ngo.mis.ui.management.lambda.EnrollDevice.RequestManagementLambda;
import sukriti.ngo.mis.utils.AWSIotProvisioningClient;
import sukriti.ngo.mis.utils.LambdaClient;
import sukriti.ngo.mis.utils.SharedPrefsClient;
import sukriti.ngo.mis.utils.UserAlertClient;
import java.util.ArrayList;


public class RegisterNewBillingGroup extends DialogFragment implements OnClickListener {
    private UserAlertClient userAlertClient;
//    private CitiesApiClient citiesApiClient;

    private TextView label;
    private ImageView close;
    private FloatingActionButton fab;
    private TextInputLayout groupNameLayout, descriptionLayout;
    private TextInputEditText billingGroup, description;

    private boolean isValidName = false;
    private String BillingGroupName = "";

    private int Action;
    private String Title;
    private SharedPrefsClient sharedPrefsClient;
    private LambdaClient lambdaClient;

    public RegisterNewBillingGroup() {
        // Required empty public constructor
    }

    public static RegisterNewBillingGroup newInstance(int action) {
        RegisterNewBillingGroup fragment = new RegisterNewBillingGroup();
        Bundle args = new Bundle();
        args.putInt("action", action);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFragmentStyle);
        if (getArguments() != null) {
            userAlertClient = new UserAlertClient(getActivity());
            Action = getArguments().getInt("action");
            Title = "Add New Billing Group";
        }
        sharedPrefsClient = new SharedPrefsClient(getContext());
        lambdaClient = new LambdaClient(getContext());
        userAlertClient = new UserAlertClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popup_register_new_billing_group, container, false);

        groupNameLayout = v.findViewById(R.id.groupNameLayout);
        descriptionLayout = v.findViewById(R.id.descriptionLayout);
        label = v.findViewById(R.id.popupLabel);
        close = v.findViewById(R.id.closeContainer);
        fab = v.findViewById(R.id.fab);
        billingGroup = v.findViewById(R.id.billingGroup);
//        billingGroupErr = v.findViewById(R.id.billingGroupErr);
        description = v.findViewById(R.id.description);

        close.setOnClickListener(this);
        fab.setOnClickListener(this);

//        label.setText(Title);
        return v;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeContainer:
                dismiss();
                break;

            case R.id.fab:
                if (isValidated())
                    initiateAddThingGroup();
                break;
        }
    }

    public void initiateAddThingGroup() {
        final BillingGroupDetails billingGroupDetails = new BillingGroupDetails();
        billingGroupDetails.Parent = "";
        billingGroupDetails.Name = BillingGroupName;
        billingGroupDetails.Description = description.getText().toString();
        billingGroupDetails.Attributes = new ArrayList<>();
        //billingGroupDetails.Attributes.add(new Attributes(_ATTRIBUTE_MODIFICATION_LOG, getThingGroupCreationLogLog()));

        userAlertClient.showWaitDialog("Creating New Billing Group");

        ManagementLambdaRequest request = new ManagementLambdaRequest(
                sharedPrefsClient.getUserDetails().getUser().getUserName(),
                "create-billing-group",
                BillingGroupName,
                description.getText().toString()
        );

        ListIotStateDistrictCityResponseHandler callback = new ListIotStateDistrictCityResponseHandler() {
            @Override
            public void onSuccess(@NonNull JsonObject response) {
                userAlertClient.closeWaitDialog();
                int statusCode = response.get("statusCode").getAsInt();
                Log.i("create_billing_group", "onSuccess: Status Code: " + statusCode);
                String message = response.get("body").getAsString();
                Log.i("create_billing_group", "onSuccess: Message: " + message);
                if(statusCode == 200) {
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

        lambdaClient.ExecuteManagementLambda(request, callback);

    }

    private boolean isValidated() {
        String errMsg = "";
        BillingGroupName = billingGroup.getText().toString();
        if (BillingGroupName.isEmpty()) {
            errMsg = "Enter a Billing Group Name";
            userAlertClient.showDialogMessage("Error Alert!", errMsg, false);
            return false;
        }
        if (BillingGroupName.contains(" ")) {
            errMsg = "Name cannot have Spaces. Please provide a name without any space.";
            userAlertClient.showDialogMessage("Error Alert!", errMsg, false);
            return false;
        }
        return true;
    }
    private void setErrorMessage(TextView errTextView, String message) {
        errTextView.setText(message);
    }

    private void clearErrorMessage(TextView errTextView) {
        errTextView.setText("");
    }

}