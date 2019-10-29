package trade.paper.app.adapters

import android.content.Context
import com.example.testapp.R

class GoToTradeFromTransferAdapter(context: Context): GoToTradeAdapter(context) {
    override var itemLayoutId: Int? = R.layout.go_to_trade_item_from_transfer
}