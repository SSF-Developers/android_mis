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


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.components.Section;
import com.github.anastr.speedviewlib.components.Style;

import java.util.ArrayList;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.dataModel.UserSelection;
import sukriti.ngo.mis.repository.entity.AqiLumen;
import sukriti.ngo.mis.ui.dashboard.data.UiResult;
import sukriti.ngo.mis.utils.SharedPrefsClient;

public class AqiLumenAdapter extends RecyclerView.Adapter<AqiLumenAdapter.MyViewHolder> {

    //UI

    //Data
    private AqiLumen mData;
    private Context mContext;
    private UiResult uiResult;
    public AqiLumenAdapter(Context mCtx, AqiLumen mData) {
        this.mData = mData;
        this.mContext = mCtx;
        uiResult =new SharedPrefsClient(mContext).getUiResult();

    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aqi_lumen, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aqi_lumen, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Boolean display = true;
        switch (position) {
            case 0:
                if (uiResult.data.methane.equals("true")){ display =true;}else {display = false;}
                break;
            case 1:
                if (uiResult.data.carbon_monooxide.equals("true")){ display =true;}else {display = false;}
                break;
            case 2:
                if (uiResult.data.ammonia.equals("true")){ display =true;}else {display = false;}
                break;
            case 3:
                if (uiResult.data.luminous.equals("true")){ display =true;}else {display = false;}
                break;
        }
        if (display){
            holder.surface.setVisibility(View.VISIBLE);
        }else {
            holder.surface.setVisibility(View.VISIBLE);
        }

        loadData(position, holder.analogueGauge, holder.title);
    }

    @Override
    public int getItemCount() {
        return 4;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        RelativeLayout surface;
        SpeedView analogueGauge;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            surface = itemView.findViewById(R.id.surface);
            analogueGauge = itemView.findViewById(R.id.speedView);
            analogueGauge.setWithTremble(false);
        }
    }

    @SuppressLint("ResourceType")
    private void loadData(int position, SpeedView gauge, TextView label) {
        if (position == 0) {
            //CH4
            gauge.setUnit("PPM");
            gauge.speedTo(getValue(mData.getConcentrationCH4()), 3500);
            label.setText("Methane Concentration");

            gauge.getSections().clear();
            gauge.addSections(new Section(0f, .2f, Color.parseColor(mContext.getString(R.color.gauge_good)), gauge.dpTOpx(30f))
                    , new Section(.2f, .5f, Color.parseColor(mContext.getString(R.color.gauge_average)) ,gauge.dpTOpx(30f))
                    , new Section(.5f, 1f, Color.parseColor(mContext.getString(R.color.gauge_poor)), gauge.dpTOpx(30f)));

        } else if (position == 1) {
            //CO
            gauge.setUnit("PPM");
            gauge.speedTo(getValue(mData.getConcentrationCO()), 3500);
            label.setText("Carbon Monoxide Concentration");

            gauge.getSections().clear();
            gauge.addSections(new Section(0f, .2f, Color.parseColor(mContext.getString(R.color.gauge_good)), gauge.dpTOpx(30f))
                    , new Section(.2f, .5f, Color.parseColor(mContext.getString(R.color.gauge_average)) ,gauge.dpTOpx(30f))
                    , new Section(.5f, 1f, Color.parseColor(mContext.getString(R.color.gauge_poor)), gauge.dpTOpx(30f)));
        } else if (position == 2) {
            //NH3
            gauge.setUnit("PPM");
            gauge.speedTo(getValue(mData.getConcentrationNH3()), 3500);
            label.setText("Ammonia Concentration");

            gauge.getSections().clear();
            gauge.addSections(new Section(0f, .2f, Color.parseColor(mContext.getString(R.color.gauge_good)), gauge.dpTOpx(30f))
                    , new Section(.2f, .5f, Color.parseColor(mContext.getString(R.color.gauge_average)) ,gauge.dpTOpx(30f))
                    , new Section(.5f, 1f, Color.parseColor(mContext.getString(R.color.gauge_poor)), gauge.dpTOpx(30f)));

        } else if (position == 3) {
            //Lumen
            gauge.setUnit("Lumen");
            gauge.speedTo(getValue(mData.getConcentrationLuminosityStatus()), 3500);
            label.setText("Luminous Intensity");

            gauge.getSections().clear();
            gauge.addSections(new Section(0f, .2f, Color.parseColor(mContext.getString(R.color.gauge_poor)), gauge.dpTOpx(30f))
                    , new Section(.2f, .5f, Color.parseColor(mContext.getString(R.color.gauge_average)) ,gauge.dpTOpx(30f))
                    , new Section(.5f, 1f, Color.parseColor(mContext.getString(R.color.gauge_good)), gauge.dpTOpx(30f)));
        }
    }

    private int getValue(String value) {
        int intValue = 0;
        try {
            intValue = Integer.valueOf(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return intValue;
    }
}
