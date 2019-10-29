package com.example.testapp.home

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.testapp.DetailActivity
import com.example.testapp.R
import com.example.testapp.SettingsActivity
import com.example.testapp.SubscriptionActivity
import com.example.testapp.ui.dashboard.AccountActivity
import com.example.testapp.ui.dashboard.ExchangeActivity
import com.example.testapp.ui.dashboard.MarketsActivity
import com.example.testapp.ui.dashboard.OrdersActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.bottom_nav
import kotlinx.android.synthetic.main.activity_main.btn_settings_home_main
import kotlinx.android.synthetic.main.activity_main.layout_home_toolbar
import kotlinx.android.synthetic.main.activity_main.layout_toolbar_container
import kotlinx.android.synthetic.main.activity_main.tl_home_main
import kotlinx.android.synthetic.main.activity_main.vp_home_main
import kotlinx.android.synthetic.main.fragment_home_main.*
import kotlinx.android.synthetic.main.layout_home_logined.*
import kotlinx.android.synthetic.main.layout_home_logined.view.*
import trade.paper.app.fragments.home.homeTab.HomeTabFragment
import trade.paper.app.fragments.home.settings.SettingsFragment

class HomeActivity : AppCompatActivity() {


    private val vpAdapter by lazy {
        HomePagerAdapter(supportFragmentManager)
    }

    override fun onResume() {
        bottom_nav.selectedItemId = R.id.home_menu_item
        //layout_search.setOnClickListener(clickListener)
        super.onResume()
    }

    private fun initViewPager() {
        vp_home_main.adapter = vpAdapter
        tl_home_main.setupWithViewPager(vp_home_main)
     //   tl_home_main.getTabAt(3)?.customView = SharedActions.favsView(context!!, 3, 12f)
        val titles = arrayOf(
            "FAVS",
            getString(R.string.gainers),
            getString(R.string.losers)
        )

        for (i in 0 until 3) {
            tl_home_main.getTabAt(i)?.text = titles[i]
        }
        vp_home_main.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(position: Int) {}

            override fun onPageScrolled(position: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(position: Int) {
                tl_home_main.getTabAt(2)?.customView?.let {
                    (it as TextView).compoundDrawables[0].setColorFilter(if (position == 2) resources.getColor(R.color.white) else resources.getColor(R.color.gray_50), PorterDuff.Mode.SRC_IN)
                }
            }
        })

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav)
        setCheckedMenuItem(R.id.reports_menu_item, true)
        val search = findViewById<View>(R.id.layout_search)
        search.setOnClickListener {
            openMarkets()
        }
        setLoggedView()
        initViewPager()



        navView.setOnNavigationItemSelectedListener{item ->

            when (item.itemId) {
                R.id.home_menu_item -> {
                  //  FLOW_HOME
                }
                R.id.trades_menu_item -> {
                    val notificationIntent = Intent(this, ExchangeActivity::class.java)
                    startActivity(notificationIntent)
                  //  finish()
                }
                R.id.reports_menu_item -> {
                    val notificationIntent = Intent(this, OrdersActivity::class.java)
                    startActivity(notificationIntent)
                  //  finish()
                }
                R.id.account_menu_item -> {
                    val notificationIntent = Intent(this, AccountActivity::class.java)
                    startActivity(notificationIntent)
                 //   finish()
                }
                else -> {
                    openMarkets()
                        //     finish()
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        btn_settings_home_main.setOnClickListener {
            val notificationIntent = Intent(this, SettingsActivity::class.java)
            startActivity(notificationIntent)
        }
    }


    fun openMarkets(){
        val notificationIntent = Intent(this, MarketsActivity::class.java)
        startActivity(notificationIntent)
    }

    fun openDetails(){
        val notificationIntent = Intent(this, DetailActivity::class.java)
        startActivity(notificationIntent)
    }

    private fun setCheckedMenuItem(itemId: Int, isEnabled: Boolean) {
        val item = bottom_nav.menu.findItem(itemId)
        if (isEnabled) {
            item.apply {
                icon.clearColorFilter()
                icon.alpha = 255
                title = item.title.toString()
            }
        } else {
            val spannable = SpannableString(item.title)
            spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.gray_light_30)), 0, spannable.length, 0)
            item.apply {
                icon.setColorFilter(resources.getColor(R.color.gray_light), PorterDuff.Mode.SRC_IN)
                icon.alpha = (0.3f * 255).toInt()
                title = spannable
            }
        }
    }


     fun setLoggedView() {
        layout_home_toolbar.setBackgroundResource(R.color.background2)
        layout_toolbar_container.removeAllViews()
        layout_toolbar_container.addView(LayoutInflater.from(this).inflate(R.layout.layout_home_logined, layout_toolbar_container, false).apply {
            btn_deposit_home.setOnClickListener {
            }
            btn_withdraw_home.setOnClickListener {
            }
        })

        /* else if (RXCache.getCurrenciesForWithdraw()?.isEmpty() == true) {
             //showWithdrawDisabled(true)
         }*/

        btn_premium.setOnClickListener{

            val notificationIntent = Intent(this, SubscriptionActivity::class.java)
            startActivity(notificationIntent)




        //    (activity as BaseActivity).openFragment(fragment)
        }
      //  shouldShowPremium()
    }


    class HomePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        var fragments = Array<HomeTabFragment?>(3) { null }

        override fun getItem(position: Int): Fragment {
            val fr = HomeTabFragment().apply {
              arguments = Bundle().apply {
                    putInt("type", position)
                }
            }
            fragments[position] = fr
            return fr
        }

        override fun getPageTitle(position: Int) = getTitle(position)

        override fun getCount() = 3

        private fun getTitle(position: Int): String {
            return when (position) {
                0 -> "FAVS"
                1 -> "Gainers"
                2 -> "Losers"
                else -> ""
            }
        }
    }
}
