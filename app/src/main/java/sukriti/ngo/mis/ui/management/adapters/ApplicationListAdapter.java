package sukriti.ngo.mis.ui.management.adapters;

import android.animation.LayoutTransition;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.ArrayList;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.ui.management.EnrollDevice.Fragments.AddNewApplicationDialog;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.ClickCallback;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.DeleteAppCallback;
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.EditAppClickCallback;
import sukriti.ngo.mis.ui.management.data.device.Application;
import sukriti.ngo.mis.ui.management.fargments.CreateNewPolicyDialog;

public class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListAdapter.ViewHolder> {

    ArrayList<Application> list;
    DeleteAppCallback deleteCallback;
    private boolean showDelete = true;
    ClickCallback clickCallback;
    EditAppClickCallback editAppClickCallback;
    CreateNewPolicyDialog.RadioButtonSelection radioButtonSelectionCallback;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.application_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.installType.setText(list.get(i).getInstallType());
        viewHolder.packageName.setText(list.get(i).getPackageName());
        viewHolder.autoUpdateMode.setText(list.get(i).getAutoUpdateMode());
        viewHolder.defaultPermissionPolicy.setText(list.get(i).getDefaultPermissionPolicy());
        viewHolder.userControlSetting.setText(list.get(i).getUserControlSettings());
        if(!showDelete) {
            Log.i("showDelDebug", "showDelete = false");
            viewHolder.deleteApp.setVisibility(View.GONE);
        } else {
            Log.i("showDelDebug", "showDelete = true");
        }
        viewHolder.deleteApp.setOnClickListener(view -> {
            // Notify CreateNewPolicyDialog to delete application from the list
            deleteCallback.deleteApp(i);
        });


        viewHolder.layout.setOnClickListener( view -> {
            if(viewHolder.autoUpdateMode.getVisibility() == View.GONE) {
                viewHolder.autoUpdateMode.setVisibility(View.VISIBLE);
            } else {
                viewHolder.autoUpdateMode.setVisibility(View.GONE);
            }

            if(viewHolder.defaultPermissionPolicy.getVisibility() == View.GONE) {
                viewHolder.defaultPermissionPolicy.setVisibility(View.VISIBLE);
            } else {
                viewHolder.defaultPermissionPolicy.setVisibility(View.GONE);
            }

            if(viewHolder.userControlSetting.getVisibility() == View.GONE) {
                viewHolder.userControlSetting.setVisibility(View.VISIBLE);
            } else {
                viewHolder.userControlSetting.setVisibility(View.GONE);
            }
            TransitionManager.beginDelayedTransition(viewHolder.layout,new AutoTransition());

            clickCallback.onClick(viewHolder.getAdapterPosition());

        });

        viewHolder.editAppButton.setOnClickListener(view -> {
            editAppClickCallback.edit(list.get(i), i);
        });

        if(list.get(i).getShowRadioButton()) {
            viewHolder.deleteApp.setVisibility(View.GONE);
            viewHolder.editAppButton.setVisibility(View.GONE);
            viewHolder.radioButton.setVisibility(View.VISIBLE);
        } else if (showDelete) {
            viewHolder.deleteApp.setVisibility(View.VISIBLE);
            viewHolder.editAppButton.setVisibility(View.VISIBLE);
            viewHolder.radioButton.setVisibility(View.GONE);
        } else {
            viewHolder.deleteApp.setVisibility(View.GONE);
            viewHolder.editAppButton.setVisibility(View.GONE);
            viewHolder.radioButton.setVisibility(View.GONE);
        }

        viewHolder.radioButton.setChecked(list.get(i).isKioskSelected());

        viewHolder.radioButton.setOnClickListener( view -> {
            radioButtonSelectionCallback.onKioskAppSelected(i);
        } );

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public ArrayList<Application> getList() {
        return list;
    }

    public void setList(ArrayList<Application> list) {
        this.list = list;
    }

    public void setDeleteCallback(DeleteAppCallback deleteCallback) {
        this.deleteCallback = deleteCallback;
    }

    public void setClickCallback(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public void setEditAppClickCallback(EditAppClickCallback editAppClickCallback) {
        this.editAppClickCallback = editAppClickCallback;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView packageName, installType, autoUpdateMode, defaultPermissionPolicy, userControlSetting;
        ImageButton deleteApp;
        ConstraintLayout layout;
        MaterialRadioButton radioButton;
        ImageButton editAppButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            packageName = itemView.findViewById(R.id.packageNameTv);
            installType = itemView.findViewById(R.id.installTypeTv);
            autoUpdateMode = itemView.findViewById(R.id.autoUpdateTv);
            defaultPermissionPolicy = itemView.findViewById(R.id.defaultPermissionPolicyTv);
            userControlSetting = itemView.findViewById(R.id.userControlSettingTv);
            layout = itemView.findViewById(R.id.layout);
            deleteApp = itemView.findViewById(R.id.deleteAppButton);
            radioButton = itemView.findViewById(R.id.isKioskApp);
            layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
            editAppButton = itemView.findViewById(R.id.editAppButton);
        }

    }

    public void setRadioButtonSelectionCallback(CreateNewPolicyDialog.RadioButtonSelection radioButtonSelectionCallback) {
        this.radioButtonSelectionCallback = radioButtonSelectionCallback;
    }

    public void showDelete(boolean showDelete) {
        Log.i("showDelDebug", "showDelete() " + showDelete);
        this.showDelete = showDelete;
        Log.i("showDelDebug", "showDelete() this.showDelete" + this.showDelete);
    }


}
