package trade.paper.app.fragments.exchange.ExchangeTimeAndSales

import trade.paper.app.models.rest.TradesResponse

interface ExchangeTimeAndSalesContract {
    interface View {
        fun toastUploadError()
        fun setTimesAndSalesSnapShot(data: List<TradesResponse>)
        fun updateTimesAndSales(data: List<TradesResponse>)
        fun settingHeadline(baseCurrency: String, feeCurrency: String)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun subscribeOnTrades()
        fun setCurrency(currency: String)
        fun unsubscribeOnTrades()
        fun getSymbolInfo()
    }
}