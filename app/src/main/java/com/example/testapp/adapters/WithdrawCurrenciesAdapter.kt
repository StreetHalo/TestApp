package trade.paper.app.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testapp.Formatter
import com.example.testapp.R
import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.rest.CurrencyResponce
import kotlinx.android.synthetic.main.item_withdraw_currency.view.*
import kotlinx.android.synthetic.main.item_withdraw_select_last.view.*

class WithdrawCurrenciesAdapter : CurrenciesAdapter(View.OnClickListener { }) {

    private val TYPE_ITEM = R.layout.item_withdraw_currency
    private var TYPE_HEADER = R.layout.item_withdraw_select_header
    private var TYPE_LAST = R.layout.item_withdraw_select_last

    var onItemClick: ((String) -> Unit)? = null
    var onCurrencyToTransferClick: (() -> Unit)? = null

    var haveCurrenciesWithTradingBalance = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        val viewHolder = when (viewType) {
            TYPE_ITEM -> {
                CurrencyViewHolder(view).apply {
                    itemView.setOnClickListener {
                        val pos = adapterPosition
                        if (pos != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                            currencies?.get(pos - 1)?.id?.let {
                                onItemClick?.invoke(it)
                            }
                        }
                    }
                }
            }
            TYPE_HEADER -> HeaderViewHolder(view)
            else -> {
                val holder = TradingAccountHintViewHolder(view)
                holder.itemView.tv_search_to_transfer.setOnClickListener {
                    val pos = holder.adapterPosition
                    if (pos != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                        onCurrencyToTransferClick?.invoke()
                    }
                }
                holder
            }
        }
        return viewHolder
    }

    override fun getItemCount() = (if (!currencies.isNullOrEmpty()) (currencies!!.size + 1) else 0) + (if (haveCurrenciesWithTradingBalance) 1 else 0)

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        if (holder is CurrencyViewHolder) {
            currencies?.get(position - 1)?.let {
                holder.bind(it)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (!currencies.isNullOrEmpty()) {
            when {
                position == 0 -> TYPE_HEADER
                position <= currencies?.size ?: 0 -> TYPE_ITEM
                else -> TYPE_LAST
            }
        } else {
            return TYPE_LAST
        }
    }
}

class CurrencyViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
    fun bind(item: CurrencyResponce) {
        with(itemView) {
            tv_withdraw_item_currency.text = Formatter.currency(item.id)
            tv_withdraw_item_currency_name.text = Formatter.currencyFullName(item.id)
            val available = RXCache.getBalance(item.id)?.available
            tv_withdraw_item_balance.text = Formatter.amount(available)
        }
    }
}

class HeaderViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

class TradingAccountHintViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)