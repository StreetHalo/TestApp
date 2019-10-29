package trade.paper.app.models.rest.paper


import com.google.gson.annotations.SerializedName

data class TradesReport(
    @SerializedName("commissionAsset")
    val commissionAsset: String,
    @SerializedName("fee")
    val fee: Double,
    @SerializedName("id")
    val id: Int,
    @SerializedName("price")
    val price: Double,
    @SerializedName("quantity")
    val quantity: Double,
    @SerializedName("timestamp")
    val timestamp: String
)