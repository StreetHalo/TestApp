package trade.paper.app.models.rpc.response

data class NewOrderResult(
        val id: String,
        val clientOrderId: String,
        val symbol: String,
        val side: String,
        val status: String,
        val type: String,
        val timeInForce: String,
        val quantity: String,
        val price: String?,
        val cumQuantity: String,
        val createdAt: String,
        val updatedAt: String,
        val reportType: String
)