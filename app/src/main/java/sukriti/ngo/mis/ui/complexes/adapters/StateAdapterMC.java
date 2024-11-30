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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.dataModel.dynamo_db.Country;
import sukriti.ngo.mis.ui.administration.AdministrationViewModel;
import sukriti.ngo.mis.ui.administration.data.TreeEdge;
import sukriti.ngo.mis.ui.administration.interfaces.TreeInteractionListener;
import sukriti.ngo.mis.ui.login.data.UserProfile;

public class StateAdapterMC extends RecyclerView.Adapter<StateAdapterMC.MyViewHolder> {

    //UI

    //Data
    private Country mData;
    private Context mContext;
    private TreeInteractionListener mTreeInteractionListener;
    private AdministrationViewModel viewModel;

    public StateAdapterMC(Context mCtx, Country mData, TreeInteractionListener mTreeInteractionListener, AdministrationViewModel viewModel) {
        this.mData = mData;
        this.mContext = mCtx;
        this.mTreeInteractionListener = mTreeInteractionListener;
        this.viewModel = viewModel;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_state, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.title.setText(mData.getStates().get(position).getName());
        DistrictAdapterMC mAdapter = new DistrictAdapterMC(mContext, mData.getStates().get(position),position,mTreeInteractionListener,viewModel);
        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(mContext, 1);
        holder.recyclerView.setLayoutManager(gridLayoutManager);
        holder.recyclerView.setAdapter(mAdapter);

        holder.title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mData.getStates().get(position).getRecursive()==0 && !mData.getStates().get(position).getDistricts().isEmpty()){
                    if (holder.recyclerView.getVisibility() == View.VISIBLE)
                        collapse(holder,position);
                    else
                        expand(holder,position);
                }
            }
        });

        holder.recursive.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mData.getStates().size();
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

    private void expand(MyViewHolder holder,int index) {
        holder.recyclerView.setVisibility(View.VISIBLE);
        holder.icon.setImageResource(R.drawable.ic_collapse);
        holder.recursive.setVisibility(View.GONE);
    }

    private void collapse(MyViewHolder holder,int index) {
        holder.recyclerView.setVisibility(View.GONE);
        holder.icon.setImageResource(R.drawable.ic_expand);
        holder.recursive.setVisibility(View.VISIBLE);

        if(UserProfile.Companion.isClientSpecificRole(UserProfile.Companion.getRole(viewModel.getSelectedUser().cognitoUser.getRole()))) {
            holder.recursive.setVisibility(View.GONE);
        }
    }
}
