package trade.paper.app.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testapp.R
import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.rest.OrderHistoryResponse


import kotlinx.android.synthetic.main.item_order.view.*

import io.reactivex.processors.PublishProcessor
import java.time.ZonedDateTime

import java.util.*
import java.util.concurrent.TimeUnit

class OrderHistoryAdapter(var onClickListener: View.OnClickListener) : androidx.recyclerview.widget.RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {
    lateinit var symbol: String
    var data = arrayListOf<OrderHistoryResponse>()

    var onBottomItem = PublishProcessor.create<Int>()



    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater
                        .from(parent.context!!)
                        .inflate(
                                R.layout.item_active_order,
                                parent,
                                false
                        ),
                onClickListener
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(p0: ViewHolder, position: Int) {
        with(p0.itemView) {
            val o = data[position]

           /* var instant = ZonedDateTime.parse(o.timestamp)
            val a =
                    TimeUnit.HOURS.convert(TimeZone.getDefault().rawOffset.toLong(), TimeUnit.MILLISECONDS)
            instant=instant.minusHours(-a)
            val date = "${instant.dayOfMonth} " + "${instant.month.toString().subSequence(0, 3)}, "
            val hour = if (instant.hour <= 9) "0${instant.hour}:" else "${instant.hour}:"
            val minute = if (instant.minute <= 9) "0${instant.minute}:" else "${instant.minute}:"
            val second = if (instant.second <= 9) "0${instant.second}" else "${instant.second}"*/

         //   active_order_time.text = date + hour + minute + second

            var symbol = RXCache.getSymbol(o.symbol)


            active_order_amount.text =o.quantity
          //  active_orders_symbol.text = Formatter.currency(symbol?.baseCurrency ?: "") + " / " + Formatter.currency(symbol?.feeCurrency ?: "")

            active_order_price.text = o.price
            val color = when (o.side) {
                "buy" -> R.color.green
                "sell" -> R.color.red
                else -> R.color.white
            }
            active_order_price.setTextColor(resources.getColor(color))
            //cancel_order_btn.visibility = View.GONE
            //order_progress.visibility = View.GONE

            tag = o.clientOrderId
        }
        if (position >= 99 && position%99==0){
            onBottomItem.onNext(position)
        }

    }

    class ViewHolder(view: View, listener: View.OnClickListener) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener(listener)
        }
    }
}