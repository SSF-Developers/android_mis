package sukriti.ngo.mis.ui.management.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import sukriti.ngo.mis.R
import sukriti.ngo.mis.ui.management.EnrollDevice.Interface.RefreshDeviceList
import sukriti.ngo.mis.ui.management.HelperClassesAndFragments.Interfaces.PolicyToggle
import sukriti.ngo.mis.ui.management.ManagementViewModel
import sukriti.ngo.mis.ui.management.data.device.Device

class DeviceAdapter(
    context: Context?,
    list: List<Device>,
    clickListener: DeviceItemClickListener,
    policyToggleListener: PolicyToggle,
    viewModel: ManagementViewModel
) : RecyclerView.Adapter<DeviceAdapter.MyViewHolder>() {

    private var ctx: Context?
    private var deviceList: List<Device>
    private var clickListener: DeviceItemClickListener
    private var policyToggleListener: PolicyToggle
    private var lastPosition: Int
    private var viewModel: ManagementViewModel

    init {
        ctx = context
        deviceList = list
        this.clickListener = clickListener
        this.policyToggleListener = policyToggleListener
        lastPosition = -1
        this.viewModel = viewModel
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cabinName: TextView
        var enrollmentStatus: TextView
        var awsCommissioningStatus: TextView
        var enrollmentStatusIcon: ImageView
        var awsCommissioningStatusIcon: ImageView
        var policyToggleSwitch: SwitchCompat

        var state: TextView
        var district: TextView
        var city: TextView
        var complex: TextView
        init {
            cabinName = itemView.findViewById(R.id.deviceName)
            enrollmentStatus = itemView.findViewById(R.id.enrollmentStatus)
            awsCommissioningStatus = itemView.findViewById(R.id.awsCommissioningStatus)
            enrollmentStatusIcon = itemView.findViewById(R.id.enrollmentStatusIcon)
            awsCommissioningStatusIcon = itemView.findViewById(R.id.awsCommissioningStatusIcon)
            state = itemView.findViewById(R.id.stateCode)
            district = itemView.findViewById(R.id.districtCode)
            city = itemView.findViewById(R.id.cityCode)
            complex = itemView.findViewById(R.id.complexName)
            policyToggleSwitch = itemView.findViewById(R.id.policyToggle)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.manage_devices_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val device = deviceList[position]

        var stateVal = ""
        var districtVal = ""
        var cityVal = ""
        var shortThingName = ""
        if(device.cabin_details != null && device.cabin_details!!.attributes != null && device.cabin_details!!.attributes.size > 0) {
            for (i in 0 until device.cabin_details?.attributes?.size!!) {
                val attr = device.cabin_details!!.attributes[i]
                if(attr.Name.equals("STATE_CODE")) {
                    stateVal = attr.Value
                } else if (attr.Name.equals("DISTRICT")) {
                    districtVal = attr.Value
                } else if (attr.Name.equals("CITY")) {
                    cityVal = attr.Value
                } else if (attr.Name.equals("SHORT_THING_NAME")) {
                    shortThingName = attr.Value
                }
            }

        }

        if(device.complex_details != null && device.complex_details.name.isNotEmpty()) {
            holder.complex.text = device.complex_details.name
        }

        val cabinName = "$stateVal/$districtVal/$cityVal/$shortThingName"

        holder.cabinName.text = cabinName

        if(device.DEVICE_PROV_GET_INFO_PUBLISH) {
            holder.enrollmentStatusIcon.setImageResource(R.drawable.green_check)
        }
        else {
            holder.enrollmentStatusIcon.setImageResource(R.drawable.red_cancel)
        }

        if(device.DEVICE_PROV_COMPLETED_INFO_RESP_INIT) {
            holder.awsCommissioningStatusIcon.setImageResource(R.drawable.green_check)
        }
        else {
            holder.awsCommissioningStatusIcon.setImageResource(R.drawable.red_cancel)
        }

//        Log.i("policyToggle", "Adapter: ${device.android_data.policyName}")
        if(device.android_data != null) {
            holder.policyToggleSwitch.isChecked = device.isKioskEnabled
        }
        Log.i("policyToggle", "Previous State: ${holder.policyToggleSwitch.isChecked}")

        val listener = {_: Any, isChecked: Boolean ->
            holder.policyToggleSwitch.setOnCheckedChangeListener(null)
            holder.policyToggleSwitch.isChecked = !isChecked
            policyToggleListener.onClick(
                device,
                position,
                isChecked
            )
        }

        holder.policyToggleSwitch.setOnCheckedChangeListener(listener)

        /*
                holder.policyToggleSwitch.setOnCheckedChangeListener { _, state ->
                    Log.i("policyToggle", "New State: $state" )
                    policyToggleListener.onClick(
                        device,
                        position,
                        state
                    )
                }
        */

//        holder.policyToggleSwitch.setOnClickListener {
//            policyToggleListener.onClick(
//                device,
//                position,
//                holder.policyToggleSwitch.isChecked
//            )
//        }

        holder.state.text = stateVal
        holder.district.text = districtVal
        holder.city.text = cityVal

        holder.itemView.setOnClickListener {
            clickListener.onClick(device)
        }

        setAnimation(holder.itemView, position)
    }


    private fun setAnimation(view: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(ctx, android.R.anim.slide_in_left)
            view.startAnimation(animation)
            lastPosition = position
        }
    }


    interface DeviceItemClickListener {
        fun onClick(device: Device?)

    }

    private fun setListener(holder: MyViewHolder, position: Int) {
        val device = deviceList[position]

    }

}