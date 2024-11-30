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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.dataModel.dynamo_db.City;
import sukriti.ngo.mis.ui.administration.AdministrationViewModel;
import sukriti.ngo.mis.ui.administration.data.TreeEdge;
import sukriti.ngo.mis.ui.administration.interfaces.TreeInteractionListener;

public class ComplexAdapterST extends RecyclerView.Adapter<ComplexAdapterST.MyViewHolder> {

    //UI

    //Data
    private City mData;
    private Context mContext;
    private int stateIndex;
    private int districtIndex;
    private int cityIndex;
    private TreeInteractionListener mTreeInteractionListener;

    public ComplexAdapterST(Context mCtx, City mData, int stateIndex, int districtIndex, int cityIndex, TreeInteractionListener mTreeInteractionListener) {
        this.mData = mData;
        this.mContext = mCtx;
        this.stateIndex = stateIndex;
        this.districtIndex = districtIndex;
        this.cityIndex = cityIndex;
        this.mTreeInteractionListener = mTreeInteractionListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.element_complex,parent,false);
        return new MyViewHolder (view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.title.setText (mData.getComplexes().get(position).getName());
        holder.title.setTextAppearance(mContext, R.style.AccessTree_Complex_SM);

        if(mData.getComplexes().get(position).getIsSelected()==1)
            setSelection(holder,1,position);

        holder.recursive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                setSelection(holder,isChecked?1:0,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.getComplexes().size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;
        RecyclerView recyclerView;
        CheckBox recursive;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            recursive = itemView.findViewById(R.id.recursive);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }



    private void setSelection(MyViewHolder holder, int status, int index){
        mTreeInteractionListener.onSelectionChange(AdministrationViewModel.TREE_NODE_COMPLEX, new TreeEdge(stateIndex,districtIndex,cityIndex,index),status);
        if (status==1) {
            mData.getComplexes().get(index).setIsSelected(1);
            holder.icon.setImageResource(R.drawable.ic_selected);
            holder.recursive.setChecked(true);
        } else {
            holder.recursive.setChecked(false);
            mData.getComplexes().get(index).setIsSelected(0);
            holder.icon.setImageResource(R.drawable.ic_not_selected);
        }
    }

}
