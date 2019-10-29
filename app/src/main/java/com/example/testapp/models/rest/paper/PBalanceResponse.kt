package trade.paper.app.models.rest.paper


import com.google.gson.annotations.SerializedName

data class PBalanceResponse(
    @SerializedName("asset")
    val asset: String,
    @SerializedName("available")
    val available: Double,
    @SerializedName("reserved")
    val reserved: Double
)