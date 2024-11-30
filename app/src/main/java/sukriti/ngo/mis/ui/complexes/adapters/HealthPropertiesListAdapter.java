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
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData;
import sukriti.ngo.mis.ui.dashboard.data.UiResult;
import sukriti.ngo.mis.utils.SharedPrefsClient;

public class HealthPropertiesListAdapter extends RecyclerView.Adapter<HealthPropertiesListAdapter.MyViewHolder> {

    //UI

    //Data
    private List<PropertyNameValueData> mData;
    private Context mContext;
    private UiResult uiResult;

    public HealthPropertiesListAdapter(Context mCtx, List<PropertyNameValueData> mData) {
        this.mData = mData;
        this.mContext = mCtx;
        uiResult =new SharedPrefsClient(mContext).getUiResult();
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_health_properties, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_health_properties, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Boolean display = true;
        holder.title.setText(mData.get(position).Name);
        holder.value.setText(mData.get(position).Value);

        setStatusIcon(holder.icon,mData.get(position).Value);
        Log.i("cabinDetails", "onBindViewHolder Name: "+mData.get(position).Name);
        Log.i("cabinDetails", "onBindViewHolder Value: "+mData.get(position).Value);
       //holder.value.setTextColor(getTextColor(mData.get(position).Value));

        if (uiResult.data.air_dryer_health =="false" && mData.get(position).Name =="Air Dryer"){
            holder.title.setVisibility(View.GONE);
            holder.value.setVisibility(View.GONE);
            holder.icon.setVisibility(View.GONE);
        }else if (uiResult.data.tap_health =="false" && mData.get(position).Name =="Tap"){
            holder.title.setVisibility(View.GONE);
            holder.value.setVisibility(View.GONE);
            holder.icon.setVisibility(View.GONE);
        }else if (uiResult.data.choke_health =="false" && mData.get(position).Name =="Choke"){
            holder.title.setVisibility(View.GONE);
            holder.value.setVisibility(View.GONE);
            holder.icon.setVisibility(View.GONE);
        }else {
            holder.title.setVisibility(View.VISIBLE);
            holder.value.setVisibility(View.VISIBLE);
            holder.icon.setVisibility(View.VISIBLE);
        }
    }

    private void setStatusIcon(ImageView view, String value) {
        if (value.compareToIgnoreCase("Feature is not installed") == 0)
            view.setBackgroundResource(R.drawable.ic_status_feature_not_available);
        else if (value.compareToIgnoreCase("Fault detected in the unit") == 0)
            view.setBackgroundResource(R.drawable.ic_status_fault);
        else if (value.compareToIgnoreCase("Unit working fine") == 0)
            view.setBackgroundResource(R.drawable.ic_status_working);
    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount", "" + mData.size());
        return mData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, value;
        ImageView icon;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.label);
            value = itemView.findViewById(R.id.value);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}
