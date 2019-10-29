package trade.paper.app.models.rpc.params

data class CandlesData(
        val close: String,
        val max: String,
        val min: String,
        val open: String,
        val timestamp: String,
        val volume: String,
        val volumeQuote: String
): ChartData
data class CandlesResponse(
        var data: List<CandlesData>,
        var symbol: String,
        var period: String
)