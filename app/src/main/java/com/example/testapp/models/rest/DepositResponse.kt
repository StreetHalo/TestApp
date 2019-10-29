package trade.paper.app.models.rest

data class DepositResponse(
        var address: String,
        var paymentId: String?
)