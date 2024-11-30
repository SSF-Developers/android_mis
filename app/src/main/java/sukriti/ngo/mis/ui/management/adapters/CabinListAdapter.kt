package sukriti.ngo.mis.ui.management.adapters

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import sukriti.ngo.mis.R
import sukriti.ngo.mis.ui.management.EnrollDeviceViewModel
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ComplexDetails
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.DataModel.ThingDetails
import sukriti.ngo.mis.ui.management.fargments.CabinDetailsDialog.ClickCallback

class CabinListAdapter: RecyclerView.Adapter<CabinListAdapter.MyViewHolder>() {

    var cabinDetails =  mutableListOf<ThingDetails>()
    var cabinList = mutableListOf<String>()
    lateinit var complexDetails: ComplexDetails
    val cabinListLiveData = MutableLiveData<List<String>>()
    val liveDAta = cabinListLiveData
    lateinit var clickListener: CabinListItemClickListener
    lateinit var clickCallback: ClickCallback

/*    fun setCabinDetails(details: List<IotCabinDetails>) {
        cabinDetails = details
    }*/

/*    fun setComplexDetails(details: ComplexDetails) {
        complexDetails = details
    }*/


    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var cabinUuid: TextView
        var cabinUserType: TextView
        var cabinLocation: TextView
        val item: CardView
        val cabinImage: ImageView


        init {
            cabinUuid = itemView.findViewById(R.id.cabinUUID)
            cabinUserType = itemView.findViewById(R.id.cabinUserType)
            cabinLocation = itemView.findViewById(R.id.cabinLocation)
            cabinImage = itemView.findViewById(R.id.cabinImage)
            item = itemView.findViewById(R.id.cabinListItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.list_registered_cabins, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cabinDetails.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.cabinUuid.text = cabinDetails[position].Name
        holder.cabinUserType.text = cabinDetails[position].ThingType
        holder.cabinLocation.text = complexDetails.stateName

        Log.i("ClickCallback", "${cabinDetails[position].Name} = ${cabinDetails[position].isSelected} ")
        if(cabinDetails[position].isSelected) {
            holder.cabinImage.setImageResource(R.drawable.check)
        } else {
            holder.cabinImage.setImageResource(R.drawable.ic_wc_male)
        }

        holder.item.setOnClickListener {
            clickListener.onClick(cabinDetails[position])
        }
    }

    interface CabinListItemClickListener {
        fun onClick(details: ThingDetails)
    }


}