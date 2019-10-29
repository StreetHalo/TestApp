package trade.paper.app.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testapp.R
import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.rest.BalanceResponse
import kotlinx.android.synthetic.main.item_balance.view.*
import trade.paper.app.utils.extensions.gone
import trade.paper.app.utils.extensions.visible

class BalanceRecyclerAdapter(var onClickListener: View.OnClickListener) : androidx.recyclerview.widget.RecyclerView.Adapter<BalanceRecyclerAdapter.ViewHolder>() {

    var data: List<BalanceResponse> = arrayListOf()
    var fullData: List<BalanceResponse> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater
                        .from(parent.context!!)
                        .inflate(
                                R.layout.item_balance,
                                parent,
                                false
                        )
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        with(p0.itemView) {
            val currency = data[p1].currency

/*
            balance_currency.text = Formatter.currency(currency)
            val fullName = Formatter.currencyFullName(currency)
            balance_full_name.text = Formatter.fullCurrencyName(fullName)
            balance_avialable.text = Formatter.amount((data[p1].available ?: "0.0").toDouble())
*/
            val inOrderBalance  = RXCache.getInOrdersBalance(currency)
            if (inOrderBalance != 0.0){
                balance_orders.visible()
                on_orders_hint.visible()
            //    balance_orders.text = Formatter.amount(inOrderBalance)

            }

            else {
                balance_orders.gone()
                on_orders_hint.gone()
            }

            tag = currency
        }
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener(onClickListener)
        }
    }

}