package trade.paper.app.utils.extensions

import android.widget.EditText

fun EditText.moveSelection() {
    this.setSelection(this.text.length)
}