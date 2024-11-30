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

package sukriti.ngo.mis.adapters;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.repository.data.ComplexHealthStats;
import sukriti.ngo.mis.repository.entity.Health;
import sukriti.ngo.mis.ui.dashboard.data.LowWaterComplex;
import sukriti.ngo.mis.utils.Nomenclature;

import static sukriti.ngo.mis.repository.utils.HLWHelper.isLowWaterLevel;

public class LowWaterLevelComplexAdapter extends RecyclerView.Adapter<LowWaterLevelComplexAdapter.MyViewHolder> {

    //UI

    //Data
//    private List<Health> mData;
    private ArrayList<LowWaterComplex> mData;
    private Context mContext;

    private final int LOW = 1;
    private final int GOOD = 2;
    private final int NOT_AVAILABLE = -1;

    private final String LOW_LABEL = "LOW";
    private final String GOOD_LABEL = "GOOD";
    private final String NOT_AVAILABLE_LABEL = "NOT AVAILABLE";

//    public LowWaterLevelComplexAdapter(Context mCtx, List<Health> mData) {
//        this.mData = mData;
//        this.mContext = mCtx;
//    }
    public LowWaterLevelComplexAdapter(Context mCtx, ArrayList<LowWaterComplex> mData) {
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
        if(viewType == 0 )
            view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.item_low_water_complex,parent,false);
        else
            view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.item_low_water_complex,parent,false);
        return new MyViewHolder (view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        LowWaterComplex item = mData.get(position);

            final String STATUS_BWT_NOT_AVAILABLE = "-1";
            final String STATUS_LOW = "0";
            loadInfo(getLabel("Water Level Detected",LOW),holder.freshCont,holder.fresh);
//
//            if(item.getFreshWaterLevel().compareToIgnoreCase(STATUS_LOW)==0){
//                loadInfo(getLabel("Fresh-water level",LOW),holder.freshCont,holder.fresh);
//            }else {
//                loadInfo(getLabel("Fresh-water level",GOOD),holder.freshCont,holder.fresh);
//            }
//
//            Log.i("_adapterWaterData",item.getCOMPLEX()+", "+item.getSHORT_THING_NAME()+": "+": fresh:"+item.getFreshWaterLevel()+", re-cycle: "+item.getRecycleWaterLevel());
//            if(item.getRecycleWaterLevel().compareToIgnoreCase(STATUS_LOW)==0){
//                loadInfo(getLabel("Recycled-water level",LOW),holder.recycledCont,holder.recycled);
//            }else if(item.getRecycleWaterLevel().compareToIgnoreCase(STATUS_BWT_NOT_AVAILABLE)==0){
//                loadInfo(getLabel("Recycled-water level",NOT_AVAILABLE),holder.recycledCont,holder.recycled);
//            }else {
//                loadInfo(getLabel("Recycled-water level",GOOD),holder.recycledCont,holder.recycled);
//            }


        holder.title.setText (mData.get (position).complex.name);
        holder.surface.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount",""+mData.size ());
        return mData.size ();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title,fresh,recycled;
        LinearLayout freshCont,recycledCont;
        FrameLayout surface;

        public MyViewHolder(View itemView) {
            super (itemView);
            title = itemView.findViewById(R.id.title);
            surface = itemView.findViewById(R.id.surface);
            fresh = itemView.findViewById(R.id.fresh);
            recycled = itemView.findViewById(R.id.recycled);
            freshCont = itemView.findViewById(R.id.freshCont);
            recycledCont = itemView.findViewById(R.id.recycledCont);
        }
    }

    private void loadInfo(SpannableString info, LinearLayout cont, TextView textView){
        if(info.length()>0){
            cont.setVisibility(View.VISIBLE);
            textView.setText(info);
        }else {
            cont.setVisibility(View.GONE);
        }
    }
    private SpannableString getLabel(String label, int status){
        label = label+": ";

        String statusStr = "";
        if(status == LOW)
            statusStr = LOW_LABEL;
        if(status == GOOD)
            statusStr = GOOD_LABEL;
        if(status == NOT_AVAILABLE)
            statusStr = NOT_AVAILABLE_LABEL;

        Log.i("_adapterWaterData",label+": "+statusStr+": "+status);

        SpannableString spanInfo;
        String totalString =  statusStr+"  " +label;
        spanInfo = new SpannableString(totalString);

        spanInfo.setSpan(new RelativeSizeSpan(1.0f), 0, label.length(), 0);
        spanInfo.setSpan(new StyleSpan(Typeface.NORMAL), 0, label.length(), 0);
        spanInfo.setSpan(new ForegroundColorSpan(Color.BLACK), 0, label.length(), 0);

        if(status == LOW){
            spanInfo.setSpan(new RelativeSizeSpan(1.2f), 0 ,statusStr.length(), 0);
            spanInfo.setSpan(new StyleSpan(Typeface.BOLD), 0,statusStr.length(), 0);
            spanInfo.setSpan(new ForegroundColorSpan(Color.RED), 0,statusStr.length(), 0);
        }else if(status == NOT_AVAILABLE){
            spanInfo.setSpan(new RelativeSizeSpan(.7f), label.length(),totalString.length(), 0);
            spanInfo.setSpan(new StyleSpan(Typeface.NORMAL), label.length(),totalString.length(), 0);
            spanInfo.setSpan(new ForegroundColorSpan(Color.BLUE), label.length(),totalString.length(), 0);
        }else {
            spanInfo.setSpan(new RelativeSizeSpan(1.2f), label.length(),totalString.length(), 0);
            spanInfo.setSpan(new StyleSpan(Typeface.BOLD), label.length(),totalString.length(), 0);
            spanInfo.setSpan(new ForegroundColorSpan(Color.GREEN), label.length(),totalString.length(), 0);
        }

        return spanInfo;
    }
}
