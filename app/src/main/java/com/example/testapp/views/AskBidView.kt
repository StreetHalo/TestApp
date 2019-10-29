package trade.paper.app.views

import trade.paper.app.models.rpc.params.AskBid

interface AskBidView : BaseView {
    fun updateAsk(ask: List<AskBid>, bestAsk: AskBid? = null)
    fun updateBid(bid: List<AskBid>, bestBid: AskBid? = null)
    fun updateOrderBook(ask: List<AskBid>, bid: List<AskBid>) {}
}