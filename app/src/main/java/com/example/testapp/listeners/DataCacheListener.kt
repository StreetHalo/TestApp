package trade.paper.app.listeners

import trade.paper.app.models.rpc.params.TickerParams

interface DataCacheListener {
    fun tickerChanged(ticker: TickerParams) {}

    fun tickersSnapshot(tickers: List<TickerParams>) {}
}