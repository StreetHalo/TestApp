package trade.paper.app.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import android.webkit.WebView
import com.example.testapp.R

class PrivacyDialog(activity: Activity, var onPass: () -> Unit ): CustomDialog(activity, false) {
    override fun bindViews(v: View) {
        val webView: WebView = v.findViewById(R.id.dialog_web_view)
        webView.loadUrl(activity.getString(R.string.privacy_policy_url))
        webView.settings.javaScriptEnabled = true
    }

    override var layout: Int = R.layout.dialog_privacy_policy

    override fun updateDialog(dialog: AlertDialog.Builder) {
        dialog.setTitle(R.string.privacy_policy)
        dialog.setPositiveButton(R.string.accept_eng){ dialog, which ->
            onPass()
        }
        dialog.setNegativeButton(R.string.decline){ dialog, which ->
            activity.finish()
        }
    }
}