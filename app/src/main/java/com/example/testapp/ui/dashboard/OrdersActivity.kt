package com.example.testapp.ui.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testapp.HomeActivity
import com.example.testapp.R
import kotlinx.android.synthetic.main.activity_markets.*

class OrdersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)
        bottom_nav.selectedItemId = R.id.reports_menu_item

        bottom_nav.setOnNavigationItemSelectedListener{item ->

            when (item.itemId) {
                R.id.home_menu_item -> {
                    val notificationIntent = Intent(this, HomeActivity::class.java)
                    startActivity(notificationIntent)
                    finish()
                }
                R.id.trades_menu_item -> {
                           val notificationIntent = Intent(this, ExchangeActivity::class.java)
                             startActivity(notificationIntent)
                             finish()
                }
                R.id.reports_menu_item -> {
               /*     val notificationIntent = Intent(this, OrdersActivity::class.java)
                    startActivity(notificationIntent)
                    finish()*/
                }
                R.id.account_menu_item -> {
                    val notificationIntent = Intent(this, AccountActivity::class.java)
                    startActivity(notificationIntent)
                    finish()
                }
                else -> {

                    val notificationIntent = Intent(this, MarketsActivity::class.java)
                    startActivity(notificationIntent)
                    finish()

                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }
}
