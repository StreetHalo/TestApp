package trade.paper.app.fragments.exchange.ExchangeTimeAndSales

import android.util.Log
import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.rpc.RPCMethods
import trade.paper.app.models.rpc.params.Symbol
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ExchangeTimeAndSalesPresenter : ExchangeTimeAndSalesContract.Presenter {

    private var view: ExchangeTimeAndSalesContract.View? = null

    private var currency = "BTCUSD"

    private var haveSnapshot = false

    private var isSubscribed = false

    private val compositeDisposable = CompositeDisposable()

    override fun attachView(view: ExchangeTimeAndSalesContract.View) {
        this.view = view
    }

    override fun detachView() {
        compositeDisposable.dispose()
        isSubscribed = false
        this.view = null
    }

    override fun unsubscribeOnTrades() {
        compositeDisposable.clear()
        isSubscribed = false
    }

    override fun setCurrency(currency: String) {
        this.currency = currency
    }

    override fun subscribeOnTrades() {
        if (!isSubscribed) {
            haveSnapshot = false

            getTradesSnapshot()

            compositeDisposable.add(RXCache.tradesUpdate
                    .subscribeOn(Schedulers.io())
                    .filter {
                        (it.params.data.isNotEmpty() && it.params.symbol == currency)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                if (it.method.equals("snapshotTrades", true)) {
                                    haveSnapshot = true
                                    view?.setTimesAndSalesSnapShot(it.params.data)
                                } else {
                                    view?.updateTimesAndSales(it.params.data)
                                }
                            },
                            {
                                view?.toastUploadError()
                                Log.d("ERROR_TIMES_AND_SALES", it.message)
                            }
                    )
            )
        }
    }

    private fun getTradesSnapshot() {
        RXCache.getTrades(currency)?.let {
            haveSnapshot = true
            view?.setTimesAndSalesSnapShot(it)
        }
    }

    override fun getSymbolInfo() {

        val symbol = RXCache.getSymbol(currency)
        if (symbol == null) {
            compositeDisposable.addAll(RXCache.symbolUpdate
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view?.settingHeadline(it.baseCurrency, it.feeCurrency)
                    }, {
                        view?.toastUploadError()
                        Log.d("ERROR_GET_SYMBOL", it.message)
                    })
            )
        } else {
            view?.settingHeadline(symbol.baseCurrency, symbol.feeCurrency)
        }
    }
}