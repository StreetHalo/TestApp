package trade.paper.app.models.rpc

/**
 * All possible RPC-methods for HITBTC API research
 */
enum class RPCMethods(val method: String) {
    SUBSCRIBE_ORDERBOOK("subscribeOrderbook"),
    UNSUBSCRIBE_ORDERBOOK("unsubscribeOrderbook"),
    LOGIN("login"),
    GET_TRADING_BALANCE("getTradingBalance"),
    GET_CURRENCIES("getCurrencies"),
    //quickfix
    SUBSCRIBE_TICKER("subscribeTicker"), SUBSCRIBE_TİCKER("subscribeTicker"),
    UNSUBSCRIBE_TICKER("unsubscribeTicker"),
    SUBSCRIBE_TICKERS("subscribeTickers"),
    UNSUBSCRIBE_TICKERS("unsubscribeTickers"),
    GET_TICKER("getTickers"),
    GET_SYMBOLS("getSymbols"),
    GET_SYMBOL("getSymbol"),
    GET_ACTIVE_ORDERS("getOrders"),
    NEW_ORDER("newOrder"),
    SUBSCRIBE_TRADES("subscribeTrades"),
    UNSUBSCRIBE_TRADES("unsubscribeTrades"),
    SUBSCRIBE_CANDLES("subscribeCandles"),
    UNSUBSCRIBE_CANDLES("unsubscribeCandles"),
    ACTIVE_ORDERS("activeOrders"),
    SUBSCRIBE_REPORTS("subscribeReports"),
    SUBSCRIBE_BALANCE("subscribeBalance")
}