package trade.paper.app.models.cache

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.testapp.Formatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import trade.paper.app.models.CryptoPaymentIdName
import trade.paper.app.models.TAG
import trade.paper.app.models.dto.SymbolDTO
import trade.paper.app.models.hawk.Settings
import trade.paper.app.models.rest.*
import trade.paper.app.models.rest.paper.PBalanceResponse
import trade.paper.app.models.rpc.RPCMethods
import trade.paper.app.models.rpc.params.*
import trade.paper.app.models.rpc.response.RPCNotification
import trade.paper.app.models.rpc.response.RPCResponse
import java.io.IOException
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

object RXCache {

    class OrderbookHolder {
        var ask: ConcurrentHashMap<Double, AskBid> = ConcurrentHashMap()
        var bid: ConcurrentHashMap<Double, AskBid> = ConcurrentHashMap()

        fun mergeMap(map: MutableMap<Double, AskBid>, list: List<AskBid>) {
            for (item in list) {
                if (item.size == 0.0) map.remove(item.price.toDouble())
                else map[item.price.toDouble()] = item
            }
        }

        fun getAskList(limit: Int): List<AskBid> {
            return ask.map { it.value }.sortedBy { it.price }.take(limit)
        }

        fun getBidList(limit: Int): List<AskBid> {
            return bid.map { it.value }.sortedByDescending { it.price }.take(limit)
        }
    }

    const val FAVORITES: String = "Favorites"

    var compositeDisposable = CompositeDisposable()

    var stock = ""
    var crypto = ""
    var hasBaseData = false
    var orderbookCache = hashMapOf<String, OrderbookHolder?>()
    var symbolTabs = arrayListOf<String>()
    var symbols = hashMapOf<String, SymbolResponse>()
    var tickers = hashMapOf<String, TickerParams>()
    var currencies = hashMapOf<String, CurrencyResponce>()
    @Volatile
    var balances = hashMapOf<String, BalanceResponse>()
    var paperBalances = hashMapOf<String, PBalanceResponse>()
    var tradingBalances = hashMapOf<String, TradingBalanceResponse>()
    var transactionHistory = hashMapOf<String, TransactionHistoryResponce>()
    var orderHistory = hashMapOf<String, OrderHistoryResponse>()
    var candlesCache = hashMapOf<String, HashMap<String, MutableList<CandlesData>>>()
    var tradesCache = hashMapOf<String, MutableList<TradesResponse>>()
    var activeOrders = arrayListOf<ActiveOrdersResponse>()
    var ordersBalance = hashMapOf<String, Double>()
    var accessDeniedBalance = false
    var isBalanceDeniedDialogShown = false
    var isAccountBalanceDeniedDialogShown = false
    var accessDeniedTradingBalance = false
    var accessDeniedOrders = false
    val onOverflow: () -> Unit = { Log.d("overflow", "overflow") }

    fun clear() {
        hasBaseData = false
        /*symbolTabs.clear()
        symbols.clear()
        tickers.clear()*/
        transactionHistory.clear()
        //currencies.clear()
        balances.clear()
        tradingBalances.clear()
        orderHistory.clear()
       /* candlesCache.clear()
        orderbookCache.clear()*/
        activeOrders.clear()
        ordersBalance.clear()
    }

    fun dispose() {
      //  Subscribtions.unsubscribeAll()
        compositeDisposable.clear()
      //  disposable.dispose()
        loginDisposable.dispose()

    }

   /* val disposable = Socket
            .socketListener
            .processor
            .subscribeOn(Schedulers.io())
            .onBackpressureDrop() //TODO: Слабое место, данные могут дропаться
            .subscribe(
                    { onMsgReceived(it) },
                    { Log.d("ERROR", it?.localizedMessage ?: "error") })*/

    private val orderbookProcessor = PublishProcessor.create<String>()
    private val loginProcessor = PublishProcessor.create<String>()
    private val tradesProcessor = PublishProcessor.create<String>()
    private val candlesProcessor = PublishProcessor.create<String>()
    private val symbolProcessor = PublishProcessor.create<String>()
    private val reportsProcessor = PublishProcessor.create<String>()
    private val balanceProcessor = PublishProcessor.create<String>()

    var paymentIdNames = HashMap<String, CryptoPaymentIdName>()

    val loginDisposable =
            loginProcessor
                    .map { Gson().fromJson<RPCResponse<String>>(it) }
                    .subscribe({ onLogin.onNext(it) }, {})


    fun <T> processor(): PublishProcessor<T> {
        return PublishProcessor.create<T>()
    }

    val onBaseDataReady = PublishProcessor.create<Object>()
    val onBalanceSnapshot = PublishProcessor.create<List<BalanceResponse>>()
    val onPaperBalanceSnapshot = PublishProcessor.create<List<PBalanceResponse>>()
    val onActiveOrders = PublishProcessor.create<List<ActiveOrdersResponse>>()
    val onTradingBalanceSnapshot = PublishProcessor.create<List<TradingBalanceResponse>>()
    val onTransactionHistory = PublishProcessor.create<List<TransactionHistoryResponce>>()
    var onLogin = PublishProcessor.create<RPCResponse<String>>()
    val isLogined = MutableLiveData<Boolean>()
    val onEnter = PublishProcessor.create<Object>()
    val onTick = PublishProcessor.create<List<TickerParams>>()
    private var onReports: PublishProcessor<List<ActiveOrdersResponse>>? = null
    private var onBalanceUpdate: PublishProcessor<List<BalanceResponse>>? = null


    val favorites = MutableLiveData<List<SymbolDTO>>()
    val favoriteChange = MutableLiveData<String>()
    val newOrderCreated = MutableLiveData<String>()
    val orderCanceled = MutableLiveData<String>()
    val transferCompleted = MutableLiveData<String>()

    val onOrderBookUpdate: PublishProcessor<RPCNotification<SnapshotOrderbookParams>> by lazy {
        val p = processor<RPCNotification<SnapshotOrderbookParams>>()
        compositeDisposable.add(
                orderbookProcessor
                        .onBackpressureBuffer()
                        .observeOn(Schedulers.io())
                        .subscribeOn(Schedulers.computation())
                        .filter {
                            !it.contains("\"result\":true")
                        }
                        .map {
                            try {
                                Gson().fromJson<RPCNotification<SnapshotOrderbookParams>>(it)
                            }catch (e: Exception){
                                null
                            }
                        }
                        .subscribe({
                            it?.let {
                                val symbol = it.params.symbol

                                if (!orderbookCache.containsKey(symbol) || it.method.contains("snapshotOrderbook", true)) orderbookCache[symbol] = OrderbookHolder()

                                val orderbook = orderbookCache[symbol]!!

                                orderbook.mergeMap(orderbook.ask, it.params.ask)
                                orderbook.mergeMap(orderbook.bid, it.params.bid)

                                p.onNext(it)
                            }
                        }, {
                            Log.d("orderBookDebug", it.localizedMessage ?: "err in order")
                        })
        )
        return@lazy p
    }
    val tradesUpdate: PublishProcessor<RPCNotification<Data>> by lazy {
        val p = processor<RPCNotification<Data>>()
        compositeDisposable.add(
                tradesProcessor
                        .onBackpressureDrop()
                        .observeOn(Schedulers.io())
                        .map { Gson().fromJson<RPCNotification<Data>>(it) }
                        .subscribe({

                            val symbol = it.params.symbol

                            if (!tradesCache.containsKey(symbol) || it.method.contains("snapshotTrades", true)) tradesCache[symbol] = arrayListOf()


                            tradesCache[symbol]?.addAll(it.params.data)
                            p.onNext(it)
                        }, {
                            Log.d("err", it.localizedMessage ?: "err in trades")
                        })
        )
        return@lazy p
    }
    val candlesUpdate: PublishProcessor<RPCNotification<CandlesResponse>> by lazy {
        val p = processor<RPCNotification<CandlesResponse>>()
        compositeDisposable.add(
                candlesProcessor
                        .onBackpressureDrop()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(Schedulers.io())
                        .map {
                            Gson().fromJson<RPCNotification<CandlesResponse>>(it)
                        }
                        .filter { it.params.data != null && it.params.data.isNotEmpty() }
                        .subscribe({
                            val symbol = it.params.symbol

                            if (!candlesCache.containsKey(symbol)) candlesCache[symbol] = hashMapOf()

                            val candles = candlesCache[symbol]!!

                            if (it.params.data.isEmpty() || it.params.data.last() == null) return@subscribe

                            if (candles[it.params.period] == null)
                                candles[it.params.period] = mutableListOf()

                            candles[it.params.period]?.let { cands ->
                                val updatedId = cands.indexOfLast { candle -> candle.timestamp == it.params.data.last().timestamp }

                                when {
                                    it.method == "snapshotCandles" -> {
                                        candles[it.params.period]?.apply {
                                            clear()
                                            addAll(it.params.data)
                                        }
                                    }
                                    cands.isNotEmpty() && (updatedId != -1) -> {
                                        cands[updatedId] = it.params.data.last()
                                    }
                                    cands.isNotEmpty() && it.params.data?.last().timestamp == cands.last().timestamp -> {
                                        cands[cands.lastIndex] = it.params.data.last()
                                    }
                                    else -> cands.addAll(it.params.data)
                                }
                            }
                            p.onNext(it)
                        },
                                { Log.d("err", it.localizedMessage ?: "err in candles") })
        )
        return@lazy p
    }
    val symbolUpdate: PublishProcessor<SymbolResponse> by lazy {
        val p = processor<SymbolResponse>()
        compositeDisposable.add(
                symbolProcessor
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(
                                {
                                    try {
                                        p.onNext(Gson().fromJson<RPCResponse<SymbolResponse>>(it).result)
                                    } catch (e: Exception) {
                                    }
                                },
                                {
                                    Log.d(TAG.ERROR.tag, it.localizedMessage)
                                }
                        )
        )
        return@lazy p
    }


    val candlesChart = PublishProcessor.create<RPCNotification<CandlesResponse>>()

    fun reportsUpdate(): PublishProcessor<List<ActiveOrdersResponse>>{
         if (onReports ==null) {
            onReports = PublishProcessor.create()
            compositeDisposable.add(
                    reportsProcessor
                            .onBackpressureBuffer()
                            .observeOn(Schedulers.io())
                            .subscribeOn(Schedulers.computation())
                            .filter {
                                !it.contains("\"result\":true")
                            }
                            .map {
                                try {
                                    val single = Gson().fromJson<RPCNotification<ActiveOrdersResponse>>(it)
                                    listOf(single.params)
                                }catch (e: Exception){
                                    Gson().fromJson<RPCNotification<List<ActiveOrdersResponse>>>(it).params
                                }
                            }
                            .retry()
                            .subscribe({
                                it?.let {
                                    onReports?.onNext(it)
                                }
                            }, {
                                Log.d("reports", it.localizedMessage ?: "err in reports")
                            })

            )
       //      Socket.sendMessage(RPCMethods.SUBSCRIBE_REPORTS, EmptyParams())

        }
        return onReports!!
    }

    fun onBalanceUpdate(): PublishProcessor<List<BalanceResponse>> {
        if (onBalanceUpdate == null){
            onBalanceUpdate = processor()
            compositeDisposable.add(
                    balanceProcessor
                            .onBackpressureBuffer()
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.computation())
                            .filter {
                                !it.contains("\"result\":true")
                            }
                            .map {
                                try {
                                    val n = Gson().fromJson<RPCNotification<BalanceParams>>(it)
                                    n.params.data
                                }catch (e: Exception){
                                    listOf<BalanceResponse>()
                                }
                            }
                            .retry()
                            .subscribe({
                                it?.let {
                                    onBalanceUpdate?.onNext(it)
                                }
                            }, {
                                Log.d("reports", it.localizedMessage ?: "err in reports")
                            })
            )
        }
        return onBalanceUpdate!!
    }

    init {
        compositeDisposable.addAll(
                candlesUpdate
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onBackpressureDrop()
                        .subscribe(
                                { candlesChart.onNext(it) },
                                { Log.d("err", it.localizedMessage ?: "err in candles") }
                        ),
                onBalanceUpdate()
                        .subscribeOn(Schedulers.computation())
                        .subscribe({
                            handleBalancesSnapshot(it)
                        }, {
                            Log.e("error in", this::class.java.simpleName)
                        })
        )

        Handler(Looper.getMainLooper()).post {
         //   isLogined.value = Settings.isLogin()
            favorites.value = getFavorites()
            favoriteChange.value = null
            newOrderCreated.value = null
            orderCanceled.value = null
            transferCompleted.value = null
        }
        initPaymentIds()
    }

    private fun initPaymentIds() {

    }

    fun onMsgReceived(msg: String) {
    }




    fun updateSymbolTab(){
        symbolTabs.clear()
        //symbolTabs.addAll(tabs.toList().sortedByDescending { getSymbols(it).size })
        symbolTabs = arrayListOf(
                FAVORITES,
                stock,
                "$crypto BTC",
                "$crypto ETH",
                "$crypto USD"
        )
    }
    fun handleSymbols(list: List<SymbolResponse>) {

    }


     fun handleTickers(list: List<TickerParams>) {
        val filtered = list.filter {
            it.last != null && it.open != null
                    && it.high != null && it.low != null
                    && it.symbol != null
        }

        for (ticker in filtered) {
            if(tickers[ticker.symbol] == null ||
                    Formatter.dateFormat.parse(tickers[ticker.symbol]?.timestamp).before(Formatter.dateFormat.parse(ticker.timestamp))) {
                tickers[ticker.symbol] = ticker
            }
        }
         onTick.onNext(filtered)
     }

    private fun handleCurrenciesSnapshot(list: List<CurrencyResponce>) {

    }

    fun handleBalancesSnapshot(list: List<BalanceResponse>) {
        accessDeniedBalance = false
        balances.clear()

        for (balance in list) {
            if (balance.available == null) continue

            balances[balance.currency] = balance
        }

        onBalanceSnapshot.onNext(list)
    }

    fun getBalance(currency: String): BalanceResponse?{
        return balances[currency]
    }

    fun handleTradingBalancesSnapshot(list: List<TradingBalanceResponse>) {
        accessDeniedTradingBalance = false
        tradingBalances.clear()

        for (balance in list) {
            if (balance.available == null) continue

            tradingBalances[balance.currency] = balance
        }

        onTradingBalanceSnapshot.onNext(list)
    }

    fun handleTransactionHistorySnapshot(list: List<TransactionHistoryResponce>) {
        //transactionHistory.clear()

        for (historyItem in list) {
            transactionHistory[historyItem.id] = historyItem
        }

        //transactionHistory["asdhjgaskf"] = TransactionHistoryResponce("asdhjgaskf", 0, "BTC", 1.0, 0.0, "sdfbadjkf", null, "", "pending", "payin", Date(), Date())
        //transactionHistory["asdhjgaskf2"] = TransactionHistoryResponce("asdhjgaskf2", 0, "EOS", 1.0, 0.0, "sdfbadjkf", "12345", "", "success", "payout", Date(), Date())

        onTransactionHistory.onNext(list)
    }

    fun handleActiveOrders(list: List<ActiveOrdersResponse>) {
        activeOrders.clear()
        activeOrders.addAll(list)

        updateInOrdersBalanceMap()

        onActiveOrders.onNext(list)
    }

    fun handleActiveOrdersInUi(list: List<ActiveOrdersResponse>) {
        GlobalScope.launch(Dispatchers.Main) {
            handleActiveOrders(list)
        }
    }

    private fun updateInOrdersBalanceMap() {
        ordersBalance.clear()

        for (order in activeOrders) {
            if (order.type == "stopLimit" || order.type == "stopMarket") continue

            val symbol = getSymbol(order.symbol) ?: continue
            val currency = if (order.side == "buy") symbol.feeCurrency else symbol.baseCurrency

            if (!ordersBalance.containsKey(currency)) ordersBalance[currency] = 0.0

            val quantity = order.quantity.toDouble()
            val price = order.price.toDouble()
            val amount = if (order.side == "buy") quantity * price else quantity
            val fee = if (order.side == "buy") symbol.takeLiquidityRate * amount else 0.0

            ordersBalance[currency] = ordersBalance[currency]!! + amount //+ fee
        }
    }

    fun getSymbol(symbol: String): SymbolResponse? {
        return symbols[symbol]
    }

    fun getSymbols(currency: String): List<SymbolResponse> {
        return if(currency!= stock) symbols.filter { (it.value.baseCurrency == currency || it.value.feeCurrency == currency) && tickers.containsKey(it.value.id) && it.value.family!="STOCK"  }.values.toList()
        else symbols.filter { (it.value.family=="STOCK")}.values.toList()
    }

    fun getSymbolsDto(currency: String): List<SymbolDTO> {
        var editCurrency = ""
        if (currency == FAVORITES) return getFavorites()
        editCurrency = if(currency.contains("$crypto ")){
            currency.replace("$crypto ","")
        } else currency
        return getSymbols(editCurrency).map { toDto(it) }
    }

    fun getFavorites(): List<SymbolDTO> {
     return listOf(SymbolDTO("USDBTC","USDBTC","fee",0.1,0.2,0.3))
    }

    fun getAllSymbolsDto(): List<SymbolDTO> {
        return symbols.filter { tickers.containsKey(it.value.id) }.values.toList().map { toDto(it) }.sortedBy { it.symbol }
    }

    fun getOrdersForSymbol(symbol: String): List<ActiveOrdersResponse> {
        return activeOrders.filter { it.symbol == symbol }
    }

    fun removeOrder(clientOrderId: String) {
        activeOrders.removeAll(activeOrders.filter { it.clientOrderId == clientOrderId })
    }

    fun hasCandles(symbol: String, period: String): Boolean {
        return candlesCache.containsKey(symbol) && candlesCache[symbol]!!.containsKey(period)
    }

    fun getCandles(symbol: String, period: String): List<CandlesData>? {
        if (!hasCandles(symbol, period)) return null

        return candlesCache[symbol]!![period]
    }

    fun clearCanldeCache(symbol: String) {
        candlesCache[symbol]?.clear()
    }

    fun getTrades(symbol: String): List<TradesResponse>? {
        return tradesCache[symbol]
    }

    fun getOrderbook(symbol: String): OrderbookHolder? {
        return orderbookCache[symbol]
    }

    fun clearTradesCache(symbol: String) {
        tradesCache[symbol]?.clear()
    }

    fun clearOrderbook(symbol: String) {
        orderbookCache.remove(symbol)
    }

    fun toDto(symbol: SymbolResponse): SymbolDTO {
        val dto = SymbolDTO(symbol.id, symbol.baseCurrency, symbol.feeCurrency)
        val ticker = tickers[dto.symbol] ?: return dto
        if(symbol.family=="STOCK") dto.stock = true

        dto.volume = getVolumeFromTicker(ticker)
        dto.price = ticker.last.toDouble()

        val open = ticker.open.toDouble()
        val last = ticker.last.toDouble()
        var percent = ((last - open) / open) * 100.0

        if (percent.isNaN()) percent = 0.0

        dto.change24 = percent

        return dto
    }


    fun getVolumeFromTicker(ticker: TickerParams): Double {
        when {
            ticker.symbol.startsWith("BTCP") -> return convert(ticker)
            ticker.symbol.endsWith("BTC") -> return ticker.volumeQuote.toDouble()
            ticker.symbol.startsWith("BTC") -> return ticker.volume.toDouble()
            else -> {
                return convert(ticker)
            }
        }
    }

    fun convert(ticker: TickerParams): Double {
        var symbol = symbols[ticker.symbol] ?: return 0.0

        return convertCurrencyAmount(ticker.volumeQuote.toDouble(), symbol.quoteCurrency, "BTC")

    }

    fun getTicker(symbol: String): TickerParams? {
        var ticker = tickers[symbol]

        // Fix for part of tickers have USDT instead of USD
        if (ticker == null && symbol.endsWith("USD") && !symbol.endsWith("TUSD") && !symbol.endsWith("GUSD")) {
            ticker = tickers[symbol + "T"]
        }

        return ticker
    }

    fun getEstimatedPrice(price: Double, symbol: SymbolDTO): Double {
        if (symbol.fee == "BTC") return symbol.price
        else if (symbol.base == "BTC") return 1.0

        var ticker = getTicker(symbol.base + "BTC")

        if (ticker == null) {
            var feeBtcTicker = getTicker(symbol.fee + "BTC")
                    ?: getTicker("BTC" + symbol.fee)
            ?: return 0.0
            var feeBtcPrice = feeBtcTicker.last.toDouble()

            if (feeBtcTicker.symbol.startsWith("BTC")) feeBtcPrice = 1.0 / feeBtcPrice

            return price * feeBtcPrice
        }

        return ticker.last.toDouble()
    }

    fun getEstimatedBalance(currency: String): Double {
        return calcEstimated(balances[currency], tradingBalances[currency], "BTC")
    }

    fun getEstimatedBalance(currency: String, targetCurrency: String): Double {
        return calcEstimated(balances[currency], tradingBalances[currency], targetCurrency)
    }

    fun getMainEstimatedBalance(targetCurrency: String): Double {
        var b = 0.0
        val values = balances.values.toList()
        for (i in 0 until values.size) {
            val balance = values[i]
            b += calcEstimated(balance, null, targetCurrency)
        }
        return b
    }
    fun getTradingEstimatedBalance( targetCurrency: String): Double {
        var b = 0.0
        tradingBalances.forEach {
            b += calcEstimated(null, it.value, targetCurrency)
        }
        return b
    }

    fun getEstimatedBalanceUsd(currency: String): Double {
        val btcEstimated = getEstimatedBalance(currency)
        return calcEstimatedForBalanceDouble("BTC", btcEstimated, "USD")
    }


    fun getTotalEstimatedBalance(): Double {
        var result = 0.0

        val balancesList = balances.map { it.value }.filter { it.available != "0" }
        for (i in 0 until balancesList.size) {
            balancesList.getOrNull(i)?.let { balance ->
                result += calcEstimated(balance, null, "BTC")
            }
        }

        val tadingBalancesList = tradingBalances.map { it.value }.filter { it.available != "0" }
        for (i in 0 until tadingBalancesList.size) {
            tadingBalancesList.getOrNull(i)?.let { balance ->
                result += calcEstimated(null, balance, "BTC")
            }
        }

        for (entry in ordersBalance) {
            result += convertCurrencyAmount(entry.value, entry.key, "BTC")
        }

        return result
    }

    fun getTotalEstimatedBalanceUsd(): Double {
        var result = 0.0

        for (balance in balances.map { it.value }.filter { it.available != "0" }) {
            result += calcEstimated(balance, null, "USD")
        }

        for (balance in tradingBalances.map { it.value }.filter { it.available != "0" }) {
            result += calcEstimated(null, balance, "USD")
        }

        for (entry in ordersBalance) {
            result += convertCurrencyAmount(entry.value, entry.key, "USD")
        }

        return result
    }

    fun getInOrdersBalance(currency: String): Double {
        return ordersBalance[currency] ?: 0.0
    }

    private fun calcEstimated(balance: BalanceResponse?, tradingBalance: TradingBalanceResponse?, resultCurrency: String): Double {
        var result = 0.0

        if (balance != null) result += calcEstimatedForBalance(balance.currency, balance.available, resultCurrency)
        if (tradingBalance != null) result += calcEstimatedForBalance(tradingBalance.currency, tradingBalance.available, resultCurrency)

        return result
    }

    private fun calcEstimatedForBalance(balanceCurrency: String, balanceAvailable: String?, resultCurrency: String): Double {
        var available = (balanceAvailable?.toDouble() ?: 0.0)

        return calcEstimatedForBalanceDouble(balanceCurrency, available, resultCurrency)
    }

    fun calcEstimatedForBalanceDouble(balanceCurrency: String, available: Double, resultCurrency: String): Double {
        if (balanceCurrency == resultCurrency) {
            return available
        } else {
            var ticker = findNormalizedTicker(balanceCurrency, resultCurrency)
            if (ticker != null) return ticker * available

            if (resultCurrency == "BTC" || resultCurrency == "USD") {
                var currencyOrder = if(resultCurrency == "BTC") arrayListOf("ETH", "USD", "DAI", "TUSD", "EURS", "EOS") else arrayListOf("BTC", "ETH", "DAI", "TUSD", "EURS", "EOS")

                for (convertCurrency in currencyOrder) {
                    ticker = findNormalizedTicker(balanceCurrency, convertCurrency)
                    if (ticker == null || findNormalizedTicker(convertCurrency, resultCurrency) == null) continue

                    return calcEstimatedForBalanceDouble(convertCurrency, ticker * available, resultCurrency)
                }
            }
        }

        return 0.0
    }

    fun convertCurrencyAmount(amount: Double, fromCurrency: String, toCurrency: String): Double =
            calcEstimatedForBalanceDouble(fromCurrency, amount, toCurrency)

    fun findNormalizedTicker(balanceCurrency: String, resultCurrency: String): Double? {
        var ticker = getTicker(balanceCurrency + resultCurrency)
        if (ticker != null) return ticker.last.toDouble()

        ticker = getTicker(resultCurrency + balanceCurrency)
        if (ticker != null) return (1.0f / ticker.last.toDouble())

        return null
    }

    fun getCurrency(currency: String): CurrencyResponce? {
        return currencies[currency]
    }

    fun getCurrenciesForDeposit(): List<CurrencyResponce> {
        return getAllCurrencies().filter { it.payinEnabled }
    }



    fun getAllCurrencies(): List<CurrencyResponce> {
        return currencies.map { it.value }.sortedBy { it.id }.sortedByDescending { symbolTabs.reversed().indexOf(it.id) }
    }

    fun getBalances(skipLow: Boolean = true): List<BalanceResponse> {

        return balances.map { it.value }.filter {
            if (skipLow)

                (it.available?.toDouble()
                        ?: 0.0) > 0 || (tradingBalances[it.currency]?.available?.toDouble()
                        ?: 0.0) > 0 || ordersBalance[it.currency] ?: 0.0 > 0.0
            else true
        }.sortedBy { it.currency }.sortedByDescending { symbolTabs.reversed().indexOf(it.currency) }.sortedByDescending {
            (it.available?.toDouble() ?: 0.0) + (tradingBalances[it.currency]?.available?.toDouble()
                    ?: 0.0)
        }
    }

    fun getDepositHistory(): List<TransactionHistoryResponce> {
        return transactionHistory.filter { it.value.type == "payin" }.map { it.value }.sortedByDescending { it.createdAt }
    }

    fun getWithdrawHistory(): List<TransactionHistoryResponce> {
        return transactionHistory.filter { it.value.type == "payout" }.map { it.value }.sortedByDescending { it.createdAt }
    }

    fun getTransferHistory(): List<TransactionHistoryResponce> {
        return transactionHistory.filter { it.value.type == "bankToExchange" || it.value.type == "exchangeToBank" }.map { it.value }.sortedByDescending { it.createdAt }
    }
    fun getTransferHistoryDetail(currency: String): List<TransactionHistoryResponce> {
        return transactionHistory.filter { it.value.currency  ==currency }.map { it.value }.sortedByDescending { it.createdAt }
    }

    fun localTrasfer(currency: String, amount: Double, toTrading: Boolean) {
        val balance = balances[currency] ?: return
        val tradingBalance = tradingBalances[currency] ?: return

        val amountBigDecimal = BigDecimal(amount)
        var mainAmount = BigDecimal(balance.available ?: "0")
        var tradingAmount = BigDecimal(tradingBalance.available ?: "0")

        if (toTrading) {
            mainAmount = mainAmount.minus(amountBigDecimal)
            tradingAmount = tradingAmount.plus(amountBigDecimal)
        } else {
            mainAmount = mainAmount.plus(amountBigDecimal)
            tradingAmount = tradingAmount.minus(amountBigDecimal)
        }

        balances[currency] = BalanceResponse(currency, mainAmount.toString(), balance.reserved)
        tradingBalances[currency] = TradingBalanceResponse(currency, tradingAmount.toString(), tradingBalance.reserved)

        onBalanceSnapshot.onNext(arrayListOf())
        onTradingBalanceSnapshot.onNext(arrayListOf())
    }

    fun haveAnyTradingBalance(): Boolean{
        val lock = ReentrantLock()
        lock.lock()
        try {
            balances.values.forEach {
                it.available?.toDoubleOrNull()?.let {
                    if (it > 0.0) return true
                }
            }
            return false
        }
        catch (e: java.lang.Exception){
            return false
        }
        finally {
            lock.unlock()
        }

    }

    fun handleOrderBook(orderbook: AskBidResponse, symbol: String){
        if (!orderbookCache.containsKey(symbol)) orderbookCache[symbol] = OrderbookHolder().apply {
            mergeMap(ask, orderbook.ask)
            mergeMap(bid, orderbook.bid)

            onOrderBookUpdate.onNext(RPCNotification("", "", SnapshotOrderbookParams(emptyList(), emptyList(), symbol, 0L), ""))
        }
    }
}