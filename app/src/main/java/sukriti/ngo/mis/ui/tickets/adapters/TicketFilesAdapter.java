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


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.dataModel.QuickConfigItem;
import sukriti.ngo.mis.ui.tickets.data.TicketFileItem;

import static sukriti.ngo.mis.utils.Nomenclature.getQuickConfigItemList;

public class TicketFilesAdapter extends RecyclerView.Adapter<TicketFilesAdapter.MyViewHolder> {

    //UI

    //Data
    private List<TicketFileItem> mData;
    private Context mContext;
    private RemoveHandler removeHandler;

    public TicketFilesAdapter(RemoveHandler removeHandler, List<TicketFileItem> ticketFiles) {
        this.mData = ticketFiles;
        this.removeHandler = removeHandler;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_files, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_files, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        TicketFileItem item = mData.get(position);

        Log.i("_thumbNail",""+item.getFileType());
        if(item.getFileType() == TicketFileItem.Companion.get_photo()){
            holder.remove.setVisibility(View.VISIBLE);
            holder.placeholder.setImageURI(mData.get(position).getUri());
        }else if(item.getFileType() == TicketFileItem.Companion.get_video()){
            holder.remove.setVisibility(View.VISIBLE);
            Log.i("_thumbNail",""+item.getFileType());
            holder.placeholder.setBackgroundResource(R.drawable.ic_video);
        }else if(item.getFileType() == TicketFileItem.Companion.get_empty()){
            holder.remove.setVisibility(View.GONE);
            Log.i("_thumbNail",""+item.getFileType());
            holder.placeholder.setBackgroundResource(R.drawable.placeholder_upload_image);
        }else if(item.getFileType() == TicketFileItem.Companion.get_urlPhoto()){
            holder.remove.setVisibility(View.GONE);
            Picasso.get().load(item.getUri()).into(holder.placeholder);
        }

        holder.remove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                removeHandler.onRemove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount", "" + mData.size());
        return mData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView placeholder,remove;

        public MyViewHolder(View itemView) {
            super(itemView);
            placeholder = itemView.findViewById(R.id.placeholder);
            remove = itemView.findViewById(R.id.remove);
        }
    }

    public interface RemoveHandler {
        void onRemove(int index);
    }
}
