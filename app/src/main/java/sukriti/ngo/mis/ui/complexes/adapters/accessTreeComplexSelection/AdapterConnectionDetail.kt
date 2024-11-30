package sukriti.ngo.mis.ui.complexes.adapters.accessTreeComplexSelection

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import sukriti.ngo.mis.R
import sukriti.ngo.mis.repository.utils.DateConverter.*
import sukriti.ngo.mis.ui.dashboard.data.Table


class AdapterConnectionDetail(var mData: ArrayList<Table>) : RecyclerView.Adapter<AdapterConnectionDetail.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = if (viewType == 0)
            LayoutInflater.from(parent.context).inflate(R.layout.element_complex_connection_detail, parent, false)
        else LayoutInflater.from(parent.context).inflate(R.layout.element_complex_connection_detail, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.i("cabinDetails", "onBindViewHolder: "+ Gson().toJson(mData[position]))
        if(mData[position] !=null) {
            val date: String = toDbDate(toDbTimestamp(mData[position].timestamp))
            val time = toDbTime(toDbTimestamp(mData[position].timestamp))
            holder.fields[0]!!.text = mData[position].THING_NAME.substring(mData[position].THING_NAME.length-7,mData[position].THING_NAME.length)
            holder.fields[1]!!.text = mData[position].CONNECTION_STATUS
            holder.fields[2]!!.text = mData[position].DISCONNECT_REASON
            holder.fields[3]!!.text = date
            holder.fields[4]!!.text = time
        }else{
            holder.fields[0]!!.visibility =View.GONE
            holder.fields[1]!!.visibility =View.GONE
            holder.fields[2]!!.visibility =View.GONE
            holder.fields[3]!!.visibility =View.GONE
            holder.fields[4]!!.visibility =View.GONE
        }
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
            R.id.field_05
        )
        var fields = arrayOfNulls<TextView>(ids.size)

        init {
            for (i in ids.indices) {
                fields[i] = itemView.findViewById(ids[i])
            }
        }
    }
}