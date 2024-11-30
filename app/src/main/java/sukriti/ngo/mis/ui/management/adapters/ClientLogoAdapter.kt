package sukriti.ngo.mis.ui.management.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import sukriti.ngo.mis.R

class ClientLogoAdapter(
    val list: List<String>
): RecyclerView.Adapter<ClientLogoAdapter.ViewHolder>() {

    private var selectedPosition = -1

    inner class ViewHolder(itemView: View, val context: Context): RecyclerView.ViewHolder(itemView) {
        private val logo: ImageView = itemView.findViewById(R.id.clientLogo)
        private val checkBox: CheckBox = itemView.findViewById(R.id.clientLogoCheckBox)

        fun onBind(imageUrl: String, position: Int) {

            Glide.with(context)
                .load(imageUrl)
                .error(R.drawable.ic_account_circle_white)
                .into(logo)


            checkBox.isChecked = (selectedPosition == position)

            checkBox.setOnClickListener {
                val previousSelected = selectedPosition

                if (previousSelected >= 0) {
                    notifyItemChanged(previousSelected)
                }

                selectedPosition = position
                notifyItemChanged(selectedPosition)
            }

        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.client_logo_item, viewGroup, false)

        return ViewHolder(view, viewGroup.context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.onBind(list[position], position)
    }

    fun getSelectedImagePosition(): Int {
        return selectedPosition
    }
}