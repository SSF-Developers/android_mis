package sukriti.ngo.mis.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.ui.dashboard.data.ConnectionStatus
import sukriti.ngo.mis.ui.dashboard.data.Table
import sukriti.ngo.mis.utils.Nomenclature

class ConnectionAdapter(
    var mContext: Context,
    var mData: ArrayList<ConnectionStatus>,
    var onDashboard: Boolean,
    var listener : ClickHandler
) : RecyclerView.Adapter<ConnectionAdapter.MyViewHolder>() {


    override fun getItemViewType(position: Int): Int {
        return position % 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        var view: View = if (viewType == 0) LayoutInflater.from(parent.context).inflate(
            R.layout.item_faulty_cabin,
            parent,
            false
        ) else LayoutInflater.from(parent.context)
            .inflate(R.layout.item_faulty_cabin, parent, false)

        if (!onDashboard) {
            view = if (viewType == 0) LayoutInflater.from(parent.context).inflate(
                R.layout.item_faulty_cabin_list,
                parent,
                false
            ) else LayoutInflater.from(parent.context)
                .inflate(R.layout.item_faulty_cabin_list, parent, false)

        }
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = mData.get(position)
        Log.i("faultyAdapter", position.toString() + ". ComplexHealthStats: " + Gson().toJson(item))
        val mwc = getLabel(item, Nomenclature.CABIN_TYPE_MWC) // is for total
        val fwc = getLabel(item, Nomenclature.CABIN_TYPE_FWC)
        val pwc = getLabel(item, Nomenclature.CABIN_TYPE_PWC)
        val mur = getLabel(item, Nomenclature.CABIN_TYPE_MUR)
        val bwt = getLabel(item, Nomenclature.CABIN_TYPE_BWT)
        loadInfo(mwc, holder.mwcCont, holder.mwc)
        loadInfo(fwc, holder.fwcCont, holder.fwc)
        loadInfo(pwc, holder.pwcCont, holder.pwc)
        loadInfo(mur, holder.murCont, holder.mur)
        loadInfo(bwt, holder.bwtCont, holder.bwt)
        holder.title.setText(mData.get(position).complex.name)
        holder.card.setOnClickListener {
            listener.onSelect(mData[position])
        }
    }

    override fun getItemCount(): Int {
        Log.i("getItemCount", "" + mData.size)
        return if (onDashboard && mData.size > 10) {
            10
        } else
            mData.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var mwc: TextView
        var fwc: TextView
        var pwc: TextView
        var mur: TextView
        var bwt: TextView
        var mwcCont: LinearLayout
        var fwcCont: LinearLayout
        var pwcCont: LinearLayout
        var murCont: LinearLayout
        var bwtCont: LinearLayout
        var surface: FrameLayout
        var card: LinearLayout

        init {
            title = itemView.findViewById(R.id.title)
            surface = itemView.findViewById(R.id.surface)
            card = itemView.findViewById(R.id.card)
            mwc = itemView.findViewById(R.id.mwc)
            fwc = itemView.findViewById(R.id.fwc)
            pwc = itemView.findViewById(R.id.pwc)
            mur = itemView.findViewById(R.id.mur)
            bwt = itemView.findViewById(R.id.bwt)
            mwcCont = itemView.findViewById(R.id.mwcCont)
            fwcCont = itemView.findViewById(R.id.fwcCont)
            pwcCont = itemView.findViewById(R.id.pwcCont)
            murCont = itemView.findViewById(R.id.murCont)
            bwtCont = itemView.findViewById(R.id.bwtCont)
        }
    }

    private fun loadInfo(info: SpannableString, cont: LinearLayout, textView: TextView) {
        if (info.length > 0) {
            cont.visibility = View.VISIBLE
            textView.text = info
        } else {
            cont.visibility = View.GONE
        }
    }

    private fun getLabel(item: ConnectionStatus, Type: String): SpannableString {
        val faultyCountStr: String
        var info = ""
        val spanInfo: SpannableString
        var faultyCount = 0
        var totalCount = 0
        if (Type.compareTo(Nomenclature.CABIN_TYPE_MWC, ignoreCase = true) == 0) {
            faultyCount = item.count
            totalCount = item.count
            Log.i("faultyAdapter", "getLabel: faultyCount Mwc : $faultyCount")
        } else if (Type.compareTo(Nomenclature.CABIN_TYPE_FWC, ignoreCase = true) == 0) {
            faultyCount = 0
            totalCount = 0
            Log.i("faultyAdapter", "getLabel: faultyCount Fwc : $faultyCount")
        } else if (Type.compareTo(Nomenclature.CABIN_TYPE_PWC, ignoreCase = true) == 0) {
            faultyCount = 0
            totalCount = 0
            Log.i("faultyAdapter", "getLabel: faultyCount Pwc : $faultyCount")
        } else if (Type.compareTo(Nomenclature.CABIN_TYPE_MUR, ignoreCase = true) == 0) {
            faultyCount = 0
            totalCount = 0
            Log.i("faultyAdapter", "getLabel: faultyCount Mur : $faultyCount")
        } else if (Type.compareTo(Nomenclature.CABIN_TYPE_BWT, ignoreCase = true) == 0) {
            faultyCount = 0
            totalCount = 0
            Log.i("faultyAdapter", "getLabel: faultyCount Bwt : $faultyCount")
        }
        if (totalCount == 0) {
            spanInfo = SpannableString("")
            Log.i("faultyAdapter", "totalCount == 0 $spanInfo")
        } else if (faultyCount != 0) {
            faultyCountStr = "" + faultyCount
            //            info = faultyCountStr+"/"+totalCount +" faulty " +Type +" units detected";
            info = "$faultyCountStr disconnected Cabin(s) reported"
            Log.i("faultyAdapter", "faultyCount!=0: $info")
            spanInfo = SpannableString(info)
            spanInfo.setSpan(RelativeSizeSpan(1.2f), 0, faultyCountStr.length, 0)
            spanInfo.setSpan(StyleSpan(Typeface.BOLD), 0, faultyCountStr.length, 0)
            spanInfo.setSpan(ForegroundColorSpan(Color.RED), 0, faultyCountStr.length, 0)
            spanInfo.setSpan(RelativeSizeSpan(1f), faultyCountStr.length + 1, info.length, 0)
            spanInfo.setSpan(StyleSpan(Typeface.NORMAL), faultyCountStr.length + 1, info.length, 0)
            spanInfo.setSpan(
                ForegroundColorSpan(Color.BLACK),
                faultyCountStr.length + 1,
                info.length,
                0
            )
        } else {
            spanInfo = SpannableString("")
            //            if(totalCount == 1)
//                info = "The only " +Type +" unit is working";
//            else if(totalCount == 2)
//                info = "The 2 " +Type +" units are working";
//            else
//                info = "All " +Type +" units working";
//
//            spanInfo = new SpannableString(info);
//
//            spanInfo.setSpan(new RelativeSizeSpan(1f), 0, info.length(),0);
//            spanInfo.setSpan(new StyleSpan(Typeface.NORMAL), 0, info.length(),0);
//            spanInfo.setSpan(new ForegroundColorSpan(Color.BLACK), 0,info.length(), 0);
        }
        Log.i("faultyAdapter", "getLabel: $spanInfo")
        return spanInfo
    }

    interface ClickHandler {
        fun onSelect(status: ConnectionStatus)
    }
}