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

package sukriti.ngo.mis.ui.tickets.adapters;


import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.repository.entity.Ticket;
import sukriti.ngo.mis.ui.tickets.data.TicketDetailsData;
import sukriti.ngo.mis.utils.Utilities;

public class TicketListAdapter extends RecyclerView.Adapter<TicketListAdapter.MyViewHolder> {

    //UI

    //Data
    private List<Ticket> mData;
    private TicketListInteractionHandler ticketListInteractionHandler;

    public TicketListAdapter(List<Ticket> mData, TicketListInteractionHandler ticketListInteractionHandler) {
        this.mData = mData;
        this.ticketListInteractionHandler = ticketListInteractionHandler;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_list, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_list, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Ticket item = mData.get(position);

        holder.status.setText(item.getTicket_status());
        holder.title.setText(item.getTitle());
        holder.ticketId.setText(item.getTicket_id());
        holder.complex.setText(item.getComplex_name());
        holder.state.setText(item.getState_code()+":"+item.getDistrict_name());
        holder.city.setText(item.getCity_name());
        holder.userRole.setText(item.getCreator_role());
        holder.userName.setText(item.getCreator_id());
        holder.date.setText(Utilities.getTimeDifference(item.getTimestamp()));
        holder.critical.setText(item.getCriticality());
        holder.criticalIv.setBackgroundResource(R.drawable.surface_circle_red);

        holder.root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ticketListInteractionHandler.onTicketSelected(position, mData.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount", "" + mData.size());
        return mData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        View root;
       TextView status,title,ticketId,complex,state,city,userRole,userName,date,critical;
       ImageView criticalIv;

        public MyViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            status = itemView.findViewById(R.id.status);
            title = itemView.findViewById(R.id.title);
            ticketId = itemView.findViewById(R.id.ticketId);
            complex = itemView.findViewById(R.id.complex);
            state = itemView.findViewById(R.id.state);
            city = itemView.findViewById(R.id.city);
            userRole = itemView.findViewById(R.id.userRole);
            userName = itemView.findViewById(R.id.userName);
            date = itemView.findViewById(R.id.date);
            critical = itemView.findViewById(R.id.critical);
            criticalIv = itemView.findViewById(R.id.criticalIv);
        }
    }

    public interface TicketListInteractionHandler {
        void onTicketSelected(int index, Ticket ticketDetailsData);
    }
}
