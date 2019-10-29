package trade.paper.app.models.rest

import trade.paper.app.models.rpc.params.ChartData

data class TradesResponse(
        val id: Int,
        val price: String,
        val quantity: String,
        val side: String,
        val timestamp: String
): ChartData