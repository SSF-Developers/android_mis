package sukriti.ngo.mis.ui.management.adapters

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import sukriti.ngo.mis.R
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.Policy
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.PolicyListItem
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.PolicyDeleteCallback
import sukriti.ngo.mis.ui.management.fargments.PolicyDetails.SelectedCallback
import sukriti.ngo.mis.utils.Utilities

class PolicyListAdapter: RecyclerView.Adapter<PolicyListAdapter.MyViewHolder>() {

    var policiesList = mutableListOf<PolicyListItem>()
    var showDelete = false
    lateinit var clickListener: PolicyListItemClickListener
    lateinit var selectedCallback: SelectedCallback
    lateinit var policyDeleteCallback: PolicyDeleteCallback

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val policyName: TextView
        val policyVersion: TextView
        val policyListCardView: CardView
        val policySelectedImageView: ImageView
        val deletePolicy: ImageButton

        init {
            policyName = itemView.findViewById(R.id.policyName)
            policyVersion = itemView.findViewById(R.id.policyVersion)
            policyListCardView = itemView.findViewById(R.id.policyListCardView)
            policySelectedImageView = itemView.findViewById(R.id.policySelectedImageView)
            deletePolicy = itemView.findViewById(R.id.policyDeleteImageButton)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.policy_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return policiesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        var name = policiesList[position].name.split("/")[3]

        var name = policiesList[position].name
        Log.i("customName", name)
        if(name.contains("_KIOSK_LAUNCHER")) {
            name = name.subSequence(0, name.length-15).toString()
            Log.i("customName", "Contains _KIOSK_LAUNCHER")
        } else {
            Log.i("customName", "Not contains _KIOSK_LAUNCHER")
        }


        if(showDelete) {
            holder.deletePolicy.visibility = View.VISIBLE
        } else {
            holder.deletePolicy.visibility = View.GONE
        }

        holder.deletePolicy.setOnClickListener {
            policyDeleteCallback.onDeleteRequest(policiesList[position], position)
        }

        holder.policyName.text = name
        holder.policyVersion.text = holder.itemView.context.getString(R.string.version, policiesList[position].version)

        holder.policyListCardView.setOnClickListener {
            clickListener.onClick(policiesList[position])
        }

        if(policiesList[position].isSelected) {
            holder.policySelectedImageView.visibility = View.VISIBLE
        }
        else {
            holder.policySelectedImageView.visibility = View.GONE
        }


    }

    interface PolicyListItemClickListener {
        fun onClick(policy: PolicyListItem)
    }
}