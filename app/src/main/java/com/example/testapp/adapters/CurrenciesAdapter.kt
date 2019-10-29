package trade.paper.app.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testapp.R
import trade.paper.app.models.rest.CurrencyResponce
import kotlinx.android.synthetic.main.item_currency.view.*

open class CurrenciesAdapter(var onClickListener: View.OnClickListener) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
    var currencies: MutableList<CurrencyResponce>? = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        return ViewHolder(
                LayoutInflater
                        .from(parent.context)
                        .inflate(
                                R.layout.item_currency,
                                parent,
                                false
                        ),
                onClickListener
        )
    }

    override fun getItemCount(): Int {
        return currencies?.size ?: 0
    }

    fun setFilteredCurrencies(filtered: List<CurrencyResponce>?) {
        currencies?.clear()
        currencies?.addAll(filtered ?: return)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        with(holder.itemView) {
            val currency = currencies?.get(position) ?: return

            tag = currency.id

        //    s_name.text = Formatter.currency(currency.id)
        //    s_full_name.text = Formatter.fullCurrencyName(currency.fullName)
        }
    }

    class ViewHolder(view: View, listener: View.OnClickListener) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener(listener)
        }
    }
}