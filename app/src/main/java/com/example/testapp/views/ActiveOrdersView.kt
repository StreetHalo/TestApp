package trade.paper.app.views

import trade.paper.app.models.rest.ActiveOrdersResponse

interface ActiveOrdersView : BaseView {
    fun showActiveOrders(orders: MutableList<ActiveOrdersResponse>)
    fun showError(error: String)
}