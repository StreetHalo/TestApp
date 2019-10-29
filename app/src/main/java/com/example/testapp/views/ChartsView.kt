package trade.paper.app.views

import trade.paper.app.models.rpc.params.CandlesData

@Deprecated(
        level = DeprecationLevel.WARNING,
        message = "@see detail.ChartView"
)
interface ChartsView : BaseView {
    fun updateCandles(candleData: List<CandlesData>)
    fun showError(error: String)
}