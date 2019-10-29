package trade.paper.app.models.rpc.params

class CandlesParams(
        var symbol: String,
        var period: String,
        var limit: Int? = null
): Params