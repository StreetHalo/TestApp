package trade.paper.app.utils.extensions

import android.widget.TextView
import com.example.testapp.R


fun TextView.isAvailable(isAvailable: Boolean) {
    this.isClickable = isAvailable
    if (!isAvailable) {
        this.setTextColor(resources.getColor(R.color.dark_blue))
    } else {
        this.setTextColor(resources.getColor(R.color.rd_blue))
    }
}