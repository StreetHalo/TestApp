package trade.paper.app.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testapp.R
import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.rest.OrderHistoryResponse
import trade.paper.app.utils.extensions.parseDateForTimeAndSales
import kotlinx.android.synthetic.main.item_trades_rd.view.*
import trade.paper.app.models.hawk.Settings

class MyTradesRecyclerAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<MyTradesViewHolder>() {

    var data = ArrayList<OrderHistoryResponse>()

    var onItemClick: ((OrderHistoryResponse) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyTradesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trades_rd, parent, false)
        val viewHolder = MyTradesViewHolder(view)

        view.setOnClickListener {
            val position = viewHolder.adapterPosition
            if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                onItemClick?.invoke(data[position])
            }
        }

        return viewHolder
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: MyTradesViewHolder, position: Int) {
        val item = data[position]
        with(holder.itemView){
           /* if(Settings.getLocale()=="ru") textView9.textSize = 10.0f
            tv_item_trades_date.text = item.timestamp.parseDateForTimeAndSales()

            val currency = RXCache.getSymbol(item.symbol)
            tv_item_trades_currency.text = "${Formatter.currency(currency?.baseCurrency ?: "")}/${Formatter.currency(currency?.feeCurrency ?: "")}"

            tv_item_trades_price.text = Formatter.currency(item.price)
            tv_item_trades_price_currency.text = Formatter.currency(currency?.feeCurrency ?: "")

            if (item.side.contains("buy", true)) {
                tv_item_trades_price.setTextColor(resources.getColor(R.color.rd_green))
                tv_item_trades_price_currency.setTextColor(resources.getColor(R.color.rd_green))
            } else {
                tv_item_trades_price.setTextColor(resources.getColor(R.color.rd_red))
                tv_item_trades_price_currency.setTextColor(resources.getColor(R.color.rd_red))
            }

            tv_item_trades_volume.text = item.quantity
            tv_item_trades_volume_currency.text = Formatter.currency(currency?.baseCurrency ?: "")*/
        }
    }

    fun setData(data: List<OrderHistoryResponse>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }
}

class MyTradesViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)