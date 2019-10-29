package trade.paper.app.views

import trade.paper.app.models.rest.BalanceResponse

interface AccountView : BaseView {
    fun showBalance(balance: List<BalanceResponse>)

    fun showDepositAddres(address: String)

    fun setBalanceData(data: MutableList<String>)

    fun withdrawFinished(id: String)

    fun showBalance(balance: BalanceResponse)

    fun onBalanceError(msg: String)
}