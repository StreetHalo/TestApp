package trade.paper.app.models.rest

import trade.paper.app.models.rpc.params.AskBid

data class AskBidResponse(
        val ask: List<AskBid>,
        val bid: List<AskBid>,
        val timestamp: String
)
