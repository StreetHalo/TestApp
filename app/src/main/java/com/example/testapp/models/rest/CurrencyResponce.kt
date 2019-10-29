package trade.paper.app.models.rest

data class CurrencyResponce(
        val id: String,
        val name: String,
        val fullName: String,
        val crypto: Boolean,
        val payinEnabled: Boolean,
        val payinPaymentId: Boolean,
        val payinConfirmations: Integer,
        val payoutEnabled: Boolean,
        val payoutIsPaymentId: Boolean,
        val transferEnabled: Boolean,
        val delisted: Boolean,
        val payoutFee: Double
)