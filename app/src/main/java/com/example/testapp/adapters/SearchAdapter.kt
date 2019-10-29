package trade.paper.app.adapters

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testapp.R
import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.dto.SymbolDTO
import trade.paper.app.models.hawk.Settings
import kotlinx.android.synthetic.main.item_select_symbol.view.*

class SearchAdapter(var onClickListener: View.OnClickListener) : androidx.recyclerview.widget.RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    var symbols: MutableList<SymbolDTO> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater
                        .from(parent.context)
                        .inflate(
                                R.layout.item_select_symbol,
                                parent,
                                false
                        ),
                onClickListener
        )
    }

    override fun getItemCount(): Int {
        return symbols.size
    }

    fun setFilteredSymbols(filtered: List<SymbolDTO>) {
        symbols.clear()
        symbols.addAll(filtered)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.itemView) {
           // s_name.text = String.format("%s / %s", Formatter.currency(symbols[position].base), Formatter.currency(symbols[position].fee))
            tag = symbols[position].symbol

            var ticker = RXCache.getTicker(symbols[position].symbol) ?: return

          //  s_price.text = Formatter.amount(ticker.last.toDouble())

            val open = ticker.open.toDouble()
            val last = ticker.last.toDouble()
            var percent = ((last - open) / open) * 100.0
            if (percent.isNaN()) percent = 0.0

            if (percent == 0.0) {
                s_percent.text = "0%"
                s_percent.setTextColor(ContextCompat.getColor(context, R.color.gray))

            } else {
                s_percent.text = String.format("%+.2f%%", percent)
                s_percent.setTextColor(ContextCompat.getColor(context, if (percent >= 0) R.color.green else R.color.red))
            }

         //   favorite.visibility = if(Settings.isFavorite(symbols[position].symbol)) View.VISIBLE else View.GONE
            favorite_corner.visibility = favorite.visibility
        }
    }

    class ViewHolder(view: View, listener: View.OnClickListener) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener(listener)
        }
    }
}