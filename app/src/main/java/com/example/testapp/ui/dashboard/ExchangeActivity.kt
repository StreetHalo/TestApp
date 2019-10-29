package com.example.testapp.ui.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.testapp.home.HomeActivity
import com.example.testapp.R
import kotlinx.android.synthetic.main.activity_markets.*
import kotlinx.android.synthetic.main.fragment_exchange.*
import kotlinx.android.synthetic.main.item_transaction_history.*
import trade.paper.app.fragments.exchange.ExchangeTimeAndSales.ExchangeTimeAndSalesFragment
import trade.paper.app.fragments.exchange.exchangeMain.ExchangeMainFragment
import trade.paper.app.utils.extensions.hideKeyboard

class ExchangeActivity : AppCompatActivity() {


    override fun onResume() {
        bottom_nav.selectedItemId = R.id.trades_menu_item
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exchange)

        bottom_nav.setOnNavigationItemSelectedListener{item ->

            when (item.itemId) {
                R.id.home_menu_item -> {
                    val notificationIntent = Intent(this, HomeActivity::class.java)
                    startActivity(notificationIntent)
                  //  finish()
                }
                R.id.trades_menu_item -> {
           /*         val notificationIntent = Intent(this, ExchangeActivity::class.java)
                    startActivity(notificationIntent)
                    finish()*/
                }
                R.id.reports_menu_item -> {
                    val notificationIntent = Intent(this, OrdersActivity::class.java)
                    startActivity(notificationIntent)
                  //  finish()
                }
                R.id.account_menu_item -> {
                    val notificationIntent = Intent(this, AccountActivity::class.java)
                    startActivity(notificationIntent)
                   // finish()
                }
                else -> {

                    val notificationIntent = Intent(this, MarketsActivity::class.java)
                    startActivity(notificationIntent)
                  //  finish()

                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        initBtnFavorite()
        initViewPager()
    }
    fun openTimeAndSales() {
        vp_exchange.setCurrentItem(if (vp_exchange.currentItem == 0) 1 else 0, true)
    }
    private fun initBtnFavorite() {
        btn_favorite_exchange.setOnClickListener {
            /*if (Settings.isFavorite(currency)) {
                Settings.removeFromFavorites(currency)
                btn_favorite_exchange.setImageResource(R.drawable.ic_add_to_favs)
            } else {
                Settings.addToFavorites(currency)
                btn_favorite_exchange.setImageResource(R.drawable.ic_added_to_favs)
            }
            (activity as BaseActivity).detachMArkets()*/


        }

        /*RXCache.favorites.observe(this, Observer {
            if (Settings.isFavorite(currency)) {
                btn_favorite_exchange.setImageResource(R.drawable.ic_added_to_favs)
            } else {
                btn_favorite_exchange.setImageResource(R.drawable.ic_add_to_favs)
            }
        })*/
    }
    private fun initViewPager() {
        val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, resources.displayMetrics).toInt()
        vp_exchange.adapter = ExchangePagerAdapter(supportFragmentManager, " ", "  ",true,2)
        vp_exchange.pageMargin = -margin
        vp_exchange.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(position: Int, p1: Float, p2: Int) {}

            override fun onPageSelected(position: Int) {
                var fragment: ExchangeTimeAndSalesFragment? = null
                supportFragmentManager.fragments.forEach {
                    if (it is ExchangeTimeAndSalesFragment)
                        fragment = it
                }
                if (position == 0) {
                    fragment?.rotateArrowUp()
                } else {
                    fragment?.rotateArrowDown()
                }
            }
        })
    }

    class ExchangePagerAdapter(fm: FragmentManager, private val currency: String, private val side: String, val isStock:Boolean, val countFragments :Int) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            var fragment = when (position) {
                0 -> ExchangeMainFragment()
                else -> ExchangeTimeAndSalesFragment()
            }
            fragment.arguments = Bundle().apply {
                putString("currency", currency)
                putString("side", side)
                putBoolean("isStock",isStock)
            }
            return fragment
        }



        override fun getCount(): Int {
            return countFragments
        }
    }
}
