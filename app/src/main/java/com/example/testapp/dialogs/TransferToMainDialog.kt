package trade.paper.app.dialogs

import android.app.Dialog
import android.content.Context
import com.example.testapp.R
import kotlinx.android.synthetic.main.dialog_transfer_to_main.*

class TransferToMainDialog(context: Context) : Dialog(context) {

    var transferToMain: (() -> Unit)? = null

    init {
        setContentView(R.layout.dialog_transfer_to_main)
        btn_transfer_to_main.setOnClickListener {
            dismiss()
            transferToMain?.invoke()
        }
        btn_close.setOnClickListener {
            dismiss()
        }
        //this.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}