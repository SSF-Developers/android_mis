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
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.repository.data.ComplexHealthStats;
import sukriti.ngo.mis.ui.complexes.data.CabinDetailsData;
import sukriti.ngo.mis.ui.complexes.interfaces.CabinSelectionHandler;
import sukriti.ngo.mis.utils.Nomenclature;

public class CabinListAdapter extends RecyclerView.Adapter<CabinListAdapter.MyViewHolder> {

    //UI

    //Data
    private List<CabinDetailsData> mData;
    private Context mContext;
    private CabinSelectionHandler selectionHandler;

    public CabinListAdapter(Context mCtx, List<CabinDetailsData> mData,CabinSelectionHandler selectionHandler) {
        this.mData = mData;
        this.mContext = mCtx;
        this.selectionHandler = selectionHandler;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 0 )
            view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.item_cabin,parent,false);
        else
            view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.item_cabin,parent,false);
        return new MyViewHolder (view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        CabinDetailsData item = mData.get(position);
//        SpannableString mwc = getLabel(item,Nomenclature.CABIN_TYPE_MWC);
//        loadInfo(mwc,holder.mwcCont,holder.mwc);

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);
        holder.propertiesList.setLayoutManager(gridLayoutManager);
        PropertiesListAdapter adapter = new PropertiesListAdapter(mContext, mData.get(position).displayProperties);
        holder.propertiesList.setAdapter(adapter);

        Log.i("connection", "onBindViewHolder: "+mData.get(position).ConnectionStatus);
        if (mData.get(position).ConnectionStatus.equalsIgnoreCase("ONLINE")) {
            holder.connectionStatus.setBackgroundColor(Color.GREEN);
        }else{
            holder.connectionStatus.setBackgroundColor(Color.RED);
        }
        holder.title.setText (mData.get (position).ShortThingName);
        holder.details.setOnClickListener(view -> selectionHandler.onCabinSelected(mData.get(position)));
    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount",""+mData.size ());
        return mData.size ();
    }

    public void updateStatus(List<CabinDetailsData> cabinDetailsData){
        this.mData = cabinDetailsData;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        Button details;
        TextView connectionStatus;
        RecyclerView propertiesList;

        public MyViewHolder(View itemView) {
            super (itemView);
            title = itemView.findViewById(R.id.title);
            details = itemView.findViewById(R.id.details);
            connectionStatus =itemView.findViewById(R.id.connectionstatus);
            propertiesList = itemView.findViewById(R.id.propertiesList);
        }
    }
}
