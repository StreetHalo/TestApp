package trade.paper.app.fragments.exchange.exchangeMain

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.core.view.ViewCompat
import android.text.*
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.testapp.BaseFragment
import com.example.testapp.R
import trade.paper.app.adapters.OrderbookRecyclerAdapter
import trade.paper.app.fragments.exchange.exchangeMain.ExchangeMainFragment.TimeInForce.*
import trade.paper.app.models.cache.AppCache
import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.hawk.Settings
import trade.paper.app.models.rest.ActiveOrdersResponse
import trade.paper.app.models.rpc.params.AskBid
import trade.paper.app.models.rpc.response.NewOrderResult
import trade.paper.app.utils.*

import trade.paper.app.utils.extensions.*
import kotlinx.android.synthetic.main.dialog_orderbook_type.view.*
import kotlinx.android.synthetic.main.fragment_main_exchange.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import trade.paper.app.models.rest.paper.NewOrderResponse
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class ExchangeMainFragment : BaseFragment(), ExchangeMainContract.View {

    companion object {
        const val MAX_ORDER_BOOK_COUNT = 50
    }

    private var tabPositionToReportsFragment = 0

    private val askBidRvAdapter = OrderbookRecyclerAdapter()

    private val toast by lazy { Toast.makeText(context, "", Toast.LENGTH_LONG) }

    private val totalFormatter = DecimalFormat()
    private val amountFormatter = DecimalFormat.getInstance(Locale("en-US")) as DecimalFormat
    private val priceFormatter = DecimalFormat()
    private val feeFormatter = (DecimalFormat.getNumberInstance(Locale("en-US")) as DecimalFormat).apply {
        applyLocalizedPattern("#.########")
    }

    private val editTextList = ArrayList<EditText>()

    private val orderTypes by lazy { arrayOf(getString(R.string.order_limit), getString(R.string.order_market)) }

    private var presenter: ExchangeMainContract.Presenter? = null

    private var currency = "BTCUSD"

    private var baseCurrency = "BTC"

    private var feeCurrency = "USD"

    private var maxOrderbookCount = MAX_ORDER_BOOK_COUNT

    private var side = Side.BUY

    private var orderType = OrderType.LIMIT

    private var timeInForce = GTC
    private var timeForGtdt = ""

    private var isSentOrderbookScrollEvent = false

    private var isConnected = true
    private var avblBalance = 0.0

    private var isBackFromPause = false
    private var isStock = false
    private lateinit var orderTypeSelector: PopupWindow
    private lateinit var orderTypeSelectorDialog: BottomSheetDialog
    private lateinit var orderExchangeTypeSelectorDialog: BottomSheetDialog

    private var isSubscribe = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

      //  presenter = ExchangeMainPresenter()

        arguments?.let {
            isStock = it.getBoolean("isStock",false)
            currency = it.getString("currency", "BTCUSD")
            presenter?.setCurrency(currency)
            side = if (it.getString("side", "buy") == "buy") Side.BUY else Side.SELL
            presenter?.setSide(side)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_exchange, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.attachView(this)
        tv_order_type_exchange.text = orderTypes[if (orderType==OrderType.LIMIT)0 else 1]

        initEditTexts()
        initOrderbookRecycler()
        initBottomSheetOrder()
        initAvblTextView()
        settingClickListeners()
        setupViewsVisibility()
        if(!isStock) initBottomMargin()
        RXCache.isLogined.observe(this, androidx.lifecycle.Observer {
            it?.let { isLogined ->
                if (isLogined){
                    presenter?.getUserFees()
                    //tv_fee_exchange.visible()
                    //tv_rebate_exchange.visible()
                } else {
                    tv_fee_exchange.gone()
                    tv_rebate_exchange.gone()
                }
            }
        })

        RXCache.orderCanceled.observe(this, androidx.lifecycle.Observer {
            it?.let {
                presenter?.getMyOrders()
                presenter?.getTradingBalance()
                askBidRvAdapter.removeOrder(it)
            }
        })

        RXCache.transferCompleted.observe(this, androidx.lifecycle.Observer {
            it?.let {
                presenter?.getTradingBalance()
            }
        })

       /* ApiRightsStateScheduler.accessKeys[ApiRights.PlaceCancelOrders]?.observe(this, androidx.lifecycle.Observer { state ->
            state?.let {
                if (it == State.DENIED) {
                    btn_buy_sell_exchange.alpha = 0.5f
                } else if (it == State.ALLOWED) {
                    btn_buy_sell_exchange.alpha = 1f
                }
            }
        })*/
/*
        ActiveOrderScheduler.hasActiveOrders.observe(this, androidx.lifecycle.Observer {
            it?.let {
                if (it){
                    btn_active_orders_exchange.visible()
                    presenter?.getMyOrders()
                } else {
                    askBidRvAdapter.setMyOrders(emptyList())
                    btn_active_orders_exchange.gone()
                }
            }
        })
        RotationManager.unbind()
*/
    }

    private fun initBottomMargin() {
       val param = linearLayout.layoutParams as ConstraintLayout.LayoutParams
        param.bottomMargin = 40
        linearLayout.layoutParams = param
    }

    override fun onShow() {
        super.onShow()
      //  RotationManager.unbind()
    }

    override fun onStart() {
        super.onStart()
       // ActiveOrderScheduler.subscribeOnActiveOrders(currency)
    }

    override fun onStop() {
      //  ActiveOrderScheduler.cancel()
        super.onStop()
    }

    override fun setupViewsVisibility() {
        /*RXCache.isLogined.observe(this, androidx.lifecycle.Observer {
            if (it == true) {*/
                presenter?.getTradingBalance()
                tv_avlb_exchange.visible()
            //}
        //})

        //val orderRightAccessed = ApiRightsStateScheduler.accessKeys[ApiRights.PlaceCancelOrders]?.value == State.ALLOWED
        btn_active_orders_exchange.visible()

      /*  if(SharedActions.hasActiveOrders(currency) *//*&& orderRightAccessed*//*)
        else

            btn_active_orders_exchange.gone()*/
    }

    private fun initAvblTextView() {
        tv_avlb_exchange.visibility = View.VISIBLE //if (Settings.logined()) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        subscribe()
        isBackFromPause = true

    }

    override fun onPause() {
        unsubscribe()
        super.onPause()
        isBackFromPause = false

    }

    override fun noNetwork() {
        unsubscribe()
        tv_avlb_exchange.gone()
        isConnected = false
        super.noNetwork()
    }

    override fun upNetwork() {
        super.upNetwork()
        isConnected = true
        tv_avlb_exchange?.visibility = View.VISIBLE //if (Settings.logined()) View.VISIBLE else View.GONE
    }

    private fun subscribe() {
        if (!isSubscribe) {
            presenter?.subscribeOnOrderbook()
            presenter?.subscribeOnTickers()
            presenter?.getSymbolInfo()
            presenter?.getMyOrders()
         //   if (NetworkStateReceiver.isConnected) isSubscribe = true
        }
    }

    private fun unsubscribe() {
        presenter?.unsubscribeOnOrderbook()
        presenter?.unsubscribeOnTickers()
        presenter?.pause()
        isSubscribe = false
    }

    private fun initBottomSheetOrder() {
/*
        orderTypeSelectorDialog = BottomLimitMarkerDialog(timeForGtdt, orderType, ::timeInForce,
                ::changeOrderTypeValue,
                ::setOrderTypeSelected,
                ::timeForGtdt::set).getDialog(context!!)
        orderTypeSelectorDialog.disableScroll()
*/

    }

    private fun changeOrderTypeValue() : OrderType {
        orderType = if (cb_stop_price_exchange.isChecked) {
            if (orderType == OrderType.STOP_MARKET) OrderType.STOP_LIMIT else OrderType.STOP_MARKET
        } else {
            if (orderType == OrderType.MARKET) OrderType.LIMIT else OrderType.MARKET
        }

        return orderType
    }

    private fun initEditTexts() {
        editTextList.add(et_stop_price_exchange)
        editTextList.add(et_price_exchange)
        editTextList.add(et_stop_price_exchange)
        editTextList.add(et_amount_exchange)

        editTextList.forEach {
            it.isEnabled = false
        }

        et_total_exchange.addTextChangedListener(totalTextWatcher)
        et_price_exchange.addTextChangedListener(priceTextWatcher)
        et_amount_exchange.addTextChangedListener(amountTextWatcher)

        editTextList.forEach {
            it.onFocusChangeListener = ZeroValueListener()
        }
        et_total_exchange?.onFocusChangeListener = ZeroValueListener()

        var wasOpen = false
        val measureRect = Rect()

        et_amount_exchange.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val amount = et_amount_exchange.text.toString()
                if (amount.toDoubleOrNull() == 0.0) {
                    et_amount_exchange.setText("0")
                }
            }
        }

        view!!.viewTreeObserver.addOnGlobalLayoutListener {
            view?.let {
                it.getWindowVisibleDisplayFrame(measureRect)

                val screenHeight = it.rootView.height
                val diffHeight = screenHeight - measureRect.height()
                val isOpen = diffHeight > screenHeight * 0.15

                if (isOpen == wasOpen) {
                    return@addOnGlobalLayoutListener
                }
                wasOpen = isOpen

                if (!isOpen) {
                    editTextList.forEach {
                        it.clearFocus()
                    }
                }
            }
        }
    }

    private fun initOrderbookRecycler() {
        askBidRvAdapter.onItemClick = { price ->
          //  AnalyticsManager.sendEvent(AnalyticsManager.Event.ExchangeClickedOrderbookForPrice)

            if (orderType == OrderType.LIMIT || orderType == OrderType.STOP_LIMIT) {
                et_price_exchange.setText(priceFormatter.format(price))
            }
        }

        rv_orderbook_exchange.apply {
            val llm = llm()
            ViewCompat.setNestedScrollingEnabled(this, true)
            layoutManager = llm
            adapter = askBidRvAdapter
            setHasFixedSize(true)

            askBidRvAdapter.viewWidth = resources.displayMetrics.widthPixels / 2

            addOnItemTouchListener(object : androidx.recyclerview.widget.RecyclerView.OnItemTouchListener {
                override fun onTouchEvent(p0: androidx.recyclerview.widget.RecyclerView, p1: MotionEvent) {}

                override fun onInterceptTouchEvent(rv: androidx.recyclerview.widget.RecyclerView, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_MOVE -> {
                            rv.parent.requestDisallowInterceptTouchEvent(true)
                        }
                    }
                    return false
                }

                override fun onRequestDisallowInterceptTouchEvent(p0: Boolean) {}
            })

            addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    if ((maxOrderbookCount < llm.findFirstVisibleItemPosition()
                                    || maxOrderbookCount > llm.findLastVisibleItemPosition())
                            && !isSentOrderbookScrollEvent) {
                     //   AnalyticsManager.sendEvent(AnalyticsManager.Event.ExchangeScrollOrderbook)
                        isSentOrderbookScrollEvent = true
                    }
                    askBidRvAdapter.isScrolling = newState != androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
                }
            })
        }
    }

    private fun askBidScrollToCenter() {
        rv_orderbook_exchange?.post {
            context?.let {
                val height = resources.displayMetrics.heightPixels - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f + 40f + 40f + 56f, resources.displayMetrics).toInt()
                val position = askBidRvAdapter.getCenterPosition()
                val viewHolderItemHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28f, resources.displayMetrics).toInt()
                val centerOfScreen = (height) / 2 - viewHolderItemHeight / 2
                ((rv_orderbook_exchange
                        ?: return@post).layoutManager as androidx.recyclerview.widget.LinearLayoutManager).scrollToPositionWithOffset(position, centerOfScreen)

                showOrderbook()
            }
        }
    }

    private fun settingClickListeners() {
        cb_stop_price_exchange.setOnCheckedChangeListener { buttonView, isChecked ->
           // AnalyticsManager.sendEvent(AnalyticsManager.Event.ExchangeStopPriceClicked, isChecked)
            if (isChecked) {
                et_stop_price_exchange.text = et_price_exchange.text
                //et_stop_price_exchange.visibility = View.VISIBLE
                orderType = if (orderType == OrderType.LIMIT) OrderType.STOP_LIMIT else OrderType.STOP_MARKET
            } else {
                et_stop_price_exchange.visibility = View.GONE
                orderType = if (orderType == OrderType.STOP_LIMIT) OrderType.LIMIT else OrderType.MARKET
            }
        }

        if (side == Side.BUY) {
            tab_buy_exchange.isSelected = true
            tab_sell_exchange.isSelected = false
        } else {
            tab_buy_exchange.isSelected = false
            tab_sell_exchange.isSelected = true
        }

        tab_buy_exchange.setOnClickListener {
            tab_buy_exchange.isSelected = true
            tab_sell_exchange.isSelected = false
            side = Side.BUY
            presenter?.tabBuySelected()
        }

        tab_sell_exchange.setOnClickListener {
            tab_buy_exchange.isSelected = false
            tab_sell_exchange.isSelected = true
            side = Side.SELL
            presenter?.tabSellSelected()
        }

        tv_orderbook_type.setOnClickListener {
            orderExchangeTypeSelectorDialog.show()
        }

        btn_select_order_type_exchange.setOnClickListener {
            orderTypeSelectorDialog.show()
            updateGTD()
        }
        invalidateBuySellButton()
        btn_buy_sell_exchange.setOnClickListener {
           /* if(currency != "BTCUSD" && !Settings.isPremiumEnabled()) {
                orderTypeSelectorDialog.dismiss()

                val fragment = SubscriptionFragment().apply {
                    arguments = Bundle().apply {
                        putString("from",SUBSCRIBE_BUY)
                    }
                }

                (activity as BaseActivity).openFragment(fragment)
            } else {
                checkCredentials {
                    *//*if (ApiRightsStateScheduler.accessKeys[ApiRights.PlaceCancelOrders]?.value == State.DENIED) {
                    SharedActions.showKeyAlert((activity as BaseActivity), R.string.locked_exchange_key)
                    return@checkCredentials
                }*//*
                    if (!checkDivisible(et_amount_exchange.text.toString().toDoubleOrNull()
                                    ?: 0.0) || !it.isEnabled) return@checkCredentials
                    setEnableBtnBuySell(false)
                    val stopPrice = if (cb_stop_price_exchange.isChecked) et_stop_price_exchange.text.toString() else null
                    val price = et_price_exchange.text.toString()
                    val quantity = et_amount_exchange.text.toString()
                    if (checkFields(price, quantity)) {
                        Log.d("btn_buy_sell", "disabled")
                        if (orderType == OrderType.LIMIT || orderType == OrderType.STOP_LIMIT) {
                            presenter?.newOrder(currency, side.side, quantity, orderType, price, stopPrice, timeInForce, if (timeInForce == GTD) Formatter.socketDate(timeForGtdt) else null, cb_post_only_exchange.isChecked)
                        } else {
                            presenter?.newOrder(currency, side.side, quantity, orderType, price, stopPrice, null, null, null)
                        }
                    } else {
                        setEnableBtnBuySell(true)
                    }

                    val eventForAnalytics = if (side == Side.BUY) {
                        if (orderType == OrderType.LIMIT || orderType == OrderType.STOP_LIMIT) {
                            AnalyticsManager.Event.ExchangeBuyLimitClicked
                        } else {
                            AnalyticsManager.Event.ExchangeBuyMarketClicked
                        }
                    } else {
                        if (orderType == OrderType.LIMIT || orderType == OrderType.STOP_LIMIT) {
                            AnalyticsManager.Event.ExchangeSellLimitClicked
                        } else {
                            AnalyticsManager.Event.ExchangeSellMarketClicked
                        }
                    }

                    AnalyticsManager.sendEvent(eventForAnalytics)
                }
            }*/
        }

        btn_active_orders_exchange.setOnClickListener {
            tabPositionToReportsFragment = 0
            openActiveOrders()
        }

        val outsideTouch = View.OnTouchListener { v: View, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN) askBidScrollToCenter()
            false
        }
        scrollView2.setOnTouchListener(outsideTouch)
        linear_scroll_layout.setOnTouchListener(outsideTouch)

        btn_minus_exchange.setOnClickListener {
            presenter?.changeAccuracy(-1)
        }

        btn_plus_exchange.setOnClickListener {
            presenter?.changeAccuracy(+1)
        }
        btn_plus_exchange.isAvailable(false)

        percent_25?.setOnClickListener { onPercentClick(it as Button) }
        percent_50?.setOnClickListener { onPercentClick(it as Button) }
        percent_75?.setOnClickListener { onPercentClick(it as Button) }
        percent_100?.setOnClickListener { onPercentClick(it as Button) }
    }

    fun openActiveOrders(){
    /*    AnalyticsManager.sendEvent(AnalyticsManager.Event.ExchangeActiveOrdersClicked)
        val fragment = ReportsFragment().apply {
            arguments = Bundle().apply {
                putBoolean("fromTrades", true)
                putString("currencyForFilter", Formatter.currency(currency))
                when (orderType) {
                    OrderType.LIMIT, OrderType.STOP_LIMIT -> {

                        putInt("tabPosition",0)
                    }

                    OrderType.MARKET, OrderType.STOP_MARKET -> {
                        putInt("tabPosition",2)

                    }
                }
            }
        }

        (activity as BaseActivity).apply {
            hideKeyboard()
            changeFlow(BaseActivity.FLOW_REPORTS, fragment)
        }*/
    }

    override fun setEnableBtnBuySell(isEnable: Boolean) {
        inUI {
            btn_buy_sell_exchange.isEnabled = isEnable
        }
    }

    override fun openLoginFragment() {
    //    (activity as BaseActivity).openSpecialFragment(LoginFragment())
    }

    override fun updateOrderbook(askList: List<AskBid>, bidList: List<AskBid>, isFirstEmit: Boolean) {
        val isFirstData = askBidRvAdapter.setItems(askList, bidList)
        if (isFirstData || isFirstEmit||isBackFromPause) {
            askBidScrollToCenter()
            isBackFromPause = false
        }
    }

    override fun onDestroy() {
        presenter?.detachView()
        super.onDestroy()
    }

    override fun toastUploadError() {
        Looper.prepare()
        toast.setText(getString(R.string.cannot_upload))
        toast.show()
    }

    override fun showAccessDenied() {
        inUI {
         //   SharedActions.showKeyAlert((activity as BaseActivity), R.string.locked_exchange_key, false)
        }
    }

    override fun hideOrderbook() {
        rv_orderbook_exchange?.invisible()
        pb_orderbook_exchange?.visible()
        startTrackingTimeForAnalytics()
    }

    override fun showOrderbook() {
        rv_orderbook_exchange.visibility = View.VISIBLE
        pb_orderbook_exchange.visibility = View.INVISIBLE
      //  checkTimeAndSendEventToAnalytics(AnalyticsManager.Event.TimeLoadOrderBook)
    }

    override fun setBaseFeeCurrencies(baseCurrency: String, feeCurrency: String) {
        this.baseCurrency = baseCurrency
        this.feeCurrency = feeCurrency

        setupTextViewsCurrency()
        initBottomSheetOrderExhangeType()
    }

    override fun setPrice(price: BigDecimal) {
        et_price_exchange.setText(priceFormatter.format(price))
    }

    override fun setEdittextsEnabled(state: Boolean) {
        editTextList.forEach {
            it.isEnabled = state
        }
    }

    override fun setPriceUnlocked(state: Boolean) {
        et_price_exchange.isEnabled = state
    }

    override fun setEdittextsTicks(amountTick: BigDecimal, priceTick: BigDecimal, totalTick: BigDecimal) {
        val decimalFormatSymbols = DecimalFormatSymbols(Locale.ENGLISH)

       // var numberAfterDot = Formatter.getNumberAfterDot(amountTick)
        var str = StringBuilder()
       // for (i in 0 until numberAfterDot)
     //       str.append("#")
        amountFormatter.applyPattern("#.$str")
        amountFormatter.decimalFormatSymbols = decimalFormatSymbols
       // setAmountAfterDot(et_amount_exchange?: return, numberAfterDot)

       // numberAfterDot = Formatter.getNumberAfterDot(priceTick)
        str = StringBuilder()
       // for (i in 0 until numberAfterDot)
      //      str.append("0")
        priceFormatter.applyPattern("#0.${str}")
        priceFormatter.decimalFormatSymbols = decimalFormatSymbols
     //   setAmountAfterDot(et_price_exchange, Formatter.getNumberAfterDot(priceTick))
     //   setAmountAfterDot(et_stop_price_exchange, Formatter.getNumberAfterDot(priceTick))

      //  numberAfterDot = Formatter.getNumberAfterDot(totalTick)
        str = StringBuilder()
     //   for (i in 0 until numberAfterDot)
   //         str.append("#")
        totalFormatter.applyPattern("#.${str}")
        totalFormatter.decimalFormatSymbols = decimalFormatSymbols
      //  setAmountAfterDot(et_total_exchange?: return, numberAfterDot)
        //et_amount_exchange?.setText(Formatter.amount(amountTick.toDouble()))
        presenter?.restoreAmount(et_amount_exchange ?: return){
            amountTick.toDouble()
        }
    }

    private fun setAmountAfterDot(editText: EditText, numberAfterDot: Int) {
        editText?.filters = arrayOf(DecimalDigitsInputFilter(numberAfterDot))
    }

    override fun onNewOrderSuccess(result: NewOrderResult) {
        val symbol = RXCache.getSymbol(result.symbol)
        val color = if (result.side == "buy") "#99cd73" else "#e55441"
      //  val m = "<font color=#cccccc>${getString(R.string.your)}</font> <font color=${color}> ${side.side} ${result.type}</font> <font color=#cccccc>${getString(R.string.was_placed)}</font> \n<font color=${color}>${symbol?.feeCurrency}${Formatter.amount(result.price)}</font>"

      //  SharedActions.showSnackbarInOrders(view!!, Html.fromHtml(m), activity!!){
            openActiveOrders()
       // }

      //  presenter?.getMyOrders()
    }

    override fun onNewOrderSuccess(result: NewOrderResponse) {
        val symbol = RXCache.getSymbol(result.symbol)
        val color = if (result.side == "buy") "#99cd73" else "#e55441"
     //   val m = "<font color=#cccccc>${getString(R.string.your)}</font> <font color=${color}> ${side.side} ${orderType.type}</font> <font color=#cccccc>${getString(R.string.was_placed)}</font> \n<font color=${color}>${symbol?.feeCurrency}${result.price}</font>"

      /* // SharedActions.showSnackbarInOrders(view!!, Html.fromHtml(m), activity!!){
            openActiveOrders()
        }*/

        presenter?.getMyOrders()
        setEnableBtnBuySell(true)
    }

    override fun onNewOrderError(error: String) {
       // wrapError(error)
    }

    override fun onNewOrderError(error: Int) {
        val type = getOrderType(orderType)
        val color = if(side == Side.BUY) "#99cd73" else "#e55441"
        val m = "<font color=#cccccc>${getString(R.string.your)}</font> <font color=${color}> ${side.side} ${type}</font> <font color=#cccccc>${getString(R.string.was_rejected)}${getString(error)}</font> "

       // SharedActions.showSnackbarInOrders(view ?: return, Html.fromHtml(m), activity!!)
    }

    override fun updateMarketPrices(buyPrice: BigDecimal, sellPrice: BigDecimal) {
        if ((orderType == OrderType.STOP_MARKET || orderType == OrderType.MARKET) && isPriceUpdateNeeded(buyPrice, sellPrice)) {
            Handler(Looper.getMainLooper()).post {
                et_price_exchange.setText(priceFormatter.format(if (side == Side.BUY) buyPrice else sellPrice))
            }
        }
    }

    fun isPriceUpdateNeeded(buyPrice: BigDecimal, sellPrice: BigDecimal): Boolean {
        return try {
            when (side) {
                Side.BUY -> priceFormatter.format(buyPrice) != et_price_exchange.text.toString()
                Side.SELL -> priceFormatter.format(sellPrice) != et_price_exchange.text.toString()
            }
                    //&& ((orderType == OrderType.STOP_MARKET || orderType == OrderType.MARKET))
                    && !et_total_exchange.hasFocus() && !et_price_exchange.hasFocus()
        } catch (e: Exception) {
            error(e)
            false
        }

    }

    override fun marketOrderType() {
        setupTextViewsCurrency()
        tv_rebate_exchange.gone()
        cb_post_only_exchange.gone()
    }

    override fun limitOrderType() {
        setupTextViewsCurrency()
        //tv_rebate_exchange.visible()
        //cb_post_only_exchange.visible()
    }

    private fun setupTextViewsCurrency() {
     /*   //val baseSpannable = SpannableString(Formatter.currency(baseCurrency,true)).apply {
            setSpan(ForegroundColorSpan(resources.getColor(R.color.gray_50)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }*/
        val feeSpannable = feeSpannable()

        tv_amount_exchange?.apply {
            text = getString(R.string.amount)
            append(", ")
         //   append(baseSpannable)
        }
        tv_price_exchange?.apply {
            text = getString(R.string.price)
            append(", ")
            append(feeSpannable)
        }
        cb_stop_price_exchange?.apply {
            text = getString(R.string.stop_price)
            append(", ")
            append(feeSpannable)
        }

        tv_total_exchange?.apply {
            text = getString(R.string.total)
            append(", ")
            append(feeSpannable)
        }
    }

    override fun setAvialable(balance: String, currency: String) {
        tv_avlb_exchange.visibility = if (balance.isNotEmpty()) View.VISIBLE else{
            View.GONE
            return
        }

       // val trimmedBalance = Formatter.amount(Formatter.trimTrailingZeros(balance))
        avblBalance = balance.toDouble()

        /*   val spannable = SpannableString("$trimmedBalance ${Formatter.currency(currency)}").apply {
             setSpan(ForegroundColorSpan(resources.getColor(R.color.rd_blue)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
          }*/
        tv_avlb_exchange.apply {
            text = getString(R.string.available_short)
            append(" ")
           // append(spannable)

            setOnClickListener {
            //    AnalyticsManager.sendEvent(AnalyticsManager.Event.ExchangeClickedAvlbForAmount)

              //  presenter?.onAvailableClick(trimmedBalance, et_price_exchange.text.toString())
            }
        }
    }

    override fun setAvailableVisible(visible: Boolean) {
        tv_avlb_exchange.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun setOrderTypeSelected() {
        when (orderType) {
            OrderType.LIMIT, OrderType.STOP_LIMIT -> {
                tv_order_type_exchange.text = getString(R.string.order_limit)
                orderTypeSelectorDialog.hide()
                tabPositionToReportsFragment = 0
                cb_post_only_exchange.isChecked = false
                //cb_post_only_exchange.visible()
                presenter?.limitOrderTypeSelected()

                et_price_exchange.apply {
                    setTextColor(resources.getColor(R.color.gray_light))
                    background.alpha = 255
                }

            }

            OrderType.MARKET, OrderType.STOP_MARKET -> {
                tv_order_type_exchange.text = getString(R.string.order_market)
                tabPositionToReportsFragment = 2
                orderTypeSelectorDialog.hide()
                presenter?.marketOrderTypeSelected()
                et_price_exchange.apply {
                    setTextColor(resources.getColor(R.color.rd_text_a_50))
                    background.alpha = 102
                }
                tv_price_exchange.apply {
                    text = getString(R.string.estimated_best_price)
                    append(", ")
                    append(feeSpannable())
                }
            }
        }
    }

    override fun setMyOrders(orders: List<ActiveOrdersResponse>) {
        askBidRvAdapter.setMyOrders(orders)
    }

    override fun invalidateBuySellButton() {
        val text: String
        if (side == Side.BUY) {
            btn_buy_sell_exchange.setBackgroundResource(R.drawable.btn_buy_exchange_background)
            text = getString(R.string.buy)
        } else {
            btn_buy_sell_exchange.setBackgroundResource(R.drawable.btn_sell_exchange_background)
            text = getString(R.string.sell)
        }
      //  val locale = Settings.getLocale()
        val append = when (orderType) {
            OrderType.LIMIT, OrderType.STOP_LIMIT -> getString(R.string.limit)
            OrderType.MARKET, OrderType.STOP_MARKET -> getString(R.string.market)
        }
        btn_buy_sell_exchange.text = "  "
    }

    override fun setRebate(rebatePercent: BigDecimal, rebate: BigDecimal) {
        tv_rebate_exchange.text = "${getString(R.string.maker_fee)} ${rebatePercent.stripTrailingZeros()}% ${feeFormatter.format(rebate)} ${feeCurrency}"
    }

    override fun setFee(feePercent: BigDecimal, fee: BigDecimal) {
        tv_fee_exchange.text = "${getString(R.string.taker_fee)} ${feePercent.stripTrailingZeros()}% ${feeFormatter.format(fee)} ${feeCurrency}"
    }

    override fun setTotal(value: BigDecimal) {
        et_total_exchange.removeTextChangedListener(totalTextWatcher)
        et_total_exchange.setText(totalFormatter.format(value))
        et_total_exchange.addTextChangedListener(totalTextWatcher)
        et_total_exchange.moveSelection()
    }

    override fun setAmount(value: BigDecimal) {
        et_amount_exchange.removeTextChangedListener(amountTextWatcher)
       // et_amount_exchange.setText(Formatter.amount(amountFormatter.format(value)))
        et_amount_exchange.addTextChangedListener(amountTextWatcher)
        et_amount_exchange.moveSelection()
    }

    fun setConvertedAmount(value: BigDecimal) {
        val conveted = RXCache.convertCurrencyAmount(value.toDouble(), feeCurrency, baseCurrency)

        et_amount_exchange.removeTextChangedListener(amountTextWatcher)
      //  et_amount_exchange.setText(Formatter.amount(amountFormatter.format(conveted)))
        et_amount_exchange.addTextChangedListener(amountTextWatcher)
        et_amount_exchange.moveSelection()
    }

    override fun setLastTradePrice(price: String, isIncrease: Boolean) {
        val color = if (isIncrease) R.color.rd_green else R.color.rd_red
        askBidRvAdapter.setLastTradePrice(price, color)
    }

    override fun getTotal() = et_total_exchange.text.toString().parseDouble()
    override fun getPrice() = et_price_exchange.text.toString().parseDouble()
    override fun getAmount() = et_amount_exchange.text.toString().parseDouble()

    override fun setAccuracyToAdapter(amountAccuracy: Int, priceAccuracy: Int) {
        askBidRvAdapter.numbersAfterDotAmount = amountAccuracy
        askBidRvAdapter.numbersAfterDotPriceForBest = priceAccuracy
        askBidRvAdapter.numbersAfterDotPrice = priceAccuracy
    }

    override fun setAccuracyToAdapter(accuracy: Int) {
        askBidRvAdapter.accuracy = accuracy
    }

   /* override fun setAccuracyAvailable(type: OrderbookCalculation.AccuracyType, isAvailable: Boolean) {
        when(type){
            PLUS -> btn_plus_exchange?.isAvailable(isAvailable)
            MINUS -> btn_minus_exchange?.isAvailable(isAvailable)
        }
    }*/

    private val amountTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (!et_total_exchange.hasFocus())
                presenter?.calculateTotal()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private val priceTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (!et_total_exchange.hasFocus())
                presenter?.calculateTotal()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private val totalTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (!et_amount_exchange.hasFocus()) {
                presenter?.calculateAmount()
                presenter?.calculateFee()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    fun onPercentClick(button: Button){
        //val buttons = mutableListOf(R.id.percent_25, R.id.percent_50, R.id.percent_75, R.id.percent_100)

        val percent = button.text.toString().replace("%", "").toInt()
      //  presenter?.onAvailableClick(Formatter.trimTrailingZeros(((percent/100.0)*avblBalance).toString()), et_price_exchange.text.toString())
        /*buttons.remove(button.id)
        button.background = getDrawable(context!!, R.drawable.rd_percent_selected)
        buttons.forEach {
            inUI {
                view?.findViewById<Button>(it)?.background = getDrawable(context!!, R.drawable.ic_rd_percent)
            }
        }*/
    }

    class DecimalDigitsInputFilter(digitsAfterZero: Int) : InputFilter {

        private val pattern = if (digitsAfterZero <= 0) {
            Pattern.compile("[0-9]*")
        } else {
            Pattern.compile("[0-9]+((\\.[0-9]{0,${digitsAfterZero}})?)")
        }

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
            val index = dest.indexOf('.')
            if (index != -1 && dstart > index) {
                val matcher = pattern.matcher("$dest$source")
                return if (!matcher.matches()) {
                    ""
                } else {
                    null
                }
            } else {
                return null
            }
        }
    }

    enum class Side(var side: String) {
        BUY("buy"),
        SELL("sell")
    }

    enum class OrderType(var type: String) {
        LIMIT("limit"),
        MARKET("market"),
        STOP_LIMIT("stopLimit"),
        STOP_MARKET("stopMarket")
    }

    fun getOrderType(type: OrderType) = when(type){
        OrderType.LIMIT, OrderType.MARKET -> type.type
        OrderType.STOP_LIMIT -> "stop limit"
        OrderType.STOP_MARKET -> "stop market"
    }

    enum class TimeInForce(var type: String) {
        GTC("GTC"),
        IOC("IOC"),
        FOK("FOK"),
        Day("Day"),
        GTD("GTD")
    }

    private class ZeroValueListener : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            try {
                var target = v as EditText
                if (target.text.toString() == "0.0") {
                    target.setText("0")
                }
            } catch (e: Exception) {
                Log.e("err", e.message)
            }
        }
    }

    private var wrongDivisible: (Double) -> String = {
 "  "
    }

    private fun checkDivisible(amount: Double): Boolean{
        if(amount <= 0.0){
         //   SharedActions.showSnackbar(view!!, getString(R.string.amount_not_a_valid))
            return false
        }
        val qTick = RXCache.getSymbol(currency)?.quantityIncrement ?: 1.0
        val res =  try {
            BigDecimal.valueOf(amount)%BigDecimal.valueOf(qTick)
        }
        catch (e: ArithmeticException){
            BigDecimal.valueOf(0.0)
        }
        return if (res.toDouble()!=0.0){

            showError(wrongDivisible(qTick))
            false
        }
        else true
    }

    private fun checkFields(price: String?, amount: String?): Boolean {
        when {
            amount.isNullOrBlank() -> {
                showError( getString(R.string.empty_field_trading, getString(R.string.amount)))
                return false
            }
            price.isNullOrBlank() -> {
                showError(getString(R.string.empty_field_trading, getString(R.string.price)))
                return false
            }
            else -> return true
        }
    }

    fun showError(message: String){
        val type = getOrderType(orderType)
        val color = if(side == Side.BUY) "#99cd73" else "#c74452"
        val m = "<font color=#BECFDD>${getString(R.string.your)}</font> <font color=${color}> ${side.side} ${type}</font> <font color=#BECFDD>${getString(R.string.was_rejected)}${message}</font> "

      //  SharedActions.showSnackbarInOrders(view ?: return, Html.fromHtml(m), activity!!)
    }

    override fun setAccuracyToLeftPartOrderbook(accuracy: Int) {
        askBidRvAdapter.numbersAfterDotAmount = accuracy
    }

    @SuppressLint("InflateParams")
    private fun initBottomSheetOrderExhangeType() {
        orderExchangeTypeSelectorDialog = BottomSheetDialog(context!!).apply {
            val dialog = this

            setContentView(layoutInflater.inflate(R.layout.dialog_orderbook_type, null).apply {
                orderbook_type_selector_btn_first_currency.text = getString(R.string.orderbook_selector_sum, baseCurrency)
                orderbook_type_selector_btn_second_currency.text = getString(R.string.orderbook_selector_sum, feeCurrency)

                val itemClickListener = View.OnClickListener {
                    if (it.isSelected) return@OnClickListener
                    it.isSelected = !it.isSelected

                    val selectorType = when(it.id) {
                        orderbook_type_selector_btn_amount.id -> {
                            orderbook_type_selector_btn_first_currency.isSelected = false
                            orderbook_type_selector_btn_second_currency.isSelected = false
                            OrderbookExchangeSelectorEnum.AMOUNT
                        }
                        orderbook_type_selector_btn_first_currency.id -> {
                            orderbook_type_selector_btn_amount.isSelected = false
                            orderbook_type_selector_btn_second_currency.isSelected = false
                            OrderbookExchangeSelectorEnum.SUM_BASE_CURRENCY
                        }
                        orderbook_type_selector_btn_second_currency.id -> {
                            orderbook_type_selector_btn_amount.isSelected = false
                            orderbook_type_selector_btn_first_currency.isSelected = false
                            OrderbookExchangeSelectorEnum.SUM_FEE_CURRENCY
                        }
                        else -> OrderbookExchangeSelectorEnum.AMOUNT
                    }

                    presenter?.changeOrderbookExchangeSelector(selectorType)
                    dialog.dismiss()
                }

                orderbook_type_selector_btn_amount.setOnClickListener(itemClickListener)
                orderbook_type_selector_btn_first_currency.setOnClickListener(itemClickListener)
                orderbook_type_selector_btn_second_currency.setOnClickListener(itemClickListener)
/*
                when(AppCache.orderbookExchangeSelectorType) {
                    OrderbookExchangeSelectorEnum.AMOUNT -> {
                        orderbook_type_selector_btn_amount.isSelected = true
                    }
                    OrderbookExchangeSelectorEnum.SUM_BASE_CURRENCY -> {
                        orderbook_type_selector_btn_first_currency.isSelected = true
                    }
                    OrderbookExchangeSelectorEnum.SUM_FEE_CURRENCY -> {
                        orderbook_type_selector_btn_second_currency.isSelected = true
                    }
                }*/
            })
        }
        orderExchangeTypeSelectorDialog.disableScroll()
    }


    override fun setOrderbookSelectorText(type: OrderbookExchangeSelectorEnum) {
        when(type) {
            OrderbookExchangeSelectorEnum.AMOUNT -> {
                tv_orderbook_type?.setText(R.string.amount)
                tv_orderbook_type.setTextSize(14f)
            }
            OrderbookExchangeSelectorEnum.SUM_BASE_CURRENCY -> {
                tv_orderbook_type?.text = getString(R.string.orderbook_selector_sum, baseCurrency)
                 tv_orderbook_type.setTextSize(12f)
            }
            OrderbookExchangeSelectorEnum.SUM_FEE_CURRENCY -> {
                tv_orderbook_type?.text = getString(R.string.orderbook_selector_sum, feeCurrency)
               tv_orderbook_type.setTextSize(12f)
            }
        }
    }
    fun updateGTD(){
       /* GlobalScope.launch {
            try {
                inUI {
                    val time = Formatter.dateOrderFragment(CalendarUtils.getUpperRoundedTime())
                }
                delay(1000*60)
                if (orderTypeSelectorDialog.isShowing) updateGTD()
                else return@launch
            }catch (e: java.lang.Exception){
                ErrorWrapper.onError(e)}
        }*/
    }

    fun checkCredentials(onLogined: () -> Unit) {
       /* val pKey = Settings.publicKey()
        val sKey = Settings.privateKey()

        if (pKey == "0" || sKey == "0" || !Settings.isLogin()) {
            openLoginFragment()
        } else {*/
            onLogined()
        //}
    }
    fun feeSpannable(): SpannableString{
        return SpannableString(feeCurrency).apply {
            setSpan(ForegroundColorSpan(resources.getColor(R.color.gray_50)), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

    }

    override fun getOrderBookHeight() : Float {
        return rv_orderbook_exchange?.height?.toFloat() ?: 0f
    }

    override fun getOrderBookItemHeight() : Float {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                28f,
                context?.resources?.displayMetrics
        )
    }

    fun timesAndSalesScroll(scrolling: Boolean) {
        askBidRvAdapter.isScrolling = scrolling
    }
}