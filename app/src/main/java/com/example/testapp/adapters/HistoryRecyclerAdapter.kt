package trade.paper.app.adapters

import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testapp.R
import trade.paper.app.models.rest.TransactionHistoryResponce
import trade.paper.app.utils.extensions.visible
import kotlinx.android.synthetic.main.item_transaction_history.view.*

class HistoryRecyclerAdapter(var onClickListener: View.OnClickListener) : androidx.recyclerview.widget.RecyclerView.Adapter<HistoryRecyclerAdapter.ViewHolder>() {

    var data: MutableList<TransactionHistoryResponce> = arrayListOf()
    var onHashClick: ((String, String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val viewHolder = ViewHolder(
                LayoutInflater
                        .from(parent.context)
                        .inflate(
                                R.layout.item_transaction_history,
                                parent,
                                false
                        ),
                onClickListener
        )

        viewHolder.itemView.address.setOnClickListener {
            val pos = viewHolder.adapterPosition
            val item = data[pos]
            if (pos != androidx.recyclerview.widget.RecyclerView.NO_POSITION && (item.type == "payout" || item.type == "payin")) {
                onHashClick?.invoke(item.currency, item.hash ?: "")
            }
        }

        return viewHolder
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        with(holder.itemView) {
            val historyItem = data[position]

          //  currency.text = Formatter.currency(historyItem.currency)
         //   amount.text = Formatter.amount(historyItem.amount)
            //address.paintFlags = 0
         //   tv_start_date.text = Formatter.date(historyItem.updatedAt)


            transaction_status.setImageResource(
                    when (historyItem.status) {
                        "success" -> R.drawable.rd_trans_status_green
                        "failed" -> R.drawable.rd_trans_status_red
                        "pending" -> R.drawable.rd_trans_status_yellow
                        else -> R.drawable.rd_trans_status_yellow
                    }
            )

            transaction_msg.setText(
                    when(historyItem.status){
                        "success" -> R.string.confirmed
                        "failed" -> R.string.failed
                        "pending" -> R.string.unconfirmed
                        else -> R.string.unknown
                    }
            )


            when (historyItem.type) {
                "payout", "payin" -> {
                    if (historyItem.hash.isNullOrEmpty()) {
                        address.visibility = View.GONE
                    }
                    else address.visible()
                    address.paintFlags = address.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                    txid.visibility = View.VISIBLE
                    address.text = stripHash(historyItem.hash ?: "")
                }
                "bankToExchange" -> {
                    address.text = context.getString(R.string.to_trading)
                    txid.visibility = View.GONE
                }
                "exchangeToBank" -> {
                    address.text = context.getString(R.string.to_main)
                    txid.visibility = View.GONE
                }
            }

            tag = historyItem.id
        }
    }

    class ViewHolder(view: View, listener: View.OnClickListener) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener(listener)
        }
    }
    fun stripHash(orig: String): String{
        return try {
            var res = ""
            res+=orig.subSequence(0,5)
            res+="..."
            res+=orig.subSequence(orig.length-5,orig.length)
            res
        }catch (e: Exception){
            orig
        }
    }
}