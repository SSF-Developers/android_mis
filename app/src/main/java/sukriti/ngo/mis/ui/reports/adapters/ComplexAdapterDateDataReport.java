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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.ui.reports.data.UsageReportData;

public class ComplexAdapterDateDataReport extends RecyclerView.Adapter<ComplexAdapterDateDataReport.MyViewHolder> {

    //UI
    //Data
    private ArrayList<UsageReportData> mData;
    private Context mContext;

    public ComplexAdapterDateDataReport(Context mCtx, ArrayList<UsageReportData> mData) {
        this.mData = mData;
        this.mContext = mCtx;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_complex_report_date_data, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        UsageReportData complexUsageData = mData.get(position);

        holder.title.setText(complexUsageData.getComplex().getName());
        holder.subTitle.setText(complexUsageData.getComplex().getAddress());
        DateDataReportAdapter mAdapter = new DateDataReportAdapter(complexUsageData);
        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(mContext, 1);
        holder.dataGrid.setLayoutManager(gridLayoutManager);
        holder.dataGrid.setAdapter(mAdapter);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView labelHolder;
        TextView title,subTitle;
        ImageView icon;
        RecyclerView dataGrid;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            subTitle = itemView.findViewById(R.id.subTitle);
            dataGrid = itemView.findViewById(R.id.dataGrid);

            ((TextView)itemView.findViewById(R.id.total).findViewById(R.id.header)).setText("Total");
            ((TextView)itemView.findViewById(R.id.total).findViewById(R.id.field_01)).setText("Usage");
            ((TextView)itemView.findViewById(R.id.total).findViewById(R.id.field_02)).setText("Collection");
            ((TextView)itemView.findViewById(R.id.mwc).findViewById(R.id.header)).setText("Male WC");
            ((TextView)itemView.findViewById(R.id.mwc).findViewById(R.id.field_01)).setText("Usage");
            ((TextView)itemView.findViewById(R.id.mwc).findViewById(R.id.field_02)).setText("Collection");
            ((TextView)itemView.findViewById(R.id.fwc).findViewById(R.id.header)).setText("Female WC");
            ((TextView)itemView.findViewById(R.id.fwc).findViewById(R.id.field_01)).setText("Usage");
            ((TextView)itemView.findViewById(R.id.fwc).findViewById(R.id.field_02)).setText("Collection");
            ((TextView)itemView.findViewById(R.id.pwc).findViewById(R.id.header)).setText("PD WC");
            ((TextView)itemView.findViewById(R.id.pwc).findViewById(R.id.field_01)).setText("Usage");
            ((TextView)itemView.findViewById(R.id.pwc).findViewById(R.id.field_02)).setText("Collection");
            ((TextView)itemView.findViewById(R.id.mur).findViewById(R.id.header)).setText("Male Urinals");
            ((TextView)itemView.findViewById(R.id.mur).findViewById(R.id.field_01)).setText("Usage");
            ((TextView)itemView.findViewById(R.id.mur).findViewById(R.id.field_02)).setText("Collection");
            ((TextView)itemView.findViewById(R.id.tickets).findViewById(R.id.header)).setText("Tickets");
            ((TextView)itemView.findViewById(R.id.tickets).findViewById(R.id.field_01)).setText("Raised#");
            ((TextView)itemView.findViewById(R.id.tickets).findViewById(R.id.field_02)).setText("Resolved#");
        }
    }
}
