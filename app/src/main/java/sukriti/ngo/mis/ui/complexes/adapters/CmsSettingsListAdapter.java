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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData;
import sukriti.ngo.mis.utils.Nomenclature;

public class CmsSettingsListAdapter extends RecyclerView.Adapter<CmsSettingsListAdapter.MyViewHolder> {

    //UI

    //Data
    final int TYPE_TIMER = 0;
    final int TYPE_SPINNER_ENABLED = 1;
    final int TYPE_COUNT = 2;
    final int TYPE_NONE = -1;
    private String inputData;
    private List<PropertyNameValueData> mData;
    private Context mContext;
    //Handlers
    private ChangeHandler changeHandler;

    public CmsSettingsListAdapter(Context mCtx, List<PropertyNameValueData> mData) {
        this.mData = mData;
        //inputData = Base64.encodeToString(new Gson().toJson(mData).getBytes(), Base64.DEFAULT);
        inputData = new Gson().toJson(mData);
        this.mContext = mCtx;
    }

    public void setChangeHandler(ChangeHandler changeHandler) {
        this.changeHandler = changeHandler;
    }

    @Override
    public int getItemViewType(int position) {
        Log.i("__type ",position + ": "+getRowType(position));
        return getRowType(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == TYPE_TIMER )
            view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.element_properties_timer,parent,false);
        else if(viewType == TYPE_SPINNER_ENABLED )
            view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.element_properties_spinner,parent,false);
        else if(viewType == TYPE_COUNT )
            view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.element_properties_count,parent,false);
        else
            view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.element_properties_spinner,parent,false);
        return new MyViewHolder (view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        int rowType = getRowType(position);
        holder.title.setText (mData.get (position).Name);

        if(rowType == TYPE_TIMER){
            holder.time.getEditText().setText(mData.get(position).Value);
            holder.time.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mData.get(position).Value = s.toString();

                    if (hasDataChanged())
                        changeHandler.onChange(mData);
                }
            });
        }
        else if(rowType == TYPE_COUNT){
            holder.time.getEditText().setText(mData.get(position).Value);
            holder.time.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mData.get(position).Value = s.toString();

                    if (hasDataChanged())
                        changeHandler.onChange(mData);
                }
            });
        }
        else if(rowType == TYPE_SPINNER_ENABLED){

            ArrayAdapter criticalAdapter = new ArrayAdapter(mContext, R.layout.item_config_selection, Nomenclature.getCmsConfigOptions());
            holder.options.setAdapter(criticalAdapter);
            holder.options.setSelection(getEnabedSelection(mData.get(position).Value));
            holder.options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String name = mData.get(position).Name;
                    Log.i("_defaultSelect",name + " "+i+": "+(i==1?"ic_status_fault":"ic_status_working"));

                    String selection = Nomenclature.getCmsConfigOptions().get(i);
                    mData.get(position).Value = selection;

                    if (i == 0)
                        holder.icon.setBackgroundResource(R.drawable.ic_status_working);

                    if (i == 1)
                        holder.icon.setBackgroundResource(R.drawable.ic_status_fault);

                    if (hasDataChanged())
                        changeHandler.onChange(mData);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount",""+mData.size ());
        return mData.size ();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title,value;
        Spinner options;
        ImageView icon;
        TextInputLayout time;

        public MyViewHolder(View itemView) {
            super (itemView);
            title = itemView.findViewById(R.id.label);
            
            options = itemView.findViewById(R.id.options);
            icon = itemView.findViewById(R.id.icon);
            
            time = itemView.findViewById(R.id.time);
        }
    }

    private int getRowType(int position){
        if(mData.get(position).Name.toLowerCase().contains("Timer".toLowerCase()) ||
                mData.get(position).Name.toLowerCase().contains("Time".toLowerCase()) ||
                mData.get(position).Name.toLowerCase().contains("Timmer".toLowerCase()))
            return TYPE_TIMER;
        else if(mData.get(position).Name.toLowerCase().contains("Enabled".toLowerCase()) ||
                mData.get(position).Name.toLowerCase().contains("Auto Pre Flush".toLowerCase()))
            return TYPE_SPINNER_ENABLED;
        else if(mData.get(position).Name.toLowerCase().contains("Floor CLean Count".toLowerCase()) )
            return TYPE_COUNT;
        else
            return TYPE_NONE;
    }

    private int getEnabedSelection(String selection){
        List<String> options = Nomenclature.getCmsConfigOptions();

        int index = 0;
        int selectedIndex = 0;
        for(String item : options){
            if(selection.compareToIgnoreCase(item)==0)
                selectedIndex = index;
            index++;
        }

        return selectedIndex;
    }

    private boolean hasDataChanged() {
        //String currentData = Base64.encodeToString(new Gson().toJson(mData).getBytes(), Base64.DEFAULT);
        String currentData = new Gson().toJson(mData);
        if (currentData.equals(inputData)) {
            return false;
        }
        else {
            return true;
        }
    }

    public interface ChangeHandler {
        public void onChange(List<PropertyNameValueData> mData);
    }
}
