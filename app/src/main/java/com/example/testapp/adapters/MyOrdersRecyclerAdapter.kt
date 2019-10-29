package trade.paper.app.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testapp.R
import trade.paper.app.utils.extensions.parseDateForTimeAndSales
import kotlinx.android.synthetic.main.item_order_rd.view.*
import trade.paper.app.models.rest.ActiveOrdersResponse

class OrdersRecyclerAdapter(private val orderType: Int, var context: Context?) : androidx.recyclerview.widget.RecyclerView.Adapter<MyOrderViewHolder>() {

    var onCancelOrder: ((orderId: String) -> Unit)? = null

    val data = ArrayList<ActiveOrdersResponse?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_rd, parent, false)
        val viewHolder = MyOrderViewHolder(view)

        view.apply {
            btn_item_order_cancel.setOnClickListener {
                val position = viewHolder.adapterPosition
                if (position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                    try {
                        onCancelOrder?.invoke(data[position]!!.clientOrderId)
                    }catch (e: Exception) {
                     //   ErrorWrapper.onError(e)
                    }
                }
            }
        }

        return viewHolder
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: MyOrderViewHolder, position: Int) {
        val item = data[position] ?: return
        with(holder.itemView){
      /*      tv_item_order_date.text = item.createdAt.parseDateForTimeAndSales()

            val currency = RXCache.getSymbol(item.symbol)
            tv_item_order_currency.text = "${Formatter.currency(currency?.baseCurrency ?: "")}/${Formatter.currency(currency?.feeCurrency ?: "")}"

            if (orderType == MyOrdersFragment.ORDER_TYPE_ACTIVE) {
                val type = item.type
                tv_item_order_type.visibility = View.VISIBLE
                tv_item_order_type.text = type

                btn_item_order_cancel.visibility = View.VISIBLE

                tv_item_order_status.visibility = View.GONE
            } else {
                tv_item_order_type.visibility = View.GONE

                btn_item_order_cancel.visibility = View.GONE

                tv_item_order_status.visibility = View.VISIBLE
                tv_item_order_status.text = getStatus(item.status)
            }
            val tickSize = RXCache.getSymbol(item.symbol)?.tickSize
            val accuracy = if (tickSize != null) {
                Formatter.getNumberAfterDot(tickSize.toBigDecimal())
            } else {
                2
            }

            tv_item_order_price.text = Formatter.amountWithAccuracy(item.price.toDouble(), accuracy)
            tv_item_order_price_symbol.text = Formatter.currency(currency?.feeCurrency ?: "")

            if (item.side.contains("buy", true)) {
                tv_item_order_price.setTextColor(resources.getColor(R.color.rd_green))
                tv_item_order_price_symbol.setTextColor(resources.getColor(R.color.rd_green))
            } else {
                tv_item_order_price.setTextColor(resources.getColor(R.color.rd_red))
                tv_item_order_price_symbol.setTextColor(resources.getColor(R.color.rd_red))
            }

            tv_item_order_volume.text = item.quantity
            tv_item_order_volume_symbol.text = Formatter.currency(currency?.baseCurrency ?: "")

            tv_item_order_exec.text = item.cumQuantity
            tv_item_order_exec_symbol.text = Formatter.currency(currency?.baseCurrency ?: "")*/
        }
    }

    fun setData(data: List<ActiveOrdersResponse?>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun removeItem(order: ActiveOrdersResponse) {
        var pos = -1
        for (i in 0 until data.size){
            if (order.clientOrderId == data[i]?.clientOrderId) {
                pos = i
                break
            }
        }
        if (pos!=-1) {
            data.removeAt(pos)
            notifyItemRemoved(pos)
        }
    }
    fun getStatus(apiStatus: String) : String {
        return " "
    }
}

class MyOrderViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)
