package trade.paper.app.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.example.testapp.R
import trade.paper.app.utils.extensions.gone

class GoToBinanceDialog(activity: Activity): CustomDialog(activity) {
    override var layout: Int = R.layout.dialog_go_to_binance

    override fun bindViews(v: View) {
       v.findViewById<LinearLayout>(R.id.btn_go_to_binance).setOnClickListener {
          // SharedActions.openURL(R.string.binance_with_refcode, activity)
       }

    }
}