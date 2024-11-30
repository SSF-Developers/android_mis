package sukriti.ngo.mis.ui.management.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sukriti.ngo.mis.R
import sukriti.ngo.mis.ui.management.data.device.Device

class DeleteEnterpriseDeviceListAdapter(
    val list: ArrayList<Device>
): RecyclerView.Adapter<DeleteEnterpriseDeviceListAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val index: TextView = itemView.findViewById(R.id.index)
        val shortThingName: TextView = itemView.findViewById(R.id.shortThingName)
        val serialNumber: TextView = itemView.findViewById(R.id.serialNum)
        val state: TextView = itemView.findViewById(R.id.state)
        val district: TextView = itemView.findViewById(R.id.district)
        val city: TextView = itemView.findViewById(R.id.city)
        val complex: TextView = itemView.findViewById(R.id.complex)
    }

    override fun onCreateViewHolder(parentView: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parentView.context).inflate(R.layout.delete_enterprise_device_list_item, parentView, false)

        return ViewHolder(view)
    }



    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val device = list[position]
        viewHolder.index.text = (position+1).toString()
        viewHolder.serialNumber.text = device.serial_number
        device.cabin_details.attributes.forEach {
            when (it.Name) {
                "COMPLEX" -> {viewHolder.complex.text = it.Value}
                "CITY" -> {viewHolder.city.text = it.Value}
                "DISTRICT" -> {viewHolder.district.text = it.Value}
                "STATE" -> {viewHolder.state.text = it.Value}
                "SHORT_THING_NAME" -> {viewHolder.shortThingName.text = it.Value}
            }
        }
    }
}