package sukriti.ngo.mis.ui.management.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sukriti.ngo.mis.R
import sukriti.ngo.mis.ui.administration.data.MemberDetailsData
import sukriti.ngo.mis.ui.management.ManagementViewModel
import sukriti.ngo.mis.ui.management.data.Enterprise

class EnterpriseAdapter(
    context: Context?,
    list: List<Enterprise>,
    clickListener: EnterpriseItemClickListener,
    viewModel: ManagementViewModel
) : RecyclerView.Adapter<EnterpriseAdapter.MyViewHolder>() {

    private var ctx: Context?
    private var enterpriseList: List<Enterprise>
    private var clickListener: EnterpriseItemClickListener
    private var isEnable = false
    private var viewModel: ManagementViewModel
    private var lastPosition: Int

    init {
        ctx = context
        enterpriseList = list
        this.clickListener = clickListener
        this.viewModel = viewModel
        lastPosition = -1
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var displayName: TextView
        var enterpriseName: TextView
        var enterpriseSelect: ImageView


        init {
            displayName = itemView.findViewById(R.id.displayName)
            enterpriseName = itemView.findViewById(R.id.enterpriseName)
            enterpriseSelect = itemView.findViewById(R.id.enterpriseSelect)
        }
    }


    interface EnterpriseItemClickListener {
        fun onClick(enterprise: Enterprise?)

/*
        fun onLongClick(enterprise: Enterprise?)

        fun showDelete(show: Boolean)
*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.manage_enterprise_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return enterpriseList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.displayName.text = enterpriseList[position].enterpriseDisplayName
        holder.enterpriseName.text = enterpriseList[position].name
        holder.enterpriseSelect.visibility = View.GONE
        val item = enterpriseList[position]

        holder.itemView.setOnClickListener {

//            if (!isEnable) {
                clickListener.onClick(enterpriseList[position])
//                clickListener.showDelete(false)
            /*         } else {
                         if (viewModel.deleteEnterpriseList.contains(position)) {
         //                    viewModel.deleteEnterpriseList.removeAt(position)
                             viewModel.deleteEnterpriseList.remove(position)
                             holder.enterpriseSelect.visibility = View.GONE
                             item.selected = false
                             if (viewModel.deleteEnterpriseList.isEmpty()) {
                                 isEnable = false
                                 clickListener.showDelete(false)
                             }
                         } else if (isEnable) {
                             selectItem(holder, item, position)
                         }
                     }*/
        }

/*        holder.itemView.setOnLongClickListener {
            clickListener.onLongClick(enterpriseList[position])
            selectItem(holder, item, position)
            true
        }*/

        setAnimation(holder.itemView, position)
    }

    private fun selectItem(
        holder: MyViewHolder,
        item: Enterprise,
        position: Int
    ) {
        isEnable = true
        viewModel.deleteEnterpriseList.add(position)
        item.selected = true
        holder.enterpriseSelect.visibility = View.VISIBLE
//        clickListener.showDelete(true)
    }


    private fun setAnimation(view: View, position: Int) {
        if(position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(ctx, android.R.anim.slide_in_left)
            view.startAnimation(animation)
            lastPosition = position
        }
    }

}