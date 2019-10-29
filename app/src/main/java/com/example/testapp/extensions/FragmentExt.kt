package trade.paper.app.utils.extensions

import android.content.Context
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager

fun Fragment.hideKeyboard() {
    val activity = this.activity ?: return

    val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocusedView = activity.currentFocus
    if (currentFocusedView != null) {
        inputManager.hideSoftInputFromWindow(currentFocusedView!!.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
    }
}