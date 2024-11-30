package sukriti.ngo.mis.ui.reports.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sukriti.ngo.mis.R
import sukriti.ngo.mis.ui.reports.data.UsageReportData

class DateDataReportAdapter(val mData: UsageReportData) :
    RecyclerView.Adapter<DateDataReportAdapter.ViewHolder>() {
    private var _rs = "";

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DateDataReportAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.element_row_date_data, parent, false)
        _rs = parent.context.getString(R.string.Rs)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: DateDataReportAdapter.ViewHolder, position: Int) {
        holder.bindItems(mData, position,_rs)
    }

    override fun getItemCount(): Int {
        return mData.usageComparison.female.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ids = intArrayOf(
            R.id.field_01,
            R.id.field_02,
            R.id.field_03,
            R.id.field_04,
            R.id.field_05,
            R.id.field_06,
            R.id.field_07,
            R.id.field_08,
            R.id.field_09,
            R.id.field_10,
            R.id.field_11,
            R.id.field_12,
            R.id.field_13
        )
        var fields = arrayOfNulls<TextView>(ids.size)
        var i = 0

        fun bindItems(mData: UsageReportData, position: Int,_rs: String) {

            for (i in ids.indices) {
                fields[i] = itemView.findViewById(ids[i])
            }

            fields[0]?.text = mData.usageComparison.male[position].date


            fields[1]?.text = "" + getTotalCount(mData, position)
            fields[2]?.text = _rs +  getTotalAmount(mData, position)

            fields[3]?.text = "" + mData.usageComparison.male[position].count
            fields[4]?.text = _rs +  mData.collectionComparison.male[position].amount

            fields[5]?.text = "" + mData.usageComparison.female[position].count
            fields[6]?.text = _rs +  mData.collectionComparison.female[position].amount

            fields[7]?.text = "" + mData.usageComparison.pd[position].count
            fields[8]?.text = _rs +  mData.collectionComparison.pd[position].amount

            fields[9]?.text = "" + mData.usageComparison.mur[position].count
            fields[10]?.text = _rs +  mData.collectionComparison.mur[position].amount

            fields[11]?.text = "" + mData.ticketResolutionTimeline.total[position].raiseCount
            fields[12]?.text = "" + mData.ticketResolutionTimeline.total[position].resolveCount
        }

        private fun getTotalCount(mData: UsageReportData, position: Int): Int {
            //            aug QA :Rahul Karn
            var total = (mData.usageComparison.male[position].count
            +mData.usageComparison.female[position].count
            +mData.usageComparison.pd[position].count
            +mData.usageComparison.mur[position].count)

//            var total = mData.usageComparison.male[position].count
//                    +mData.usageComparison.female[position].count
//                    +mData.usageComparison.pd[position].count
//                    +mData.usageComparison.mur[position].count
//            ***************


            return total
        }

        private fun getTotalAmount(mData: UsageReportData, position: Int): Float {
            //            aug QA :Rahul Karn
            var total = (mData.collectionComparison.male[position].amount
            +mData.collectionComparison.female[position].amount
            +mData.collectionComparison.pd[position].amount
            +mData.collectionComparison.mur[position].amount)

//            var total = mData.collectionComparison.male[position].amount
//                    +mData.collectionComparison.female[position].amount
//                    +mData.collectionComparison.pd[position].amount
//                    +mData.collectionComparison.mur[position].amount
//            *******************************
            return total
        }
    }

}