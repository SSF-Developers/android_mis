

package sukriti.ngo.mis.ui.complexes.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.dataModel.dynamo_db.District;
import sukriti.ngo.mis.ui.administration.AdministrationViewModel;
import sukriti.ngo.mis.ui.administration.data.TreeEdge;
import sukriti.ngo.mis.ui.administration.interfaces.TreeInteractionListener;
import sukriti.ngo.mis.ui.login.data.UserProfile;

public class CityAdapterMC extends RecyclerView.Adapter<CityAdapterMC.MyViewHolder> {

    //UI

    //Data
    private District mData;
    private Context mContext;
    private int stateIndex;
    private int districtIndex;
    private TreeInteractionListener mTreeInteractionListener;
    private AdministrationViewModel viewModel;

    public CityAdapterMC(Context mCtx, District mData, int stateIndex, int districtIndex, TreeInteractionListener mTreeInteractionListener, AdministrationViewModel viewModel) {
        this.mData = mData;
        this.mContext = mCtx;
        this.stateIndex = stateIndex;
        this.districtIndex = districtIndex;
        this.mTreeInteractionListener = mTreeInteractionListener;
        this.viewModel = viewModel;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.element_city,parent,false);
        return new MyViewHolder (view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.title.setText (mData.getCities().get(position).getName());
        ComplexAdapterMC mAdapter = new ComplexAdapterMC(mContext, mData.getCities().get(position),stateIndex,districtIndex,position,mTreeInteractionListener,viewModel);
        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(mContext, 1);
        holder.recyclerView.setLayoutManager(gridLayoutManager);
        holder.recyclerView.setAdapter(mAdapter);

        if(mData.getCities().get(position).getRecursive()==1)
            setRecursive(holder,1,position);

        if(mData.getCities().get(position).getComplexes().isEmpty())
            holder.icon.setImageResource(R.drawable.ic_empty);

        if(UserProfile.Companion.isClientSpecificRole(UserProfile.Companion.getRole(viewModel.getSelectedUser().cognitoUser.getRole()))) {
            holder.recursive.setVisibility(View.GONE);
        }

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(mData.getCities().get(position).getRecursive()==1) && !mData.getCities().get(position).getComplexes().isEmpty()){
                    if (holder.recyclerView.getVisibility() == View.VISIBLE)
                        collapse(holder,position);
                    else
                        expand(holder,position);
                }
            }
        });

        holder.recursive.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mData.getCities().size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;
        RecyclerView recyclerView;
        CheckBox recursive;

        public MyViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            recursive = itemView.findViewById(R.id.recursive);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }

    private void expand(MyViewHolder holder, int index) {
        holder.recyclerView.setVisibility(View.VISIBLE);
        holder.icon.setImageResource(R.drawable.ic_collapse);
        holder.recursive.setVisibility(View.GONE);
    }

    private void collapse(MyViewHolder holder, int index) {
        holder.recyclerView.setVisibility(View.GONE);
        holder.icon.setImageResource(R.drawable.ic_expand);
        holder.recursive.setVisibility(View.VISIBLE);

        if(UserProfile.Companion.isClientSpecificRole(UserProfile.Companion.getRole(viewModel.getSelectedUser().cognitoUser.getRole()))) {
            holder.recursive.setVisibility(View.GONE);
        }
    }

    private void setRecursive(MyViewHolder holder, int status, int index){
        mTreeInteractionListener.onSelectionChange(AdministrationViewModel.TREE_NODE_CITY, new TreeEdge(stateIndex,districtIndex,index),status);
        if (status==1) {
            mData.getCities().get(index).setRecursive(1);
            holder.icon.setImageResource(R.drawable.ic_selected);
            holder.recursive.setChecked(true);
        } else {
            mData.getCities().get(index).setRecursive(0);
            holder.recursive.setChecked(false);
            if(mData.getCities().get(index).getComplexes().isEmpty())
                holder.icon.setImageResource(R.drawable.ic_empty);
            else
                holder.icon.setImageResource(R.drawable.ic_expand);
        }
    }
}
