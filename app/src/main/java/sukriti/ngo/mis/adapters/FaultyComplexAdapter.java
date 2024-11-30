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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.dataModel.UserSelection;
import sukriti.ngo.mis.repository.data.ComplexHealthStats;
import sukriti.ngo.mis.utils.Nomenclature;

public class FaultyComplexAdapter extends RecyclerView.Adapter<FaultyComplexAdapter.MyViewHolder> {

    //UI

    //Data
    private List<ComplexHealthStats> mData;
    private Context mContext;

    public FaultyComplexAdapter(Context mCtx, List<ComplexHealthStats> mData) {
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
            view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.item_faulty_cabin,parent,false);
        else
            view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.item_faulty_cabin,parent,false);
        return new MyViewHolder (view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ComplexHealthStats item = mData.get(position);
        Log.i("myFaultyAdapter", position +". ComplexHealthStats: "+ new Gson().toJson(item));
        Log.i("myFaultyAdapter", "Calling getLabel for MWC");
        SpannableString mwc = getLabel(item,Nomenclature.CABIN_TYPE_MWC); // is for total
        Log.i("myFaultyAdapter", "Calling getLabel for FWC");
        SpannableString fwc = getLabel(item,Nomenclature.CABIN_TYPE_FWC);
        Log.i("myFaultyAdapter", "Calling getLabel for PWC");
        SpannableString pwc = getLabel(item,Nomenclature.CABIN_TYPE_PWC);
        Log.i("myFaultyAdapter", "Calling getLabel for MUR");
        SpannableString mur = getLabel(item,Nomenclature.CABIN_TYPE_MUR);
        Log.i("myFaultyAdapter", "Calling getLabel for BWT");
        SpannableString bwt = getLabel(item,Nomenclature.CABIN_TYPE_BWT);

        loadInfo(mwc,holder.mwcCont,holder.mwc);
/*
        loadInfo(fwc,holder.fwcCont,holder.fwc);
        loadInfo(pwc,holder.pwcCont,holder.pwc);
        loadInfo(mur,holder.murCont,holder.mur);
        loadInfo(bwt,holder.bwtCont,holder.bwt);
*/


        holder.title.setText (mData.get (position).getComplexName());
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
        TextView title,mwc,fwc,pwc,mur,bwt;
        LinearLayout mwcCont,fwcCont,pwcCont,murCont,bwtCont;
        FrameLayout surface;

        public MyViewHolder(View itemView) {
            super (itemView);
            title = itemView.findViewById(R.id.title);
            surface = itemView.findViewById(R.id.surface);
            mwc = itemView.findViewById(R.id.mwc);
            fwc = itemView.findViewById(R.id.fwc);
            pwc = itemView.findViewById(R.id.pwc);
            mur = itemView.findViewById(R.id.mur);
            bwt = itemView.findViewById(R.id.bwt);
            mwcCont = itemView.findViewById(R.id.mwcCont);
            fwcCont = itemView.findViewById(R.id.fwcCont);
            pwcCont = itemView.findViewById(R.id.pwcCont);
            murCont = itemView.findViewById(R.id.murCont);
            bwtCont = itemView.findViewById(R.id.bwtCont);
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
    private SpannableString getLabel(ComplexHealthStats item, String Type){
        String faultyCountStr;
        String info = "";
        SpannableString spanInfo;
        int faultyCount = 0;
        int totalCount = 0 ;
        Log.i("myFaultyAdapter", "Type: "+Type);
        if(Type.compareToIgnoreCase(Nomenclature.CABIN_TYPE_MWC)==0){
             faultyCount = item.getFaultyMwc();
             totalCount = item.getTotalMwc();
            Log.i("myFaultyAdapter", "getLabel: faultyCount Mwc : "+faultyCount);
        }else if(Type.compareToIgnoreCase(Nomenclature.CABIN_TYPE_FWC)==0){
            faultyCount = item.getFaultyFwc();
            totalCount = item.getTotalFwc();
            Log.i("myFaultyAdapter", "getLabel: faultyCount Fwc : "+faultyCount);
        }else if(Type.compareToIgnoreCase(Nomenclature.CABIN_TYPE_PWC)==0){
            faultyCount = item.getFaultyPwc();
            totalCount = item.getTotalPwc();
            Log.i("myFaultyAdapter", "getLabel: faultyCount Pwc : "+faultyCount);
        }else if(Type.compareToIgnoreCase(Nomenclature.CABIN_TYPE_MUR)==0){
            faultyCount = item.getFaultyMur();
            totalCount = item.getTotalMur();
            Log.i("myFaultyAdapter", "getLabel: faultyCount Mur : "+faultyCount);
        }else if(Type.compareToIgnoreCase(Nomenclature.CABIN_TYPE_BWT)==0){
            faultyCount = item.getFaultyBwt();
            totalCount = item.getTotalBwt();
            Log.i("myFaultyAdapter", "getLabel: faultyCount Bwt : "+faultyCount);
        }




        if(totalCount == 0){
            spanInfo = new SpannableString("");
            Log.i("faultyAdapter", "totalCount == 0 " +spanInfo);
        }
        else if(faultyCount!=0)
        {
            faultyCountStr = ""+faultyCount;
//            info = faultyCountStr+"/"+totalCount +" faulty " +Type +" units detected";
            info = faultyCountStr +" faulty Cabin(s) reported";
            Log.i("faultyAdapter", "faultyCount!=0: " +info);
            spanInfo = new SpannableString(info);

            spanInfo.setSpan(new RelativeSizeSpan(1.2f), 0, faultyCountStr.length(), 0);
            spanInfo.setSpan(new StyleSpan(Typeface.BOLD), 0, faultyCountStr.length(), 0);
            spanInfo.setSpan(new ForegroundColorSpan(Color.RED), 0, faultyCountStr.length(), 0);

            spanInfo.setSpan(new RelativeSizeSpan(1f), faultyCountStr.length()+1,info.length(), 0);
            spanInfo.setSpan(new StyleSpan(Typeface.NORMAL), faultyCountStr.length()+1,info.length(), 0);
            spanInfo.setSpan(new ForegroundColorSpan(Color.BLACK), faultyCountStr.length()+1,info.length(), 0);

        }else {
            spanInfo = new SpannableString("");
//            if(totalCount == 1)
//                info = "The only " +Type +" unit is working";
//            else if(totalCount == 2)
//                info = "The 2 " +Type +" units are working";
//            else
//                info = "All " +Type +" units working";
//
//            spanInfo = new SpannableString(info);
//
//            spanInfo.setSpan(new RelativeSizeSpan(1f), 0, info.length(),0);
//            spanInfo.setSpan(new StyleSpan(Typeface.NORMAL), 0, info.length(),0);
//            spanInfo.setSpan(new ForegroundColorSpan(Color.BLACK), 0,info.length(), 0);
        }

        Log.i("faultyAdapter", "getLabel: "+spanInfo);
        Log.i("myFaultyAdapter", "getLabel: "+spanInfo);
        return spanInfo;
    }
}
