package trade.paper.app.models.rpc.params

data class NewOrderParams(
        val clientOrderId: String,
        val symbol: String,
        val side: String,
        val price: Double?,
        val type: String?,
        val timeInForce: String? = null,
        val expireTime: String? = null,
        val quantity: Double,
        val stopPrice: Double? = null,
        val postOnly: Boolean? = null
) : Params