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
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData;

public class BwtSettingsListAdapter extends RecyclerView.Adapter<BwtSettingsListAdapter.MyViewHolder> {

    //UI

    //Data
    final int TYPE_INPUT = 0;

    private List<PropertyNameValueData> mData;
    private Context mContext;
    private String inputData;
    //Handlers
    private ChangeHandler changeHandler;


    public BwtSettingsListAdapter(Context mCtx, List<PropertyNameValueData> mData) {
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
        return getRowType(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == TYPE_INPUT )
            view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.element_properties_input,parent,false);
        else
            view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.element_properties_input,parent,false);
        return new MyViewHolder (view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        int rowType = getRowType(position);
        holder.title.setText (mData.get (position).Name);

        if(rowType == TYPE_INPUT){
            holder.input.getEditText().setText(mData.get(position).Value);
            holder.input.getEditText().addTextChangedListener(new TextWatcher() {
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

    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount",""+mData.size ());
        return mData.size ();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        Spinner options;
        TextInputLayout input;

        public MyViewHolder(View itemView) {
            super (itemView);
            title = itemView.findViewById(R.id.label);
            
            options = itemView.findViewById(R.id.options);

            input = itemView.findViewById(R.id.input);
        }
    }

    private int getRowType(int position){
            return TYPE_INPUT;
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
