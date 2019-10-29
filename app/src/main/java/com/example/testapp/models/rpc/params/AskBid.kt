package trade.paper.app.models.rpc.params

import java.math.BigDecimal

open class AskBid(
    val price: BigDecimal,
    var size: Double
)
