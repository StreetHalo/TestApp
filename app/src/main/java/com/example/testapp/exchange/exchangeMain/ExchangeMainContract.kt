package trade.paper.app.fragments.exchange.exchangeMain

import android.widget.EditText
import trade.paper.app.models.rest.ActiveOrdersResponse
import trade.paper.app.models.rest.paper.NewOrderResponse
import trade.paper.app.models.rpc.params.AskBid
import trade.paper.app.models.rpc.response.NewOrderResult
import java.math.BigDecimal

interface ExchangeMainContract {
    interface View {
        fun toastUploadError()
        fun hideOrderbook()
        fun showOrderbook()
        fun getTotal(): Double?
        fun getPrice(): Double?
        fun getAmount(): Double?
        fun setTotal(value: BigDecimal)
        fun setAmount(value: BigDecimal)
        fun updateOrderbook(askList: List<AskBid>, bidList: List<AskBid>, isFirstEmit: Boolean = false)
        fun setEdittextsEnabled(state: Boolean)
        fun setPriceUnlocked(state: Boolean)
        fun setEdittextsTicks(amountTick: BigDecimal, priceTick: BigDecimal, totalTick: BigDecimal)
        fun setBaseFeeCurrencies(baseCurrency: String, feeCurrency: String)
        fun setFee(feePercent: BigDecimal, fee: BigDecimal)
        fun setRebate(rebatePercent: BigDecimal, rebate: BigDecimal)
        fun setLastTradePrice(price: String, isIncrease: Boolean)
        fun setAvialable(balance: String, currency: String)
        fun invalidateBuySellButton()
        fun marketOrderType()
        fun limitOrderType()
        fun updateMarketPrices(buyPrice: BigDecimal, sellPrice: BigDecimal)
        fun onNewOrderSuccess(result: NewOrderResult)
        fun onNewOrderSuccess(result: NewOrderResponse)
        fun onNewOrderError(error: String)
        fun onNewOrderError(error: Int)
        fun openLoginFragment()
        fun setMyOrders(orders: List<ActiveOrdersResponse>)
        fun setPrice(price: BigDecimal)
        fun setAvailableVisible(visible: Boolean)
        fun setOrderTypeSelected()
        fun setOrderbookSelectorText(type: OrderbookExchangeSelectorEnum)
        fun setAccuracyToAdapter(amountAccuracy: Int, priceAccuracy: Int)
        fun setAccuracyToAdapter(accuracy: Int)
        fun setAccuracyToLeftPartOrderbook(accuracy: Int)
        fun setupViewsVisibility()
      //  fun setAccuracyAvailable(type: , isAvailable: Boolean)
        fun showAccessDenied()
        fun setEnableBtnBuySell(isEnable: Boolean)
        fun getOrderBookHeight() : Float
        fun getOrderBookItemHeight() : Float
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun setCurrency(currency: String)
        fun subscribeOnOrderbook()
        fun getSymbolInfo()
        fun calculateTotal()
        fun calculateAmount()
        fun unsubscribeOnOrderbook()
        fun pause()
        fun subscribeOnTickers()
        fun getTradingBalance()
        fun tabSellSelected()
        fun tabBuySelected()
        fun setSide(side: ExchangeMainFragment.Side)
        fun unsubscribeOnTickers()
        fun marketOrderTypeSelected()
        fun limitOrderTypeSelected()
        fun newOrder(symbol: String, side: String, quantity: String, type: ExchangeMainFragment.OrderType, price: String?, stopPrice: String?, timeInForce: ExchangeMainFragment.TimeInForce?, expireTime: String?, postOnly: Boolean?)
        fun getMyOrders()
        fun updateOrderbook(isFirstEmit: Boolean = false)
        fun changeAccuracy(diff: Int)
        fun changeOrderbookExchangeSelector(type: OrderbookExchangeSelectorEnum)
        fun onAvailableClick(available: String, price: String)
        fun restoreAmount(amountET: EditText, ifNotRestored: () -> Double)
        fun calculateFee()
        fun getUserFees()
    }
}