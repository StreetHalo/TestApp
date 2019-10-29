package com.example.testapp

import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.hawk.Settings
import trade.paper.app.models.rest.SymbolResponse
import java.lang.StringBuilder
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.*
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit


object Formatter {
    private val dateFormatD1 = SimpleDateFormat("dd MMM", Locale("en"))
    private val dateFormatW1M1 = SimpleDateFormat("yyyy, dd MMM", Locale("en"))
    private val dateFormatChart = SimpleDateFormat("HH:mm", Locale("en"))
    private val dateFormatChartFull = SimpleDateFormat("HH:mm dd MMM", Locale("en"))

    val dateFormatDst = SimpleDateFormat("HH:mm, dd MMM", Locale("en"))
    val dateFormatSrc = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale("en"))
    val dateFormatLin = SimpleDateFormat("dd/MM/yy, HH:mm", Locale.getDefault())
    val dateFormatOrder = SimpleDateFormat("MMM dd, HH:mm", Locale("en"))
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()) // 2017-10-19T16:33:42.821Z
    val dateFormatStandart = SimpleDateFormat("dd/MM/yy, HH:mm", Locale("en"))
    val dateFormatDMYOnly = SimpleDateFormat("dd/MM/yy", Locale("en"))

    var numberFormat = NumberFormat.getInstance(Locale("en-US"))
    var numberChartAxisFormat = NumberFormat.getInstance(Locale("en-US"))
    var numberFormatRoundUpTo11 = NumberFormat.getInstance(Locale("en-US"))
    var numberFormatRoundUpTo13 = NumberFormat.getInstance(Locale("en-US"))
    var numberFormatRoundUpTo4 = NumberFormat.getInstance(Locale("en-US"))
    var numberFormatLong = NumberFormat.getInstance(Locale("en-US"))
    var numberFormatLarge = NumberFormat.getInstance(Locale("en-US"))
    var numberFormatShort = NumberFormat.getInstance(Locale("en-US"))
    var charDateFormat = SimpleDateFormat("MM-dd HH:mm")
    var splitPattern: Regex = Regex("""/|\s|\\""")

    init {
        numberFormat.maximumFractionDigits = 8
        numberFormat.minimumFractionDigits = 1

        numberFormatRoundUpTo11.maximumFractionDigits = 11
        numberFormatRoundUpTo11.minimumFractionDigits = 1

        numberFormatRoundUpTo13.maximumFractionDigits = 13
        numberFormatRoundUpTo13.minimumFractionDigits = 1

        numberFormatLong.maximumFractionDigits = 11
        numberFormatLong.minimumFractionDigits = 1

        numberFormatLarge.maximumFractionDigits = 0
        numberFormatLarge.minimumFractionDigits = 0

        numberFormatShort.maximumFractionDigits = 6
        numberFormatShort.minimumFractionDigits = 1

        numberFormatRoundUpTo4.maximumFractionDigits = 4
        numberFormatRoundUpTo4.minimumFractionDigits = 0
    }

    fun currency(currency: String, isFullName:Boolean = false): String {
        return if(RXCache.currencies.containsKey(currency)&&!RXCache.getCurrency(currency)?.crypto!!)
        {   if(!isFullName) currencyFullName(currency)
        else return currencyName(currency)
        }
        else replaceUsdt(currency)
    }

    fun currencyName(currency: String): String{
        val c = RXCache.getCurrency(currency)?.name ?: ""
        return if (c.contains("Dai", true)) "DAI"
        else c
    }

    fun currencyFullName(currency: String): String{
        val c = RXCache.getCurrency(currency)?.fullName ?: ""
        return if (c.contains("Dai", true)) "DAI"
        else c
    }

    fun symbol(symbol: String): String {
        val symbol = RXCache.getSymbol(symbol) ?: return ""
        return symbol(symbol)
    }

    fun symbol(symbol: SymbolResponse): String {
        return "${currency(symbol.baseCurrency)} / ${currency(symbol.quoteCurrency)}"
    }

    fun amountMarkets(amount: Double): String {
        return if (amount > 10) numberFormatLarge.format(amount) else numberFormatShort.format(amount)
    }

    fun amountMarketsDepth(amount: Double): String {
        return if (amount > 10) numberFormatLarge.format(amount) else numberFormatRoundUpTo11.format(amount)
    }

    fun amount(amount: Double): String {
        return if (amount % 1 == 0.0) amount.toInt().toString()
        else numberFormat.format(amount)
    }

    fun amount(amount: Float): String {
        return if (amount % 1 == 0f) amount.toInt().toString()
        else numberFormat.format(amount)
    }

    fun amount(amount: String?) : String {
        val safeAmount = try {
            amount?.toDouble() ?: 0.0
        } catch (e: Exception){
        //    ErrorWrapper.onError(e)
            0.0
        }
        return amount(safeAmount)
     }

    fun amountRoundUpTo11(amount: Double): String {
        return numberFormatRoundUpTo11.format(amount)
    }

    fun amountRoundUpTo11(amount: BigDecimal): String {
        return numberFormatRoundUpTo11.format(amount)
    }

    fun amountRoundUpTo13(amount: BigDecimal): String {
        return numberFormatRoundUpTo13.format(amount)
    }

    fun amountRoundUpTo13(amount: Double): String {
        return numberFormatRoundUpTo13.format(amount)
    }

    fun amountRoundUpTo4(amount: Double): String{
        return numberFormatRoundUpTo4.format(amount)
    }

    fun amountLong(amount: Double): String {
        return numberFormatLong.format(amount)
    }

    fun amountWithAccuracy(amount : Double, accuracy : Int) : String {
        return if (accuracy <= 0) String.format(Locale.US, "%d", amount.toLong()) else String.format(Locale.US, "%." + accuracy + "f", amount)
    }

    fun amountVolumeWithAccuracy(amount : Double, accuracy : Int): String {
        return if (amount>=10){
            numberFormatLarge.format(amount)
        }
        else amountWithAccuracy(amount, accuracy)
    }

    fun amountWithAccuracy(amount : Float, accuracy : Int) : String {
        return if (accuracy <= 0) String.format(Locale.US, "%d", amount.toLong()) else String.format(Locale.US, "%." + accuracy + "f", amount)
    }

    fun amountWithAccuracy(amount : BigDecimal, accuracy : Int) : String {
        return if (accuracy <= 0) String.format(Locale.US, "%d", amount.toLong()) else String.format(Locale.US, "%.${accuracy}f", amount)
    }

    fun amountChart(amount: Double): String {
        return if(Math.abs(amount) > 10) amountWithAccuracy(amount, 3) else amount(amount)
    }

    fun amountChart(amount: Double, accuracy: Int): String {
        return /*if(Math.abs(amount) > 10) amountWithAccuracy(amount, accuracy) else amount(amount)*/ amountWithAccuracy(Math.abs(amount), accuracy)
    }

    fun amountSumOrderbook(amount: Double, accuracy: Int): String {
        return when {
            amount >= 1_000_000 -> "${roundDownWithZeros((amount / 1_000_000), 1)}M"
            amount >= 1000 -> "${roundDownWithZeros((amount / 1000), 1)}K"
            accuracy > 5 -> roundDownWithZeros(amount, 5)
            else -> amountWithAccuracy(amount, accuracy)
        }
    }

    fun amountWithAccuracyAndRounding(amount: String?, accuracy: Int, roundingMode: RoundingMode = RoundingMode.DOWN): String{
        val bd = (amount?.toBigDecimalOrNull() ?: return "0")
        return bd.setScale(accuracy, roundingMode).toPlainString().stripTrailingZeros()
    }

    fun amountWithAccuracyAndRounding(amount: Double, accuracy: Int, roundingMode: RoundingMode = RoundingMode.DOWN): String{
        val bd = BigDecimal(amount)
        return bd.setScale(accuracy, roundingMode).toPlainString().stripTrailingZeros()
    }

    fun amountWithAccuracyAndRounding(amount: BigDecimal, accuracy: Int, roundingMode: RoundingMode = RoundingMode.DOWN): String{
        return amount.setScale(accuracy, roundingMode).toPlainString().stripTrailingZeros()
    }

    fun formatWithAccuracy(symbol: String, value: Double): String{
        val amountTick = RXCache.getSymbol(symbol)?.quantityIncrement?.toBigDecimal() ?: BigDecimal(0.0)
        val amountAccuracy = getNumberAfterDot(amountTick)
        return amountSumOrderbook(value, amountAccuracy)
    }

    fun roundDownWithZeros(value: Double, withAccuracy: Int): String {
        println(value)
        val formatSymbols = DecimalFormatSymbols(Locale.getDefault())
        formatSymbols.decimalSeparator = '.'
        val df = DecimalFormat("0.${"0".repeat(withAccuracy)}", formatSymbols)
        df.roundingMode = RoundingMode.DOWN
        return df.format(value)
    }

    fun volumeChart(volume: Double): String{
        return when {
            volume < 10 -> {
                numberFormat.format(volume)
            }
            volume >= 1_000_000 -> {
                val short = BigDecimal(volume / 1_000_000)
                "${short.setScale(1, RoundingMode.DOWN)}M"
            }
            volume >= 1_000 -> {
                val short = BigDecimal(volume / 1_000)
                "${short.setScale(1, RoundingMode.DOWN)}K"
            }
            else -> {
                amountWithAccuracy(volume, 2)
            }
        }
    }

    fun volumeChart(volume: Float, accuracy: Int? = null): String{
        try {
            return when {
                volume < 10 -> {
                    if (accuracy != null) {
                        stringFormatForAxis(volume, accuracy)
                    } else {
                        numberFormat.format(volume)
                    }
                }
                volume >= 1_000_000 -> {
                    val short = (volume / 1_000_000).toBigDecimal()
                    "${short.setScale(1, RoundingMode.DOWN)}M"
                }
                volume >= 1_000 -> {
                    val short = (volume / 1_000).toBigDecimal()
                    "${short.setScale(1, RoundingMode.DOWN)}K"
                }
                else -> {
                    amountWithAccuracy(volume, 2)
                }
            }
        } catch (e: Exception) {
            return ""
        }
    }
    fun volumeChart(volume: Float, accuracy: Int?  = null, hasKilos: Boolean = false, hasMegas: Boolean = false): String{
        try {
            return when {
                volume == 0f -> "0"
                hasKilos -> {
                    val short = (volume / 1_000).toBigDecimal()
                    "${short.setScale(1, RoundingMode.DOWN)}K"
                }
                hasMegas -> {
                    val short = (volume / 1_000_000).toBigDecimal()
                    "${short.setScale(1, RoundingMode.DOWN)}M"
                }
                else -> volumeChart(volume, accuracy)
            }
        } catch (e: Exception) {
            return ""
        }
    }

    fun amountChart(amount: Float, accuracy: Int): String {
        return if(Math.abs(amount) > 10) amountWithAccuracy(amount, accuracy) else amount(amount)
    }

    fun date(date : Date) : String {
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(date)
    }

    fun standardDateFormat(date: Date) : String {
        return dateFormatStandart.format(date)
    }

    fun socketDate(date : Date) : String {
        return dateFormat.format(date)
    }

    fun currencyHistoryDateFragment(date: Date) : String {
        return dateFormatLin.format(date)
    }

    fun dateOrderFragment(date: Date) : String {
        return dateFormatOrder.format(date)
    }

    fun chartDate(date: Date, period: String? = null): String {
        val str = when (period) {
            "1M", "D7" -> dateFormatW1M1.format(date)
            "D1" -> dateFormatD1.format(date)
            null -> charDateFormat.format(date)
            else -> dateFormatChart.format(date)
        }
        return if (str == "00:00") dateFormatChartFull.format(date)
        else str
    }

    fun timestamp(timestamp: String) : String {
       /* var instant = ZonedDateTime.parse(timestamp)
        val a = TimeUnit.HOURS.convert(TimeZone.getDefault().rawOffset.toLong(), TimeUnit.MILLISECONDS)
        instant = instant.minusHours(-a)

        val date = "${instant.dayOfMonth} " + "${instant.month.toString().subSequence(0, 3)}, "
        val hour = if (instant.hour <= 9) "0${instant.hour}:" else "${instant.hour}:"
        val minute = if (instant.minute <= 9) "0${instant.minute}:" else "${instant.minute}:"
        val second = if (instant.second <= 9) "0${instant.second}" else "${instant.second}"
*/
        return " "
    }

    fun replaceUsdt(str: String): String {
        if(str == "USDC") return str
        if(str == "USDTUSD") return "USDTTUSD"
        if(str == "USDGUSD") return "USDTGUSD"

        var ret = str
        //if(ret.contains("USD") && !ret.contains("TUSD") && !ret.contains("GUSD") && !ret.contains("USDT")) ret = ret.replace("USD", "USDT")
        if(ret.contains("CVCOIN")) ret = ret.replace("CVCOIN", "CVN")
        if(str == "dai") ret = "Dai"

        return ret
    }

    fun fullCurrencyName(str: String): String {
        if(str == "dai") return "DAI"

        return str
    }

    fun getNumberAfterDot(number: Double): Int {
        try {
            return number.toBigDecimal().stripTrailingZeros().scale()
        } catch (e: Exception) {
            return 0
        }
    }

    fun getNumberAfterDot(number: BigDecimal): Int {
        return number.stripTrailingZeros().scale()
    }

    fun getNumberAfterDot(number: Float): Int {
        try {
            return number.toBigDecimal().stripTrailingZeros().scale()
        } catch (e: Exception) {
            return 0
        }
    }

    fun doubleWithAccuracy(double: Double, numberAfterDot: Int): String{
        val res = String.format(Locale.ENGLISH, "%.${numberAfterDot}f", double)
        return if (res.contains('.')) res.replace(Regex("[.]?0*$"), "") else res
    }

    fun trimTrailingZeros(amount: String): String {
        if (amount == "0" || amount.endsWith(".0") || amount.indexOf('.') == -1) return amount
        val ret = amount.trimEnd('0')

        if (ret.endsWith(".")) return "${ret}0"

        return ret
    }

    fun getStatus(apiStatus: String) : Int{
        return when(apiStatus){
                    "canceled" -> R.string.canceled
                    "new" -> R.string.new_
                    "suspended" -> R.string.suspended
                    "partiallyFilled" -> R.string.partially_filled
                    "filled" -> R.string.filled
                    "expired" -> R.string.expired
                    else -> R.string.undefined
                }
    }

    fun formatBalance(amount: String): String {
        val doubleAmount = amount.toDoubleOrNull()

        var balance = if(doubleAmount == null || doubleAmount <= -0.0) "0"
        else amountRoundUpTo13(BigDecimal(amount))

        return if(balance.toDouble() == 0.0) "0"
        else balance
    }

    fun formatForAxisWithAccuracy(number: Float, axisAccuracy: Int, isRoundUp: Boolean): Float {
        try {
            return when {
                axisAccuracy > 0 -> {
                    number.toBigDecimal().setScale(axisAccuracy, if (isRoundUp) RoundingMode.UP else RoundingMode.DOWN).toFloat()
                }
                axisAccuracy < 0 -> {
                    val rate = (Math.pow(10.toDouble(), -axisAccuracy.toDouble())).toFloat()
                    val tail = number % rate
                    val head = number - tail
                    if (tail > 0 && isRoundUp) {
                        head + rate
                    } else {
                        head
                    }
                }
                else -> {
                    val floor = number.toInt()
                    if (isRoundUp && (number - floor) != 0f) {
                        (floor + 1).toFloat()
                    } else {
                        floor.toFloat()
                    }
                }
            }
        } catch (e: Exception) {
            return 0f
        }
    }

    fun stringFormatForAxis(number: Float, axisAccuracy: Int): String {
        return if (number == 0f) {
            "0.0"
        } else {
            if (axisAccuracy > 0) {
                String.format(Locale.ENGLISH, "%.${axisAccuracy}f", number)
            } else {
                String.format(Locale.ENGLISH, "%.8f", number)
            }
        }
    }

    fun stringFormatForChartValue(number: Float, currencyAccuracy: Int): String {
        numberChartAxisFormat.minimumFractionDigits = currencyAccuracy
        numberChartAxisFormat.maximumFractionDigits = currencyAccuracy
        return numberChartAxisFormat.format(number)
    }

    fun String.stripTrailingZeros(): String {
        val buff = StringBuilder(this)
        while ((((buff.getOrNull(buff.length - 1)
                        ?: "") == '0') && buff.getOrNull(buff.length - 2) ?: "" != '.')) {
            buff.setLength(buff.length - 1)
        }
        return buff.toString()
    }
}