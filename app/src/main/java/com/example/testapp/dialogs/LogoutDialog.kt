package trade.paper.app.dialogs


import android.app.Activity
import android.content.Intent
import android.view.View
import com.example.testapp.R
import kotlinx.android.synthetic.main.dialog_logout.view.*

class LogoutDialog(activity: Activity, var logoutSucceeded: () -> Unit) : CustomDialog(activity){

    override var layout: Int
        get() = R.layout.dialog_logout
        set(value) {}

    override fun bindViews(v: View) {
        v.apply {
            logout_close.setOnClickListener { dismiss() }
            btn_log_out.setOnClickListener {
            }
            cancel_logout.setOnClickListener { dismiss() }
        }
    }
}