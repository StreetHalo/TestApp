package trade.paper.app.adapters

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testapp.R
import trade.paper.app.models.rest.TradesResponse
import trade.paper.app.utils.extensions.parseDateForTimeAndSalesWithMilliseconds
import kotlinx.android.synthetic.main.item_times_and_sales.view.*
import java.util.*

class TimesAndSalesRecyclerAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<TimesAndSalesViewHolder>() {

    private val data = ArrayList<TradesResponse>()

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): TimesAndSalesViewHolder {
        val view = LayoutInflater.from(container.context).inflate(R.layout.item_times_and_sales, container, false)

        val viewHolder = TimesAndSalesViewHolder(view)

        return viewHolder
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: TimesAndSalesViewHolder, position: Int) {
        holder.bind(data[position], data.size-position)
    }

    fun setItems(data: List<TradesResponse>) {
        this.data.clear()
        this.data.addAll(data.reversed())

        notifyDataSetChanged()
    }

    fun addItems(data: List<TradesResponse>) {
        this.data.addAll(0, data.reversed())
        notifyItemRangeInserted(0, data.size)
    }
}

class TimesAndSalesViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {

    fun bind(item: TradesResponse, position: Int) {
        val color = when (item.side) {
            "buy" -> R.color.rd_green
            "sell" -> R.color.rd_red
            else -> R.color.gray_light
        }

        with(itemView) {
            tv_price_item_times_and_sales.setTextColor(ContextCompat.getColor(context, color))
            tv_date_item_times_and_sales.text = item.timestamp.parseDateForTimeAndSalesWithMilliseconds()
            tv_price_item_times_and_sales.text = item.price
            tv_amount_item_times_and_sales.text = item.quantity

            background = if (position % 2 == 0) null else resources.getDrawable(R.color.rd_superdark_gray)
        }
    }
}