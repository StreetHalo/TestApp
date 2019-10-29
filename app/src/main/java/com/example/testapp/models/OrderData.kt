package trade.paper.app.models

data class OrderData(
    val price: String,
    val side: String,
    val symbol: String,
    val id: String
)