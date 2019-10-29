package trade.paper.app.models.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SymbolDTO(

    var symbol : String,
    var base : String,
    var fee : String,
    var volume : Double = 0.0,
    var price: Double = 0.0,
    var change24: Double = 0.0,
    var stock:Boolean = false

) : Parcelable {
    override fun equals(other: Any?): Boolean {
        return symbol == (other as SymbolDTO).symbol && (price == other.price)
    }
}