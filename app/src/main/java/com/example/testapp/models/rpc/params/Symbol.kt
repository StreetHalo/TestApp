package trade.paper.app.models.rpc.params

import com.google.gson.annotations.SerializedName

/**
 * Currency symbols (currency pairs) traded on HitBTC exchange.
 * @see <a href="HitBTC - symbols">https://github.com/hitbtc-com/hitbtc-api/blob/master/APIv2.md#symbols</a>
 */
data class Symbol(
        @SerializedName("symbol") var symbol: String
) : Params