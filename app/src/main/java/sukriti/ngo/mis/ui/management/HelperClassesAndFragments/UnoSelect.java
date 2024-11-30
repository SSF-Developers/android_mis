package sukriti.ngo.mis.ui.management.HelperClassesAndFragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.UnoSelectHandler;
import sukriti.ngo.mis.R;

import java.util.ArrayList;

import static sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Constants.SELECT_ACTION_BILLING_GROUP;
import static sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Constants.SELECT_ACTION_BWT_LEVEL;
import static sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Constants.SELECT_ACTION_CABIN_TYPE;
import static sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Constants.SELECT_ACTION_CITY;
import static sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Constants.SELECT_ACTION_CLIENT;
import static sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Constants.SELECT_ACTION_COMMISSIONING;
import static sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Constants.SELECT_ACTION_DISTRICT;
import static sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Constants.SELECT_ACTION_SMARTNESS;
import static sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Constants.SELECT_ACTION_STATE;
import static sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Constants.SELECT_ACTION_SUFFIX;
import static sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Constants.SELECT_ACTION_USAGE_CHARGE_TYPE;
import static sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Constants.SELECT_ACTION_USER_TYPE;

public class UnoSelect extends DialogFragment implements View.OnClickListener {

    private TextView label;
    private RelativeLayout close;
    private RecyclerView options;
    private CheckboxListAdapter mAdapter;
    private int action;
    private String selection, title;
    private ArrayList<String> data;
    private UnoSelectHandler mListener;

    public UnoSelect() {
        // Required empty public constructor
    }

    public static UnoSelect newInstance(int action, String selection) {

        UnoSelect fragment = new UnoSelect();
        Bundle args = new Bundle();
        args.putString("selection", selection);
        args.putInt("action", action);
        fragment.setArguments(args);
        return fragment;
    }

    public void setUp(ArrayList<String> data, UnoSelectHandler mListener)
    {
        this.data = data;
        this.mListener = mListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            action = getArguments().getInt("action");
            String dialogTitle= "";
            if(action == SELECT_ACTION_STATE)
                title = "Select State";
            else if(action == SELECT_ACTION_DISTRICT)
                title = "Select District";
            else if(action == SELECT_ACTION_CITY)
                title = "Select City";
            else if(action == SELECT_ACTION_CLIENT)
                title = "Select Client";
            else if(action == SELECT_ACTION_BILLING_GROUP)
                title = "Select Billing Group";
            else if(action == SELECT_ACTION_SMARTNESS)
                title = "Select Smartness Level";
            else if(action == SELECT_ACTION_COMMISSIONING)
                title = "Select Commissioning Status";
            else if(action == SELECT_ACTION_BWT_LEVEL)
                title = "Select BWT Level";
            else if(action == SELECT_ACTION_SUFFIX)
                title = "Select Thing Name Suffix";
            else if(action == SELECT_ACTION_CABIN_TYPE)
                title = "Select Cabin Type";
            else if(action == SELECT_ACTION_USER_TYPE)
                title = "Select User Type";
            else if(action == SELECT_ACTION_USAGE_CHARGE_TYPE)
                title = "Select Usage Charge Type";
            selection = getArguments().getString("selection");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.popup_list_checkbox, container, false);
        options = v.findViewById(R.id.optionsList);
        label = v.findViewById(R.id.popupLabel);
        close = v.findViewById(R.id.closeContainer);

        label.setText(title);
        int numberOfColumns = 1;
        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(getContext(), numberOfColumns);
        options.setLayoutManager(gridLayoutManager);
        mAdapter = new CheckboxListAdapter(getContext(), data, mListener, selection);
        options.setAdapter(mAdapter);
        close.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }

    class CheckboxListAdapter extends RecyclerView.Adapter<CheckboxListAdapter.MyViewHolder> {
        //UI
        //Data
        private ArrayList<String> mData;
        private Context mContext;
        private UnoSelectHandler mListener;
        private String currentSelection;
        private CheckBox selectedCheckBox = null;

        public CheckboxListAdapter(Context mCtx, ArrayList<String> mData, UnoSelectHandler mListener, String currentSelection) {
            this.mData = mData;
            this.mContext = mCtx;
            this.mListener = mListener;
            this.currentSelection = currentSelection;

        }

        @Override
        public int getItemViewType(int position) {
            return position % 2;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0)
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkbox_list, parent, false);
            else
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkbox_list, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.label.setText(mData.get(position));
            if (mData.get(position).compareToIgnoreCase(currentSelection) == 0) {
                holder.checkBox.setChecked(true);
                selectedCheckBox = holder.checkBox;
            } else
                holder.checkBox.setChecked(false);

            holder.parent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onSelectThingGroup(mData.get(position),action);
                }
            });
            holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) {
                        if (selectedCheckBox != null) selectedCheckBox.setChecked(false);
                        mListener.onSelectThingGroup(mData.get(position),action);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            Log.i("getItemCount", "" + mData.size());
            return mData.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView label;
            CheckBox checkBox;
            View parent;

            public MyViewHolder(View itemView) {
                super(itemView);
                label = itemView.findViewById(R.id.label);
                checkBox = itemView.findViewById(R.id.checkbox);
                parent = itemView;
            }
        }
    }
}