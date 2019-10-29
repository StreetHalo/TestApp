package trade.paper.app.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testapp.R
import trade.paper.app.models.rest.TransactionHistoryResponce
import kotlinx.android.synthetic.main.item_currency_history_detail.view.*


class CurrencyHistoryDetailAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<CurrencyHistoryDetailAdapter.ViewHolder>() {
    var data  = mutableListOf<TransactionHistoryResponce>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.item_currency_history_detail, viewGroup, false)
        )
    }

    override fun getItemCount(): Int {
       return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView){
     //       amount_th.text = Formatter.amount(data[position].amount)
       //     time_th.text = Formatter.standardDateFormat(data[position].createdAt)

            side_th.text = context.getString(
                    when (data[position].type){
                        "bankToExchange" -> R.string.to_trading
                        "exchangeToBank" -> R.string.to_main
                        "payout" -> R.string.withdraw
                        "payin" -> R.string.deposit
                        else -> R.string.undefined
                    }
            )


            status_icn_th.setImageResource(
                    when (data[position].status) {
                        "success" -> R.drawable.rd_trans_status_green
                        "failed" -> R.drawable.rd_trans_status_red
                        "pending" -> R.drawable.rd_trans_status_yellow
                        else -> R.drawable.rd_trans_status_yellow
                    }
            )

            status_tv_th.setText(
                    when(data[position].status){
                        "success" -> R.string.confirmed
                        "failed" -> R.string.failed
                        "pending" -> R.string.unconfirmed
                        else -> R.string.unknown
                    }
            )
        }
    }

    inner class ViewHolder(item: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(item)
}