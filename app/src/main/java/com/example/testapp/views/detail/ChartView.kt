package trade.paper.app.views.detail

import trade.paper.app.models.rpc.params.ChartData
import trade.paper.app.views.BaseView

interface ChartView: BaseView {
    fun updateChart(data: List<ChartData>)
    fun showError(msg: String)
}