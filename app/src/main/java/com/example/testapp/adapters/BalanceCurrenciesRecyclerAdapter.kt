package trade.paper.app.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testapp.R
import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.rest.BalanceResponse
import kotlinx.android.synthetic.main.item_balance.view.*

class BalanceCurrenciesRecyclerAdapter(onItemClick: View.OnClickListener): CurrenciesAdapter(onItemClick) {
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

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        with(holder.itemView) {
            val currency = data[position].currency

/*
            balance_currency.text = Formatter.currency(currency)
            val fullName = Formatter.currencyFullName(currency)
            balance_full_name.text = Formatter.fullCurrencyName(fullName)
            balance_avialable.text = Formatter.amount((data[position].available ?: "0.0").toDouble())
            balance_orders.text = Formatter.amount(RXCache.getInOrdersBalance(currency))
*/

            tag = currency
        }
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener(onClickListener)
        }
    }


    fun setFilteredData(filtered: List<BalanceResponse>?) {
        data = filtered?:return
        notifyDataSetChanged()
    }
}