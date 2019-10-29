package trade.paper.app.models.rpc.params

data class TickerParams(
        //var ask: String,
        //var bid: String,
        var last: String,
        var open: String,
        var low: String,
        var high: String,
        var volume: String,
        var volumeQuote: String,
        var timestamp: String,
        var symbol: String
)