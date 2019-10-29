package trade.paper.app.views.detail

import trade.paper.app.models.rpc.params.TickerParams
import trade.paper.app.views.BaseView

interface SymbolDetailView : BaseView {
    fun onTick(symbol: TickerParams?)

    fun showError(error: String)
}