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
import sukriti.ngo.mis.ui.tickets.data.TicketDetailsData;
import sukriti.ngo.mis.ui.tickets.data.TicketProgressData;
import sukriti.ngo.mis.utils.Nomenclature;
import sukriti.ngo.mis.utils.Utilities;

public class TicketProgressAdapter extends RecyclerView.Adapter<TicketProgressAdapter.MyViewHolder> {

    //UI

    //Data
    private List<TicketProgressData> mData;

    public TicketProgressAdapter(List<TicketProgressData> mData) {
        this.mData = mData;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_progress_list, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_progress_list, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        TicketProgressData item = mData.get(position);
        holder.event.setText(item.getEvent());
        holder.eventDescription.setText(Nomenclature.getEventDescription(item.getEvent()));
        holder.userRole.setText(item.getUser_role());
        holder.userName.setText(item.getUser_id());
        holder.date.setText(Utilities.getTimeDifference(Long.valueOf(item.getTimestamp())));
        holder.comment.setText(item.getComments());
    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount", "" + mData.size());
        return mData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        View root;
       TextView event,eventDescription,userRole,userName,date, comment;

        public MyViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            event = itemView.findViewById(R.id.event);
            eventDescription = itemView.findViewById(R.id.eventDescription);
            userRole = itemView.findViewById(R.id.userRole);
            userName = itemView.findViewById(R.id.userName);
            date = itemView.findViewById(R.id.date);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    public interface TicketListInteractionHandler {
        void onTicketSelected(int index, TicketDetailsData ticketDetailsData);
    }
}
