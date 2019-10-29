package trade.paper.app.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View

abstract class CustomDialog(
        val activity: Activity,
        val cancelable: Boolean = true,
        val onClose: (() -> Unit)? = null
) {
    var dialog : AlertDialog? = null
    open var layout: Int = 0

    abstract fun bindViews(v: View)

    fun getCustomDialog(): AlertDialog{
        val dialogView = LayoutInflater.from(activity).inflate(layout, null)
        bindViews(dialogView)
        val dialogBuilder = AlertDialog.Builder(activity)
                .setView(dialogView)
                .setCancelable(cancelable)
                .setOnCancelListener {
                    onClose?.invoke()
                }
        updateDialog(dialogBuilder)
        dialog = dialogBuilder.create()
        return dialog!!
    }

    open fun updateDialog(dialog: AlertDialog.Builder){

    }

    fun dismiss() = dialog?.dismiss()

    fun show() = dialog?.show()

    fun hide() = dialog?.dismiss()

    fun isShowing() = dialog?.isShowing
}