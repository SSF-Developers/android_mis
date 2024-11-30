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

package sukriti.ngo.mis.ui.administration.adapter;


import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData;

import static sukriti.ngo.mis.repository.utils.DateConverter.toDateString_FromAwsDateFormat;
import static sukriti.ngo.mis.utils.Utilities.getInitials;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.MyViewHolder> {

    //UI

    //Data
    private List<MemberDetailsData>  mData;
    private Context mContext;
    private TeamItemClickListener listener;

    public TeamAdapter(Context mCtx, List<MemberDetailsData> mData, TeamItemClickListener listener) {
        this.mData = mData;
        this.mContext = mCtx;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.element_team,parent,false);
        return new MyViewHolder (view);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.roleInitials.setText (getInitials(mData.get(position).team.getMemberRole()));
        holder.roleName.setText (mData.get(position).team.getMemberRole());
        holder.userName.setText (mData.get(position).team.getMember());
        holder.organisation.setText (mData.get(position).cognitoUser.getOrganisation());
        if(mData.get(position).cognitoUser.isEnabled()){
            holder.status.setText (mData.get(position).cognitoUser.getAccountStatus());
        }else {
            holder.status.setText ("Disabled");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.status.setTextColor(mContext.getColor(R.color.alert));
            }
        }

        holder.created.setText (toDateString_FromAwsDateFormat(mData.get(position).cognitoUser.getCreated()));
        holder.itemView.setOnClickListener(view -> listener.onClick(mData.get(position)));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView roleInitials,roleName,userName,organisation,created,status;
        public MyViewHolder(View itemView) {
            super (itemView);
            roleInitials = itemView.findViewById(R.id.roleInitials);
            roleName = itemView.findViewById(R.id.roleName);
            userName = itemView.findViewById(R.id.userName);
            organisation = itemView.findViewById(R.id.organisation);
            created = itemView.findViewById(R.id.created);
            status = itemView.findViewById(R.id.status);
        }
    }

    public interface TeamItemClickListener{
        void onClick(MemberDetailsData memberDetailsData);
    }
}
