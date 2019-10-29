package trade.paper.app.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.testapp.R
import kotlinx.android.synthetic.main.alert_pin_changed.view.tw_alert
import kotlinx.android.synthetic.main.alert_with_text.view.btn_accept_alert
import kotlinx.android.synthetic.main.alert_withdrawals_unavailable.view.*

class WithdrawUnavailableDialog (var openTransfer: () -> Unit)  {
    lateinit var dialog : AlertDialog

    fun getDialog(context: Context): AlertDialog {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.alert_withdrawals_unavailable, null)
        dialogView.apply {
            tw_alert.text = context.getString(R.string.withdraw_unavailable)
            btn_accept_alert.setOnClickListener {
                dialog.dismiss()
                openTransfer()
            }
            btn_close_alert_withdraw.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create()
        return dialog
    }

    fun show() = dialog.show()

    fun hide() = dialog.dismiss()

    fun isShowing() = dialog.isShowing
}