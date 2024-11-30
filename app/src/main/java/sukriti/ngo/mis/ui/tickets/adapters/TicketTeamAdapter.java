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
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.ui.tickets.data.TicketFileItem;
import sukriti.ngo.mis.ui.tickets.data.TicketTeamData;

public class TicketTeamAdapter extends ArrayAdapter<TicketTeamData> {

    //UI

    //Data
    private List<TicketTeamData> mData, tempItems, suggestions;
    private Context mContext;

    public TicketTeamAdapter(Context context, List<TicketTeamData> objects) {
        super(context, 0 , objects);
        mData = (ArrayList<TicketTeamData>) objects;
        tempItems = new ArrayList<>(mData);
        suggestions = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public TicketTeamData getItem(int position) {
        return mData.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.item_ticket_team_list, parent, false);
        }

        // get the position of the view from the ArrayAdapter
        TicketTeamData item = getItem(position);

        // then according to the position of the view assign the desired image for the same
        TextView userName = currentItemView.findViewById(R.id.userName);
        TextView userRole = currentItemView.findViewById(R.id.userRole);
        userName.setText(item.getUser());
        userRole.setText(item.getRole());

        // then return the recyclable view
        return currentItemView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return fruitFilter;
    }
    private Filter fruitFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            TicketTeamData fruit = (TicketTeamData) resultValue;
            return fruit.getUser();
        }
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            if (charSequence != null) {
                suggestions.clear();
                for (TicketTeamData fruit: tempItems) {
                    if (fruit.getUser().toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                        suggestions.add(fruit);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            ArrayList<TicketTeamData> tempValues = (ArrayList<TicketTeamData>) filterResults.values;
            if (filterResults != null && filterResults.count > 0) {
                clear();
                for (TicketTeamData fruitObj : tempValues) {
                    add(fruitObj);
                }
                notifyDataSetChanged();
            } else {
                clear();
                notifyDataSetChanged();
            }
        }
    };
}
