package sukriti.ngo.mis.adapters.accessTree.singleSelection;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import sukriti.ngo.mis.R;
import sukriti.ngo.mis.dataModel.dynamo_db.City;
import sukriti.ngo.mis.ui.administration.AdministrationViewModel;
import sukriti.ngo.mis.ui.administration.data.TreeEdge;
import sukriti.ngo.mis.ui.administration.interfaces.TreeInteractionListener;

public class ComplexAdapterSS extends RecyclerView.Adapter<ComplexAdapterSS.MyViewHolder> {

    //UI

    //Data
    private City mData;
    private Context mContext;
    private int stateIndex;
    private int districtIndex;
    private int cityIndex;
    private TreeInteractionListener mTreeInteractionListener;
    private AdministrationViewModel viewModel;

    public ComplexAdapterSS(Context mCtx, City mData, int stateIndex, int districtIndex, int cityIndex, TreeInteractionListener mTreeInteractionListener) {
        this.mData = mData;
        this.mContext = mCtx;
        this.stateIndex = stateIndex;
        this.districtIndex = districtIndex;
        this.cityIndex = cityIndex;
        this.mTreeInteractionListener = mTreeInteractionListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from (parent.getContext ()).inflate (R.layout.element_complex_2,parent,false);
        return new MyViewHolder (view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.title.setText (mData.getComplexes().get(position).getName());

        if(mData.getComplexes().get(position).getIsSelected()==1)
            holder.icon.setImageResource(R.drawable.ic_selected);

        holder.root.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mTreeInteractionListener.onSelectionChange(AdministrationViewModel.TREE_NODE_COMPLEX,
                        new TreeEdge(stateIndex,districtIndex,cityIndex,position),1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.getComplexes().size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        View root;
        TextView title;
        ImageView icon;
        RecyclerView recyclerView;
        ImageView details;

        public MyViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            details = itemView.findViewById(R.id.recursive);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }

}
