package trade.paper.app.models.rpc

import com.google.gson.annotations.SerializedName
import trade.paper.app.models.rpc.params.Params


data class RPCRequest(
        @SerializedName("jsonrpc") val jsonrpc: String = "2.0",
        @SerializedName("method") var method: String,
        @SerializedName("params") var params: Params?,
        @SerializedName("id") var id: String
)