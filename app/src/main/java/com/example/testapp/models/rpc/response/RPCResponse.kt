package trade.paper.app.models.rpc.response

import trade.paper.app.models.rpc.Error

data class RPCResponse<T>(
        val jsonrpc: String,
        val result: T,
        val error: Error?,
        val id: String
)