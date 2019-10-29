package trade.paper.app.models.rpc.params

import trade.paper.app.models.rest.BalanceResponse

data class BalanceParams(
        val data: List<BalanceResponse>
)