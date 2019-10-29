package trade.paper.app.models.rpc.params

data class SnapshotOrderbookParams(
        val ask: List<AskBid>,
        val bid: List<AskBid>,
        val symbol: String,
        val sequence: Long
) : Params
