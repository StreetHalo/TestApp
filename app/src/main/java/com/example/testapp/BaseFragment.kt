package com.example.testapp

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.example.testapp.home.HomeActivity
import trade.paper.app.models.TAG
import trade.paper.app.models.cache.RXCache
import trade.paper.app.models.hawk.Settings
import trade.paper.app.views.CustomSearchView
import java.util.*

abstract class BaseFragment : Fragment() {

    val sideMain = "main"
    val sideTrades = "trades"
    var startTimeLoadCandles = -1L

    open var showBottomNav: Boolean? = null
    open var mayGenerateBlueScreen = false


    open fun onScreen(){}

    fun provideArrayAdapter(data: MutableList<String>): ArrayAdapter<String> {
        val adapter = ArrayAdapter<String>(
                context!!,
                android.R.layout.simple_spinner_item,
                data
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

    fun inflate(res: Int): View {
        return LayoutInflater.from(context).inflate(res, null, false)
    }

    fun hideKeyboard(layout: CustomSearchView){
        (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)?.hideSoftInputFromWindow(layout.getWindowToken(), 0)
    }

    fun setupToolbar(v: View, res: Int) {
        var toolbar: Toolbar = v.findViewById(res)
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar!!.show()
    }

    fun toast(msg: String) {
        activity?.runOnUiThread {
            if (context != null && !msg.contains("429") && !msg.contains("too many request")) Toast.makeText(context!!, msg, Toast.LENGTH_SHORT).show()
        }

    }

    fun error(e: Throwable){
        Log.e(TAG.ERROR.tag, e.localizedMessage ?: "somewhere")
    }

    fun inUI(action: (() -> Unit)) {
        activity?.runOnUiThread(action)
    }

    fun drawable(res: Int): Drawable {
        return ContextCompat.getDrawable(context!!, res)!!
    }

    fun color(res: Int): Int {
        return ContextCompat.getColor(context?:return 0, res)
    }

    fun llm(): androidx.recyclerview.widget.LinearLayoutManager {
        return WrapContentLinearLayoutManager(context!!)
    }

    open fun getBackClick(): (() -> Unit)? = null

    fun showThrowable(err: String) {
        toast(err)
    }

    open fun upNetwork() {}

    open fun noNetwork() {}

    fun shouldShowPinFragment(): Boolean {
        return true
    }

    /* Method should be override if you need inside logic of backpress
     *
     * @return:
     * true - navigation logic has been used in current fragment and no need to use BaseActivity.onBackPressed logic
     * false - use outside logic in BaseActivity to navigation
     */
    open fun onBackPressed(): Boolean {
        return false
    }

    fun bottomNav(): BottomNavigationView?{
     return (activity as HomeActivity).findViewById(R.id.bottom_nav)
    }

    fun catch(e: Exception){
        Log.d("exception", e.localizedMessage ?: e.toString())
    }

    open fun updateLocale(locale: String) {
     /*   val config = resources.configuration
        config.setLocale(Locale(locale))
        resources.updateConfiguration(config, resources.displayMetrics)
        Settings.setLocale(locale)
        (activity as MainActivity).detachAll()

        fragmentManager!!.beginTransaction().detach(this).attach(this).commit()

        val navBar = (activity as? MainActivity)?.findViewById<BottomNavigationView>(R.id.bottom_nav)
                ?: return
        navBar.menu.findItem(R.id.markets_menu_item).setTitle(R.string.markets)
        navBar.menu.findItem(R.id.home_menu_item).setTitle(R.string.home)
        navBar.menu.findItem(R.id.trades_menu_item).setTitle(R.string.exchange)
        navBar.menu.findItem(R.id.reports_menu_item).setTitle(R.string.orders)
        navBar.menu.findItem(R.id.account_menu_item).setTitle(R.string.account)*/
    }

    fun startTrackingTimeForAnalytics() {
        startTimeLoadCandles = System.currentTimeMillis()
    }

  //  fun checkTimeAndSendEventToAnalytics(event: AnalyticsManager.Event) {
       /* if (startTimeLoadCandles != -1L) {
            val loadTimeCandles = System.currentTimeMillis() - startTimeLoadCandles
            AnalyticsManager.sendEvent(event, loadTimeCandles)
            Log.d("TimeAnalyse", "$event is: $loadTimeCandles")
            startTimeLoadCandles = -1L
        }*/
   // }

    open fun onShow() {
        initBottomNav()
      //  RotationManager.unbind()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomNav()
    }


    open fun initBottomNav() {
      /*  showBottomNav?.let {
            if (it) {
                bottomNav().visible()
            } else {
                bottomNav().gone()
            }
        }*/
    }

   /* override fun onAttach(context: Context?) {
        RXCache.stock = getString(R.string.stocks)
        RXCache.crypto = getString(R.string.crypto)
        RXCache.updateSymbolTab()
        super.onAttach(context)
    }
*/
    fun setImeVisibility(visible: Boolean, searchView: EditText) {
        val showRunnable = mShowImeRunnable(searchView)

        if (visible) {
            Looper.prepare()
            handler.post(showRunnable)
        } else {
            handler.removeCallbacks(showRunnable)
            val imm = context!!
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            imm.hideSoftInputFromWindow(searchView.windowToken, 0)
        }
    }

    private fun mShowImeRunnable(searchView: EditText) = Runnable {
        val imm = context!!
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.showSoftInput(searchView, 0)
    }
    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    class WrapContentLinearLayoutManager(val context: Context) : androidx.recyclerview.widget.LinearLayoutManager(context) {

        override fun onLayoutChildren(recycler: androidx.recyclerview.widget.RecyclerView.Recycler?, state: androidx.recyclerview.widget.RecyclerView.State?) {
            try {
                super.onLayoutChildren(recycler, state)
            }
            catch (e: IndexOutOfBoundsException){
                Log.e(trade.paper.app.models.TAG.ERROR.tag, "IOOB in recycler again")
            }
            catch (e: Exception){
                Log.e(trade.paper.app.models.TAG.ERROR.tag, "Something in recycler again")
            }
        }
    }
}