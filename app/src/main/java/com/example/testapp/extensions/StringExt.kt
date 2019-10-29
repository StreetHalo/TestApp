package trade.paper.app.utils.extensions

import android.util.Log
import trade.paper.app.models.hawk.Settings
import java.text.SimpleDateFormat
import java.util.*

val apiDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale("en")).apply {
    timeZone = TimeZone.getTimeZone("GMT")
}
val dateFormatTimesAndSalesWithDay = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("en"))
val dateFormatTimesAndSalesWithoutDay = SimpleDateFormat("HH:mm:ss", Locale("en"))
val dateFormatTimesAndSalesWithMonth = SimpleDateFormat("HH:mm, dd MMM", Locale("en"))
val dateFormatTimesAndSalesMillisecondsWithDay = SimpleDateFormat("MM-dd HH:mm:ss", Locale("en"))
val dateFormatTimesAndSalesMillisecondsWithoutDay = SimpleDateFormat("HH:mm:ss.SSS", Locale("en"))

fun String.parseDouble(): Double? {
    return try {
        this.toDouble()
    } catch (e: Exception) {
        Log.d("ParsingError", "Cant parse to double: $this")
        null
    }
}

/**
 * Parse and format timestamp to display date in Times & Sales lists.
 *
 * If day of order is today than result contains only hours, minutes and seconds.
 * Otherwise, result contains full date including year, month and day.
 *
 * @return formatted string for Times & Sales
 */
fun String.parseDateForTimeAndSales(): String {
    return try {
        val apiDate = apiDateFormat.parse(this)
        if (apiDate.isToday()) {
            dateFormatTimesAndSalesWithoutDay.format(apiDate)
        } else {
            dateFormatTimesAndSalesWithDay.format(apiDate)
        }
    } catch (e: Exception) {
        "-"
    }
}

fun String.parseDateForTimeAndSalesWithMonth(): String {
    return try {
        val apiDate = apiDateFormat.parse(this)
        if (apiDate.isToday()) {
            dateFormatTimesAndSalesWithoutDay.format(apiDate)
        } else {
            dateFormatTimesAndSalesWithMonth.format(apiDate)
        }
    } catch (e: Exception) {
        "-"
    }
}

fun String.parseDateForTimeAndSalesWithMilliseconds(): String {
    return try {
        val apiDate = apiDateFormat.parse(this)
        if (apiDate.isToday()) {
            dateFormatTimesAndSalesMillisecondsWithoutDay.format(apiDate)
        } else {
            dateFormatTimesAndSalesMillisecondsWithDay.format(apiDate)
        }
    } catch (e: Exception) {
        "-"
    }
}