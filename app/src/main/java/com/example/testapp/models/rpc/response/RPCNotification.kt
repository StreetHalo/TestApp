package trade.paper.app.models.rpc.response

import com.google.gson.annotations.SerializedName


data class RPCNotification<T>(
        @SerializedName("jsonrpc") val jsonrpc: String = "2.0",
        @SerializedName("method") var method: String,
        @SerializedName("params") var params: T,
        @SerializedName("id") var id: String
)