package trade.paper.app.models

data class CryptoPaymentIdName(val fullName: String,
                               val paymentIdName: String) {
    companion object {
        const val MEMO = "Memo"
        const val PAYMENT_ID = "Payment ID"
        const val MESSAGE = "Message"
        const val PUBLIC_KEY = "Public key"
        const val DESTINATION_TAG = "DestinationTag"
        const val ATTACHMENT = "Attachment"
    }
}