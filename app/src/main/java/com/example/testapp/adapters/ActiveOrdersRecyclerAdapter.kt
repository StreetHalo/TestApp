package trade.paper.app.adapters

import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.testapp.R
import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.rest.ActiveOrdersResponse
import trade.paper.app.models.rpc.Error
import kotlinx.android.synthetic.main.item_order.view.*
import java.time.ZonedDateTime
import java.util.*
import java.util.TimeZone.getDefault
import java.util.concurrent.TimeUnit

class ActiveOrdersRecyclerAdapter(var context: Context) : RecyclerView.Adapter<ActiveOrdersRecyclerAdapter.ViewHolder>() {

    var orders = arrayListOf<ActiveOrdersResponse>()
    var clientID: HashMap<String, ActiveOrdersResponse> = hashMapOf()
    var onItemRemovedListener: (Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater
                        .from(parent.context!!)
                        .inflate(
                                R.layout.item_order,
                                parent,
                                false
                        )
        )
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView) {
            val o = orders[position]
            clientID[o!!.clientOrderId] = o

            var instant = ZonedDateTime.parse(o.createdAt)
            val a =
                    TimeUnit.HOURS.convert(getDefault().rawOffset.toLong(), TimeUnit.MILLISECONDS)
            instant=instant.minusHours(-a)
            val date = "${instant.dayOfMonth} " + "${instant.month.toString().subSequence(0, 3)}, "
            val hour = if (instant.hour <= 9) "0${instant.hour}:" else "${instant.hour}:"
            val minute = if (instant.minute <= 9) "0${instant.minute}:" else "${instant.minute}:"
            val second = if (instant.second <= 9) "0${instant.second}" else "${instant.second}"

            active_order_time.text = date + hour + minute + second

            //val instant = ZonedDateTime.parse(o.createdAt)
            //active_order_time.text = Formatter.date(Date(o.createdAt))
                    /*"${instant.dayOfMonth} " +
                    "${instant.month.toString().subSequence(0, 3)}, " +
                    if (instant.hour <= 9) "0${instant.hour}:" else "${instant.hour}:" +
                            if (instant.minute <= 9) "0${instant.minute}:" else "${instant.minute}:${if (instant.second <= 9) "0${instant.second}" else "${instant.second}"}"*/

            var symbol = RXCache.getSymbol(o.symbol)

            active_order_amount.text = o.quantity
           // active_orders_symbol.text = Formatter.currency(symbol?.baseCurrency
           //         ?: "") + " / " + Formatter.currency(symbol?.feeCurrency ?: "")
            active_order_price.text = o.price
            val color = when (o.side) {
                "buy" -> R.color.green
                "sell" -> R.color.red
                else -> R.color.white
            }
            active_order_price.setTextColor(ContextCompat.getColor(context,color))
            cancel_order_btn.setOnClickListener { v ->

                AlertDialog.Builder(context)
                        .setMessage(context.getString(R.string.cancel_order_question))
                        .setPositiveButton("OK"){
                            _, _ -> cancelOrder(v as TextView, o)
                        }
                        .setNegativeButton(context.getString(R.string.dismiss)){
                            dialog, _ -> dialog.dismiss()
                        }
                        .create()
                        .show()



            }
            active_orders_executed.text = "${context.getString(R.string.executed)} ${o.cumQuantity}"
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    fun cancelOrder(v: TextView, order: ActiveOrdersResponse){
      //  AnalyticsManager.sendEvent(AnalyticsManager.Event.OrderCanceled)

        with(v){
            setTextColor(ContextCompat.getColor(context,R.color.red_weak))
            isClickable = false
        }

/*        App.paperAPI.cancelOrder(order?.clientOrderId ?: return)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            cancelOrderSucces(it)
                            AnalyticsManager.sendEvent(AnalyticsManager.Event.SuccessfulCancelOrder)
                        },
                        { cancelOrderFailure(
                                Error(400, it.localizedMessage, "")
                                , v)
                        }
                )*/
    }

    fun cancelOrderSucces(order: ActiveOrdersResponse) {
        RXCache.removeOrder(order?.clientOrderId ?: return)

        Toast.makeText(context, context.getString(R.string.order_cancelled), Toast.LENGTH_SHORT).show()
        val o = clientID[order.clientOrderId]
        val index = orders.indexOf(o)
        orders.remove(orders[index])


        notifyItemRemoved(index)
        onItemRemovedListener(index)
    }

    fun cancelOrderFailure(error: Error, v: TextView) {
        if (error.code != 429) Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
        with(v){
            isClickable = true
            setTextColor(ContextCompat.getColor(context,R.color.white))
        }
    }

}