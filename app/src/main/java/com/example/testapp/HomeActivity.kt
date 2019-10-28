package com.example.testapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.testapp.ui.dashboard.AccountActivity
import com.example.testapp.ui.dashboard.ExchangeActivity
import com.example.testapp.ui.dashboard.MarketsActivity
import com.example.testapp.ui.dashboard.OrdersActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav)


        navView.setOnNavigationItemSelectedListener{item ->

            when (item.itemId) {
                R.id.home_menu_item -> {
                  //  FLOW_HOME
                }
                R.id.trades_menu_item -> {
                    val notificationIntent = Intent(this, ExchangeActivity::class.java)
                    startActivity(notificationIntent)
                    finish()
                }
                R.id.reports_menu_item -> {
                    val notificationIntent = Intent(this, OrdersActivity::class.java)
                    startActivity(notificationIntent)
                    finish()
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

    private fun openActivity( cls:Class<Unit>){
    }
}
