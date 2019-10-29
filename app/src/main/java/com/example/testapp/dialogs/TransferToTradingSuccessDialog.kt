package trade.paper.app.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.widget.AdapterView
import com.example.testapp.R
import trade.paper.app.adapters.GoToTradeFromTransferAdapter
import trade.paper.app.models.cache.RXCache
import kotlinx.android.synthetic.main.dialog_transfer_to_trading_success.*

class TransferToTradingSuccessDialog(context: Context, currency: String) : Dialog(context) {

    var viewMorePairs: (() -> Unit)? = null
    var toTrades: ((String) -> Unit)? = null

    init {


        setContentView(R.layout.dialog_transfer_to_trading_success)

        btn_close.setOnClickListener {
            dismiss()
        }

        btn_view_more_pairs.setOnClickListener {
            dismiss()
            viewMorePairs?.invoke()
        }

        val adapter = GoToTradeFromTransferAdapter(context)
        gv_transfer_to_trading.adapter = adapter
        gv_transfer_to_trading.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            dismiss()
            toTrades?.invoke(adapter.getItem(position)?.symbol ?: return@OnItemClickListener)
        }

        val list = RXCache.getSymbolsDto(currency).sortedByDescending { it.volume }.filterIndexed { index, _ -> index <= 3 }
        adapter.clear()
        adapter.addAll(list)
        adapter.notifyDataSetChanged()
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.window?.setGravity(Gravity.CENTER)
        popular_pair_with_currency.setText(context.getString(R.string.top_24h_volume_pairs_with, currency))

    }

    fun show(sum: String) {
        tv_transfer_summ.text = sum
        super.show()
    }
}