package trade.paper.app.fragments.home.settings


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import trade.paper.app.models.TAG
import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.hawk.Settings
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.example.testapp.BaseFragment
import com.example.testapp.R
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(), View.OnClickListener {

    override var showBottomNav: Boolean? = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLoginTheme()
        val locale = "en" ?: "en"
     //   tv_curent_locale.text = SharedActions.languageData[locale]

     //   tv_build_version.text = "${getString(R.string.build_version)} ${BuildConfig.VERSION_NAME}"

       /* RXCache.isLogined.observe(this, androidx.lifecycle.Observer {
            it?.let {
                if (it) {*/
                /*} else {
                    btn_logout_settings.text = getString(R.string.log_in)
                }
            }*/
        //})
        btn_remove_pin_code.setOnClickListener(this)
        btn_set_pin_code.setOnClickListener(this)
        btn_back.setOnClickListener(this)
        btn_language_settings.setOnClickListener(this)
        btn_change_pin_settings.setOnClickListener(this)
        btn_support_settings.setOnClickListener(this)
        btn_licenses_settings.setOnClickListener(this)
        btn_terms_of_use_settings?.setOnClickListener(this)
        btn_fees_and_limits_settings?.setOnClickListener(this)
        btn_privacy_policy.setOnClickListener(this)
    }

    fun setLoginTheme(){

     /*   if((activity as BaseActivity).doYouNeedLogin)
        {
            btn_set_pin_code.visibility = View.GONE
            btn_change_pin_settings.visibility = View.VISIBLE
            btn_remove_pin_code.visibility = View.VISIBLE
        }
        else
        {
            btn_set_pin_code.visibility = View.VISIBLE
            btn_change_pin_settings.visibility = View.GONE
            btn_remove_pin_code.visibility = View.GONE
        }*/
    }

    override fun onClick(v: View) {
        when (v.id) {


//                AnalyticsManager.sendEvent(AnalyticsManager.Event.AccountSettingsOpenLicensesClicked)
//
//                val fragment = AboutFragment()
//                val arguments = Bundle()
//                arguments.putString("license", "MPAndroidChart")
//                fragment.arguments = arguments
//                (activity as BaseActivity).openFragment(fragment)

        }
    }


    override fun updateLocale(locale: String) {
        super.updateLocale(locale)
     //   tv_curent_locale?.text = SharedActions.languageData[locale]
        fragmentManager?.beginTransaction()?.detach(this)?.attach(this)?.commitAllowingStateLoss()
    }
}
