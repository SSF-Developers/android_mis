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
import sukriti.ngo.mis.ui.complexes.data.DisplayResetProfile;
import sukriti.ngo.mis.ui.complexes.data.DisplayUsageProfile;

import static sukriti.ngo.mis.repository.utils.DateConverter.toDbDate;
import static sukriti.ngo.mis.repository.utils.DateConverter.toDbTime;
import static sukriti.ngo.mis.repository.utils.DateConverter.toDbTimestamp;
import static sukriti.ngo.mis.utils.Nomenclature.getCabinType;

public class AdapterProfileReset extends RecyclerView.Adapter<AdapterProfileReset.MyViewHolder> {

    //UI

    //Data
    private List<DisplayResetProfile> mData;
    private Context mContext;

    public AdapterProfileReset(Context mCtx, List<DisplayResetProfile> mData) {
        this.mData = mData;
        this.mContext = mCtx;
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
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_reset_profile_row, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_reset_profile_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String date = toDbDate(toDbTimestamp(mData.get(position).getDate()));
        String time = toDbTime(toDbTimestamp(mData.get(position).getTime()));
        holder.fields[0].setText(date);
        holder.fields[1].setText(time);
//        holder.fields[0].setText(mData.get(position).getDate());
//        holder.fields[1].setText(mData.get(position).getTime());
        holder.fields[2].setText(getCabinType(mData.get(position).getResetProfile().getSHORT_THING_NAME()));
        holder.fields[3].setText(""+mData.get(position).getResetProfile().getUserId());
        holder.fields[4].setText(""+mData.get(position).getResetProfile().getBoardId());
        holder.fields[5].setText(""+mData.get(position).getResetProfile().getResetsource());
        holder.fields[6].setText(mData.get(position).getResetProfile().getCLIENT());
        holder.fields[7].setText(mData.get(position).getResetProfile().getCOMPLEX());
        holder.fields[8].setText(mData.get(position).getResetProfile().getSTATE());
        holder.fields[9].setText(mData.get(position).getResetProfile().getDISTRICT());
        holder.fields[10].setText(mData.get(position).getResetProfile().getCITY());
    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount", "" + mData.size());
        return mData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        int[] ids = {R.id.field_01, R.id.field_02, R.id.field_03,
                R.id.field_04, R.id.field_05, R.id.field_06, R.id.field_07, R.id.field_08, R.id.field_09,
                R.id.field_10, R.id.field_11};
        TextView[] fields = new TextView[ids.length];

        public MyViewHolder(View itemView) {
            super(itemView);
            for (int i = 0; i < ids.length; i++) {
                fields[i] = itemView.findViewById(ids[i]);
            }
        }
    }
}
