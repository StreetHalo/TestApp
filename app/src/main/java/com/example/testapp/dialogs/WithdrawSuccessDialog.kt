package trade.paper.app.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Handler
import com.example.testapp.R
import kotlinx.android.synthetic.main.dialog_withdraw_success.*

class WithdrawSuccessDialog(context: Context, private val sum: String, onClick: () -> Unit) : Dialog(context) {

    init {
        setContentView(R.layout.dialog_withdraw_success)
        tv_withdraw_dialog_summ.text = sum
        btn_track_your_transaction.setOnClickListener {
            onClick()
        }
    }

    override fun show() {
        super.show()
        Handler().postDelayed({
            dismiss()
        }, 5000)
    }
}