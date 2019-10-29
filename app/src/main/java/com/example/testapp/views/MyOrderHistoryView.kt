package trade.paper.app.views

import trade.paper.app.models.rest.OrderHistoryResponse

interface MyOrderHistoryView : BaseView {
    fun showOrder(orders: List<OrderHistoryResponse>)
    fun appendOrders(orders: List<OrderHistoryResponse>)
    fun showError(error: String)
}