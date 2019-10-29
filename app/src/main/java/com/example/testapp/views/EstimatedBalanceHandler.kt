package trade.paper.app.views

import trade.paper.app.models.rest.ActiveOrdersResponse
import trade.paper.app.models.rest.BalanceResponse
import trade.paper.app.models.rest.TradingBalanceResponse

abstract class EstimatedBalanceHandler {
    open fun onBalanceReady(response: List<BalanceResponse>) {}
    open fun onTradingBalanceReady(response: List<TradingBalanceResponse>) {}
    open fun onActiveOrders(response: List<ActiveOrdersResponse>) {}
    abstract fun showAccessDenied()

}