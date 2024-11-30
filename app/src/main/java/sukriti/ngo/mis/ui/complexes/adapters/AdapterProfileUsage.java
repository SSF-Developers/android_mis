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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.ui.complexes.data.DisplayUsageProfile;
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData;
import sukriti.ngo.mis.ui.dashboard.data.UiResult;
import sukriti.ngo.mis.utils.SharedPrefsClient;

import static sukriti.ngo.mis.repository.utils.DateConverter.toDbDate;
import static sukriti.ngo.mis.repository.utils.DateConverter.toDbTime;
import static sukriti.ngo.mis.repository.utils.DateConverter.toDbTimestamp;
import static sukriti.ngo.mis.utils.Nomenclature.getCabinType;

public class AdapterProfileUsage extends RecyclerView.Adapter<AdapterProfileUsage.MyViewHolder> {

    //UI

    //Data
    private List<DisplayUsageProfile> mData;
    private Context mContext;
    private UiResult uiResult;

    public AdapterProfileUsage(Context mCtx, List<DisplayUsageProfile> mData) {
        this.mData = mData;
        this.mContext = mCtx;
        uiResult =new SharedPrefsClient(mContext).getUiResult();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        return 1;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_cabin_profile_row, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_cabin_profile_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String date = toDbDate(toDbTimestamp(mData.get(position).getUsageProfile().getDEVICE_TIMESTAMP()));
        String time = toDbTime(toDbTimestamp(mData.get(position).getUsageProfile().getDEVICE_TIMESTAMP()));

        if (uiResult.data.usage_charge_profile == "true") {
            holder.fields[4].setVisibility(View.VISIBLE);
        }else{
            holder.fields[4].setVisibility(View.GONE);
        }

        if (uiResult.data.air_dryer_profile == "true") {
            holder.fields[7].setVisibility(View.VISIBLE);
        }else{
            holder.fields[7].setVisibility(View.GONE);
        }

        if (uiResult.data.rfid_profile == "true") {
            holder.fields[15].setVisibility(View.VISIBLE);
        }else{
            holder.fields[15].setVisibility(View.GONE);

        }

        holder.fields[0].setText(date);
        holder.fields[1].setText(time);
//        holder.fields[0].setText(mData.get(position).getDate());
//        holder.fields[1].setText(mData.get(position).getTime());
        holder.fields[2].setText(getCabinType(mData.get(position).getUsageProfile().getSHORT_THING_NAME()));
        holder.fields[3].setText(mData.get(position).getUsageProfile().getDuration());
        holder.fields[4].setText(mData.get(position).getUsageProfile().getAmountcollected());
        holder.fields[5].setText(mData.get(position).getUsageProfile().getFeedback());
        holder.fields[6].setText(mData.get(position).getUsageProfile().getEntrytype());
        holder.fields[7].setText(mData.get(position).getUsageProfile().getAirdryer());
        holder.fields[8].setText(mData.get(position).getUsageProfile().getFantime());
        holder.fields[9].setText(mData.get(position).getUsageProfile().getFloorclean());
        holder.fields[10].setText(mData.get(position).getUsageProfile().getFullflush());
        holder.fields[11].setText(mData.get(position).getUsageProfile().getManualflush());
        holder.fields[12].setText(mData.get(position).getUsageProfile().getLighttime());
        holder.fields[13].setText(mData.get(position).getUsageProfile().getMiniflush());
        holder.fields[14].setText(mData.get(position).getUsageProfile().getPreflush());
        holder.fields[15].setText(mData.get(position).getUsageProfile().getRFID());
        holder.fields[16].setText(mData.get(position).getUsageProfile().getCLIENT());
        holder.fields[17].setText(mData.get(position).getUsageProfile().getCOMPLEX());
        holder.fields[18].setText(mData.get(position).getUsageProfile().getSTATE());
        holder.fields[19].setText(mData.get(position).getUsageProfile().getDISTRICT());
        holder.fields[20].setText(mData.get(position).getUsageProfile().getCITY());
    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount", "" + mData.size());
        return mData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        int[] ids = {R.id.field_01, R.id.field_02, R.id.field_03,
                R.id.field_04, R.id.field_05, R.id.field_06, R.id.field_07, R.id.field_08, R.id.field_09,
                R.id.field_10, R.id.field_11, R.id.field_12, R.id.field_13, R.id.field_14, R.id.field_15,
                R.id.field_16, R.id.field_17, R.id.field_18, R.id.field_19, R.id.field_20,R.id.field_21};
        TextView[] fields = new TextView[ids.length];

        public MyViewHolder(View itemView) {
            super(itemView);
            for (int i = 0; i < ids.length; i++) {
                fields[i] = itemView.findViewById(ids[i]);
            }
        }
    }
}
