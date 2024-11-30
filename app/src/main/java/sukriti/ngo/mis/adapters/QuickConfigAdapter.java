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

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.dataModel.QuickConfigItem;

import static sukriti.ngo.mis.utils.Nomenclature.getQuickConfigItemList;

public class QuickConfigAdapter extends RecyclerView.Adapter<QuickConfigAdapter.MyViewHolder> {

    //UI

    //Data
    private List<QuickConfigItem> mData;
    private Context mContext;
    private QuickConfigSelectionHandler selectionHandler;

    public QuickConfigAdapter(Context mCtx, QuickConfigSelectionHandler selectionHandler) {
        this.mData = getQuickConfigItemList();
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
        if (viewType == 0)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quick_config, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quick_config, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        QuickConfigItem item = mData.get(position);
//        SpannableString mwc = getLabel(item,Nomenclature.CABIN_TYPE_MWC);
//        loadInfo(mwc,holder.mwcCont,holder.mwc);

        holder.title.setText(mData.get(position).getTitle());
        holder.description.setText(mData.get(position).getDescription());
        holder.details.setText("View");
        holder.details.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionHandler.onConfigItemSelected(mData.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.i("getItemCount", "" + mData.size());
        return mData.size();
    }

    public void remove(int position){
        this.mData.remove(position);
        notifyItemRemoved(position);
    }

    public void  add(QuickConfigItem item,int position){
        this.mData.add(position,item);
        notifyItemInserted(position);
    }

    public List<QuickConfigItem> getQuickConfigList(){
        return this.mData;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        Button details;
        TextView description;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            details = itemView.findViewById(R.id.details);
            description = itemView.findViewById(R.id.description);
        }
    }

    public interface QuickConfigSelectionHandler {
        public void onConfigItemSelected(QuickConfigItem data);
    }
}
