package trade.paper.app.models.rpc

import com.google.gson.annotations.SerializedName

/**
 * Base error response model
 * @see <a href="HitBTC - error">https://github.com/hitbtc-com/hitbtc-api/blob/master/APIv2.md#error-response</a>
 */


data class Error(
        @SerializedName("code") var code: Int,
        @SerializedName("message") var message: String,
        @SerializedName("description") var description: String
)
