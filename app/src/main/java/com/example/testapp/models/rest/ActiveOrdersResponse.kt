package trade.paper.app.models.rest

import trade.paper.app.models.rest.paper.TradesReport

data class ActiveOrdersResponse(
        val id: Long,
        val clientOrderId: String,
        val symbol: String,
        val side: String,
        val status: String,
        val type: String,
        val timeInForce: String?,
        val quantity: String,
        val price: String,
        val cumQuantity: String?,
        val createdAt: String,
        val updatedAt: String,
        val tradesReport: List<TradesReport>? = null
)