package trade.paper.app.models.rest

import java.io.Serializable
import java.math.BigInteger

data class OrderHistoryResponse(
        val id: BigInteger,
        val clientOrderId: String,
        val orderId: BigInteger,
        val symbol: String,
        val side: String,
        val quantity: String,
        val price: String,
        val fee: String,
        val timestamp: String
): Serializable