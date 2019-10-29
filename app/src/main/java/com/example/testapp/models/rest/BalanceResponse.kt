package trade.paper.app.models.rest

import com.google.gson.annotations.SerializedName

data class BalanceResponse(
        @SerializedName("asset")
        var currency: String,
        var available: String?,
        var reserved: String
)