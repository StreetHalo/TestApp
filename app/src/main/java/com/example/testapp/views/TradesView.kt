package trade.paper.app.views

import trade.paper.app.models.rpc.params.TickerParams

interface TradesView : AskBidView {
    fun onNewOrderSucces(id: String)
    fun onNewOrderError(error: String)
    fun showAvialable(balance: String, currency: String)
    fun onTick(tickerParams: TickerParams?)
    fun showPrice(price: String)
    fun updateTickSize(tickSize: Double, quantityIncrement: Double)
    fun enableBuyBtn()
    fun disbleBuyBtn()
    fun updateFees(takeLiquidityRate: Double, provideLiquidityRate: Double)

}