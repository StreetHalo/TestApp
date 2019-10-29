package trade.paper.app.models


class PaymenIDs{
    val values: HashMap<String, PaymentID> by lazy {
        hashMapOf(
                "ACT" to PaymentID(
                        "ACT",
                        "Achain",
                        "Memo"
                ),
                "AEON" to PaymentID(
                        "AEON",
                        "Aeon",
                        "Payment ID"
                ),
                "BCN" to PaymentID(
                        "BCN",
                        "Bytecoin",
                        "Payment ID"
                ),
                "DSH" to PaymentID(
                        "DSH",
                        "DashCoin",
                        "Payment ID"
                ),
                "DCT" to PaymentID(
                        "DCT",
                        "Decent",
                        "Memo"
                ),
                "XDN" to PaymentID(
                        "XDN",
                        "DigitalNote",
                        "Payment ID"
                ),
                "XDNICCO" to PaymentID(
                        "XDNICCO",
                        "DigitalNote ICCO",
                        "Payment ID"
                ),
                "DIM" to PaymentID(
                        "DIM",
                        "DIMCOIN",
                        "Payment ID"
                ),
                "EOS" to PaymentID(
                        "EOS",
                        "EOS",
                        "Memo"
                ),
                "FCN" to PaymentID(
                        "FCN",
                        "Fantomcoin",
                        "Payment ID"
                ),
                "IGNIS" to PaymentID(
                        "IGNIS",
                        "Ignis",
                        "Message"
                ),
                "WMGO" to PaymentID(
                        "WMGO",
                        "MobileGo on WAVES",
                        "Payment ID"
                ),
                "XMR" to PaymentID(
                        "XMR",
                        "Monero",
                        "Payment ID"
                ),
                "XMO" to PaymentID(
                        "XMO",
                        "Monero Original",
                        "Payment ID"
                ),
                "XEM" to PaymentID(
                        "XEM",
                        "NEM",
                        "Message"
                ),
                /*"NXT" to PaymentID(
                        "NXT",
                        "Nxt",
                        "Public key"
                ),*/
                "OTX" to PaymentID(
                        "OTX",
                        "OTX",
                        "Attachment"
                ),
                "PING" to PaymentID(
                        "PING",
                        "PING",
                        "Attachment"
                ),
                "QCN" to PaymentID(
                        "QCN",
                        "QuazarCoin",
                        "Payment ID"
                ),
                "XRP" to PaymentID(
                        "XRP",
                        "Ripple",
                        "Destination Tag"
                ),
                "STEEM" to PaymentID(
                        "STEEM",
                        "Steem",
                        "Memo"
                ),
                "SBD" to PaymentID(
                        "SBD",
                        "Steem Dollars",
                        "Memo"
                ),
                "XLM" to PaymentID(
                        "XLM",
                        "Stellar",
                        "Message"
                ),
                "ZRC" to PaymentID(
                        "ZRC",
                        "Zrcoin",
                        "Attachment"
                ),
                "CVCOIN" to PaymentID(
                        "CVCOIN",
                        "CVCoin",
                        "Message"
                ),
                "BTS" to PaymentID(
                        "BTS",
                        "Bitshares",
                        "Memo"
                ),
                "XMV" to PaymentID(
                        "XMV",
                        "MoneroV",
                        "Payment ID"
                ),
                "ETN" to PaymentID(
                        "ETN",
                        "Electroneum",
                        "Payment ID"
                ),
                "KIN" to PaymentID(
                        "KIN",
                        "KIN",
                        "Payment ID"
                )
                /*"XTZ" to PaymentID(
                        "XTZ",
                        "Tezos",
                        "Public key"
                )*/

        )
    }
}

data class PaymentID(
        var code: String,
        var name: String,
        var paymentIdName: String
        )


