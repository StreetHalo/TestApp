package trade.paper.app.utils.extensions

import java.util.*


/**
 * Checks if two dates are on the same day ignoring time.
 * @param compareDate the date to be compared
 * @return true if they represent the same day
 */
fun Date.isSameDay(compareDate: Date): Boolean {
    val cal1 = Calendar.getInstance()
    cal1.time = this
    val cal2 = Calendar.getInstance()
    cal2.time = compareDate
    return cal1.isSameDay(cal2)
}


/**
 * Checks if a date is today.
 * @return true if the date is today.
 */
fun Date.isToday(): Boolean = this.isSameDay(Calendar.getInstance().time)
