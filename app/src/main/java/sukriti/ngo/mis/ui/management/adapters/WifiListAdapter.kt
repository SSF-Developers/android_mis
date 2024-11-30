package sukriti.ngo.mis.ui.management.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sukriti.ngo.mis.R

class WifiListAdapter(private var list: ArrayList<String>): RecyclerView.Adapter<WifiListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wifi_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.wifiName.text = list[position]

        viewHolder.deleteWifi.setOnClickListener {
            removeItem(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addItem(name: String) {
        list.add(name)
        notifyItemInserted(list.size-1)
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var wifiName: TextView = itemView.findViewById(R.id.wifiName)
        var deleteWifi: ImageView = itemView.findViewById(R.id.deleteWifi)
    }
}