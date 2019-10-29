package trade.paper.app.utils.extensions

import java.util.*


/**
 *
 * Checks if two calendars represent the same day ignoring time.
 * @param compareCalendar the calendar to be compared
 * @return true if they represent the same day
 */
fun Calendar.isSameDay(compareCalendar: Calendar): Boolean {
    return this.get(Calendar.ERA) == compareCalendar.get(Calendar.ERA) &&
            this.get(Calendar.YEAR) == compareCalendar.get(Calendar.YEAR) &&
            this.get(Calendar.DAY_OF_YEAR) == compareCalendar.get(Calendar.DAY_OF_YEAR)
}