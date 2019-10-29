package trade.paper.app.dialogs

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import com.example.testapp.R
import trade.paper.app.utils.extensions.showActionDisabled
import trade.paper.app.utils.extensions.showActionEnabled
import kotlinx.android.synthetic.main.dialog_transfer_to_main_success.*

class TransferToMainSuccessDialog(context: Context, currency: String) : Dialog(context) {

    var onBtnWithdrawClick: (() -> Unit)? = null

    init {
        setContentView(R.layout.dialog_transfer_to_main_success)
        btn_withdraw.setOnClickListener {
            dismiss()
            onBtnWithdrawClick?.invoke()
        }

        btn_close.setOnClickListener {
            dismiss()
        }
        //this.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.window?.setGravity(Gravity.CENTER)
        //popular_pair_with_currency.setText(context.getString(R.string.top_24h_volume_pairs_with, currency))

    }

    fun setWithdrawVisible(visible: Boolean) {
        if (!visible) btn_withdraw?.apply {
            showActionDisabled(context, this, true)
            this.isClickable = false
        }
        else showActionEnabled(context, btn_withdraw
                ?: return)

        // Constraint layout is awesome
        /* var text = context.getString(R.string.tranfer_to_main_success_info)
        textView58.text = text
        if(visible) textView58.text = "${textView58.text}\n"*/
    }

    fun show(sum: String){
        tv_transfer_summ.text = sum
        super.show()
    }
}