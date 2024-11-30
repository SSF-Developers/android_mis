/*
 * Copyright 2013-2017 Amazon.com,
 * Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the
 * License. A copy of the License is located at
 *
 *      http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, express or implied. See the License
 * for the specific language governing permissions and
 * limitations under the License.
 */

package sukriti.ngo.mis.ui.complexes.adapters;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.dataModel.dynamo_db.Country;
import sukriti.ngo.mis.ui.administration.AdministrationViewModel;
import sukriti.ngo.mis.ui.administration.interfaces.TreeInteractionListener;
import sukriti.ngo.mis.ui.complexes.data.MisCommand;
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData;
import sukriti.ngo.mis.ui.login.data.UserProfile;
import sukriti.ngo.mis.utils.Nomenclature;

public class CommandsAdapter extends RecyclerView.Adapter<CommandsAdapter.MyViewHolder> {

    //UI
    private MyViewHolder selectedHolder;
    private int selectedIndex;

    //Data
    private ArrayList<MisCommand> mData;
    private Context mContext;
    private CommandSubmitHandler submitHandler;

    public CommandsAdapter(Context mCtx, ArrayList<MisCommand> mData) {
        this.mData = mData;
        this.mContext = mCtx;
    }

    public void setCommandSubmitHandler(CommandSubmitHandler handler){
        submitHandler = handler;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_command, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.title.setText(mData.get(position).getName());
        holder.timerLabel.setText("Duration");
        holder.actionLabel.setText("Action");
        holder.overrideLabel.setText("Override");
        holder.time.getEditText().setText(mData.get(position).getDuration());

        if(mData.get(position).getAction().compareToIgnoreCase("-1")==0)
            holder.actionContainer.setVisibility(View.GONE);
        if(mData.get(position).getDuration().compareToIgnoreCase("-1")==0)
            holder.timerContainer.setVisibility(View.GONE);

        holder.time.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mData.get(position).setDuration(s.toString());
            }
        });

        holder.title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.executeContainer.getVisibility() == View.VISIBLE)
                    collapse(holder, position);
                else
                    expand(holder, position);

            }
        });

        ArrayAdapter actionAdapter = new ArrayAdapter(mContext, R.layout.item_config_selection, Nomenclature.getActionOptions());
        holder.actionOptions.setAdapter(actionAdapter);
        holder.actionOptions.setSelection(getSelectedIndex(mData.get(position).getAction(),Nomenclature.getActionOptions()));
        holder.actionOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = Nomenclature.getActionOptions().get(i);
                mData.get(position).setAction(selection);

                if (i == 0)
                    holder.actionIcon.setBackgroundResource(R.drawable.ic_status_fault);

                if (i == 1)
                    holder.actionIcon.setBackgroundResource(R.drawable.ic_status_working);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ArrayAdapter overrideAdapter = new ArrayAdapter(mContext, R.layout.item_config_selection, Nomenclature.getOverrideOptions());
        holder.overrideOptions.setAdapter(overrideAdapter);
        holder.overrideOptions.setSelection(getSelectedIndex(mData.get(position).getAction(),Nomenclature.getOverrideOptions()));
        holder.overrideOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = Nomenclature.getOverrideOptions().get(i);
                mData.get(position).setOverride(selection);

                if (i == 0)
                    holder.overrideIcon.setBackgroundResource(R.drawable.ic_status_fault);

                if (i == 1)
                    holder.overrideIcon.setBackgroundResource(R.drawable.ic_status_working);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        holder.submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MisCommand command = mData.get(position);
                submitHandler.onSubmit(command);
                selectedHolder = holder;
                selectedIndex = position;
                Log.i("__command",""+command.getName() +" | "+command.getCode()+", "+command.getDuration()+"Sec " +"|"+command.getAction() +"|"+command.getOverride());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        Button submit;
        TextView title;
        ImageView icon;
        LinearLayout executeContainer, timerContainer, actionContainer, overrideContainer;
        Spinner actionOptions, overrideOptions;
        TextView actionLabel, overrideLabel, timerLabel;
        ImageView actionIcon, overrideIcon;
        TextInputLayout time;


        public MyViewHolder(View itemView) {
            super(itemView);
            submit = itemView.findViewById(R.id.submit);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            executeContainer = itemView.findViewById(R.id.executeContainer);
            timerContainer = executeContainer.findViewById(R.id.timer);
            actionContainer = executeContainer.findViewById(R.id.action);
            overrideContainer = executeContainer.findViewById(R.id.override);

            actionLabel = actionContainer.findViewById(R.id.label);
            actionIcon = actionContainer.findViewById(R.id.icon);
            actionOptions = actionContainer.findViewById(R.id.options);

            overrideLabel = overrideContainer.findViewById(R.id.label);
            overrideIcon = overrideContainer.findViewById(R.id.icon);
            overrideOptions = overrideContainer.findViewById(R.id.options);

            timerLabel = timerContainer.findViewById(R.id.label);
            time = timerContainer.findViewById(R.id.time);

        }
    }

    private void expand(MyViewHolder holder, int index) {
        holder.executeContainer.setVisibility(View.VISIBLE);
        holder.icon.setImageResource(R.drawable.ic_collapse);
        holder.submit.setVisibility(View.VISIBLE);
    }

    private void collapse(MyViewHolder holder, int index) {
        holder.executeContainer.setVisibility(View.GONE);
        holder.icon.setImageResource(R.drawable.ic_expand);
        holder.submit.setVisibility(View.INVISIBLE);
    }

    private int getSelectedIndex(String Selection,List<String> options) {
        int index = 0;
        int selectedIndex = 0;
        for (String item : options) {
            if (Selection.compareToIgnoreCase(item) == 0)
                selectedIndex = index;
            index++;
        }
        return selectedIndex;
    }

    public void respondToSuccessfulSubmit(){
        collapse(selectedHolder,selectedIndex);
    }

    public interface CommandSubmitHandler {
        public void onSubmit(MisCommand command);
    }
}
