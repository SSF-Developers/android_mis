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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.ui.complexes.data.MisCommand;
import sukriti.ngo.mis.utils.Nomenclature;

public class QuickCommandsAdapter extends RecyclerView.Adapter<QuickCommandsAdapter.MyViewHolder> {

    //UI
    private MyViewHolder selectedHolder;
    private int selectedIndex;

    //Data
    private ArrayList<MisCommand> mData;
    private Context mContext;
    private CommandSubmitHandler submitHandler;

    public QuickCommandsAdapter(Context mCtx, ArrayList<MisCommand> mData) {
        this.mData = mData;
        this.mContext = mCtx;
    }

    public void setCommandSubmitHandler(CommandSubmitHandler handler){
        submitHandler = handler;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_command_quick, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.title.setText(mData.get(position).getName());
        holder.subTitle.setText(mData.get(position).getDescription());
        holder.submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MisCommand command = mData.get(position);
                submitHandler.onSubmit(command);
                selectedHolder = holder;
                selectedIndex = position;
                Log.i("__command",""+command.getName() +" | "+command.getCode()+", "+command.getDuration()+"Sec " +"|"+command.getAction() +"|"+command.getOverride());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        Button submit;
        TextView title,subTitle;
        ImageView icon;


        public MyViewHolder(View itemView) {
            super(itemView);
            submit = itemView.findViewById(R.id.submit);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            subTitle = itemView.findViewById(R.id.subTitle);
        }
    }

    public interface CommandSubmitHandler {
        public void onSubmit(MisCommand command);
    }
}
