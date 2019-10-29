package trade.paper.app.fragments.exchange.ExchangeTimeAndSales

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.testapp.BaseFragment
import com.example.testapp.Formatter
import com.example.testapp.R
import com.example.testapp.ui.dashboard.ExchangeActivity
import kotlinx.android.synthetic.main.fragment_exchange.*
import trade.paper.app.adapters.TimesAndSalesRecyclerAdapter
import trade.paper.app.models.rest.TradesResponse
import kotlinx.android.synthetic.main.fragment_exchange_time_and_sales.*
import trade.paper.app.utils.extensions.invisible
import trade.paper.app.utils.extensions.visible

class ExchangeTimeAndSalesFragment : BaseFragment(), ExchangeTimeAndSalesContract.View {

    private var currency = "BTCUSD"

    private var presenter: ExchangeTimeAndSalesContract.Presenter? = null

    private var rvAdapter = TimesAndSalesRecyclerAdapter()

    private val errorToast by lazy { Toast.makeText(context, "", Toast.LENGTH_LONG) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = ExchangeTimeAndSalesPresenter()

        arguments?.let {
            currency = it.getString("currency", "BTCUSD")
            presenter?.setCurrency(currency)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_exchange_time_and_sales, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.attachView(this)

        presenter?.getSymbolInfo()
        initRecycler()
        setListener()
        startTrackingTimeForAnalytics() // Tracking time at the startup screen, bec loading view is show by default
    }

    override fun upNetwork() {
        super.upNetwork()
        presenter?.subscribeOnTrades()
    }

    override fun noNetwork() {
        super.noNetwork()
        presenter?.unsubscribeOnTrades()
    }

    private fun setListener() {
        layout_times_and_sales.setOnClickListener {
           // AnalyticsManager.sendEvent(AnalyticsManager.Event.ExchangeTimeAndSalesClicked)
          (activity as ExchangeActivity).openTimeAndSales()
        }
    }


    fun openTimeAndSales() {
        vp_exchange.setCurrentItem(if (vp_exchange.currentItem == 0) 1 else 0, true)
    }
    override fun settingHeadline(baseCurrency: String, feeCurrency: String) {
        tv_time_times_and_sales_exchange.text = "${getString(R.string.time)}"
        tv_price_times_and_sales_exchange.text = "${getString(R.string.price)}, ${Formatter.currency(feeCurrency)}"
        tv_amount_times_and_sales_exchange.text = "${getString(R.string.amount)}, ${Formatter.currency(baseCurrency)}"
    }

    private fun initRecycler() {
        rv_times_and_sales_exchange.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = rvAdapter

            addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) {
                //    (parentFragment as ExchangeFragment).timesAndSalesScroll(newState != androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()

        presenter?.subscribeOnTrades()
    }

    override fun onPause() {
        super.onPause()

        presenter?.unsubscribeOnTrades()
    }

    override fun onDestroyView() {
        iv_arrow_times_and_sales.animate().cancel()
        super.onDestroyView()
    }

    override fun onDestroy() {
        presenter?.detachView()
        super.onDestroy()
    }

    override fun toastUploadError() {
        errorToast.setText(getString(R.string.cannot_upload))
        errorToast.show()
    }

    override fun setTimesAndSalesSnapShot(data: List<TradesResponse>) {
        rv_times_and_sales_exchange.visibility = View.VISIBLE

        pb_times_and_sales_exchange.visibility = View.GONE
        rvAdapter.setItems(data)

      //  checkTimeAndSendEventToAnalytics(AnalyticsManager.Event.TimeLoadTimeAmpSales)
    }

    override fun updateTimesAndSales(data: List<TradesResponse>) {
        rv_times_and_sales_exchange.visibility = View.VISIBLE
        pb_times_and_sales_exchange.visibility = View.GONE

        val shouldScroll = (rv_times_and_sales_exchange.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findFirstVisibleItemPosition() == 0

        rvAdapter.addItems(data)
        if (shouldScroll) tradesAndSalesScrollToTop()

      //  checkTimeAndSendEventToAnalytics(AnalyticsManager.Event.TimeLoadTimeAmpSales)
    }

    private fun tradesAndSalesScrollToTop() {
        (rv_times_and_sales_exchange.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).scrollToPosition(0)
    }

    fun rotateArrowDown() {
        iv_arrow_times_and_sales.animate().rotation(180f).duration = 300
    }

    fun rotateArrowUp() {
        iv_arrow_times_and_sales.animate().rotation(0f).duration = 300
    }
}