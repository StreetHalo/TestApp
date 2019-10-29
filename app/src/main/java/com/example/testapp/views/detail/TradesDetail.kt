package trade.paper.app.views.detail

import trade.paper.app.models.rest.TradesResponse
import trade.paper.app.views.BaseView

interface TradesDetail: BaseView {
    fun updateTrades(trades: List<TradesResponse>)
    fun setTrades(trades: List<TradesResponse>)
    fun showError(error: String)
}