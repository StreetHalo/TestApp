package trade.paper.app.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testapp.R
import trade.paper.app.models.rest.TradesResponse
import trade.paper.app.utils.extensions.parseDateForTimeAndSalesWithMilliseconds
import kotlinx.android.synthetic.main.item_active_order.view.*

//TODO This one should be merged with active orders adapter!!!
class DetailTradesRecyclerAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<DetailTradesRecyclerAdapter.ViewHolder>() {
    lateinit var symbol: String
    var data: MutableList<TradesResponse> = MutableList(12){ TradesResponse(-1, "", "", "", "") }//arrayListOf<TradesResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater
                        .from(parent.context!!)
                        .inflate(
                                R.layout.item_market_trades,
                                parent,
                                false
                        )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val o = data[position]
        val color = when (o.side) {
            "buy" -> R.color.chart_green
            "sell" -> R.color.orange_new
            else -> R.color.white
        }

        with(holder.itemView) {
            active_order_time.text = o.timestamp.parseDateForTimeAndSalesWithMilliseconds()
            active_order_amount.text = if (o.quantity.isEmpty()) "-" else o.quantity
            active_order_price.text = if (o.price.isEmpty()) "-" else o.price
            active_order_price.setTextColor(resources.getColor(color))
        }

    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView)
}