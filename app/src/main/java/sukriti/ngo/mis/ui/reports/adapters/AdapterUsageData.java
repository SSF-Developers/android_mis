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

package sukriti.ngo.mis.ui.reports.adapters;


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
import sukriti.ngo.mis.ui.reports.data.UsageReportRawData;

import static sukriti.ngo.mis.utils.Nomenclature.getCabinType;
import static sukriti.ngo.mis.utils.Utilities.getDisplayAmount;

public class AdapterUsageData extends RecyclerView.Adapter<AdapterUsageData.MyViewHolder> {

    //UI

    //Data
    private List<UsageReportRawData> mData;
    private Context mContext;

    public AdapterUsageData(Context mCtx, List<UsageReportRawData> mData) {
        this.mData = mData;
        this.mContext = mCtx;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mData.size()-1)
            return 0;
        return 1;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_report_data_row_summary, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_report_data_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.fields[0].setText(mData.get(position).getCabinType());
//        holder.fields[1].setText(""+mData.get(position).getCabinCount());

        Float usage = Float.valueOf(mData.get(position).getUsage());
        String usageStr = getDisplayAmount(usage);
        holder.fields[1].setText(usageStr);

        String feedbackStr = String.format("%.1f", mData.get(position).getFeedback());
        holder.fields[2].setText(feedbackStr);

        String amountStr = mContext.getString(R.string.Rs)+"" + getDisplayAmount(mData.get(position).getCollection());
        holder.fields[3].setText(amountStr);

        String raisedTicketCount = ""+mData.get(position).getRaisedTicketCount();
        holder.fields[4].setText(raisedTicketCount);
    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount", "" + mData.size());
        return mData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        int[] ids = {R.id.field_01, R.id.field_02, R.id.field_03,
                R.id.field_04, R.id.field_05};
        TextView[] fields = new TextView[ids.length];

        public MyViewHolder(View itemView) {
            super(itemView);
            for (int i = 0; i < ids.length; i++) {
                fields[i] = itemView.findViewById(ids[i]);
            }
        }
    }
}
