package sukriti.ngo.mis.ui.complexes.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.repository.utils.DateConverter.*
import sukriti.ngo.mis.ui.complexes.data.UpiPaymentList


class AdapterProfileUpi(var mData : ArrayList<UpiPaymentList>) : RecyclerView.Adapter<AdapterProfileUpi.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = if (viewType == 0)
            LayoutInflater.from(parent.context).inflate(R.layout.element_cabin_upi_row, parent, false)
        else LayoutInflater.from(parent.context).inflate(R.layout.element_cabin_upi_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.i("cabinDetails", "onBindViewHolder: "+ Gson().toJson(mData[position]))
        val date: String = toDbDate(toDbTimestamp(mData[position].timestamp))
        val time = toDbTime(toDbTimestamp(mData[position].timestamp))
        holder.fields[0]!!.text = date
        holder.fields[1]!!.text = time
        holder.fields[2]!!.text = mData[position].amount_received
        holder.fields[3]!!.text = mData[position].amount_transferred
        holder.fields[4]!!.text = mData[position].sukriti_fee
        holder.fields[5]!!.text = mData[position].vendor_fee
        holder.fields[6]!!.text = mData[position].vpa
        holder.fields[7]!!.text = mData[position].tax
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ids = intArrayOf(
            R.id.field_01,
            R.id.field_02,
            R.id.field_03,
            R.id.field_04,
            R.id.field_05,
            R.id.field_06,
            R.id.field_07,
            R.id.field_08
        )
        var fields = arrayOfNulls<TextView>(ids.size)

        init {
            for (i in ids.indices) {
                fields[i] = itemView.findViewById(ids[i])
            }
        }
    }
}