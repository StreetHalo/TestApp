package trade.paper.app.models.rpc.params

import trade.paper.app.models.rest.TradesResponse

data class Data(
        val data: List<TradesResponse>,
        val symbol: String
)