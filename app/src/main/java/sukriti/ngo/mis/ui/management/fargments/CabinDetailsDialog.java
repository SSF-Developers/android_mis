package sukriti.ngo.mis.ui.management.fargments;

import android.app.Dialog;
import android.os.Bundle;
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

import java.util.Objects;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ComplexDetails;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ThingDetails;

public class CabinDetailsDialog extends DialogFragment implements View.OnClickListener{

    TextView stateName, stateNameErr, districtName, districtNameErr;
    TextView  cityName, cityNameErr, complexName, complexNameErr;
    TextView  cabinName, cabinNameErr, cabinType, cabinTypeErr;

    TextView urinalCountErr, bwtCapacityErr, bwtLevelErr, bwtLevel, usageChargeType, usageChargeTypeErr;
    EditText urinalCount, bwtCapacity;

    TextView  complexUuid, complexUuidErr, commissioning, commissioningErr;
    TextView  smartness, smartnessErr;
    TextView  maleWc, femaleWc;
    TextView  pdWc, urinals;
    TextView  urinalCabins, bwt;
    TextView  userType, userTypeErr;
    TextView  address, addressErr;
    TextView  lat, lon;
    TextView  clientName, clientNameErr;
    TextView  billingGroup, billingGroupErr;
    TextView  date, dateErr;
    ComplexDetails details;
    ThingDetails thingDetails;
    EditText cameraSerial;

    LinearLayout cabinAttributesLayout, complexAttributesLayout, clientLayout, cameraLayout;
    LinearLayout userTypeContainer, usageChargeTypeContainer, urinalCountContainer, bwtCapacityContainer, bwtLevelContainer;
    FloatingActionButton fabRegisterCabin;
    private EnrollDeviceViewModel viewModel;
    public ClickCallback clickCallback;

    public CabinDetailsDialog() {
    }

    public CabinDetailsDialog(ComplexDetails details, ThingDetails thingDetails) {
        this.details = details;
        this.thingDetails = thingDetails;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        if(dialog != null) {
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_cabin, container, false);
        cabinAttributesLayout= view.findViewById(R.id.cabinAttributes);
        complexAttributesLayout= view.findViewById(R.id.complexDetails);
        clientLayout= view.findViewById(R.id.clientDetails);
        cameraLayout= view.findViewById(R.id.cabinCameraDetails);

        cabinAttributesLayout.setVisibility(View.VISIBLE);
        complexAttributesLayout.setVisibility(View.VISIBLE);
        clientLayout.setVisibility(View.VISIBLE);
        cameraLayout.setVisibility(View.VISIBLE);

        stateName = view.findViewById(R.id.stateName);
        stateNameErr = view.findViewById(R.id.stateNameErr);
        districtName = view.findViewById(R.id.districtName);
        districtNameErr = view.findViewById(R.id.districtNameErr);
        cityName = view.findViewById(R.id.cityName);
        cityNameErr = view.findViewById(R.id.cityNameErr);
        complexName = view.findViewById(R.id.complexName);
        complexNameErr = view.findViewById(R.id.complexNameErr);
        cabinName = view.findViewById(R.id.cabinName);
        cabinNameErr = view.findViewById(R.id.cabinNameErr);
        cabinType = view.findViewById(R.id.cabinType);
        cabinTypeErr = view.findViewById(R.id.cabinTypeErr);
        urinalCount = view.findViewById(R.id.urinalCount);
        urinalCountErr = view.findViewById(R.id.urinalCountErr);
        userType = view.findViewById(R.id.userType);
        userTypeErr = view.findViewById(R.id.userTypeErr);
        usageChargeType = view.findViewById(R.id.usageChargeType);
        usageChargeTypeErr = view.findViewById(R.id.usageChargeTypeErr);
        bwtCapacity = view.findViewById(R.id.bwtCapacity);
        bwtLevel = view.findViewById(R.id.bwtLevel);
        bwtLevelErr = view.findViewById(R.id.bwtLevelErr);
        bwtCapacityErr = view.findViewById(R.id.bwtCapacityErr);
        complexUuid = view.findViewById(R.id.complexUuid);
        complexUuidErr = view.findViewById(R.id.complexUuidErr);
        commissioning = view.findViewById(R.id.commissioning);
        commissioningErr = view.findViewById(R.id.commissioningErr);
        smartness = view.findViewById(R.id.smartness);
        smartnessErr = view.findViewById(R.id.smartnessErr);
        maleWc = view.findViewById(R.id.maleWc);
        femaleWc = view.findViewById(R.id.femaleWc);
        pdWc = view.findViewById(R.id.pdWc);
        urinals = view.findViewById(R.id.urinals);
        urinalCabins = view.findViewById(R.id.urinalCabins);
        bwt = view.findViewById(R.id.bwt);
        address = view.findViewById(R.id.address);
        addressErr = view.findViewById(R.id.addressErr);
        lat = view.findViewById(R.id.lat);
        lon = view.findViewById(R.id.lon);
        clientName = view.findViewById(R.id.clientName);
        clientNameErr = view.findViewById(R.id.clientNameErr);
        billingGroup = view.findViewById(R.id.billingGroup);
        billingGroupErr = view.findViewById(R.id.billingGroupErr);
        cameraSerial = view.findViewById(R.id.cameraSerialNumET);
        date = view.findViewById(R.id.date);
        dateErr = view.findViewById(R.id.dateErr);
        fabRegisterCabin = view.findViewById(R.id.fabRegisterCabin);

        userTypeContainer = view.findViewById(R.id.userTypeContainer);
        usageChargeTypeContainer = view.findViewById(R.id.usageChargeTypeContainer);
        urinalCountContainer = view.findViewById(R.id.urinalCountContainer);
        bwtCapacityContainer = view.findViewById(R.id.bwtCapacityContainer);
        bwtLevelContainer = view.findViewById(R.id.bwtLevelContainer);
        fabRegisterCabin.setOnClickListener(this);
        setUpUi();

        return view;
    }

    private void setUpUi() {
        bwtLevelContainer.setVisibility(View.GONE);
        bwtCapacityContainer.setVisibility(View.GONE);
        urinalCountContainer.setVisibility(View.GONE);
        usageChargeTypeContainer.setVisibility(View.GONE);
        userTypeContainer.setVisibility(View.GONE);
        fabRegisterCabin.setVisibility(View.GONE);
        bwtCapacityErr.setVisibility(View.GONE);
        bwtLevelErr.setVisibility(View.GONE);
        urinalCountErr.setVisibility(View.GONE);
        fabRegisterCabin.setVisibility(View.VISIBLE);

        stateName.setText(details.getStateName());
        districtName.setText(details.getDistrictName());
        cityName.setText(details.getCityName());
        complexName.setText(details.getComplexName());
        complexUuid.setText(details.getUuid());
        commissioning.setText(details.getCommissioningStatus());
        smartness.setText(details.getSmartness());
        maleWc.setText(details.getWCCountMale());
        femaleWc.setText(details.getWCCountFemale());
        pdWc.setText(details.getWCCountPD());

        cabinName.setText(thingDetails.Name);
        cabinType.setText(thingDetails.ThingType);

        if(thingDetails.ThingType.equals("BWT")) {
            bwtCapacityContainer.setVisibility(View.VISIBLE);
            bwtLevelContainer.setVisibility(View.VISIBLE);
            bwtCapacity.setText(thingDetails.AttributesMap.get("BWT_KLD"));
            bwtLevel.setText(thingDetails.AttributesMap.get("BWT_LVL"));
        }
        else if (thingDetails.ThingType.equals("URINAL")) {
            urinalCountContainer.setVisibility(View.VISIBLE);
            userTypeContainer.setVisibility(View.VISIBLE);
            usageChargeTypeContainer.setVisibility(View.VISIBLE);
            urinalCount.setText(thingDetails.AttributesMap.get("NO_OF_URINALS"));
            userType.setText(thingDetails.AttributesMap.get("USER_TYPE"));
            usageChargeType.setText(thingDetails.AttributesMap.get("USAGE_CHARGE"));
        }
        else if (thingDetails.ThingType.equals("WC")) {
            userType.setText(thingDetails.AttributesMap.get("USER_TYPE"));
            usageChargeType.setText(thingDetails.AttributesMap.get("USAGE_CHARGE"));
            userTypeContainer.setVisibility(View.VISIBLE);
            usageChargeTypeContainer.setVisibility(View.VISIBLE);
        }

        urinals.setText(details.getUrinals());
        urinalCabins.setText(details.getUrinalCabins());
        bwt.setText(details.getBwt());
        address.setText(details.getAddress());
        lat.setText(details.getLatitude());
        lon.setText(details.getLongitude());
        clientName.setText(details.getClient());
        billingGroup.setText(details.getBillingGroup());
        date.setText(details.getDate());
        cameraSerial.setText(thingDetails.AttributesMap.get("CAMERA_SERIAL_NUM"));

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == fabRegisterCabin.getId()) {
            viewModel.setCabinDetails(thingDetails);
            viewModel.setStepTwoCompleted(true);
            for(int i = 0; i < viewModel.getThingDetailsList().size(); i++) {
                viewModel.getThingDetailsList().get(i).isSelected = viewModel.getThingDetailsList().get(i).Name.equals(thingDetails.Name);
            }
            clickCallback.onClick();
            dismiss();
        }
    }

    public void setvViewmodel(EnrollDeviceViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public interface ClickCallback {
        void onClick();
    }

    public ClickCallback getClickCallback() {
        return clickCallback;
    }

    public void setClickCallback(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }
}
