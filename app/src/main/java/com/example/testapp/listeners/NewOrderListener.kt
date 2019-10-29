package trade.paper.app.listeners

import trade.paper.app.models.rpc.Error
import trade.paper.app.models.rpc.response.NewOrderResult

interface NewOrderListener {
    fun onSucces(response: NewOrderResult)
    fun onFailure(error: Error?)
}