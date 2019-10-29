package trade.paper.app.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.testapp.R
import kotlinx.android.synthetic.main.alert_pin_changed.view.*

class PinDialog (val closeDialogAction: () -> Unit) {
    var dialog : AlertDialog?  = null

    fun getDialog(context: Context, changeingPin: Boolean) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.alert_pin_changed, null)
        dialogView.apply {
            tw_title_alert.text = context.getString(if (changeingPin) R.string.PIN_changed else R.string.PIN_created)
            btn_close_alert.setOnClickListener { closeDialogAction() }
        }

        dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .setOnCancelListener {
                    closeDialogAction()
                }
                .create()
    }

    fun show() = dialog?.show()

    fun hide() = dialog?.dismiss()

    fun isShowing() = dialog?.isShowing
}