package trade.paper.app.adapters

import android.content.Context
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.testapp.R
import trade.paper.app.models.dto.SymbolDTO
import java.util.*

open class GoToTradeAdapter(context: Context) : ArrayAdapter<SymbolDTO>(context, R.layout.go_to_trade_item, arrayListOf()) {

    open var itemLayoutId: Int? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val convertView = convertView
        val pair = getItem(position)

        var view = convertView

        if (view == null) {
            view = LayoutInflater
                    .from(parent.context!!)
                    .inflate(
                            itemLayoutId ?: R.layout.go_to_trade_item,
                            parent,
                            false
                    )
        }

        val textPair = view!!.findViewById<TextView>(R.id.text_pair)
        val textPercent = view!!.findViewById<TextView>(R.id.text_percent)
        val textPrice = view!!.findViewById<TextView>(R.id.text_vol)
        val vol = view!!.findViewById<TextView>(R.id.text_vol_usd)

        //textPair.text = Formatter.symbol(pair.symbol)
      //  vol.text = "Vol: ${Formatter.amountMarkets(pair.volume)} BTC"
      //  textPrice.text = Formatter.amount(pair.price)
/*
        if (pair?.change24 == 0.0) {
            textPercent.text = "0%"
            textPercent.setTextColor(ContextCompat.getColor(context, R.color.gray))
        } else {
            textPercent.text = String.format(Locale.US, "%+.2f%%", pair?.change24)
            textPercent.setTextColor(ContextCompat.getColor(context, if (pair?.change24 >= 0) R.color.green else R.color.red))
        }*/

        return view
    }
}