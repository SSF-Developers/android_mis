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

import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.repository.data.ComplexHealthStats;
import sukriti.ngo.mis.repository.entity.Ticket;
import sukriti.ngo.mis.ui.tickets.adapters.TicketListAdapter;
import sukriti.ngo.mis.utils.Nomenclature;

public class ActiveTicketsAdapter extends RecyclerView.Adapter<ActiveTicketsAdapter.MyViewHolder> {

    //UI

    //Data
    private List<Ticket> mData;
    private Context mContext;
    private TicketListInteractionHandler ticketListInteractionHandler;

    public ActiveTicketsAdapter(Context mCtx, List<Ticket> mData, TicketListInteractionHandler ticketListInteractionHandler) {
        this.mData = mData;
        this.mContext = mCtx;
        this.ticketListInteractionHandler = ticketListInteractionHandler;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == 0 )
            view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.item_active_tickets,parent,false);
        else
            view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.item_active_tickets,parent,false);
        return new MyViewHolder (view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Ticket item = mData.get(position);
        holder.title.setText (mData.get (position).getComplex_name());
        holder.ticketId.setText(mData.get (position).getTicket_id());
        holder.ticketTitle.setText(mData.get (position).getTitle());
        holder.status.setText(mData.get (position).getTicket_status());
        holder.root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ticketListInteractionHandler.onTicketSelected(position, mData.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount",""+mData.size ());
        return mData.size ();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        View root;
        TextView title,ticketTitle,ticketId,status;
        FrameLayout surface;

        public MyViewHolder(View itemView) {
            super (itemView);
            root = itemView;
            title = itemView.findViewById(R.id.title);
            surface = itemView.findViewById(R.id.surface);
            ticketTitle = itemView.findViewById(R.id.titleTicket);
            ticketId = itemView.findViewById(R.id.ticketId);
            status = itemView.findViewById(R.id.status);
        }
    }

    public interface TicketListInteractionHandler {
        void onTicketSelected(int index, Ticket ticketDetailsData);
    }

}
