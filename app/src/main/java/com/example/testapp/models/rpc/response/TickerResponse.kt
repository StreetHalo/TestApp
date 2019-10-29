package trade.paper.app.models.rpc.response

import com.google.gson.annotations.SerializedName
import trade.paper.app.models.rpc.params.TickerParams

data class TickerResponse(
        @SerializedName("jsonrpc") val jsonrpc: String = "2.0",
        @SerializedName("method") var method: String,
        @SerializedName("params") var params: TickerParams,
        @SerializedName("id") var id: String
)