package trade.paper.app.models.rest

data class TradingBalanceResponse(
        val currency: String,
        val available: String?,
        val reserved: String?
)