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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.repository.entity.QuickAccess;
import sukriti.ngo.mis.ui.complexes.adapters.PropertiesListAdapter;
import sukriti.ngo.mis.ui.complexes.data.CabinDetailsData;
import sukriti.ngo.mis.ui.complexes.data.PropertyNameValueData;
import sukriti.ngo.mis.ui.complexes.interfaces.CabinSelectionHandler;

public class QuickAccessAdapter extends RecyclerView.Adapter<QuickAccessAdapter.MyViewHolder> {

    //UI

    //Data
    private List<QuickAccess> mData;
    private Context mContext;
    private QuickAccessCabinSelectionHandler selectionHandler;

    public QuickAccessAdapter(Context mCtx, List<QuickAccess> mData, QuickAccessCabinSelectionHandler selectionHandler) {
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
        QuickAccess item = mData.get(position);
//        SpannableString mwc = getLabel(item,Nomenclature.CABIN_TYPE_MWC);
//        loadInfo(mwc,holder.mwcCont,holder.mwc);

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);
        holder.propertiesList.setLayoutManager(gridLayoutManager);
        List<PropertyNameValueData> displayProperties = new ArrayList<>();
        displayProperties.add(new PropertyNameValueData("Complex",item.getComplexName()));
        displayProperties.add(new PropertyNameValueData("Client",item.getClient()));
        displayProperties.add(new PropertyNameValueData("District",item.getStateCode() +"->"+item.getDistrictName()));
        displayProperties.add(new PropertyNameValueData("City",item.getCityName()));

        PropertiesListAdapter adapter = new PropertiesListAdapter(mContext,displayProperties);
        holder.propertiesList.setAdapter(adapter);

        holder.title.setText (mData.get (position).getShortThingName());
        holder.details.setText("Actions");
        holder.details.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionHandler.onCabinSelected(mData.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount",""+mData.size ());
        return mData.size ();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        Button details;
        RecyclerView propertiesList;

        public MyViewHolder(View itemView) {
            super (itemView);
            title = itemView.findViewById(R.id.title);
            details = itemView.findViewById(R.id.details);
            propertiesList = itemView.findViewById(R.id.propertiesList);
        }
    }

    public interface QuickAccessCabinSelectionHandler {
        public void onCabinSelected(QuickAccess data);
    }
}
