package sukriti.ngo.mis.ui.management.adapters

import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sukriti.ngo.mis.R
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.WifiDetails

class WifiListAdapter2(
     var list: ArrayList<WifiDetails>,
     val selectedList : ArrayList<WifiDetails>

): RecyclerView.Adapter<WifiListAdapter2.ViewHolder>() {

    // Variable to track which item is selected
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    // Variable to track visibility of RadioButton in all items
     var isRadioButtonVisible: Boolean = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wifi_list_item_2, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = list[position]

        holder.wifiName.text = currentItem.name
        holder.wifiPassword.text = currentItem.password

        holder.wifiCheckBox.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(isChecked) {
                    add(list[position])
                }
                else {
                    remove(list[position])
                }
            }
        })

        // Set RadioButton visibility based on the flag
        holder.radioButton.visibility = if (isRadioButtonVisible && currentItem.isSelected) View.VISIBLE else View.GONE

        if (isRadioButtonVisible){
            holder.wifiCheckBox.isEnabled = false

        }
        else{
            holder.wifiCheckBox.isEnabled = true
        }


        if (currentItem.isSelected == true) {
            // Set the RadioButton checked state based on the selected position
            holder.radioButton.isChecked = position == selectedPosition
        }
        // Handle radio button click
        holder.radioButton.setOnClickListener {
            if(selectedPosition != -1) {
                list[selectedPosition].isDefault = false
            }
            selectedPosition = holder.adapterPosition
            currentItem.isDefault = true
//            notifyDataSetChanged()  // This will refresh the entire list, ensuring only one item is selected
            setRadioButtonVisibility(false)
        }

    }

    // Function to show or hide the radio buttons
    fun setRadioButtonVisibility(visible: Boolean) {
        isRadioButtonVisible = visible
        notifyDataSetChanged()  // Refresh the RecyclerView to update visibility of RadioButtons
    }


    fun add(item: WifiDetails){
        selectedList.add(item)
        item.isSelected = true
    }

    fun remove(wifiItem: WifiDetails){
        var itemPosition = -1

/*
        selectedList.forEach { item ->
            if (item.name == wifiItem.name && item.password == wifiItem.password){
//                selectedList.remove(item)
                itemPosition =
            }
        }
*/

        for(i in 0 until selectedList.size) {
            val item = selectedList[i]
            if (item.name == wifiItem.name && item.password == wifiItem.password) {
                itemPosition = i
                break
            }
        }

        if(itemPosition != -1) {
            list[itemPosition].isSelected = false
            selectedList.removeAt(itemPosition)
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val wifiName : TextView = itemView.findViewById(R.id.wifiName)
        val wifiPassword : TextView = itemView.findViewById(R.id.wifiPassword)
        val wifiCheckBox : CheckBox = itemView.findViewById(R.id.wifiCheckBox)
        val radioButton : RadioButton = itemView.findViewById(R.id.radioButtonDefault)


    }

}