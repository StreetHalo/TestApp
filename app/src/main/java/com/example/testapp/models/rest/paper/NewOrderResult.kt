package trade.paper.app.models.rest.paper


import com.google.gson.annotations.SerializedName

data class NewOrderResponse(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("cumQuantity")
    val cumQuantity: Double,
    @SerializedName("id")
    val id: Int,
    @SerializedName("price")
    val price: Double,
    @SerializedName("quantity")
    val quantity: Double,
    @SerializedName("side")
    val side: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("tradesReport")
    val tradesReport: List<TradesReport>,
    @SerializedName("updatedAt")
    val updatedAt: String
)