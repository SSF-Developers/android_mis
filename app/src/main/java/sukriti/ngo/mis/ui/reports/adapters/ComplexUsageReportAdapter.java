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
import android.graphics.Shader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.ui.dashboard.data.UiResult;
import sukriti.ngo.mis.ui.reports.ReportsViewModel;
import sukriti.ngo.mis.ui.reports.chartClient.BwtComparisionChartClient;
import sukriti.ngo.mis.ui.reports.chartClient.CollectionComparisonChartClient;
import sukriti.ngo.mis.ui.reports.chartClient.CollectionTimelineChartClient;
import sukriti.ngo.mis.ui.reports.chartClient.FeedbackDistributionChartClient;
import sukriti.ngo.mis.ui.reports.chartClient.TicketResolutionTimelineChartClient;
import sukriti.ngo.mis.ui.reports.chartClient.UpiComparisionChartClient;
import sukriti.ngo.mis.ui.reports.chartClient.UsageComparisonChartClient;
import sukriti.ngo.mis.ui.reports.data.UsageReportData;
import sukriti.ngo.mis.utils.SharedPrefsClient;
import sukriti.ngo.mis.utils.Utilities;

public class ComplexUsageReportAdapter extends RecyclerView.Adapter<ComplexUsageReportAdapter.MyViewHolder> {

    //UI
    //Data
    private ArrayList<UsageReportData> mData;
    private Context mContext;
    private UiResult uiResult;
    int sys = ReportsViewModel.Companion.getBOTHCHECKED();

    public ComplexUsageReportAdapter(Context mCtx, ArrayList<UsageReportData> mData) {
        this.mData = mData;
        this.mContext = mCtx;
        uiResult = new SharedPrefsClient(mContext).getUiResult();
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_report_complex_usage, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        UsageReportData complexUsageData = mData.get(position);

        holder.title.setText(complexUsageData.getComplex().getName());
        holder.subTitle.setText(complexUsageData.getComplex().getAddress());
        AdapterUsageData mAdapter = new AdapterUsageData(mContext, Utilities.getUsageReportRawData(complexUsageData));
        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(mContext, 1);
        holder.dataGrid.setLayoutManager(gridLayoutManager);
        holder.dataGrid.setAdapter(mAdapter);

        UsageComparisonChartClient mUsageComparisonChartClient = new UsageComparisonChartClient(mContext, holder.usageComparisonChart, complexUsageData);
        mUsageComparisonChartClient.setFormatter();

        FeedbackDistributionChartClient mFeedbackDistributionChartClient = new FeedbackDistributionChartClient(mContext, holder.feedbackDistributionChart, complexUsageData);
        mFeedbackDistributionChartClient.setFormatter();

        CollectionComparisonChartClient mCollectionComparisonChartClient = new CollectionComparisonChartClient(mContext, holder.collectionTimelineChart, complexUsageData);
        mCollectionComparisonChartClient.setFormatter();

        UpiComparisionChartClient upiComparisionChartClient = new UpiComparisionChartClient(mContext, holder.upiCollectionTimelineChart, complexUsageData);
        upiComparisionChartClient.setFormatter();

        BwtComparisionChartClient bwtComparisionChartClient = new BwtComparisionChartClient(mContext, holder.bwtCollectionTimelineChart, complexUsageData);
        bwtComparisionChartClient.setFormatter();

        Log.i("gg", "onBindViewHolder: " + sys);

        if (sys == ReportsViewModel.Companion.getBOTHCHECKED()) {
            Log.i("gg", "onBindViewHolder:  " + sys);

            holder.usageCard.setVisibility(View.VISIBLE);
            feedbackCardVisibility(holder.feedbackCard,true);
            collectionCardVisibility(holder.collectionCard,true);
            collectionCardVisibility(holder.upiCard,true);
//            holder.bwtCard.setVisibility(View.VISIBLE);
            bwtCardVisibility(holder.bwtCard,true);

//        TicketResolutionTimelineChartClient mTicketResolutionTimelineChartClient = new TicketResolutionTimelineChartClient(mContext,holder.ticketResolutionTimeline,complexUsageData);
//        mTicketResolutionTimelineChartClient.setFormatter();
        } else if (sys == ReportsViewModel.Companion.getTOILETCHECKED()) {
            Log.i("gg", "onBindViewHolder:  " + sys);

            holder.usageCard.setVisibility(View.VISIBLE);
            feedbackCardVisibility(holder.feedbackCard,true);
            collectionCardVisibility(holder.collectionCard,true);
            collectionCardVisibility(holder.upiCard,true);
//            holder.bwtCard.setVisibility(View.GONE);
            bwtCardVisibility(holder.bwtCard,false);

        } else if (sys == ReportsViewModel.Companion.getBWTCHECKED()) {
            Log.i("gg", "onBindViewHolder:  " + sys);

            holder.usageCard.setVisibility(View.GONE);
            feedbackCardVisibility(holder.feedbackCard,false);
            collectionCardVisibility(holder.collectionCard,false);
            collectionCardVisibility(holder.upiCard,false);
//            holder.bwtCard.setVisibility(View.VISIBLE);
            bwtCardVisibility(holder.bwtCard,true);
        }

        Log.i("gg", "average_feedback:  " + uiResult.data.average_feedback);


        Log.i("gg", "collection_stats:  " + uiResult.data.collection_stats);

    }

    void feedbackCardVisibility(View view, Boolean visibility) {
        if (uiResult.data.average_feedback.equalsIgnoreCase("true") && visibility) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    void collectionCardVisibility(View view, Boolean visibility) {
        if (uiResult.data.collection_stats.equalsIgnoreCase("true") && visibility) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
    void bwtCardVisibility(View view, Boolean visibility) {
        if (uiResult.data.bwt_stats.equalsIgnoreCase("true") && visibility) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    public void updateSys(int sys) {
        this.sys = sys;
        Log.i("gg", "updateSys: ");
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, subTitle;
        ImageView icon;
        RecyclerView dataGrid;
        BarChart usageComparisonChart;
        BarChart feedbackDistributionChart;
        BarChart collectionTimelineChart;
        BarChart upiCollectionTimelineChart;
        BarChart bwtCollectionTimelineChart;
        LineChart ticketResolutionTimeline;

        LinearLayout usageCard;
        LinearLayout feedbackCard;
        LinearLayout collectionCard;
        LinearLayout upiCard;
        LinearLayout bwtCard;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            subTitle = itemView.findViewById(R.id.subTitle);
            dataGrid = itemView.findViewById(R.id.dataGrid);
            usageComparisonChart = itemView.findViewById(R.id.usageComparison).findViewById(R.id.chart);
            feedbackDistributionChart = itemView.findViewById(R.id.feedbackDistribution).findViewById(R.id.chart);
            collectionTimelineChart = itemView.findViewById(R.id.collectionTimeline).findViewById(R.id.chart);
            upiCollectionTimelineChart = itemView.findViewById(R.id.upiCollectionTimeline).findViewById(R.id.chart);
            bwtCollectionTimelineChart = itemView.findViewById(R.id.bwtTimeline).findViewById(R.id.chart);
//            ticketResolutionTimeline = itemView.findViewById(R.id.ticketResolutionTimeline).findViewById(R.id.chart);

            usageCard = itemView.findViewById(R.id.usageCard);
            feedbackCard = itemView.findViewById(R.id.feedbackCard);
            collectionCard = itemView.findViewById(R.id.collectionCard);
            upiCard = itemView.findViewById(R.id.upiCard);
            bwtCard = itemView.findViewById(R.id.bwtCard);


        }
    }

}
