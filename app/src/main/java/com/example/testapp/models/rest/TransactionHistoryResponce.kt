package trade.paper.app.models.rest

import java.util.*

data class TransactionHistoryResponce(
    val id : String,
    val index : Long,
    val currency : String,
    val amount : Double,
    val fee : Double,
    val address : String,
    val paymentId : String?,
    val hash : String?,
    val status : String,
    val type : String,
    val createdAt : Date,
    val updatedAt : Date
)