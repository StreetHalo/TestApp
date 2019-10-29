package trade.paper.app.models.rest

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SymbolResponse(
        var id: String,
        var family: String,
        var baseCurrency: String,
        var quoteCurrency: String,
        var quantityIncrement: Double,
        var tickSize: Double,
        var takeLiquidityRate: Double,
        var provideLiquidityRate: Double,
        var feeCurrency: String
) : Parcelable